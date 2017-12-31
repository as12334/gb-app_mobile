package so.wwb.gamebox.mobile.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.commons.security.CryptoTool;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import org.springframework.ui.Model;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.Game;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.content.po.CttCarouselI18n;
import so.wwb.gamebox.model.master.content.po.CttFloatPic;
import so.wwb.gamebox.model.master.content.po.CttFloatPicItem;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.CommonStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerWithdraw;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.*;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.PlayerRecommendAward;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.company.site.po.AppSiteApiTypeRelastionVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

import static org.soul.web.tag.ImageTag.getImagePath;
import static so.wwb.gamebox.model.CacheBase.getSiteGameName;

/**
 * Created by LeTu on 2017/3/31.
 */
public abstract class BaseApiController extends BaseDemoController {
    private Log LOG = LogFactory.getLog(BaseApiController.class);
    private static final int PROMO_RECORD_DAYS = -7;
    private static final int RECOMMEND_DAYS = -1;

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
            gameStatus = siteGame.getSystemStatus();
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
        if (api != null && GameStatusEnum.NORMAL.getCode().equals(api.getSystemStatus()) && siteApi != null && GameStatusEnum.NORMAL.getCode().equals(siteApi.getSystemStatus())) {
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
                    cover = getImagePath( SessionManager.getDomain(request),cover);
                    m.put("cover",cover);
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
    private SiteGameListVo getGames(SiteGameListVo listVo) {
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
        listVo = getGames(listVo);
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

    protected void initQueryDate(VPlayerTransactionListVo listVo) {
        final int DEFAULT_TIME = -6;
        listVo.setMinDate(SessionManager.getDate().addDays(DEFAULT_TIME));
        if (listVo.getSearch().getBeginCreateTime() == null) {
            listVo.getSearch().setBeginCreateTime(SessionManager.getDate().addDays(DEFAULT_TIME));
        } else if (listVo.getSearch().getBeginCreateTime().before(listVo.getMinDate())) {
            listVo.getSearch().setBeginCreateTime(listVo.getMinDate());
        }
        if (listVo.getSearch().getEndCreateTime() == null) {
            listVo.getSearch().setEndCreateTime(SessionManager.getDate().getNow());
        }
    }


    private IPlayerTransferService playerTransferService;
    private IPlayerTransferService playerTransferService() {
        if (playerTransferService == null)
            playerTransferService = ServiceTool.playerTransferService();
        return playerTransferService;
    }
    /**
     * 取款处理中/转账处理中的金额
     */
    protected void getFund(Map map) {
        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(SessionManager.getUserId());

        map.put("withdrawSum", ServiceTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));
        if (!ParamTool.isLotterySite()) {
            //正在转账中金额
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
//            model.addAttribute("transferSum", playerTransferService().queryProcessAmount(playerTransferVo));
            map.put("transferSum", playerTransferService().queryProcessAmount(playerTransferVo));
        }
    }

    protected VPlayerTransactionListVo preList(VPlayerTransactionListVo playerTransactionListVo) {
        Map<String, Serializable> transactionMap = DictTool.get(DictEnum.COMMON_TRANSACTION_TYPE);
        if (transactionMap != null) {   // 过滤转账类型
            transactionMap.remove(TransactionTypeEnum.TRANSFERS.getCode());
        }
        playerTransactionListVo.setDictCommonTransactionType(transactionMap);
        Map<String, Serializable> dictCommonStatus = DictTool.get(DictEnum.COMMON_STATUS);
        /*删掉稽核失败待处理状态*/
        dictCommonStatus.remove(CommonStatusEnum.DEAL_AUDIT_FAIL.getCode());
        playerTransactionListVo.setDictCommonStatus(dictCommonStatus);
        /*将 返水 推荐 的成功状态 修改为已发放*/
        Criteria criteriaType = Criteria.add(VPlayerTransaction.PROP_TRANSACTION_TYPE, Operator.IN, ListTool.newArrayList(TransactionTypeEnum.BACKWATER.getCode(), TransactionTypeEnum.RECOMMEND.getCode()));
        Criteria criteria = Criteria.add(VPlayerTransaction.PROP_STATUS, Operator.EQ, CommonStatusEnum.LSSUING.getCode());
        if (!playerTransactionListVo.getResult().isEmpty()) {
            CollectionTool.batchUpdate(playerTransactionListVo.getResult(), Criteria.and(criteria, criteriaType), MapTool.newHashMap(new Pair<String, Object>(VPlayerTransaction.PROP_STATUS, CommonStatusEnum.SUCCESS.getCode())));
        }
        return playerTransactionListVo;
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
     * 获取我的个人数据
     */
    protected void getUserInfo(Map<String, Object> userInfo, HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        Integer userId = SessionManager.getUserId();
        try {
            //总资产
            PlayerApiListVo playerApiListVo = new PlayerApiListVo();
            playerApiListVo.getSearch().setPlayerId(userId);
            playerApiListVo.setApis(Cache.getApi());
            playerApiListVo.setSiteApis(Cache.getSiteApi());
            double totalAssets = ServiceTool.playerApiService().queryPlayerAssets(playerApiListVo);
            userInfo.put("totalAssets", totalAssets);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        //钱包余额
        userInfo.put("walletBalance", getWalletBalance(userId));

        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(userId);
        userInfo.put("withdrawAmount", ServiceTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));

        //正在处理中转账金额(额度转换)
        PlayerTransferVo playerTransferVo = new PlayerTransferVo();
        playerTransferVo.getSearch().setUserId(userId);
        userInfo.put("transferAmount", ServiceTool.playerTransferService().queryProcessAmount(playerTransferVo));

        //计算近7日收益（优惠金额）
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();
        vPreferentialRecodeListVo.getSearch().setUserId(userId);
        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo.getSearch().setCheckState(ActivityApplyCheckStatusEnum.SUCCESS.getCode());
        vPreferentialRecodeListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), PROMO_RECORD_DAYS));
        vPreferentialRecodeListVo.setPropertyName(VPreferentialRecode.PROP_PREFERENTIAL_VALUE);
        userInfo.put("preferentialAmount", ServiceTool.vPreferentialRecodeService().sum(vPreferentialRecodeListVo));

        //银行卡信息
        List<UserBankcard> userBankcards = BankHelper.getUserBankcardList();
        Map<String, String> bankcardNumMap = new HashMap<>(1, 1f);
        for (UserBankcard userBankcard : userBankcards) {
            int length = userBankcard.getBankcardNumber().length();
            if (UserBankcardTypeEnum.BITCOIN.getCode().equals(userBankcard.getType())) {
                userInfo.put("btcNum", StringTool.overlay(userBankcard.getBankcardNumber(), "*", 0, length - 4));
            } else {
                bankcardNumMap.put(UserBankcard.PROP_BANK_NAME, userBankcard.getBankName());
                bankcardNumMap.put(UserBankcard.PROP_BANKCARD_NUMBER, StringTool.overlay(userBankcard.getBankcardNumber(), "*", 0, length - 4));
                userInfo.put("bankcard", bankcardNumMap);
            }
        }

        //推荐好友,昨日收益
        PlayerRecommendAwardListVo playerRecommendAwardListVo = new PlayerRecommendAwardListVo();
        playerRecommendAwardListVo.getSearch().setUserId(userId);
        playerRecommendAwardListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), RECOMMEND_DAYS));
        playerRecommendAwardListVo.getSearch().setEndTime(SessionManager.getDate().getToday());
        userInfo.put("recomdAmount", ServiceTool.playerRecommendAwardService().searchRecomdAmount(playerRecommendAwardListVo, PlayerRecommendAward.PROP_REWARD_AMOUNT));

        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long number = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        VPlayerAdvisoryListVo listVo = new VPlayerAdvisoryListVo();
        listVo.setSearch(null);
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceTool.vPlayerAdvisoryService().search(listVo);
        Integer advisoryUnReadCount = 0;
        String tag = "";
        //所有咨询数据
        for (VPlayerAdvisory obj : listVo.getResult()) {
            //查询回复表每一条在已读表是否存在
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);
            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
                readVo.setResult(new PlayerAdvisoryRead());
                readVo.getSearch().setUserId(SessionManager.getUserId());
                readVo.getSearch().setPlayerAdvisoryReplyId(replay.getId());
                readVo = ServiceTool.playerAdvisoryReadService().search(readVo);
                //不存在未读+1，标记已读咨询Id
                if (readVo.getResult() == null && !tag.contains(replay.getPlayerAdvisoryId().toString())) {
                    advisoryUnReadCount++;
                    tag += replay.getPlayerAdvisoryId().toString() + ",";
                }
            }
        }
        //判断已标记的咨询Id除外的未读咨询id,添加未读标记isRead=false;
        String[] tags = tag.split(",");
        for (VPlayerAdvisory vo : listVo.getResult()) {
            for (int i = 0; i < tags.length; i++) {
                if (tags[i] != "") {
                    VPlayerAdvisoryVo pa = new VPlayerAdvisoryVo();
                    pa.getSearch().setId(Integer.valueOf(tags[i]));
                    VPlayerAdvisoryVo vpaVo = ServiceTool.vPlayerAdvisoryService().get(pa);
                    if (vo.getId().equals(vpaVo.getResult().getContinueQuizId()) || vo.getId().equals(vpaVo.getResult().getId())) {
                        vo.setIsRead(false);
                    }
                }
            }
        }
        userInfo.put("unReadCount", number + advisoryUnReadCount);
        //用户个人信息
        userInfo.put("username", StringTool.overlayString(sysUser.getUsername()));
        userInfo.put("avatarUrl", ImageTag.getThumbPathWithDefault(SessionManager.getDomain(request), sysUser.getAvatarUrl(), 46, 46, null));
        //有上次登录时间就不展示本次登录时间，否则展示本次登录时间
        if (sysUser.getLastLoginTime() != null) {
            userInfo.put("lastLoginTime", LocaleDateTool.formatDate(sysUser.getLastLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        } else if (sysUser.getLoginTime() != null) {
            userInfo.put("loginTime", LocaleDateTool.formatDate(sysUser.getLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        }
        userInfo.put("currency", getCurrencySign());
    }

    protected Double getWalletBalance(Integer userId) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(userId);
        userPlayerVo = ServiceTool.userPlayerService().get(userPlayerVo);
        UserPlayer player = userPlayerVo.getResult();
        if (player == null) {
            return 0.0d;
        } else {
            Double balance = player.getWalletBalance();
            return balance == null ? 0.0d : balance;
        }
    }

    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 取款
     */
    protected void withdraw(Map map) {
        Map tempMap = MapTool.newHashMap();

        //是否存在取款订单
        boolean hasOrder = hasOrder();

        //取款时同步彩票余额
        double apiBalance = 0;
        if (ParamTool.isLotterySite()) {
            apiBalance = queryLotteryApiBalance();
        }

        //判断玩家是否冻结
        boolean hasFreeze = hasFreeze(tempMap);

        //是否达到取款上限
        boolean isFull = isFull(tempMap);
        PlayerRank rank = (PlayerRank) tempMap.get("rank");
        UserPlayer user = (UserPlayer) tempMap.get("player");
        Double totalBalance = user.getWalletBalance() + apiBalance;

        if (rank.getWithdrawMinNum() > totalBalance) {
            map.put("balanceLess", true);
            map.put("balanceMin", rank.getWithdrawMinNum());
        }
    }

    private double queryLotteryApiBalance() {
        PlayerApiVo apiVo = new PlayerApiVo();
        apiVo.getSearch().setApiId(Integer.valueOf(ApiProviderEnum.PL.getCode()));
        apiVo.getSearch().setPlayerId(SessionManagerBase.getUserId());
        double apiBalance = ServiceTool.playerApiService().queryApiBalance(apiVo);

        return apiBalance;
    }

    /**
     * 查询是否已存在取款订单
     */
    protected boolean hasOrder() {
        if (SessionManagerCommon.getUserId() == null) {
            return true;
        }
        PlayerWithdrawVo vo = new PlayerWithdrawVo();
        vo.setResult(new PlayerWithdraw());
        vo.getSearch().setPlayerId(SessionManagerCommon.getUserId());
        Long result = ServiceTool.playerWithdrawService().existPlayerWithdrawCount(vo);
        boolean hasOrder = result > 0;
        LOG.info("玩家{0}取款订单是否已存在{1}", SessionManagerCommon.getUserName(), hasOrder);
        return hasOrder;
    }

    /**
     * 验证是否余额冻结
     *
     * @param map
     * @return
     */
    public boolean hasFreeze(Map map) {
        UserPlayer player = getPlayer();
        map.put("player", player);
        return hasFreeze(map, player);
    }

    public boolean hasFreeze(Map map, UserPlayer player) {
        map.put("currencySign", getCurrencySign(SessionManagerCommon.getUser().getDefaultCurrency()));
        boolean hasFreeze = player.getBalanceFreezeEndTime() != null
                && player.getBalanceFreezeEndTime().getTime() > SessionManagerCommon.getDate().getNow().getTime();
        map.put("hasFreeze", hasFreeze);
        LOG.info("取款玩家{0}是否冻结{1}", SessionManagerCommon.getUserName(), hasFreeze);

        return hasFreeze;
    }

    private String getCurrencySign(String currency) {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(SessionManagerCommon.getUser().getDefaultCurrency());
        if (sysCurrency != null && StringTool.isNotBlank(sysCurrency.getCurrencySign())) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 验证是否今日取款是否达到上限
     *
     * @param map
     * @return
     */
    private boolean isFull(Map map) {
        PlayerRank rank = getRank();
        return isFull(map, rank);
    }

    /**
     * 获取玩家层级
     *
     * @return 层级信息
     */
    private PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManagerCommon.getUserId());
        return ServiceTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    /**
     * 验证是否今日取款是否达到上限
     *
     * @param rank
     * @return
     */
    private boolean isFull(Map map, PlayerRank rank) {
        //层级信息
        map.put("rank", rank);
        int count = get24HHasCount();
        if (rank.getIsWithdrawLimit() != null && rank.getIsWithdrawLimit() && rank.getWithdrawCount() != null && count >= rank.getWithdrawCount()) {
            // 已达取款次数上限
            map.put("isFull", true);
            LOG.info("取款玩家{0}取款次数已达到上限{1},当前玩家取款次数{2}", SessionManagerCommon.getUserName(), rank.getWithdrawCount(), count);
            return true;
        }
        if (rank.getWithdrawCount() != null) {
            // 还剩取款次数
            map.put("reminder", rank.getWithdrawCount() - count);
            LOG.info("取款玩家{0}取款次数{1},剩余取款次数{2}", SessionManagerCommon.getUserName(), count, rank.getWithdrawCount() - count);
        }
        return false;
    }

    /**
     * 取得24H内已取款次数
     */
    private Integer get24HHasCount() {
        Date nowTime = SessionManagerCommon.getDate().getToday(); // 今天零时时间

        PlayerWithdrawVo playVo = new PlayerWithdrawVo();
        playVo.getSearch().setCreateTime(nowTime);
        playVo.getSearch().setPlayerId(SessionManagerCommon.getUserId());
        Long count = ServiceTool.playerWithdrawService().searchPlayerWithdrawNum(playVo);
        count = (count == null) ? 0L : count;
        return count.intValue();
    }

    /**
     * 获取玩家信息
     *
     * @return 玩家信息
     */
    private UserPlayer getPlayer() {
        if (SessionManagerCommon.getUserId() == null) {
            return null;
        }
        UserPlayerVo playerVo = new UserPlayerVo();
        playerVo.getSearch().setId(SessionManagerCommon.getUserId());
        playerVo.setResult(new UserPlayer());
        playerVo = ServiceTool.userPlayerService().get(playerVo);
        return playerVo.getResult();
    }

    protected MobileActivityMessageVo getActivity(HttpServletRequest request){
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();

        Map<String,List<VActivityMessage>> activityMessage = MapTool.newHashMap();
        List<SiteI18n> siteI18nTemp = ListTool.newArrayList();

        for (SiteI18n site : siteI18nMap.values()) {
            if(StringTool.equalsIgnoreCase(site.getLocale(),SessionManager.getLocale().toString())){
                VActivityMessageListVo vActivityMessageListVo = new VActivityMessageListVo();
                vActivityMessageListVo.getSearch().setActivityClassifyKey(site.getKey());
                activityMessage.put(site.getKey(),setDefaultImage(getActivityMessage(vActivityMessageListVo),request));
                siteI18nTemp.add(site);
            }
        }
        MobileActivityMessageVo messageVo  = new MobileActivityMessageVo();
        messageVo.setTypeList(siteI18nTemp);
        messageVo.setTypeMessageMap(activityMessage);
        return messageVo;
    }

    /**
     * 获取正在进行中的活动
     */
    protected VActivityMessageListVo getActivityMessage(VActivityMessageListVo vActivityMessageListVo ){
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        //vActivityMessageListVo.getPaging().setPageSize(pageSize);
        vActivityMessageListVo.getSearch().setActivityClassifyKey(vActivityMessageListVo.getSearch().getActivityClassifyKey());
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            vActivityMessageListVo.getSearch().setRankId(ServiceTool.playerRankService().searchRankByPlayerId(sysUserVo).getId());
        }
        vActivityMessageListVo = ServiceTool.vActivityMessageService().getActivityList(vActivityMessageListVo);
        return vActivityMessageListVo;
    }

    protected List<VActivityMessage> setDefaultImage(VActivityMessageListVo vActivityMessageListVo, HttpServletRequest request){
        for(VActivityMessage a : vActivityMessageListVo.getResult()){
            String resRootFull = MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName());
            String activityAffiliated = ImageTag.getImagePathWithDefault(request.getServerName(),a.getActivityAffiliated(),resRootFull.concat("'/images/img-sale1.jpg'"));
            a.setActivityAffiliated(activityAffiliated);
        }
        return vActivityMessageListVo.getResult();
    }
}
