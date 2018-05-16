package so.wwb.gamebox.mobile.V3.enums;

import org.soul.commons.enums.EnumTool;
import org.soul.commons.enums.ICodeEnum;
import so.wwb.gamebox.mobile.V3.helper.*;

public enum DepositChannelEnum implements ICodeEnum {
    ONLINE("online", "在线支付", OnlineDepositControllerHelper.class),
    E_BANK("company", "网银支付", CompanyDepositControllerHelper.class),//
    WECHATPAY("wechatpay", "微信支付", ScancodeDepositControllerHelper.class),//
    ALIPAY("alipay", "支付宝支付", ScancodeDepositControllerHelper.class),//
    QQWALLET("qqwallet", "QQ钱包", ScancodeDepositControllerHelper.class),//
    JDWALLET("jdwallet", "京东钱包", ScancodeDepositControllerHelper.class),//
    BDWALLET("bdwallet", "百度钱包", ScancodeDepositControllerHelper.class),//
    BITCOIN("bitcoin", "比特币", BitCoinDepositControllerHelper.class),//
    ONECODEPAY("onecodepay", "一码付", ScancodeDepositControllerHelper.class),//
    UNIONPAY("unionpay", "银联扫码", ScancodeDepositControllerHelper.class),//
    FASTRECHARGE("fastRecharge", "快充中心", null),//
    COUNTER("counter", "柜台存款", CompanyDepositControllerHelper.class),//
    EASYPAY("easypay", "易支付", ScancodeDepositControllerHelper.class),//
    OTHER("other", "其他", ScancodeDepositControllerHelper.class);

    private String code;
    private String trans;
    private Class<? extends IDepositControllerHelper> helperClazz;

    DepositChannelEnum(String code, String trans, Class<? extends IDepositControllerHelper> helperClazz) {
        this.code = code;
        this.trans = trans;
        this.helperClazz = helperClazz;
    }

    public static DepositChannelEnum enumOf(String code) {
        return EnumTool.enumOf(DepositChannelEnum.class, code);
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public Class<? extends IDepositControllerHelper> getHelperClazz() {
        return helperClazz;
    }

    public void setHelperClazz(Class<? extends IDepositControllerHelper> helperClazz) {
        this.helperClazz = helperClazz;
    }
}
