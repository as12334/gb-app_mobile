package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by  on 17-4-3.
 */
@Controller
@RequestMapping("/origin")
public class OriginController extends BaseApiController {
    private final String version = "app_01";
    Map<String,Object> mapJson = MapTool.newHashMap();

    @RequestMapping("/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request) {
        Map<String,Object> map = MapTool.newHashMap();
        Map<String,Object> lotteryGame = MapTool.newHashMap();
        Map<String,Object> casinoMap = MapTool.newHashMap();
        List<Map> floatList = ListTool.newArrayList();

        //浮动图
        showMoneyActivityFloat(floatList);

        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());

        map.put("siteApiRelation", getSiteApiRelationI18n(lotteryGame,casinoMap));
        map.put("activity", floatList);
        map.put("lotteryGame",lotteryGame);
        map.put("casinoMap",casinoMap);

        setMapJson();
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request){
        Map<String,Object> map = MapTool.newHashMap();
        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        return null;
    }

    private void setMapJson(){
        mapJson.put("error", 0);
        mapJson.put("code", null);
        mapJson.put("msg", "请求成功");
        mapJson.put("version", version);
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}