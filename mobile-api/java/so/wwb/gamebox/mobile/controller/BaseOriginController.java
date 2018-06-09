package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.SystemTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.Paging;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.model.gameapi.result.GameApiResult;
import org.soul.model.gameapi.result.LoginResult;
import org.soul.model.gameapi.result.RegisterResult;
import org.soul.model.gameapi.result.ResultStatus;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.vo.GameVo;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.*;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.GameTypeEnum;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.support.CdnConf;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

/**
 * Created by LeTu on 2017/3/31.
 */
public abstract class BaseOriginController {
    private Log LOG = LogFactory.getLog(BaseOriginController.class);
    private String TRANSFERS_URL = "/transfer/index.html";
    private String API_DETAIL_LINK = "/api/detail.html?apiId=%d&apiTypeId=%d";
    private String API_GAME_LINK = "/game/apiGames.html?apiId=%d&apiTypeId=%d";
    private String AUTO_GAME_LINK = "/mobile-api/origin/getGameLink.html?apiId=%d&apiTypeId=%d";
    private String CASINO_GAME_LINK = "/mobile-api/origin/getCasinoGame.html?search.apiId=%d&search.apiTypeId=%d";
    private String TRANSFER_LINK = "/transfer/index.html?apiId=%d&apiTypeId=%s";

    /**
     * 根据条件筛选游戏
     *
     * @param request
     * @param listVo
     * @param tag
     * @return
     */
    protected Map<String, Object> getGameByApiIdAndApiTypeId(HttpServletRequest request, SiteGameListVo listVo, SiteGameTag tag) {
        SiteGameSo so = listVo.getSearch();
        Integer apiId = so.getApiId();
        Integer apiTypeId = so.getApiTypeId();
        if (apiId == null || apiId <= 0 || apiTypeId == null) {
            return defaultCasinoGameMap();
        }
        List<Integer> gameIds = null;
        if (tag != null && StringTool.isNotBlank(tag.getTagId())) { //判断是否符合游戏标签
            gameIds = getGamesByTagId(tag.getTagId());
            if (CollectionTool.isEmpty(gameIds)) { //无符合标签的游戏
                return defaultCasinoGameMap();
            }
        }

        Map<String, LinkedHashMap<String, GameCacheEntity>> gameGroupByApiMap = Cache.getMobileGameCacheEntity(String.valueOf(apiTypeId));
        LinkedHashMap<String, GameCacheEntity> gameCacheMap = gameGroupByApiMap.get(String.valueOf(apiId));
        if (MapTool.isEmpty(gameCacheMap)) {
            return defaultCasinoGameMap();
        }
        Paging paging = listVo.getPaging();
        int pageSize = paging.getPageSize();
        int pageNum = paging.getPageNumber();
        int fromIndex = (pageNum - 1) * pageSize;
        int endIndex = pageSize * pageNum;
        List<AppSiteGame> appGames = new ArrayList<>();
        int i = 0;
        String domain = ServletTool.getDomainFullAddress(request);
        boolean isAutoPay = SessionManager.isAutoPay();
        Integer siteId = CommonContext.get().getSiteId();
        //筛选的条件 游戏名称
        String name = so.getName();
        for (GameCacheEntity game : gameCacheMap.values()) {
            //不符合游戏
            if (StringTool.isNotBlank(name) && !game.getName().contains(name)) {
                continue;
            }
            if (i < fromIndex) {
                i++;
                continue;
            }
            if (i >= endIndex) {
                break;
            }
            i++;
            appGames.add(changeGameToApp(game, domain, isAutoPay, siteId));
        }
        int totalCount = appGames.size();
        paging.setTotalCount(totalCount);
        //处理游戏结果
        Map<String, Object> map = new HashMap<>(2, 1f);
        map.put("casinoGames", appGames);
        Map<String, Object> pageTotal = new HashMap<>(1, 1f);
        //总数
        pageTotal.put("pageTotal", totalCount);
        map.put("page", pageTotal);
        return map;
    }

    private Map<String, Object> defaultCasinoGameMap() {
        Map<String, Object> map = new HashMap<>(2, 1f);
        map.put("casinoGames", new ArrayList<>(0));
        Map<String, Object> pageTotal = new HashMap<>(1, 1f);
        //总数
        pageTotal.put("pageTotal", 0);
        map.put("page", pageTotal);
        return map;
    }

    private AppSiteGame changeGameToApp(GameCacheEntity game, String domain, boolean isAutoPay, Integer siteId) {
        AppSiteGame casinoGame = new AppSiteGame();
        casinoGame.setGameId(game.getGameId());
        casinoGame.setSiteId(siteId);
        casinoGame.setApiId(game.getApiId());
        casinoGame.setGameType(game.getGameType());
        casinoGame.setStatus(game.getStatus());
        casinoGame.setApiTypeId(game.getApiTypeId());
        casinoGame.setCode(game.getCode());
        casinoGame.setName(game.getName());
        casinoGame.setCover(ImageTag.getImagePath(domain, game.getCover()));
        casinoGame.setSystemStatus(game.getStatus());
        casinoGame.setGameLink(getCasinoGameRequestUrl(game.getApiTypeId(), game.getApiId(), game.getGameId(), game.getCode()));
        casinoGame.setAutoPay(isAutoPay);
        return casinoGame;
    }

    /**
     * 根据tagid取游戏
     *
     * @param tagId
     * @return
     */
    private List<Integer> getGamesByTagId(String tagId) {
        Map<String, SiteGameTag> gameTagMap = Cache.getSiteGameTag();
        List<Integer> gameIds = new ArrayList<>();
        for (SiteGameTag gameTag : gameTagMap.values()) {
            if (StringTool.equalsIgnoreCase(tagId, gameTag.getTagId())) {
                gameIds.add(gameTag.getGameId());
            }
        }
        return gameIds;
    }

    /**
     * 获取游戏标签
     *
     * @return
     */
    protected List<AppGameTag> getGameTag(SiteGameListVo listVo) {
        List<AppGameTag> gameTags = new ArrayList<>();
        if (listVo.getSearch().getApiId() == null || listVo.getSearch().getApiTypeId() == null) {
            return gameTags;
        }

        SiteGameTagVo tagVo = new SiteGameTagVo();
        tagVo.setApiId(listVo.getSearch().getApiId());
        tagVo.setApiTypeId(listVo.getSearch().getApiTypeId());
        tagVo.getSearch().setSiteId(SessionManager.getSiteId());
        List<String> siteTagIds = ServiceSiteTool.siteGameTagService().searchTagId(tagVo);
        Map<String, String> tagNameMap = getTagNameMap();

        for (String tagId : siteTagIds) {
            if (tagId != null && tagNameMap.get(tagId) != null) {
                AppGameTag appGameTag = new AppGameTag();
                appGameTag.setKey(tagId);
                appGameTag.setValue(tagNameMap.get(tagId));
                gameTags.add(appGameTag);
            }
        }
        return gameTags;
    }

    /**
     * 组装游戏标签国际化<tagId,tagName>
     *
     * @return
     */
    private Map<String, String> getTagNameMap() {
        Map<String, SiteI18n> siteI18nMap = Cache.getSiteI18n(SiteI18nEnum.MASTER_GAME_TAG, Const.BOSS_DATASOURCE_ID);
        if (MapTool.isEmpty(siteI18nMap)) {
            return new HashMap<>(0);
        }
        Map<String, String> tagNameMap = new HashMap<>();
        String locale = String.valueOf(SessionManager.getLocale());
        for (SiteI18n siteI18n : siteI18nMap.values()) {
            if (locale.equals(siteI18n.getLocale())) {
                tagNameMap.put(siteI18n.getKey(), siteI18n.getValue());
            }
        }
        return tagNameMap;
    }

    /**
     * 获取电子游戏请求路径
     *
     * @param apiTypeId
     * @param apiId
     * @param gameId
     * @param code
     * @return
     */
    private String getCasinoGameRequestUrl(Integer apiTypeId, Integer apiId, Integer gameId, String code) {
        //kg需进入大厅，不支持直接进入游戏
        StringBuilder sb = new StringBuilder();
        if (ApiTypeEnum.CASINO.getCode() == apiTypeId) {
            sb.append(String.format(AUTO_GAME_LINK, apiId, apiTypeId));
            if (gameId != null && NumberTool.toInt(ApiProviderEnum.KG.getCode()) != apiId) {
                sb.append("&gameId=").append(gameId);
            }
            if (StringTool.isNotBlank(code) && NumberTool.toInt(ApiProviderEnum.KG.getCode()) != apiId) {
                sb.append("&gameCode=").append(code);
            }
        } else {
            if (SessionManager.isAutoPay()) {
                sb.append(String.format(AUTO_GAME_LINK, apiId, apiTypeId));
                if (NumberTool.toInt(ApiProviderEnum.BBIN.getCode()) != apiId && NumberTool.toInt(ApiProviderEnum.KG.getCode()) != apiId && gameId != null) {
                    sb.append("&gameId=").append(gameId);
                }
                if (NumberTool.toInt(ApiProviderEnum.BBIN.getCode()) != apiId && NumberTool.toInt(ApiProviderEnum.KG.getCode()) != apiId && StringTool.isNotBlank(code)) {
                    sb.append("&gameCode=").append(code);
                }
            } else {
                sb.append(String.format(API_DETAIL_LINK, apiId, apiTypeId));
            }
        }
        return sb.toString();
    }

    //获取API类型
    protected List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_MOBILE_ORDER_NUM));
    }

    /**
     * 获取游戏类型api
     *
     * @param model
     * @param request
     * @return
     */
    protected List<AppSiteApiTypeRelastionVo> getApiTypeGames(AppRequestModelVo model, HttpServletRequest request) {
        Map<String, ApiTypeCacheEntity> apiType = Cache.getMobileSiteApiTypes();
        Map<String, LinkedHashMap<String, ApiCacheEntity>> apiCacheMap = Cache.getMobileApiCacheEntity();
        String cdnUrl = new CdnConf().getCndUrl();
        String gameCover = cdnUrl + String.format(AppConstant.GAME_COVER_URL, model.getTerminal(), model.getResolution(), SessionManager.getLocale().toString());
        String apiLogoUrl = setApiLogoUrl(model, request);
        List<AppSiteApiTypeRelastionVo> appApiTypes = changeToAppSiteApiRelation(apiCacheMap, apiLogoUrl, apiType, gameCover, cdnUrl);

        //处理捕鱼数据
        Map<String, GameCacheEntity> fishGameMap = Cache.getMobileFishGameCache();
        StringBuffer fishName;
        List<AppSiteGame> fishGames = new ArrayList<>();
        for (GameCacheEntity game : fishGameMap.values()) {
            game.setCover(MessageFormat.format(gameCover, game.getApiId(), game.getCode()));
            //捕鱼游戏名称=api名称 + 游戏名称
            fishName = new StringBuffer();
            fishName.append(ApiProviderEnum.getApiProviderEnumByCode(String.valueOf(game.getApiId()))).append(" ").append(game.getName());
            game.setName(fishName.toString());
            fishGames.add(changeSiteGameToApp(game));
        }
        if (CollectionTool.isNotEmpty(fishGames)) {
            AppSiteApiTypeRelastionVo fish = new AppSiteApiTypeRelastionVo();
            fish.setApiType(FISH_API_TYPE_ID);
            fish.setCover(setApiLogoUrl(model, request) + "/fish.png");
            fish.setLevel(true);
            fish.setApiTypeName(LocaleTool.tranDict(DictEnum.GAME_TYPE, GameTypeEnum.FISH.getCode()));
            AppSiteApiTypeRelationI18n appSiteApiTypeRelationI18n = new AppSiteApiTypeRelationI18n();
            appSiteApiTypeRelationI18n.setApiId(FISH_API_TYPE_ID);
            appSiteApiTypeRelationI18n.setGameList(fishGames);
            List<AppSiteApiTypeRelationI18n> appApis = new ArrayList<>(1);
            appApis.add(appSiteApiTypeRelationI18n);
            fish.setSiteApis(appApis);
            appApiTypes.add(fish);
        }

        return appApiTypes;
    }

    /**
     * 取api缓存数据转换到app原生api
     *
     * @param apiCacheMap
     * @param apiLogoUrl
     * @return
     */
    private List<AppSiteApiTypeRelastionVo> changeToAppSiteApiRelation(Map<String, LinkedHashMap<String, ApiCacheEntity>> apiCacheMap,
                                                                       String apiLogoUrl,
                                                                       Map<String, ApiTypeCacheEntity> apiType,
                                                                       String gameCover, String cdnUrl) {
        List<AppSiteApiTypeRelastionVo> appApiTypes = new ArrayList<>();
        List<AppSiteApiTypeRelationI18n> appApis;
        Integer apiTypeId;
        List<Integer> navApiTypes = navApiTypes();
        Map<String, LinkedHashMap<String, GameCacheEntity>> gameMap;
        Map<String, GameCacheEntity> apiGameMap;
        List<AppSiteGame> appSiteGames;
        LinkedHashMap<String, ApiCacheEntity> apiMap;
        AppSiteApiTypeRelastionVo appApiType;
        AppSiteApiTypeRelationI18n appSite;
        int sportType = ApiTypeEnum.SPORTS_BOOK.getCode();
        int bb = NumberTool.toInt(ApiProviderEnum.BBIN.getCode());
        String apiTypeLogoUrl = cdnUrl + API_TYPE_LOGO_URL;
        for (ApiTypeCacheEntity apiTypeCacheEntity : apiType.values()) {
            apiTypeId = apiTypeCacheEntity.getApiTypeId();
            apiMap = apiCacheMap.get(String.valueOf(apiTypeCacheEntity.getApiTypeId()));
            if (MapTool.isEmpty(apiMap)) {
                continue;
            }

            appApis = new ArrayList<>();
            for (ApiCacheEntity apiCacheEntity : apiMap.values()) {
                //BB体育不展示
                if (apiTypeId == sportType && apiCacheEntity.getApiId() == bb) {
                    continue;
                }
                appSite = changeApiTypeRelationI18nModelToApp(apiCacheEntity, apiLogoUrl);
                //彩票，棋牌
                if (navApiTypes.contains(apiTypeId)) {
                    gameMap = Cache.getMobileGameCacheEntity(String.valueOf(apiTypeId));
                    apiGameMap = gameMap.get(String.valueOf(apiCacheEntity.getApiId()));
                    if (MapTool.isNotEmpty(apiGameMap)) {
                        appSiteGames = new ArrayList<>();
                        for (GameCacheEntity game : apiGameMap.values()) {
                            game.setCover(MessageFormat.format(gameCover, game.getApiId(), game.getCode()));
                            appSiteGames.add(changeSiteGameToApp(game));
                        }
                        appSite.setGameList(appSiteGames);
                        appApis.add(appSite);
                    }
                } else {
                    appApis.add(appSite);
                }
            }
            //转换实体提供给app原生
            appApiType = new AppSiteApiTypeRelastionVo();
            appApiType.setApiType(apiTypeId);
            appApiType.setApiTypeName(apiTypeCacheEntity.getName());
            appApiType.setCover(String.format(apiTypeLogoUrl, apiLogoUrl, apiTypeId));
            appApiType.setSiteApis(appApis);
            if (navApiTypes.contains(apiTypeId)) {
                appApiType.setLevel(true);
            }
            appApiTypes.add(appApiType);
        }

        return appApiTypes;
    }

    /**
     * 转换彩票,棋牌实体到原生app
     *
     * @return
     */
    private AppSiteGame changeSiteGameToApp(GameCacheEntity game) {
        AppSiteGame appSiteGame = new AppSiteGame();
        appSiteGame.setGameId(game.getGameId());
        appSiteGame.setApiId(game.getApiId());
        appSiteGame.setGameType(game.getGameType());
        //appSiteGame.setOrderNum(siteGame.getOrderNum());
        appSiteGame.setStatus(game.getStatus());
        appSiteGame.setApiTypeId(game.getApiTypeId());
        appSiteGame.setCode(game.getCode());
        appSiteGame.setName(game.getName());
        appSiteGame.setCover(game.getCover());
        appSiteGame.setSystemStatus(game.getStatus());
        appSiteGame.setGameLink(getCasinoGameRequestUrl(game.getApiTypeId(), game.getApiId(), game.getGameId(), game.getCode()));
        appSiteGame.setAutoPay(SessionManager.isAutoPay());
        return appSiteGame;
    }

    /**
     * 转换apiTypeRelation到原生实体
     *
     * @param apiEntity
     * @return
     */
    private AppSiteApiTypeRelationI18n changeApiTypeRelationI18nModelToApp(ApiCacheEntity apiEntity, String apiLogoUrl) {
        AppSiteApiTypeRelationI18n appRelation = new AppSiteApiTypeRelationI18n();
        appRelation.setName(apiEntity.getRelationName());
        appRelation.setApiId(apiEntity.getApiId());
        appRelation.setApiTypeId(apiEntity.getApiTypeId());
        List<Integer> siteIds = API_SITE_SPECIAL.get(apiEntity.getApiId());
        if (siteIds != null && siteIds.contains(SessionManager.getSiteId())) {
            appRelation.setCover(String.format(AppConstant.API_LOGO_URL, apiLogoUrl, apiEntity.getApiId() + "_site" + SessionManager.getSiteId()));
        } else {
            appRelation.setCover(String.format(AppConstant.API_LOGO_URL, apiLogoUrl, apiEntity.getApiId()));
        }
        appRelation.setGameLink(getAutoPayGameLink(apiEntity));

        appRelation.setAutoPay(SessionManager.isAutoPay());
        return appRelation;
    }

    /**
     * 设置api链接地址
     *
     * @return
     */
    private String getAutoPayGameLink(ApiCacheEntity apiEntity) {
        if (ApiTypeEnum.CASINO.getCode() == apiEntity.getApiTypeId()) {
            return String.format(CASINO_GAME_LINK, apiEntity.getApiId(), apiEntity.getApiTypeId());
        } else if (SessionManager.isAutoPay()) {
            return String.format(AUTO_GAME_LINK, apiEntity.getApiId(), apiEntity.getApiTypeId());
        } else {
            return String.format(API_DETAIL_LINK, apiEntity.getApiId(), apiEntity.getApiTypeId());
        }
    }

    /**
     * 获取api类型图片
     *
     * @param apiLogUrl
     * @param apiTypeId
     * @return
     */
    private String getApiTypeCover(String apiLogUrl, Integer apiTypeId) {
        StringBuffer coverBuffer = new StringBuffer();
        coverBuffer.append(apiLogUrl).append("/api_type_");
        coverBuffer.append(apiTypeId).append(".png");
        return coverBuffer.toString();
    }

    /**
     * 获取要展示两层游戏的类型
     *
     * @return
     */
    private List<Integer> navApiTypes() {
        List<Integer> apiTypes = new ArrayList<>();
        apiTypes.add(ApiTypeEnum.LOTTERY.getCode());
        apiTypes.add(ApiTypeEnum.CHESS.getCode());
        return apiTypes;
    }

    /**
     * 设置api图片路径
     *
     * @param model
     * @return
     */
    private String setApiLogoUrl(AppRequestModelVo model, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()));
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            sb.append("/android/themes");
        }
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            sb.append("/ios/themes");
        }
        sb.append("/").append(model.getTheme()).append("/images");
        sb.append("/").append(model.getResolution());
        return sb.toString();
    }

    /**
     * 获取游戏注册或登录地址
     *
     * @param request
     * @param apiId
     * @param apiTypeId
     * @param gameCode
     * @param model
     * @return
     */
    protected AppSiteApiTypeRelationI18n goGameUrl(HttpServletRequest request, Integer apiId, String apiTypeId, String gameCode, AppRequestModelVo model) {
        AppSiteApiTypeRelationI18n appI18n = new AppSiteApiTypeRelationI18n();
        PlayerApiAccountVo playerApiAccountVo = new PlayerApiAccountVo();
        playerApiAccountVo.setApiId(apiId);
        playerApiAccountVo.setApiTypeId(apiTypeId);
        if (StringTool.isNotBlank(gameCode)) {
            playerApiAccountVo.setGameCode(gameCode);
        }

        playerApiAccountVo = getPlayerApiAccount(request, playerApiAccountVo);
        String msg = isAllowLogin(playerApiAccountVo);
        if (StringTool.isNotBlank(msg)) {
            appI18n.setGameMsg(msg);
            appI18n.setGameLink("");
            return appI18n;
        }

        DemoModelEnum demoModel = SessionManagerCommon.getDemoModelEnum();
        if (demoModel != null) {
            //平台试玩免转不可用 //纯彩票试玩免转不可用 //模拟账号免转可用
            playerApiAccountVo.setTrial(true);
            if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel)
                    && apiId != null
                    && (StringTool.equals(apiId.toString(), ApiProviderEnum.PL.getCode()) || StringTool.equals(apiId.toString(), ApiProviderEnum.DWT.getCode()))) {
                playerApiAccountVo.setTrial(false);
            }
        }

        playerApiAccountVo = ServiceSiteTool.freeTranferServcice().autoTransferLogin(playerApiAccountVo);
        if (playerApiAccountVo == null || playerApiAccountVo.getGameApiResult() == null || ResultStatus.SUCCESS != playerApiAccountVo.getGameApiResult().getStatus()) {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_LOGIN_ERROR, Module.Passport.getCode()));
            return appI18n;
        } else if (StringTool.isNotBlank(playerApiAccountVo.getErrMsg())) {
            appI18n.setGameMsg(playerApiAccountVo.getErrMsg());
            return appI18n;
        }

        GameApiResult gameApiResult = playerApiAccountVo.getGameApiResult();
        String url = (gameApiResult instanceof RegisterResult) ?
                ((RegisterResult) gameApiResult).getDefaultLink() : ((LoginResult) gameApiResult).getDefaultLink();
        appI18n.setGameLink(url);

        return appI18n;
    }

    /**
     * 获取游戏地址后
     * 根据安卓和ios构建游戏地址路径
     *
     * @param url
     * @param model
     * @param apiId
     * @return
     */
    private String buildGameUrl(String url, AppRequestModelVo model, Integer apiId) {
        if (StringTool.isBlank(url) || apiId == null) {
            return url;
        }
        if (url.indexOf("?") > 0) {
            url = url + "&ad=" + apiId;
        } else {
            url = url + "?ad=" + apiId;
        }
        return url;
    }

    /**
     * 电子游戏地址
     *
     * @param playerApiAccountVo
     * @param request
     */
    protected AppSiteApiTypeRelationI18n getCasinoGameUrl(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request, AppRequestModelVo model) {
        AppSiteApiTypeRelationI18n appI18n = new AppSiteApiTypeRelationI18n();
        setAccount(playerApiAccountVo, request);

        if (checkApiStatus(playerApiAccountVo)) {
            DemoModelEnum demoModel = SessionManagerCommon.getDemoModelEnum();
            if (demoModel != null) {
                //平台试玩免转不可用
                //纯彩票试玩免转不可用
                playerApiAccountVo.setTrial(true);
                //Integer apiId = playerApiAccountVo.getApiId();
                if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel)
                        && (StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.PL.getCode())
                        || StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.DWT.getCode()))) {
                    //模拟账号免转可用
                    playerApiAccountVo.setTrial(false);
                }
            }
            playerApiAccountVo = ServiceSiteTool.playerApiAccountService().loginApi(playerApiAccountVo);
        } else {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode()));
            return appI18n;
        }
        if (StringTool.isNotBlank(playerApiAccountVo.getErrMsg())) {
            appI18n.setGameMsg(playerApiAccountVo.getErrMsg());
            return appI18n;
        }
        GameApiResult gameApiResult = playerApiAccountVo.getGameApiResult();
        if (gameApiResult == null) {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode()));
            return appI18n;
        }
        String url = (gameApiResult instanceof RegisterResult) ?
                ((RegisterResult) gameApiResult).getDefaultLink() : ((LoginResult) gameApiResult).getDefaultLink();
        appI18n.setGameLink(url);
        return appI18n;
    }

    protected void setAccount(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        Integer apiId = playerApiAccountVo.getApiId();

        StringBuilder domain = new StringBuilder();
        domain.append(request.getServerName());
        if (!domain.toString().contains("http")) {
            domain.insert(0, "http://");
        }

        String transferUrl = domain + String.format(TRANSFER_LINK, apiId, playerApiAccountVo.getApiTypeId());
        playerApiAccountVo.setTransfersUrl(transferUrl);

        playerApiAccountVo.setLobbyUrl(domain.toString());
        if (request.getHeader("User-Agent").contains(AppTypeEnum.APP_ANDROID.getCode())) {
            playerApiAccountVo.setLobbyUrl("javascript:window.gb.finish()");
        }

        playerApiAccountVo.setSysUser(SessionManager.getUser());
        if (StringTool.isNotBlank(playerApiAccountVo.getGameCode())) {
            GameVo gameVo = new GameVo();
            gameVo.getSearch().setApiId(apiId);
            gameVo.getSearch().setCode(playerApiAccountVo.getGameCode());
            gameVo.getSearch().setSupportTerminal(SessionManagerCommon.getTerminal(request));
            gameVo = ServiceTool.gameService().search(gameVo);
            if (gameVo.getResult() != null) {
                playerApiAccountVo.setGameId(gameVo.getResult().getId());
                playerApiAccountVo.setPlatformType(gameVo.getResult().getSupportTerminal());
            }
        }
        playerApiAccountVo.setPlatformType(TerminalEnum.MOBILE.getCode());
    }

    protected boolean checkApiStatus(PlayerApiAccountVo playerApiAccountVo) {
        Integer apiId = playerApiAccountVo.getApiId();
        if (apiId == null) {
            return false;
        }
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api = apiMap.get(apiId.toString());
        SiteApi siteApi = siteApiMap.get(apiId.toString());
        if (api == null || siteApi == null) {
            return false;
        }
        return isAllowToLogin(api, siteApi);
    }

    protected boolean isAllowToLogin(Api api, SiteApi siteApi) {
        if (GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(api.getSystemStatus()))
            return false;
        return !(GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(siteApi.getSystemStatus()));
    }

    /**
     * 玩家信息
     *
     * @param request
     * @param playerApiVo
     * @return
     */
    private PlayerApiAccountVo getPlayerApiAccount(HttpServletRequest request, PlayerApiAccountVo playerApiVo) {
        String domain = request.getServerName();
        if (SystemTool.isDebug()) {
            domain = domain + "/mobile";
        }
        playerApiVo.setLobbyUrl(domain);
        playerApiVo.setTransfersUrl(domain + TRANSFERS_URL);
        playerApiVo.setSysUser(SessionManager.getUser());
        playerApiVo.setPlatformType(SessionManager.getTerminal(request));
        return playerApiVo;
    }

    private String isAllowLogin(PlayerApiAccountVo playerApiAccountVo) {
        String msg = "";
        playerApiAccountVo.setDemoModel(SessionManagerCommon.getDemoModelEnum());
        boolean mockAccount = checkMockAccount(playerApiAccountVo);

        if (!mockAccount) {
            LOG.info("判断是否可以登录游戏：{1}_{2},mockAccount:{0}", mockAccount, SessionManagerBase.getSiteId(), playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.MOCKMODEL_NOT_ALLOW_LOGIN, Module.Passport.getCode());
        }

        //api是否允许登陆
        boolean isAllowApi = checkApiStatus(playerApiAccountVo.getApiId());
        if (!isAllowApi) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowApi:{0}", isAllowApi, SessionManagerBase.getSiteId(), playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode());
        }

        //游戏是否允许登陆
        boolean isAllowGame = isAllowGame(playerApiAccountVo);
        if (!isAllowGame) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowGame:{0}", isAllowGame, SessionManagerBase.getSiteId(), playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.NOT_GAME_LOGIN, Module.Passport.getCode());
        }

        boolean demeModelIsAllowLogin = isAllowDemoModelLogin(playerApiAccountVo);
        if (!demeModelIsAllowLogin) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowDemoModelLogin:{0}", demeModelIsAllowLogin, SessionManagerBase.getSiteId(), playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.DEMOMODEL_NOT_ALLOW_LOGIN, Module.Passport.getCode());
        }
        return msg;
    }

    /**
     * 试玩模式是否可以登录
     *
     * @param playerApiAccountVo
     * @return
     */
    protected boolean isAllowDemoModelLogin(PlayerApiAccountVo playerApiAccountVo) {
        DemoModelEnum demoModel = playerApiAccountVo.getDemoModel();
        if (demoModel == null) {
            return true;
        }
        //checkMockAccount已经验证
        if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel)) {
            if (playerApiAccountVo.getApiId() != null && (StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.PL.getCode())
                    || StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.DWT.getCode()))) {
                return true;
            }
        }
        //彩票试玩，如果是龙头彩票，就可以玩
        if (DemoModelEnum.MODEL_4_LOTTERY.equals(demoModel)) {
            if (playerApiAccountVo.getApiId() != null && StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.PL.getCode())) {
                return true;
            }
        }
        GameVo gameVo = fetchLoginGame(playerApiAccountVo);
        if (gameVo.getResult() != null) {
            Boolean canTry = gameVo.getResult().getCanTry();
            LOG.info("判断是否可以登录游戏：{1}_{2},canTry:{0}", canTry, SessionManagerBase.getSiteId(), playerApiAccountVo.getGameCode());
            if (canTry == null || !canTry) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否允许登陆的游戏
     *
     * @param playerApiAccountVo
     * @return
     */
    private boolean isAllowGame(PlayerApiAccountVo playerApiAccountVo) {
        //若无指定游戏code，跳转游戏大厅
        if (StringTool.isBlank(playerApiAccountVo.getGameCode())) {
            return true;
        }
        GameVo gameVo = fetchLoginGame(playerApiAccountVo);
        if (gameVo.getResult() == null || GameStatusEnum.DISABLE.getCode().equals(gameVo.getResult().getSystemStatus())) { //尚未接入该游戏或已停用
            return false;
        }
        playerApiAccountVo.setGameId(gameVo.getResult().getId());
        Map<String, SiteGame> siteGameMap = Cache.getSiteGame();
        SiteGame siteGame = siteGameMap.get(String.valueOf(gameVo.getResult().getId()));
        if (siteGame == null || GameStatusEnum.DISABLE.getCode().equals(siteGame.getStatus())) {//站点未接入该游戏或者已停用
            return false;
        }
        playerApiAccountVo.setGameId(gameVo.getResult().getId());
        return true;
    }

    private GameVo fetchLoginGame(PlayerApiAccountVo playerApiAccountVo) {
        GameVo gameVo = new GameVo();
        gameVo.getSearch().setApiId(playerApiAccountVo.getApiId());
        gameVo.getSearch().setSupportTerminal(TerminalEnum.MOBILE.getCode());
        gameVo.getSearch().setCode(playerApiAccountVo.getGameCode());
        gameVo.getSearch().setStatusArray(new String[]{GameStatusEnum.NORMAL.getCode(), GameStatusEnum.MAINTAIN.getCode()});
        gameVo = ServiceTool.gameService().search(gameVo);
        return gameVo;
    }

    /**
     * 判断api状态，判断是否允许登陆
     *
     * @param apiId
     * @return
     */
    private boolean checkApiStatus(Integer apiId) {
        if (apiId == null) {
            return false;
        }
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api = apiMap.get(apiId.toString());
        SiteApi siteApi = siteApiMap.get(apiId.toString());
        if (api == null || siteApi == null) {
            return false;
        }
        if (GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(api.getSystemStatus()))
            return false;
        return !(GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(siteApi.getSystemStatus()));
    }

    private boolean checkMockAccount(PlayerApiAccountVo playerApiAccountVo) {
        DemoModelEnum demoModelEnum = playerApiAccountVo.getDemoModel();
        if (demoModelEnum != null) {
            //模拟账号只能登录自主彩票及自主体育
            if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModelEnum)) {
                if (playerApiAccountVo.getApiId() != null
                        && !StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.PL.getCode())
                        && !StringTool.equals(playerApiAccountVo.getApiId().toString(), ApiProviderEnum.DWT.getCode())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取国际化信息
     *
     * @param msgConst
     * @param code
     * @return
     */
    private String setMsg(String msgConst, String code) {
        return LocaleTool.tranMessage(code, msgConst);
    }
}
