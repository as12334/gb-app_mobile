package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.App.common.CommonApp;
import so.wwb.gamebox.mobile.App.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.App.model.AppModelVo;
import so.wwb.gamebox.mobile.controller.BaseDiscountsController;

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

        AppModelVo vo = CommonApp.buildAppModelVo(getActivity(request));

        return JsonTool.toJson(vo);
    }

}
