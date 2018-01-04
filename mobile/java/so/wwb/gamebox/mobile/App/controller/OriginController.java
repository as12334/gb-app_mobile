package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.security.CryptoTool;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.*;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.AppErrorCodeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;
import so.wwb.gamebox.model.master.setting.vo.AppRequestModelVo;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.soul.web.tag.ImageTag.getImagePath;

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
    public String mainIndex(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = MapTool.newHashMap();

        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());

        map.put("siteApiRelation", getSiteApiRelationI18n(request,model));
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
    public String getSiteApi(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = MapTool.newHashMap();
        //公告
        map.put("siteApiRelation", getSiteApiRelationI18n(request,model));
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


    /**
     * 查询Banner
     *
     */
    protected List<Map> getCarouselApp(HttpServletRequest request, String type) {
        Map<String, Map> carousels = (Map) Cache.getSiteCarousel();
        List<Map> resultList = new ArrayList<>();
        String webSite = ServletTool.getDomainFullAddress(request);
        if (carousels != null) {
            for (Map m : carousels.values()) {
                if ((StringTool.equalsIgnoreCase(type, m.get("type").toString()))
                        && (StringTool.equals(m.get(CttCarouselI18n.PROP_LANGUAGE).toString(), SessionManager.getLocale().toString()))
                        && (((Date) m.get("start_time")).before(new Date()) && ((Date) m.get("end_time")).after(new Date()))
                        && (MapTool.getBoolean(m, "status") == null || MapTool.getBoolean(m, "status") == true)) {
                    String link = String.valueOf(m.get("link"));
                    if (StringTool.isNotBlank(link)) {
                        if (link.contains("${website}")) {
                            link = link.replace("${website}", webSite);
                        }
                    }
                    m.put("link", link);
                    String cover = m.get("cover").toString();
                    cover = getImagePath(SessionManager.getDomain(request), cover);
                    m.put("cover", cover);
                    m.put("link", link);
                    resultList.add(m);
                }
            }
        }
        return resultList;
    }

    /**
     * 查询公告
     */
    private List<CttAnnouncement> getAnnouncement() {
        Map<String, CttAnnouncement> announcement = Cache.getSiteAnnouncement();
        List<CttAnnouncement> resultList = new ArrayList<>();
        if (announcement != null) {
            for (CttAnnouncement an : announcement.values()) {
                if (StringTool.equals(an.getAnnouncementType(), CttAnnouncementTypeEnum.SITE_ANNOUNCEMENT.getCode())
                        && StringTool.equals(an.getLanguage(), SessionManager.getLocale().toString())) {
                    resultList.add(an);
                }
            }
        }
        return resultList;
    }


    /**
     * 接口获取红包活动
     * @param request
     * @return
     */
    protected AppFloatPicItem getMoneyActivityFloat(HttpServletRequest request){
        AppFloatPicItem appFloatPicItem = null;
        CttFloatPic cttFloatPic = queryMoneyActivityFloat();
        if(cttFloatPic == null){
            return appFloatPicItem;
        }

        PlayerActivityMessage moneyActivity = findMoneyActivity();
        if(moneyActivity == null){
            return appFloatPicItem;
        }

        appFloatPicItem = new AppFloatPicItem();
        CttFloatPicItem cttFloatPicItem = queryMoneyFloatPic(cttFloatPic);
        appFloatPicItem.setDescription(moneyActivity.getActivityDescription());
        appFloatPicItem.setActivityId(CryptoTool.aesEncrypt(String.valueOf(moneyActivity.getId()), "PlayerActivityMessageListVo"));
        appFloatPicItem.setNormalEffect(getImagePath( SessionManager.getDomain(request),cttFloatPicItem.getNormalEffect()));
        appFloatPicItem.setLocation(cttFloatPic.getLocation());
        appFloatPicItem.setLanguage(cttFloatPic.getLanguage());
        appFloatPicItem.setDistanceSide(cttFloatPic.getDistanceSide());
        appFloatPicItem.setDistanceTop(cttFloatPic.getDistanceTop());

        return appFloatPicItem;
    }

    /**
     * 查询红包浮动图
     */
    private CttFloatPic queryMoneyActivityFloat() {
        Map<String, CttFloatPic> floatPicMap = Cache.getFloatPic();
        Iterator<String> iter = floatPicMap.keySet().iterator();
        CttFloatPic tempFloatPic = null;
        while (iter.hasNext()) {
            String key = iter.next();
            CttFloatPic cttFloatPic = floatPicMap.get(key);
            if (CttPicTypeEnum.PROMO.getCode().equals(cttFloatPic.getPicType()) && cttFloatPic.getStatus()) {
                tempFloatPic = cttFloatPic;
                break;
            }
        }
        return tempFloatPic;
    }

    /**
     * 查找红包活动
     *
     * @return
     */
    private PlayerActivityMessage findMoneyActivity() {
        Map<String, PlayerActivityMessage> activityMessages = Cache.getActivityMessages(SessionManagerBase.getSiteId());
        String lang = SessionManagerBase.getLocale().toString();
        Iterator<String> iter = activityMessages.keySet().iterator();
        Date justNow = new Date();
        PlayerActivityMessage playerActivityMessage = null;
        while (iter.hasNext()) {
            String key = iter.next();
            if (key.endsWith(lang)) {
                playerActivityMessage = activityMessages.get(key);
                Date startTime = playerActivityMessage.getStartTime();
                Date endTime = playerActivityMessage.getEndTime();
                if (!ActivityTypeEnum.MONEY.getCode().equals(playerActivityMessage.getCode())) {
                    //不是红包活动继续
                    continue;
                }
                if (playerActivityMessage.getIsDeleted()) {
                    continue;
                }
                if (!playerActivityMessage.getIsDisplay()) {
                    continue;
                }
                if (startTime.before(justNow) && justNow.before(endTime)) {
                    return playerActivityMessage;
                }

            }
        }
        return null;
    }

    private CttFloatPicItem queryMoneyFloatPic(CttFloatPic cttFloatPic) {
        CttFloatPicItem item = null;
        Map<String, CttFloatPicItem> floatPicItemMap = Cache.getFloatPicItem();
        Iterator<String> iter = floatPicItemMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            CttFloatPicItem cttFloatPicItem = floatPicItemMap.get(key);
            if (cttFloatPicItem.getFloatPicId().equals(cttFloatPic.getId())) {
                item = cttFloatPicItem;
                break;
            }
        }
        return item;
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}