package so.wwb.gamebox.mobile.App.model;

import java.util.Date;

/**
 * Created by legend on 18-1-1.
 */
public class RecordDetailApp {
    private Integer id;
    private String transactionNo;     //交易号
    private java.util.Date createTime; //创建时间
    private String transactionType;   //资金类型
    private Double transactionMoney;   //取款金额
    private String status;          //状态
    private String failureReason;    //失败原因
    private Double administrativeFee;  //行政费用
    private Double deductFavorable;    //扣除优惠
    private String fundType;         //资金类型
    private String transactionWay;   //资金类型
    private String username;        //账号
    /**
     * 账号
     */
    private String payerBankcard;
    /**
     * 实际到账金额
     */
    private Double rechargeTotalAmount;
    /**
     * 交易金额
     */
    private Double rechargeAmount;
    /**
     * 交易地点
     */
    private String rechargeAddress;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getTransactionMoney() {
        return transactionMoney;
    }

    public void setTransactionMoney(Double transactionMoney) {
        this.transactionMoney = transactionMoney;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Double getAdministrativeFee() {
        return administrativeFee;
    }

    public void setAdministrativeFee(Double administrativeFee) {
        this.administrativeFee = administrativeFee;
    }

    public Double getDeductFavorable() {
        return deductFavorable;
    }

    public void setDeductFavorable(Double deductFavorable) {
        this.deductFavorable = deductFavorable;
    }

    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public String getTransactionWay() {
        return transactionWay;
    }

    public void setTransactionWay(String transactionWay) {
        this.transactionWay = transactionWay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPayerBankcard() {
        return payerBankcard;
    }

    public void setPayerBankcard(String payerBankcard) {
        this.payerBankcard = payerBankcard;
    }

    public Double getRechargeTotalAmount() {
        return rechargeTotalAmount;
    }

    public void setRechargeTotalAmount(Double rechargeTotalAmount) {
        this.rechargeTotalAmount = rechargeTotalAmount;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getRechargeAddress() {
        return rechargeAddress;
    }

    public void setRechargeAddress(String rechargeAddress) {
        this.rechargeAddress = rechargeAddress;
    }
}
