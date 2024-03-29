package so.wwb.gamebox.mobile.deposit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.security.CryptoTool;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.model.pay.enums.PayApiTypeConst;
import org.soul.model.pay.vo.OnlinePayVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.web.session.SessionManagerBase;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruce on 16-12-13.
 */
public class BaseOnlineDepositController extends BaseDepositController {

    private static Log LOG = LogFactory.getLog(BaseOnlineDepositController.class);

    /*站长中心-线上支付链接*/
    private static final String MCENTER_ONLINE_RECHARGE_URL = "fund/deposit/online/list.html";

    /**
     * 调用第三方接口
     */
    @RequestMapping("/pay")
    public void pay(PlayerRechargeVo playerRechargeVo, HttpServletResponse response, HttpServletRequest request) {
        LOG.info("调用第三方pay：交易号：{0}", playerRechargeVo.getSearch().getTransactionNo());
        if (StringTool.isBlank(playerRechargeVo.getSearch().getTransactionNo())) {
            return;
        }
        try {
            playerRechargeVo = ServiceSiteTool.playerRechargeService().searchPlayerRecharge(playerRechargeVo);
            PlayerRecharge playerRecharge = playerRechargeVo.getResult();
            PayAccount payAccount = getPayAccountById(playerRecharge.getPayAccountId());
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
                String url = domain + uri;
                //添加支付网址
                playerRecharge.setPayUrl(domain);
                playerRechargeVo.setProperties(PlayerRecharge.PROP_PAY_URL);
                ServiceSiteTool.playerRechargeService().updateOnly(playerRechargeVo);
                response.sendRedirect(url);
            }
        } catch (Exception e) {
            LOG.error(e, "调用第三方pay出错交易号：{0}", playerRechargeVo.getSearch().getTransactionNo());
        }
    }

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

    public String getDomain(String domain, PayAccount payAccount) {
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
     * 线上支付（含扫码支付）提交公共方法
     */
    Map<String, Object> commonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage("deposit_auto", "请检查提交的数据是否正确"));
            return getResultMsg(false, playerRechargeVo.getErrMsg(), null);
        }
        PayAccount payAccount = null;
        if (StringTool.isNotBlank(playerRechargeVo.getAccount())) {
            payAccount = getPayAccountBySearchId(playerRechargeVo.getAccount());
        }

        if (payAccount == null) {
            Map<String, Object> resultMsg = getResultMsg(false, LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST), null);
            resultMsg.put("accountNotUsing", true);
            return resultMsg;
        }

        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        if (payAccount.getRandomAmount() != null && payAccount.getRandomAmount()) {
            Double rechargeAmount = playerRecharge.getRechargeAmount();
            if (rechargeAmount.intValue() == rechargeAmount) {
                rechargeAmount += playerRechargeVo.getResult().getRandomCash() / 100;
                playerRecharge.setRechargeAmount(rechargeAmount);
            }
        }
        playerRechargeVo = saveRecharge(playerRechargeVo, payAccount, rank, RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode(),
                playerRechargeVo.getResult().getRechargeType(),request);

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
    }

    private PayAccount getPayAccountBySearchId(String searchId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.setSearchId(searchId);
        payAccountVo.getSearch().setPlayerId(SessionManager.getUserId());
        payAccountVo = ServiceSiteTool.payAccountService().queryAccountByAccountIdAndPlayerId(payAccountVo);//查询可用的账户
        PayAccount payAccount = payAccountVo.getResult();
        if (payAccount == null) {
            return null;
        }
        if (!PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            LOG.info("账号{0}已听用,故返回收款账号null", payAccount.getPayName());
            return null;
        }
       /* Bank bank = Cache.getBank().get(payAccount.getBankCode());
        if (bank == null || (bank.getIsUse() != null && !bank.getIsUse())) {
            LOG.info("{0}渠道已关闭，故返回收款账号null", payAccount.getBankCode());
            return null;
        }*/
        return payAccount;
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
        sysResourceListVo.getSearch().setUrl(MCENTER_ONLINE_RECHARGE_URL);
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }

    /**
     * 保存存款数据
     */
    private PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, PlayerRank rank,
                                          String rechargeTypeParent, String rechargeType,HttpServletRequest request) {
        //设置存款其他数据
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(SessionManagerCommon.getTerminal(request));
        playerRechargeVo.setRankId(rank.getId());
        String account = CryptoTool.aesEncrypt(String.valueOf(payAccount.getId()), "BaseVo");//calculateFeeSchemaAndRank方法中id是加密的,所以调用前加密
        if (playerRecharge.getCounterFee() == null) {
//            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
            playerRecharge.setCounterFee(calculateFeeSchemaAndRank(rank, playerRecharge.getRechargeAmount(),account));
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
     * 提交存款金额:计算手续费和优惠
     *
     * @param playerRechargeVo
     * @return
     */
    @RequestMapping("/submit")
    @Upgrade(upgrade = true)
    public String submit(PlayerRechargeVo playerRechargeVo, Model model) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        Double rechargeAmount = playerRecharge.getRechargeAmount();
        Double randomCash = playerRechargeVo.getResult().getRandomCash();
        boolean unCheckSuccess = false;
        boolean pop = true;
        if (randomCash != null && randomCash > 0 && rechargeAmount != null) {
            rechargeAmount += randomCash / 100;
        }
        PlayerRank rank = getRank();
        PayAccount payAccount = null;
        if (StringTool.isNotBlank(playerRechargeVo.getAccount())) {
            payAccount = getPayAccountBySearchId(playerRechargeVo.getAccount());
        }
        if (payAccount == null) {
            model.addAttribute("accountNotUsing", true);
            return submitReturn(model, unCheckSuccess, pop, rechargeAmount, LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST));
        }
        //统计该渠道连续存款失败次数
        PlayerRechargeVo playerRechargeVo4Count = new PlayerRechargeVo();
        playerRechargeVo4Count.getSearch().setPayAccountId(payAccount.getId());
        Integer failureCount = ServiceSiteTool.playerRechargeService().statisticalFailureCount(playerRechargeVo4Count, SessionManager.getUserId());
        model.addAttribute("failureCount", failureCount);
        Long max = payAccount.getSingleDepositMax();
        Long min = payAccount.getSingleDepositMin();
        if (min == null) {
            min = Const.MIN_MONEY.longValue();
        }
        if (max == null) {
            max = Const.MAX_MONEY.longValue();
        }
        //验证存款金额的合法性
        if (rechargeAmount == null || rechargeAmount <= 0) {
            return submitReturn(model, unCheckSuccess, pop, rechargeAmount, LocaleTool.tranMessage(Module.FUND, MessageI18nConst.RECHARGE_AMOUNT_OVER, min, max));
        }
        if (max < rechargeAmount || min > rechargeAmount) {
            return submitReturn(model, unCheckSuccess, pop, rechargeAmount, LocaleTool.tranMessage(Module.FUND, MessageI18nConst.RECHARGE_AMOUNT_OVER, min, max));
        }
//        Double fee = calculateFee(rank, rechargeAmount);
        Double fee = calculateFeeSchemaAndRank(rank, rechargeAmount,playerRechargeVo.getAccount());
        fee = fee == null ? 0 : fee;
        if (rechargeAmount + fee <= 0) {
            return submitReturn(model, unCheckSuccess, pop, rechargeAmount, LocaleTool.tranMessage(Module.FUND, MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
        }
        unCheckSuccess = true;
        //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示提交弹窗
        //手续费标志
        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
        boolean isOpenActivityHall = ParamTool.isOpenActivityHall();
        model.addAttribute("isOpenActivityHall", isOpenActivityHall);
        if(isFee || isReturnFee) {
            String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
            model.addAttribute("counterFee", counterFee);
            model.addAttribute("fee", fee);
            String msg = "";
            if (fee > 0) {
                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.returnFee", counterFee);
            } else if (fee < 0) {
                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.needFee", counterFee);
            } else if (fee == 0) {
                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.freeFee", counterFee);
            }
            model.addAttribute("msg", msg);
            model.addAttribute("depositChannel", playerRechargeVo.getDepositChannel());
        }
        if(!isOpenActivityHall) {
            List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, playerRecharge.getRechargeType());
            model.addAttribute("sales", activityMessages);
        }
        return submitReturn(model, unCheckSuccess, pop, rechargeAmount, "");
    }

    private String submitReturn(Model model, boolean unCheckSuccess, boolean pop, Double rechargeAmount, String tips) {
        model.addAttribute("unCheckSuccess", unCheckSuccess);
        model.addAttribute("pop", pop);
        model.addAttribute("rechargeAmount", rechargeAmount);
        model.addAttribute("tips", tips);
        return "/deposit/Sale2";
    }

    /**
     * 查询符合玩家条件收款账号
     */
    List<PayAccount> searchPayAccount(String type, String accountType) {
        Map<String, Object> map = new HashMap<>(4, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", type);
        map.put("accountType", accountType);
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        map.put("terminal", TerminalEnum.MOBILE.getCode());
        PayAccountListVo listVo = new PayAccountListVo();
        listVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
    }

    PayAccount getScanPay(PlayerRank rank, String accountType, String rechargeType) {
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setRechargeType(rechargeType);
        return ServiceSiteTool.payAccountService().getOnlineScanAccount(payAccountListVo);
    }


}
