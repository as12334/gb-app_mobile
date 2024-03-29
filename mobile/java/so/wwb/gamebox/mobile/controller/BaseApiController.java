package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.model.gameapi.base.PlatformTypeEnum;
import org.springframework.ui.Model;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.common.consts.MobileConst;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ApiGameTool;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.Game;
import so.wwb.gamebox.model.company.setting.po.GameI18n;
import so.wwb.gamebox.model.company.setting.vo.GameVo;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.*;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.GameTypeEnum;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;
import so.wwb.gamebox.web.support.CdnConf;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by LeTu on 2017/3/31.
 */
public abstract class BaseApiController extends BaseDemoController {
    private Log LOG = LogFactory.getLog(BaseApiController.class);

    List<SiteApiRelation> getSiteApiRelation(Integer apiTypeId) {
        SiteApiTypeRelationVo siteApiTypeRelationVo = new SiteApiTypeRelationVo();
        siteApiTypeRelationVo.getSearch().setApiTypeId(apiTypeId);
        siteApiTypeRelationVo.getSearch().setSiteId(SessionManager.getSiteId());
        siteApiTypeRelationVo.setTerminal(SupportTerminal.PC.getCode());
        List<SiteApiRelation> siteApiRelations = ServiceTool.siteApiTypeRelationService().queryApiTypeRelation(siteApiTypeRelationVo);
        return siteApiRelations;
    }

    List<Map<String, Object>> getApiType() {
        List<SiteApiTypeRelationI18n> relationI18ns;
        List<SiteApiTypeRelation> relations = new GameController().getSiteApiTypeRelationList(null);

        List<Relation> relationList = new ArrayList<>();
        relationI18ns = setI18nName();

        //通过查出的手机端API,再找出API的I18N
        for (SiteApiTypeRelationI18n i18n : relationI18ns) {
            for (SiteApiTypeRelation relation : relations) {
                if (relation.getApiId().equals(i18n.getApiId()) && relation.getApiTypeId().equals(i18n.getApiTypeId())) {
                    if (!(relation.getApiId().equals(10) && !i18n.getApiTypeId().equals(1))) {
                        //BBIN只能显示一个
                        Map<String, Object> map = new HashMap<>();
                        map.put("apiTypeRelation", i18n);
                        relationList.add(new Relation(relation.getOrderNum(), map));
                    }
                }
            }
        }

        List<Relation> listRelation = CollectionQueryTool.sort(relationList, Order.asc("order"));

        List<Map<String, Object>> list = new ArrayList<>();
        for (Relation relation : listRelation) {
            list.add(relation.getMap());
        }

        return list;
    }

    private List<SiteApiTypeRelationI18n> setI18nName() {
        List<SiteApiTypeRelationI18n> list = new ArrayList<>();
        Map<String, SiteApiTypeRelationI18n> i18nMap = Cache.getSiteApiTypeRelactionI18n(SessionManager.getSiteId());
        list.addAll(i18nMap.values());
        return list;
    }

    class Relation {
        private Integer order;
        private Map<String, Object> map;

        Relation(Integer order, Map<String, Object> map) {
            this.order = order;
            this.map = map;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }
    }

    /**
     * 设置查询条件
     */
    protected Criteria getQueryGameCriteria(SiteGameSo so, List<SiteGameI18n> siteGameI18n) {
        Criteria criteria = Criteria.add(SiteGame.PROP_API_TYPE_ID, Operator.EQ, so.getApiTypeId())
                .addAnd(Criteria.add(SiteGame.PROP_API_ID, Operator.EQ, so.getApiId()))
                .addAnd(Criteria.add(SiteGame.PROP_TERMINAL, Operator.EQ, SupportTerminal.PHONE.getCode()))
                .addAnd(Criteria.add(SiteGame.PROP_STATUS, Operator.NE, GameStatusEnum.DISABLE.getCode()))
                .addAnd(Criteria.add(SiteGame.PROP_GAME_TYPE, Operator.EQ, so.getGameType()));

        List<Integer> gameIds = CollectionTool.extractToList(siteGameI18n, SiteGame.PROP_GAME_ID);
        if (gameIds != null && gameIds.size() == 0) {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.EQ, 0);
        } else {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        }

        return criteria;
    }

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

    /**
     * 游戏过滤 - 总控录入的游戏status = normal && 站点游戏status = normal
     *
     * @return
     */
    protected List<SiteGame> getSiteGamesWhichIsNormalStatus(List<SiteGame> allSiteGames) {
        List<Game> allGames = new ArrayList<>(Cache.getGame().values());
        // 维护与正常的status都为normal
        allGames = CollectionQueryTool.query(allGames, Criteria.add(SiteGame.PROP_STATUS, Operator.EQ, GameStatusEnum.NORMAL.getCode())
                .addAnd(Game.PROP_SUPPORT_TERMINAL, Operator.EQ, GameSupportTerminalEnum.PHONE.getCode()));
        List<Integer> normalGameIdList = (List<Integer>) CollectionTool.intersection(CollectionTool.extractToList(allGames, Game.PROP_ID), CollectionTool.extractToList(allSiteGames, SiteGame.PROP_GAME_ID));
        if (normalGameIdList.size() != 0) {
            return CollectionQueryTool.query(allSiteGames, Criteria.add(SiteGame.PROP_GAME_ID, Operator.IN, normalGameIdList));
        } else {
            return null;
        }
    }

    /**
     * 设置游戏状态
     */
    protected List<SiteGame> setGameStatus(SiteGameListVo listVo, List<SiteGame> games) {
        List<SiteGame> siteGames = new ArrayList<>();
        Integer apiId = listVo.getSearch().getApiId();
        String status = getApiStatus(apiId);
        String normal = GameStatusEnum.NORMAL.getCode();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        Map<String, Game> gameMap = Cache.getGame();
        String gameStatus;
        for (SiteGame siteGame : games) {
            Game game = gameMap.get(String.valueOf(siteGame.getGameId()));
            gameStatus = siteGame.getStatus();
            if (disable.equals(status) || game == null || disable.equals(gameStatus) || disable.equals(game.getSystemStatus())) {
                siteGame.setStatus(disable);
            } else if (maintain.equals(status) || maintain.equals(gameStatus) || maintain.equals(game.getSystemStatus())) {
                siteGame.setStatus(maintain);
                siteGames.add(siteGame);
            } else {
                siteGame.setStatus(normal);
                siteGames.add(siteGame);
            }
        }
        return siteGames;
    }

    protected String getApiStatus(Integer apiId) {
        Map<String, Api> apiMap = Cache.getApi();
        Api api = apiMap.get(String.valueOf(apiId));
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi(SessionManager.getSiteId());
        SiteApi siteApi = siteApiMap.get(String.valueOf(apiId));
        String status = GameStatusEnum.MAINTAIN.getCode();
        if (api != null && (GameStatusEnum.NORMAL.getCode().equals(api.getSystemStatus()) || GameStatusEnum.PRE_MAINTAIN.getCode().equals(api.getSystemStatus())) && siteApi != null && (GameStatusEnum.NORMAL.getCode().equals(siteApi.getSystemStatus()) || GameStatusEnum.PRE_MAINTAIN.getCode().equals(siteApi.getSystemStatus()))) {
            status = GameStatusEnum.NORMAL.getCode();
        }
        return status;
    }

    //获取API类型
    protected List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_MOBILE_ORDER_NUM));
    }

    protected void getApiTypeGame(Model model) {
        //处理彩票、棋牌游戏
        List<Integer> navType = getNavType();
        Map<String, ApiTypeCacheEntity> apiType = Cache.getMobileSiteApiTypes();
        Map<String, LinkedHashMap<String, ApiCacheEntity>> apiCacheMap = Cache.getMobileApiCacheEntity();
        String locale = SessionManager.getLocale().toString();
        String cdnUrl = new CdnConf().getCndUrl();
        Integer apiTypeId;
        Map<String, LinkedHashMap<String, GameCacheEntity>> gameMap;
        Map<String, GameCacheEntity> apiGameMap;
        List<GameCacheEntity> games;
        ApiCacheEntity apiCacheEntity;
        for (ApiTypeCacheEntity apiTypeCacheEntity : apiType.values()) {
            LinkedHashMap<String, ApiCacheEntity> apiMap = apiCacheMap.get(String.valueOf(apiTypeCacheEntity.getApiTypeId()));
            if (MapTool.isEmpty(apiMap)) {
                continue;
            }
            apiTypeId = apiTypeCacheEntity.getApiTypeId();
            Collection<ApiCacheEntity> apiCacheEntityList = apiMap.values();
            //彩票棋牌
            if (navType.contains(apiTypeId)) {
                gameMap = Cache.getMobileGameCacheEntity(String.valueOf(apiTypeId));
                Iterator<ApiCacheEntity> iterator = apiCacheEntityList.iterator();
                while (iterator.hasNext()) {
                    apiCacheEntity = iterator.next();
                    apiGameMap = gameMap.get(String.valueOf(apiCacheEntity.getApiId()));
                    if (MapTool.isEmpty(apiGameMap)) {
                        iterator.remove();
                        continue;
                    }
                    games = new ArrayList<>();
                    for (GameCacheEntity game : apiGameMap.values()) {
                        game.setCover(cdnUrl + String.format(MobileConst.GAME_COVER_URL, locale, game.getApiId(), game.getCode()));
                        games.add(game);
                    }
                    apiCacheEntity.setGames(games);
                }
                apiTypeCacheEntity.setApis(apiCacheEntityList);
            } else {
                apiTypeCacheEntity.setApis(apiCacheEntityList);
            }
        }
        model.addAttribute("siteApiTypes", apiType.values());
        handleFishGame(model, cdnUrl, locale);
    }

    protected void handleFishGame(Model model, String cdnUrl, String locale) {
        //处理捕鱼数据
        Map<String, GameCacheEntity> fishGameMap = Cache.getMobileFishGameCache();
        StringBuffer fishName;
        List<GameCacheEntity> fishGames = new ArrayList<>();
        for (GameCacheEntity game : fishGameMap.values()) {
            game.setCover(cdnUrl + String.format(MobileConst.GAME_COVER_URL, locale, game.getApiId(), game.getCode()));
            //捕鱼游戏名称=api名称 + 游戏名称
            fishName = new StringBuffer();
            fishName.append(ApiProviderEnum.getApiProviderEnumByCode(String.valueOf(game.getApiId()))).append(" ").append(game.getName());
            game.setName(fishName.toString());
            fishGames.add(game);
        }
        if (MapTool.isNotEmpty(fishGameMap)) {
            model.addAttribute("fishGames", fishGames);
        }
    }

    /**
     * 有nav二级分类的类型
     *
     * @return
     */
    private List<Integer> getNavType() {
        List<Integer> navType = new ArrayList<>(2);
        navType.add(ApiTypeEnum.LOTTERY.getCode());
        navType.add(ApiTypeEnum.CHESS.getCode());
        return navType;
    }

    /**
     * 处理游戏二级分类展示
     *
     * @param model
     */
    protected void handleNavGame(Model model) {
        Map<String, SiteGame> siteGameMap = CacheBase.getSiteGame();
        if (MapTool.isEmpty(siteGameMap)) {
            return;
        }
        List<Integer> navType = getNavType();
        //捕鱼
        List<SiteGame> fish = new ArrayList<>();
        //除捕鱼外的二级分类游戏数据
        Map<Integer, Map<Integer, List<SiteGame>>> navApiGameMap = new HashMap<>();
        Integer apiId;
        Map<String, SiteGameI18n> siteGameI18nMap = CacheBase.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = CacheBase.getGameI18n();
        Map<String, Game> gameMap = CacheBase.getGame();
        String disabled = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        Game game;
        String mobile = GameSupportTerminalEnum.PHONE.getCode();
        String locale = SessionManager.getLocale().toString();
        String fishGameType = GameTypeEnum.FISH.getCode();
        Integer apiTypeId;
        Map<Integer, List<SiteGame>> navGameMap;
        StringBuffer fishName;
        for (SiteGame siteGame : siteGameMap.values()) {
            apiId = siteGame.getApiId();
            game = gameMap.get(String.valueOf(siteGame.getGameId()));
            if (game == null || disabled.equals(game.getStatus()) || disabled.equals(siteGame.getStatus()) || maintain.equals(game.getSystemStatus()) || maintain.equals(siteGame.getSystemStatus())) {
                continue;
            }
            setGameNameAndCover(siteGameI18nMap, gameI18nMap, siteGame, locale);
            apiTypeId = siteGame.getApiTypeId();
            if (fishGameType.equals(siteGame.getGameType()) && mobile.equals(siteGame.getSupportTerminal())) {
                fishName = new StringBuffer();
                fishName.append(ApiProviderEnum.getApiProviderEnumByCode(String.valueOf(apiId))).append(" ").append(siteGame.getName());
                siteGame.setName(fishName.toString());
                fish.add(siteGame);
            } else if (navType.contains(apiTypeId) && mobile.equals(siteGame.getSupportTerminal())) {
                navGameMap = navApiGameMap.get(apiTypeId);
                if (navGameMap == null) {
                    navGameMap = new HashMap<>();
                }
                if (navGameMap.get(apiId) == null) {
                    navGameMap.put(apiId, new ArrayList<>());
                }
                navGameMap.get(apiId).add(siteGame);
                navApiGameMap.put(apiTypeId, navGameMap);
            }
        }
        model.addAttribute("navApiGameMap", navApiGameMap);
        model.addAttribute("fish", fish);
    }

    private void setGameNameAndCover(Map<String, SiteGameI18n> siteGameI18nMap, Map<String, GameI18n> gameI18nMap, SiteGame siteGame, String locale) {
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
        siteGame.setCover(String.format(MobileConst.GAME_COVER_URL, locale, siteGame.getApiId(), siteGame.getCode()));
    }

    /**
     * 处理设置分类下api名称
     *
     * @param siteApiTypeRelationMap
     * @param apiI18nMap
     * @param siteApiI18nMap
     * @return
     */
    private Map<Integer, List<SiteApiTypeRelation>> apiTypeRelationGroupByType(Map<String, SiteApiTypeRelation> siteApiTypeRelationMap, Map<String, ApiI18n> apiI18nMap, Map<String, SiteApiI18n> siteApiI18nMap) {
        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelationI18nMap = Cache.getSiteApiTypeRelactionI18n();
        Map<Integer, Map<Integer, String>> map = ApiGameTool.getSiteApiNameByApiType(siteApiTypeRelationI18nMap);
        Integer apiTypeId;
        Integer apiId;
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api;
        SiteApi siteApi;
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        String preMaintain = GameStatusEnum.PRE_MAINTAIN.getCode();
        String normal = GameStatusEnum.NORMAL.getCode();
        String terminal = GameSupportTerminalEnum.PC.getCode();
        Map<Integer, List<SiteApiTypeRelation>> apiTypeRelationGroupByType = new HashMap<>();
        for (SiteApiTypeRelation apiTypeRelation : siteApiTypeRelationMap.values()) {
            apiTypeId = apiTypeRelation.getApiTypeId();
            if (apiTypeRelationGroupByType.get(apiTypeId) == null) {
                apiTypeRelationGroupByType.put(apiTypeId, new ArrayList<>());
            }
            apiId = apiTypeRelation.getApiId();
            apiTypeRelation.setApiName(ApiGameTool.getSiteApiName(map, siteApiI18nMap, apiI18nMap, apiId, apiTypeId));
            api = apiMap.get(String.valueOf(apiId));
            if (api == null || terminal.equals(api.getTerminal())) {
                continue;
            }
            siteApi = siteApiMap.get(String.valueOf(apiId));
            if (maintain.equals(api.getSystemStatus()) || siteApi != null && maintain.equals(siteApi.getSystemStatus())) {
                apiTypeRelation.setApiStatus(maintain);
                apiTypeRelationGroupByType.get(apiTypeId).add(apiTypeRelation);
            } else if (siteApi != null && (normal.equals(api.getSystemStatus()) || preMaintain.equals(api.getSystemStatus())) && (normal.equals(siteApi.getSystemStatus()) || preMaintain.equals(siteApi.getSystemStatus()))) {
                apiTypeRelation.setApiStatus(normal);
                apiTypeRelationGroupByType.get(apiTypeId).add(apiTypeRelation);
            }
        }
        return apiTypeRelationGroupByType;
    }

    /**
     * 获取游戏国际化数据
     */
    protected Map<String, SiteGameI18n> getGameI18nMap(SiteGameListVo listVo) {
        return CollectionTool.toEntityMap(getGameI18n(listVo), SiteGameI18n.PROP_GAME_ID, String.class);
    }

    protected void setAccount(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        Integer apiId = playerApiAccountVo.getApiId();
        String fullAddress = ServletTool.getDomainFullAddress(request);
        String transferUrl = fullAddress + "/transfer/index.html"
                + "?apiId=" + apiId
                + "&apiTypeId=" + playerApiAccountVo.getApiTypeId();
        playerApiAccountVo.setTransfersUrl(transferUrl);

        playerApiAccountVo.setLobbyUrl(fullAddress);
        /*if (request.getHeader("User-Agent").contains(AppTypeEnum.APP_ANDROID.getCode())) {
            playerApiAccountVo.setLobbyUrl("javascript:window.gb.finish()");
        }*/

        playerApiAccountVo.setSysUser(SessionManager.getUser());
        String terminal = SessionManagerCommon.getTerminal(request);
        if (StringTool.isNotBlank(playerApiAccountVo.getGameCode())) {
            PlatformTypeEnum platformType = (SupportTerminal.PC.getCode().equals(terminal))?
                    PlatformTypeEnum.pc:PlatformTypeEnum.mobile;
            Game game = Cache.getGameByApiIdCode(playerApiAccountVo.getApiId(), playerApiAccountVo.getGameCode(), platformType);
            if (game != null) {
                playerApiAccountVo.setGameId(game.getId());
                playerApiAccountVo.setPlatformType(game.getSupportTerminal());
            }
        }
        playerApiAccountVo.setPlatformType(terminal);
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
}
