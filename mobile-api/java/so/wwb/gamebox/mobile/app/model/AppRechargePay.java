package so.wwb.gamebox.mobile.app.model;

import java.util.List;

public class AppRechargePay {
    /**货币符号*/
    private String currency;
    /**在线客服路径*/
    private String customerService;
    /**存款渠道集合*/
    private List<AppPayAccount> arrayList;
    /**快选金额*/
    private int[] quickMoneys;
    /**是否展示多个收款账号*/
    private boolean isMultipleAccount;
    /**是否影藏收款账号*/
    private boolean isHide;
    /**上一次填写的账号/昵称*/
    private String payerBankcard;
    /**添加柜台机存款类型list*/
    private List<AppSimpleModel> counterRechargeTypes;
    /**是否是新活动*/
    private boolean isNewActivity;

    public boolean isNewActivity() {
        return isNewActivity;
    }

    public void setNewActivity(boolean newActivity) {
        isNewActivity = newActivity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCustomerService() {
        return customerService;
    }

    public void setCustomerService(String customerService) {
        this.customerService = customerService;
    }

    public List<AppPayAccount> getArrayList() {
        return arrayList;
    }

    public void setArrayList(List<AppPayAccount> arrayList) {
        this.arrayList = arrayList;
    }

    public boolean isMultipleAccount() {
        return isMultipleAccount;
    }

    public void setMultipleAccount(boolean multipleAccount) {
        isMultipleAccount = multipleAccount;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public String getPayerBankcard() {
        return payerBankcard;
    }

    public void setPayerBankcard(String payerBankcard) {
        this.payerBankcard = payerBankcard;
    }

    public int[] getQuickMoneys() {
        return quickMoneys;
    }

    public void setQuickMoneys(int[] quickMoneys) {
        this.quickMoneys = quickMoneys;
    }

    public List<AppSimpleModel> getCounterRechargeTypes() {
        return counterRechargeTypes;
    }

    public void setCounterRechargeTypes(List<AppSimpleModel> counterRechargeTypes) {
        this.counterRechargeTypes = counterRechargeTypes;
    }
}

