package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.model.AppPayAccount;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.enums.RankFeeType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeListVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.cache.Cache;

import java.util.*;

/**
 * Created by ed on 17-12-31.
 */
public class BaseDepositController {
    private Log LOG = LogFactory.getLog(BaseOriginController.class);
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
        appPayAccount.setBankCode(payAccount.getBankCode());
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
            appPayAccount.setRechargeType(payAccount.getRechargeType());
            electronicPay(appPayAccount);
            companyBankName(appPayAccount);
        }
        return appPayAccount;
    }

    /**
     * 电子支付别名设置多个收款账号
     *
     * @param
     * @return
     */
    public void getCompanyPayAccounts(List<AppPayAccount> AppPayAccountList,PayAccount payAccount, String rechargeType) {
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.FUND_RECHARGE_TYPE);
        String bankName = i18n.get(rechargeType);
        if (isMultipleAccount()) {
            int size = AppPayAccountList.size();
            int count = 1;
            String other = RechargeTypeEnum.OTHER_FAST.getCode();
            if (StringTool.isBlank(payAccount.getAliasName())) {
                if (other.equals(rechargeType)) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                } else if (size > 1) {
                    payAccount.setAliasName(bankName + count);
                    count++;
                } else {
                    payAccount.setAliasName(bankName);
                }
            }
        }else{
            payAccount.setAliasName(i18n.get(rechargeType));
        }
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

    //公司入款快选金额
    public void quickSelection(Map<String, Object> payAccountMap) {
        double[] quickMoneys = new double[]{100, 300, 500, 1000, 3000};
        payAccountMap.put("quickMoneys", quickMoneys);
    }

    //是否展示多个账号
    public boolean isMultipleAccount() {
        PlayerRank rank = getRank();
        boolean isMultipleAccount = rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        return isMultipleAccount;
    }

    public void fastRecharge(Map<String, Object> payAccountMap) {
        String url = getFastRechargeUrl();
        if (StringTool.isNotBlank(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
            payAccountMap.put(AppConstant.IS_FAST_RECHARGE, url);
        }

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

    public void companyBankName(AppPayAccount appPayAccount) {
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        String bankCode = appPayAccount.getBankCode();
        if (StringTool.equals(BankCodeEnum.OTHER_BANK.getCode(), bankCode)) {
            appPayAccount.setBankName(appPayAccount.getCustomBankName());
        } else {
            appPayAccount.setBankName(i18n.get(bankCode));
        }
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
    public List<VActivityMessage> searchSaleByAmount(Double rechargeAmount, String type,UserPlayer userPlayer) {
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

    public UserPlayer getUserPlayerById(Integer userPlayerId){
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        if(userPlayerId == null){
            return null;
        }
        userPlayerVo.getSearch().setId(userPlayerId);
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult();
    }
}