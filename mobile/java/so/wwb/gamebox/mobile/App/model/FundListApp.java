package so.wwb.gamebox.mobile.App.model;

import java.util.Date;

/**
 * Created by legend on 18-1-10.
 */
public class FundListApp {

    private Integer id;
    private String createTime;
    private Double transactionMoney;

    private String transactionType;
    private String transaction_typeName;

    private String status;
    private String statusName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getTransactionMoney() {
        return transactionMoney;
    }

    public void setTransactionMoney(Double transactionMoney) {
        this.transactionMoney = transactionMoney;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransaction_typeName() {
        return transaction_typeName;
    }

    public void setTransaction_typeName(String transaction_typeName) {
        this.transaction_typeName = transaction_typeName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
