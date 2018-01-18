package so.wwb.gamebox.mobile.app.constant;

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
}
