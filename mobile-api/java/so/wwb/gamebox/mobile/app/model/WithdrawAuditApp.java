package so.wwb.gamebox.mobile.app.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cherry on 18-6-9.
 */
public class WithdrawAuditApp implements Serializable {
    private static final long serialVersionUID = 3515000623809881386L;
    /*创建时间*/
    private Date createTime;
    /*存款费用*/
    private Double rechargeAmount = 0.00;
    /*存款稽核点*/
    private Double rechargeAudit = 0.00;
    /*存款剩余稽核点*/
    private Double rechargeRemindAudit = 0.00;
    /*存款行政费用*/
    private Double rechargeFee = 0.00;
    /*优惠金额*/
    private Double favorableAmount = 0.00;
    /*优惠稽核点*/
    private Double favorableAudit = 0.00;
    /*优惠剩余稽核点*/
    private Double favorableRemindAudit = 0.00;
    /*优惠扣除金额*/
    private Double favorableFee = 0.00;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public Double getRechargeAudit() {
        return rechargeAudit;
    }

    public void setRechargeAudit(Double rechargeAudit) {
        this.rechargeAudit = rechargeAudit;
    }

    public Double getRechargeFee() {
        return rechargeFee;
    }

    public void setRechargeFee(Double rechargeFee) {
        this.rechargeFee = rechargeFee;
    }

    public Double getFavorableAmount() {
        return favorableAmount;
    }

    public void setFavorableAmount(Double favorableAmount) {
        this.favorableAmount = favorableAmount;
    }

    public Double getFavorableAudit() {
        return favorableAudit;
    }

    public void setFavorableAudit(Double favorableAudit) {
        this.favorableAudit = favorableAudit;
    }

    public Double getFavorableFee() {
        return favorableFee;
    }

    public void setFavorableFee(Double favorableFee) {
        this.favorableFee = favorableFee;
    }

    public Double getRechargeRemindAudit() {
        return rechargeRemindAudit;
    }

    public void setRechargeRemindAudit(Double rechargeRemindAudit) {
        this.rechargeRemindAudit = rechargeRemindAudit;
    }

    public Double getFavorableRemindAudit() {
        return favorableRemindAudit;
    }

    public void setFavorableRemindAudit(Double favorableRemindAudit) {
        this.favorableRemindAudit = favorableRemindAudit;
    }
}
