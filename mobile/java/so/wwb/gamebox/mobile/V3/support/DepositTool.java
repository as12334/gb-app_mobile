package so.wwb.gamebox.mobile.V3.support;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.model.pay.enums.PayApiTypeConst;
import org.soul.model.pay.vo.OnlinePayVo;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.RankFeeType;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeListVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DepositTool {
    public static final Log LOG = LogFactory.getLog(DepositTool.class);

    /**
     * 获取快充地址
     *
     * @return
     */
    public static String getFastRechargeUrl() {
        SysParam rechargeUrlParam = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL);
        if (rechargeUrlParam == null || StringTool.isBlank(rechargeUrlParam.getParamValue())) {
            return "";
        }
        //是否包含全部层级
        SysParam allRank = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL_ALL_RANK);
        if (allRank != null && "true".equals(allRank.getParamValue())) {
            return rechargeUrlParam.getParamValue();
        }
        SysParam ranksParam = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL_RANKS);
        boolean isFastRecharge = false;
        if (ranksParam != null && StringTool.isNotBlank(ranksParam.getParamValue())) {
            PlayerRank rank = searchRank();
            String[] ranks = ranksParam.getParamValue().split(",");
            for (String rankId : ranks) {
                if (String.valueOf(rank.getId()).equals(rankId)) {
                    isFastRecharge = true;
                    break;
                }
            }
        }
        if (isFastRecharge) {
            String url = rechargeUrlParam.getParamValue();
            if (StringTool.isNotBlank(url)) {
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }
                return url;
            }
        }
        return "";
    }

    /**
     * 获取货币符号
     *
     * @return
     */
    public static String getCurrencySign() {
        String defaultCurrency = SessionManager.getUser().getDefaultCurrency();
        if (StringTool.isBlank(defaultCurrency)) {
            return "";
        }
        SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 获取用户层级
     *
     * @return
     */
    public static PlayerRank searchRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    /**
     * 计算手续费
     *
     * @param rechargeAmount 存款金额
     * @return
     */
    public static double calculateFee(PlayerRank rank, double rechargeAmount) {
        if (rank == null) {
            return 0d;
        }
        //手续费标志
        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
        if (!isFee && !isReturnFee) {
            return 0d;
        }
        if (isReturnFee && rechargeAmount < rank.getReachMoney()) {
            return 0d;
        }
        // 规定时间内存款次数
        long count = getDepositCountInTime(rank, isFee, isReturnFee);
        if (isFee && rank.getFreeCount() != null && count < rank.getFreeCount()) {
            return 0d;
        }
        if (isReturnFee && rank.getReturnFeeCount() != null && count >= rank.getReturnFeeCount()) {
            return 0d;
        }
        double fee = 0d;
        if (isFee && rank.getFeeMoney() != null) {
            fee = computeFee(rank.getFeeType(), rank.getFeeMoney(), rechargeAmount, rank.getMaxFee());
        } else if (isReturnFee && rank.getReturnMoney() != null) {
            fee = computeFee(rank.getReturnType(), rank.getReturnMoney(), rechargeAmount, rank.getMaxReturnFee());
        }
        if (isFee) {
            fee = -Math.abs(fee);
        } else {
            fee = Math.abs(fee);
        }
        return fee;
    }

    /**
     * 计算在层级设置的符合免手续费的存款次数
     *
     * @param rank        玩家层级
     * @param isFee       手续费标志
     * @param isReturnFee 返手续费标志
     * @return
     */
    private static long getDepositCountInTime(PlayerRank rank, boolean isFee, boolean isReturnFee) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        Date now = SessionManager.getDate().getNow();
        listVo.getSearch().setEndTime(now);
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        if (isFee && rank.getFreeCount() != null && rank.getFreeCount() > 0 && rank.getFeeTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getFeeTime()));
        } else if (isReturnFee && rank.getReturnFeeCount() != null && rank.getReturnFeeCount() > 0 && rank.getReturnTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getReturnTime()));
        }
        listVo.setRank(rank);
        return ServiceSiteTool.playerRechargeService().searchPlayerRechargeCount(listVo);
    }

    /**
     * 按计算手续费方式计算手续费
     *
     * @param feeType        计算手续费方式：按比例、固定
     * @param feeMoney       收取手续费固定值
     * @param rechargeAmount 存款金额
     * @param maxFee         手续费最大值
     * @return
     */
    private static double computeFee(String feeType, Double feeMoney, double rechargeAmount, Double maxFee) {
        double fee = 0d;
        if (RankFeeType.PROPORTION.getCode().equals(feeType)) {
            fee = rechargeAmount * feeMoney / 100.0;
        } else if (RankFeeType.FIXED.getCode().equals(feeType)) {
            fee = feeMoney;
        }
        if (maxFee != null && fee > maxFee) {
            fee = maxFee;
        }
        return fee;
    }


    public static String getOnlinePayUrl(PayAccount payAccount, PlayerRecharge playerRecharge, HttpServletRequest request) {
        String url = "";
        try {
            List<Map<String, String>> accountJson = JsonTool.fromJson(payAccount.getChannelJson(), new TypeReference<ArrayList<Map<String, String>>>() {
            });
            String domain = ServletTool.getDomainPath(request);
            for (Map<String, String> map : accountJson) {
                if (map.get("column").equals(CommonFieldsConst.PAYDOMAIN)) {
                    domain = map.get("value");
                    break;
                }
            }
            if (domain != null && (RechargeStatusEnum.PENDING_PAY.getCode().equals(playerRecharge.getRechargeStatus())
                    || RechargeStatusEnum.OVER_TIME.getCode().equals(playerRecharge.getRechargeStatus()))) {
                String uri = "/onlinePay/abcefg.html?search.transactionNo=" + playerRecharge.getTransactionNo() + "&origin=" + TerminalEnum.MOBILE.getCode();
                domain = getDomain(domain, payAccount);
                url = domain + uri;
                //保存订单支付网址
                playerRecharge.setPayUrl(domain);
                PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
                playerRechargeVo.setResult(playerRecharge);
                playerRechargeVo.setProperties(PlayerRecharge.PROP_PAY_URL);
                ServiceSiteTool.playerRechargeService().updateOnly(playerRechargeVo);
            }
        } catch (Exception e) {
            LOG.error("获取第三方支付url出错.", e);
        }
        return url;
    }

    private static String getDomain(String domain, PayAccount payAccount) {
        domain = domain.replace("http://", "");
        VSysSiteDomain siteDomain = Cache.getSiteDomain(domain);
        Boolean sslEnabled = false;
        if (siteDomain != null && siteDomain.getSslEnabled() != null && siteDomain.getSslEnabled()) {
            sslEnabled = true;
        }
        String sslDomain = "https://" + domain;
        String notSslDomain = "http://" + domain;
        ;
        if (!sslEnabled) {
            return notSslDomain;
        }
        try {
            OnlinePayVo onlinePayVo = new OnlinePayVo();
            onlinePayVo.setChannelCode(payAccount.getBankCode());
            onlinePayVo.setApiType(PayApiTypeConst.PAY_SSL_ENABLE);
            sslEnabled = ServiceTool.onlinePayService().getSslEnabled(onlinePayVo);
        } catch (Exception e) {
            LOG.error(e);
        }
        if (sslEnabled) {
            return sslDomain;
        }
        return notSslDomain;
    }

}
