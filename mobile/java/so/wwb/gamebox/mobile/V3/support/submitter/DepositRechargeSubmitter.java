package so.wwb.gamebox.mobile.V3.support.submitter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.commons.support._Module;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.model.pay.enums.PayApiTypeConst;
import org.soul.model.pay.vo.OnlinePayVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DepositRechargeSubmitter {
    private static Log LOG = LogFactory.getLog(DepositRechargeSubmitter.class);

    public static DepositRechargeSubmitter getInstance() {
        return SpringTool.getBean(DepositRechargeSubmitter.class);
    }

    public Map<String, Object> submit(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(3, 1f);
        if (result.hasErrors()) {
            playerRechargeVo.setSuccess(false);
            return getVoMessage(map, playerRechargeVo);
        }
        PayAccount payAccount = getPayAccountById(playerRechargeVo.getResult().getPayAccountId());
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST));
            map.put("accountNotUsing", true);
            return getVoMessage(map, playerRechargeVo);
        }


        boolean isOnline = true;
        if (isOnline) {
            PlayerRecharge playerRecharge = playerRechargeVo.getResult();
            if (payAccount.getRandomAmount() != null && payAccount.getRandomAmount()) {
                Double rechargeAmount = playerRecharge.getRechargeAmount();
                if (rechargeAmount.intValue() == rechargeAmount) {
                    rechargeAmount += playerRechargeVo.getResult().getRandomCash() / 100;
                    playerRecharge.setRechargeAmount(rechargeAmount);
                }
            }
            PlayerRank rank = DepositTool.searchRank();
            playerRechargeVo = saveRecharge(playerRechargeVo, payAccount, rank, RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode(), playerRechargeVo.getResult().getRechargeType());
            if (playerRechargeVo.isSuccess()) {
                //声音提醒站长中心
                onlineToneWarn();
                //设置session相关存款数据
                //setRechargeCount();
                Map<String, Object> resultMap = getResultMsg(true, null, playerRechargeVo.getResult().getTransactionNo());
                resultMap.put("payUrl", getOnlinePayUrl(payAccount, playerRechargeVo.getResult(), request));
                return resultMap;
            } else {
                return getResultMsg(false, playerRechargeVo.getErrMsg(), null);
            }
        } else {
            return null;
        }
    }

    /**
     * 在线支付提醒站长后台
     */
    private void onlineToneWarn() {
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_ONLINE_RECHARGE_REMINDER.getCode());
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());
        message.setMsgBody(SiteParamEnum.WARMING_TONE_ONLINEPAY.getType());
        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl("/fund/deposit/online/list.html");
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }

    /**
     * 返回信息
     *
     * @param map
     * @param playerRechargeVo
     * @return
     */
    private Map<String, Object> getVoMessage(Map<String, Object> map, PlayerRechargeVo playerRechargeVo) {
        if (!playerRechargeVo.isSuccess()) {
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        } else {
            map.put("orderNo", playerRechargeVo.getResult().getTransactionNo());
        }
        if (playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getOkMsg())) {
            playerRechargeVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));

        } else if (!playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getErrMsg())) {
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }
        map.put("state", playerRechargeVo.isSuccess());
        map.put("msg", StringTool.isNotBlank(playerRechargeVo.getOkMsg()) ? playerRechargeVo.getOkMsg() : playerRechargeVo.getErrMsg());
        return map;
    }

    private Map<String, Object> getResultMsg(boolean isSuccess, String msg, String transactionNo) {
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("state", isSuccess);
        if (isSuccess) {
            map.put("orderNo", transactionNo);
        } else {
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        }
        if (StringTool.isNotBlank(msg)) {
            map.put("msg", msg);
        }
        return map;
    }

    /**
     * 获取在线支付url
     *
     * @param payAccount
     * @param playerRecharge
     * @param request
     * @return
     */
    private String getOnlinePayUrl(PayAccount payAccount, PlayerRecharge playerRecharge, HttpServletRequest request) {
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
            LOG.error(e);
        }
        return url;
    }

    /**
     * 获取在线支付域名
     *
     * @param domain
     * @param payAccount
     * @return
     */
    private String getDomain(String domain, PayAccount payAccount) {
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

    /**
     * 保存存款数据
     */
    private PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, PlayerRank rank,
                                          String rechargeTypeParent, String rechargeType) {
        //设置存款其他数据
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setRankId(rank.getId());
        if (playerRecharge.getCounterFee() == null) {
            playerRecharge.setCounterFee(DepositTool.calculateFee(rank, playerRecharge.getRechargeAmount()));
        }
        //存款总额（存款金额+手续费）>0才能继续执行
        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }
        playerRecharge.setRechargeTypeParent(rechargeTypeParent);
        playerRecharge.setRechargeType(rechargeType);
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());

        //保存订单
        return ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
    }

    /**
     * 根据id获取收款账号
     *
     * @param payAccountId
     * @return
     */
    private PayAccount getPayAccountById(Integer payAccountId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.getSearch().setId(payAccountId);
        payAccountVo = ServiceSiteTool.payAccountService().get(payAccountVo);
        return payAccountVo.getResult();
    }
}
