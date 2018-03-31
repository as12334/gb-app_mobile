package so.wwb.gamebox.mobile.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.support._Module;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.pay.enums.CommonFieldsConst;
import org.soul.model.pay.enums.PayApiTypeConst;
import org.soul.model.pay.vo.OnlinePayVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import org.springframework.validation.BindingResult;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppDepositPayEnum;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.AppPayAccount;
import so.wwb.gamebox.mobile.app.model.AppRechargePay;
import so.wwb.gamebox.mobile.app.model.AppRequestModelVo;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteCustomerService;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.so.PayAccountSo;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightListVo;
import so.wwb.gamebox.model.master.enums.*;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeListVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.DEPOSIT_IMG_URL;

/**
 * Created by ed on 17-12-31.
 */
public class BaseDepositController {
    public Log LOG = LogFactory.getLog(BaseOriginController.class);

    /**
     * 获取层级
     */
    public PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    /**
     * 查询收款账号
     */
    public List<PayAccount> searchPayAccount(String type, String accountType, String terminal, Boolean supportAtmCounter, String[] accountTypes) {
        PayAccountListVo listVo = new PayAccountListVo();
        Map<String, Object> map = new HashMap<>(7, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", type);
        map.put("accountType", accountType);
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        if (StringTool.isNotBlank(terminal)) {
            map.put("terminal", terminal);
        }
        map.put("supportAtmCounter", supportAtmCounter);
        map.put("accountTypes", accountTypes);
        //map.put(PayAccount.PROP_BANK_CODE,)
        listVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
    }

    /**
     * 获取默认客服
     *
     * @return
     */
    public String getCustomerService() {
        SiteCustomerService siteCustomerService = Cache.getDefaultCustomerService();
        if (siteCustomerService == null) {
            return null;
        }
        String url = siteCustomerService.getParameter();
        if (StringTool.isNotBlank(url) && !url.contains("http")) {
            url = "http://" + url;
        }
        return url.replace("\r\n", "");
    }

    /**
     * 实体替换
     *
     * @param payAccounts
     * @param onlineWay   　线上支付获取优惠类型
     * @param companyWay  　公司入款获取优惠类型
     * @return
     */
    public List<AppPayAccount> changeModel(List<PayAccount> payAccounts,
                                           String onlineWay,
                                           String companyWay, Map<String,String> imgUrl) {
        if (!CollectionTool.isNotEmpty(payAccounts)) {
            return null;
        }
        List<AppPayAccount> appPayAccounts = new ArrayList<>();
        PayAccountVo payAccountVo = new PayAccountVo();

        for (PayAccount payAccount : payAccounts) {
            AppPayAccount appPayAccount = new AppPayAccount();
            appPayAccount.setSearchId(payAccountVo.getSearchId(payAccount.getId()));
            appPayAccount.setPayName(payAccount.getPayName());
            appPayAccount.setAccount(payAccount.getAccount());
            appPayAccount.setType(payAccount.getType());
            appPayAccount.setCode(payAccount.getCode());
            appPayAccount.setBankCode(payAccount.getBankCode());
            appPayAccount.setAccountType(payAccount.getAccountType());
            appPayAccount.setSingleDepositMin(payAccount.getSingleDepositMin() == null ? Const.MIN_MONEY : payAccount.getSingleDepositMin());
            appPayAccount.setSingleDepositMax(payAccount.getSingleDepositMax() == null ? Const.MAX_MONEY : payAccount.getSingleDepositMax());
            boolean isOnlinePay = StringTool.isNotBlank(payAccount.getType()) && StringTool.isNotBlank(payAccount.getAccountType()) && PayAccountAccountType.THIRTY.getCode().equals(payAccount.getAccountType()) && PayAccountType.ONLINE_ACCOUNT.getCode().equals(payAccount.getType());
            appPayAccount.setDepositWay(isOnlinePay ? companyWay : onlineWay);
            appPayAccount.setPayType(payAccount.getPayType());
            if (StringTool.isNotBlank(payAccount.getType()) && PayAccountType.COMPANY_ACCOUNT.getCode().equals(payAccount.getType())) {
                appPayAccount.setSingleDepositMin(getRank().getOnlinePayMin());
                appPayAccount.setSingleDepositMax(getRank().getOnlinePayMax());
                appPayAccount.setAliasName(payAccount.getAliasName());
                appPayAccount.setRandomAmount(payAccount.getRandomAmount());
                appPayAccount.setFullName(payAccount.getFullName());
                appPayAccount.setAccountInformation(payAccount.getAccountInformation());
                appPayAccount.setCustomBankName(payAccount.getCustomBankName());
                appPayAccount.setOpenAcountName(payAccount.getOpenAcountName());
                appPayAccount.setQrCodeUrl(payAccount.getQrCodeUrl() == null ? null :
                        ImageTag.getThumbPath(imgUrl.get("serverName"),payAccount.getQrCodeUrl(),AppConstant.QRHEIGHT,AppConstant.QRWIDTH));
                appPayAccount.setRemark(payAccount.getRemark());
                appPayAccount.setDepositWay(companyWay);
            }
            boolean isOnlineBank = StringTool.isNotBlank(payAccount.getType()) && StringTool.isNotBlank(payAccount.getAccountType()) && PayAccountAccountType.BANKACCOUNT.getCode().equals(payAccount.getAccountType()) && PayAccountType.COMPANY_ACCOUNT.getCode().equals(payAccount.getType());
            appPayAccount.setRechargeType(isOnlineBank ? RechargeTypeEnum.ONLINE_BANK.getCode() : appPayAccount.getDepositWay());
            appPayAccount.setImgUrl(isOnlineBank ? imgUrl.get("depositImgUrl").replace("null", appPayAccount.getBankCode()) : imgUrl.get("depositImgUrl"));
            appPayAccounts.add(appPayAccount);
        }
        return appPayAccounts;
    }

    /**
     * 拼接渠道图片地址
     *
     * @param model
     * @param request
     * @return
     */
    protected Map<String,String> depositImgUrl(AppRequestModelVo model, HttpServletRequest request, String code) {
        Map<String,String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName())).append("/");
        if (StringTool.equals(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            sb.append(AppTypeEnum.ANDROID.getCode());
        }
        if (StringTool.equals(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            sb.append(AppTypeEnum.IOS.getCode());
        }
        map.put("depositImgUrl",String.format(DEPOSIT_IMG_URL, sb, model.getResolution(), code));
        map.put("serverName",request.getServerName());
        return map;
    }

    /**
     * 是否隐藏收款账号
     *
     * @param
     */
    public boolean isHide(SiteParamEnum paramEnum) {
        // 查询隐藏参数
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.CONTENT_PAY_ACCOUNT_HIDE);
        if (sysParam == null) return false;

        SysParam hideParam = ParamTool.getSysParam(paramEnum);

        // 判断是否隐藏收款账号
        if ("true".equals(sysParam.getParamValue()) && "true".equals(hideParam.getParamValue())) {
            return true;
        }
        return false;
    }

    /**
     * 线上支付查询可用渠道
     *
     * @param rank
     * @param accountType
     * @param accountTypes
     * @return
     */
    public Map<String, PayAccount> getScanAccount(PlayerRank rank, String accountType, String[] accountTypes) {
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType, TerminalEnum.MOBILE.getCode(), null, accountTypes);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        return ServiceSiteTool.payAccountService().getScanAccount(payAccountListVo);
    }

    /**
     * @param rank
     * @param bankCode
     * @param rechargeType
     * @return
     */
    public List<PayAccount> getElectronicAccount(PlayerRank rank, String bankCode, String rechargeType) {
        //获取该渠道下电子支付账号
        List<PayAccount> payAccounts = getElectronicPayAccount(bankCode);
        if (CollectionTool.isEmpty(payAccounts)) {
            return null;
        }
        //电子支付是否展示多个支付
        boolean display = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        List<PayAccount> payAccountList;
        if (display) {
            payAccountList = getCompanyPayAccounts(payAccounts, rechargeType);
        } else {
            //默认只展示一个
            PayAccount payAccount = payAccounts.get(0);
            Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.FUND_RECHARGE_TYPE);
            payAccount.setAliasName(i18n.get(rechargeType));
            payAccountList = new ArrayList<>(1);
            payAccountList.add(payAccount);
        }
        return payAccountList;
    }

    /**
     * 根据bankCode获取电子支付收款账号
     *
     * @param bankCode
     * @return
     */
    private List<PayAccount> getElectronicPayAccount(String bankCode) {
        PayAccountSo payAccountSo = new PayAccountSo();
        payAccountSo.setType(PayAccountType.COMMPANY_ACCOUNT_CODE);
        payAccountSo.setAccountType(PayAccountAccountType.THIRTY.getCode());
        payAccountSo.setBankCode(bankCode);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setSearch(payAccountSo);
        return searchPayAccount(payAccountListVo, null, null);
    }

    /**
     * 查询收款账号
     */
    public List<PayAccount> searchPayAccount(PayAccountListVo payAccountListVo, Boolean supportAtmCounter, String[] accountTypes) {
        PayAccountSo payAccountSo = payAccountListVo.getSearch();
        Map<String, Object> map = new HashMap<>(7, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", payAccountSo.getType());
        map.put("accountType", payAccountSo.getAccountType());
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        map.put("terminal", payAccountSo.getTerminal());
        map.put("supportAtmCounter", supportAtmCounter);
        map.put("accountTypes", accountTypes);
        map.put(PayAccount.PROP_BANK_CODE, payAccountSo.getBankCode());
        payAccountListVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(payAccountListVo);
    }

    /**
     * 展示电子支付多个收款账号
     *
     * @param accounts
     * @return
     */
    public List<PayAccount> getCompanyPayAccounts(List<PayAccount> accounts, String rechargeType) {
        if (CollectionTool.isEmpty(accounts)) {
            return null;
        }
        int size = accounts.size();
        int count = 1;
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.FUND_RECHARGE_TYPE);
        String bankName = i18n.get(rechargeType);
        String other = RechargeTypeEnum.OTHER_FAST.getCode();
        for (PayAccount payAccount : accounts) {
            if (StringTool.isBlank(payAccount.getAliasName())) {
                if (other.equals(rechargeType) || other.equals(rechargeType)) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                } else if (size > 1) {
                    payAccount.setAliasName(bankName + count);
                    count++;
                } else {
                    payAccount.setAliasName(bankName);
                }
            }
        }
        return accounts;
    }

    /**
     * 填充　AppRechargePay　属性
     *
     * @param appRechargePay
     * @param scanAccount
     * @param electronicAccount
     * @param onliineWay
     * @param companyWay
     * @return
     */
    public String fillAttr(AppRechargePay appRechargePay,
                           Map<String, PayAccount> scanAccount,
                           List<PayAccount> electronicAccount,
                           String onliineWay,
                           String companyWay,
                           Map<String,String> imgUrl) {
        List<AppPayAccount> scanAppPayAccounts = null;
        List<AppPayAccount> electronicAppPayAccounts = null;
        if (MapTool.isNotEmpty(scanAccount)) {
            List<PayAccount> list = new ArrayList<>();
            for (Map.Entry<String, PayAccount> payAccountEntry : scanAccount.entrySet()) {
                PayAccount payAccount = payAccountEntry.getValue();
                if (StringTool.isNotBlank(onliineWay)) {
                    payAccount.setPayName(LocaleTool.tranMessage(Module.COMMON, "recharge_type." + onliineWay));
                }
                if (PayAccountAccountType.QQ_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    payAccount.setPayName(LocaleTool.tranMessage(Module.COMMON, AppConstant.QQ_MICROPAY));
                } else if (PayAccountAccountType.ALIPAY_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    payAccount.setPayName(LocaleTool.tranMessage(Module.COMMON, AppConstant.ALIPAY_MICROPAY));
                } else if (PayAccountAccountType.WECHAT_MICROPAY.getCode().equals(payAccount.getAccountType())) {
                    payAccount.setPayName(LocaleTool.tranMessage(Module.COMMON, AppConstant.WECHAT_MICROPAY));
                }
                list.add(payAccount);
            }
            scanAppPayAccounts = changeModel(list, onliineWay, null, imgUrl);
            appRechargePay.setArrayList(scanAppPayAccounts);
        }

        if (CollectionTool.isNotEmpty(electronicAccount)) {
            electronicAppPayAccounts = changeModel(electronicAccount, null, companyWay, imgUrl);
            if (CollectionTool.isNotEmpty(scanAppPayAccounts)) {
                electronicAppPayAccounts.addAll(scanAppPayAccounts);
            }
            appRechargePay.setArrayList(electronicAppPayAccounts);
        } else if (!CollectionTool.isNotEmpty(scanAppPayAccounts)) {
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }

        appRechargePay.setCurrency(SessionManager.getUser().getDefaultCurrency());
        appRechargePay.setCustomerService(getCustomerService());
        appRechargePay.setQuickMoneys(quickSelection());
        appRechargePay.setMultipleAccount(isMultipleAccount());
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                appRechargePay, APP_VERSION);
    }

    /**
     * 查询收款账号(无终端)
     */
    public List<PayAccount> searchPayAccount(String type, String accountType, Boolean supportAtmCounter) {
        return searchPayAccount(type, accountType, null, supportAtmCounter, null);
    }

    public List<PayAccount> getCompanyPayAccounts(List<PayAccount> accounts) {
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        Map<String, List<PayAccount>> accountMap = CollectionTool.groupByProperty(accounts, PayAccount.PROP_BANK_CODE, String.class);
        for (PayAccount payAccount : accounts) {
            if (StringTool.isBlank(payAccount.getAliasName())) {
                String bankCode = payAccount.getBankCode();
                if (countMap.get(bankCode) == null) {
                    countMap.put(bankCode, 1);
                } else {
                    countMap.put(bankCode, countMap.get(bankCode) + 1);
                }
                if (BankCodeEnum.OTHER.getCode().equals(bankCode) || BankCodeEnum.OTHER_BANK.getCode().equals(bankCode)) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                } else {
                    if (accountMap.get(bankCode).size() > 1) {
                        payAccount.setAliasName(i18n.get(bankCode) + countMap.get(bankCode));
                    } else {
                        payAccount.setAliasName(i18n.get(bankCode));
                    }
                }
            }
        }
        return accounts;
    }

    /**
     * 获取公司入款收款账号（根据固定顺序获取收款账户）
     */
    public List<PayAccount> getCompanyPayAccount(List<PayAccount> accounts) {
        List<String> bankCodes = new ArrayList<>();
        List<PayAccount> payAccounts = new ArrayList<>();
        for (PayAccount payAccount : accounts) {
            if (!bankCodes.contains(payAccount.getBankCode())) {
                payAccounts.add(payAccount);
                bankCodes.add(payAccount.getBankCode());
            }
        }
        return payAccounts;
    }

    /**
     * 查询玩家可选择的存款渠道
     *
     * @param
     */
    public List<Bank> searchBank(String type) {
        Map<String, Bank> bankMap = Cache.getBank();
        if (bankMap == null || bankMap.size() <= 0) {
            return null;
        }
        return CollectionQueryTool.query(bankMap.values(), Criteria.add(Bank.PROP_TYPE, Operator.EQ, type));
    }

    //公司入款快选金额
    public int[] quickSelection() {
        int[] quickMoneys = new int[]{100, 200, 500, 1000, 5000};
        return quickMoneys;
    }

    //是否展示多个账号
    public boolean isMultipleAccount() {
        PlayerRank rank = getRank();
        boolean isMultipleAccount = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        return isMultipleAccount;
    }

    public String fastRecharge() {
        String url = getFastRechargeUrl();
        if (StringTool.isNotBlank(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            return url;
        }
        return null;
    }

    public String getFastRechargeUrl() {
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
     * 去除维护中收款账户
     *
     * @param payAccounts
     */
    public void deleteMaintainChannel(List<PayAccount> payAccounts) {
        Map<String, Bank> bankMap = CacheBase.getBank();
        Bank bank;
        Iterator<PayAccount> accountIterator = payAccounts.iterator();
        while (accountIterator.hasNext()) {
            PayAccount payAccount = accountIterator.next();
            bank = bankMap.get(payAccount.getBankCode());
            if (bank == null || (bank.getIsUse() != null && !bank.getIsUse())) {
                accountIterator.remove();
            }
        }
    }

    /**
     * 获取主货币符号
     *
     * @return
     */
    public String getCurrencySign() {
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
     * 计算手续费
     */
    public Double calculateFee(PlayerRank rank, Double rechargeAmount) {
        if (rank == null || rechargeAmount == null) {
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
        Double fee = 0d;
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
        return fee == null ? 0d : fee;
    }

    /**
     * 计算在层级设置的符合免手续费的存款次数
     *
     * @param rank        玩家层级
     * @param isFee       手续费标志
     * @param isReturnFee 返手续费标志
     */
    private long getDepositCountInTime(PlayerRank rank, boolean isFee, boolean isReturnFee) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        Date now = SessionManager.getDate().getNow();
        listVo.getSearch().setEndTime(now);
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.setRank(rank);
        if (isFee && rank.getFreeCount() != null && rank.getFreeCount() > 0 && rank.getFeeTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getFeeTime()));
        } else if (isReturnFee && rank.getReturnFeeCount() != null && rank.getReturnFeeCount() > 0 && rank.getReturnTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getReturnTime()));
        }
        return ServiceSiteTool.playerRechargeService().searchPlayerRechargeCount(listVo);
    }

    /**
     * 按计算手续费方式计算手续费
     *
     * @param feeType        计算手续费方式：按比例、固定
     * @param feeMoney       收取手续费固定值
     * @param rechargeAmount 存款金额
     * @param maxFee         手续费最大值
     */
    private double computeFee(String feeType, Double feeMoney, Double rechargeAmount, Double maxFee) {
        double fee = 0d;
        if (feeMoney != null || rechargeAmount != null) {
            if (RankFeeType.PROPORTION.getCode().equals(feeType)) {
                fee = rechargeAmount * feeMoney / 100.0;
            } else if (RankFeeType.FIXED.getCode().equals(feeType)) {
                fee = feeMoney;
            }
        }
        if (maxFee != null && fee > maxFee) {
            fee = maxFee;
        }
        return fee;
    }

    /**
     * 根据存款金额、存款方式获取优惠
     *
     * @param rechargeAmount
     * @param type
     * @return
     */
    public List<VActivityMessage> searchSaleByAmount(Double rechargeAmount, String type) {
        UserPlayer userPlayer = getUserPlayer();
        VActivityMessageVo vActivityMessageVo = new VActivityMessageVo();
        vActivityMessageVo.getSearch().setDepositWay(type);
        vActivityMessageVo.setDepositAmount(rechargeAmount);
        vActivityMessageVo.setRankId(userPlayer.getRankId());
        vActivityMessageVo.setLocal(SessionManager.getLocale().toString());
        vActivityMessageVo = ServiceActivityTool.vActivityMessageService().searchDepositPromotions(vActivityMessageVo);
        LinkedHashSet<VActivityMessage> vActivityMessages = vActivityMessageVo.getvActivityMessageList();
        if (CollectionTool.isEmpty(vActivityMessages)) {
            return new ArrayList<>();
        }
        //如果玩家首存可同时显示首存送和存就送
        if (userPlayer.getIsFirstRecharge() != null && userPlayer.getIsFirstRecharge()) {
            return setClassifyKeyName(new ArrayList(vActivityMessages));
        }
        //玩家非首存，查询存就送优惠
        List<VActivityMessage> activityList = CollectionQueryTool.query(vActivityMessages, Criteria.add(VActivityMessage.PROP_CODE, Operator.EQ, ActivityTypeEnum.DEPOSIT_SEND.getCode()));
        return setClassifyKeyName(activityList);
    }


    /**
     * 设置分类
     */
    private List<VActivityMessage> setClassifyKeyName(List<VActivityMessage> vActivityMessages) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        for (VActivityMessage message : vActivityMessages) {
            String str = message.getActivityClassifyKey() + ":" + SessionManager.getLocale();
            message.setClassifyKeyName(siteI18nMap.get(str).getValue());
        }
        return vActivityMessages;
    }

    /**
     * 根据id获取收款账号
     *
     * @param payAccountId
     * @return
     */
    public PayAccount getPayAccountById(Integer payAccountId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.getSearch().setId(payAccountId);
        payAccountVo = ServiceSiteTool.payAccountService().get(payAccountVo);
        return payAccountVo.getResult();
    }

    /**
     * 根据searchId获取收款账号
     * searchId 加密后的 payAccountId
     *
     * @param
     * @return
     */
    public PayAccount getPayAccountBySearchId(String searchId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.setSearchId(searchId);
        payAccountVo = ServiceSiteTool.payAccountService().get(payAccountVo);
        PayAccount payAccount = payAccountVo.getResult();
        if (payAccount != null && !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            LOG.info("账号{0}已停用,故返回收款账号null", payAccount.getPayName());
            return null;
        }
        return payAccount;
    }

    /**
     * 获取玩家信息
     */
    public UserPlayer getUserPlayer() {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult();
    }

    /**
     * 验证txId是否已提交过
     *
     * @param txId
     * @return
     */
    public boolean checkTxId(String txId) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        listVo.getSearch().setBankOrder(txId);
        listVo.getSearch().setRechargeStatus(RechargeStatusEnum.FAIL.getCode());
        return ServiceSiteTool.playerRechargeService().isExistsTxId(listVo);
    }

    /**
     * 线上支付（含扫码支付）提交公共方法
     */
    public String onlineCommonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            LOG.debug("手机端存款:表单验证未通过，error:{0}", result.getAllErrors());
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    LocaleTool.tranMessage(Module.VALID, result.getAllErrors().get(0).getDefaultMessage()),
                    null, APP_VERSION);
        }
        PayAccount payAccount = getPayAccountBySearchId(playerRechargeVo.getAccount());
        if (payAccount == null) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }
        PlayerRank rank = getRank();
        playerRechargeVo = saveRecharge(playerRechargeVo, payAccount, rank, RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode(),
                playerRechargeVo.getResult().getRechargeType());
        if (playerRechargeVo.isSuccess()) {
            //声音提醒站长中心
            onlineToneWarn();
            //设置session相关存款数据
            //setRechargeCount();
            return ThirdPartyPay(playerRechargeVo, request);
        } else {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.DEPOSIT_FAIL.getCode(),
                    playerRechargeVo.getErrMsg(),
                    null, APP_VERSION);
        }
    }

    public String ThirdPartyPay(PlayerRechargeVo playerRechargeVo, HttpServletRequest request) {
        LOG.info("调用第三方pay：交易号：{0}", playerRechargeVo.getResult().getTransactionNo());
        if (StringTool.isBlank(playerRechargeVo.getResult().getTransactionNo())) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.ORDER_ERROR.getCode(),
                    AppErrorCodeEnum.ORDER_ERROR.getMsg(),
                    null, APP_VERSION);
        }
        try {
            playerRechargeVo.getSearch().setTransactionNo(playerRechargeVo.getResult().getTransactionNo());
            if (playerRechargeVo.getResult().getId() != null) {
                playerRechargeVo.getSearch().setId(playerRechargeVo.getResult().getId());
            }
            playerRechargeVo._setDataSourceId(null);
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
                Map<String, Object> map = new HashMap<>();
                map.put("payLink", url);
                return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map, APP_VERSION);
            }
        } catch (Exception e) {
            LOG.error(e, "调用第三方pay出错交易号：{0}", playerRechargeVo.getSearch().getTransactionNo());
        }
        return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.DEPOSIT_FAIL.getCode(),
                AppErrorCodeEnum.DEPOSIT_FAIL.getMsg(),
                null, APP_VERSION);
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
        sysResourceListVo.getSearch().setUrl(AppConstant.MCENTER_ONLINE_RECHARGE_URL);
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
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
        return ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
    }

    public String companyCommonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result, String type) {
        if (result.hasErrors()) {
            LOG.debug("手机端存款:表单验证未通过，error:{0}", result.getAllErrors());
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    LocaleTool.tranMessage(Module.VALID, result.getAllErrors().get(0).getDefaultMessage()),
                    null, APP_VERSION);
        }
        PayAccount payAccount = getPayAccountBySearchId(playerRechargeVo.getAccount());
        if (payAccount == null) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.CHANNEL_CLOSURE.getCode(),
                    AppErrorCodeEnum.CHANNEL_CLOSURE.getMsg(),
                    null, APP_VERSION);
        }

        if (AppDepositPayEnum.COMPANY_PAY.getCode().equals(type)) {
            playerRechargeVo = companySaveRecharge(playerRechargeVo, payAccount);
        } else if (AppDepositPayEnum.ELECTRONIC_PAY.getCode().equals(type)) {
            playerRechargeVo = electronicSaveRecharge(playerRechargeVo, payAccount);
        } else if (AppDepositPayEnum.BITCOIN_PAY.getCode().equals(type)) {
            playerRechargeVo = bitcoinSaveRecharge(playerRechargeVo, payAccount);
        }
        //保存订单
        playerRechargeVo = ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
        if (playerRechargeVo.isSuccess()) {
            tellerReminder(playerRechargeVo);
        }
        return getVoMessage(playerRechargeVo);
    }

    /**
     * 返回信息
     *
     * @param
     * @param playerRechargeVo
     * @return
     */
    public String getVoMessage(PlayerRechargeVo playerRechargeVo) {
        Map<String, Object> map = new HashMap<>();
        String code;
        if (!playerRechargeVo.isSuccess()) {
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
            code = AppErrorCodeEnum.DEPOSIT_FAIL.getCode();
        } else {
            map.put("orderNo", playerRechargeVo.getResult().getTransactionNo());
            code = AppErrorCodeEnum.SUCCESS.getCode();
        }
        if (playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getOkMsg())) {
            playerRechargeVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));

        } else if (!playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getErrMsg())) {
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }

        return AppModelVo.getAppModeVoJson(playerRechargeVo.isSuccess(), code,
                StringTool.isNotBlank(playerRechargeVo.getOkMsg()) ? playerRechargeVo.getOkMsg() : playerRechargeVo.getErrMsg(),
                map, APP_VERSION);
    }

    /**
     * 存款消息提醒发送消息给前端
     *
     * @param playerRechargeVo
     */
    public void tellerReminder(PlayerRechargeVo playerRechargeVo) {
        PlayerRecharge recharge = playerRechargeVo.getResult();
        if (recharge == null || recharge.getId() == null) {
            return;
        }
        //推送消息给前端
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_RECHARGE_REMINDER.getCode());
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("date", recharge.getCreateTime() == null ? new Date() : recharge.getCreateTime());
        map.put("currency", getCurrencySign());
        map.put("type", recharge.getRechargeTypeParent());
        map.put("amount", CurrencyTool.formatCurrency(recharge.getRechargeAmount()));
        map.put("id", recharge.getId());
        map.put("transactionNo", recharge.getTransactionNo());
        message.setMsgBody(JsonTool.toJson(map));
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());
        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl(AppConstant.MCENTER_COMPANY_RECHARGE_URL);
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(userIdByUrl);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }

    private void filterUnavailableSubAccount(List<Integer> userIdByUrl) {
        SysUserDataRightListVo sysUserDataRightListVo = new SysUserDataRightListVo();
        sysUserDataRightListVo.getSearch().setUserId(SessionManager.getUserId());
        sysUserDataRightListVo.getSearch().setModuleType(DataRightModuleType.COMPANYDEPOSIT.getCode());
        List<Integer> dataRightEntityIds = ServiceSiteTool.sysUserDataRightService().searchPlayerDataRightEntityId(sysUserDataRightListVo);

        for (Iterator<Integer> iterator = userIdByUrl.iterator(); iterator.hasNext(); ) {
            Integer userId = iterator.next();
            if (!dataRightEntityIds.contains(userId)) {
                iterator.remove();
            }
        }
    }

    /**
     * 网银存款　保存提交存款
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo companySaveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        if (playerRecharge.getCounterFee() == null) {
            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
        }

        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }
        boolean flag = false;//判断传回来的数据类型是否属于银行存款类型
        String rechargeType = playerRecharge.getRechargeType();
        for (String type : AppConstant.RECHARGE_TYPE) {
            if (type.equals(rechargeType)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            playerRecharge.setRechargeType(RechargeTypeEnum.ONLINE_BANK.getCode());
        }
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());

        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setRankId(rank.getId());

        return playerRechargeVo;
    }

    /**
     * 电子支付　保存存款数据
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo electronicSaveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        if (playerRecharge.getCounterFee() == null) {
            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
        }

        //存款总额（存款金额+手续费）>0才能继续执行
        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }


        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());

        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setRankId(rank.getId());
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());

        return playerRechargeVo;
    }

    /**
     * 比特币支付　保存存款数据
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo bitcoinSaveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        playerRecharge.setRechargeAmount(0d);
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());
        playerRecharge.setRechargeType(RechargeTypeEnum.BITCOIN_FAST.getCode());
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());
        return playerRechargeVo;
    }
}