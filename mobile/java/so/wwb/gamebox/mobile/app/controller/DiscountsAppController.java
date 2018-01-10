package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.data.json.JsonTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.app.common.CommonApp;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.controller.BaseDiscountsController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by legend on 17-12-31.
 */
@Controller
@RequestMapping("/discountsOrigin")
public class DiscountsAppController extends BaseDiscountsController{

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
