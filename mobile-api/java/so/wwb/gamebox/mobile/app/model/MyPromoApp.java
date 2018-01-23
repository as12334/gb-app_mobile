package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by legend on 18-1-12.
 */
public class MyPromoApp {

    private Integer id; // 主键

    private Date applyTime;  //时间

    private Double preferentialAudit;  //是否稽核

    private String preferentialAuditName; //是否稽核的名称

    private String activityName; //优惠名称

    private Double preferentialValue; // 优惠金额

    private Integer userId; // 用户的ID

    private String checkState; //状态

    private String checkStateName; // 状态名称

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Double getPreferentialAudit() {
        return preferentialAudit;
    }

    public void setPreferentialAudit(Double preferentialAudit) {
        this.preferentialAudit = preferentialAudit;
    }

    public String getPreferentialAuditName() {
        return preferentialAuditName;
    }

    public void setPreferentialAuditName(String preferentialAuditName) {
        this.preferentialAuditName = preferentialAuditName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Double getPreferentialValue() {
        return preferentialValue;
    }

    public void setPreferentialValue(Double preferentialValue) {
        this.preferentialValue = preferentialValue;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getCheckStateName() {
        return checkStateName;
    }

    public void setCheckStateName(String checkStateName) {
        this.checkStateName = checkStateName;
    }
}
