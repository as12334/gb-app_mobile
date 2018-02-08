package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.lang.SystemTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
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
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.*;
import so.wwb.gamebox.model.company.setting.vo.GameVo;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.GameTypeEnum;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.soul.web.tag.ImageTag.getImagePath;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.FISH_API_TYPE_ID;
import static so.wwb.gamebox.model.CacheBase.getSiteGameName;

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
    private String TRANSFER_LINK = "/transfer/index.html?apiId=%d&apiTypeId=%d";

    protected List<SiteGameI18n> getGameI18n(SiteGameListVo listVo) {
        List<Integer> gameIds = CollectionTool.extractToList(listVo.getResult(), SiteGame.PROP_GAME_ID);
        Criteria cGameIds = Criteria.add(SiteGameI18n.PROP_GAME_ID, Operator.IN, gameIds);
        Criteria local = Criteria.add(SiteGameI18n.PROP_LOCAL, Operator.EQ, SessionManager.getLocale().toString());
        String name = listVo.getSearch().getName();
        try {
            if (StringTool.isNotBlank(name)) {
                name = new String(name.getBytes("ISO-8859-1"), "UTF-8");
            }
        } catch (Exception e) {
            LOG.error(e, e.getMessage());
        }
        Criteria cName = Criteria.add(SiteGameI18n.PROP_NAME, Operator.ILIKE, name);

        List<SiteGameI18n> gameI18ns = CollectionQueryTool.query(Cache.getSiteGameI18n().values(), Criteria.and(cGameIds, local, cName));

        if (listVo.getSearch().getApiId() != null && StringTool.equals(listVo.getSearch().getApiId().toString(), ApiProviderEnum.PL.getCode())) {
            List<SiteGameI18n> plGames = new ArrayList<>();
            for (SiteGameI18n game : gameI18ns) {
                if (StringTool.isNotBlank(game.getCover())) {
                    plGames.add(game);
                }
            }
            return plGames;
        } else {
            return gameI18ns;
        }
    }

    protected SiteGameListVo getCasinoGames(SiteGameListVo listVo, SiteGameTag tag) {
        SiteGameSo so = listVo.getSearch();
        Integer apiId = so.getApiId();
        Integer apiTypeId = so.getApiTypeId();
        if (apiId == null || apiId <= 0 || apiTypeId == null) {
            return listVo;
        }
        List<Integer> gameIds = null;
        if (tag != null && StringTool.isNotBlank(tag.getTagId())) { //判断是否符合游戏标签
            gameIds = getGamesByTagId(tag.getTagId());
            if (CollectionTool.isEmpty(gameIds)) { //无符合标签的游戏
                return listVo;
            }
        }
        //筛选的条件 类型、apiId、游戏标签、游戏名称
        String name = so.getName();
        Map<String, SiteGame> siteGameMap = Cache.getSiteGame();
        List<SiteGame> siteGames = new ArrayList<>();
        Integer gameId;
        Map<String, SiteGameI18n> siteGameI18nMap = new HashMap<>();
        Map<String, GameI18n> gameI18nMap = new HashMap<>();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        Map<String, Game> gameMap = Cache.getGame();
        Game game;
        for (SiteGame siteGame : siteGameMap.values()) {
            //不属于该api分类下不包含
            if (apiId != siteGame.getApiId() || apiTypeId != siteGame.getApiTypeId()) {
                continue;
            }
            //游戏维护或已停用不包含
            if (disable.equals(siteGame.getSystemStatus()) || maintain.equals(siteGame.getSystemStatus())) {
                continue;
            }
            gameId = siteGame.getGameId();
            game = gameMap.get(String.valueOf(gameId));
            if (game == null || disable.equals(game.getSystemStatus()) || maintain.equals(game.getSystemStatus())) {
                continue;
            }
            if (gameIds != null && !gameIds.contains(gameId)) {  //搜索条件不符合游戏标签不包含
                continue;
            }
            siteGame.setName(getGameName(siteGameI18nMap, gameI18nMap, String.valueOf(gameId)));
            if (StringTool.isNotBlank(name) && !siteGame.getName().contains(name)) {
                continue;
            }
            siteGames.add(siteGame);
        }
        if (CollectionTool.isEmpty(siteGames)) {
            return listVo;
        }
        int totalCount = siteGames.size();
        Paging paging = listVo.getPaging();
        paging.setTotalCount(siteGames.size());
        int pageSize = paging.getPageSize();
        int pageNum = paging.getPageNumber();
        int fromIndex = (pageNum - 1) * pageSize;
        int endIndex = pageSize * pageNum;
        if (fromIndex > totalCount) {
            return listVo;
        }
        if (endIndex > totalCount) {
            endIndex = totalCount;
        }
        siteGames = siteGames.subList(fromIndex, endIndex);
        listVo.setResult(siteGames);
        return listVo;
    }

    /**
     * 获取游戏名称
     *
     * @param siteGameI18nMap
     * @param gameI18nMap
     * @param gameId
     * @return
     */
    private String getGameName(Map<String, SiteGameI18n> siteGameI18nMap, Map<String, GameI18n> gameI18nMap, String gameId) {
        SiteGameI18n siteGameI18n = siteGameI18nMap.get(gameId);
        String gameName = null;
        if (siteGameI18n != null) {
            gameName = siteGameI18n.getName();
        }
        if (StringTool.isNotBlank(gameName)) {
            return gameName;
        }
        GameI18n gameI18n = gameI18nMap.get(gameId);
        if (gameI18n != null) {
            gameName = gameI18n.getName();
        }
        if (StringTool.isBlank(gameName)) { //避免比较游戏名称出现空指针异常
            return "";
        }
        return gameName;
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

    protected List<AppSiteGame> handleCasinoGames(List<SiteGame> siteGames, String domain, boolean isAutoPay) {
        if (CollectionTool.isEmpty(siteGames)) {
            return new ArrayList<>(0);
        }
        List<AppSiteGame> appSiteGames = new ArrayList<>();
        AppSiteGame casinoGame;
        for (SiteGame siteGame : siteGames) {
            casinoGame = new AppSiteGame();
            casinoGame.setGameId(siteGame.getGameId());
            casinoGame.setSiteId(siteGame.getSiteId());
            casinoGame.setApiId(siteGame.getApiId());
            casinoGame.setGameType(siteGame.getGameType());
            casinoGame.setOrderNum(siteGame.getOrderNum());
            casinoGame.setStatus(siteGame.getStatus());
            casinoGame.setApiTypeId(siteGame.getApiTypeId());
            casinoGame.setCode(siteGame.getCode());
            casinoGame.setName(getSiteGameName(siteGame.getGameId().toString()));
            casinoGame.setCover(getImagePath(domain, siteGame.getCover()));
            casinoGame.setSystemStatus(siteGame.getSystemStatus());
            casinoGame.setGameLink(getCasinoGameRequestUrl(siteGame));
            casinoGame.setAutoPay(isAutoPay);
            appSiteGames.add(casinoGame);
        }
        return appSiteGames;
    }

    /**
     * 获取游戏标签
     *
     * @return
     */
    protected List<AppGameTag> getGameTag() {
        Map<String, SiteGameTag> siteGameTag = Cache.getSiteGameTag();
        List<String> tags = new ArrayList<>();
        Map<String, SiteI18n> siteI18n = Cache.getSiteI18n(SiteI18nEnum.MASTER_GAME_TAG, Const.BOSS_DATASOURCE_ID);
        List<AppGameTag> gameTags = ListTool.newArrayList();
        for (SiteGameTag tag : siteGameTag.values()) {
            if (!tags.contains(tag.getTagId())) {
                for (SiteI18n site : siteI18n.values()) {
                    if (StringTool.equalsIgnoreCase(tag.getTagId(), site.getKey())
                            && StringTool.equalsIgnoreCase(site.getLocale().toString(), SessionManager.getLocale().toString())) {
                        AppGameTag appGameTag = new AppGameTag();
                        appGameTag.setKey(site.getKey());
                        appGameTag.setValue(site.getValue());
                        gameTags.add(appGameTag);
                    }
                }
                tags.add(tag.getTagId());
            }
        }

        return gameTags;
    }

    /**
     * 获取电子游戏请求路劲
     *
     * @param siteGame
     * @return
     */
    private String getCasinoGameRequestUrl(SiteGame siteGame) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(AUTO_GAME_LINK, siteGame.getApiId(), siteGame.getApiTypeId()));
        if (siteGame.getGameId() != null) {
            sb.append("&gameId=").append(siteGame.getGameId());
        }
        if (StringTool.isNotBlank(siteGame.getCode())) {
            sb.append("&gameCode=").append(siteGame.getCode());
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
     * @param
     */
    protected List<AppSiteApiTypeRelastionVo> getApiTypeGame(AppRequestModelVo model, HttpServletRequest request) {
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        //需要有二级分类的游戏分类
        List<Integer> navApiTypes = navApiTypes();
        //获取游戏分类下的游戏
        Map<Integer, Map<Integer, List<AppSiteGame>>> navGames = getNavGames(model, apiI18nMap, siteApiI18nMap, navApiTypes);
        //获取类型
        List<SiteApiType> siteApiTypes = getApiTypes();
        Map<String, SiteApiTypeRelation> siteApiTypeRelationMap = CacheBase.siteApiTypeRelationMap(SessionManager.getSiteId());
        String apiLogoUrl = setApiLogoUrl(model, request);
        Map<Integer, List<AppSiteApiTypeRelationI18n>> apiTypeRelationGroupByType = apiTypeRelationGroupByType(siteApiTypeRelationMap, apiI18nMap, siteApiI18nMap, apiLogoUrl, navGames);
        Map<String, ApiTypeI18n> apiTypeI18nMap = CacheBase.getApiTypeI18n();
        List<AppSiteApiTypeRelastionVo> appApiTypes = new ArrayList<>();
        String apiLogUrl = setApiLogoUrl(model, request);
        Integer apiTypeId;
        for (SiteApiType siteApiType : siteApiTypes) {
            //转换实体提供给app原生
            AppSiteApiTypeRelastionVo appApiType = new AppSiteApiTypeRelastionVo();
            apiTypeId = siteApiType.getApiTypeId();
            appApiType.setApiType(apiTypeId);
            appApiType.setApiTypeName(apiTypeI18nMap.get(String.valueOf(siteApiType.getApiTypeId())).getName());
            appApiType.setCover(getApiTypeCover(apiLogUrl, apiTypeId));
            appApiType.setSiteApis(CollectionQueryTool.sort(apiTypeRelationGroupByType.get(siteApiType.getApiTypeId()), Order.asc(AppSiteApiTypeRelationI18n.PROP_ORDER_NUM)));
            if (navApiTypes.contains(apiTypeId)) {
                appApiType.setLevel(true);
            }
            appApiTypes.add(appApiType);
        }
        List<AppSiteGame> fishGames = navGames.get(FISH_API_TYPE_ID).get(FISH_API_TYPE_ID);
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

    private String getApiTypeCover(String apiLogUrl, Integer apiTypeId) {
        StringBuffer coverBuffer = new StringBuffer();
        coverBuffer.append(apiLogUrl).append("/api_type_");
        coverBuffer.append(apiTypeId).append(".png");
        return coverBuffer.toString();
    }

    /**
     * 对棋牌,彩票siteGame游戏分组
     *
     * @return
     */
    private Map<Integer, Map<Integer, List<AppSiteGame>>> getNavGames(AppRequestModelVo model, Map<String, ApiI18n> apiI18nMap, Map<String, SiteApiI18n> siteApiI18nMap, List<Integer> navApiTypes) {
        //处理棋牌、彩票游戏
        Map<String, SiteGame> siteGameMap = CacheBase.getSiteGame();
        //捕鱼
        String fishGameType = GameTypeEnum.FISH.getCode();
        Integer apiId;
        Map<String, SiteGameI18n> siteGameI18nMap = CacheBase.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = CacheBase.getGameI18n();
        Map<String, Game> gameMap = CacheBase.getGame();
        String disabled = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        String normal = GameStatusEnum.NORMAL.getCode();
        Game game;
        String mobile = GameSupportTerminalEnum.PHONE.getCode();
        //分类游戏 <apiTypeId,<apiId,游戏>>
        Map<Integer, Map<Integer, List<AppSiteGame>>> navGames = new HashMap<>();
        List<AppSiteGame> fishGames = new ArrayList<>();
        String gameCover = String.format(AppConstant.GAME_COVER_URL, model.getTerminal(), model.getResolution(), SessionManager.getLocale().toString());
        Integer apiTypeId;
        AppSiteGame appSiteGame;
        Map<Integer, List<AppSiteGame>> apiGameMap;
        String apiName;
        StringBuffer fishName;
        for (SiteGame siteGame : siteGameMap.values()) {
            apiId = siteGame.getApiId();
            game = gameMap.get(String.valueOf(siteGame.getGameId()));
            //非手机端游戏、已下架游戏、已维护游戏过滤
            if (!mobile.equals(siteGame.getSupportTerminal()) || game == null || disabled.equals(game.getStatus()) || disabled.equals(siteGame.getStatus()) || maintain.equals(game.getSystemStatus()) || maintain.equals(siteGame.getSystemStatus())) {
                continue;
            }
            game.setStatus(normal);
            setGameName(siteGameI18nMap, gameI18nMap, siteGame);
            siteGame.setCover(MessageFormat.format(gameCover, apiId, siteGame.getCode()));
            apiTypeId = siteGame.getApiTypeId();
            if (navApiTypes.contains(apiTypeId)) {
                appSiteGame = changeSiteGameToApp(siteGame);
                apiGameMap = navGames.get(apiTypeId);
                if (apiGameMap == null) {
                    apiGameMap = new HashMap<>();
                }
                if (apiGameMap.get(apiId) == null) {
                    apiGameMap.put(apiId, new ArrayList<>());
                }
                apiGameMap.get(apiId).add(appSiteGame);
            } else if (fishGameType.equals(siteGame.getGameType())) {
                //捕鱼游戏名称=api名称 + 游戏名称
                fishName = new StringBuffer();
                fishName.append(ApiProviderEnum.getApiProviderEnumByCode(String.valueOf(apiId))).append(" ").append(siteGame.getName());
                siteGame.setName(fishName.toString());
                appSiteGame = changeSiteGameToApp(siteGame);
                fishGames.add(appSiteGame);
            }
        }
        navGames.put(FISH_API_TYPE_ID, new HashMap<>());
        navGames.get(FISH_API_TYPE_ID).put(FISH_API_TYPE_ID, fishGames);
        return navGames;
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
     * 转换彩票,棋牌实体到原生app
     *
     * @param siteGame
     * @return
     */
    private AppSiteGame changeSiteGameToApp(SiteGame siteGame) {
        AppSiteGame appSiteGame = new AppSiteGame();
        appSiteGame.setGameId(siteGame.getGameId());
        appSiteGame.setApiId(siteGame.getApiId());
        appSiteGame.setGameType(siteGame.getGameType());
        appSiteGame.setOrderNum(siteGame.getOrderNum());
        appSiteGame.setStatus(siteGame.getStatus());
        appSiteGame.setApiTypeId(siteGame.getApiTypeId());
        appSiteGame.setCode(siteGame.getCode());
        appSiteGame.setName(siteGame.getName());
        appSiteGame.setCover(siteGame.getCover());
        appSiteGame.setSystemStatus(siteGame.getSystemStatus());
        if (SessionManager.isAutoPay()) {
            appSiteGame.setGameLink(getCasinoGameRequestUrl(siteGame));
        } else {
            appSiteGame.setGameLink(String.format(API_DETAIL_LINK, siteGame.getApiId(), siteGame.getApiTypeId()));
        }
        appSiteGame.setAutoPay(SessionManager.isAutoPay());
        return appSiteGame;
    }

    /**
     * 设置游戏名称和图片地址
     *
     * @param siteGameI18nMap
     * @param gameI18nMap
     * @param siteGame
     */
    private void setGameName(Map<String, SiteGameI18n> siteGameI18nMap, Map<String, GameI18n> gameI18nMap, SiteGame siteGame) {
        Integer gameId = siteGame.getGameId();
        SiteGameI18n siteGameI18n = siteGameI18nMap.get(String.valueOf(gameId));
        String gameName = null;
        GameI18n gameI18n = gameI18nMap.get(String.valueOf(gameId));
        if (siteGameI18n != null && StringTool.isNotBlank(siteGameI18n.getName())) {
            gameName = siteGameI18n.getName();
        } else if (gameI18n != null && StringTool.isNotBlank(gameI18n.getName())) {
            gameName = gameI18n.getName();
        }
        siteGame.setName(gameName);
    }

    /**
     * 获取api名称
     *
     * @param apiId
     * @param apiI18nMap
     * @param siteApiI18nMap
     * @return
     */
    private String getApiName(Integer apiId, Map<String, ApiI18n> apiI18nMap, Map<String, SiteApiI18n> siteApiI18nMap) {
        String apiName = "";
        SiteApiI18n siteApiI18n = siteApiI18nMap.get(String.valueOf(apiId));
        ApiI18n apiI18n = apiI18nMap.get(String.valueOf(apiId));
        if (siteApiI18n != null && StringTool.isNotBlank(siteApiI18n.getName())) {
            apiName = siteApiI18n.getName();
        } else if (apiI18n != null && StringTool.isNotBlank(apiI18n.getName())) {
            apiName = apiI18n.getName();
        }
        return apiName;
    }

    /**
     * 处理分类
     *
     * @param siteApiTypeRelationMap
     * @param apiI18nMap
     * @param siteApiI18nMap
     * @param navGames
     * @return
     */
    private Map<Integer, List<AppSiteApiTypeRelationI18n>> apiTypeRelationGroupByType(Map<String, SiteApiTypeRelation> siteApiTypeRelationMap, Map<String, ApiI18n> apiI18nMap,
                                                                                      Map<String, SiteApiI18n> siteApiI18nMap, String apiLogoUrl, Map<Integer, Map<Integer, List<AppSiteGame>>> navGames) {
        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelationI18nMap = Cache.getSiteApiTypeRelactionI18n();
        Map<Integer, Map<Integer, String>> map = new HashMap<>(siteApiI18nMap.size());
        Integer siteId = SessionManager.getSiteId();
        Integer apiTypeId;
        Integer apiId;
        for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nMap.values()) {
            if (siteId.equals(siteApiTypeRelationI18n.getSiteId())) {
                apiTypeId = siteApiTypeRelationI18n.getApiTypeId();
                apiId = siteApiTypeRelationI18n.getApiId();
                if (map.get(apiTypeId) == null) {
                    map.put(apiTypeId, new HashMap<>());
                }
                map.get(apiTypeId).put(apiId, siteApiTypeRelationI18n.getName());
            }
        }

        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api;
        SiteApi siteApi;
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        String preMaintain = GameStatusEnum.PRE_MAINTAIN.getCode();
        String normal = GameStatusEnum.NORMAL.getCode();
        String disabled = GameStatusEnum.DISABLE.getCode();
        Map<Integer, List<AppSiteApiTypeRelationI18n>> apiTypeRelationGroupByType = new HashMap<>();
        String apiName;
        Map<Integer, List<AppSiteGame>> navApiGameMap;
        for (SiteApiTypeRelation apiTypeRelation : siteApiTypeRelationMap.values()) {
            apiTypeId = apiTypeRelation.getApiTypeId();
            apiId = apiTypeRelation.getApiId();
            api = apiMap.get(String.valueOf(apiId));
            siteApi = siteApiMap.get(String.valueOf(apiId));
            if (api == null || siteApi == null || disabled.equals(api.getSystemStatus()) || disabled.equals(siteApi.getSystemStatus())) {
                continue;
            }
            apiName = MapTool.getString(map.get(apiTypeId), apiId);
            if (StringTool.isBlank(apiName)) {
                apiName = getApiName(apiId, apiI18nMap, siteApiI18nMap);
            }
            apiTypeRelation.setApiName(apiName);

            if (maintain.equals(api.getSystemStatus()) || maintain.equals(siteApi.getSystemStatus())) {
                apiTypeRelation.setApiStatus(maintain);
            } else if ((normal.equals(api.getSystemStatus()) || preMaintain.equals(api.getSystemStatus())) && (normal.equals(siteApi.getSystemStatus()) || preMaintain.equals(siteApi.getSystemStatus()))) {
                apiTypeRelation.setApiStatus(normal);
            }
            if (apiTypeRelationGroupByType.get(apiTypeId) == null) {
                apiTypeRelationGroupByType.put(apiTypeId, new ArrayList<>());
            }
            AppSiteApiTypeRelationI18n appSiteApiTypeRelationI18n;
            navApiGameMap = navGames.get(apiTypeId);
            if (navApiGameMap == null) {
                appSiteApiTypeRelationI18n = changeApiTypeRelationI18nModelToApp(apiTypeRelation, apiLogoUrl, null);
            } else {
                appSiteApiTypeRelationI18n = changeApiTypeRelationI18nModelToApp(apiTypeRelation, apiLogoUrl, navApiGameMap.get(apiId));
            }
            apiTypeRelationGroupByType.get(apiTypeId).add(appSiteApiTypeRelationI18n);
        }
        return apiTypeRelationGroupByType;
    }

    /**
     * 转换apiTypeRelation给原生app
     *
     * @param apiTypeRelation
     * @return
     */
    private AppSiteApiTypeRelationI18n changeApiTypeRelationI18nModelToApp(SiteApiTypeRelation apiTypeRelation, String apiLogoUrl, List<AppSiteGame> games) {
        //转换实体提供给app原生
        AppSiteApiTypeRelationI18n appRelation = new AppSiteApiTypeRelationI18n();
        appRelation.setName(apiTypeRelation.getApiName());
        appRelation.setApiId(apiTypeRelation.getApiId());
        appRelation.setApiTypeId(apiTypeRelation.getApiTypeId());
        appRelation.setCover(String.format(AppConstant.API_LOGO_URL, apiLogoUrl, apiTypeRelation.getApiId()));
        appRelation.setGameLink(getAutoPayGameLink(apiTypeRelation));
        appRelation.setOrderNum(apiTypeRelation.getMobileOrderNum());
        appRelation.setAutoPay(SessionManager.isAutoPay());
        if (CollectionTool.isNotEmpty(games)) {
            appRelation.setGameList(CollectionQueryTool.sort(games, Order.asc(AppSiteGame.PROP_ORDER_NUM)));
        }
        return appRelation;
    }

    /**
     * 设置api链接地址
     *
     * @param siteApi
     * @return
     */
    private String getAutoPayGameLink(SiteApiTypeRelation siteApi) {
        if (SessionManager.getUser() != null && siteApi.getApiTypeId() != ApiTypeEnum.LOTTERY.getCode()) {
            if (siteApi.getApiId() != null && StringTool.equals(siteApi.getApiId().toString(), ApiProviderEnum.BSG.getCode())) {
                return String.format(API_GAME_LINK, siteApi.getApiId(), siteApi.getApiTypeId());
            } else if (SessionManager.isAutoPay() && siteApi.getApiTypeId() != null && siteApi.getApiTypeId().intValue() != ApiTypeEnum.CASINO.getCode()) {
                return String.format(AUTO_GAME_LINK, siteApi.getApiId(), siteApi.getApiTypeId());
            } else if (ApiTypeEnum.CASINO.getCode() == siteApi.getApiTypeId()) {
                return String.format(CASINO_GAME_LINK, siteApi.getApiId(), siteApi.getApiTypeId());
            } else {
                return String.format(API_DETAIL_LINK, siteApi.getApiId(), siteApi.getApiTypeId());
            }
        }
        return "";
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
            appI18n.setGameLink("");
            return appI18n;
        }

        GameApiResult gameApiResult = playerApiAccountVo.getGameApiResult();
        if (gameApiResult == null) {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode()));
            return appI18n;
        }

        String url = (gameApiResult instanceof RegisterResult) ?
                ((RegisterResult) gameApiResult).getDefaultLink() : ((LoginResult) gameApiResult).getDefaultLink();
        url = buildGameUrl(url, model, apiId);
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
        if (StringTool.isBlank(url)) {
            return url;
        }
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            if (apiId != null && StringTool.equals(apiId.toString(), ApiProviderEnum.PL.getCode())) {
                url = url + "?ad=" + apiId;
            }
        }

        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            if (apiId != null
                    && StringTool.equals(apiId.toString(), ApiProviderEnum.PL.getCode())
                    && url.indexOf("/mainIndex") == -1
                    && url.indexOf("/lottery/") == -1) {
                url = url + "mainIndex.html?ad=22";
            } else {
                if (url.indexOf("?") > 0) {
                    url = url + "&ad=" + apiId;
                } else {
                    url = url + "?ad=" + apiId;
                }
            }
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
            appI18n.setGameLink("");
        }

        GameApiResult gameApiResult = playerApiAccountVo.getGameApiResult();
        if (gameApiResult == null) {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode()));
            return appI18n;
        }
        String url = (gameApiResult instanceof RegisterResult) ?
                ((RegisterResult) gameApiResult).getDefaultLink() : ((LoginResult) gameApiResult).getDefaultLink();
        url = buildGameUrl(url, model, playerApiAccountVo.getApiId());
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
            gameVo.getSearch().setSupportTerminal(GameSupportTerminalEnum.PHONE.getCode());
            gameVo = ServiceTool.gameService().search(gameVo);
            if (gameVo.getResult() != null) {
                playerApiAccountVo.setGameId(gameVo.getResult().getId());
                playerApiAccountVo.setPlatformType(gameVo.getResult().getSupportTerminal());
            }
        }
        playerApiAccountVo.setPlatformType(SupportTerminal.PHONE.getCode());
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
        playerApiVo.setPlatformType(SupportTerminal.PHONE.getCode());
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
        gameVo.getSearch().setSupportTerminal(playerApiAccountVo.getPlatformType());
        gameVo.getSearch().setCode(playerApiAccountVo.getGameCode());
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
