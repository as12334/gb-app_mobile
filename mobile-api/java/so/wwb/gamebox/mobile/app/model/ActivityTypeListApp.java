package so.wwb.gamebox.mobile.app.model;

/**
 * Created by legend on 18-1-15.
 */
public class ActivityTypeListApp {

    private String photo; //活动对应的图片

    private String url; // 对应的具体的详情 h5链接
    private int id;
    private String name;

    public String getName() {
        return name;
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


}