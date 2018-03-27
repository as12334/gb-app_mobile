package so.wwb.gamebox.mobile.app.model;

import java.util.List;
import java.util.Map;

public class DepositPayApp {
    private String code;
    private String name;

    private List<AppPayAccount> payAccounts;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<AppPayAccount> getPayAccounts() {
        return payAccounts;
    }

    public void setPayAccounts(List<AppPayAccount> payAccounts) {
        this.payAccounts = payAccounts;
    }

}

