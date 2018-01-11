package so.wwb.gamebox.mobile.app.model;

import java.util.List;

/**
 * Created by ed on 17-12-27.
 */
public class AppSiteApiTypeRelastionVo {
    private Integer apiType;
    private List<AppSiteApiTypeRelationI18n> siteApis;
    private String apiTypeName;
    private String locale;
    private String cover;
    private boolean level;

    public boolean isLevel() {
        return level;
    }

    public void setLevel(boolean level) {
        this.level = level;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getApiTypeName() {
        return apiTypeName;
    }

    public void setApiTypeName(String apiTypeName) {
        this.apiTypeName = apiTypeName;
    }

    public Integer getApiType() {
        return apiType;
    }

    public void setApiType(Integer apiType) {
        this.apiType = apiType;
    }

    public List<AppSiteApiTypeRelationI18n> getSiteApis() {
        return siteApis;
    }

    public void setSiteApis(List<AppSiteApiTypeRelationI18n> siteApis) {
        this.siteApis = siteApis;
    }
}
