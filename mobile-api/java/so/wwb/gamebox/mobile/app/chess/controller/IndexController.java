package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.DateTool;
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
                    if (apiTypeid != null && ApiTypeEnum.CASINO.getCode() == apiTypeid) {
                        game.setCover(ImageTag.getImagePath(ServletTool.getDomainFullAddress(request), game.getGameConver()));
                    }
                }
            }
        }
    }

    /**
     * 获取优惠活动类型和标题
     */
    @RequestMapping(value = "/getActivityTypes")
    @ResponseBody
    public String getActivityTypes(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo, request), APP_VERSION);
    }

    /**
     * 获取优惠活动信息
     */
    @RequestMapping(value = "/getActivityById")
    @ResponseBody
    public String getActivityById(VActivityMessageListVo listVo, HttpServletRequest request) {
        if(listVo.getSearch() == null || listVo.getSearch().getId() == null){
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.ACTIVITY_ID_EMPTY.getCode(), AppErrorCodeEnum.ACTIVITY_ID_EMPTY.getMsg(),null , APP_VERSION);
        }
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getMobileActivityMessages();
        List<PlayerActivityMessage> activityList = getActivityMessages(listVo, activityMessageMap);
        String domain = ServletTool.getDomainFullAddress(request);
        ActivityTypeListApp activityTypeListApp = new ActivityTypeListApp();
        if (CollectionTool.isNotEmpty(activityList)) {
            PlayerActivityMessage playerActivityMessage = activityList.get(0);
            activityTypeListApp.setId(playerActivityMessage.getId());
            activityTypeListApp.setPhoto(ImageTag.getImagePath(domain, playerActivityMessage.getActivityAffiliated() == null ? playerActivityMessage.getActivityCover() : playerActivityMessage.getActivityAffiliated()));
            activityTypeListApp.setName(playerActivityMessage.getActivityName());
            activityTypeListApp.setStatus(playerActivityMessage.getStates());
            activityTypeListApp.setExplain(playerActivityMessage.getActivityDescription());
            activityTypeListApp.setTime(DateTool.formatDate(playerActivityMessage.getStartTime(),DateTool.yyyy_MM_dd_HH_mm_ss) +" —— "+ DateTool.formatDate(playerActivityMessage.getEndTime(),DateTool.yyyy_MM_dd_HH_mm_ss));
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(),activityTypeListApp , APP_VERSION);
        }
       return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.ACTIVITY_IS_INVALID.getCode(), AppErrorCodeEnum.ACTIVITY_IS_INVALID.getMsg(),null , APP_VERSION);
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
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getMobileActivityMessages();
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            List<PlayerActivityMessage> activityList = getActivityMessages(listVo, activityMessageMap);

            if (CollectionTool.isNotEmpty(activityList)) {
                List<AppSimpleModel> activitys = new ArrayList<>();
                for (PlayerActivityMessage playerActivityMessage : activityList) {
                    AppSimpleModel appSimpleModel = new AppSimpleModel();
                    appSimpleModel.setCode(String.valueOf(playerActivityMessage.getId()));
                    appSimpleModel.setName(playerActivityMessage.getActivityName());
                    activitys.add(appSimpleModel);
                }
                type.setActivityList(activitys);
                result.add(type);
            }
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
     * 筛选出正在进行中的活动
     */
    private List<PlayerActivityMessage> getActivityMessages(VActivityMessageListVo listVo, Map<String, PlayerActivityMessage> activityMessageMap) {
        List<PlayerActivityMessage> result = new ArrayList<>();
        if (MapTool.isEmpty(activityMessageMap)) {
            return result;
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
        String activityId = listVo.getSearch().getId() == null ? null : String.valueOf(listVo.getSearch().getId());

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
                if (hasRank && (StringTool.isEmpty(activityId) || activityId.equals(String.valueOf(playerActivityMessage.getId())))) {
                    result.add(playerActivityMessage);
                }
            }
        }

        return result;
    }
}


