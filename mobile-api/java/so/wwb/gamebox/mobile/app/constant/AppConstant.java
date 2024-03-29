package so.wwb.gamebox.mobile.app.constant;

import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 游戏图片路径公共部分地址：ftl/终端/分辨率/语言/apiId/gameCode
     */
    String GAME_COVER_URL = "/ftl/commonPage/images/game_logo/%s/%s/%s/{0}/{1}.png";

    /**
     * 首页默认广播图
     */
    String DEFAULT_BANNER_URL = "%s/images/ban-01.jpg";

    /*api图片地址 */
    String API_LOGO_URL = "%s/api/api_logo_%s.png";

    /*apiType图片地址 */
    String API_TYPE_LOGO_URL = "%s/api_type_%s.png";

    /**
     * 存款渠道图片路径：/终端/common/deposit/分辨率/deposit_entry
     */
    String DEPOSIT_ENTRY_URL = "%s/common/deposit/%s/deposit_entry/%s.png";

    String DEPOSIT_IMG_URL = "%s/common/pay_bank_b/%s/%s.png";

    String ACCOUNT_IMG_URL = "%s/common/pay_bank/%s.png";

    String RECHARGE_TYPE_STR = "recharge_type.";

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

    String MCENTER_ONLINE_RECHARGE_URL = "fund/deposit/online/list.html"; //站长中心-线上支付链接
    String MCENTER_COMPANY_RECHARGE_URL = "fund/deposit/company/confirmCheck.html";//公司入款地址
    //公司入款支持的存款类型
    String[] RECHARGE_TYPE = {
            RechargeTypeEnum.ONLINE_BANK.getCode(),
            RechargeTypeEnum.ATM_COUNTER.getCode(),
            RechargeTypeEnum.ATM_MONEY.getCode(),
            RechargeTypeEnum.ATM_RECHARGE.getCode()
    };

    //特殊站点图片路径地址
    Map<Integer, List<Integer>> API_SITE_SPECIAL = new HashMap() {{
        put(37, Arrays.asList(119, 270, 209, 141));
        put(40, Arrays.asList(322));
    }};

    String USER_REGISTER = "玩家注册验证";
    String FORGET_PASSWORD = "忘记密码验证";

    String RELATION_TYPE_GAME = "game";
    String RELATION_TYPE_API = "api";
    String RELATION_TYPE_APITYPE = "apiType";
    String JOIN_CHAR = "_";

    /*游戏图片公共目录 ftl/resource/{chess}/public/game/game01/{分辨率}/{语言}/{图片名称} */
    String CHESS_GAME_IMG_PATH = "/ftl/resource/chess/public/game/game01/%s/%s/%s";

    /*游戏个性图片目录 ftl/resource/{chess}/sites/{7000}/game/{分辨率}/{语言}/{图片名称} */
    String CHESS_GAME_OWN_IMG_PATH = "/ftl/resource/%s/sites/%s/game/%s/%s/%s";
    /**
     * Chess 游戏图片路径  game_{chess}_{apiId}_{gameCode}.png
     */
    String CHESS_GAME_COVER_URL = "game_%s_%s_%s.png";

    /*Chess api图片地址  game_{chess}_{apiId}.png*/
    String CHESS_API_LOGO_URL = "game_%s_%s.png";

    /*Chess apiType图片地址  game_{chess}.png*/
    String CHESS_API_TYPE_LOGO_URL = "game_%s.png";

    /*字符串占位符*/
    String STR_PLACEHOLDER = "%s";
}
