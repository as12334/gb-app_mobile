package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionTool;
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
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.locale.LocaleTool;
import so.wwb.gamebox.mobile.app.model.AppPlayerRechange;
import so.wwb.gamebox.mobile.app.model.AppSale;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;

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
     * 线上支付　优惠查询
     */
    @RequestMapping("/depositSeachDiscount")
    @ResponseBody
    public String depositSeachDiscount(AppPlayerRechange appPlayerRechange) {
        AppPayAccount appPayAccount = appPlayerRechange.getAppPayAccount();
        if (appPlayerRechange.getAppPayAccount() != null) {
            PayAccount payAccount = null;
            if (appPayAccount.getId() != null) {
                payAccount = getPayAccountById(appPayAccount.getId());
            }
            if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                        AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                        null, APP_VERSION);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            //统计该渠道连续存款失败次数
            Double rechargeAmount = appPlayerRechange.getRechargeAmount();
            if (rechargeAmount == null || rechargeAmount <= 0) {
                return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.MONEY_ERROR.getCode(),
                        AppErrorCodeEnum.MONEY_ERROR.getMsg(),
                        null, APP_VERSION);
            }
            Integer min = Const.MIN_MONEY;
            Integer max = Const.MAX_MONEY;
            if (PayAccountAccountType.THIRTY.getCode().equals(appPayAccount.getAccountType()) && AppConstant.BITCOIN.equals(appPayAccount.getBankCode())) {
                return bitcoinSeachDiscount(appPlayerRechange);
            } else if (PayAccountType.COMPANY_ACCOUNT.getCode().equals(appPayAccount.getType())) {
                PlayerRank rank = getRank();
                max = rank.getOnlinePayMax();
                min = rank.getOnlinePayMin();
            } else if (PayAccountType.ONLINE_ACCOUNT.getCode().equals(appPayAccount.getType())) {
                max = payAccount.getSingleDepositMax();
                min = payAccount.getSingleDepositMin();
                if (min == null) {
                    min = Const.MIN_MONEY;
                }
                if (max == null) {
                    max = Const.MAX_MONEY;
                }
                if (appPayAccount.getRandomAmount() && appPlayerRechange.getRandomCash() != null) {
                    rechargeAmount = rechargeAmount + appPlayerRechange.getRandomCash();
                }
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
            List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, appPlayerRechange.getRechargeType(), getUserPlayerById(appPlayerRechange.getUserPlayerId()));

            if (!isFee && !isReturnFee && CollectionTool.isEmpty(activityMessages)) {
                return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.NOT_SALE.getCode(),
                        AppErrorCodeEnum.NOT_SALE.getMsg(),
                        null, APP_VERSION);
            } else {
                List<AppSale> saleList =new ArrayList<>();
                for(VActivityMessage vActivityMessage : activityMessages){
                    AppSale appSale = new AppSale();
                    appSale.setId(vActivityMessage.getId());
                    appSale.setPreferential(vActivityMessage.isPreferential());
                    appSale.setActivityName(vActivityMessage.getActivityName());
                    saleList.add(appSale);
                }
                String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
                map.put("counterFee", counterFee);
                map.put("fee", fee);
                map.put("sales", saleList);
                String msg = "";
                if (fee > 0) {
                    msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.returnFee", counterFee);
                } else if (fee < 0) {
                    msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.needFee", counterFee);
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
    public String bitcoinSeachDiscount(AppPlayerRechange appPlayerRechange) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null, APP_VERSION);
    }

}
