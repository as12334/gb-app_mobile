package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.security.CryptoTool;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.controller.BaseOriginController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteGameTag;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.content.po.CttCarouselI18n;
import so.wwb.gamebox.model.master.content.po.CttFloatPic;
import so.wwb.gamebox.model.master.content.po.CttFloatPicItem;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static org.soul.web.tag.ImageTag.getImagePath;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

@Controller
@RequestMapping("/origin")
public class OriginController extends BaseOriginController {
    private Log LOG = LogFactory.getLog(OriginController.class);

    //region mainIndex
    /**
     * 请求首页，查询轮播图，公告，游戏类，红包活动
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = MapTool.newHashMap();
        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());
        map.put("siteApiRelation", getSiteApiRelationI18n(request, model));
        map.put("activity", getMoneyActivityFloat(request));

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 查询轮播图
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request) {
        //轮播图
        Map<String, Object> map = MapTool.newHashMap();
        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 查询公告
     *
     * @return
     */
    @RequestMapping(value = "/getAnnouncement")
    @ResponseBody
    public String getAnnounce() {
        //公告
        Map<String, Object> map = MapTool.newHashMap();
        map.put("announcement", getAnnouncement());

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 查询游戏类
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/getSiteApiRelation")
    @ResponseBody
    public String getSiteApi(HttpServletRequest request, AppRequestModelVo model) {
        //游戏
        Map<String, Object> map = MapTool.newHashMap();
        map.put("siteApiRelation", getSiteApiRelationI18n(request, model));

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 查询红包活动图
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getFloat")
    @ResponseBody
    public String getFloat(HttpServletRequest request) {
        //浮动图
        Map<String, Object> map = MapTool.newHashMap();
        map.put("activity", getMoneyActivityFloat(request));

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 获取电子游戏
     *
     * @param listVo
     * @param request
     * @param tag
     * @return
     */
    @RequestMapping(value = "/getCasinoGame")
    @ResponseBody
    public String getCasinoGame(SiteGameListVo listVo, HttpServletRequest request, SiteGameTag tag) {
        //电子游戏
        Map<String, Object> map = MapTool.newHashMap();
        Map<String, Object> pageTotal = MapTool.newHashMap();
        map.put("casinoGames", getCasinoGameByApiId(listVo, request, pageTotal, tag));
        map.put("page", pageTotal);

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 获取游戏分类
     *
     * @return
     */
    @RequestMapping(value = "/getGameTag")
    @ResponseBody
    public String getGameTags() {
        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getGameTag(),
                APP_VERSION);
    }

    /**
     * 获取登录或注册游戏链接地址
     *
     * @param siteGame
     * @param request
     * @param modelVo
     * @return
     */
    @RequestMapping(value = "getGameLink")
    @ResponseBody
    public String getGameLink(AppRequestGameLink siteGame, HttpServletRequest request, AppRequestModelVo modelVo) {
        if (SessionManager.getUser() == null) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.UN_LOGIN.getCode(),
                    AppErrorCodeEnum.UN_LOGIN.getMsg(),
                    null, APP_VERSION);
        }
        if (siteGame.getApiId() == null) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.GAME_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.GAME_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (siteGame.getApiTypeId() == null) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.GAME_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.GAME_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        Map map = MapTool.newHashMap();

        if (SessionManager.isAutoPay()) {
            AppSiteApiTypeRelationI18n gameUrl = goGameUrl(request, siteGame.getApiId(), siteGame.getApiTypeId(), siteGame.getGameCode(), modelVo);

            map.put("gameLink", gameUrl.getGameLink());
            map.put("gameMsg", gameUrl.getGameMsg());

            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                    AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    map,
                    APP_VERSION);
        } else {
            if (Integer.parseInt(siteGame.getApiTypeId()) == ApiTypeEnum.CASINO.getCode()) {
                PlayerApiAccountVo player = new PlayerApiAccountVo();
                player.setApiId(siteGame.getApiId());
                player.setApiTypeId(siteGame.getApiTypeId().toString());
                player.setGameId(siteGame.getGameId());
                player.setGameCode(siteGame.getGameCode());
                AppSiteApiTypeRelationI18n gameUrl = getCasinoGameUrl(player, request, modelVo);
                map.put("gameLink", gameUrl.getGameLink());
                map.put("gameMsg", gameUrl.getGameMsg());

                return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                        AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map,
                        APP_VERSION);
            }
        }
        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }
    //endregion mainIndex

    /**
     * 查询Banner
     */
    protected List<Map> getCarouselApp(HttpServletRequest request, String type) {
        Map<String, Map> carousels = (Map) Cache.getSiteCarousel();
        List<Map> resultList = ListTool.newArrayList();
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
        //没数据默认banner图
        if (resultList.size() <= 0) {
            Map defaultMap = MapTool.newHashMap();
            String coverUrl = MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()) + "/images/ban-01.jpg";
            defaultMap.put("cover", coverUrl);
            resultList.add(defaultMap);
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
     *
     * @param request
     * @return
     */
    protected AppFloatPicItem getMoneyActivityFloat(HttpServletRequest request) {
        CttFloatPic cttFloatPic = queryMoneyActivityFloat();
        if (cttFloatPic == null) {
            return null;
        }

        PlayerActivityMessage moneyActivity = findMoneyActivity();
        if (moneyActivity == null) {
            return null;
        }

        AppFloatPicItem appFloatPicItem = new AppFloatPicItem();
        CttFloatPicItem cttFloatPicItem = queryMoneyFloatPic(cttFloatPic);
        appFloatPicItem.setDescription(moneyActivity.getActivityDescription());
        appFloatPicItem.setActivityId(CryptoTool.aesEncrypt(String.valueOf(moneyActivity.getId()), "PlayerActivityMessageListVo"));
        appFloatPicItem.setNormalEffect(getImagePath(SessionManager.getDomain(request), cttFloatPicItem.getNormalEffect()));
        appFloatPicItem.setLocation(cttFloatPic.getLocation());
        appFloatPicItem.setLanguage(cttFloatPic.getLanguage());
        appFloatPicItem.setDistanceSide(cttFloatPic.getDistanceSide());
        appFloatPicItem.setDistanceTop(cttFloatPic.getDistanceTop());

        return appFloatPicItem;
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

}