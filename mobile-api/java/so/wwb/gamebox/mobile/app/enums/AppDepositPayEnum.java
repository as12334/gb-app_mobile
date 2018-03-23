package so.wwb.gamebox.mobile.app.enums;

import org.soul.commons.enums.ICodeEnum;

public enum AppDepositPayEnum implements ICodeEnum {
    ONLINE("online", "在线支付"),
    COMPANY("company", "网银存款"),
    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝支付"),
    QQWALLET("qqWallet", "QQ钱包"),
    JDPAY("jdPay", "京东钱包"),
    BAIFUPAY("baifuPay", "百度钱包"),
    BITCONIT("bitcoin", "BitConit"),
    ONECODEPAY("oneCodePay", "一码付"),
    UNIONPAY("unionPay", "银联扫码"),
    COUNTER("counter", "柜员机"),
    EASYPAY("easyPay", "易收付"),
    OTHER("other", "其他"),
    ;

    private String code;
    private String trans;
    AppDepositPayEnum(String code, String trans) {
        this.code = code;
        this.trans = trans;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTrans() {
        return trans;
    }
}
