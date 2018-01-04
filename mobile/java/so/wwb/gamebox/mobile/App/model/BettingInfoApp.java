package so.wwb.gamebox.mobile.App.model;

import java.util.Date;
import java.util.Map;

/**
 * Created by legend on 18-1-3.
 */
public class BettingInfoApp {


    private Integer id;
    private Integer apiId;
    private Integer gameId;
    private String terminal;

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
}
