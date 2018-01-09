package so.wwb.gamebox.mobile.App.model;

import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;

import java.util.Date;
import java.util.List;

/**
 * Created by legend on 18-1-9.
 */
public class FundRecordApp {

    private List<VPlayerTransaction> vPlayerTransactionList; //资金记录列表数据

    private Date maxDate; //查询最大时间

    private Date minDate; //查询最小时间

    private int totalCount; //不算分页的总记录数

    private String currency; // 钱币的符号

    private String transaction_type; // 资金类型

    private Double withdrawSum;

    public List<VPlayerTransaction> getvPlayerTransactionList() {
        return vPlayerTransactionList;
    }

    public void setvPlayerTransactionList(List<VPlayerTransaction> vPlayerTransactionList) {
        this.vPlayerTransactionList = vPlayerTransactionList;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
}
