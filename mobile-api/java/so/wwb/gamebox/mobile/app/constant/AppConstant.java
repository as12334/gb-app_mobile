package so.wwb.gamebox.mobile.app.constant;

import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

public interface AppConstant {
    String APP_VERSION = "app_01";//版本号

    String SPLIT_REGEX = ",";//String转数据

    String TARGET_REGEX = "x";//匹配正则替换

    int APP_ERROR_TIMES = 5;//安全码错误次数

    int TIME_INTERVAL = -30; //设置时间区间的最小时间

    int DEFAULT_TIME = 1; //默认时间

    int PROMO_RECORD_DAYS = -7; //设置一周前

    int RECOMMEND_DAYS = -1;

    int LAST_WEEK__MIN_TIME = -6; //设置一周查询时间的最小时间

    int ZERO = 0;

    int TWO = 2;

    String KEY_CAPTCHA = "captcha";//需要安全密码验证码

    String KEY_TIMES_KEY = "times";//安全密码错误剩余次数key值

    String KEY_STATE_KEY = "state"; //安全密码状态key值

    String KEY_FORCE_START = "force"; //安全密码生效开始时间key值

    String CUSTOMER_SERVICE_KEY = "customer";//客服

    int SYSTEM_NOTICE_MIN_TIME = -29; //系统最小时间

    int FOUR = 4;

    int SIX = 6;

    float ONE_FLOAT = 1F;

    int RECOVERY_TIME_INTERVAL = 10;//一键回收按钮时间间隔，以秒为单位

    int API_RECOVERY_TIME_INTERVAL = 3;//单个api回收按钮时间间隔，以秒为单位

    String IS_READ = "12"; //站点中心是否标志为已读信息

    int SEND_MSG_CAPTCHA_COUNT = 3; // 我的消息 申请优惠发送消息次数

    Integer FISH_API_TYPE_ID = -1;

    String COMMON_PAYBANK_PHOTO = "/common/pay_bank/"; //银行字样 的图片

    /**游戏图片路径公共部分地址：ftl/终端/分辨率/语言/apiId/gameCode*/
    String GAME_COVER_URL = "/ftl/commonPage/images/game_logo/%s/%s/%s/{0}/{1}.png";

    /**首页默认广播图*/
    String DEFAULT_BANNER_URL = "%s/images/ban-01.jpg";

    /*api图片地址 */
    String API_LOGO_URL = "%s/api/api_logo_%s.png";

    /**存款渠道图片路径：/终端/common/deposit/分辨率/deposit_entry*/
    String DEPOSIT_ENTRY_URL = "%s/common/deposit/%s/deposit_entry/%s.png";

    String SUCCESS = "success";    //4 , 2 ,success代表已发放
    String SUCCESS_4 = "4";
    String SUCCESS_2 = "2";

    String CHECKING_1 = "1"; // 1代表 待审核
    String PROCESSING_0 = "0"; // 0代表 进行中
    String KEY_STATE = "state";
    String TRANSFER_WALLET = "wallet";//转入转出选择我的钱包



    String IS_FAST_RECHARGE = "isFastRecharge";//快速充值标记
    String WECHAT_PAY = "wechatpay";
    String ALI_PAY = "alipay";
    String QQ_WALLET = "qqwallet";
    String JD_WALLET = "jdwallet";
    String BD_WALLET = "bdwallet";
    String ONE_CODE_PAY = "onecodepay";
    String OTHER = "other";
    String BITCOIN = "bitcoin";
    String WECHAT_MICROPAY = "pay_name.wechat_micropay";
    String ALIPAY_MICROPAY = "pay_name.alipay_micropay";
    String QQ_MICROPAY = "pay_name.qq_micropay";

    String MCENTER_ONLINE_RECHARGE_URL = "/fund/deposit/online/list.html"; //站长中心-线上支付链接
    String ONLINE_PAY_URL ="/wallet/deposit/online/pay.html?pay=online&search.transactionNo=";
    String SCAN_PAY_URL ="/wallet/deposit/online/scan/pay.html?pay=online&search.transactionNo=";
    String MCENTER_COMPANY_RECHARGE_URL = "fund/deposit/company/confirmCheck.html";//公司入款地址

    //公司入款支持的存款类型
    String[] RECHARGE_TYPE = {
            RechargeTypeEnum.ONLINE_BANK.getCode(),
            RechargeTypeEnum.ATM_COUNTER.getCode(),
            RechargeTypeEnum.ATM_MONEY.getCode(),
            RechargeTypeEnum.ATM_RECHARGE.getCode()
    };
}
