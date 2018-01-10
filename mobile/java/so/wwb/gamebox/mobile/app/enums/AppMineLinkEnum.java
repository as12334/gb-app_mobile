package so.wwb.gamebox.mobile.app.enums;

public enum AppMineLinkEnum {
    deposit("deposit","存款","/wallet/deposit/index.html"),
    withDraw("withDraw","取款","/wallet/withdraw/index.html"),
    transfer("transfer","额度转换","/transfer/index.html"),
    record("record","资金记录","/fund/record/index.html"),
    betting("betting","投注记录","/fund/betting/index.html"),
    myPromo("myPromo","优惠记录","/promo/myPromo.html"),
    bankCard("bankCard","银行卡","/bankCard/page/addCard.html"),
    btc("btc","比特币钱包","/bankCard/page/addBtc.html"),
    gameNotice("gameNotice","申请优惠","/message/gameNotice.html?isSendMessage=true"),
    securityPassword("securityPassword","修改安全密码","/passport/securityPassword/edit.html"),
    loginPassword("loginPassword","修改登录密码","/my/password/editPassword.html"),
    help("help","常见问题","/help/firstType.html"),
    terms("terms","注册条款",""),
    about("about","关于我们","");

    AppMineLinkEnum(String code,String name,String link){
        this.code = code;
        this.name = name;
        this.link = link;
    }
    private String code;
    private String name;
    private String link;

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
