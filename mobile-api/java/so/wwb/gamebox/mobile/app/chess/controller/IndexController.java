package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.controller.BaseOriginController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.company.site.vo.ApiTypeCacheEntity;
import so.wwb.gamebox.model.company.site.vo.GameCacheEntity;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.enums.CttCarouselTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;


/**
 * 棋牌包网首页控制器
 */
@Controller
@RequestMapping("/chess")
public class IndexController extends BaseOriginController {
    private Log LOG = LogFactory.getLog(IndexController.class);
    private static final String ACTIVITY_DETAIL_URL = "/promo/promoDetail.html";

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
        getBannerAndPhoneDialog(map, request, CttCarouselTypeEnum.CAROUSEL_TYPE_PHONE_DIALOG);//获取轮播图和手机弹窗广告
        map.remove("phoneDialog");
        map.put("language", SessionManager.getLocale().toString());
        //以下获取游戏.先获取棋牌游戏,再获取其他入口.入口里面包含API
        String chessApiTypeStr = String.valueOf(ApiTypeEnum.CHESS.getCode());
        List<String> excludeApis = new ArrayList<>();
        excludeApis.add(StringTool.join(JOIN_CHAR, String.valueOf(ApiTypeEnum.SPORTS_BOOK.getCode()), ApiProviderEnum.BBIN.getCode()));
        //获取apiType不包含CHESS，包含FISH
        Collection<ApiTypeCacheEntity> apiTypes = getApiType(new Integer[]{ApiTypeEnum.CHESS.getCode()}, true);
        List<SiteApiRelationApp> gamesByApiTypes = getGamesByApiTypes(request, model, apiTypes, excludeApis);

        Map<String, GameCacheEntity> mobileGameByApiType = getNotEmptyMap(Cache.getMobileGameByApiType(chessApiTypeStr), new LinkedHashMap());

        gamesByApiTypes.addAll(rechangeGameEntity(mobileGameByApiType.values(), excludeApis,
                String.format(CHESS_GAME_IMG_PATH, model.getResolution(), SessionManager.getLocale().toString(), STR_PLACEHOLDER)));

        convertLiveImg(gamesByApiTypes, request, null);
        Collections.sort(gamesByApiTypes);
        map.put("siteApiRelation", gamesByApiTypes);

        return AppModelVo.getAppModeVoJsonUseFastjson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 获取分享二维码图片
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/getShareQRCode")
    @ResponseBody
    public String getShareQRCode(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = new HashMap<>();
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.CHESS_SHARE_PICTURE);
        String qrCodeUrl = "";
        if (sysParam != null) {
            qrCodeUrl = StringTool.isNotBlank(sysParam.getParamValue()) ? sysParam.getParamValue() : sysParam.getDefaultValue();
        }
        map.put("qrCodeUrl", ImageTag.getImagePath(ServletTool.getDomainFullAddress(request), qrCodeUrl));
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 棋牌包网,真人图片特殊处理
     *
     * @param siteApiRelationApps
     */
    private void convertLiveImg(List<SiteApiRelationApp> siteApiRelationApps, HttpServletRequest request, Integer apiTypeid) {
        for (SiteApiRelationApp game : siteApiRelationApps) {
            if (ApiTypeEnum.LIVE_DEALER.getCode() == game.getApiTypeId()) {
                String cover = game.getCover();
                cover = cover.replaceAll("livedealer", "live");//棋牌包网真人图片名称更改
                game.setCover(cover);
                //如果是真人api,则强制设置为游戏类型
                if ("api".equals(game.getType())) {
                    game.setType("game");
                    game.setRelation(new ArrayList<>());
                    game.setGameLink(getCasinoGameRequestUrl(game.getApiTypeId(), game.getApiId(), game.getGameId(), game.getCode()));
                }
            }
            //递归执行替换
            if (CollectionTool.isNotEmpty(game.getRelation())) {
                if (apiTypeid != null) {
                    convertLiveImg(game.getRelation(), request, apiTypeid);
                } else {
                    convertLiveImg(game.getRelation(), request, game.getApiTypeId());
                }

            } else {
                //如果没有下个层级,则直接为game:比如申博API是直接进入游戏大厅的.
                //电子和真人,第三层游戏图标更换
                if ("game".equals(game.getType())) {
                    if (apiTypeid!=null && ApiTypeEnum.CASINO.getCode() == apiTypeid) {
                        game.setCover(ImageTag.getImagePath(ServletTool.getDomainFullAddress(request), game.getGameConver()));
                    }
                }
            }
        }
    }

    /**
     * 获取优惠活动和类型
     */
    @RequestMapping(value = "/getActivitysAndTypes")
    @ResponseBody
    public String getActivityTypes(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo, request), APP_VERSION);
    }

    /**
     * 获取活动和类型
     */
    private List<ActivityTypeApp> getActivityTypeMessages(VActivityMessageListVo listVo, HttpServletRequest request) {
        List<ActivityTypeApp> types = new ArrayList<>();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        if (StringTool.isNotBlank(activityClassifyKey)) {
            ActivityTypeApp typeApp = new ActivityTypeApp();
            typeApp.setActivityKey(activityClassifyKey);
            types.add(typeApp);
        } else {
            types = getActivityTypes();
        }
        List<ActivityTypeApp> result = new ArrayList<>();

        Map<String, Object> activityTypes = new HashMap<>(types.size(), 1f);
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getMobileActivityMessages();
        String domain = ServletTool.getDomainFullAddress(request);
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            List<ActivityTypeListApp> messages = getActivityMessages(listVo, activityMessageMap, domain);
            ActivityTypeApp activityTypeApp = new ActivityTypeApp();
            activityTypeApp.setActivityKey(type.getActivityKey());
            activityTypeApp.setActivityTypeName(type.getActivityTypeName());
            activityTypeApp.setActivityList(messages);
            result.add(activityTypeApp);
        }
        return result;
    }

    /**
     * 获取活动类型
     *
     * @return types
     */
    private List<ActivityTypeApp> getActivityTypes() {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        List<ActivityTypeApp> types = new ArrayList<>();
        for (SiteI18n site : siteI18nMap.values()) {
            if (StringTool.equalsIgnoreCase(site.getLocale(), SessionManager.getLocale().toString())) {
                ActivityTypeApp type = new ActivityTypeApp();
                type.setActivityKey(site.getKey());
                type.setActivityTypeName(site.getValue());
                types.add(type);
            }
        }
        return types;
    }

    /**
     * 获取正在进行中的活动 转换接口数据
     */
    private List<ActivityTypeListApp> getActivityMessages(VActivityMessageListVo listVo, Map<String, PlayerActivityMessage> activityMessageMap, String domain) {
        Map<String, Object> map = new HashMap<>(2, 1f);
        if (MapTool.isEmpty(activityMessageMap)) {
            return new ArrayList<>(0);
        }
        String locale = SessionManager.getLocale().toString();
        Integer rankId = null;
        if (SessionManager.getUser() != null && !SessionManager.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            PlayerRank playerRank = ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
            if (playerRank != null) {
                rankId = playerRank.getId();
            }
        }
        long nowTime = SessionManager.getDate().getNow().getTime();
        boolean isDisplay;
        boolean isDelete;
        boolean isAllRank;
        boolean hasRank;
        String release = ActivityStateEnum.RELEASE.getCode();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        List<ActivityTypeListApp> activityTypeListApps = new ArrayList<>();
        for (PlayerActivityMessage playerActivityMessage : activityMessageMap.values()) {
            isDisplay = playerActivityMessage.getIsDisplay() != null && playerActivityMessage.getIsDisplay();
            isDelete = playerActivityMessage.getIsDeleted() != null && playerActivityMessage.getIsDeleted();
            if (release.equals(playerActivityMessage.getActivityState()) && locale.equals(playerActivityMessage.getActivityVersion()) && !isDelete && isDisplay && (StringTool.isBlank(activityClassifyKey) || activityClassifyKey.equals(playerActivityMessage.getActivityClassifyKey())) && playerActivityMessage.getEndTime().getTime() > nowTime) {
                isAllRank = playerActivityMessage.getAllRank() != null && playerActivityMessage.getAllRank();
                hasRank = true;
                if (rankId != null && !isAllRank && playerActivityMessage.getRankid() != null && !playerActivityMessage.getRankid().contains(rankId + ",")
                        && playerActivityMessage.getRankid() != null && !playerActivityMessage.getRankid().contains("," + rankId)) {
                    hasRank = false;
                }
                if (hasRank) {
                    ActivityTypeListApp activityApp = new ActivityTypeListApp();
                    activityApp.setId(playerActivityMessage.getId());
                    activityApp.setPhoto(ImageTag.getImagePath(domain, playerActivityMessage.getActivityAffiliated() == null ? playerActivityMessage.getActivityCover() : playerActivityMessage.getActivityAffiliated()));
                    activityApp.setUrl(ACTIVITY_DETAIL_URL + "?searchId=" + listVo.getSearchId(playerActivityMessage.getId()));
                    activityApp.setName(playerActivityMessage.getActivityName());
                    activityApp.setStatus(playerActivityMessage.getStates());
                    activityTypeListApps.add(activityApp);
                }
            }
        }

        return activityTypeListApps;
    }
}


