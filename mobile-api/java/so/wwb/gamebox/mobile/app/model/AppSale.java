package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 17-12-29.
 */
public class AppSale {
    /** 优惠ｉｄ */
    private Integer id;
    /** 优惠 */
    private boolean preferential;
    /** 活动名称 */
    private String activityName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isPreferential() {
        return preferential;
    }

    public void setPreferential(boolean preferential) {
        this.preferential = preferential;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
