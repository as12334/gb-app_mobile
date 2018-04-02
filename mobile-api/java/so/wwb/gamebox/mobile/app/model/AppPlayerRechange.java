package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by ed on 17-12-29.
 */
public class AppPlayerRechange {
    /** id */
    private Integer id;
    /**  */
    private Double randomCash;
    /**  */
    private Double rechargeAmount;
    /** DepositWayEunm */
    private String rechargeType;
    /** 玩家id */
    private Integer userPlayerId;
    /** 存款渠道 */
    private AppPayAccount appPayAccount;
    /** 回执时间 */
    private java.util.Date returnTime;
    /*比特币金额*/
    private Double bitAmount;
    /** 银行订单号 */
    private String bankOrder;
    /** 付款者的银行卡号，如果是AMT 或网银转帐则记录订单号 点卡充值 则记录点卡号 */
    private String payerBankcard;
    /** 付款者名 */
    private String payerName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Integer getUserPlayerId() {
        return userPlayerId;
    }

    public void setUserPlayerId(Integer userPlayerId) {
        this.userPlayerId = userPlayerId;
    }

    public AppPayAccount getAppPayAccount() {
        return appPayAccount;
    }

    public void setAppPayAccount(AppPayAccount appPayAccount) {
        this.appPayAccount = appPayAccount;
    }

    public Double getRandomCash() {
        return randomCash;
    }

    public void setRandomCash(Double randomCash) {
        this.randomCash = randomCash;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public Double getBitAmount() {
        return bitAmount;
    }

    public void setBitAmount(Double bitAmount) {
        this.bitAmount = bitAmount;
    }

    public String getBankOrder() {
        return bankOrder;
    }

    public void setBankOrder(String bankOrder) {
        this.bankOrder = bankOrder;
    }

    public String getPayerBankcard() {
        return payerBankcard;
    }

    public void setPayerBankcard(String payerBankcard) {
        this.payerBankcard = payerBankcard;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
}
