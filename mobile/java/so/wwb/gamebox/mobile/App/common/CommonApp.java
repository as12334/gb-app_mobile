package so.wwb.gamebox.mobile.App.common;

import so.wwb.gamebox.mobile.App.constant.AppConstant;
import so.wwb.gamebox.mobile.App.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.App.model.AppModelVo;

/**
 * Created by legend on 18-1-7.
 */
public class CommonApp {

    public static AppModelVo buildAppModelVo(Object data) {
        AppModelVo appModelVo = new AppModelVo();

        appModelVo.setVersion(AppConstant.appVersion);
        appModelVo.setMsg(AppErrorCodeEnum.Success.getMsg());
        appModelVo.setCode(AppErrorCodeEnum.Success.getCode());
        appModelVo.setData(data);

        return appModelVo;
    }
}
