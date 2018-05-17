package so.wwb.gamebox.mobile.V3.enums;

import org.soul.commons.enums.EnumTool;
import org.soul.commons.enums.ICodeEnum;
import so.wwb.gamebox.mobile.V3.support.builder.*;

public enum ScanCodeTypeEnum implements ICodeEnum {
    ALIPAY("alipay", "支付宝", AlipayScanCodeBuilder.class),
    WECHATPAY("wechatpay", "微信", WechatScanCodeBuilder.class),
    QQWALLET("qqwallet", "QQ钱包", QqScanCodeBuilder.class),
    JDWALLET("jdwallet", "京东钱包", JdScanCodeBuilder.class),
    BDWALLET("bdwallet", "百度钱包", BdScanCodeBuilder.class),
    UNIONPAY("unionpay", "银联扫码", UniontScanCodeBuilder.class),
    ONECODEPAY("onecodepay", "一码付", OneCodePayScanCodeBuilder.class),
    OTHER("other", "其他支付", OtherScanCodeBuilder.class),
    EASYPAY("easypay", "易支付", EasyPayScanCodeBuilder.class);
    private String code;
    private String trans;
    private Class<? extends IScanCodeControllerBuilder> handlerClazz;

    ScanCodeTypeEnum(String code, String trans, Class<? extends IScanCodeControllerBuilder> clazz) {
        this.code = code;
        this.trans = trans;
        this.handlerClazz = clazz;
    }

    public static ScanCodeTypeEnum enumOf(String code) {
        return EnumTool.enumOf(ScanCodeTypeEnum.class, code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Class<? extends IScanCodeControllerBuilder> getHandlerClazz() {
        return handlerClazz;
    }

    public void setHandlerClazz(Class<? extends IScanCodeControllerBuilder> handlerClazz) {
        this.handlerClazz = handlerClazz;
    }
}
