package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseDiscountsController;
import so.wwb.gamebox.model.master.enums.AppErrorCodeEnum;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by legend on 17-12-31.
 */
@Controller
@RequestMapping("/discountsOrigin")
public class DiscountsAppController extends BaseDiscountsController{
    private final String version = "app_01";
    Map<String, Object> mapJson = MapTool.newHashMap();



    /**
     * 获取优惠活用
     */
    @RequestMapping("/getActivityType")
    @ResponseBody
    public String getActivityType(HttpServletRequest request) {


        setMapJson(new AppModelVo());
        mapJson.put("data", getActivity(request));

        return JsonTool.toJson(mapJson);
    }





    private void setMapJson(AppModelVo app) {
        if (app.getError() != 0) {
            mapJson.put("error", app.getError());
        } else {
            mapJson.put("error", 0);
        }

        if (app.getCode() != 0) {
            mapJson.put("code", app.getCode());
        } else {
            mapJson.put("code", AppErrorCodeEnum.Success.getCode());
        }

        if (StringTool.isNotBlank(app.getMsg())) {
            mapJson.put("msg", app.getMsg());
        } else {
            mapJson.put("msg", AppErrorCodeEnum.Success.getMsg());
        }

        if (StringTool.isNotBlank(app.getVersion())) {
            mapJson.put("version", app.getVersion());
        } else {
            mapJson.put("version", version);
        }
    }
}
