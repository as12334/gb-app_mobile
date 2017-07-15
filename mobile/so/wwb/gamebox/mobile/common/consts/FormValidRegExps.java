package so.wwb.gamebox.mobile.common.consts;


import so.wwb.gamebox.model.common.RegExpConstants;

/**
 * 表单验证正则表达式规则常量
 *
 * @author Kevice
 * @time 8/5/15 4:04 PM
 */
public interface FormValidRegExps extends RegExpConstants {
    //验证金额
    String MONEY = "^(?!0+(?:\\.0+)?$)(?:[1-9]\\d*|0)(?:\\.\\d{1,2})?$";
    //验证数字
    String DIGITS = "^[0-9]*$";
    /*skeype 账号 ^[a-zA-Z][a-zA-Z0-9]{5,31}$*/
    String SKYPE = "^[a-zA-Z][A-Za-z0-9~!@#$%^&*()_+\\\\{\\\\}|\\\\:;\\'\\\"<>,./?]+{6,32}$";
    //中文或英文大小写和数字
    String CNANDEN_NUMBER = "^[0-9a-zA-Z\u4e00-\u9fa5]+$";
    /**
     * 正整数，包含（3,3.0）
     */
    String POSITIVE_INTEGER_DIGITS = "^(?:[1-9]\\d*)(?:\\.\\d{1,2})?$";
}
