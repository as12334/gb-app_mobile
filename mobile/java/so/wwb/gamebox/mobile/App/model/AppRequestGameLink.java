package so.wwb.gamebox.mobile.App.model;

/**
 * Created by ed on 18-1-9.
 */
public class AppRequestGameLink {
    private Integer gameId;
    private Integer apiId;
    private String gameCode;
    private String apiTypeId;

    public String getApiTypeId() {
        return apiTypeId;
    }

    public void setApiTypeId(String apiTypeId) {
        this.apiTypeId = apiTypeId;
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

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }
}
