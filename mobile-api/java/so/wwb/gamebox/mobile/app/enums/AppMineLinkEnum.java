package so.wwb.gamebox.mobile.app.enums;

public enum AppMineLinkEnum {
    DEPOSIT("deposit","存款","/wallet/deposit/index.html"),
    WITH_DRAW("withDraw","取款","/wallet/withdraw/index.html"),
    TRANSFER("transfer","额度转换","/transfer/index.html"),
    RECORD("record","资金记录","/fund/record/index.html"),
    BETTING("betting","投注记录","/fund/betting/index.html"),
    MY_PROMO("myPromo","优惠记录","/promo/myPromo.html"),
    BANK_CARD("bankCard","银行卡","/bankCard/page/addCard.html"),
    BTC("btc","比特币钱包","/bankCard/page/addBtc.html"),
    GAME_NOTICE("gameNotice","申请优惠","/message/gameNotice.html?isSendMessage=true"),
    SECURITY_PASSWORD("securityPassword","修改安全密码","/passport/securityPassword/edit.html"),
    LOGIN_PASSWORD("loginPassword","修改登录密码","/my/password/editPassword.html"),
    HELP("help","常见问题","/help/firstType.html"),
    TERMS("terms","注册条款",""),
    ABOUT("about","关于我们","");

    private String code;
    private String name;
    private String link;
    AppMineLinkEnum(String code,String name,String link){
        this.code = code;
        this.name = name;
        this.link = link;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
