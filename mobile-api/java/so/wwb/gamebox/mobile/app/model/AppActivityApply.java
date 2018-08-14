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
    private Integer  standard;//满足条件标准值
    private Integer  reached;//当前值
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

    public Integer getStandard() {
        return this.standard;
    }

    public void setStandard(Integer standard) {
        this.standard = standard;
    }

    public Integer getReached() {
        return this.reached;
    }

    public void setReached(Integer reached) {
        this.reached = reached;
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
}
