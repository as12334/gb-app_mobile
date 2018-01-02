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
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.Paging;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.commons.security.CryptoTool;
import org.soul.model.gameapi.result.GameApiResult;
import org.soul.model.gameapi.result.ResultStatus;
import org.soul.web.session.SessionManagerBase;
import org.springframework.ui.Model;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
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
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.*;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;

import javax.servlet.http.HttpServletRequest;
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

        if (listVo.getSearch().getApiId().intValue() == Integer.valueOf(ApiProviderEnum.PL.getCode()).intValue()) {
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
     * 查询公告
     */
    protected List<CttAnnouncement> getAnnouncement() {
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
     * 查询Banner
     *
     * @deprecated since v1057
     */
    protected List<Map> getCarousel(HttpServletRequest request, String type) {
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
                    resultList.add(m);
                }
            }
        }
        return resultList;
    }

    /**
     * 查询Banner
     *
     * @deprecated since v1057
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
     *
     * @param listVo
     * @return
     */
    protected List<AppSiteGame> getCasinoGameByApiId(SiteGameListVo listVo,HttpServletRequest request,Map pageMap) {
        Integer apiId = listVo.getSearch().getApiId();
        List<AppSiteGame> siteGames = ListTool.newArrayList();

        if(apiId == null){
            return siteGames;
        }
        if (!NumberTool.isNumber(String.valueOf(apiId)) && apiId <= 0) {
            return siteGames;
        }

        listVo = getCasinoGames(listVo);
        Map<String,SiteGameI18n> map = getGameI18nMap(listVo);
        for (SiteGame siteGame: listVo.getResult()){
            for (String api : map.keySet()){
                if(StringTool.equalsIgnoreCase(siteGame.getGameId().toString(),api)){
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
                    casinoGame.setCover(getImagePath(SessionManager.getDomain(request),map.get(api).getCover()));
                    casinoGame.setSystemStatus(siteGame.getSystemStatus());
                    if(SessionManager.getUser() != null){
                        if(SessionManager.isAutoPay()){
                            casinoGame.setGameLink("/transfer/auto/loginAndAutoTransfer.html");
                        }else{
                            casinoGame.setGameLink("/api/login.html");
                        }
                    }

                    siteGames.add(casinoGame);
                }
            }
        }
        pageMap.put("page",listVo.getPaging());

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

    /**
     * 显示红包浮动图
     *
     * @param floatList
     */
    protected void showMoneyActivityFloat(List<Map> floatList) {
        CttFloatPic cttFloatPic = queryMoneyActivityFloat();
        if (cttFloatPic != null) {
            PlayerActivityMessage moneyActivity = findMoneyActivity();
            CttFloatPicItem cttFloatPicItem = queryMoneyFloatPic(cttFloatPic);
            if (moneyActivity != null) {
                String activityId = CryptoTool.aesEncrypt(String.valueOf(moneyActivity.getId()), "PlayerActivityMessageListVo");
                Map floatMap = new HashMap();
                floatMap.put("type", "moneyActivity");
                floatMap.put("activityId", activityId);
                floatMap.put("floatItem", cttFloatPicItem);
                floatMap.put("cttFloatPic", cttFloatPic);
                floatMap.put("description", moneyActivity.getActivityDescription());
                floatList.add(floatMap);
            }
        }
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

    //获取API类型
    protected List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_ORDER_NUM));
    }

    //获取游戏和对应的类型
    protected Map<Integer, List<SiteApiTypeRelationI18n>> getSiteApiRelationI18n(Model model) {
        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelactionI18n = Cache.getSiteApiTypeRelactionI18n(SessionManager.getSiteId());
        List<SiteApiType> siteApiTypes = getApiTypes();
        Map<Integer, List<SiteGame>> lotteryGames = MapTool.newHashMap();

        Map<Integer, List<SiteApiTypeRelationI18n>> siteApiRelation = MapTool.newHashMap();
        for (SiteApiType api : siteApiTypes) {
            List<SiteApiTypeRelationI18n> i18ns = ListTool.newArrayList();
            for (SiteApiTypeRelationI18n relationI18n : siteApiTypeRelactionI18n.values()) {

                if (StringTool.equalsIgnoreCase(relationI18n.getApiTypeId().toString(), api.getApiTypeId().toString())) {
                    i18ns.add(relationI18n);
                    siteApiRelation.put(api.getApiTypeId(), i18ns);
                    //判断捕鱼AG GG是否存在
                    if (relationI18n.getApiTypeId() == 2 && relationI18n.getApiId() == 9) {
                        model.addAttribute("AGExist", true);
                    }
                    if (relationI18n.getApiTypeId() == 2 && relationI18n.getApiId() == 28) {
                        model.addAttribute("GGExist", true);
                    }
                    //彩票类游戏
                    if (api.getApiTypeId() == 4) {
                        SiteGameListVo siteGameListVo = new SiteGameListVo();
                        siteGameListVo.getSearch().setApiTypeId(relationI18n.getApiTypeId());
                        siteGameListVo.getSearch().setApiId(relationI18n.getApiId());
                        lotteryGames.put(relationI18n.getApiId(), getLotteryGame(siteGameListVo));
                    }
                }
            }
        }
        model.addAttribute("lotteryGame", lotteryGames);
        return siteApiRelation;
    }

    protected List<AppSiteApiTypeRelastionVo> getSiteApiRelationI18n(HttpServletRequest request) {
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
                    vo.setCover("images/icon-" + apiType + ".png");
                }
            }
            if (apiType == ApiTypeEnum.LOTTERY.getCode()) {
                vo.setLevel(true);

            } else {
                vo.setLevel(false);
            }
            vo.setSiteApis(setAppApiRelationI18n(siteApiRelation.get(apiType),request));
            vo.setLocale(SessionManager.getLocale().toString());
            appList.add(vo);
        }

        appList.add(setFishGame(siteApiTypeRelactionI18n.values()));

        return appList;
    }

    /**
     * 转换彩票
     *
     * @return
     */
    private List<AppSiteGame> setAppSiteGame(SiteApiTypeRelationI18n relationI8n,AppSiteApiTypeRelationI18n i18n,HttpServletRequest request) {
        List<AppSiteGame> games = ListTool.newArrayList();

        SiteGameListVo siteGameListVo = new SiteGameListVo();
        siteGameListVo.getSearch().setApiId(relationI8n.getApiId());
        siteGameListVo.getSearch().setApiTypeId(relationI8n.getApiTypeId());
        List<SiteGame> lotteryGame = getLotteryGame(siteGameListVo);

        for (SiteGame siteGame : lotteryGame){
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

            if(SessionManager.getUser() != null) {
                if (SessionManager.isAutoPay()) {
                    app.setGameLink("/transfer/auto/loginAndAutoTransfer.html");
                } else {
                    app.setGameLink("/api/login.html");
                }
            }
            games.add(app);
        }
        i18n.setGameList(games);
        return games;
    }

    /**
     * 转换游戏类
     *
     * @param siteApis
     * @return
     */
    private List<AppSiteApiTypeRelationI18n> setAppApiRelationI18n(List<SiteApiTypeRelationI18n> siteApis,HttpServletRequest request) {
        List<AppSiteApiTypeRelationI18n> appSites = ListTool.newArrayList();
        for (SiteApiTypeRelationI18n i18n : siteApis) {
            AppSiteApiTypeRelationI18n appI18n = new AppSiteApiTypeRelationI18n();
            appI18n.setApiId(i18n.getApiId());
            appI18n.setApiTypeId(i18n.getApiTypeId());
            appI18n.setLocal(i18n.getLocal());
            appI18n.setName(i18n.getName());
            appI18n.setSiteId(i18n.getSiteId());
            appI18n.setCover("images/icon-" + i18n.getApiTypeId() + "-" + i18n.getApiId() + "" + ".png");
            if(SessionManager.getUser()!=null && i18n.getApiTypeId() != ApiTypeEnum.LOTTERY.getCode()){
                if(i18n.getApiId().equals(ApiProviderEnum.BSG.getCode())){
                    appI18n.setGameLink("/game/apiGames.html?apiId="+i18n.getApiId()+"&apiTypeId="+i18n.getApiTypeId());
                }else if(SessionManager.isAutoPay() && i18n.getApiTypeId() != ApiTypeEnum.CASINO.getCode()){
                    appI18n.setGameLink("/transfer/auto/loginAndAutoTransfer.html");
                }else{
                    appI18n.setGameMsg(setMsg(MessageI18nConst.NOT_ALLOW_AUTO_PAY, Module.Passport.getCode()));
                    appI18n.setGameLink("/game/getGameByApiId.html?search.apiId="+i18n.getApiId()+"&search.apiTypeId="+i18n.getApiTypeId());
                }
                appI18n.setAutoPay(SessionManager.isAutoPay());
            }

            if(i18n.getApiTypeId() == ApiTypeEnum.LOTTERY.getCode()){
                setAppSiteGame(i18n,appI18n,request);
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
    private AppSiteApiTypeRelastionVo setFishGame(Collection<SiteApiTypeRelationI18n> i18ns) {
        AppSiteApiTypeRelastionVo fishVo = new AppSiteApiTypeRelastionVo();
        fishVo.setApiType(-1);
        fishVo.setApiTypeName("捕鱼");
        fishVo.setLocale(SessionManager.getLocale().toString());
        fishVo.setCover("images/icon-fish.png");
        List<AppSiteApiTypeRelationI18n> fishSiteApis = ListTool.newArrayList();

        for (SiteApiTypeRelationI18n relationI18n : i18ns) {
            if (relationI18n.getApiTypeId() == ApiTypeEnum.CASINO.getCode()
                    && StringTool.equalsIgnoreCase(relationI18n.getApiId().toString(), ApiProviderEnum.AG.getCode())) {
                AppSiteApiTypeRelationI18n i18n = new AppSiteApiTypeRelationI18n();
                i18n.setName(ApiProviderEnum.AG.getTrans());
                i18n.setLocal(SessionManager.getSiteLocale().toString());
                i18n.setSiteId(SessionManager.getSiteId());
                i18n.setApiId(Integer.getInteger(ApiProviderEnum.AG.getCode()));
                i18n.setApiTypeId(ApiTypeEnum.CASINO.getCode());
                i18n.setGameLink("/game/getGameByApiId.html?search.apiId=9&search.apiTypeId=2&search.gameType=Fish");
                fishSiteApis.add(i18n);
            }
            if (relationI18n.getApiTypeId() == ApiTypeEnum.CASINO.getCode()
                    && StringTool.equalsIgnoreCase(relationI18n.getApiId().toString(), ApiProviderEnum.GG.getCode())) {
                AppSiteApiTypeRelationI18n i18n = new AppSiteApiTypeRelationI18n();
                i18n.setName(ApiProviderEnum.GG.getTrans());
                i18n.setLocal(SessionManager.getSiteLocale().toString());
                i18n.setSiteId(SessionManager.getSiteId());
                i18n.setApiId(Integer.getInteger(ApiProviderEnum.GG.getCode()));
                i18n.setApiTypeId(ApiTypeEnum.CASINO.getCode());
                i18n.setGameLink("/game/getGameByApiId.html?search.apiId=28&search.apiTypeId=2");
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

    private AppSiteApiTypeRelationI18n goGameUrl(HttpServletRequest request,PlayerApiAccountVo playerApiAccountVo,AppSiteApiTypeRelationI18n appI18n){
        playerApiAccountVo.setApiId(10);
        playerApiAccountVo.setApiTypeId("4");
        playerApiAccountVo.getGameCode();

        playerApiAccountVo = getPlayerApiAccount(request,playerApiAccountVo);

        String msg = isAllowLogin(playerApiAccountVo);
        if(StringTool.isNotBlank(msg)){
            appI18n.setGameMsg(msg);
            appI18n.setGameLink("");
            return appI18n;
        }

        DemoModelEnum demoModel = SessionManagerCommon.getDemoModelEnum();
        if(demoModel!=null){
            //平台试玩免转不可用
            //纯彩票试玩免转不可用
            playerApiAccountVo.setTrial(true);
            Integer apiId = playerApiAccountVo.getApiId();
            if(DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel)&&(
                    apiId==Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                            apiId==Integer.valueOf(ApiProviderEnum.DWT.getCode()))){
                //模拟账号免转可用
                playerApiAccountVo.setTrial(false);
            }
        }

        playerApiAccountVo = ServiceTool.freeTranferServcice().autoTransferLogin(playerApiAccountVo);
        if (playerApiAccountVo == null || playerApiAccountVo.getGameApiResult() == null || ResultStatus.SUCCESS != playerApiAccountVo.getGameApiResult().getStatus()) {
            appI18n.setGameMsg(setMsg(MessageI18nConst.API_LOGIN_ERROR, Module.Passport.getCode()));
            appI18n.setGameLink("");
            return appI18n;
        }

        GameApiResult gameApiResult = playerApiAccountVo.getGameApiResult();
        return appI18n;
    }

    /**
     * 玩家信息
     * @param request
     * @param playerApiVo
     * @return
     */
    private PlayerApiAccountVo getPlayerApiAccount(HttpServletRequest request,PlayerApiAccountVo playerApiVo){
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

    private String isAllowLogin(PlayerApiAccountVo playerApiAccountVo){
        String msg = "";
        playerApiAccountVo.setDemoModel(SessionManagerCommon.getDemoModelEnum());
        boolean mockAccount = checkMockAccount(playerApiAccountVo);

        if(!mockAccount){
            LOG.info("判断是否可以登录游戏：{1}_{2},mockAccount:{0}",mockAccount,SessionManagerBase.getSiteId(),playerApiAccountVo.getGameCode());
           msg = setMsg(MessageI18nConst.MOCKMODEL_NOT_ALLOW_LOGIN, Module.Passport.getCode());
        }

        //api是否允许登陆
        boolean isAllowApi = checkApiStatus(playerApiAccountVo.getApiId());
        if (!isAllowApi) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowApi:{0}",isAllowApi,SessionManagerBase.getSiteId(),playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.API_MAINTAIN, Module.Passport.getCode());
        }

        //游戏是否允许登陆
        boolean isAllowGame = isAllowGame(playerApiAccountVo);
        if (!isAllowGame) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowGame:{0}",isAllowGame,SessionManagerBase.getSiteId(),playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.NOT_GAME_LOGIN, Module.Passport.getCode());
        }

        boolean demeModelIsAllowLogin = isAllowDemoModelLogin(playerApiAccountVo);
        if (!demeModelIsAllowLogin) {
            LOG.info("判断是否可以登录游戏：{1}_{2},isAllowDemoModelLogin:{0}",demeModelIsAllowLogin,SessionManagerBase.getSiteId(),playerApiAccountVo.getGameCode());
            msg = setMsg(MessageI18nConst.DEMOMODEL_NOT_ALLOW_LOGIN, Module.Passport.getCode());
        }
        return msg;
    }

    /**
     * 试玩模式是否可以登录
     * @param playerApiAccountVo
     * @return
     */
    protected boolean isAllowDemoModelLogin(PlayerApiAccountVo playerApiAccountVo){
        DemoModelEnum demoModel = playerApiAccountVo.getDemoModel();
        if(demoModel == null ){
            return true;
        }
        //checkMockAccount已经验证
        if(DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel)){
            if(playerApiAccountVo.getApiId()==Integer.valueOf(ApiProviderEnum.PL.getCode())
                    || playerApiAccountVo.getApiId()==Integer.valueOf(ApiProviderEnum.DWT.getCode())){
                return true;
            }
        }
        //彩票试玩，如果是龙头彩票，就可以玩
        if(DemoModelEnum.MODEL_4_LOTTERY.equals(demoModel)){
            if(playerApiAccountVo.getApiId()==Integer.valueOf(ApiProviderEnum.PL.getCode())){
                return true;
            }
        }
        GameVo gameVo = fetchLoginGame(playerApiAccountVo);
        if(gameVo.getResult()!=null){
            Boolean canTry = gameVo.getResult().getCanTry();
            LOG.info("判断是否可以登录游戏：{1}_{2},canTry:{0}",canTry,SessionManagerBase.getSiteId(),playerApiAccountVo.getGameCode());
            if(canTry==null||!canTry){
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
        if (apiId == null){
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

    private boolean checkMockAccount(PlayerApiAccountVo playerApiAccountVo){
        DemoModelEnum demoModelEnum = playerApiAccountVo.getDemoModel();
        if(demoModelEnum !=null){
            //模拟账号只能登录自主彩票及自主体育
            if(DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModelEnum)){
                if(playerApiAccountVo.getApiId()!=Integer.valueOf(ApiProviderEnum.PL.getCode())
                        && playerApiAccountVo.getApiId()!=Integer.valueOf(ApiProviderEnum.DWT.getCode())){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取国际化信息
     * @param msgConst
     * @param code
     * @return
     */
    private String setMsg(String msgConst, String code){
       return LocaleTool.tranMessage(code, msgConst);
    }
}
