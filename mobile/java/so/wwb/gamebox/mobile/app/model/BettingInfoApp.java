package so.wwb.gamebox.mobile.app.model;

import java.util.Date;

/**
 * Created by legend on 18-1-3.
 */
public class BettingInfoApp {


    private Integer id;
    private Integer apiId;
    private Integer gameId;
    private String terminal;
    private String apiName;
    private String gameName;

    private String url; // 投注记录详情的h5链接,不用再请求详情接口
    /**
     * 盈亏结果金额（派彩）
     */
    private Double profitAmount;

    /**
     * 投注时间
     */
    private java.util.Date betTime;
    /**
     * 下单金额 (交易量)
     */
    private Double singleAmount;
    /**
     * 下单状态：未结算、已结算、订单取消 字典：game order_state
     */
    private String orderState;
    /**
     * action_id_json，格式为action_id:profitAmount
     */
    private String actionIdJson;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }


    public Date getBetTime() {
        return betTime;
    }

    public void setBetTime(Date betTime) {
        this.betTime = betTime;
    }

    public Double getSingleAmount() {
        return singleAmount;
    }

    public void setSingleAmount(Double singleAmount) {
        this.singleAmount = singleAmount;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getActionIdJson() {
        return actionIdJson;
    }

    public void setActionIdJson(String actionIdJson) {
        this.actionIdJson = actionIdJson;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(Double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
