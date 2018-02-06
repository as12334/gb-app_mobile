package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by legend on 18-1-1.
 */
public class RecordDetailApp {
    private Integer id;
    private String transactionNo;     //交易号
    private java.util.Date createTime; //创建时间
    private String transactionType;   //资金类型
    private Double transactionMoney;
    private String withdrawMoney; //取款金额
    private String status;          //状态
    private String statusName; //状态名称

    private String failureReason;    //失败原因
    private String administrativeFee;  //行政费用
    private String deductFavorable;    //扣除优惠
    private String fundType;         //资金类型
    private String transactionWay;   //资金类型
    private String transactionWayName; // 资金类型名称
    private String username;        //账号
    private String poundage;  //手续费
    private String poundageName; // 是否免手续费（中文汉字）
    private String realName; //真实姓名

    private String transferOut; // 转出
    private String transferInto; //转入
    private String bankCode; //取款 银行code
    private String bankCodeName; // 银行名称

    private String bankUrl; //银行的图片的链接地址

    private String withDrwalsRemark; // 取款 和优惠描述

    private String bitAmount; //比特币
    /**
     * 账号
     */
    private String payerBankcard;
    /**
     * 实际到账金额
     */
    private String rechargeTotalAmount;
    /**
     * 交易金额
     */
    private String rechargeAmount;
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

    public String getAdministrativeFee() {
        return administrativeFee;
    }

    public void setAdministrativeFee(String administrativeFee) {
        this.administrativeFee = administrativeFee;
    }

    public String getDeductFavorable() {
        return deductFavorable;
    }

    public void setDeductFavorable(String deductFavorable) {
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

    public String getRechargeTotalAmount() {
        return rechargeTotalAmount;
    }

    public void setRechargeTotalAmount(String rechargeTotalAmount) {
        this.rechargeTotalAmount = rechargeTotalAmount;
    }

    public String getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getRechargeAddress() {
        return rechargeAddress;
    }

    public void setRechargeAddress(String rechargeAddress) {
        this.rechargeAddress = rechargeAddress;
    }

    public String getTransactionWayName() {
        return transactionWayName;
    }

    public void setTransactionWayName(String transactionWayName) {
        this.transactionWayName = transactionWayName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage;
    }

    public String getTransferOut() {
        return transferOut;
    }

    public void setTransferOut(String transferOut) {
        this.transferOut = transferOut;
    }

    public String getTransferInto() {
        return transferInto;
    }

    public void setTransferInto(String transferInto) {
        this.transferInto = transferInto;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBitAmount() {
        return bitAmount;
    }

    public void setBitAmount(String bitAmount) {
        this.bitAmount = bitAmount;
    }

    public String getPoundageName() {
        return poundageName;
    }

    public void setPoundageName(String poundageName) {
        this.poundageName = poundageName;
    }

    public String getBankCodeName() {
        return bankCodeName;
    }

    public void setBankCodeName(String bankCodeName) {
        this.bankCodeName = bankCodeName;
    }

    public String getWithDrwalsRemark() {
        return withDrwalsRemark;
    }

    public void setWithDrwalsRemark(String withDrwalsRemark) {
        this.withDrwalsRemark = withDrwalsRemark;
    }

    public String getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(String withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public String getBankUrl() {
        return bankUrl;
    }

    public void setBankUrl(String bankUrl) {
        this.bankUrl = bankUrl;
    }
}
