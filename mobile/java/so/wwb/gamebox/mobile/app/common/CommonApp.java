package so.wwb.gamebox.mobile.app.common;

import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;

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
