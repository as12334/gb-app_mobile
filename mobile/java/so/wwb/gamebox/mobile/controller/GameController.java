package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.Paging;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.security.HttpTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.Game;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.SiteApiTypeRelationI18nListVo;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.company.site.vo.VSiteApiListVo;
import so.wwb.gamebox.model.company.site.vo.VSiteApiVo;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.tools.ServiceTool.vSiteApiService;

/**
 * 游戏Controller
 * Created by fei on 16-9-25.
 */
@Controller
@RequestMapping("/game")
public class GameController {

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
        }
        model.addAttribute("command", getSiteApiTypeRelationList(apiType));
        return redirectUrl;
    }

    private VSiteApiListVo getSiteApiListVo(Integer apiType) {
        VSiteApiListVo listVo = new VSiteApiListVo();
        listVo.getSearch().setStatus(GameStatusEnum.DISABLE.getCode());
        listVo.getSearch().setSiteId(SessionManager.getSiteId());
        listVo.getSearch().setLocale(SessionManager.getSiteLocale().toString());
        listVo.getSearch().setApiTypeId(apiType);
        listVo = vSiteApiService().queryMobileSiteApi(listVo);
        if(apiType.equals(4)){
            listVo.setResult(CollectionQueryTool.sort(listVo.getResult(), Order.desc(VSiteApi.PROP_API_ID)));
        }
        return listVo;
    }
    public List<SiteApiTypeRelation> getSiteApiTypeRelationList(Integer apiType) {
        Map<String, List<SiteApiTypeRelation>>  typeRelationMap =  Cache.getSiteApiTypeRelation(SessionManager.getSiteId());
        List<SiteApiTypeRelation> typeRelationList = new ArrayList<>();
        //参数为空时查询所有API
        if(apiType!=null)
            typeRelationList = typeRelationMap.get(String.valueOf(apiType));
        else{
            for(List<SiteApiTypeRelation> siteApiTypeRelation: typeRelationMap.values()){
                typeRelationList.addAll(siteApiTypeRelation);
            }
        }

        List<SiteApiTypeRelation> delList = new ArrayList<>();
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        for (SiteApiTypeRelation r : typeRelationList){
            Api api = apiMap.get(String.valueOf(r.getApiId()));
            SiteApi siteApi = siteApiMap.get(String.valueOf(r.getApiId()));
            if (api == null || siteApi == null)
                continue;
            if (GameSupportTerminalEnum.PC.getCode().equals(api.getTerminal())){//去掉只支持PC的
                delList.add(r);
                continue;
            }
            if (GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus())){
                delList.add(r);
            }
        }
        typeRelationList.removeAll(delList);

        return CollectionQueryTool.sort(typeRelationList,Order.asc(SiteApiTypeRelation.PROP_ORDER_NUM));
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

    /**
     * 根据API-TYPE跳转不同的页面
     * 顺带设置每种Type每页显示数量
     */
    private String getRedirectUrl(SiteGameListVo listVo, String redirectUrl) {
        Paging paging = listVo.getPaging();
        // 电子
        if (listVo.getSearch().getApiTypeId()!=null && ApiTypeEnum.CASINO.getCode() == listVo.getSearch().getApiTypeId()) {
            paging.setPageSize(CASINO_PAGE_SIZE);
            redirectUrl = String.format(redirectUrl, "Casino");
        } else { // 彩票
            paging.setPageSize(LOTTERY_PAGE_SIZE);
            redirectUrl = String.format(redirectUrl, "Lottery");
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

    /**
     * 游戏过滤 - 总控录入的游戏status = normal && 站点游戏status = normal
     * @return
     */
    private List<SiteGame> getSiteGamesWhichIsNormalStatus(List<SiteGame> allSiteGames) {
        List<Game> allGames = new ArrayList<>(Cache.getGame().values());
        // 维护与正常的status都为normal
        allGames = CollectionQueryTool.query(allGames, Criteria.add(SiteGame.PROP_STATUS, Operator.EQ, GameStatusEnum.NORMAL.getCode())
                .addAnd(Game.PROP_SUPPORT_TERMINAL,Operator.EQ, GameSupportTerminalEnum.PHONE.getCode()));
        List<Integer> normalGameIdList = (List<Integer>) CollectionTool.intersection(CollectionTool.extractToList(allGames,Game.PROP_ID),CollectionTool.extractToList(allSiteGames,SiteGame.PROP_GAME_ID));
        if(normalGameIdList.size() != 0){
            return CollectionQueryTool.query(allSiteGames,Criteria.add(SiteGame.PROP_GAME_ID,Operator.IN,normalGameIdList));
        }else{
            return null;
        }
    }

    /**
     * 设置查询条件
     */
    private Criteria getQueryGameCriteria(SiteGameSo so, List<SiteGameI18n> siteGameI18n) {
        Criteria criteria = Criteria.add(SiteGame.PROP_API_TYPE_ID, Operator.EQ, so.getApiTypeId())
                .addAnd(Criteria.add(SiteGame.PROP_API_ID, Operator.EQ, so.getApiId()))
                .addAnd(Criteria.add(SiteGame.PROP_TERMINAL, Operator.EQ, SupportTerminal.PHONE.getCode()))
                .addAnd(Criteria.add(SiteGame.PROP_STATUS, Operator.NE, GameStatusEnum.DISABLE.getCode()));

        List<Integer> gameIds = CollectionTool.extractToList(siteGameI18n, SiteGame.PROP_GAME_ID);
        if (gameIds != null && gameIds.size() == 0) {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.EQ, 0);
        } else {
            criteria.addAnd(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        }

        return criteria;
    }

    /**
     * 设置游戏状态
     */
    private List<SiteGame> setGameStatus(SiteGameListVo listVo, List<SiteGame> games) {
        List<SiteGame> siteGames = new ArrayList<>();
        VSiteApiVo vo = new VSiteApiVo();
        vo.getSearch().setSiteId(SessionManager.getSiteId());
        vo.getSearch().setApiId(listVo.getSearch().getApiId());
        String status = ServiceTool.vSiteApiService().queryOneApiStatus(vo);
        Date now = SessionManager.getDate().getNow();
        Collection<Game> allGames = Cache.getGame().values();
        for (SiteGame siteGame : games) {
            if (GameStatusEnum.MAINTAIN.getCode().equals(status)) {
                siteGame.setStatus(status);
            } else {
                for(Game game : allGames){
                    if(siteGame.getGameId().intValue() == game.getId().intValue()) {
                        if(game.getMaintainStartTime()!=null && game.getMaintainEndTime()!=null && game.getMaintainEndTime().compareTo(new Date())==1){
                            int diff1 = DateTool.truncatedCompareTo(now, game.getMaintainStartTime(), Calendar.SECOND);
                            int diff2 = DateTool.truncatedCompareTo(now, game.getMaintainEndTime(), Calendar.SECOND);
                            if (diff1 >= 0 && diff2 <= 0) {
                                siteGame.setStatus(GameStatusEnum.MAINTAIN.getCode());
                            }
                        }
                        siteGame.setCode(game.getCode());
                        break;
                    }
                }
            }
            siteGames.add(siteGame);
        }
        return siteGames;
    }

    /**
     * 获取游戏国际化数据
     */
    private Map<String, SiteGameI18n> getGameI18nMap(SiteGameListVo listVo) {
        return CollectionTool.toEntityMap(getGameI18n(listVo), SiteGameI18n.PROP_GAME_ID, String.class);
    }

    private List<SiteGameI18n> getGameI18n(SiteGameListVo listVo) {
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

    /** ------------------------ 华丽丽的分割线 ------------------------ **/

    /**
     * 进入游戏
     * @param url 游戏URL
     * @param first 是否首次进入
     * @param idx   是否首页进入
     */
    @RequestMapping("/openGame")
    public String openGame(String apiId, String url, boolean first, boolean idx, Model model) {
        model.addAttribute("url", HttpTool.formatUrl(url));
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
        List<Integer> gameIds = Arrays.asList(fishId,31000,31009,31050,90071,100303,100222,100302,31077,31002,31011,150135,150141);//游戏ID
        //查询指定游戏
        Criteria criteria = Criteria.add(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        List<SiteGame> games = CollectionQueryTool.query(Cache.getSiteGame().values(), criteria);
        List<SiteGame> newGames = new ArrayList<>();
        SiteGame fish = new SiteGame();
        //查询状态
        for(SiteGame siteGame:games){
            listVo.getSearch().setApiId(siteGame.getApiId());
            List<SiteGame> siteGameList = new ArrayList<>();
            siteGameList.add(siteGame);
            if(siteGame.getGameId().equals(fishId))
                fish = setGameStatus(listVo, siteGameList).get(0);
            else
                newGames.add(setGameStatus(listVo, siteGameList).get(0));
        }
        listVo.setResult(newGames);
        //获取推荐api状态
        VSiteApiVo vo = new VSiteApiVo();
        vo.getSearch().setSiteId(SessionManager.getSiteId());
        vo.getSearch().setApiId(16);
        model.addAttribute("apiStatus_16",ServiceTool.vSiteApiService().queryOneApiStatus(vo));

        vo.getSearch().setApiId(19);
        model.addAttribute("apiStatus_19",ServiceTool.vSiteApiService().queryOneApiStatus(vo));

        model.addAttribute("gameI18nMap",CollectionTool.toEntityMap(getGameI18n(listVo), SiteGameI18n.PROP_GAME_ID, String.class));
        model.addAttribute("hotGames",listVo.getResult());
        model.addAttribute("fish",fish);

        return "/game/HotGames";
    }
    @RequestMapping("/apiGames")
    public String getApiGames(Integer apiId,Integer apiTypeId, Model model,HttpServletRequest request){
        model.addAttribute("sysUser", SessionManager.getUser());
        model.addAttribute("apiId",apiId);
        model.addAttribute("apiTypeId",apiTypeId);

        SiteApiTypeRelationI18nListVo siteApiTypeRelationI18nListVo = new SiteApiTypeRelationI18nListVo();
        siteApiTypeRelationI18nListVo.getSearch().setSiteId(SessionManager.getSiteId());
        siteApiTypeRelationI18nListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        siteApiTypeRelationI18nListVo = ServiceTool.siteApiTypeRelationI18nService().getSiteApi(siteApiTypeRelationI18nListVo);
        for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nListVo.getResult()) {
            if(siteApiTypeRelationI18n.getApiId().equals(apiId) && siteApiTypeRelationI18n.getApiTypeId().equals(apiTypeId))
                model.addAttribute("siteApi",siteApiTypeRelationI18n);
        }
        model.addAttribute("sysDomain",IndexController.getSiteDomain(request));
        return "/game/ApiGame";
    }

}
