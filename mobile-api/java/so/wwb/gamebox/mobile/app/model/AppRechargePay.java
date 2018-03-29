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
    private double[] quickMoneys;
    /**是否展示多个收款账号*/
    private boolean isMultipleAccount;
    /**是否影藏收款账号*/
    private boolean isHide;
    /**上一次填写的账号/昵称*/
    private String payerBankcard;

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

    public double[] getQuickMoneys() {
        return quickMoneys;
    }

    public void setQuickMoneys(double[] quickMoneys) {
        this.quickMoneys = quickMoneys;
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
}

