package so.wwb.gamebox.mobile.V3.enums;

import org.apache.taglibs.standard.lang.jstl.Coercions;
import org.soul.commons.enums.EnumTool;
import org.soul.commons.enums.ICodeEnum;
import so.wwb.gamebox.mobile.V3.handler.*;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

public enum ScanCodeTypeEnum implements ICodeEnum {
    ALIPAY("alipay", "支付宝", AlipayScanCodeHandler.class),
    WECHATPAY("wechatpay", "微信", WechatScanCodeHandler.class),
    QQWALLET("qqwallet", "QQ钱包", QqScanCodeHandler.class),
    JDWALLET("jdwallet", "京东钱包", JdScanCodeHandler.class),
    BDWALLET("bdwallet", "百度钱包", BdScanCodeHandler.class),
    UNIONPAY("unionpay", "银联扫码", UniontScanCodeHandler.class),
    ONECODEPAY("onecodepay", "一码付", OneCodePayScanCodeHandler.class),
    OTHER("other", "其他支付", OtherScanCodeHandler.class),
    EASYPAY("easypay", "易支付", EasyPayScanCodeHandler.class);
    private String code;
    private String trans;
    private Class<? extends IScanCodeControllerHandler> handlerClazz;

    ScanCodeTypeEnum(String code, String trans, Class<? extends IScanCodeControllerHandler> clazz) {
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

    public Class<? extends IScanCodeControllerHandler> getHandlerClazz() {
        return handlerClazz;
    }

    public void setHandlerClazz(Class<? extends IScanCodeControllerHandler> handlerClazz) {
        this.handlerClazz = handlerClazz;
    }
}
