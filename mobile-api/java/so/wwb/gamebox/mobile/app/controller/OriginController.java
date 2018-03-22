package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.security.CryptoTool;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.controller.BaseOriginController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.company.help.po.HelpDocumentI18n;
import so.wwb.gamebox.model.company.help.po.VHelpTypeAndDocument;
import so.wwb.gamebox.model.company.help.vo.VHelpTypeAndDocumentListVo;
import so.wwb.gamebox.model.company.site.po.SiteGameTag;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttDocumentEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.*;
import so.wwb.gamebox.model.master.content.vo.CttDocumentI18nListVo;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.enums.CttCarouselTypeEnum;
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
     * 获取当前时区
     *
     * @return
     */
    @RequestMapping(value = "/getTimeZone")
    @ResponseBody
    public String getTimeZone() {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), SessionManager.getTimeZone(), APP_VERSION);
    }


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
        Map<String, Object> map = new HashMap<>(5, 1f);
        getBannerAndPhoneDialog(map, request);//获取轮播图和手机弹窗广告
        map.put("announcement", getAnnouncement());
        map.put("siteApiRelation", getApiTypeGame(model, request));
        map.put("activity", getMoneyActivityFloat(request));
        map.put("language", SessionManager.getLocale().toString());

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
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
        //轮播图和弹窗广告
        Map<String, Object> map = new HashMap<>(2, 1f);
        getBannerAndPhoneDialog(map, request);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
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
        Map<String, Object> map = new HashMap<>(1, 1f);
        map.put("announcement", getAnnouncement());

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
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
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getApiTypeGame(model, request),
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
        Map<String, Object> map = new HashMap<>(1);
        map.put("activity", getMoneyActivityFloat(request));

        return AppModelVo.getAppModeVoJson(true,
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
        listVo = getGameByApiIdAndApiTypeId(listVo, tag);
        //处理游戏结果
        Map<String, Object> map = new HashMap<>(2, 1f);
        map.put("casinoGames", handleCasinoGames(listVo.getResult(), ServletTool.getDomainFullAddress(request), SessionManager.isAutoPay()));
        Map<String, Object> pageTotal = new HashMap<>(1, 1f);
        //总数
        pageTotal.put("pageTotal", listVo.getPaging().getTotalCount());
        map.put("page", pageTotal);

        return AppModelVo.getAppModeVoJson(true,
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
        return AppModelVo.getAppModeVoJson(true,
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
    @RequestMapping("/getGameLink")
    @ResponseBody
    public String getGameLink(AppRequestGameLink siteGame, HttpServletRequest request, AppRequestModelVo modelVo) {
        if (siteGame.getApiId() == null || siteGame.getApiTypeId() == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.GAME_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.GAME_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        Map map = new HashMap<>(2, 1f);

        PlayerApiAccountVo player = new PlayerApiAccountVo();
        player.setApiId(siteGame.getApiId());
        player.setApiTypeId(siteGame.getApiTypeId());
        player.setGameId(siteGame.getGameId());
        player.setGameCode(siteGame.getGameCode());
        AppSiteApiTypeRelationI18n gameUrl = SessionManager.isAutoPay() ? goGameUrl(request, siteGame.getApiId(), siteGame.getApiTypeId(), siteGame.getGameCode(), modelVo) : getCasinoGameUrl(player, request, modelVo);
        map.put("gameLink", gameUrl.getGameLink());
        map.put("gameMsg", gameUrl.getGameMsg());

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 关于我们
     *
     * @return
     */
    @RequestMapping("/about")
    @ResponseBody
    public String getAbout(HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                setAbout(request),
                APP_VERSION);
    }

    /**
     * 注册条款
     *
     * @return
     */
    @RequestMapping("/terms")
    @ResponseBody
    public String getRegisterRules() {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                Cache.getSiteI18n(SiteI18nEnum.MASTER_SERVICE_TERMS).get(SessionManager.getLocale().toString()),
                APP_VERSION);
    }

    /**
     * 获取帮助文档父类型
     *
     * @return
     */
    @RequestMapping("/helpFirstType")
    @ResponseBody
    public String getHelpParentType() {
        //先查出帮助中心父菜单
        Criteria criteria = Criteria.add(VHelpTypeAndDocument.PROP_PARENT_ID, Operator.IS_NULL, true);
        List<VHelpTypeAndDocument> helpTypeAndDocumentList;
        helpTypeAndDocumentList = CollectionQueryTool.query(Cache.getHelpTypeAndDocument().values(), criteria);

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getHelpType(getTypeI18n(helpTypeAndDocumentList)),
                APP_VERSION);
    }

    /**
     * 获取帮助文档子类型
     *
     * @param vHelpTypeAndDocumentListVo
     * @return
     */
    @RequestMapping("/secondType")
    @ResponseBody
    public String getHelpChildType(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo) {
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        if (searchId == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        Criteria criteria = Criteria.add(VHelpTypeAndDocument.PROP_PARENT_ID, Operator.EQ, searchId);
        List<VHelpTypeAndDocument> typeList;
        typeList = CollectionQueryTool.query(Cache.getHelpTypeAndDocument().values(), criteria);
        Map map = new HashMap(2, 1f);
        map.put("list", getHelpType(getTypeI18n(typeList)));
        map.put("name", getTypeName(searchId));
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 获取帮助文档详情国际化
     *
     * @param vHelpTypeAndDocumentListVo
     * @return
     */
    @RequestMapping("/helpDetail")
    @ResponseBody
    public String getHelpDetail(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo) {
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        VHelpTypeAndDocument vHelpTypeAndDocument = Cache.getHelpTypeAndDocument().get(searchId.toString());
        List<Map<String, String>> list = JsonTool.fromJson(vHelpTypeAndDocument.getDocumentIdJson(), List.class);
        List<HelpDocumentI18n> documentI18nList = new ArrayList<>();
        if (list != null) {
            for (Map<String, String> map : list) {
                HelpDocumentI18n helpDocumentI18n = Cache.getHelpDocumentI18n().get(String.valueOf(map.get("id")));
                if (helpDocumentI18n != null) {
                    String content = helpDocumentI18n.getHelpContent().replaceAll("\\$\\{customerservice}", "在线客服");
                    helpDocumentI18n.setHelpContent(content);
                }
                documentI18nList.add(helpDocumentI18n);
            }
        }
        Map map = new HashMap(2, 1f);
        map.put("list", documentI18nList);
        map.put("name", getTypeName(searchId));
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }
    //endregion mainIndex

    private List<HelpParentTypeApp> getHelpType(List<Map<String, String>> getTypeI18n) {
        List<HelpParentTypeApp> helpParents = new ArrayList<>();
        VHelpTypeAndDocumentListVo listVo = new VHelpTypeAndDocumentListVo();
        for (Map map : getTypeI18n) {
            HelpParentTypeApp parent = new HelpParentTypeApp();
            parent.setId(listVo.getSearchId(map.get("id")));
            if (map.get("ids") != null) {
                parent.setIds(map.get("ids").toString());
            }
            if (map.get("name") != null) {
                parent.setName(map.get("name").toString());
            }
            helpParents.add(parent);
        }
        return helpParents;
    }

    /**
     * 通过id获取类型当前国际化信息
     *
     * @param id
     * @return
     */
    public String getTypeName(Integer id) {
        String typeName = "";
        List<Map<String, String>> list = JsonTool.fromJson(Cache.getHelpTypeAndDocument().get(id.toString()).getHelpTypeNameJson(), List.class);
        String local = SessionManager.getLocale().toString();
        for (Map<String, String> map : list) {
            if (map.get("language").equals(local)) {
                typeName = map.get("name");
            }
        }
        return typeName;
    }

    /**
     * 在帮助类型和文档缓存中拿出 i18n信息
     *
     * @return
     */
    private List<Map<String, String>> getTypeI18n(List<VHelpTypeAndDocument> helpTypeAndDocumentList) {
        List<Map<String, String>> typeList;
        List<Map<String, String>> typeI18nList = new ArrayList<>();
        String local = SessionManager.getLocale().toString();
        for (VHelpTypeAndDocument document : helpTypeAndDocumentList) {
            typeList = JsonTool.fromJson(document.getHelpTypeNameJson(), List.class);
            Map<String, String> documentMap = new HashMap<>();
            for (Map<String, String> map : typeList) {
                if (map.get("language").equals(local)) {
                    documentMap.put("id", document.getId().toString());
                    documentMap.put("name", map.get("name"));
                }
            }
            documentMap.put("ids", document.getDocumentIdJson());
            typeI18nList.add(documentMap);
        }
        return typeI18nList;
    }

    //关于我们
    private AboutApp setAbout(HttpServletRequest request) {
        CttDocumentI18nListVo listVo = initDocument("aboutUs");
        CttDocumentI18n cttDocumentI18n = ServiceSiteTool.cttDocumentI18nService().queryAboutDocument(listVo);
        if (cttDocumentI18n != null) {
            cttDocumentI18n.setContent(cttDocumentI18n.getContent().replaceAll("\\$\\{weburl\\}", request.getServerName()));
        }
        AboutApp about = new AboutApp();
        about.setTitle(cttDocumentI18n.getTitle());
        about.setContent(cttDocumentI18n.getContent());
        about.setContentDefault(cttDocumentI18n.getContentDefault());
        return about;
    }

    private CttDocumentI18nListVo initDocument(String code) {
        CttDocumentI18nListVo listVo = new CttDocumentI18nListVo();
        listVo.getSearch().setStatus(CttDocumentEnum.ON.getCode());
        listVo.getSearch().setCode(code);
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        return listVo;
    }

    /**
     * 获取轮播图和手机弹窗广告
     *
     * @param map
     * @param request
     */
    private void getBannerAndPhoneDialog(Map map, HttpServletRequest request) {
        Map<String, Map> carouselMap = (Map) Cache.getSiteCarousel();
        if (MapTool.isEmpty(carouselMap)) {
            return;
        }
        String webSite = ServletTool.getDomainFullAddress(request);
        List<Map> phoneDialog = new ArrayList<>();
        List<Map> carousels = new ArrayList<>();
        String phoneDialogType = CttCarouselTypeEnum.CAROUSEL_TYPE_PHONE_DIALOG.getCode();
        String bannerType = CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode();
        Date date = new Date();
        String local = SessionManager.getLocale().toString();
        for (Map m : carouselMap.values()) {
            if ((StringTool.equals(m.get(CttCarouselI18n.PROP_LANGUAGE).toString(), local))
                    && (((Date) m.get("start_time")).before(date) && ((Date) m.get("end_time")).after(date))
                    && (MapTool.getBoolean(m, "status") == null || MapTool.getBoolean(m, "status") == true)) {
                String link = MapTool.getString(m, "link");
                if (StringTool.isNotBlank(link)) {
                    if (link.contains("${website}")) {
                        link = link.replace("${website}", webSite);
                    }
                }
                m.put("link", link);
                String cover = m.get("cover") == null ? "" : m.get("cover").toString();
                cover = getImagePath(ServletTool.getDomainFullAddress(request), cover);
                m.put("cover", cover);
                if (phoneDialogType.equals(m.get("type"))) {
                    phoneDialog.add(m);
                } else if (bannerType.equals(m.get("type"))) {
                    carousels.add(m);
                }
            }
        }
        //手机弹窗广告
        map.put("phoneDialog", phoneDialog);
        //没数据默认banner图
        if (carousels.size() <= 0) {
            Map defaultMap = new HashMap();
            String coverUrl = String.format(AppConstant.DEFAULT_BANNER_URL, MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()));
            defaultMap.put("cover", coverUrl);
            carousels.add(defaultMap);
        }
        map.put("banner", carousels);
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
        appFloatPicItem.setNormalEffect(getImagePath(ServletTool.getDomainFullAddress(request), cttFloatPicItem.getNormalEffect()));
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
        Date justNow = SessionManager.getDate().getNow();
        PlayerActivityMessage playerActivityMessage;
        while (iter.hasNext()) {
            String key = iter.next();
            if (key.endsWith(lang)) {
                playerActivityMessage = activityMessages.get(key);
                if (!ActivityTypeEnum.MONEY.getCode().equals(playerActivityMessage.getCode())) {
                    //不是红包活动继续
                    continue;
                }
                if (!playerActivityMessage.getIsDisplay()) {
                    continue;
                }
                Date startTime = playerActivityMessage.getStartTime();
                Date endTime = playerActivityMessage.getEndTime();
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