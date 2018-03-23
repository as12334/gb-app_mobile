package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 17-12-29.
 */
public class AppPlayerRechange {
    /** id */
    private Integer id;
    /**  */
    private Double randomCash;
    /**  */
    private Double rechargeAmount;
    /** DepositWayEunm */
    private String rechargeType;
    /** 玩家id */
    private Integer userPlayerId;
    /** 存款渠道 */
    private AppPayAccount appPayAccount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Integer getUserPlayerId() {
        return userPlayerId;
    }

    public void setUserPlayerId(Integer userPlayerId) {
        this.userPlayerId = userPlayerId;
    }

    public AppPayAccount getAppPayAccount() {
        return appPayAccount;
    }

    public void setAppPayAccount(AppPayAccount appPayAccount) {
        this.appPayAccount = appPayAccount;
    }

    public Double getRandomCash() {
        return randomCash;
    }

    public void setRandomCash(Double randomCash) {
        this.randomCash = randomCash;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }
}
