package so.wwb.gamebox.mobile.app.model;

/**
 * Created by legend on 18-1-15.
 */
public class ActivityTypeListApp {

    private String photo; //活动对应的图片
    private String url; // 对应的具体的详情 h5链接
    private int id;
    private String name;
    private String status;
    private Integer orderNum;
    private String time; //活动时间
    private String searchId;//加密活动ID

    public String getName() {
        return name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }


    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

}
