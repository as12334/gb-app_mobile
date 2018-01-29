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
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.enums.AppResolutionEnum;
import so.wwb.gamebox.mobile.app.enums.AppThemeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.Game;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.soul.web.tag.ImageTag.getImagePath;
import static so.wwb.gamebox.model.CacheBase.getSiteGameName;

/**
 * Created by LeTu on 2017/3/31.
 */
public abstract class BaseOriginController {
    private Log LOG = LogFactory.getLog(BaseOriginController.class);
    private String TRANSFERS_URL = "/transfer/index.html";

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
            for (Map.Entry<String, SiteGameI18n> entry : siteGameI18n.entrySet()) {
                if (StringTool.equalsIgnoreCase(siteGame.getGameId().toString(), entry.getKey())) {
                    siteGame.setCover(entry.getValue().getCover());
                }
            }
        }
        return listVo.getResult();
    }

    /**
     * 获取Api的电子游戏
     *
     * @param listVo
     * @return
     */
    protected List<AppSiteGame> getCasinoGameByApiId(SiteGameListVo listVo, HttpServletRequest request, Map pageMap, SiteGameTag tag) {
        Integer apiId = listVo.getSearch().getApiId();
        List<AppSiteGame> siteGames = ListTool.newArrayList();

        if (apiId == null) {
            return siteGames;
        }
        if (!NumberTool.isNumber(String.valueOf(apiId)) && apiId <= 0) {
            return siteGames;
        }

        listVo = getCasinoGames(listVo, tag);
        Map<String, SiteGameI18n> map = getGameI18nMap(listVo);
        for (SiteGame siteGame : listVo.getResult()) {
            for (Map.Entry<String, SiteGameI18n> entry : map.entrySet()) {
                if (StringTool.equalsIgnoreCase(siteGame.getGameId().toString(), entry.getKey())) {
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
                    casinoGame.setCover(getImagePath(SessionManager.getDomain(request), entry.getValue().getCover()));
                    casinoGame.setSystemStatus(siteGame.getSystemStatus());
                    if (SessionManager.getUser() != null) {
                        casinoGame.setGameLink(getCasinoGameRequestUrl(siteGame));
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
        sb.append("/origin/getGameLink.html")
                .append("?apiId=").append(siteGame.getApiId())
                .append("&apiTypeId=").append(siteGame.getApiTypeId());
        if (siteGame.getGameId() != null) {
            sb.append("&gameId=").append(siteGame.getGameId());
        }
        if (StringTool.isNotBlank(siteGame.getCode())) {
            sb.append("&gameCode=").append(siteGame.getCode());
        }
        return sb.toString();
    }

    /**
     * 获取电子游戏
     */
    private SiteGameListVo getCasinoGames(SiteGameListVo listVo, SiteGameTag tag) {
        SiteGameSo so = listVo.getSearch();
        Paging paging = listVo.getPaging();
        Criteria gamesCriteria;
        if (StringTool.isBlank(tag.getTagId())) {
            gamesCriteria = getQueryGameCriteria(so, getGameI18n(listVo));
        } else {
            gamesCriteria = getGameIdByGameTagId(so, tag);
        }

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

    /**
     * 根据GameTagId获取查询条件
     *
     * @param so
     * @param tag
     * @return
     */
    private Criteria getGameIdByGameTagId(SiteGameSo so, SiteGameTag tag) {
        Map<String, SiteGameTag> gameTagMap = Cache.getSiteGameTag();
        List<Integer> gameIds = ListTool.newArrayList();
        for (SiteGameTag gameTag : gameTagMap.values()) {
            if (StringTool.equalsIgnoreCase(tag.getTagId(), gameTag.getTagId())) {
                gameIds.add(gameTag.getGameId());
            }
        }
        return getGameTagCriteria(so, gameIds);
    }

    /**
     * 根据GameTag设置查询条件
     */
    private Criteria getGameTagCriteria(SiteGameSo so, List<Integer> gameIds) {
        Criteria criteria = Criteria.add(SiteGame.PROP_API_TYPE_ID, Operator.EQ, so.getApiTypeId())
                .addAnd(Criteria.add(SiteGame.PROP_API_ID, Operator.EQ, so.getApiId()))
                .addAnd(Criteria.add(SiteGame.PROP_TERMINAL, Operator.EQ, SupportTerminal.PHONE.getCode()))
                .addAnd(Criteria.add(SiteGame.PROP_STATUS, Operator.NE, GameStatusEnum.DISABLE.getCode()))
                .addAnd(Criteria.add(SiteGame.PROP_GAME_TYPE, Operator.EQ, so.getGameType()));

        if (gameIds != null && gameIds.size() == 0) {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.EQ, 0);
        } else {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        }

        return criteria;
    }

    //获取API类型
    protected List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_ORDER_NUM));
    }

    /**
     * 获取游戏类集合
     *
     * @param request
     * @param model
     * @return
     */
    protected List<AppSiteApiTypeRelastionVo> getSiteApiRelationI18n(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelactionI18n = Cache.getSiteApiTypeRelactionI18n(SessionManager.getSiteId());
        List<SiteApiType> siteApiTypes = getApiTypes();

        //组装游戏类型集合
        Map<Integer, List<SiteApiTypeRelationI18n>> siteApiRelation = MapTool.newHashMap();
        for (SiteApiType api : siteApiTypes) {
            List<SiteApiTypeRelationI18n> i18ns = ListTool.newArrayList();
            for (SiteApiTypeRelationI18n relationI18n : siteApiTypeRelactionI18n.values()) {
                if (relationI18n.getApiTypeId().equals(api.getApiTypeId())) {
                    i18ns.add(relationI18n);
                    siteApiRelation.put(api.getApiTypeId(), i18ns);
                }
            }
        }

        //过滤游戏数据提供给原生
        List<AppSiteApiTypeRelastionVo> appList = new ArrayList<>();
        for (Map.Entry<Integer, List<SiteApiTypeRelationI18n>> entry : siteApiRelation.entrySet()) {
            AppSiteApiTypeRelastionVo vo = new AppSiteApiTypeRelastionVo();
            vo.setApiType(entry.getKey());
            for (ApiTypeEnum type : ApiTypeEnum.values()) {
                if (type.getCode() == entry.getKey().intValue()) {
                    vo.setApiTypeName(type.getMsg());
                    vo.setCover(setApiLogoUrl(model, request) + "/api_type_" + entry.getKey() + ".png");
                }
            }
            vo.setLevel(false);
            if (entry.getKey().intValue() == ApiTypeEnum.LOTTERY.getCode()) {
                vo.setLevel(true);
            }
            //获取游戏集合
            vo.setSiteApis(setAppApiRelationI18n(entry.getValue(), request, model));
            vo.setLocale(SessionManager.getLocale().toString());
            appList.add(vo);
        }

        //构造捕鱼游戏
        appList.add(setFishGame(siteApiTypeRelactionI18n.values(), request, model));

        return appList;
    }

    /**
     * 转换彩票
     *
     * @return
     */
    private List<AppSiteGame> setAppSiteGame(SiteApiTypeRelationI18n relationI8n, AppSiteApiTypeRelationI18n i18n, HttpServletRequest request, AppRequestModelVo model) {
        //获取彩票类游戏
        SiteGameListVo siteGameListVo = new SiteGameListVo();
        siteGameListVo.getSearch().setApiId(relationI8n.getApiId());
        siteGameListVo.getSearch().setApiTypeId(relationI8n.getApiTypeId());
        List<SiteGame> lotteryGame = getLotteryGame(siteGameListVo);

        //组装彩票类游戏
        List<AppSiteGame> games = ListTool.newArrayList();
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
                    app.setGameLink(getCasinoGameRequestUrl(siteGame));
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
        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()));
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
            appI18n.setCover(setApiLogoUrl(model, request) + "/api/api_logo_" + i18n.getApiId() + ".png"); //api图片路劲

            if (SessionManager.getUser() != null && i18n.getApiTypeId() != ApiTypeEnum.LOTTERY.getCode()) {
                if (i18n.getApiId() != null && StringTool.equals(i18n.getApiId().toString(), ApiProviderEnum.BSG.getCode())) {
                    appI18n.setGameLink("/game/apiGames.html?apiId=" + i18n.getApiId() + "&apiTypeId=" + i18n.getApiTypeId());
                } else if (SessionManager.isAutoPay() && i18n.getApiTypeId() != null && i18n.getApiTypeId().intValue() != ApiTypeEnum.CASINO.getCode()) {
                    String gameUrl = "/origin/getGameLink.html?apiId=" + appI18n.getApiId() + "&apiTypeId=" + appI18n.getApiTypeId();
                    appI18n.setGameLink(gameUrl);
                } else if (i18n.getApiTypeId() != null && i18n.getApiTypeId().intValue() == ApiTypeEnum.CASINO.getCode()) {
                    appI18n.setGameLink("/origin/getCasinoGame.html?search.apiId=" + i18n.getApiId() + "&search.apiTypeId=" + i18n.getApiTypeId());
                } else {
                    appI18n.setGameLink("/api/detail.html?apiId=" + i18n.getApiId() + "&apiTypeId=" + i18n.getApiTypeId());
                }
                appI18n.setAutoPay(SessionManager.isAutoPay());
            }

            //彩票类游戏
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
                i18n.setGameLink("/mobile-api/origin/getCasinoGame.html?search.apiId=9&search.apiTypeId=2&search.gameType=Fish");
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
                i18n.setGameLink("/mobile-api/origin/getCasinoGame.html?search.apiId=28&search.apiTypeId=2");
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

        String transferUrl = domain + "/transfer/index.html"
                + "?apiId=" + apiId
                + "&apiTypeId=" + playerApiAccountVo.getApiTypeId();
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