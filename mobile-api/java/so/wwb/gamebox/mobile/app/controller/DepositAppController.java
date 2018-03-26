package so.wwb.gamebox.mobile.app.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.net.ServletTool;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppDepositPayEnum;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.AppPayAccount;
import so.wwb.gamebox.mobile.app.model.DepositPayApp;
import so.wwb.gamebox.mobile.app.validateForm.*;
import so.wwb.gamebox.mobile.controller.BaseDepositController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.locale.LocaleTool;
import so.wwb.gamebox.mobile.app.model.AppSale;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

@Controller
@RequestMapping("/depositOrigin")
public class DepositAppController extends BaseDepositController {

    /**
     * 存款首页
     */
    @RequestMapping("/index")
    @ResponseBody
    public String recharge() {
        PayAccountListVo listVo = new PayAccountListVo();
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("currency", SessionManager.getUser().getDefaultCurrency()); //默认货币代码
        map.put("terminal", TerminalEnum.MOBILE.getCode()); //手机端
        listVo.setConditions(map);
        List<PayAccount> payAccounts = ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
        Map<String, Object> payAccountMap = arrangePayAccounts(payAccounts);
        if (payAccountMap.isEmpty()) {
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.NO_AVAILABLE_CHANNELS.getMsg(),
                    null, APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                payAccountMap, APP_VERSION);
    }

    /**
     * 存款渠道分类
     */
    private Map arrangePayAccounts(List<PayAccount> payAccounts) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> payData = new HashMap<>();
        if (CollectionTool.isEmpty(payAccounts) && payAccounts.size() <= 0) {
            return map;
        }
        //线上支付
        List<AppPayAccount> online = new ArrayList<>(0);
        //网银
        List<AppPayAccount> company = new ArrayList<>(0);
        //微信
        List<AppPayAccount> wechat = new ArrayList<>(0);
        //支付宝
        List<AppPayAccount> alipay = new ArrayList<>(0);
        //ＱＱ
        List<AppPayAccount> qqWallet = new ArrayList<>(0);
        //
        List<AppPayAccount> jdPay = new ArrayList<>(0);
        //
        List<AppPayAccount> baifuPay = new ArrayList<>(0);
        //
        List<AppPayAccount> unionPay = new ArrayList<>(0);
        //
        List<AppPayAccount> easyPay = new ArrayList<>(0);
        //
        List<AppPayAccount> counter = new ArrayList<>(0);
        //
        List<AppPayAccount> bitcoin = new ArrayList<>(0);
        //
        List<AppPayAccount> other = new ArrayList<>(0);
        //
        List<AppPayAccount> oneCodePay = new ArrayList<>(0);

        for (PayAccount payAccount : payAccounts) {
            if (PayAccountType.COMPANY_ACCOUNT.getCode().equals(payAccount.getType())) {
                //公司入款
                if (AppConstant.WECHAT_PAY.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.WECHATPAY_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    wechat.add(createAppPayAccount(payAccount));

                } else if (AppConstant.ALI_PAY.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.ALIPAY_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    alipay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.QQ_WALLET.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.QQWALLET_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    qqWallet.add(createAppPayAccount(payAccount));

                } else if (AppConstant.JD_WALLET.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.JDWALLET_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    jdPay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.BD_WALLET.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.BDWALLET_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    baifuPay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.ONE_CODE_PAY.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.ONECODEPAY_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    oneCodePay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType()) && AppConstant.BITCOIN.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.BITCOIN_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.BITCOIN_PAY.getCode());
                    bitcoin.add(createAppPayAccount(payAccount));

                } else if (AppConstant.OTHER.equals(payAccount.getBankCode())) {
                    payAccount.setDepositWay(DepositWayEnum.OTHER_FAST.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ELECTRONIC_PAY.getCode());
                    other.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.BANKACCOUNT.getCode().equals(payAccount.getAccountType())) {
                    //公司入款　银行账户
                    payAccount.setDepositWay(DepositWayEnum.COMPANY_DEPOSIT.getCode());
                    payAccount.setPayType(AppDepositPayEnum.COMPANY_PAY.getCode());
                    company.add(createAppPayAccount(payAccount));
                    if (payAccount.getSupportAtmCounter() == null || payAccount.getSupportAtmCounter()) {
                        //公司入款　银行账户　柜台
                        counter.add(createAppPayAccount(payAccount));
                    }
                }
            } else if (PayAccountType.ONLINE_ACCOUNT.getCode().equals(payAccount.getType())) {
                //线上支付
                if (PayAccountAccountType.WECHAT.getCode().equals(payAccount.getAccountType()) || PayAccountAccountType.WECHAT_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    //WECHAT("3", "微信支付"),WECHAT_MICROPAY("10","微信反扫"),
                    payAccount.setDepositWay(DepositWayEnum.WECHATPAY_SCAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    wechat.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.ALIPAY.getCode().equals(payAccount.getAccountType()) || PayAccountAccountType.ALIPAY_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    //ALIPAY("4", "支付宝"),ALIPAY_MICROPAY("11","支付宝反扫"),
                    payAccount.setDepositWay(DepositWayEnum.ALIPAY_SCAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    alipay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.QQWALLET.getCode().equals(payAccount.getAccountType()) || PayAccountAccountType.QQ_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    //QQWALLET("5","QQ钱包"),QQ_MICROPAY("12","QQ反扫"),
                    payAccount.setDepositWay(DepositWayEnum.QQWALLET_SCAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    qqWallet.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.JD_PAY.getCode().equals(payAccount.getAccountType())) {
                    //JD_PAY("7","京东钱包"),
                    payAccount.setDepositWay(DepositWayEnum.JDPAY_SCAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    jdPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.BAIFU_PAY.getCode().equals(payAccount.getAccountType())) {
                    //BAIFU_PAY("8","百度钱包"),
                    payAccount.setDepositWay(DepositWayEnum.BDWALLET_SAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    baifuPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.UNION_PAY.getCode().equals(payAccount.getAccountType())) {
                    //UNION_PAY("9","银联扫码"),
                    payAccount.setDepositWay(DepositWayEnum.UNION_PAY_SCAN.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    unionPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.EASY_PAY.getCode().equals(payAccount.getAccountType())) {
                    //EASY_PAY("13", "易收付"),
                    payAccount.setDepositWay(DepositWayEnum.EASY_PAY.getCode());
                    payAccount.setPayType(AppDepositPayEnum.SCAN_PAY.getCode());
                    easyPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType())) {
                    //线上支付
                    payAccount.setDepositWay(DepositWayEnum.ONLINE_DEPOSIT.getCode());
                    payAccount.setPayType(AppDepositPayEnum.ONLINE_PAY.getCode());
                    online.add(createAppPayAccount(payAccount));
                }
            }
        }
        //公司入款　快充中心
        fastRecharge(payData);
        //公司入款　快选金额
        quickSelection(payData);
        //是否展示多个账号
        boolean isMultipleAccount = isMultipleAccount();
        payData.put("isMultipleAccount", isMultipleAccount);
        //是否为纯彩票站
        boolean lotterySite = isLotterySite();
        payData.put("lotterySite", lotterySite);
        //　是否影藏收款账号
//        payData.put("isHideAccount", isHideAccount());

        deleteMaintainChannel(online);

        List<DepositPayApp> pays = new ArrayList<>();
        if (!CollectionTool.isEmpty(online)) {
            pays.add(getDepositApp("online", online));
        }
        if (!CollectionTool.isEmpty(company)) {
            pays.add(getDepositApp("company", company));
        }
        if (!CollectionTool.isEmpty(wechat)) {
            pays.add(getDepositApp("wechat", wechat));
        }
        if (!CollectionTool.isEmpty(alipay)) {
            pays.add(getDepositApp("alipay", alipay));
        }
        if (!CollectionTool.isEmpty(qqWallet)) {
            pays.add(getDepositApp("qqWallet", qqWallet));
        }
        if (!CollectionTool.isEmpty(jdPay)) {
            pays.add(getDepositApp("jdPay", jdPay));
        }
        if (!CollectionTool.isEmpty(baifuPay)) {
            pays.add(getDepositApp("baifuPay", baifuPay));
        }
        if (!CollectionTool.isEmpty(bitcoin)) {
            pays.add(getDepositApp("bitcoin", bitcoin));
        }
        if (!CollectionTool.isEmpty(oneCodePay)) {
            pays.add(getDepositApp("oneCodePay", oneCodePay));
        }
        if (!CollectionTool.isEmpty(unionPay)) {
            pays.add(getDepositApp("unionPay", unionPay));
        }
        if (!CollectionTool.isEmpty(counter)) {
            pays.add(getDepositApp("counter", counter));
        }
        if (!CollectionTool.isEmpty(easyPay)) {
            pays.add(getDepositApp("easyPay", easyPay));
        }
        if (!CollectionTool.isEmpty(other)) {
            pays.add(getDepositApp("other", other));
        }
        map.put("pay", pays);
        map.put("payData", payData);
        return map;
    }

    private DepositPayApp getDepositApp(String code, List<AppPayAccount> payAccounts) {
        DepositPayApp depositPay = new DepositPayApp();
        if (CollectionTool.isEmpty(payAccounts)) {
            return depositPay;
        }

        depositPay.setCode(code);
        for (AppDepositPayEnum payEnum : AppDepositPayEnum.values()) {
            if (payEnum.getCode().equals(code)) {
                depositPay.setName(payEnum.getTrans());
            }
        }
        depositPay.setPayAccounts(payAccounts);
        return depositPay;
    }

    /**
     * 存款　优惠查询
     */
    @RequestMapping("/seachSale")
    @ResponseBody
    public String seachSale(PlayerRechargeVo playerRechargeVo) {
        PayAccount resultPayAccount = playerRechargeVo.getPayAccount();
        PayAccount payAccount = null ;
        if (resultPayAccount != null) {
            if (resultPayAccount.getId() != null) {
                payAccount = getPayAccountById(resultPayAccount.getId());
            }
            if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                        AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                        null, APP_VERSION);
            }
            payAccount.setDepositWay(resultPayAccount.getDepositWay());
            Map<String, Object> map = new HashMap<String, Object>();
            //统计该渠道连续存款失败次数
            Double rechargeAmount = null ;
            if(playerRechargeVo.getResult() != null){
                rechargeAmount = playerRechargeVo.getResult().getRechargeAmount();
            }
            if (rechargeAmount == null || rechargeAmount <= 0) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                        AppErrorCodeEnum.MONEY_ERROR.getMsg(),
                        null, APP_VERSION);
            }
            Integer min = Const.MIN_MONEY;
            Integer max = Const.MAX_MONEY;
            if (PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType()) && AppConstant.BITCOIN.equals(payAccount.getBankCode())) {
                return bitcoinSeachDiscount(playerRechargeVo);
            } else if (PayAccountType.COMPANY_ACCOUNT.getCode().equals(payAccount.getType())) {
                PlayerRank rank = getRank();
                max = rank.getOnlinePayMax();
                min = rank.getOnlinePayMin();
            } else if (PayAccountType.ONLINE_ACCOUNT.getCode().equals(payAccount.getType())) {
                max = payAccount.getSingleDepositMax();
                min = payAccount.getSingleDepositMin();
                if (min == null) {
                    min = Const.MIN_MONEY;
                }
                if (max == null) {
                    max = Const.MAX_MONEY;
                }
                /*if (payAccount.getRandomAmount() && appPlayerRechange.getRandomCash() != null) {
                    rechargeAmount = rechargeAmount + appPlayerRechange.getRandomCash();
                }*/
                PlayerRechargeVo playerRechargeVo4Count = new PlayerRechargeVo();
                playerRechargeVo4Count.getSearch().setPayAccountId(payAccount.getId());
                Integer failureCount = ServiceSiteTool.playerRechargeService().statisticalFailureCount(playerRechargeVo4Count, SessionManager.getUserId());
                map.put("failureCount", failureCount);
            }

            //验证存款金额的合法性
            if (max < rechargeAmount || min > rechargeAmount) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                        LocaleTool.tranMessage(Module.FUND, MessageI18nConst.RECHARGE_AMOUNT_OVER, min, max),
                        null, APP_VERSION);
            }
            PlayerRank rank = getRank();
            Double fee = calculateFee(rank, rechargeAmount);
            fee = fee == null ? 0 : fee;
            if (rechargeAmount + fee <= 0) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                        LocaleTool.tranMessage(Module.FUND, MessageI18nConst.RECHARGE_AMOUNT_LT_FEE),
                        null, APP_VERSION);
            }
            //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示优惠弹窗
            //手续费标志
            boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
            //返手续费标志
            boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
            if (!StringTool.isNotEmpty(payAccount.getDepositWay())) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.DEPOSIT_TYPE_ERROR.getCode(),
                        AppErrorCodeEnum.DEPOSIT_TYPE_ERROR.getMsg(),
                        null, APP_VERSION);
            }
            List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, payAccount.getDepositWay());

            if (!isFee && !isReturnFee && CollectionTool.isEmpty(activityMessages)) {
                return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.NOT_SALE.getCode(),
                        AppErrorCodeEnum.NOT_SALE.getMsg(),
                        null, APP_VERSION);
            } else {
                List<AppSale> saleList = new ArrayList<>();
                for (VActivityMessage vActivityMessage : activityMessages) {
                    if (vActivityMessage.isPreferential()) {
                        AppSale appSale = new AppSale();
                        appSale.setId(vActivityMessage.getId());
                        appSale.setActivityName(vActivityMessage.getActivityName());
                        saleList.add(appSale);
                    }
                }
                String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
                map.put("counterFee", counterFee);
                map.put("fee", fee);
                map.put("sales", saleList);
                String msg = "";
                if (fee > 0) {
                    msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.appReturnFee", counterFee);
                } else if (fee < 0) {
                    msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.appNeedFee", counterFee);
                } else if (fee == 0) {
                    msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.freeFee", counterFee);
                }
                map.put("msg", msg);
            }
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    map, APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.SUBMIT_DATA_ERROR.getCode(),
                AppErrorCodeEnum.SUBMIT_DATA_ERROR.getMsg(), null, APP_VERSION);

    }

    /**
     * 比特币　查询优惠
     */
    public String bitcoinSeachDiscount(PlayerRechargeVo playerRechargeVo) {
        VActivityMessageListVo listVo = new VActivityMessageListVo();
        listVo.getSearch().setDepositWay(DepositWayEnum.BITCOIN_FAST.getCode());
        listVo = ServiceSiteTool.playerRechargeService().searchSale(listVo, SessionManager.getUserId());
        List<AppSale> saleList = new ArrayList<>();
        if (CollectionTool.isEmpty(listVo.getResult())) {
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.NOT_SALE.getCode(),
                    AppErrorCodeEnum.NOT_SALE.getMsg(),
                    null, APP_VERSION);
        } else {
            for (VActivityMessage vActivityMessage : listVo.getResult()) {
                if (vActivityMessage.isPreferential()) {
                    AppSale appSale = new AppSale();
                    appSale.setId(vActivityMessage.getId());
                    appSale.setActivityName(vActivityMessage.getActivityName());
                    saleList.add(appSale);
                }
            }
        }
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                saleList, APP_VERSION);
    }

    /**
     * 线上支付　存款
     */
    @RequestMapping("/onlinePay")
    @ResponseBody
    public String onlinePay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid DepositForm form, BindingResult result,HttpServletRequest request) {
        PayAccount payAccount = playerRechargeVo.getPayAccount();
        if (payAccount != null && payAccount.getId() != null) {
            payAccount = getPayAccountById(payAccount.getId());
        }
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
//        String url = AppConstant.ONLINE_PAY_URL;
        return onlineCommonDeposit(playerRechargeVo, payAccount, result, request);
    }

    //扫码支付　存款
    @RequestMapping("/scanPay")
    @ResponseBody
    public String scanPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result,HttpServletRequest request) {
        PayAccount payAccount = playerRechargeVo.getPayAccount();
        if (payAccount != null && payAccount.getId() != null) {
            payAccount = getPayAccountById(payAccount.getId());
        }
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
//        String url = AppConstant.SCAN_PAY_URL;
        return onlineCommonDeposit(playerRechargeVo, payAccount, result, request);
    }

    //网银支付　存款
    @RequestMapping("/companyPay")
    @ResponseBody
    public String companyPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyBankDepositForm form, BindingResult result) {
        PayAccount payAccount = playerRechargeVo.getPayAccount();
        if (payAccount != null && payAccount.getId() != null) {
            payAccount = getPayAccountById(payAccount.getId());
        }
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
        String type = AppDepositPayEnum.COMPANY_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, payAccount, result, type);
    }

    //电子支付 存款
    @RequestMapping("/electronicPay")
    @ResponseBody
    public String electronicPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyElectronicDepositForm form, BindingResult result) {
        PayAccount payAccount = playerRechargeVo.getPayAccount();
        if (payAccount != null && payAccount.getId() != null) {
            payAccount = getPayAccountById(payAccount.getId());
        }
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
        String type = AppDepositPayEnum.ELECTRONIC_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, payAccount, result, type);
    }

    //比特币支付　存款
    @RequestMapping("/bitcoinPay")
    @ResponseBody
    public String bitcoinPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid BitcoinDepositForm form, BindingResult result) {
        PayAccount payAccount = playerRechargeVo.getPayAccount();
        if (payAccount != null && payAccount.getId() != null) {
            payAccount = getPayAccountById(payAccount.getId());
        }
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
        String type = AppDepositPayEnum.BITCOIN_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, payAccount, result, type);
    }
}
