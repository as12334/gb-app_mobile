package so.wwb.gamebox.mobile.App.model;

import java.util.List;
import java.util.Map;

/**
 * Created by legend on 17-12-31.
 */
public class UserInfoApp {
    private List<Map<String, Object>> apis;
    private String username;  //用户名
    private String currSign;  //资金币种
    private String balance;  //钱包余额
    private String assets; //总资产

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrSign() {
        return currSign;
    }

    public void setCurrSign(String currSign) {
        this.currSign = currSign;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }

    public List<Map<String, Object>> getApis() {
        return apis;
    }

    public void setApis(List<Map<String, Object>> apis) {
        this.apis = apis;
    }
}
