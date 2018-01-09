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
import org.soul.commons.math.NumberTool;
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
import org.springframework.ui.Model;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.App.enums.AppResolutionEnum;
import so.wwb.gamebox.mobile.App.enums.AppThemeEnum;
import so.wwb.gamebox.mobile.App.model.AppRequestModelVo;
import so.wwb.gamebox.mobile.App.model.AppSiteApiTypeRelastionVo;
import so.wwb.gamebox.mobile.App.model.AppSiteApiTypeRelationI18n;
import so.wwb.gamebox.mobile.App.model.AppSiteGame;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
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
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static org.soul.web.tag.ImageTag.getImagePath;
import static so.wwb.gamebox.model.CacheBase.getSiteGameName;

/**
 * Created by LeTu on 2017/3/31.
 */
public abstract class BaseApiController extends BaseDemoController {
    private Log LOG = LogFactory.getLog(BaseApiController.class);
    private String TRANSFERS_URL = "/transfer/index.html";

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

        if (listVo.getSearch().getApiId() != null && listVo.getSearch().getApiId().intValue() == Integer.valueOf(ApiProviderEnum.PL.getCode()).intValue()) {
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

    /**
     * 获取彩票游戏
     */
    private SiteGameListVo getLotteryGames(SiteGameListVo listVo) {
        SiteGameSo so = listVo.getSearch();
        Criteria gamesCriteria = getQueryGameCriteria(so, getGameI18n(listVo));
        List<SiteGame> games = CollectionQueryTool.query(Cache.getSiteGame().values(), gamesCriteria);
        games = getSiteGamesWhichIsNormalStatus(games);
        games = games == null ? new ArrayList<SiteGame>() : games;

        // 设置游戏状态
        listVo.setResult(setGameStatus(listVo, games));
        return listVo;
    }

    private List<SiteGame> getLotteryGame(SiteGameListVo listVo) {
        listVo = getLotteryGames(listVo);
        Map<String, SiteGameI18n> siteGameI18n = getGameI18nMap(listVo);
        for (SiteGame siteGame : listVo.getResult()) {
            for (String gameId : siteGameI18n.keySet()) {
                if (StringTool.equalsIgnoreCase(siteGame.getGameId().toString(), gameId)) {
                    siteGame.setCover(siteGameI18n.get(gameId).getCover());
                }
            }
        }
        return listVo.getResult();
    }

    /**
     * @param listVo
     * @return
     */
    protected List<AppSiteGame> getCasinoGameByApiId(SiteGameListVo listVo, HttpServletRequest request, Map pageMap, AppRequestModelVo model) {
        Integer apiId = listVo.getSearch().getApiId();
        List<AppSiteGame> siteGames = ListTool.newArrayList();

        if (apiId == null) {
            return siteGames;
        }
        if (!NumberTool.isNumber(String.valueOf(apiId)) && apiId <= 0) {
            return siteGames;
        }

        listVo = getCasinoGames(listVo);
        Map<String, SiteGameI18n> map = getGameI18nMap(listVo);
        for (SiteGame siteGame : listVo.getResult()) {
            for (String api : map.keySet()) {
                if (StringTool.equalsIgnoreCase(siteGame.getGameId().toString(), api)) {
                    AppSiteGame casinoGame = new AppSiteGame();
                    casinoGame.setGameId(siteGame.getGameId());
                    casinoGame.setSiteId(siteGame.getSiteId());
                    casinoGame.setApiId(siteGame.getApiId());
                    casinoGame.setGameType(siteGame.getGameType());
                    casinoGame.setOrderNum(siteGame.getOrderNum());
                    casinoGame.setStatus(siteGame.getStatus());
                    casinoGame.setApiTypeId(siteGame.getApiTypeId());
                    casinoGame.setCode(siteGame.getCode());
                    casinoGame.setName(getSiteGameName(siteGame.getGameId().toString()));
                    casinoGame.setCover(getImagePath(SessionManager.getDomain(request), map.get(api).getCover()));
                    casinoGame.setSystemStatus(siteGame.getSystemStatus());
                    if (SessionManager.getUser() != null) {
                        if (SessionManager.isAutoPay()) {
                            AppSiteApiTypeRelationI18n gameUrl = goGameUrl(request, siteGame.getApiId(), siteGame.getApiTypeId().toString(), siteGame.getCode(), model);
                            casinoGame.setGameLink(gameUrl.getGameLink());
                            casinoGame.setGameMsg(gameUrl.getGameMsg());
                        } else {
                            PlayerApiAccountVo player = new PlayerApiAccountVo();
                            player.setApiId(siteGame.getApiId());
                            player.setApiTypeId(siteGame.getApiTypeId().toString());
                            player.setGameId(siteGame.getGameId());
                            player.setGameCode(siteGame.getCode());
                            AppSiteApiTypeRelationI18n gameUrl = getCasinoGameUrl(player, request, model);
                            casinoGame.setGameLink(gameUrl.getGameLink());
                            casinoGame.setGameMsg(gameUrl.getGameMsg());
                        }
                        casinoGame.setAutoPay(SessionManager.isAutoPay());
                    }

                    siteGames.add(casinoGame);
                }
            }
        }
        pageMap.put("pageTotal", listVo.getPaging().getTotalCount());

        return siteGames;
    }

    /**
     * 获取电子游戏
     */
    private SiteGameListVo getCasinoGames(SiteGameListVo listVo) {
        SiteGameSo so = listVo.getSearch();
        Paging paging = listVo.getPaging();
        Criteria gamesCriteria = getQueryGameCriteria(so, getGameI18n(listVo));
        List<SiteGame> games = CollectionQueryTool.query(Cache.getSiteGame().values(), gamesCriteria);
        games = getSiteGamesWhichIsNormalStatus(games);
        games = games == null ? new ArrayList<SiteGame>() : games;
        paging.setTotalCount(games.size());
        paging.cal();

        Integer pageNumber = paging.getPageNumber();
        Integer toIndex = ((pageNumber - 1) * paging.getPageSize() + paging.getPageSize());
        Integer formIndex = (pageNumber - 1) * paging.getPageSize();
        games = games.subList(formIndex, toIndex > paging.getTotalCount() ? (int) paging.getTotalCount() : toIndex);

        // 设置游戏状态
        listVo.setResult(setGameStatus(listVo, games));
        return listVo;
    }

    //获取API类型
    protected List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_ORDER_NUM));
    }

    protected void getApiTypeGame(Model model) {
        //获取类型
        List<SiteApiType> siteApiTypes = getApiTypes();
        Map<String, SiteApiTypeRelation> siteApiTypeRelationMap = CacheBase.siteApiTypeRelationMap(SessionManager.getSiteId());
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Map<Integer, List<SiteApiTypeRelation>> apiTypeRelationGroupByType = apiTypeRelationGroupByType(siteApiTypeRelationMap, apiI18nMap, siteApiI18nMap);
        Map<String, ApiTypeI18n> apiTypeI18nMap = CacheBase.getApiTypeI18n();
        for (SiteApiType siteApiType : siteApiTypes) {
            siteApiType.setName(apiTypeI18nMap.get(String.valueOf(siteApiType.getApiTypeId())).getName());
            siteApiType.setApiTypeRelations(CollectionQueryTool.sort(apiTypeRelationGroupByType.get(siteApiType.getApiTypeId()), Order.desc(SiteApiTypeRelation.PROP_ORDER_NUM)));
        }
        model.addAttribute("siteApiTypes", siteApiTypes);
        //处理捕鱼、彩票游戏
        Map<String, SiteGame> siteGameMap = CacheBase.getSiteGame();
        //捕鱼
        Map<Integer, String> fishMap = new HashMap<>();
        //彩票
        Map<Integer, List<SiteGame>> lottery = new HashMap<>();
        String fishGameType = GameTypeEnum.FISH.getCode();
        int lotteryType = ApiTypeEnum.LOTTERY.getCode();
        Integer apiId;
        Map<String, SiteGameI18n> siteGameI18nMap = CacheBase.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = CacheBase.getGameI18n();
        Map<String, Game> gameMap = CacheBase.getGame();
        String disabled = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        String normal = GameStatusEnum.NORMAL.getCode();
        Game game;
        for (SiteGame siteGame : siteGameMap.values()) {
            apiId = siteGame.getApiId();
            if (fishGameType.equals(siteGame.getGameType()) && fishMap.get(apiId) == null) {
                fishMap.put(apiId, getApiName(apiId, apiI18nMap, siteApiI18nMap));
            } else if (lotteryType == siteGame.getApiTypeId()) {
                if (lottery.get(apiId) == null) {
                    lottery.put(apiId, new ArrayList<>());
                }
                game = gameMap.get(siteGame.getId());
                if (game == null || disabled.equals(game.getStatus()) || disabled.equals(siteGame.getStatus())) {
                    siteGame.setStatus(disabled);
                } else if (maintain.equals(game.getSystemStatus()) || maintain.equals(siteGame.getSystemStatus())) {
                    siteGame.setStatus(maintain);
                } else {
                    siteGame.setStatus(normal);
                    setGameNameAndCover(siteGameI18nMap, gameI18nMap, siteGame);
                    lottery.get(apiId).add(siteGame);
                }
            }
        }
        model.addAttribute("fish", fishMap);
        model.addAttribute("lottery", lottery);
    }

    private void setGameNameAndCover(Map<String, SiteGameI18n> siteGameI18nMap, Map<String, GameI18n> gameI18nMap, SiteGame siteGame) {
        Integer gameId = siteGame.getGameId();
        SiteGameI18n siteGameI18n = siteGameI18nMap.get(String.valueOf(gameId));
        String gameName = null;
        String cover = null;
        GameI18n gameI18n = gameI18nMap.get(String.valueOf(gameId));
        if (siteGameI18n != null && StringTool.isNotBlank(siteGameI18n.getName())) {
            gameName = siteGameI18n.getName();
        } else if (gameI18n != null && StringTool.isNotBlank(gameI18n.getName())) {
            gameName = gameI18n.getName();
        }
        if (siteGameI18n != null && StringTool.isNotBlank(siteGameI18n.getCover())) {
            cover = siteGameI18n.getCover();
        } else if (gameI18n != null && StringTool.isNotBlank(gameI18n.getCover())) {
            cover = gameI18n.getCover();
        }
        siteGame.setName(gameName);
        siteGame.setCover(cover);
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
        Map<Integer, Map<Integer, String>> map = new HashMap<>(siteApiI18nMap.size());
        Integer siteId = SessionManager.getSiteId();
        Integer apiTypeId;
        Integer apiId;
        String apiName;
        for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nMap.values()) {
            if (siteId == siteApiTypeRelationI18n.getSiteId()) {
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
        Map<Integer, List<SiteApiTypeRelation>> apiTypeRelationGroupByType = new HashMap<>();
        for (SiteApiTypeRelation apiTypeRelation : siteApiTypeRelationMap.values()) {
            apiTypeId = apiTypeRelation.getApiTypeId();
            if (apiTypeRelationGroupByType.get(apiTypeId) == null) {
                apiTypeRelationGroupByType.put(apiTypeId, new ArrayList<>());
            }
            apiId = apiTypeRelation.getApiId();
            apiName = MapTool.getString(map.get(apiTypeId), apiId);
            if (StringTool.isBlank(apiName)) {
                apiName = getApiName(apiId, apiI18nMap, siteApiI18nMap);
            }
            apiTypeRelation.setApiName(apiName);
            api = apiMap.get(String.valueOf(apiId));
            siteApi = siteApiMap.get(String.valueOf(apiId));
            if (api != null && maintain.equals(api.getSystemStatus()) || siteApi != null && maintain.equals(siteApi.getSystemStatus())) {
                apiTypeRelation.setApiStatus(maintain);
                apiTypeRelationGroupByType.get(apiTypeId).add(apiTypeRelation);
            } else if (api != null && siteApi != null && (normal.equals(api.getSystemStatus()) || preMaintain.equals(api.getSystemStatus())) && (normal.equals(siteApi.getSystemStatus()) || preMaintain.equals(siteApi.getSystemStatus()))) {
                apiTypeRelation.setApiStatus(normal);
                apiTypeRelationGroupByType.get(apiTypeId).add(apiTypeRelation);
            }
        }
        return apiTypeRelationGroupByType;
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

    protected List<AppSiteApiTypeRelastionVo> getSiteApiRelationI18n(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelactionI18n = Cache.getSiteApiTypeRelactionI18n(SessionManager.getSiteId());
        List<SiteApiType> siteApiTypes = getApiTypes();

        Map<Integer, List<SiteApiTypeRelationI18n>> siteApiRelation = MapTool.newHashMap();
        List<AppSiteApiTypeRelastionVo> appList = new ArrayList<>();

        for (SiteApiType api : siteApiTypes) {
            List<SiteApiTypeRelationI18n> i18ns = ListTool.newArrayList();
            for (SiteApiTypeRelationI18n relationI18n : siteApiTypeRelactionI18n.values()) {
                if (relationI18n.getApiTypeId().equals(api.getApiTypeId())) {
                    i18ns.add(relationI18n);
                    siteApiRelation.put(api.getApiTypeId(), i18ns);
                }
            }
        }

        for (Integer apiType : siteApiRelation.keySet()) {
            AppSiteApiTypeRelastionVo vo = new AppSiteApiTypeRelastionVo();
            vo.setApiType(apiType);
            for (ApiTypeEnum type : ApiTypeEnum.values()) {
                if (type.getCode() == apiType) {
                    vo.setApiTypeName(type.getMsg());
                    vo.setCover(setApiLogoUrl(model, request) + "/api_type_" + apiType + ".png");
                }
            }
            if (apiType == ApiTypeEnum.LOTTERY.getCode()) {
                vo.setLevel(true);

            } else {
                vo.setLevel(false);
            }
            vo.setSiteApis(setAppApiRelationI18n(siteApiRelation.get(apiType), request, model));
            vo.setLocale(SessionManager.getLocale().toString());
            appList.add(vo);
        }

        appList.add(setFishGame(siteApiTypeRelactionI18n.values(), request, model));

        return appList;
    }

    /**
     * 转换彩票
     *
     * @return
     */
    private List<AppSiteGame> setAppSiteGame(SiteApiTypeRelationI18n relationI8n, AppSiteApiTypeRelationI18n i18n, HttpServletRequest request, AppRequestModelVo model) {
        List<AppSiteGame> games = ListTool.newArrayList();

        SiteGameListVo siteGameListVo = new SiteGameListVo();
        siteGameListVo.getSearch().setApiId(relationI8n.getApiId());
        siteGameListVo.getSearch().setApiTypeId(relationI8n.getApiTypeId());
        List<SiteGame> lotteryGame = getLotteryGame(siteGameListVo);

        for (SiteGame siteGame : lotteryGame) {
            AppSiteGame app = new AppSiteGame();
            app.setGameId(siteGame.getGameId());
            app.setSiteId(siteGame.getSiteId());
            app.setApiId(siteGame.getApiId());
            app.setGameType(siteGame.getGameType());
            app.setOrderNum(siteGame.getOrderNum());
            app.setStatus(siteGame.getStatus());
            app.setApiTypeId(siteGame.getApiTypeId());
            app.setCode(siteGame.getCode());
            app.setName(getSiteGameName(siteGame.getGameId().toString()));
            app.setCover(getImagePath(SessionManager.getDomain(request), siteGame.getCover()));
            app.setSystemStatus(siteGame.getSystemStatus());

            if (SessionManager.getUser() != null) {
                if (SessionManager.isAutoPay()) {
                    AppSiteApiTypeRelationI18n gameUrl = goGameUrl(request, siteGame.getApiId(), siteGame.getApiTypeId().toString(), siteGame.getCode(), model);
                    app.setGameLink(gameUrl.getGameLink());
                    app.setGameMsg(gameUrl.getGameMsg());
                } else {
                    app.setGameLink("/api/detail.html?apiId=" + siteGame.getApiId() + "&apiTypeId=" + siteGame.getApiTypeId());
                }
                app.setAutoPay(SessionManager.isAutoPay());
            }
            games.add(app);
        }
        i18n.setGameList(games);
        return games;
    }

    /**
     * 设置api图片路径
     *
     * @param model
     * @return
     */
    private String setApiLogoUrl(AppRequestModelVo model, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()) + "/../app");
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            sb.append("/android/themes");
        }
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            sb.append("/ios/themes");
        }

        for (AppThemeEnum code : AppThemeEnum.values()) {
            if (StringTool.equalsIgnoreCase(model.getTheme(), code.getCode())) {
                sb.append("/").append(code.getCode()).append("/images");
            }
        }
        for (AppResolutionEnum code : AppResolutionEnum.values()) {
            if (StringTool.equalsIgnoreCase(model.getResolution(), code.getCode())) {
                sb.append("/").append(code.getCode());
            }
        }

        return sb.toString();
    }

    /**
     * 转换游戏类
     *
     * @param siteApis
     * @return
     */
    private List<AppSiteApiTypeRelationI18n> setAppApiRelationI18n(List<SiteApiTypeRelationI18n> siteApis, HttpServletRequest request, AppRequestModelVo model) {
        List<AppSiteApiTypeRelationI18n> appSites = ListTool.newArrayList();
        for (SiteApiTypeRelationI18n i18n : siteApis) {
            AppSiteApiTypeRelationI18n appI18n = new AppSiteApiTypeRelationI18n();
            appI18n.setApiId(i18n.getApiId());
            appI18n.setApiTypeId(i18n.getApiTypeId());
            appI18n.setLocal(i18n.getLocal());
            appI18n.setName(i18n.getName());
            appI18n.setSiteId(i18n.getSiteId());
            appI18n.setCover(setApiLogoUrl(model, request) + "/api/api_logo_" + i18n.getApiId() + ".png");
            //appI18n.setCover("images/icon-" + i18n.getApiTypeId() + "-" + i18n.getApiId() + "" + ".png");
            if (SessionManager.getUser() != null && i18n.getApiTypeId() != ApiTypeEnum.LOTTERY.getCode()) {
                if (i18n.getApiId().equals(ApiProviderEnum.BSG.getCode())) {
                    appI18n.setGameLink("/game/apiGames.html?apiId=" + i18n.getApiId() + "&apiTypeId=" + i18n.getApiTypeId());
                } else if (SessionManager.isAutoPay() && i18n.getApiTypeId() != ApiTypeEnum.CASINO.getCode()) {
                    AppSiteApiTypeRelationI18n gameUrl = goGameUrl(request, appI18n.getApiId(), appI18n.getApiTypeId().toString(), null, model);
                    appI18n.setGameLink(gameUrl.getGameLink());
                    appI18n.setGameMsg(gameUrl.getGameMsg());
                } else if (i18n.getApiTypeId().equals(ApiTypeEnum.CASINO.getCode())) {
                    appI18n.setGameLink("/origin/getCasinoGame.html?search.apiId=" + i18n.getApiId() + "&search.apiTypeId=" + i18n.getApiTypeId());
                } else {
                    appI18n.setGameLink("/api/detail.html?apiId=" + i18n.getApiId() + "&apiTypeId=" + i18n.getApiTypeId());
                }
                appI18n.setAutoPay(SessionManager.isAutoPay());
            }

            if (i18n.getApiTypeId() == ApiTypeEnum.LOTTERY.getCode()) {
                setAppSiteGame(i18n, appI18n, request, model);
            }
            appSites.add(appI18n);
        }

        return appSites;
    }

    /**
     * 构造捕鱼游戏
     *
     * @return
     */
    private AppSiteApiTypeRelastionVo setFishGame(Collection<SiteApiTypeRelationI18n> i18ns, HttpServletRequest request, AppRequestModelVo model) {
        AppSiteApiTypeRelastionVo fishVo = new AppSiteApiTypeRelastionVo();
        fishVo.setApiType(-1);
        String gameType = LocaleTool.tranDict(DictEnum.GAME_TYPE, GameTypeEnum.FISH.getCode());
        fishVo.setApiTypeName(gameType);
        fishVo.setLocale(SessionManager.getLocale().toString());
        fishVo.setCover(setApiLogoUrl(model, request) + "/fish.png");
        List<AppSiteApiTypeRelationI18n> fishSiteApis = ListTool.newArrayList();

        for (SiteApiTypeRelationI18n relationI18n : i18ns) {
            if (relationI18n.getApiTypeId() == ApiTypeEnum.CASINO.getCode()
                    && StringTool.equalsIgnoreCase(relationI18n.getApiId().toString(), ApiProviderEnum.AG.getCode())) {
                AppSiteApiTypeRelationI18n i18n = new AppSiteApiTypeRelationI18n();
                i18n.setName(ApiProviderEnum.AG.getTrans());
                i18n.setLocal(SessionManager.getSiteLocale().toString());
                i18n.setSiteId(SessionManager.getSiteId());
                i18n.setApiId(Integer.parseInt(ApiProviderEnum.AG.getCode()));
                i18n.setApiTypeId(ApiTypeEnum.CASINO.getCode());
                i18n.setGameLink("/origin/getCasinoGame.html?search.apiId=9&search.apiTypeId=2&search.gameType=Fish");
                i18n.setCover(setApiLogoUrl(model, request) + "/api/api_logo_" + i18n.getApiId() + ".png");
                fishSiteApis.add(i18n);
            }
            if (relationI18n.getApiTypeId() == ApiTypeEnum.CASINO.getCode()
                    && StringTool.equalsIgnoreCase(relationI18n.getApiId().toString(), ApiProviderEnum.GG.getCode())) {
                AppSiteApiTypeRelationI18n i18n = new AppSiteApiTypeRelationI18n();
                i18n.setName(ApiProviderEnum.GG.getTrans());
                i18n.setLocal(SessionManager.getSiteLocale().toString());
                i18n.setSiteId(SessionManager.getSiteId());
                i18n.setApiId(Integer.parseInt(ApiProviderEnum.GG.getCode()));
                i18n.setApiTypeId(ApiTypeEnum.CASINO.getCode());
                i18n.setGameLink("/origin/getCasinoGame.html?search.apiId=28&search.apiTypeId=2");
                i18n.setCover(setApiLogoUrl(model, request) + "/api/api_logo_" + i18n.getApiId() + ".png");
                fishSiteApis.add(i18n);
            }
        }
        fishVo.setSiteApis(fishSiteApis);

        return fishVo;
    }

    /**
     * 获取游戏国际化数据
     */
    protected Map<String, SiteGameI18n> getGameI18nMap(SiteGameListVo listVo) {
        return CollectionTool.toEntityMap(getGameI18n(listVo), SiteGameI18n.PROP_GAME_ID, String.class);
    }

    private AppSiteApiTypeRelationI18n goGameUrl(HttpServletRequest request, Integer apiId, String apiTypeId, String gameCode, AppRequestModelVo model) {
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
            if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel) && (
                    apiId == Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                            apiId == Integer.valueOf(ApiProviderEnum.DWT.getCode()))) {
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
        String url = (gameApiResult instanceof RegisterResult) ?
                ((RegisterResult) gameApiResult).getDefaultLink() : ((LoginResult) gameApiResult).getDefaultLink();
        url = buildGameUrl(url, model, apiId);
        appI18n.setGameLink(url);

        return appI18n;
    }

    private String buildGameUrl(String url, AppRequestModelVo model, Integer apiId) {
        if (StringTool.isBlank(url)) {
            return url;
        }
        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_IOS.getCode())) {
            if (apiId.equals(ApiProviderEnum.PL.getCode())) {
                url = url + "?ad=" + apiId;
            }
        }

        if (StringTool.equalsIgnoreCase(model.getTerminal(), AppTypeEnum.APP_ANDROID.getCode())) {
            if (apiId.equals(ApiProviderEnum.PL.getCode())
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
    private AppSiteApiTypeRelationI18n getCasinoGameUrl(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request, AppRequestModelVo model) {
        AppSiteApiTypeRelationI18n appI18n = new AppSiteApiTypeRelationI18n();
        setAccount(playerApiAccountVo, request);

        if (checkApiStatus(playerApiAccountVo)) {
            DemoModelEnum demoModel = SessionManagerCommon.getDemoModelEnum();
            if (demoModel != null) {
                //平台试玩免转不可用
                //纯彩票试玩免转不可用
                playerApiAccountVo.setTrial(true);
                //Integer apiId = playerApiAccountVo.getApiId();
                if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel) && (
                        playerApiAccountVo.getApiId() == Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                                playerApiAccountVo.getApiId() == Integer.valueOf(ApiProviderEnum.DWT.getCode()))) {
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

        String transferUrl = domain + "/transfer/index.html"
                + "?apiId=" + apiId
                + "&apiTypeId=" + playerApiAccountVo.getApiTypeId();
        playerApiAccountVo.setTransfersUrl(transferUrl);

        playerApiAccountVo.setLobbyUrl(domain.toString());
        if (request.getHeader("User-Agent").contains("app_android")) {
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
            if (playerApiAccountVo.getApiId() == Integer.valueOf(ApiProviderEnum.PL.getCode())
                    || playerApiAccountVo.getApiId() == Integer.valueOf(ApiProviderEnum.DWT.getCode())) {
                return true;
            }
        }
        //彩票试玩，如果是龙头彩票，就可以玩
        if (DemoModelEnum.MODEL_4_LOTTERY.equals(demoModel)) {
            if (playerApiAccountVo.getApiId() == Integer.valueOf(ApiProviderEnum.PL.getCode())) {
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
                if (playerApiAccountVo.getApiId() != Integer.valueOf(ApiProviderEnum.PL.getCode())
                        && playerApiAccountVo.getApiId() != Integer.valueOf(ApiProviderEnum.DWT.getCode())) {
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
