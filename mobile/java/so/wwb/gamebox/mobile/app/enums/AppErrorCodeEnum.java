package so.wwb.gamebox.mobile.app.enums;

public enum AppErrorCodeEnum{
    SUCCESS(0,"请求成功"),
    //与登录用户、密码相关
    UN_LOGIN(1,"您还没有登录"),
    PWD_ERROR(11,"密码输入错误"),
    PWD_SAME(16,"新密码不能和旧密码相同"),
    INFO_NOT_EXISTS(17,"信息不存在"),
    //取款相关
    WITHDRAW_IS_FULL(24,"今日提现次数已达上限"),
    WITHDRAW_HAS_ORDER(100,"取款订单已存在"),
    WITHDRAW_USER_FREEZE(101,"全款玩家已被冻结"),
    WITHDRAW_BALANCE_ADEQUATE(102,"取款金额最少为x元"),
    WITHDRAW_NOT_BANK(103,"没有银行卡信息"),
    WITHDRAW_INVALID_AMOUNT(104,"取款金额应在x-x范围内，并且小于等于x"),
    WITHDRAW_ERROR(105,"取款失败"),
    //活动相关
    ACTIVITY_NOT_EXIST_OR_END(31,"该活动不存在或已结束"),
    //个人信息相关
    BTC_EXISTS(204,"用户绑定比特币已存在"),
    addCard(200,"用户添加银行卡"),
    addBtc(202,"用户添加比特币"),
    bindingSuccess(205,"用户绑定比特币成功"),
    submitBtcfild(206,"用户绑定比特币失败"),
    hasbibindingBankCard(207,"用户绑定银行卡号已存在"),
    showBankCardInfomation(201,"展示银行信息"),
    realName(300,"真实姓名不能为空"),
    sysCode(301,"验证码输入错误"),
    realNameError(302,"真实姓名不正确"),
    originSafePwd(303,"原始密码有误"),
    realNameSetError(304,"真实姓名修改失败"),
    safePwdNotNull(305,"安全密码不能为空"),
    newPwdNotNull(306,"新密码不能为空"),
    pwdNotNull(307,"当前密码不能为空"),
    sysCodeNotNull(308,"验证码不能为空"),
    pwdUpdateError(309,"修改密码失败"),
    updateStatusError(401,"更新失败"),
    //游戏相关
    gameExist(400,"游戏不存在"),
    autoPay(402,"非免转不能一键回收"),
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
