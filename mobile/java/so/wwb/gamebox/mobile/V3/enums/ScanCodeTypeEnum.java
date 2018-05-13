package so.wwb.gamebox.mobile.V3.enums;

import org.apache.taglibs.standard.lang.jstl.Coercions;
import org.soul.commons.enums.ICodeEnum;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;

public enum ScanCodeTypeEnum implements ICodeEnum {
    ALIPAY("alipay", "支付宝", new String[]{PayAccountAccountType.ALIPAY.getCode(), PayAccountAccountType.ALIPAY_MICROPAY.getCode()}),
    WECHATPAY("wechatpay", "微信", null),
    QQWALLET("qqwallet", "QQ钱包", null),
    JDWALLET("jdwallet", "京东钱包", null),
    BDWALLET("bdwallet", "百度钱包", null),
    UNIONPAY("unionpay", "银联扫码", null);
    private String code;
    private String trans;
    private String[] payAccountTypes;

    ScanCodeTypeEnum(String code, String trans, String[] payAccountTypes) {
        this.code = code;
        this.trans = trans;
        this.payAccountTypes = payAccountTypes;
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

    public String[] getPayAccountTypes() {
        return payAccountTypes;
    }

    public void setPayAccountTypes(String[] payAccountTypes) {
        this.payAccountTypes = payAccountTypes;
    }
}
