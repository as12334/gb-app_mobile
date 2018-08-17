package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 17-12-29.
 * 活动申请返回实体
 */
public class AppActivityApply {
    private String  searchId;//活动加密ID
    private Boolean isSatisfy;//是否成功
    private String  condition;//参与条件信息
    private Boolean  showSchedule;//是否展示进度条
    private Double  standard;//满足条件标准值
    private Double  reached;//当前值
    private String checkTime;// 订单成功时间

    public Double getStandard() {
        return this.standard;
    }

    public void setStandard(Double standard) {
        this.standard = standard;
    }

    public Double getReached() {
        return this.reached;
    }

    public void setReached(Double reached) {
        this.reached = reached;
    }

    private Boolean  mayApply;//是否展示申请按钮
    private String transactionNo;//交易订单号

    public String getSearchId() {
        return this.searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public Boolean getSatisfy() {
        return this.isSatisfy;
    }

    public void setSatisfy(Boolean satisfy) {
        this.isSatisfy = satisfy;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getShowSchedule() {
        return this.showSchedule;
    }

    public void setShowSchedule(Boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public Boolean getMayApply() {
        return this.mayApply;
    }

    public void setMayApply(Boolean mayApply) {
        this.mayApply = mayApply;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }
}
