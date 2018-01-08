package so.wwb.gamebox.mobile.App.constant;

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
}
