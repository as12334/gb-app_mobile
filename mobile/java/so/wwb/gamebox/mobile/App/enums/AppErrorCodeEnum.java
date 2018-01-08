package so.wwb.gamebox.mobile.App.enums;

public enum AppErrorCodeEnum{
    Success(0,"请求成功"),
    UN_LOGIN(1,"您还没有登录"),
    IsFull(24,"今日提现次数已达上限"),
    ActivityEnd(31,"该活动不存在或已结束"),
    hasOrder(100,"取款订单已存在"),
    hasFreeze(101,"全款玩家已被冻结"),
    IsBalanceAdequate(102,"取款金额最少为x元"),
    hasBtc(204,"用户绑定比特币已存在"),
    addCard(200,"用户添加银行卡"),
    showBankCardInfomation(201,"展示银行信息"),
    addBtc(202,"用户添加比特币"),
    bindingSuccess(205,"用户绑定比特币成功"),
    submitBtcfild(206,"用户绑定比特币失败"),
    realName(300,"真实姓名不能为空"),
    sysCode(301,"验证码输入错误"),
    realNameError(302,"真实姓名不正确"),
    originSafePwd(303,"原始密码有误"),
    realNameSetError(304,"真实姓名修改失败"),
    safePwdNotNull(305,"安全密码不能为空");

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
