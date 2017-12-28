package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
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
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.site.po.*;
import so.wwb.gamebox.model.company.site.so.SiteGameSo;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        }
        model.addAttribute("command", getSiteApiTypeRelationList(apiType));
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

    @RequestMapping("/getGameByApiId")
    @Upgrade(upgrade = true)
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
    public String getCasinoGameByApiId(SiteGameListVo listVo, Model model) {
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
     * 根据API-TYPE跳转不同的页面
     * 顺带设置每种Type每页显示数量
     */
    private String getRedirectUrl(SiteGameListVo listVo, String redirectUrl) {
        Paging paging = listVo.getPaging();
        // 电子
        if (listVo.getSearch().getApiTypeId() != null && ApiTypeEnum.CASINO.getCode() == listVo.getSearch().getApiTypeId()) {
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
        //List<Integer> gameIds = Arrays.asList(fishId, 31000, 31009, 31050, 90071, 100303, 100222, 100302, 31077, 31002, 31011, 150135, 150141);//游戏ID
        List<Integer> gameIds = Arrays.asList(fishId, 31000, 31009, 31050, 90071, 100303, 100222, 100302, 31077, 31002, 31011);
        //查询指定游戏
        Criteria criteria = Criteria.add(SiteGame.PROP_GAME_ID, Operator.IN, gameIds);
        List<SiteGame> games = CollectionQueryTool.query(Cache.getSiteGame().values(), criteria);
        List<SiteGame> newGames = new ArrayList<>();
        SiteGame fish = new SiteGame();
        //查询状态
        for (SiteGame siteGame : games) {
            listVo.getSearch().setApiId(siteGame.getApiId());
            List<SiteGame> siteGameList = new ArrayList<>();
            siteGameList.add(siteGame);
            if (siteGame.getGameId().equals(fishId))
                fish = setGameStatus(listVo, siteGameList).get(0);
            else
                newGames.add(setGameStatus(listVo, siteGameList).get(0));
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
