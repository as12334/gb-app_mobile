package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.model.company.site.po.SiteApiTypeRelationI18n;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by  on 17-4-3.
 */
@Controller
@RequestMapping("/origin")
public class OriginController extends BaseApiController {
    private final String version = "app_01";
    Map<String,Object> map = MapTool.newHashMap();

    @RequestMapping("/mainIndex")
    @ResponseBody
    public String mainIndex(Model model, HttpServletRequest request) {
        Map<String,Object> mapJson = MapTool.newHashMap();

        //浮动图
        List<Map> floatList = ListTool.newArrayList();
        showMoneyActivityFloat(floatList);

        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());
        map.put("siteApiRelation", getSiteApiRelationI18n(model));
        map.put("activity", floatList);

        mapJson.put("error", 0);
        mapJson.put("code", null);
        mapJson.put("msg", "请求成功");
        mapJson.put("version", version);
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request){
        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        return null;
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}
