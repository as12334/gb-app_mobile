package so.wwb.gamebox.mobile.app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by legend on 18-1-4.
 */
public class BettingDetailsApp {

    private String userName;  //玩家账号
    private String terminal;   //终端标示
    private String apiName;
    private String gameName;
    private String gameId;

    private String gameType; // 游戏类型
    private String betTypeName; //赌注项目
    private String oddsTypeName; //盘口类型
    /**
     * 注单号码
     */
    private String betId;
    /**
     * api
     */
    private Integer apiId;
    /**
     * api游戏类型
     */
    private Integer apiTypeId;
    /**
     * 投注时间
     */
    private java.util.Date betTime;
    /**
     * 下单金额 (交易量) 投注额
     */
    private Double singleAmount;
    /**
     * 下单状态：未结算、已结算、订单取消 字典：game order_state
     */
    private String orderState;
    /**
     * 有效交易量  有效投注额
     */
    private Double effectiveTradeAmount;
    /**
     * 派彩时间
     */
    private java.util.Date payoutTime;
    /**
     * 盈亏结果金额（派彩）
     */
    private Double profitAmount;
    /**
     * 彩池贡献金
     */
    private Double contributionAmount;
    /**
     * 注单详情
     */
    private String betDetail;
    //详情结果
    private List<Map> resultArray;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getBetId() {
        return betId;
    }

    public void setBetId(String betId) {
        this.betId = betId;
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

    public Double getEffectiveTradeAmount() {
        return effectiveTradeAmount;
    }

    public void setEffectiveTradeAmount(Double effectiveTradeAmount) {
        this.effectiveTradeAmount = effectiveTradeAmount;
    }

    public Date getPayoutTime() {
        return payoutTime;
    }

    public void setPayoutTime(Date payoutTime) {
        this.payoutTime = payoutTime;
    }

    public Double getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(Double profitAmount) {
        this.profitAmount = profitAmount;
    }

    public Double getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(Double contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public String getBetDetail() {
        return betDetail;
    }

    public void setBetDetail(String betDetail) {
        this.betDetail = betDetail;
    }

    public List<Map> getResultArray() {
        return resultArray;
    }

    public void setResultArray(List<Map> resultArray) {
        this.resultArray = resultArray;
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

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getBetTypeName() {
        return betTypeName;
    }

    public void setBetTypeName(String betTypeName) {
        this.betTypeName = betTypeName;
    }

    public String getOddsTypeName() {
        return oddsTypeName;
    }

    public void setOddsTypeName(String oddsTypeName) {
        this.oddsTypeName = oddsTypeName;
    }
}
