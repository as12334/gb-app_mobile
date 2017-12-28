package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.query.sort.Direction;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.CttAnnouncementListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.*;

/**
 * 存款
 *
 * @author Bruce.QQ
 * @date 2016-12-07
 */
@Controller
@RequestMapping("/wallet/deposit")
public class DepositController extends BaseCommonDepositController {
    private static final String DEPOSIT_URI = "/deposit/Deposit";

    private static final String BANK_NOTICE_URI = "/deposit/BankNotice";

    /*支付宝*/
    private static final String ALIPAY = "alipay";
    /*微信支付*/
    private static final String WECHATPAY = "wechatpay";
    /*QQ钱包支付*/
    private static final String QQWALLET = "qqwallet";
    /*京东钱包支付*/
    private static final String JDWALLET = "jdwallet";
    /*百度钱包支付*/
    private static final String BDWALLET = "bdwallet";
    /*银联钱包支付*/
    private static final String UNIONPAY = "unionpay";
    /*一码付支付*/
    private static final String ONECODEPAY = "onecodepay";

    private static final String OTHERFAST = "other";

    //快速充值标记
    private static final String IS_FAST_RECHARGE = "isFastRecharge";

    //比特币支付
    private static final String BITCOIN = "bitcoin";

    /**
     * 存款首页
     *
     * @param model
     * @return
     */
    @RequestMapping("/index")
    public String index(Model model) {

        //网银存款,柜员机/柜台存款
        PlayerRank rank = getRank();
        boolean isMultipleAccount = rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        Map<String, Object> payAccountMap = getPayAccountMap(isMultipleAccount);

        model.addAttribute("command", new PayAccountVo());
        model.addAttribute("isMultipleAccount", isMultipleAccount);

        fastRecharge(payAccountMap);
        //是否支持数字货币
        model.addAttribute("digiccyAccountInfo", ParamTool.getDigiccyAccountInfo());
        model.addAttribute("payAccountMap", payAccountMap);
        model.addAttribute("isLotterySite", ParamTool.isLotterySite());
        return DEPOSIT_URI;
    }

    private Map getPayAccountMap(boolean isMultipleAccount) {
        List<PayAccount> payAccounts = searchPayAccounts();
        Map<String, Object> payAccountMap = new LinkedHashMap<>();

        if (CollectionTool.isEmpty(payAccounts) && payAccounts.size() <= 0) {
            return payAccountMap;
        }

        List<PayAccount> onlinePayAccount = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForWechat = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForAlipay = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForQQwallet = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForJD = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForBD = new ArrayList<>(0);
        List<PayAccount> scanPayAccountForUnionpay = new ArrayList<>(0);
        List<PayAccount> electronicPayAccount = new ArrayList<>(0);
        List<PayAccount> companyPayAccount = new ArrayList<>(0);
        List<PayAccount> bitcoinPayAccount = new ArrayList<>(0);
        for (PayAccount payAccount : payAccounts) {
            String type = payAccount.getType();
            String accountType = payAccount.getAccountType();
            if (PayAccountType.ONLINE_ACCOUNT.getCode().equals(type)) {
                if (PayAccountAccountType.THIRTY.getCode().equals(accountType)) {
                    onlinePayAccount.add(payAccount);
                } else if (PayAccountAccountType.WECHAT.getCode().equals(accountType)) {
                    scanPayAccountForWechat.add(payAccount);
                } else if (PayAccountAccountType.ALIPAY.getCode().equals(accountType)) {
                    scanPayAccountForAlipay.add(payAccount);
                } else if (PayAccountAccountType.QQWALLET.getCode().equals(accountType)) {
                    scanPayAccountForQQwallet.add(payAccount);
                } else if (PayAccountAccountType.JD_PAY.getCode().equals(accountType)) {
                    scanPayAccountForJD.add(payAccount);
                } else if (PayAccountAccountType.BAIFU_PAY.getCode().equals(accountType)) {
                    scanPayAccountForBD.add(payAccount);
                } else if (PayAccountAccountType.UNION_PAY.getCode().equals(accountType)) {
                    scanPayAccountForUnionpay.add(payAccount);
                }
            } else if (PayAccountType.COMPANY_ACCOUNT.getCode().equals(type)) {
                if (PayAccountAccountType.BANKACCOUNT.getCode().equals(accountType)) {
                    companyPayAccount.add(payAccount);
                } else if (PayAccountAccountType.THIRTY.getCode().equals(accountType)) {
                    if (BITCOIN.equals(payAccount.getBankCode())) {
                        bitcoinPayAccount.add(payAccount);
                    } else {
                        electronicPayAccount.add(payAccount);
                    }
                }
            }
        }

        //线上支付
        online(onlinePayAccount, payAccountMap);

        //微信扫码
        scanPay(scanPayAccountForWechat, payAccountMap, RechargeTypeEnum.WECHATPAY_SCAN.getCode(), WECHATPAY);
        scanPay(scanPayAccountForAlipay, payAccountMap, RechargeTypeEnum.ALIPAY_SCAN.getCode(), ALIPAY);
        scanPay(scanPayAccountForQQwallet, payAccountMap, RechargeTypeEnum.QQWALLET_SCAN.getCode(), QQWALLET);
        scanPay(scanPayAccountForJD, payAccountMap, RechargeTypeEnum.JDPAY_SCAN.getCode(), JDWALLET);
        scanPay(scanPayAccountForBD, payAccountMap, RechargeTypeEnum.BDWALLET_SAN.getCode(), BDWALLET);
        scanPay(scanPayAccountForUnionpay, payAccountMap, RechargeTypeEnum.UNION_PAY_SCAN.getCode(), UNIONPAY);

        //网银存款,柜员机/柜台存款
        if (isMultipleAccount) {
            payAccountMap.put("company_deposit", getCompanyPayAccounts(companyPayAccount));
            //电子支付:微信,支付宝,其它
            payAccountMap.put("electronicPay", getElectronicPays(electronicPayAccount));
        } else {
            payAccountMap.put("company_deposit", company(companyPayAccount));
            //电子支付:微信,支付宝,其它
            payAccountMap.put("electronicPay", electronicPay(electronicPayAccount));
        }

        //比特币支付
        bitcoinPay(bitcoinPayAccount, payAccountMap);

        return payAccountMap;
    }

    private void bitcoinPay(List<PayAccount> payAccounts, Map<String, Object> payAccountMap) {
        Map<String, PayAccount> tempMap = new HashMap<>();
        for (PayAccount payAccount : payAccounts) {
            if (tempMap.get(payAccount.getBankCode()) == null) {
                tempMap.put(payAccount.getBankCode(), payAccount);
            }
        }
        if (tempMap.get(BITCOIN) != null) {
            payAccountMap.put(RechargeTypeEnum.BITCOIN_FAST.getCode(), tempMap.get(BITCOIN));
        }
    }

    private List<PayAccount> setAliasName(List<PayAccount> payAccounts){
        Map<String,Integer> countMap = new HashMap<>();
        Map<String,String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(),DictEnum.BANKNAME);
        Map<String,List<PayAccount>> accountMap = CollectionTool.groupByProperty(payAccounts,PayAccount.PROP_BANK_CODE,String.class);
        for(PayAccount payAccount:payAccounts){
            if(StringTool.isBlank(payAccount.getAliasName())){
                String bankCode = payAccount.getBankCode();
                if(countMap.get(bankCode) == null){
                    countMap.put(bankCode,1);
                }else{
                    countMap.put(bankCode,countMap.get(bankCode) +  1);
                }

                if (BankCodeEnum.OTHER.getCode().equals(payAccount.getBankCode()) || BankCodeEnum.OTHER_BANK.getCode().equals(payAccount.getBankCode())) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                }else{
                    if(accountMap.get(bankCode).size() > 1){
                        payAccount.setAliasName(i18n.get(bankCode) + countMap.get(bankCode));
                    }else{
                        payAccount.setAliasName(i18n.get(bankCode));
                    }
                }
            }
        }

        return payAccounts;
    }

    private List<PayAccount> getElectronicPays(List<PayAccount> payAccounts) {
        setAliasName(payAccounts);
        for (PayAccount payAccount : payAccounts) {
            electronicPay(payAccount);
        }
        return payAccounts;
    }

    private List<PayAccount> electronicPay(List<PayAccount> payAccounts) {
        List<String> bankCodes = new ArrayList<>();
        Iterator<PayAccount> iterator = payAccounts.iterator();
        while (iterator.hasNext()) {
            PayAccount payAccount = iterator.next();
            if (bankCodes.contains(payAccount.getBankCode())) {
                iterator.remove();
            } else {
                bankCodes.add(payAccount.getBankCode());
            }
            electronicPay(payAccount);
        }

        return payAccounts;
    }

    private void electronicPay(PayAccount payAccount) {
        String bankCode = payAccount.getBankCode();
        if (WECHATPAY.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.WECHATPAY_FAST.getCode());
        }else  if (ALIPAY.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.ALIPAY_FAST.getCode());
        }else  if (QQWALLET.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.QQWALLET_FAST.getCode());
        }else  if (JDWALLET.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.JDWALLET_FAST.getCode());
        } else if (BDWALLET.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.BDWALLET_FAST.getCode());
        }else if (ONECODEPAY.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.ONECODEPAY_FAST.getCode());
        }else  if (OTHERFAST.equals(bankCode)) {
            payAccount.setRechargeType(RechargeTypeEnum.OTHER_FAST.getCode());
        }
    }

    private List<Map<String, Object>> company(List<PayAccount> payAccounts) {
        List<String> bankCodes = new ArrayList<>();
        List<Map<String, Object>> bankList = new ArrayList<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        for (PayAccount payAccount : payAccounts) {
            String bankCode = payAccount.getBankCode();
            if (!bankCodes.contains(bankCode)) {
                bankCodes.add(bankCode);
                Map<String, Object> bankMap = new HashMap<>(2, 1f);
                bankMap.put("value", payAccount.getId());
                if (StringTool.equals(BankCodeEnum.OTHER_BANK.getCode(), bankCode)) {
                    bankMap.put("text", payAccount.getCustomBankName());
                } else {
                    bankMap.put("text", i18n.get(bankCode));
                }
                bankList.add(bankMap);
            }
        }
        return bankList;
    }

    private List<Map<String, Object>> getCompanyPayAccounts(List<PayAccount> accounts) {
        setAliasName(accounts);
        List<Map<String, Object>> bankList = new ArrayList<>();
        Map<String, Object> bankMap;
        for (PayAccount payAccount : accounts) {
            bankMap = new HashMap<>(2, 1f);
            bankMap.put("text", payAccount.getAliasName());
            bankMap.put("value", payAccount.getId());
            bankList.add(bankMap);
        }

        return bankList;
    }

    private void scanPay(List<PayAccount> payAccounts, Map<String, Object> payAccountMap,
                         String rechargeType, String scanType) {
        if (payAccounts.size() > 0) {
            payAccountMap.put(rechargeType, scanType);
        }
    }

    private void online(List<PayAccount> payAccounts, Map<String, Object> payAccountMap) {
        if (payAccounts.size() > 0) {
            payAccountMap.put(RechargeTypeEnum.ONLINE_DEPOSIT.getCode(), "onlinepay");
        }
    }

    private List<PayAccount> searchPayAccounts() {
        PayAccountListVo listVo = new PayAccountListVo();
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        map.put("terminal", TerminalEnum.MOBILE.getCode());
        listVo.setConditions(map);
        return ServiceTool.payAccountService().searchPayAccountByRank(listVo);
    }

    /**
     * 获取银行公告
     *
     * @param model
     * @return
     */
    @RequestMapping("/loadBankNotice")
    public String loadBankNotice(Model model) {
        CttAnnouncementListVo cttAnnouncementListVo = new CttAnnouncementListVo();
        cttAnnouncementListVo.getSearch().setAnnouncementType(CttAnnouncementTypeEnum.BANK_ANNOOUNCEMENT.getCode());
        cttAnnouncementListVo.getSearch().setLocalLanguage(SessionManager.getLocale().toString());
        cttAnnouncementListVo.getSearch().setPublishTime(new Date());
        cttAnnouncementListVo.getSearch().setDisplay(true);
        //cttAnnouncementListVo.getQuery().addOrder(CttAnnouncement.PROP_PUBLISH_TIME, Direction.DESC);
        cttAnnouncementListVo.getQuery().addOrder(CttAnnouncement.PROP_ORDER_NUM, Direction.ASC);
        cttAnnouncementListVo.getPaging().setPageSize(3);
        cttAnnouncementListVo = ServiceTool.cttAnnouncementService().search(cttAnnouncementListVo);
        model.addAttribute("bankNotices", cttAnnouncementListVo);
        return BANK_NOTICE_URI;
    }

    private void fastRecharge(Map<String, Object> payAccountMap) {
        String url = getFastRechargeUrl();
        if (StringTool.isNotBlank(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            payAccountMap.put(IS_FAST_RECHARGE, url);
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
}