package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.master.enums.AppErrorCodeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by  on 17-4-3.
 */
@Controller
@RequestMapping("/origin")
public class OriginController extends BaseApiController {
    private final String version = "app_01";
    Map<String, Object> mapJson = MapTool.newHashMap();

    //region mainIndex
    @RequestMapping("/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();

        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());

        map.put("siteApiRelation", getSiteApiRelationI18n(request));
        map.put("activity", getMoneyActivityFloat(request));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();
        //轮播图
        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getAnnouncement")
    @ResponseBody
    public String getAnnounce() {
        Map<String, Object> map = MapTool.newHashMap();
        //公告
        map.put("announcement", getAnnouncement());

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getSiteApiRelation")
    @ResponseBody
    public String getSiteApi(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();
        //公告
        map.put("siteApiRelation", getSiteApiRelationI18n(request));
        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getFloat")
    @ResponseBody
    public String getFloat(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();

        map.put("activity", getMoneyActivityFloat(request));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCasinoGame")
    @ResponseBody
    public String getCasinoGame(SiteGameListVo listVo,HttpServletRequest request){
        Map<String,Object> map = MapTool.newHashMap();
        Map<String,Object> page = MapTool.newHashMap();

        map.put("casinoGames",getCasinoGameByApiId(listVo,request,page));
        map.put("page",page);
        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }
    //endregion mainIndex

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

    @Override
    protected String getDemoIndex() {
        return null;
    }
}