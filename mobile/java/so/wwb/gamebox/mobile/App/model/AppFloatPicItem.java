package so.wwb.gamebox.mobile.App.model;

/**
 * Created by ed on 17-12-31.
 */
public class AppFloatPicItem {
    private String activityId;
    private String description;
    private String normalEffect;
    private String location;
    private String language;
    /** 顶边距 */
    private Integer distanceTop;
    /** 侧边距 */
    private Integer distanceSide;

    public Integer getDistanceTop() {
        return distanceTop;
    }

    public void setDistanceTop(Integer distanceTop) {
        this.distanceTop = distanceTop;
    }

    public Integer getDistanceSide() {
        return distanceSide;
    }

    public void setDistanceSide(Integer distanceSide) {
        this.distanceSide = distanceSide;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNormalEffect() {
        return normalEffect;
    }

    public void setNormalEffect(String normalEffect) {
        this.normalEffect = normalEffect;
    }
}
