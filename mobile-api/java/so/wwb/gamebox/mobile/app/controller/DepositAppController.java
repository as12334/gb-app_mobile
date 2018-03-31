package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppDepositPayEnum;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.app.validateForm.*;
import so.wwb.gamebox.mobile.controller.BaseDepositController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.company.enums.BankEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

@Controller
@RequestMapping("/depositOrigin")
public class DepositAppController extends BaseDepositController {
    /**
     * 存款首页
     */
    @RequestMapping("/index")
    @ResponseBody
    public String index(AppRequestModelVo model, HttpServletRequest request) {
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.getSearch().setTerminal(TerminalEnum.MOBILE.getCode());
        payAccountListVo.setPlayerId(SessionManager.getUserId());
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        Map<String, Long> channelCountMap = ServiceSiteTool.payAccountService().queryChannelCount(payAccountListVo);
        String fastRecharge = fastRecharge();
        boolean isFastRecharge = StringTool.isNotBlank(fastRecharge);
        if (!MapTool.isNotEmpty(channelCountMap) && !isFastRecharge) {
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.NO_AVAILABLE_CHANNELS.getMsg(),
                    null, APP_VERSION);
        }
        List<DepositPayApp> depositPayApps = new ArrayList<>();

        //拼接入口图片地址
        StringBuilder depositImgUrl = new StringBuilder();
        depositImgUrl.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName())).append("/");
        if (StringTool.equals(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            depositImgUrl.append(AppTypeEnum.ANDROID.getCode());
        }
        if (StringTool.equals(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            depositImgUrl.append(AppTypeEnum.IOS.getCode());
        }

        for (Map.Entry<String, Long> depositPay : channelCountMap.entrySet()) {
            if (depositPay.getValue() > 0) {
                DepositPayApp depositPayApp = new DepositPayApp();
                depositPayApp.setCode(depositPay.getKey());
                depositPayApp.setName(LocaleTool.tranMessage(Module.COMMON, depositPay.getKey()));
                depositPayApp.setIconUrl(String.format(DEPOSIT_ENTRY_URL, depositImgUrl, model.getResolution(), depositPay.getKey()));
                depositPayApps.add(depositPayApp);
            }
        }
        if (isFastRecharge) {
            DepositPayApp depositPayApp = new DepositPayApp();
            depositPayApp.setCode(fastRecharge);
            depositPayApp.setName(LocaleTool.tranMessage(Module.COMMON, AppConstant.IS_FAST_RECHARGE));
            depositPayApp.setIconUrl(String.format(DEPOSIT_ENTRY_URL, depositImgUrl, model.getResolution(), AppConstant.IS_FAST_RECHARGE));
            depositPayApps.add(depositPayApp);
        }

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                depositPayApps, APP_VERSION);
    }

    /**
     * 线上支付
     *
     * @param
     * @return
     */
    @RequestMapping("/online")
    @ResponseBody
    public String online(AppRequestModelVo model, HttpServletRequest request) {
        //可用银行
        List<Bank> banks = searchBank(BankEnum.TYPE_BANK.getCode());
        //层级
        PlayerRank rank = getRank();
        //玩家可用收款账号
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode(), TerminalEnum.MOBILE.getCode(), null, null);
        deleteMaintainChannel(payAccounts);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        payAccountListVo.setBanks(banks);
        Map<String, PayAccount> payBankMap = ServiceSiteTool.payAccountService().getOnlineAccount(payAccountListVo);

        AppRechargePay appRechargePay = new AppRechargePay();
        List<PayAccount> list = new ArrayList<>();
        if (MapTool.isNotEmpty(payBankMap)) {
            for (Map.Entry<String, PayAccount> bankPayAccount : payBankMap.entrySet()) {
                PayAccount payAccount = new PayAccount();
                PayAccount bankPayAccountValue = bankPayAccount.getValue();
                payAccount.setId(bankPayAccountValue.getId());
                payAccount.setPayName(LocaleTool.tranDict(DictEnum.BANKNAME, bankPayAccount.getKey()));
                payAccount.setSingleDepositMin(bankPayAccountValue.getSingleDepositMin());
                payAccount.setSingleDepositMax(bankPayAccountValue.getSingleDepositMax());
                payAccount.setPayType(bankPayAccountValue.getPayType());
                payAccount.setAccountType(bankPayAccountValue.getAccountType());
                payAccount.setType(bankPayAccountValue.getType());
                payAccount.setBankCode(bankPayAccount.getKey());
                list.add(payAccount);
            }
        }
        return fillAttr(appRechargePay, null, list, null, DepositWayEnum.ONLINE_DEPOSIT.getCode(), depositImgUrl(model, request, null));
    }

    /**
     * 微信支付
     *
     * @param
     * @return
     */
    @RequestMapping("/wechat")
    @ResponseBody
    public String wechat(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.WECHAT.getCode(), PayAccountAccountType.WECHAT_MICROPAY.getCode()});
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.FAST_WECHAT.getCode(), RechargeTypeEnum.WECHATPAY_FAST.getCode());
        String onliineWay = DepositWayEnum.WECHATPAY_SCAN.getCode();
        String companyWay = DepositWayEnum.WECHATPAY_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, electronicAccount, onliineWay, companyWay, depositImgUrl(model, request, AppDepositPayEnum.WECHAT.getCode()));
    }

    /**
     * 支付宝支付
     *
     * @param
     * @return
     */
    @RequestMapping("/alipay")
    @ResponseBody
    public String alipay(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.ALIPAY.getCode(), PayAccountAccountType.ALIPAY_MICROPAY.getCode()});
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.FAST_ALIPAY.getCode(), RechargeTypeEnum.ALIPAY_FAST.getCode());
        String onliineWay = DepositWayEnum.ALIPAY_SCAN.getCode();
        String companyWay = DepositWayEnum.ALIPAY_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, electronicAccount, onliineWay, companyWay, depositImgUrl(model, request, AppDepositPayEnum.ALIPAY.getCode()));
    }

    /**
     * qq支付
     */
    @RequestMapping("/qq")
    @ResponseBody
    public String qq(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.QQWALLET.getCode(), PayAccountAccountType.QQ_MICROPAY.getCode()});
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.QQWALLET.getCode(), RechargeTypeEnum.QQWALLET_FAST.getCode());
        String onliineWay = DepositWayEnum.QQWALLET_SCAN.getCode();
        String companyWay = DepositWayEnum.QQWALLET_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, electronicAccount, onliineWay, companyWay, depositImgUrl(model, request, AppDepositPayEnum.QQ.getCode()));
    }

    /**
     * 京东支付
     */
    @RequestMapping("/jd")
    @ResponseBody
    public String jd(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, PayAccountAccountType.JD_PAY.getCode(), null);
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.JDWALLET.getCode(), RechargeTypeEnum.JDWALLET_FAST.getCode());
        String onliineWay = DepositWayEnum.JDPAY_SCAN.getCode();
        String companyWay = DepositWayEnum.JDWALLET_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, electronicAccount, onliineWay, companyWay, depositImgUrl(model, request, AppDepositPayEnum.JD.getCode()));
    }

    /**
     * 百度支付
     */
    @RequestMapping("/bd")
    @ResponseBody
    public String bd(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, PayAccountAccountType.BAIFU_PAY.getCode(), null);
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.BDWALLET.getCode(), RechargeTypeEnum.BDWALLET_FAST.getCode());
        String onliineWay = DepositWayEnum.BDWALLET_SAN.getCode();
        String companyWay = DepositWayEnum.BDWALLET_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, electronicAccount, onliineWay, companyWay, depositImgUrl(model, request, AppDepositPayEnum.BD.getCode()));
    }

    /**
     * 银联支付
     */
    @RequestMapping("/unionpay")
    @ResponseBody
    public String union(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, PayAccountAccountType.UNION_PAY.getCode(), null);
        String onliineWay = DepositWayEnum.UNION_PAY_SCAN.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, null, onliineWay, null, depositImgUrl(model, request, AppDepositPayEnum.UNIONPAY.getCode()));
    }

    /**
     * 一码付
     */
    @RequestMapping("/onecodepay")
    @ResponseBody
    public String onecodepay(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.ONECODEPAY.getCode(), RechargeTypeEnum.ONECODEPAY_FAST.getCode());
        String companyWay = DepositWayEnum.ONECODEPAY_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, null, electronicAccount, null, companyWay, depositImgUrl(model, request, AppDepositPayEnum.ONECODEPAY.getCode()));
    }

    /**
     * 其他电子支付
     */
    @RequestMapping("/other")
    @ResponseBody
    public String other(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        List<PayAccount> electronicAccount = getElectronicAccount(rank, BankCodeEnum.OTHER.getCode(), RechargeTypeEnum.OTHER_FAST.getCode());
        String companyWay = DepositWayEnum.OTHER_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, null, electronicAccount, null, companyWay, depositImgUrl(model, request, AppDepositPayEnum.OTHER.getCode()));
    }

    /**
     * 易收付
     */
    @RequestMapping("/easy")
    @ResponseBody
    public String easy(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = getScanAccount(rank, PayAccountAccountType.EASY_PAY.getCode(), null);
        String onliineWay = DepositWayEnum.EASY_PAY.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        return fillAttr(appRechargePay, scanAccount, null, onliineWay, null, depositImgUrl(model, request, AppDepositPayEnum.EASYPAY.getCode()));
    }

    /**
     * 比特币支付步奏1-选择收款账号
     */
    @RequestMapping("/bitcoin")
    @ResponseBody
    public String bitcoin(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        List<PayAccount> electronicAccount = searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode(), null);
        Map<String, List<PayAccount>> payAccountMap = CollectionTool.groupByProperty(getCompanyPayAccount(electronicAccount), PayAccount.PROP_BANK_CODE, String.class);
        electronicAccount = payAccountMap.get(AppConstant.BITCOIN);
        String companyWay = DepositWayEnum.BITCOIN_FAST.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT));
        PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
        PlayerRecharge playerRecharge = new PlayerRecharge();
        playerRecharge.setRechargeType(RechargeTypeEnum.BITCOIN_FAST.getCode());
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRechargeVo.setResult(playerRecharge);
        String lastPayerBankcard = ServiceSiteTool.playerRechargeService().searchLastPayerBankcard(playerRechargeVo);
        appRechargePay.setPayerBankcard(lastPayerBankcard);
        return fillAttr(appRechargePay, null, electronicAccount, null, companyWay, depositImgUrl(model, request, AppDepositPayEnum.BITCONIT.getCode()));
    }

    /**
     * 网银存款步奏1-选择收款账户
     */
    @RequestMapping("/company")
    @ResponseBody
    public String company(AppRequestModelVo model, HttpServletRequest request) {
        PlayerRank rank = getRank();
        List<PayAccount> electronicAccount = searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.BANKACCOUNT.getCode(), null);
        //获取公司入款收款账号
        if (!isMultipleAccount()) {
            electronicAccount = getCompanyPayAccount(electronicAccount);
        } else {
            electronicAccount = getCompanyPayAccounts(electronicAccount);
        }

        String companyWay = DepositWayEnum.COMPANY_DEPOSIT.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_ONLINE_BANKING));
        return fillAttr(appRechargePay, null, electronicAccount, null, companyWay, depositImgUrl(model, request, null));
    }

    /**
     * 柜员机/柜台存款步奏1-选择收款账号
     */
    @RequestMapping("/counter")
    @ResponseBody
    public String counter(AppRequestModelVo model, HttpServletRequest request) {
        List<PayAccount> electronicAccount = searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.BANKACCOUNT.getCode(), true);
        //获取公司入款收款账号
        if (!isMultipleAccount()) {
            electronicAccount = getCompanyPayAccount(electronicAccount);
        } else {
            electronicAccount = getCompanyPayAccounts(electronicAccount);
        }
        String companyWay = DepositWayEnum.COMPANY_DEPOSIT.getCode();
        AppRechargePay appRechargePay = new AppRechargePay();
        appRechargePay.setHide(isHide(SiteParamEnum.PAY_ACCOUNT_HIDE_ATM_COUNTER));
        return fillAttr(appRechargePay, null, electronicAccount, null, companyWay, depositImgUrl(model, request, null));
    }

    /**
     * 存款　优惠查询
     */
    @RequestMapping("/seachSale")
    @ResponseBody
    public String seachSale(PlayerRechargeVo playerRechargeVo, @FormModel @Valid DepositForm form, BindingResult result) {
        if (result.hasErrors()) {
            LOG.debug("手机端存款:获取优惠活动，error:{0}", result.getAllErrors());
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    LocaleTool.tranMessage(Module.FUND, result.getAllErrors().get(0).getDefaultMessage()),
                    null, APP_VERSION);
        }
        PayAccount payAccount = getPayAccountBySearchId(playerRechargeVo.getAccount());
        if (payAccount == null ) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        //统计该渠道连续存款失败次数
        Double rechargeAmount = null;
        if (playerRechargeVo.getResult() != null) {
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
            PlayerRechargeVo playerRechargeVo4Count = new PlayerRechargeVo();
            playerRechargeVo4Count.getSearch().setPayAccountId(payAccount.getId());
            Integer failureCount = ServiceSiteTool.playerRechargeService().statisticalFailureCount(playerRechargeVo4Count, SessionManager.getUserId());
            map.put("failureCount", failureCount);
        }

        //验证存款金额的合法性
        if (max < rechargeAmount || min > rechargeAmount) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                    LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_OVER, min, max),
                    null, APP_VERSION);
        }
        PlayerRank rank = getRank();
        Double fee = calculateFee(rank, rechargeAmount);
        fee = fee == null ? 0 : fee;
        if (rechargeAmount + fee <= 0) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                    LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE),
                    null, APP_VERSION);
        }
        //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示优惠弹窗
        //手续费标志
        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());

        List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, playerRechargeVo.getDepositWay());

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

    /**
     * 比特币　查询优惠
     */
    public String bitcoinSeachDiscount(PlayerRechargeVo playerRechargeVo) {
        if (StringTool.isNotBlank(playerRechargeVo.getResult().getBankOrder())) {
            boolean isExistsTxId = checkTxId(playerRechargeVo.getResult().getBankOrder());
            if (!isExistsTxId) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.TXIDISEXISTS.getCode(),
                        AppErrorCodeEnum.TXIDISEXISTS.getMsg(),
                        null, APP_VERSION);
            }
        } else {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.TXIDISEMPTY.getCode(),
                    AppErrorCodeEnum.TXIDISEMPTY.getMsg(),
                    null, APP_VERSION);
        }
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
    public String onlinePay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result, HttpServletRequest request) {
        return onlineCommonDeposit(playerRechargeVo, result, request);
    }

    //扫码支付　存款
    @RequestMapping("/scanPay")
    @ResponseBody
    public String scanPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result, HttpServletRequest request) {
        return onlineCommonDeposit(playerRechargeVo, result, request);
    }

    //网银支付　存款
    @RequestMapping("/companyPay")
    @ResponseBody
    public String companyPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyBankDepositForm form, BindingResult result) {
        String type = AppDepositPayEnum.COMPANY_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, result, type);
    }

    //电子支付 存款
    @RequestMapping("/electronicPay")
    @ResponseBody
    public String electronicPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyElectronicDepositForm form, BindingResult result) {
        String type = AppDepositPayEnum.ELECTRONIC_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, result, type);
    }

    //比特币支付　存款
    @RequestMapping("/bitcoinPay")
    @ResponseBody
    public String bitcoinPay(PlayerRechargeVo playerRechargeVo, @FormModel @Valid BitcoinDepositForm form, BindingResult result) {
        String type = AppDepositPayEnum.BITCOIN_PAY.getCode();
        return companyCommonDeposit(playerRechargeVo, result, type);
    }
}
