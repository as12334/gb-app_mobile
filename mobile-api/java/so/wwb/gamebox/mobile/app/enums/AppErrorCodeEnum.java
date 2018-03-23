package so.wwb.gamebox.mobile.app.enums;

public enum AppErrorCodeEnum {
    SUCCESS("0", "请求成功"),
    UN_LOGIN("1001", "您还未登录,请重新登录"),
    USER_LOCK("1005", "您的账号已被系统冻结，请联系客服处理"),
    USER_INFO_NOT_EXIST("1008", "用户不存在"),
    PASSWORD_ERROR("1011", "当前密码错误"),
    PASSWORD_SAME("1016", "新密码不能和旧密码相同"),
    SYSTEM_INFO_NOT_EXIST("1017", "信息不存在"),
    WITHDRAW_AMOUNT_ERROR("1022", "请输入正确的提现金额"),
    WITHDRAW_IS_FULL("1024", "今日提现次数已达上限"),
    ACTIVITY_END("1031", "活动已结束"),
    ACTIVITY_NOT_EXIST("1032", "不存在该活动"),
    WITHDRAW_HAS_ORDER("1100", "取款订单已存在"),
    USER_HAS_FREEZE("1101", "余额已被冻结，请联系客服处理"),
    WITHDRAW_MIN_AMOUNT("1102", "取款金额最少为x元"),
    NO_BANK("1103", "没有银行卡信息"),
    WITHDRAW_BETWEEN_MIN_MAX("1104", "取款金额应在x-x范围内，并且小于等于x"),
    WITHDRAW_FAIL("1105", "取款失败"),
    USER_ADD_BANK_CARD("1200", "用户添加银行卡"),
    SHOW_BANK_CARD_INFO("1201", "展示银行信息"),
    USER_ADD_BTC("1202", "用户添加比特币"),
    HAS_BTC("1204", "用户绑定比特币已存在"),
    USER_BINDING_BTC_SUCCESS("1205", "用户绑定比特币成功"),
    SUBMIT_BTC_FAIL("1206", "用户绑定比特币失败"),
    USER_BINDING_BANK_CARD_EXIST("1207", "用户绑定银行卡号已存在"),
    USER_BINDING_BANK_CARD_FAIL("1208", "用户绑定银行卡号失败"),
    USER_SEND_NOTICE_SITE_FAIL("1209", "用户保存申请优惠失败"),
    REAL_NAME_NOT_NULL("1300", "真实姓名不能为空"),
    VALIDATE_ERROR("1301", "验证码输入错误"),
    REAL_NAME_ERROR("1302", "真实姓名不正确"),
    ORIGIN_SAFE_PASSWORD_ERROR("1303", "原始密码有误"),
    SAFE_PASSWORD_ERROR("1303", "安全密码有误"),
    UPDATE_REAL_NAME_FAIL("1304", "真实姓名修改失败"),
    SAFE_PASSWORD_NOT_NULL("1305", "安全密码不能为空"),
    NEW_PASSWORD_NOT_NULL("1306", "新密码不能为空"),
    PASSWORD_NOT_NULL("1307", "当前密码不能为空"),
    SYSTEM_VALIDATE_NOT_NULL("1308", "验证码不能为空"),
    UPDATE_PASSWORD_FAIL("1309", "修改密码失败"),
    PARAM_HAS_ERROR("1310", "参数有误"),
    SAFE_PASSWORD_TOO_SIMPLE("1311", "密码过于简单"),
    ORIGIN_SAFE_PASSWORD_ERROR_TIME("1312","安全密码有误,还有%d次机会"),
    GAME_NOT_EXIST("1400", "游戏不存在"),
    UPDATE_STATUS_FAIL("1401", "更新失败"),
    NOT_RECOVER("1402", "非免转不能一键回收"),
    SYSTEM_READ("1403", "消息已读"),
    NOT_SET_SAFE_PASSWORD("1404", "未设置安全密码"),
    REGISTER_FAIL("1500","注册失败，请联系客服！"),
    NOT_ALLOW_REGISTER("1501","禁止注册，请联系客服！"),
    REGISTER_SUCCESS("1502","注册成功"),
    REGISTER_USER_EXIST("1503","用户已存在"),
    REGISTER_REAL_NAME_EXIST("1504","真实姓名已存在"),
    REGISTER_QQ_EXIST("1505","QQ账号已被注册"),
    REGISTER_PHONE_EXIST("1506","手机号码已被注册"),
    REGISTER_EMAIL_EXIST("1507","邮箱已被注册"),
    REGISTER_WEIXIN_EXIST("1508","微信已被注册"),
    TRANSFER_ERROR("1520","额度转换失败"),
    ;
    public static final int SUCCESS_CODE = 0;
    public static final int FAIL_COED = 1;
    private String code;
    private String msg;

    AppErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
