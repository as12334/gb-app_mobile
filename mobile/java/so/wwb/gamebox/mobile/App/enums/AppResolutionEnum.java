package so.wwb.gamebox.mobile.App.enums;

import org.soul.commons.enums.ICodeEnum;

public enum AppResolutionEnum implements ICodeEnum {
    XXHDPI("xxhdpi", "安卓"),
    XXXHDPI("xxxhdpi", "安卓"),
    TWOX("2x", "ios"),
    THREEX("3x", "ios");

    AppResolutionEnum(String code, String trans) {
        this.code = code;
        this.trans = trans;
    }

    private String code;
    private String trans;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTrans() {
        return trans;
    }
}
