package so.wwb.gamebox.mobile.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的页面相关接口用户信息
 * Created by cherry on 18-2-12.
 */
public class MyUserInfo implements Serializable {
    private static final long serialVersionUID = -7265243134232776450L;
    /*是否支持免转*/
    private Boolean autoPay;
    /*是否支持比特币取款*/
    private Boolean isBit;
    /*是否支持现金取款*/
    private Boolean isCash;
    /*总资产*/
    private Double totalAssets;
    /*钱包余额*/
    private Double walletBalance;
    /*处理中取款金额*/
    private Double withdrawAmount;
    /*转账中金额*/
    private Double transferAmount;
    /*近7日收益（优惠金额）*/
    private Double preferentialAmount;
    /*用户比特币信息*/
    private Map<String, String> btc;
    /*用户银行卡信息*/
    private Map<String, String> bankcard;
    /*推荐好友的昨日收益*/
    private Double recomdAmount;
    /*用户账户*/
    private String username;
    /*用户头像地址*/
    private String avatarUrl;
    /*上一次登录时间*/
    private String lastLoginTime;
    /*此次登录时间*/
    private String loginTime;
    /*货币符号*/
    private String currency;
    /*真实姓名*/
    private String realName;
    /*用户api资金*/
    private List<Map<String, Object>> apis = new ArrayList<>(0);


    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Boolean getIsBit() {
        return isBit;
    }

    public void setIsBit(Boolean isBit) {
        this.isBit = isBit;
    }

    public Boolean getIsCash() {
        return isCash;
    }

    public void setIsCash(Boolean isCash) {
        this.isCash = isCash;
    }

    public Double getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(Double totalAssets) {
        this.totalAssets = totalAssets;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(Double withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public Double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(Double transferAmount) {
        this.transferAmount = transferAmount;
    }

    public Double getPreferentialAmount() {
        return preferentialAmount;
    }

    public void setPreferentialAmount(Double preferentialAmount) {
        this.preferentialAmount = preferentialAmount;
    }

    public Map<String, String> getBtc() {
        return btc;
    }

    public void setBtc(Map<String, String> btc) {
        this.btc = btc;
    }

    public Map<String, String> getBankcard() {
        return bankcard;
    }

    public void setBankcard(Map<String, String> bankcard) {
        this.bankcard = bankcard;
    }

    public Double getRecomdAmount() {
        return recomdAmount;
    }

    public void setRecomdAmount(Double recomdAmount) {
        this.recomdAmount = recomdAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<Map<String, Object>> getApis() {
        return apis;
    }

    public void setApis(List<Map<String, Object>> apis) {
        this.apis = apis;
    }

    public void addApi(Map<String, Object> api) {
        this.apis.add(api);
    }
}
