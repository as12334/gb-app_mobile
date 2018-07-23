package so.wwb.gamebox.mobile.app.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * app游戏实体
 *
 * @author gavin
 * 2018.07.06
 */
public class SiteApiRelationApp implements Comparable<SiteApiRelationApp>, Serializable {


    private Integer gameId;
    private Integer apiId;
    private Integer apiTypeId;
    /*实体类型 apiType:api类型实体,api:api实体,game:游戏实体,*/
    private String type;
    private String name;
    private String cover;
    private String gameLink;
    private String gameMsg;
    private Boolean autoPay;
    @JSONField(serialize = false)
    private Integer orderNum;
    @JSONField(serialize = false)
    private Boolean ownIcon;
    @JSONField(serialize = false)
    private String code;
    /*下级列表*/
    private List<SiteApiRelationApp> relation = new ArrayList<>();


    public SiteApiRelationApp() {
    }

    public SiteApiRelationApp(Integer gameId, Integer apiId, Integer apiTypeId, String type, String name, String cover, String gameLink, String gameMsg, Boolean autoPay, Integer orderNum, Boolean ownIcon, List<SiteApiRelationApp> relation) {
        this.gameId = gameId;
        this.apiId = apiId;
        this.apiTypeId = apiTypeId;
        this.type = type;
        this.name = name;
        this.cover = cover;
        this.gameLink = gameLink;
        this.gameMsg = gameMsg;
        this.autoPay = autoPay;
        this.orderNum = orderNum;
        this.ownIcon = ownIcon;
        this.relation = relation;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getGameLink() {
        return gameLink;
    }

    public void setGameLink(String gameLink) {
        this.gameLink = gameLink;
    }

    public String getGameMsg() {
        return gameMsg;
    }

    public void setGameMsg(String gameMsg) {
        this.gameMsg = gameMsg;
    }

    public List<SiteApiRelationApp> getRelation() {
        return relation;
    }

    public void setRelation(List<SiteApiRelationApp> relation) {
        this.relation = relation;
    }

    public Boolean getAutoPay() {
        return autoPay;
    }

    public void setAutoPay(Boolean autoPay) {
        this.autoPay = autoPay;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Boolean getOwnIcon() {
        return ownIcon;
    }

    public void setOwnIcon(Boolean ownIcon) {
        this.ownIcon = ownIcon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int compareTo(SiteApiRelationApp that) {
        int thatOrderNum = that.getOrderNum() == null ? Integer.MAX_VALUE : that.getOrderNum();
        int thisOrderNum = this.getOrderNum() == null ? Integer.MAX_VALUE : this.getOrderNum();
        return (thisOrderNum < thatOrderNum) ? -1 : ((thisOrderNum == thatOrderNum) ? 0 : 1);

    }
}

