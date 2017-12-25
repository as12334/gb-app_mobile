package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.data.json.JsonTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("/mainIndex")
    public String mainIndex(Model model, HttpServletRequest request, Integer skip, String path) {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> mapJson = new HashMap<>();
        //查询banner
        List<Map> bannerList = getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode());
        //查询公告
        List<CttAnnouncement> announcementList = getAnnouncement();
        //获取游戏及种类
        Map<Integer, List<SiteApiTypeRelationI18n>> siteApiRelationMap = getSiteApiRelationI18n(model);


        //浮动图
        List<Map> floatList = new ArrayList<>();
        showMoneyActivityFloat(floatList);

        map.put("banner", bannerList);
        map.put("announcement", announcementList);
        map.put("siteApiRelation", siteApiRelationMap);
        map.put("activity", floatList);

        mapJson.put("error", 0);
        mapJson.put("code", null);
        mapJson.put("msg", "请求成功");
        mapJson.put("version", "app_01");
        mapJson.put("data", map);

        String jsonStr = JsonTool.toJson(map);
        return jsonStr;
    }


    @Override
    protected String getDemoIndex() {
        return null;
    }
}
