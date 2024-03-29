package so.wwb.gamebox.mobile.app.enums;

public enum AppErrorCodeEnum {
    SUCCESS("0", "请求成功"),
    UN_LOGIN("1001", "您还未登录,请重新登录"),
    USER_LOCK("1005", "您的账号已被系统冻结，请联系客服处理"),
    USER_INFO_NOT_EXIST("1008", "用户不存在"),
    USER_NAME_NOT_NULL("1009","用户名不能为空"),
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
    SAFE_PASSWORD_ERROR("1313", "安全密码有误"),
    UPDATE_REAL_NAME_FAIL("1304", "真实姓名修改失败"),
    SAFE_PASSWORD_NOT_NULL("1305", "安全密码不能为空"),
    NEW_PASSWORD_NOT_NULL("1306", "新密码不能为空"),
    PASSWORD_NOT_NULL("1307", "当前密码不能为空"),
    SYSTEM_VALIDATE_NOT_NULL("1308", "验证码不能为空"),
    UPDATE_PASSWORD_FAIL("1309", "修改密码失败"),
    PARAM_HAS_ERROR("1310", "参数有误"),
    SAFE_PASSWORD_TOO_SIMPLE("1311", "密码过于简单"),
    ORIGIN_SAFE_PASSWORD_ERROR_TIME("1312","安全密码有误,还有%d次机会"),
    OLD_PHONE_INCORRECT("1313","原手机号码不正确"),
    GAME_NOT_EXIST("1400", "游戏不存在"),
    UPDATE_STATUS_FAIL("1401", "更新失败"),
    NOT_RECOVER("1402", "非免转不能一键回收"),
    SYSTEM_READ("1403", "消息已读"),
    NOT_SET_SAFE_PASSWORD("1404", "未设置安全密码"),
    RECOVER_FIAL("1405","回收失败"),
    RECOVER_PROCESS("1406","正在回收中"),
    RECOVER_SUCCESS("1407","资金回收成功"),
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
    TRANSFER_ACCOUNT_NOT_ENOUGH("1521","当前钱包余额不足"),
    REGISTER_PHONE_NOTNULL("1522","手机号不能为空"),
    REGISTER_PHONE_OFTEN("1523","手机发送验证码过于频繁"),
    REGISTER_PHONE_FAIL("1524","手机验证码发送失败"),
    DINDING_PHONE_FAIL("1525","绑定手机号失败"),
    UNBOUND_PHONE("1526","该账号未绑定手机号，请联系客户找回密码"),
    NO_AVAILABLE_CHANNELS("1600","支付系统维护中，请稍后刷新重试"),
    SUBMIT_DATA_ERROR("1601","提交信息有误，请核对信息提交"),
    CHANNEL_CLOSURE("1602","渠道未在使用中，请刷新存款页面后重试"),
    MONEY_ERROR("1603","存款金额有误，请输入正确的存款金额"),
    NOT_SALE("1604","暂无优惠和手续费"),
    DEPOSIT_TYPE_ERROR("1605","存款渠道有误，建议您更换渠道或稍后刷新重试"),
    DEPOSIT_FAIL("1606","存款失败，请稍后重试"),
    ORDER_ERROR("1607","订单号有误，请重新存款"),
    TXIDISEMPTY("1608","比特币TxId不能为空，请重新输入"),
    TXIDISEXISTS("1609","比特币TxId已存在，请重新输入"),
    RECHARGE_NOT_EXIT_USER("1610","存款失败，用户不存在"),
    RECHARGE_TIME_INTERVAL("1611","存款太过频繁,休息片刻"),
    ACTIVITY_ID_EMPTY("1612","活动Id不能为空"),
    ACTIVITY_IS_INVALID("1613","活动已失效"),
    ACTIVITY_NOTSTARTED("1614","活动未开始"),
    ACTIVITY_FINISHED("1615","活动已过期"),
    ACTIVITY_APPLY_SUCCESS("1616","活动申请成功"),
    ACTIVITY_APPLY_FAIL("1617","活动申请失败"),
    ACTIVITY_IS_PARTICIPATION("1618","无需申请，正在参与中"),
    ACTIVITY_FETCH_FAIL("1619","活动报名失败"),
    NPHONE_AND_OPHONE_AGREEMENT("1620", "新旧手机号不能一致"),
    PHONENUMBER_FORMAT_WRONG("1621","手机号格式不正确")
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
