package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.ArrayTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.security.CryptoTool;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.App.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.App.model.AppFloatPicItem;
import so.wwb.gamebox.mobile.App.model.AppModelVo;
import so.wwb.gamebox.mobile.App.model.AppRequestModelVo;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.vo.SiteGameListVo;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.content.po.CttCarouselI18n;
import so.wwb.gamebox.model.master.content.po.CttFloatPic;
import so.wwb.gamebox.model.master.content.po.CttFloatPicItem;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.operation.po.ActivityOpenPeriod;
import so.wwb.gamebox.model.master.operation.vo.ActivityMoneyAwardsRulesVo;
import so.wwb.gamebox.model.master.operation.vo.ActivityMoneyDefaultWinPlayerListVo;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.soul.web.tag.ImageTag.getImagePath;

/**
 * Created by  on 17-4-3.
 */
@Controller
@RequestMapping("/origin")
public class OriginController extends BaseApiController {
    private Log LOG = LogFactory.getLog(OriginController.class);
    private final String version = "app_01";
    Map<String, Object> mapJson = MapTool.newHashMap();

    //region mainIndex
    @RequestMapping("/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = MapTool.newHashMap();

        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());

        map.put("siteApiRelation", getSiteApiRelationI18n(request,model));
        map.put("activity", getMoneyActivityFloat(request));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();
        //轮播图
        map.put("banner", getCarouselApp(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getAnnouncement")
    @ResponseBody
    public String getAnnounce() {
        Map<String, Object> map = MapTool.newHashMap();
        //公告
        map.put("announcement", getAnnouncement());

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getSiteApiRelation")
    @ResponseBody
    public String getSiteApi(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = MapTool.newHashMap();
        //公告
        map.put("siteApiRelation", getSiteApiRelationI18n(request,model));
        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getFloat")
    @ResponseBody
    public String getFloat(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();

        map.put("activity", getMoneyActivityFloat(request));

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCasinoGame")
    @ResponseBody
    public String getCasinoGame(SiteGameListVo listVo,HttpServletRequest request,AppRequestModelVo modelVo){
        Map<String,Object> map = MapTool.newHashMap();
        Map<String,Object> page = MapTool.newHashMap();

        map.put("casinoGames",getCasinoGameByApiId(listVo,request,page,modelVo));
        map.put("page",page);
        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    /**
     * 剩余次数,是否能抽红包
     */
    @RequestMapping("/countDrawTimes")
    @ResponseBody
    public Map<String, java.io.Serializable> countDrawTimes(String activityMessageId){
        Map<String, java.io.Serializable> map = new HashMap<>(4,1f);
        if(SessionManagerCommon.getUser() == null){
            map.put("error",1);
            map.put("code", AppErrorCodeEnum.UN_LOGIN.getCode());
            map.put("msg",AppErrorCodeEnum.UN_LOGIN.getMsg());
            map.put("data",null);
            return map;
        }

        if(activityMessageId==null){
            map.put("isEnd","true");
            map.put("drawTimes",-1);
            return map;
        }
        Integer playerId = SessionManagerBase.getUserId();
        PlayerActivityMessage moneyActivity = findMoneyActivity();
        Integer id = Integer.valueOf(CryptoTool.aesDecrypt(activityMessageId, "PlayerActivityMessageListVo"));

        if(playerId == null || moneyActivity == null || !moneyActivity.getId().equals(id)){
            LOG.info("[玩家-{0}计算红包次数]没有红包活动，没有抽奖",playerId.toString());
            map.put("isEnd","true");
            map.put("drawTimes",-1);
        }else{
            Date now = new Date();
            if(now.after(moneyActivity.getEndTime())){
                LOG.info("[玩家-{0}计算红包次数]红包活动已经结束",playerId.toString());
                map.put("isEnd","true");
                map.put("drawTimes",-1);
                return map;
            }
            Integer rankId = getPlayerRankId(SessionManagerBase.getUserId());
            boolean containUserRank = isContainUserRank(moneyActivity, rankId);
            if(!containUserRank){
                map.put("isEnd","true");
                map.put("drawTimes",-1);
                return map;
            }
            ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
            awardsRulesVo.setPlayerId(playerId);
            awardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
            awardsRulesVo.setActivityMessage(moneyActivity);

            boolean allDayLottery = isAllDayLottery(playerId, moneyActivity.getId());
            if(allDayLottery) {//全天开奖
                int count = countAll(moneyActivity,playerId);
                map.put("drawTimes",count);
                map.put("isEnd","false");
            }else{

                ActivityOpenPeriod period = ServiceSiteTool.activityMoneyAwardsRulesService().queryMoneyOpenPeriod(awardsRulesVo);
                if(period!=null) {//开奖时间到
                    int count = countAll(moneyActivity,playerId);
                    map.put("drawTimes",count);
                    map.put("isEnd","false");
                }else{
                    map.put("drawTimes",0);
                    map.put("isEnd","true");
                }
                awardsRulesVo.setMoneyOpenPeriod(period);
            }
            //如果还有次数，看下是否还有奖品。如果没有次数，直接显示没有次数
            if(MapTool.getInteger(map,"drawTimes")>0){
                boolean hasLotteryAwards = ServiceSiteTool.activityMoneyAwardsRulesService().hasLotteryAwards(awardsRulesVo);
                if(!hasLotteryAwards){
                    map.put("drawTimes",-5);
                    map.put("isEnd","true");
                }
            }
            String startTime = fetchNextStartTime(awardsRulesVo);
            map.put("nextLotteryTime", startTime);

        }
        map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
        return map;
    }
    //endregion mainIndex

    private void setMapJson(AppModelVo app) {
        if (app.getError() != 0) {
            mapJson.put("error", app.getError());
        } else {
            mapJson.put("error", 0);
        }

        if (app.getCode() != 0) {
            mapJson.put("code", app.getCode());
        } else {
            mapJson.put("code", AppErrorCodeEnum.Success.getCode());
        }

        if (StringTool.isNotBlank(app.getMsg())) {
            mapJson.put("msg", app.getMsg());
        } else {
            mapJson.put("msg", AppErrorCodeEnum.Success.getMsg());
        }

        if (StringTool.isNotBlank(app.getVersion())) {
            mapJson.put("version", app.getVersion());
        } else {
            mapJson.put("version", version);
        }
    }


    private Integer getPlayerRankId(Integer userId) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(userId);
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult().getRankId();
    }

    private boolean isContainUserRank(PlayerActivityMessage m, Integer rankId) {
        String[] rankIds;
        if (m.getAllRank() != null && m.getAllRank()) {
            return true;
        } else if (m.getRankid() != null) {
            rankIds = m.getRankid().split(",");
            return ArrayTool.contains(rankIds, rankId.toString());
        }
        return false;
    }

    /**
     * 下次开奖时间
     * @param awardsRulesVo
     * @return
     */
    private String fetchNextStartTime(ActivityMoneyAwardsRulesVo awardsRulesVo) {
        String startTime = "";
        Date today = new Date();
        String realDate = DateTool.formatDate(today, CommonContext.get().getTimeZone(), DateTool.yyyy_MM_dd_HH_mm_ss);
        today = DateTool.parseDate(realDate,DateTool.yyyy_MM_dd_HH_mm_ss);
        ActivityOpenPeriod nextOpenPeriod = ServiceSiteTool.activityMoneyAwardsRulesService().queryMoneyOpenPeriodByActivityId(awardsRulesVo);
        LOG.info("[玩家-{0}获取下次抽次时间]下个开奖时段为:{1}",awardsRulesVo.getPlayerId().toString(),nextOpenPeriod==null?"空":nextOpenPeriod.getStartTime(today));
        Date nextStartTime = null;
        if(nextOpenPeriod!=null){
            nextStartTime = nextOpenPeriod.getStartTime(today);
            LOG.info("[玩家-{0}获取下次抽次时间]下个开奖时段是否为今天:{1}",awardsRulesVo.getPlayerId().toString(),nextOpenPeriod.isCurrentDay());
            if(!nextOpenPeriod.isCurrentDay()){
                Date tomorrow = DateQuickPicker.getInstance().getTomorrow();
                String tomorrowFormat = DateTool.formatDate(tomorrow,CommonContext.get().getTimeZone(),DateTool.yyyyMMddHHmmss);
                tomorrow = DateTool.parseDate(tomorrowFormat,DateTool.yyyyMMddHHmmss);
                nextStartTime = nextOpenPeriod.getStartTime(tomorrow);
                //nextStartTime = DateTool.addDays(nextStartTime,+1);
            }
        }else{
            nextStartTime = DateQuickPicker.getInstance().getTomorrow();
        }
        if(nextStartTime.before(awardsRulesVo.getActivityMessage().getEndTime())){
            startTime = DateTool.formatDate(nextStartTime, CommonContext.get().getLocale(),
                    CommonContext.get().getTimeZone(), CommonContext.getDateFormat().getDAY_SECOND());
        }else{
            return "";
        }
        return startTime;
    }
    /**
     * 是否全天开奖
     * @param playerId
     * @param activityMessageId
     * @return
     */
    private boolean isAllDayLottery(Integer playerId,Integer activityMessageId){
        ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
        awardsRulesVo.setPlayerId(playerId);
        awardsRulesVo.getSearch().setActivityMessageId(activityMessageId);
        boolean allDayLottery = ServiceSiteTool.activityMoneyAwardsRulesService().isAllDayLottery(awardsRulesVo);
        return allDayLottery;
    }

    /**
     * 计算所有次数
     * @param moneyActivity
     * @param playerId
     * @return
     */
    private Integer countAll(PlayerActivityMessage moneyActivity,Integer playerId ){
        LOG.info("[玩家-{0}计算红包次数]准备计算可抽奖次数",playerId.toString());
        List<Map> moneyCounts = findCountByPlayerId(moneyActivity,playerId);
        int count = 0;
        if(CollectionTool.isNotEmpty(moneyCounts)){
            for(Map mc : moneyCounts){
                count += MapTool.getInteger(mc,"remain_win_count");
            }
        }
        LOG.info("[玩家-{0}计算红包次数]查询到玩家内定红包可抽取次数为：{1}",playerId.toString(),count);
        Integer integer = queryPlayerBetCountByRules(moneyActivity);
        LOG.info("[玩家-{0}计算红包次数]按条件计算玩家的可抽取次数为：{1}",playerId.toString(),integer);
        count += integer;
        LOG.info("[玩家-{0}计算红包次数]玩家总的可抽奖次数为：{1}",playerId.toString(),count);
        return count;
    }

    /**
     * 根据玩家ID查询内定红包开奖次数
     * @param playerId
     * @return
     */
    private List<Map> findCountByPlayerId(PlayerActivityMessage moneyActivity,Integer playerId){
        if(moneyActivity==null||playerId==null){
            return new ArrayList<>();
        }
        ActivityMoneyDefaultWinPlayerListVo playerListVo = new ActivityMoneyDefaultWinPlayerListVo();
        playerListVo.getSearch().setActivityMessageId(moneyActivity.getId());
        playerListVo.getSearch().setPlayerId(playerId);
        List<Map> maps = ServiceSiteTool.activityMoneyDefaultWinPlayerService().queryMoneyCountByPlayerId(playerListVo);
        return maps;
    }

    /**
     * 根据规则计算次数
     * @param moneyActivity
     * @return
     */
    private Integer queryPlayerBetCountByRules(PlayerActivityMessage moneyActivity) {
        if(moneyActivity==null){
            return 0;
        }
        LOG.info("[玩家-{0}计算红包次数]设置查询条件",SessionManagerBase.getUserId().toString());
        ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
        awardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
        awardsRulesVo.setPlayerId(SessionManagerBase.getUserId());
        awardsRulesVo.getSearch().setStartTime(DateQuickPicker.getInstance().getToday());
        awardsRulesVo.getSearch().setEndTime(DateQuickPicker.getInstance().getTomorrow());
        awardsRulesVo.setActivityMessage(moneyActivity);
        Integer integer = ServiceSiteTool.activityMoneyAwardsRulesService().queryPlayerBetCount(awardsRulesVo);
        return integer;
    }
    /**
     * 查询Banner
     *
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
     * 查询公告
     */
    private List<CttAnnouncement> getAnnouncement() {
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

    @Override
    protected String getDemoIndex() {
        return null;
    }
}