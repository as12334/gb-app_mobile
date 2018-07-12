package so.wwb.gamebox.mobile.app.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * app游戏实体
 *
 * @author gavin
 *         2018.07.06
 */
public class SiteApiRelationApp implements Comparable<SiteApiRelationApp>, Serializable {

    /*实体类型 apiType:api类型实体,api:api实体,game:游戏实体,*/
    private String type;
    private String name;
    private String cover;
    private String gameLink;
    private String gameMsg;
    private Boolean autoPay;
    @JSONField(serialize = false)
    private Integer orderNum;
    /*下级列表*/
    private List<SiteApiRelationApp> relation = new ArrayList<>();


    public SiteApiRelationApp() {
    }

    public SiteApiRelationApp(String type, String name, String cover, String gameLink, String gameMsg, List<SiteApiRelationApp> relation, Boolean autoPay, Integer orderNum) {
        this.type = type;
        this.name = name;
        this.cover = cover;
        this.gameLink = gameLink;
        this.gameMsg = gameMsg;
        this.relation = relation;
        this.autoPay = autoPay;
        this.orderNum = orderNum;
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

    @Override
    public int compareTo(SiteApiRelationApp that) {
        return this.getOrderNum().compareTo(that.getOrderNum());
    }
}

