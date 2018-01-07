package so.wwb.gamebox.mobile.App.model;

import java.util.List;

/**
 * Created by ed on 17-12-29.
 */
public class AppSiteApiTypeRelationI18n {
    public static final String PROP_NAME = "name";
    public static final String PROP_LOCAL = "local";
    public static final String PROP_SITE_ID = "siteId";
    public static final String PROP_API_ID = "apiId";
    public static final String PROP_API_TYPE_ID = "apiTypeId";

    private String name;
    private String local;
    private Integer siteId;
    private Integer apiId;
    private Integer apiTypeId;
    private String cover;
    private String gameLink;
    private boolean isAutoPay;

    public String getGameMsg() {
        return gameMsg;
    }

    public void setGameMsg(String gameMsg) {
        this.gameMsg = gameMsg;
    }

    private String gameMsg;

    public boolean isAutoPay() {
        return isAutoPay;
    }

    public void setAutoPay(boolean autoPay) {
        isAutoPay = autoPay;
    }

    public String getGameLink() {
        return gameLink;
    }

    public void setGameLink(String gameLink) {
        this.gameLink = gameLink;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<AppSiteGame> getGameList() {
        return gameList;
    }

    public void setGameList(List<AppSiteGame> gameList) {
        this.gameList = gameList;
    }

    private List<AppSiteGame> gameList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Integer apiTypeId) {
        this.apiTypeId = apiTypeId;
    }
}
