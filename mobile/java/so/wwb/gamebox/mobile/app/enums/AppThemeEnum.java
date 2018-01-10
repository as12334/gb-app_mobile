package so.wwb.gamebox.mobile.app.enums;

import org.soul.commons.enums.ICodeEnum;

public enum AppThemeEnum implements ICodeEnum {
    WHITE("white", "白色"),
    BLACK("black", "黑色"),
    BLUE("blue", "蓝色");

    AppThemeEnum(String code, String trans) {
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
