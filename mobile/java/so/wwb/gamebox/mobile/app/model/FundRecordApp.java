package so.wwb.gamebox.mobile.app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by legend on 18-1-9.
 */
public class FundRecordApp {

    private List<FundListApp> fundListApps;
    private Date maxDate; //查询最大时间

    private Date minDate; //查询最小时间

    private long totalCount; //不算分页的总记录数

    private String currency; // 钱币的符号

    private Map<String, String> transactionMap;

    private Double withdrawSum; //取款处理

    private Double transferSum;  //转账处理

    private Map<String, Object> sumPlayerMap; // 统计总和 充值额度,体现总额,优惠总额,反水总额


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


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<FundListApp> getFundListApps() {
        return fundListApps;
    }

    public void setFundListApps(List<FundListApp> fundListApps) {
        this.fundListApps = fundListApps;
    }

    public Double getWithdrawSum() {
        return withdrawSum;
    }

    public void setWithdrawSum(Double withdrawSum) {
        this.withdrawSum = withdrawSum;
    }

    public Double getTransferSum() {
        return transferSum;
    }

    public void setTransferSum(Double transferSum) {
        this.transferSum = transferSum;
    }

    public Map<String, String> getTransactionMap() {
        return transactionMap;
    }

    public void setTransactionMap(Map<String, String> transactionMap) {
        this.transactionMap = transactionMap;
    }

    public Map<String, Object> getSumPlayerMap() {
        return sumPlayerMap;
    }

    public void setSumPlayerMap(Map<String, Object> sumPlayerMap) {
        this.sumPlayerMap = sumPlayerMap;
    }
}
