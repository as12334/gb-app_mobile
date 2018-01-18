package so.wwb.gamebox.mobile.app.enums;

public enum AppErrorCodeEnum{
    SUCCESS(0,"请求成功"),
    UN_LOGIN(1,"您还没有登录"),
    PASSWORD_ERROR(11,"密码输入错误"),
    PASSWORD_SAME(16,"新密码不能和旧密码相同"),
    SYSTEM_INFO_NOT_EXIST(17,"信息不存在"),
    WITH_DRAW_IS_FULL(24,"今日提现次数已达上限"),
    ACTIVITY_END(31,"该活动不存在或已结束"),
    WITH_DRAW_HAS_ORDER(100,"取款订单已存在"),
    USER_HAS_FREEZE(101,"全款玩家已被冻结"),
    WITH_DRAW_MIN_AMOUNT(102,"取款金额最少为x元"),
    NO_BANK(103,"没有银行卡信息"),
    WITH_DRAW_BETWEEN_MIN_MAX(104,"取款金额应在x-x范围内，并且小于等于x"),
    WITH_DRAW_FAIL(105,"取款失败"),
    HAS_BTC(204,"用户绑定比特币已存在"),
    USER_ADD_BANK_CARD(200,"用户添加银行卡"),
    SHOW_BANK_CARD_INFO(201,"展示银行信息"),
    USER_ADD_BTC(202,"用户添加比特币"),
    USER_BINDING_BTC_SUCCESS(205,"用户绑定比特币成功"),
    SUBMIT_BTC_FAIL(206,"用户绑定比特币失败"),
    USER_BINDING_BANK_CARD_EXIST(207,"用户绑定的银行卡号已存在"),
    REAL_NAME_NOT_NULL(300,"真实姓名不能为空"),
    VALIDATE_ERROR(301,"验证码输入错误"),
    REAL_NAME_ERROR(302,"真实姓名不正确"),
    ORIGIN_SAFE_PASSWORD_ERROR(303,"原始密码有误"),
    UPDATE_REAL_NAME_FAIL(304,"真实姓名修改失败"),
    SAFE_PASSWORD_NOT_NULL(305,"安全密码不能为空"),
    NEW_PASSWORD_NOT_NULL(306,"新密码不能为空"),
    PASSWORD_NOT_NULL(307,"当前密码不能为空"),
    SYSTEM_VALIDATE_NOT_NULL(308,"验证码不能为空"),
    UPDATE_PASSWORD_FAIL(309,"修改密码失败"),
    GAME_NOT_EXIST(400,"游戏不存在"),
    UPDATE_STATUS_FAIL(401,"更新失败"),
    NOT_RECOVER(402,"非免转不能一键回收"),
    ;

    private int code;
    private String msg;

    AppErrorCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
