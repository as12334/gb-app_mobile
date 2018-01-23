package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 17-12-29.
 */
public class AppSiteGame {
    public static final String PROP_GAME_ID = "gameId";
    public static final String PROP_SITE_ID = "siteId";
    public static final String PROP_API_ID = "apiId";
    public static final String PROP_GAME_TYPE = "gameType";
    public static final String PROP_ORDER_NUM = "orderNum";
    public static final String PROP_STATUS = "status";
    public static final String PROP_API_TYPE_ID = "apiTypeId";
    public static final String PROP_CODE = "code";
    public static final String PROP_NAME = "name";
    public static final String PROP_COVER = "cover";

    private Integer gameId;
    private Integer siteId;
    private Integer apiId;
    private String gameType;
    private Integer orderNum;
    private String status;
    private Integer apiTypeId;
    private String code;
    private String name;
    private String cover;
    private String systemStatus;
    private String gameLink;
    private String gameMsg;
    private boolean isAutoPay;

    public String getGameMsg() {
        return gameMsg;
    }

    public void setGameMsg(String gameMsg) {
        this.gameMsg = gameMsg;
    }

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

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
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

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(Integer apiTypeId) {
        this.apiTypeId = apiTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
}
