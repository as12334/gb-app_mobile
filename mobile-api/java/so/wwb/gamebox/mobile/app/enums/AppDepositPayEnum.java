package so.wwb.gamebox.mobile.app.enums;

import org.soul.commons.enums.ICodeEnum;

public enum AppDepositPayEnum implements ICodeEnum {
    //存款入口

    ONLINE("online", "在线支付"),
    COMPANY("company", "网银存款"),
    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝支付"),
    QQ("qq", "QQ钱包"),
    JD("jd", "京东钱包"),
    BD("bd", "百度钱包"),
    BITCONIT("bitcoin", "BitConit"),
    ONECODEPAY("onecodepay", "一码付"),
    UNIONPAY("unionpay", "银联扫码"),
    COUNTER("counter", "柜员机"),
    EASYPAY("easy", "易收付"),
    OTHER("other", "其他"),
    IS_FASTRECHARGE("isfastrecharge","快充中心"),
        //支付类型
    ONLINE_PAY("online_pay","线上支付"),
    SCAN_PAY("scan_pay","扫码支付"),
    COMPANY_PAY("company_pay","网银支付"),
    ELECTRONIC_PAY("electronic_pay","电子支付"),
    BITCOIN_PAY("bitcoin_pay","比特币支付"),

    //渠道Name
    WECHAT_MICROPAY("wechat_micropay", "微信反扫通道"),
    ALIPAY_MICROPAY("alipay_micropay", "支付宝反扫通道"),
    QQ_MICROPAY("qq_micropay", "QQ反扫通道"),
    EASY_PAY("easy_pay","易收付存款通道"),
    WECHAT_SCAN("wechat_scan", "微信扫码通道"),
    ALIPAY_SCAN("alipay_scan", "支付宝扫码通道"),
    JD_SCAN("jd_scan", "京东钱包扫码通道"),
    BD_SCAN("bd_scan", "百度钱包扫码通道"),
    QQ_SCAN("qq_scan", "QQ钱包扫码通道"),
    WECHATPAY_FAST("wechatpay_fast", "微信电子支付"),
    ALIPAY_FAST("alipay_fast", "支付宝电子支付"),
    OTHER_FAST("other_fast", "其他电子支付"),
    BITCOIN_FAST("bitcoin_fast", "比特币支付"),
    QQWALLET_FAST("qqwallet_fast", "QQ钱包电子支付"),
    JDWALLET_FAST("jdwallet_fast", "京东钱包电子支付"),
    BDWALLET_FAST("bdwallet_fast", "百度钱包电子支付"),
    ONECODEPAY_FAST("onecodepay_fast", "一码付电子支付"),

    //柜员机和网银类型
    ONLINE_BANK("online_bank", "网银存款"),
    ATM_MONEY("atm_money", "柜员机现金存款"),
    ATM_RECHARGE("atm_recharge", "柜员机转账"),
    ATM_COUNTER("atm_counter", "银行柜台存款")
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
