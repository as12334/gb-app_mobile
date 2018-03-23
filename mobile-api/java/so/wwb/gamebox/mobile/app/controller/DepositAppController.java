package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppDepositPayEnum;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.AppPayAccount;
import so.wwb.gamebox.mobile.app.model.DepositPayApp;
import so.wwb.gamebox.mobile.controller.BaseDepositController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

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
                    wechat.add(createAppPayAccount(payAccount));

                } else if (AppConstant.ALI_PAY.equals(payAccount.getBankCode())) {
                    alipay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.QQ_WALLET.equals(payAccount.getBankCode())) {
                    qqWallet.add(createAppPayAccount(payAccount));

                } else if (AppConstant.JD_WALLET.equals(payAccount.getBankCode())) {
                    jdPay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.BD_WALLET.equals(payAccount.getBankCode())) {
                    baifuPay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.ONE_CODE_PAY.equals(payAccount.getBankCode())) {
                    oneCodePay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType()) && AppConstant.BITCOIN.equals(payAccount.getBankCode())) {
                    bitcoin.add(createAppPayAccount(payAccount));

                } else if (AppConstant.ONE_CODE_PAY.equals(payAccount.getBankCode())) {
                    oneCodePay.add(createAppPayAccount(payAccount));

                } else if (AppConstant.OTHER.equals(payAccount.getBankCode())) {
                    other.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.BANKACCOUNT.getCode().equals(payAccount.getAccountType())) {
                    //公司入款　银行账户
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
                    wechat.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.ALIPAY.getCode().equals(payAccount.getAccountType()) || PayAccountAccountType.ALIPAY_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    //ALIPAY("4", "支付宝"),ALIPAY_MICROPAY("11","支付宝反扫"),
                    alipay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.QQWALLET.getCode().equals(payAccount.getAccountType()) || PayAccountAccountType.QQ_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    //QQWALLET("5","QQ钱包"),QQ_MICROPAY("12","QQ反扫"),
                    qqWallet.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.JD_PAY.getCode().equals(payAccount.getAccountType())) {
                    //JD_PAY("7","京东钱包"),
                    jdPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.BAIFU_PAY.getCode().equals(payAccount.getAccountType())) {
                    //BAIFU_PAY("8","百度钱包"),
                    baifuPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.UNION_PAY.getCode().equals(payAccount.getAccountType())) {
                    //UNION_PAY("9","银联扫码"),
                    unionPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.EASY_PAY.getCode().equals(payAccount.getAccountType())) {
                    //EASY_PAY("13", "易收付"),
                    easyPay.add(createAppPayAccount(payAccount));

                } else if (PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType())) {
                    //线上支付
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

//        if (isMultipleAccount) {
//            getCompanyPayAccounts(company);
//            getCompanyPayAccounts(counter);
//            //电子支付:微信,支付宝,其它
//            getElectronicPays(wechat);
//        } else {
//            payAccountMap.put("company_deposit", company(companyPayAccount));
//            //电子支付:微信,支付宝,其它
//            payAccountMap.put("electronicPay", electronicPay(electronicPayAccount));
//        }

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

    private void fastRecharge(Map<String, Object> payAccountMap) {
        String url = getFastRechargeUrl();
        if (StringTool.isNotBlank(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            payAccountMap.put(AppConstant.IS_FAST_RECHARGE, url);
        }

    }

    private String getFastRechargeUrl() {
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
            PlayerRank rank = getRank();
            String[] ranks = ranksParam.getParamValue().split(",");
            for (String rankId : ranks) {
                if (String.valueOf(rank.getId()).equals(rankId)) {
                    isFastRecharge = true;
                    break;
                }
            }
        }

        if (isFastRecharge) {
            return rechargeUrlParam.getParamValue();
        } else {
            return "";
        }
    }

    /**
     * 查询优惠
     */
    @RequestMapping("/onlineSeachDiscount")
    @ResponseBody
    public String onlineSeachDiscount() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }

    /**
     * 查询优惠
     */
    @RequestMapping("/companySeachDiscount")
    @ResponseBody
    public String companySeachDiscount() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }

    /**
     * 查询优惠
     */
    @RequestMapping("/electronicSeachDiscount")
    @ResponseBody
    public String electronicSeachDiscount() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }

    /**
     * 查询优惠
     */
    @RequestMapping("/scanSeachDiscount")
    @ResponseBody
    public String scanSeachDiscount() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }

    /**
     * 查询优惠
     */
    @RequestMapping("/bitcoinSeachDiscount")
    @ResponseBody
    public String bitcoinSeachDiscount() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }


}
