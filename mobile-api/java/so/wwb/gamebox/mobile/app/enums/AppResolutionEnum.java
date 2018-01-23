package so.wwb.gamebox.mobile.app.enums;

import org.soul.commons.enums.ICodeEnum;

public enum AppResolutionEnum implements ICodeEnum {
    XX_HDPI("xxhdpi", "安卓"),
    XXX_HDPI("xxxhdpi", "安卓"),
    TWO_X("2x", "ios"),
    THREE_X("3x", "ios");

    private String code;
    private String trans;
    AppResolutionEnum(String code, String trans) {
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
