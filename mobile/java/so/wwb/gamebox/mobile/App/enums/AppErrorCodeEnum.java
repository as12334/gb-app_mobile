package so.wwb.gamebox.mobile.App.enums;

public enum AppErrorCodeEnum{
    Success(0,"请求成功"),
    UN_LOGIN(1,"您还没有登录"),
    IsFull(24,"今日提现次数已达上限"),
    ActivityEnd(31,"该活动不存在或已结束"),
    hasOrder(100,"取款订单已存在"),
    hasFreeze(101,"全款玩家已被冻结"),
    IsBalanceAdequate(102,"取款金额最少为x元");

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
