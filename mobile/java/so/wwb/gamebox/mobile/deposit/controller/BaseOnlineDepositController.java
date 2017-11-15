package so.wwb.gamebox.mobile.deposit.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.RandomStringTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.model.pay.enums.PayApiTypeConst;
import org.soul.model.pay.vo.OnlinePayVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.web.session.SessionManagerBase;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by bruce on 16-12-13.
 */
public class BaseOnlineDepositController extends BaseDepositController {

    private static Log LOG = LogFactory.getLog(BaseOnlineDepositController.class);

    /*站长中心-线上支付链接*/
    private static final String MCENTER_ONLINE_RECHARGE_URL = "/fund/deposit/online/list.html";

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
            playerRechargeVo = ServiceTool.playerRechargeService().searchPlayerRecharge(playerRechargeVo);
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
                ServiceTool.playerRechargeService().updateOnly(playerRechargeVo);
                response.sendRedirect(url);
            }
        } catch (Exception e) {
            LOG.error(e, "调用第三方pay出错交易号：{0}", playerRechargeVo.getSearch().getTransactionNo());
        }
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
    Map<String, Object> commonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> messageMap = new HashMap<>(2, 1f);
            messageMap.put("state", false);
            messageMap.put("msg", LocaleTool.tranMessage("deposit_auto", "请检查提交的数据是否正确"));
            return messageMap;
        }

        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        PayAccount payAccount = null;
        if (RechargeTypeEnum.ONLINE_DEPOSIT.getCode().equals(playerRecharge.getRechargeType())) {//线上支付
            payAccount = getOnlinePayAccount(rank, playerRecharge.getPayerBank());
        } else {//扫码支付
            payAccount = getScanCodePayAccount(rank, playerRecharge.getRechargeType());
        }

        if (payAccount == null) {
            return getResultMsg(false, LocaleTool.tranMessage(Module.FUND.getCode(),
                    MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST), null);
        }
        if (payAccount.getRandomAmount()!=null) {
            boolean randomAmount = payAccount.getRandomAmount();
            if (randomAmount) {
                Double rechargeAmount = playerRecharge.getRechargeAmount();
                if (rechargeAmount.intValue() == rechargeAmount) {
                    double random = Double.parseDouble(RandomStringTool.random(2, 11, 99, false, true)) * 0.01;
                    if (random<0.11){
                        random+=0.11;
                    }
                    rechargeAmount += random;
                    playerRecharge.setRechargeAmount(rechargeAmount);
                }
            }
        }
        playerRechargeVo = saveRecharge(playerRechargeVo, payAccount, rank, RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode(),
                playerRechargeVo.getResult().getRechargeType());
        if (playerRechargeVo.isSuccess()) {
            //声音提醒站长中心
            onlineToneWarn();
            //设置session相关存款数据
            //setRechargeCount();
            return getResultMsg(true, null, playerRechargeVo.getResult().getTransactionNo());
        } else {
            return getResultMsg(false, playerRechargeVo.getErrMsg(), null);
        }
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
        List<Integer> userIdByUrl = ServiceTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
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
            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
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
        return ServiceTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
    }

    /**
     * 提交存款金额:计算手续费和优惠
     *
     * @param playerRechargeVo
     * @return
     */
    @RequestMapping("/submit")
    public String submit(PlayerRechargeVo playerRechargeVo, Model model) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        double rechargeAmount = playerRecharge.getRechargeAmount();

        boolean unCheckSuccess = false;
        boolean pop = true;

        //验证存款金额的合法性
        String tips = "";
        if (rechargeAmount <= 0) {
            tips = LocaleTool.tranMessage(Module.FUND, "rechargeForm.rechargeAmountCorrect");
            model.addAttribute("tips", tips);
        } else {
            PlayerRank rank = getRank();
            PayAccount payAccount = null;
            if (RechargeTypeEnum.ONLINE_DEPOSIT.getCode().equals(playerRecharge.getRechargeType())) {//线上支付
                payAccount = getOnlinePayAccount(rank, playerRecharge.getPayerBank());
            } else {//扫码支付
                payAccount = getScanCodePayAccount(rank, playerRecharge.getRechargeType());
            }
            if (payAccount != null) {
                Integer max = payAccount.getSingleDepositMax();
                if (max == null) {
//                    max = rank.getOnlinePayMax();
                    max = 99999999;//修改金额限制为空时,使用默认值
                }
                Integer min = payAccount.getSingleDepositMin();
                if (min == null) {
//                    min = rank.getOnlinePayMin();
                    min = 0;
                }
                if ((max != null && max < rechargeAmount) || (min != null && min > rechargeAmount)) {
                    tips = LocaleTool.tranMessage(Module.FUND, "rechargeForm.rechargeAmountOver", min, max);
                    model.addAttribute("tips", tips);
                } else {
                    double fee = calculateFee(rank, rechargeAmount);
                    if (rechargeAmount + fee <= 0) {
                        model.addAttribute("tips", "存款金额加手续费必须大于0");
                    } else {
                        unCheckSuccess = true;
                        //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示提交弹窗
                        //手续费标志
                        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
                        //返手续费标志
                        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
                        List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, playerRecharge.getRechargeType());
                        if (!isFee && !isReturnFee && activityMessages.size() <= 0) {
                            pop = false;
                        } else {
                            String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
                            model.addAttribute("counterFee", counterFee);
                            model.addAttribute("fee", fee);
                            model.addAttribute("sales", activityMessages);
                            String msg = "";
                            if (fee > 0) {
                                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.returnFee", counterFee);
                            } else if (fee < 0) {
                                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.needFee", counterFee);
                            } else if (fee == 0) {
                                msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.freeFee", counterFee);
                            }
                            model.addAttribute("msg", msg);
                        }
                    }
                }
            } else {
                model.addAttribute("tips", "当前没有收款账号");
            }
        }
        model.addAttribute("unCheckSuccess", unCheckSuccess);
        model.addAttribute("pop", pop);
        model.addAttribute("rechargeAmount", rechargeAmount);
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
        return ServiceTool.payAccountService().searchPayAccountByRank(listVo);
    }

    /**
     * 获取扫描支付收款账号
     */
    private PayAccount getScanCodePayAccount(PlayerRank rank, String rechargeType) {
        PayAccount payAccount = null;
        if (RechargeTypeEnum.ALIPAY_SCAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.ALIPAY.getCode(), rechargeType);
        } else if (RechargeTypeEnum.WECHATPAY_SCAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.WECHAT.getCode(), rechargeType);
        } else if (RechargeTypeEnum.QQWALLET_SCAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.QQWALLET.getCode(), rechargeType);
        } else if (RechargeTypeEnum.JDPAY_SCAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.JD_PAY.getCode(), rechargeType);
        }else if (RechargeTypeEnum.BDWALLET_SAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.BAIFU_PAY.getCode(), rechargeType);
        }else if (RechargeTypeEnum.UNION_PAY_SCAN.getCode().equals(rechargeType)) {
            payAccount = getScanPay(rank, PayAccountAccountType.UNION_PAY.getCode(), rechargeType);
        }
        return payAccount;
    }

    PayAccount getScanPay(PlayerRank rank, String accountType, String rechargeType) {
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType);
        return getRotationOnlinePayAccount(rank, payAccounts, rechargeType);
    }

    /**
     * 获取线上支付收款账号
     */
    private PayAccount getOnlinePayAccount(PlayerRank rank, String bankCode) {
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode());
        if (CollectionTool.isEmpty(payAccounts)) {
            return null;
        }

        //获取可用渠道货币
        Map<String, Set<String>> channelBankCode = ServiceTool.onlinePayService().getSupportDirectBank(new OnlinePayVo());
        if (channelBankCode == null) {
            return null;
        }

        Map<String, Set<String>> channelCurrency = ServiceTool.onlinePayService().getSupportCurrency(new OnlinePayVo());
        if (channelCurrency == null) {
            return null;
        }
        Set<String> currencyChannels = channelCurrency.get(SessionManager.getUser().getDefaultCurrency());
        if (CollectionTool.isEmpty(currencyChannels)) {
            return null;
        }

        Set<String> bankChannels = channelBankCode.get(bankCode);
        if (CollectionTool.isEmpty(bankChannels)) {
            return null;
        }
        List<String> channels = (List<String>) CollectionTool.intersection(currencyChannels, bankChannels);

        List<PayAccount> pAccounts = CollectionQueryTool.inQuery(payAccounts, PayAccount.PROP_BANK_CODE, channels);
        return getRotationOnlinePayAccount(rank, pAccounts, RechargeTypeEnum.ONLINE_DEPOSIT.getCode());
    }

    /**
     * 获取收款账号（根据轮流入款获取指定收款账号）
     * 金流顺序
     */
    PayAccount getRotationOnlinePayAccount(PlayerRank rank, List<PayAccount> payAccounts, String rechargeType) {
        if (CollectionTool.isEmpty(payAccounts)) {
            return null;
        }
        //轮流打开，根据最后一条线上支付存款记录确认收款账号
        if (rank.getIsTakeTurns() == null || rank.getIsTakeTurns()) {
            PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
            playerRechargeVo.getSearch().setRechargeType(rechargeType);
            playerRechargeVo.getSearch().setRechargeTypeParent(RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode());
            playerRechargeVo.setRankId(rank.getId());
            Integer payAccountId = ServiceTool.playerRechargeService().searchLastPayAccountId(playerRechargeVo);
            if (payAccountId == null) {
                return payAccounts.get(0);
            } else {
                int i = 0;
                for (PayAccount payAccount : payAccounts) {
                    i++;
                    if (payAccountId.intValue() == payAccount.getId().intValue()) {
                        break;
                    }
                }
                return i == payAccounts.size() ? payAccounts.get(0) : payAccounts.get(i);
            }
        } else { //轮流关闭，取第一个收款账号
            return payAccounts.get(0);
        }
    }

}
