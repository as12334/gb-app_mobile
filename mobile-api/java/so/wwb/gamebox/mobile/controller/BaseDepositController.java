package so.wwb.gamebox.mobile.controller;

import org.soul.commons.cache.locale.LocaleTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.model.AppPayAccount;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.*;

/**
 * Created by ed on 17-12-31.
 */
public class BaseDepositController {
    /**
     * 获取层级
     */
    public PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    public AppPayAccount createAppPayAccount(PayAccount payAccount) {
        if (payAccount == null) {
            return null;
        }
        AppPayAccount appPayAccount = new AppPayAccount();
        appPayAccount.setId(payAccount.getId());
        appPayAccount.setAccount(payAccount.getAccount());
        appPayAccount.setBankCode(LocaleTool.tranMessage(Module.DEPOSIT,payAccount.getBankCode()));
        appPayAccount.setAccountType(payAccount.getAccountType());
        appPayAccount.setType(payAccount.getType());
        appPayAccount.setAliasName(payAccount.getAliasName());
        appPayAccount.setRandomAmount(payAccount.getRandomAmount());
        appPayAccount.setSingleDepositMin(payAccount.getSingleDepositMin());
        appPayAccount.setSingleDepositMax(payAccount.getSingleDepositMax());
        scanPay(appPayAccount);
        if (StringTool.isNotBlank(payAccount.getType()) && PayAccountType.COMPANY_ACCOUNT.getCode().equals(payAccount.getType())) {
            appPayAccount.setSingleDepositMin(getRank().getOnlinePayMin());
            appPayAccount.setSingleDepositMax(getRank().getOnlinePayMax());
            appPayAccount.setAccountPrompt(payAccount.getAccountPrompt());
            appPayAccount.setFullName(payAccount.getFullName());
            appPayAccount.setAccountInformation(payAccount.getAccountInformation());
            appPayAccount.setCustomBankName(payAccount.getCustomBankName());
            appPayAccount.setOpenAcountName(payAccount.getOpenAcountName());
            appPayAccount.setQrCodeUrl(payAccount.getQrCodeUrl());
            appPayAccount.setRemark(payAccount.getRemark());
            electronicPay(appPayAccount);
        }
        return appPayAccount;
    }

    public List<Map<String, Object>> getCompanyPayAccounts(List<AppPayAccount> appPayAccounts) {
        setAliasName(appPayAccounts);
        List<Map<String, Object>> bankList = new ArrayList<>();
        Map<String, Object> bankMap;
        for (AppPayAccount appPayAccount : appPayAccounts) {
            bankMap = new HashMap<>(3, 1f);
            bankMap.put("text", appPayAccount.getAliasName());
            bankMap.put("value", appPayAccount.getId());
            bankMap.put("bankCode", appPayAccount.getBankCode());
            bankList.add(bankMap);
        }

        return bankList;
    }

    private void scanPay(AppPayAccount appPayAccount) {
        String bankCode = appPayAccount.getBankCode();
        String payAccountType = appPayAccount.getAccountType();
        if (PayAccountAccountType.WECHAT.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.FAST_WECHAT.getCode();
        } else if (PayAccountAccountType.ALIPAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.FAST_ALIPAY.getCode();
        } else if (PayAccountAccountType.QQWALLET.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.QQWALLET.getCode();
        } else if (PayAccountAccountType.JD_PAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.JDWALLET.getCode();
        } else if (PayAccountAccountType.BAIFU_PAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.BDWALLET.getCode();
        } else if (PayAccountAccountType.WECHAT_MICROPAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.WECHAT_MICROPAY.getCode();
        } else if (PayAccountAccountType.ALIPAY_MICROPAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.ALIPAY_MICROPAY.getCode();
        } else if (PayAccountAccountType.QQ_MICROPAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.QQ_MICROPAY.getCode();
        } else if (PayAccountAccountType.EASY_PAY.getCode().equals(payAccountType)) {
            bankCode = BankCodeEnum.EASY_PAY.getCode();
        } else if(PayAccountAccountType.UNION_PAY.getCode().equals(payAccountType)){
            bankCode = BankCodeEnum.UNIONPAY.getCode();
        }
        appPayAccount.setBankCode(bankCode);
    }

    private void electronicPay(AppPayAccount appPayAccount) {
        String bankCode = appPayAccount.getBankCode();
        if (AppConstant.WECHAT_PAY.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.WECHATPAY_FAST.getCode());
        } else if (AppConstant.ALI_PAY.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.ALIPAY_FAST.getCode());
        } else if (AppConstant.QQ_WALLET.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.QQWALLET_FAST.getCode());
        } else if (AppConstant.JD_WALLET.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.JDWALLET_FAST.getCode());
        } else if (AppConstant.BD_WALLET.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.BDWALLET_FAST.getCode());
        } else if (AppConstant.ONE_CODE_PAY.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.ONECODEPAY_FAST.getCode());
        } else if (AppConstant.OTHER.equals(bankCode)) {
            appPayAccount.setRechargeType(RechargeTypeEnum.OTHER_FAST.getCode());
        }
    }


    public void quickSelection(Map<String, Object> payAccountMap) {
        double[] quickMoneys = new double[]{100, 300, 500, 1000, 3000};
        payAccountMap.put("quickMoneys", quickMoneys);
    }

    public boolean isMultipleAccount() {
        PlayerRank rank = getRank();
        boolean isMultipleAccount = rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        return isMultipleAccount;
    }

    /**
     * 是否纯彩票站点
     *
     * @return
     */
    public static boolean isLotterySite() {
        if (CommonContext.get() != null
                && CommonContext.get().getSiteId() != null
                && CommonContext.get().getSiteId() < 1) {
            return false;
        }
        SysParam param = ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_IS_LOTTERY_SITE);
        return param != null ? Boolean.valueOf(param.getParamValue()) : false;
    }

    public List<Map<String, Object>> companyBankName(List<AppPayAccount> payAccounts) {
        List<String> bankCodes = new ArrayList<>();
        List<Map<String, Object>> bankList = new ArrayList<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        for (AppPayAccount appPayAccount : payAccounts) {
            String bankCode = appPayAccount.getBankCode();
            if (!bankCodes.contains(bankCode)) {
                bankCodes.add(bankCode);
                Map<String, Object> bankMap = new HashMap<>(3, 1f);
                bankMap.put("value", appPayAccount.getId());
                bankMap.put("bankCode", appPayAccount.getBankCode());
                if (StringTool.equals(BankCodeEnum.OTHER_BANK.getCode(), bankCode)) {
                    bankMap.put("text", appPayAccount.getCustomBankName());
                } else {
                    bankMap.put("text", i18n.get(bankCode));
                }
                bankList.add(bankMap);
            }
        }
        return bankList;
    }

    public List<AppPayAccount> setAliasName(List<AppPayAccount> appPayAccountList) {
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        Map<String, List<AppPayAccount>> accountMap = CollectionTool.groupByProperty(appPayAccountList, PayAccount.PROP_BANK_CODE, String.class);
        for (AppPayAccount appPayAccount : appPayAccountList) {
            if (StringTool.isBlank(appPayAccount.getAliasName())) {
                String bankCode = appPayAccount.getBankCode();
                if (countMap.get(bankCode) == null) {
                    countMap.put(bankCode, 1);
                } else {
                    countMap.put(bankCode, countMap.get(bankCode) + 1);
                }

                if (BankCodeEnum.OTHER.getCode().equals(appPayAccount.getBankCode()) || BankCodeEnum.OTHER_BANK.getCode().equals(appPayAccount.getBankCode())) {
                    appPayAccount.setAliasName(appPayAccount.getCustomBankName());
                } else {
                    if (accountMap.get(bankCode).size() > 1) {
                        appPayAccount.setAliasName(i18n.get(bankCode) + countMap.get(bankCode));
                    } else {
                        appPayAccount.setAliasName(i18n.get(bankCode));
                    }
                }
            }
        }

        return appPayAccountList;
    }

    /**
     * 去除维护中收款账户
     *
     * @param appPayAccountList
     */
    public void deleteMaintainChannel(List<AppPayAccount> appPayAccountList) {
        if (CollectionTool.isEmpty(appPayAccountList)) {
            return;
        }
        Map<String, Bank> bankMap = CacheBase.getBank();
        Bank bank;
        Iterator<AppPayAccount> accountIterator = appPayAccountList.iterator();
        while (accountIterator.hasNext()) {
            AppPayAccount appPayAccount = accountIterator.next();
            bank = bankMap.get(appPayAccount.getBankCode());
            if (bank == null || (bank.getIsUse() != null && !bank.getIsUse())) {
                accountIterator.remove();
            }
        }
    }

    public List<AppPayAccount> getElectronicPays(List<AppPayAccount> appPayAccounts) {
        setAliasName(appPayAccounts);
        for (AppPayAccount appPayAccount : appPayAccounts) {
            electronicPay(appPayAccount);
        }
        return appPayAccounts;
    }
}