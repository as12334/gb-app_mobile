package so.wwb.gamebox.mobile.controller;

import org.apache.commons.collections.map.HashedMap;
import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.Paging;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ApiGameTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.Game;
import so.wwb.gamebox.model.company.setting.po.GameI18n;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.GameCacheEntity;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.company.site.vo.SiteGameTagVo;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 游戏Controller
 * Created by fei on 16-9-25.
 */
@Controller
@RequestMapping("/game")
public class GameController extends BaseApiController {

    private static final Log LOG = LogFactory.getLog(GameController.class);
    private static final Integer CASINO_PAGE_SIZE = 24;
    private static final Integer LOTTERY_PAGE_SIZE = 18;

    @RequestMapping("/getGame")
    public String getGameByApiTypeId(Integer apiType, Model model) {
        String redirectUrl = "/game";

//        VSiteApiListVo listVo = getSiteApiListVo(apiType);

//        Cache.getSiteApi();
        switch (apiType) {
            case 1:
                redirectUrl += "/Live";
                break;
            case 2:
                redirectUrl += "/Casino";
                break;
            case 3:
                redirectUrl += "/Sport";
                break;
            case 4:
                redirectUrl += "/Lottery";
                break;
            case 5:
                redirectUrl += "/Chess";
                break;
        }
        model.addAttribute("command", CollectionQueryTool.sort(getSiteApiRelation(apiType), Order.asc(SiteApiTypeRelation.PROP_MOBILE_ORDER_NUM)));
        return redirectUrl;
    }

    public List<SiteApiTypeRelation> getSiteApiTypeRelationList(Integer apiType) {
        Map<String, List<SiteApiTypeRelation>> typeRelationMap = Cache.getSiteApiTypeRelation(SessionManager.getSiteId());
        List<SiteApiTypeRelation> typeRelationList = new ArrayList<>();
        //参数为空时查询所有API
        if (apiType != null)
            typeRelationList = typeRelationMap.get(String.valueOf(apiType));
        else {
            for (List<SiteApiTypeRelation> siteApiTypeRelation : typeRelationMap.values()) {
                typeRelationList.addAll(siteApiTypeRelation);
            }
        }

        List<SiteApiTypeRelation> delList = new ArrayList<>();
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        //typeRelationMap获取的apiType为空时会出现空指针
        if (typeRelationList == null) {
            typeRelationList = new ArrayList<>();
        }
        for (SiteApiTypeRelation r : typeRelationList) {
            Api api = apiMap.get(String.valueOf(r.getApiId()));
            SiteApi siteApi = siteApiMap.get(String.valueOf(r.getApiId()));
            if (api == null || siteApi == null)
                continue;
            if (GameSupportTerminalEnum.PC.getCode().equals(api.getTerminal())) {//去掉只支持PC的
                delList.add(r);
                continue;
            }
            if (GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus())) {
                delList.add(r);
            }
        }
        typeRelationList.removeAll(delList);

        return CollectionQueryTool.sort(typeRelationList, Order.asc(SiteApiTypeRelation.PROP_ORDER_NUM));
    }

    /**
     * mobile-v3 电子游戏访问这个方法
     *
     * @param listVo
     * @param model
     * @return
     */
    @RequestMapping("/getCasinoByApiId")
    @Upgrade(upgrade = true)
    public String getGameByApiIdV3(SiteGameListVo listVo, Model model) {
        String redirectUrl = "/game/%sPartial";
        SiteGameSo so = listVo.getSearch();
        redirectUrl = getRedirectUrl(listVo, redirectUrl);
        Integer apiId = so.getApiId();
        if (apiId == null) {
            return redirectUrl;
        }
        model.addAttribute("apiId", apiId);
        Map<String, LinkedHashMap<String, GameCacheEntity>> gameByApiMap = Cache.getMobileGameCacheEntity(String.valueOf(ApiTypeEnum.CASINO.getCode()));
        LinkedHashMap<String, GameCacheEntity> gameMap = gameByApiMap == null ? null : gameByApiMap.get(String.valueOf(apiId));
        if (MapTool.isEmpty(gameMap)) {
            return redirectUrl;
        }
        Map<String, SiteGameTag> gameTagMap = Cache.getSiteGameTag();
        //游戏标签
        Map<String, String> tagName = getGameTagMap(listVo);
        Map<String, List<GameCacheEntity>> tagGameMap = new LinkedHashMap<>();
        List<GameCacheEntity> games;
        String tagId;
        GameCacheEntity game;
        model.addAttribute("allGames", gameMap.values());
        if(MapTool.isEmpty(tagName)) {
            return redirectUrl;
        }
        for (String tagStr : tagName.keySet()) {
            games = new ArrayList<>();
            for (SiteGameTag gameTag : gameTagMap.values()) {
                tagId = gameTag.getTagId();
                if (!tagId.equals(tagStr)) {
                    continue;
                }
                game = gameMap.get(String.valueOf(gameTag.getGameId()));
                if (game == null) {
                    continue;
                }
                games.add(game);
            }
            tagGameMap.put(tagStr, games);
        }
        model.addAttribute("tagGameMap", tagGameMap);
        model.addAttribute("tagName", tagName);
        return redirectUrl;
    }

    /**
     * 根据tagid取游戏
     *
     * @param tagId
     * @return
     */
    private List<Integer> getGamesByTagId(Map<String, SiteGameTag> gameTagMap, String tagId) {

        List<Integer> gameIds = new ArrayList<>();
        for (SiteGameTag gameTag : gameTagMap.values()) {
            if (StringTool.equalsIgnoreCase(tagId, gameTag.getTagId())) {
                gameIds.add(gameTag.getGameId());
            }
        }
        return gameIds;
    }

    /**
     * 查询 该apiId 和 apiTypeId 全部游戏
     *
     * @param listVo
     * @return
     */

    protected Map<String, SiteGame> getGameListByApiIdAndApiTypeId(SiteGameListVo listVo) {
        SiteGameSo so = listVo.getSearch();
        Integer apiId = so.getApiId();
        Integer apiTypeId = so.getApiTypeId();
        if (apiId == null || apiId <= 0 || apiTypeId == null) {
            return new HashedMap();
        }

        //筛选的条件 类型 apiId,
        String name = so.getName();
        Map<String, SiteGame> siteGameMap = Cache.getSiteGame();
        List<SiteGame> siteGames = new ArrayList<>();
        Map<String, SiteGame> siteGamesMap = new HashedMap();
        Integer gameId;
        Map<String, SiteGameI18n> siteGameI18nMap = Cache.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = Cache.getGameI18n();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        String terminal = TerminalEnum.MOBILE.getCode();
        Map<String, Game> gameMap = Cache.getGame();
        Game game;

        for (SiteGame siteGame : siteGameMap.values()) {
            //不属于该api分类下包含
            if (!terminal.equals(siteGame.getSupportTerminal()) || apiId.intValue() != siteGame.getApiId() || apiTypeId.intValue() != siteGame.getApiTypeId()) {
                continue;
            }
            //游戏维护或者已停用不包含
            if (disable.equals(siteGame.getSystemStatus()) || maintain.equals(siteGame.getSystemStatus())) {
                continue;
            }
            gameId = siteGame.getGameId();
            game = gameMap.get(String.valueOf(gameId));
            if (game == null || disable.equals(game.getSystemStatus()) || maintain.equals(game.getSystemStatus())) {
                continue;
            }
            siteGame.setName(ApiGameTool.getSiteGameName(siteGameI18nMap, gameI18nMap, String.valueOf(gameId)));
            if (siteGameI18nMap.get(siteGame.getGameId().toString()) != null) {
                siteGame.setCover(siteGameI18nMap.get(siteGame.getGameId().toString()).getCover());
            }
            if (StringTool.isNotBlank(name) && !siteGame.getName().contains(name)) {
                continue;
            }
            siteGames.add(siteGame);
            siteGamesMap.put(siteGame.getGameId().toString(), siteGame);
        }
        if (CollectionTool.isEmpty(siteGames)) {
            return new HashedMap();
        }

        return siteGamesMap;
    }


    /**
     * 获取全部游戏标签
     *
     * @return
     */
    private Map<String, String> getGameTagMap(SiteGameListVo listVo) {
        Map<String, String> gameTagMap = new HashedMap();
        if (listVo.getSearch().getApiId() == null || listVo.getSearch().getApiTypeId() == null) {
            return gameTagMap;
        }

        SiteGameTagVo tagVo = new SiteGameTagVo();
        tagVo.setApiId(listVo.getSearch().getApiId());
        tagVo.setApiTypeId(listVo.getSearch().getApiTypeId());
        tagVo.getSearch().setSiteId(SessionManager.getSiteId());
        List<String> siteTagIds = ServiceSiteTool.siteGameTagService().searchTagId(tagVo);
        Map<String, String> tagNameMap = getTagNameMap();

        for (String tagId : siteTagIds) {
            if (tagId != null && StringTool.isNotBlank(tagNameMap.get(tagId))) {
                gameTagMap.put(tagId, tagNameMap.get(tagId));
            }
        }
        return gameTagMap;
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


    @RequestMapping("/getGameByApiId")
    public String getGameByApiId(SiteGameListVo listVo, Model model) {
        String redirectUrl = "/game/%sPartial";
        SiteGameSo so = listVo.getSearch();

        redirectUrl = getRedirectUrl(listVo, redirectUrl);

        Integer apiId = so.getApiId();
        if (NumberTool.isNumber(String.valueOf(apiId)) && apiId > 0) {
            listVo = getGames(listVo);
            model.addAttribute("gameI18n", getGameI18nMap(listVo));
        }
        model.addAttribute("apiId", apiId);
        model.addAttribute("command", listVo);

        return redirectUrl;
    }

    @RequestMapping("/getCasinoGameByApiId")
    @Upgrade(upgrade = true)
    public String getCasinoGameByApiId(SiteGameListVo listVo, Model model) throws UnsupportedEncodingException {
        /*if (StringTool.isNotBlank(listVo.getSearch().getName())) {
            String gameName = URLDecoder.decode(listVo.getSearch().getName(),"UTF-8");//转码，并替换掉list.search()中
            listVo.getSearch().setName(gameName);
        }*/

        SiteGameSo so = listVo.getSearch();

        Integer apiId = so.getApiId();
        if (NumberTool.isNumber(String.valueOf(apiId)) && apiId > 0) {
            listVo = getGames(listVo);
            model.addAttribute("gameI18n", getGameI18nMap(listVo));
        }
        model.addAttribute("apiId", apiId);
        model.addAttribute("command", listVo);

        return "/game/PullfreshCasinoPartial";
    }

    /**
     * 获取api游戏维护时间
     *
     * @param apiId
     * @return
     */
    @RequestMapping("/getApiMaintain")
    @ResponseBody
    public Map getApiGameStatus(Integer apiId) {
        if (apiId == null) {
            return new HashMap();
        }

        Map<String, Object> map = new HashMap<>(3, 1f);
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        for (Api api : apiMap.values()) {
            if (api.getId().equals(apiId)) {
                map.put("maintainStartTime", LocaleDateTool.formatDate(api.getMaintainStartTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
                map.put("maintainEndTime", LocaleDateTool.formatDate(api.getMaintainEndTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
                break;
            }
        }
        for (ApiI18n apiI18n : apiI18nMap.values()) {
            if (StringTool.equals(apiI18n.getLocale(), SessionManager.getLocale().toString())
                    && apiI18n.getApiId().equals(apiId)) {
                map.put("gameName", apiI18n.getName());
                break;
            }
        }
        map.put("timeZone", SessionManager.getTimeZone());

        return map;
    }

    /**
     * 根据API-TYPE跳转不同的页面
     * 顺带设置每种Type每页显示数量
     */
    private String getRedirectUrl(SiteGameListVo listVo, String redirectUrl) {
        Paging paging = listVo.getPaging();
        // 电子
        if (listVo.getSearch().getApiTypeId() != null && ApiTypeEnum.CASINO.getCode() == listVo.getSearch().getApiTypeId()) {
            paging.setPageSize(CASINO_PAGE_SIZE);
            redirectUrl = String.format(redirectUrl, "Casino");
        } else if (listVo.getSearch().getApiTypeId() != null && ApiTypeEnum.LOTTERY.getCode() == listVo.getSearch().getApiTypeId()) { // 彩票
            paging.setPageSize(LOTTERY_PAGE_SIZE);
            redirectUrl = String.format(redirectUrl, "Lottery");
        } else if (listVo.getSearch().getApiTypeId() != null && ApiTypeEnum.CHESS.getCode() == listVo.getSearch().getApiTypeId()) {
            redirectUrl = String.format(redirectUrl, "Chess");
        }
        return redirectUrl;
    }

    /**
     * 获取游戏
     */
    private SiteGameListVo getGames(SiteGameListVo listVo) {
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

    @Override
    protected String getDemoIndex() {
        return null;
    }

    /** ------------------------ 华丽丽的分割线 ------------------------ **/

    /**
     * 进入游戏
     *
     * @param url   游戏URL
     * @param first 是否首次进入
     * @param idx   是否首页进入
     */
    @RequestMapping("/openGame")
    public String openGame(String apiId, String url, boolean first, boolean idx, Model model) {
        model.addAttribute("url", ServletTool.formatUrl(url));
        model.addAttribute("apiId", apiId);
        model.addAttribute("flag", 1);  // 显示关闭
        model.addAttribute("first", first);
        model.addAttribute("idx", idx);
        return "/game/Game";
    }

    @RequestMapping("/hotGames")
    public String hotGames(SiteGameListVo listVo, Model model) {
        Integer fishId = 90114;
        int siteId = SessionManager.getSiteId();
        if (siteId == 1 || siteId == 35 || siteId == 185) {
            fishId = 280004;
        }
        List<Integer> gameIds = Arrays.asList(fishId, 31000, 31009, 31050, 90071, 100303, 100222, 100302, 31077, 31002, 31011, 150135, 150141);//游戏ID
        //查询指定游戏
        Criteria criteria = Criteria.add(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        criteria.addAnd(SiteGame.PROP_STATUS, Operator.NE, GameStatusEnum.DISABLE.getCode());
        List<SiteGame> games = CollectionQueryTool.query(Cache.getSiteGame().values(), criteria);
        List<SiteGame> newGames = new ArrayList<>();
        SiteGame fish = new SiteGame();
        //查询状态
        for (SiteGame siteGame : games) {
            listVo.getSearch().setApiId(siteGame.getApiId());
            List<SiteGame> siteGameList = new ArrayList<>();
            siteGameList.add(siteGame);
            if (siteGame.getGameId().equals(fishId)) {
                fish = setGameStatus(listVo, siteGameList).get(0);
            } else {
                siteGameList = setGameStatus(listVo, siteGameList);
                if (CollectionTool.isNotEmpty(siteGameList)) {
                    newGames.add(siteGameList.get(0));
                }
            }
        }
        listVo.setResult(newGames);
        //获取推荐api状态
        model.addAttribute("apiStatus_16", getApiStatus(NumberTool.toInt(ApiProviderEnum.EBET.getCode())));
        model.addAttribute("apiStatus_19", getApiStatus(NumberTool.toInt(ApiProviderEnum.SB.getCode())));

        model.addAttribute("gameI18nMap", CollectionTool.toEntityMap(getGameI18n(listVo), SiteGameI18n.PROP_GAME_ID, String.class));
        model.addAttribute("hotGames", listVo.getResult());
        model.addAttribute("fish", fish);

        return "/game/HotGames";
    }

    @RequestMapping("/apiGames")
    public String getApiGames(Integer apiId, Integer apiTypeId, Model model, HttpServletRequest request) {
        model.addAttribute("sysUser", SessionManager.getUser());
        model.addAttribute("apiId", apiId);
        model.addAttribute("apiTypeId", apiTypeId);

        Map<String, SiteApiTypeRelationI18n> siteApiTypeRelationI18nMap = Cache.getSiteApiTypeRelactionI18n();
        Integer siteId = SessionManager.getSiteId();
        for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nMap.values()) {
            if (siteId.intValue() == siteApiTypeRelationI18n.getSiteId() && siteApiTypeRelationI18n.getApiTypeId().equals(apiTypeId)) {
                model.addAttribute("siteApi", siteApiTypeRelationI18n);
                break;
            }
        }
        model.addAttribute("sysDomain", IndexController.getSiteDomain(request));
        return "/game/ApiGame";
    }

}
