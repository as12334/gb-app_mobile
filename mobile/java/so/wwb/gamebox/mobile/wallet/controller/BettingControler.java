package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.master.player.po.PlayerGameOrder;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderVo;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.report.betting.controller.BaseGameOrderController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**投注记录
 * Created by bill on 16-11-29.
 */
@Controller
@RequestMapping("/fund/betting")
public class BettingControler {
    private static final Log LOG = LogFactory.getLog(BaseGameOrderController.class);
    public static final String BETTING_URL = "/fund/betting/Index";
    public static final String BETTING_DETAIL_URL = "/fund/betting/Detail";
    /**
     * 查询最大时间间隔，以天为单位
     */
    private static final int TIME_INTERVAL = -30;
    private static final int DEFAULT_TIME = 1;

    /**
     * 查询投注记录列表
     * 按日期查询
     * @param listVo
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/index")
    public String getBettingList(PlayerGameOrderListVo listVo, Model model, HttpServletRequest request){
        String url=BETTING_URL;
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        //判断ajax请求
        if (ServletTool.isAjaxSoulRequest(request)) {
            //判断日期查询
            listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1), -1));
            url = BETTING_URL + "PartialList";
        }
//        model.addAttribute("statisticalData",statisticsData(listVo));
        initQueryDate(listVo);
        listVo = ServiceSiteTool.playerGameOrderService().search(listVo);
        setApiNameAndGame(listVo.getResult());
        model.addAttribute("command", listVo);

        //设置默认时间
        model.addAttribute("minDate", SessionManager.getDate().addDays(TIME_INTERVAL));
        model.addAttribute("maxDate", SessionManager.getDate().getNow());
        return url;
    }

    /**
     * 设置api名称和游戏名称
     *
     * @param playerGameOrders
     */
    private void setApiNameAndGame(List<PlayerGameOrder> playerGameOrders) {
        if (CollectionTool.isEmpty(playerGameOrders)) {
            return;
        }
        Map<Integer, Map<Integer, String>> apiNameGroupByApiType = ApiGameTool.getSiteApiNameByApiType(Cache.getSiteApiTypeRelactionI18n());
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteGameI18n> siteGameI18nMap = Cache.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = Cache.getGameI18n();
        Integer gameId;
        for (PlayerGameOrder playerGameOrder : playerGameOrders) {
            playerGameOrder.setApiName(ApiGameTool.getSiteApiName(apiNameGroupByApiType, siteApiI18nMap, apiI18nMap, playerGameOrder.getApiId(), playerGameOrder.getApiTypeId()));
            gameId = playerGameOrder.getGameId();
            if (gameId != null && gameId != 0) {
                playerGameOrder.setGameName(ApiGameTool.getSiteGameName(siteGameI18nMap, gameI18nMap, String.valueOf(gameId)));
            }
        }
    }

    /**
     * 投注记录详情
     *
     * @param playerGameOrderVo
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    public String queryVPlayerGameOrder(PlayerGameOrderVo playerGameOrderVo, Model model) {
        playerGameOrderVo.getSearch().setPlayerId(SessionManager.getUserId());
        playerGameOrderVo = ServiceSiteTool.playerGameOrderService().get(playerGameOrderVo);
        model.addAttribute("command", playerGameOrderVo);
        model.addAttribute("username", SessionManager.getUserName());
        model.addAttribute("isLotterySite", ParamTool.isLotterySite());
        return BETTING_DETAIL_URL;
    }

    @RequestMapping("/gameRecordDetail")
    public String gameRecordDetail(PlayerGameOrderVo playerGameOrderVo, Model model) {
        playerGameOrderVo = ServiceSiteTool.playerGameOrderService().getGameOrderDetail(playerGameOrderVo);
        PlayerGameOrder playerGameOrder = playerGameOrderVo.getResult();
//        如果不是这个玩家的投注订单，则视无该笔订单
        if (playerGameOrder == null || playerGameOrder.getPlayerId() != SessionManager.getUserId().intValue()) {
            playerGameOrderVo.setResult(null);
            playerGameOrderVo.setResultArray(null);
        }
        model.addAttribute("command", playerGameOrderVo);
        model.addAttribute("resultArray", playerGameOrderVo.getResultArray());
        return BETTING_DETAIL_URL;
    }

    /**
     * 统计当前页数据
     *
     * @param listVo
     */
    @RequestMapping("/statisticsData")
    @ResponseBody
    private String statisticsData(PlayerGameOrderListVo listVo) {
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        // 统计数据
//        if (listVo.getPaging().getTotalCount() != 0) {
        //判断日期查询
        listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1), -1));
        Map map = ServiceSiteTool.playerGameOrderService().queryTotalPayoutAndEffect(listVo);
        map.put("currency", getCurrencySign());
        return JsonTool.toJson(map);
//        }
//        return null;
    }

    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    private void initQueryDate(PlayerGameOrderListVo playerGameOrderListVo) {
        Date minDate = SessionManager.getDate().addDays(TIME_INTERVAL);
        playerGameOrderListVo.setMinDate(minDate);
        PlayerGameOrderSo playerGameOrderSo = playerGameOrderListVo.getSearch();
        if (playerGameOrderSo.getBeginBetTime() == null) {
            playerGameOrderListVo.getSearch().setBeginBetTime(DateTool.addDays(SessionManager.getDate().getTomorrow(), -DEFAULT_TIME)); //拿到明天在-1相当于拿到今天时间00:00:00
        } else if (playerGameOrderSo.getBeginBetTime().getTime() < minDate.getTime()) {
            playerGameOrderListVo.getSearch().setBeginBetTime(minDate);
        }
        if (playerGameOrderListVo.getSearch().getEndBetTime() == null || playerGameOrderListVo.getSearch().getBeginBetTime().after(playerGameOrderListVo.getSearch().getEndBetTime())) {
            playerGameOrderListVo.getSearch().setEndBetTime(DateTool.addSeconds(SessionManager.getDate().getTomorrow(), -1));
        }
    }

}
