package so.wwb.gamebox.mobile.app.constant;

public interface AppConstant {
    String appVersion = "app_01";//版本号

    String SplitRegex = ",";//String转数据

    String targetRegex = "x";//匹配正则替换

    int appErrorTimes = 5;//安全码错误次数

    int TIME_INTERVAL = -30; //设置时间区间的最小时间

    int DEFAULT_TIME = 1; //默认时间

    int PROMO_RECORD_DAYS = -7; //设置一周前
    int RECOMMEND_DAYS = -1;
    int LAST_WEEK__MIN_TIME = -6; //设置一周查询时间的最小时间

    int ZERO = 0;

    int TWO = 2;

    String keyCaptcha = "captcha";//需要安全密码验证码

    String keyTimes = "times";//安全密码错误剩余次数

    String keyState = "state"; //安全密码状态

    String keyForceStart = "force"; //安全密码生效开始时间

    String customerService = "customer";//客服

    int sysNoticeMinTime = -29; //系统最小时间

    int four = 4;

    float oneF = 1F;
}
