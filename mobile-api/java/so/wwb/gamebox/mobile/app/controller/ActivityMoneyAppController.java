package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.ArrayTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.CryptoTool;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.enums.ActivityPreferentialFormEnum;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.operation.po.ActivityMoneyAwardsRules;
import so.wwb.gamebox.model.master.operation.po.ActivityOpenPeriod;
import so.wwb.gamebox.model.master.operation.po.ActivityPlayerApply;
import so.wwb.gamebox.model.master.operation.po.ActivityPlayerPreferential;
import so.wwb.gamebox.model.master.operation.vo.ActivityMoneyAwardsRulesVo;
import so.wwb.gamebox.model.master.operation.vo.ActivityMoneyDefaultWinPlayerListVo;
import so.wwb.gamebox.model.master.operation.vo.ActivityMoneyDefaultWinPlayerVo;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.master.player.vo.VUserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;

import java.io.Serializable;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.SPLIT_REGEX;

/**
 * Created by ed on 18-1-24.
 */
@Controller
@RequestMapping("/activityOrigin")
public class ActivityMoneyAppController {
    private Log LOG = LogFactory.getLog(ActivityMoneyAppController.class);

    /**
     * 剩余次数,是否能抽红包
     */
    @RequestMapping(value = "/countDrawTimes", method = RequestMethod.POST)
    @ResponseBody
    public String countDrawTimes(String activityMessageId) {
        if (SessionManagerCommon.getUser() == null) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.UN_LOGIN.getCode(),
                    AppErrorCodeEnum.UN_LOGIN.getMsg(),
                    null, APP_VERSION);
        }

        if (StringTool.isBlank(activityMessageId)) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ACTIVITY_END.getCode(),
                    AppErrorCodeEnum.ACTIVITY_END.getMsg(),
                    null, APP_VERSION);
        }

        Map<String, Serializable> map = new HashMap<>(4, 1f);
        Integer playerId = SessionManagerBase.getUserId();
        PlayerActivityMessage moneyActivity = findMoneyActivity();
        Integer id = Integer.valueOf(CryptoTool.aesDecrypt(activityMessageId, "PlayerActivityMessageListVo"));

        if (playerId == null || moneyActivity == null || !moneyActivity.getId().equals(id)) {
            LOG.info("[玩家-{0}计算红包次数]没有红包活动，没有抽奖", playerId.toString());
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ACTIVITY_END.getCode(),
                    AppErrorCodeEnum.ACTIVITY_END.getMsg(),
                    null, APP_VERSION);
        }

        Date now = new Date();
        if (now.after(moneyActivity.getEndTime())) {
            LOG.info("[玩家-{0}计算红包次数]红包活动已经结束", playerId.toString());
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ACTIVITY_END.getCode(),
                    AppErrorCodeEnum.ACTIVITY_END.getMsg(),
                    null, APP_VERSION);
        }
        Integer rankId = getPlayerRankId(SessionManagerBase.getUserId());
        boolean containUserRank = isContainUserRank(moneyActivity, rankId);
        if (!containUserRank) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ACTIVITY_END.getCode(),
                    AppErrorCodeEnum.ACTIVITY_END.getMsg(),
                    null, APP_VERSION);
        }
        ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
        awardsRulesVo.setPlayerId(playerId);
        awardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
        awardsRulesVo.setActivityMessage(moneyActivity);

        boolean allDayLottery = isAllDayLottery(playerId, moneyActivity.getId());
        if (allDayLottery) {//全天开奖
            int count = countAll(moneyActivity, playerId);
            map.put("isEnd", "false");
            map.put("drawTimes", count);
        } else {
            ActivityOpenPeriod period = ServiceSiteTool.activityMoneyAwardsRulesService().queryMoneyOpenPeriod(awardsRulesVo);
            if (period != null) {//开奖时间到
                int count = countAll(moneyActivity, playerId);
                map.put("isEnd", "false");
                map.put("drawTimes", count);
            } else {
                map.put("drawTimes", 0);
                map.put("isEnd", "true");
            }
            awardsRulesVo.setMoneyOpenPeriod(period);
        }
        //如果还有次数，看下是否还有奖品。如果没有次数，直接显示没有次数
        if (MapTool.getInteger(map, "drawTimes") > 0) {
            boolean hasLotteryAwards = ServiceSiteTool.activityMoneyAwardsRulesService().hasLotteryAwards(awardsRulesVo);
            if (!hasLotteryAwards) {
                map.put("isEnd", "true");
                map.put("drawTimes", -5);
            }
        }
        String startTime = fetchNextStartTime(awardsRulesVo);
        map.put("nextLotteryTime", startTime);

        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 拆红包
     *
     * @return
     */
    @RequestMapping(value = "/getPacket", method = RequestMethod.POST)
    @ResponseBody
    @Token(valid = true)
    public String getPacket(String activityMessageId) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (SessionManager.getUser() == null) {

            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.UN_LOGIN.getCode(),
                    AppErrorCodeEnum.UN_LOGIN.getMsg(),
                    null, APP_VERSION);
        }

        if (StringTool.isBlank(activityMessageId)) {

            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ACTIVITY_END.getCode(),
                    AppErrorCodeEnum.ACTIVITY_END.getMsg(),
                    null, APP_VERSION);
        }

        Map map = new HashMap();
        try {
            LOG.info("[玩家-{0}拆红包]开始", SessionManagerBase.getUserId().toString());
            if (StringTool.isBlank(activityMessageId)) {
                //没有红包活动
                map.put("gameNum", -3);
                map.put("award", 0);

                return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                        AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map, APP_VERSION);
            }
            Integer playerId = SessionManagerBase.getUserId();
            PlayerActivityMessage moneyActivity = findMoneyActivity();
            if (playerId == null || moneyActivity == null) {
                //没有红包活动
                map.put("gameNum", -3);
                map.put("award", 0);

                return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                        AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map, APP_VERSION);
            }
            Integer id = Integer.valueOf(CryptoTool.aesDecrypt(activityMessageId, "PlayerActivityMessageListVo"));
            if (!id.equals(moneyActivity.getId())) {
                //没有红包活动
                map.put("gameNum", -3);
                map.put("award", 0);

                return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                        AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map, APP_VERSION);
            }

            Integer rankId = getPlayerRankId(SessionManagerBase.getUserId());
            boolean containUserRank = isContainUserRank(moneyActivity, rankId);
            if (!containUserRank) {
                map.put("gameNum", -4);
                map.put("award", 0);

                return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                        AppErrorCodeEnum.SUCCESS.getCode(),
                        AppErrorCodeEnum.SUCCESS.getMsg(),
                        map, APP_VERSION);
            }
            boolean allDayLottery = isAllDayLottery(playerId, moneyActivity.getId());
            if (allDayLottery) {//全天开奖
                LOG.info("[玩家-{0}拆红包]全天抽奖", SessionManagerBase.getUserId().toString());
                allDayLottery(map, playerId, moneyActivity);
            } else {//按时段开奖
                LOG.info("[玩家-{0}拆红包]按时段抽奖", SessionManagerBase.getUserId().toString());
                lotteryByPeriod(map, playerId, moneyActivity);
            }
            ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
            awardsRulesVo.setPlayerId(playerId);
            awardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
            awardsRulesVo.setActivityMessage(moneyActivity);
            String nextStartTime = fetchNextStartTime(awardsRulesVo);
            map.put("nextLotteryTime", nextStartTime);
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        } catch (Exception ex) {
            LOG.error(ex, "拆红包出现异常");
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
            map.put("gameNum", -2);
            map.put("award", 0);

            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    map, APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
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
            rankIds = m.getRankid().split(SPLIT_REGEX);
            return ArrayTool.contains(rankIds, rankId.toString());
        }
        return false;
    }

    /**
     * 是否全天开奖
     *
     * @param playerId
     * @param activityMessageId
     * @return
     */
    private boolean isAllDayLottery(Integer playerId, Integer activityMessageId) {
        ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
        awardsRulesVo.setPlayerId(playerId);
        awardsRulesVo.getSearch().setActivityMessageId(activityMessageId);
        boolean allDayLottery = ServiceSiteTool.activityMoneyAwardsRulesService().isAllDayLottery(awardsRulesVo);
        return allDayLottery;
    }

    /**
     * 计算所有次数
     *
     * @param moneyActivity
     * @param playerId
     * @return
     */
    private Integer countAll(PlayerActivityMessage moneyActivity, Integer playerId) {
        LOG.info("[玩家-{0}计算红包次数]准备计算可抽奖次数", playerId.toString());
        List<Map> moneyCounts = findCountByPlayerId(moneyActivity, playerId);
        int count = 0;
        if (CollectionTool.isNotEmpty(moneyCounts)) {
            for (Map mc : moneyCounts) {
                count += MapTool.getInteger(mc, "remain_win_count");
            }
        }
        LOG.info("[玩家-{0}计算红包次数]查询到玩家内定红包可抽取次数为：{1}", playerId.toString(), count);
        Integer integer = queryPlayerBetCountByRules(moneyActivity);
        LOG.info("[玩家-{0}计算红包次数]按条件计算玩家的可抽取次数为：{1}", playerId.toString(), integer);
        count += integer;
        LOG.info("[玩家-{0}计算红包次数]玩家总的可抽奖次数为：{1}", playerId.toString(), count);
        return count;
    }

    /**
     * 下次开奖时间
     *
     * @param awardsRulesVo
     * @return
     */
    private String fetchNextStartTime(ActivityMoneyAwardsRulesVo awardsRulesVo) {
        String startTime = "";
        Date today = new Date();
        String realDate = DateTool.formatDate(today, CommonContext.get().getTimeZone(), DateTool.yyyy_MM_dd_HH_mm_ss);
        today = DateTool.parseDate(realDate, DateTool.yyyy_MM_dd_HH_mm_ss);
        ActivityOpenPeriod nextOpenPeriod = ServiceSiteTool.activityMoneyAwardsRulesService().queryMoneyOpenPeriodByActivityId(awardsRulesVo);
        LOG.info("[玩家-{0}获取下次抽次时间]下个开奖时段为:{1}", awardsRulesVo.getPlayerId().toString(), nextOpenPeriod == null ? "空" : nextOpenPeriod.getStartTime(today));
        Date nextStartTime = null;
        if (nextOpenPeriod != null) {
            nextStartTime = nextOpenPeriod.getStartTime(today);
            LOG.info("[玩家-{0}获取下次抽次时间]下个开奖时段是否为今天:{1}", awardsRulesVo.getPlayerId().toString(), nextOpenPeriod.isCurrentDay());
            if (!nextOpenPeriod.isCurrentDay()) {
                Date tomorrow = DateQuickPicker.getInstance().getTomorrow();
                String tomorrowFormat = DateTool.formatDate(tomorrow, CommonContext.get().getTimeZone(), DateTool.yyyyMMddHHmmss);
                tomorrow = DateTool.parseDate(tomorrowFormat, DateTool.yyyyMMddHHmmss);
                nextStartTime = nextOpenPeriod.getStartTime(tomorrow);
            }
        } else {
            nextStartTime = DateQuickPicker.getInstance().getTomorrow();
        }
        if (nextStartTime.before(awardsRulesVo.getActivityMessage().getEndTime())) {
            startTime = DateTool.formatDate(nextStartTime, CommonContext.get().getLocale(),
                    CommonContext.get().getTimeZone(), CommonContext.getDateFormat().getDAY_SECOND());
        } else {
            return "";
        }
        return startTime;
    }

    private void lotteryByPeriod(Map map, Integer playerId, PlayerActivityMessage moneyActivity) {
        ActivityMoneyAwardsRulesVo awardsRulesVo = new ActivityMoneyAwardsRulesVo();
        awardsRulesVo.setPlayerId(playerId);
        awardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
        ActivityOpenPeriod period = ServiceSiteTool.activityMoneyAwardsRulesService().queryMoneyOpenPeriod(awardsRulesVo);
        if (period != null) {//开奖时间到
            List<Map> moneyCounts = findCountByPlayerId(moneyActivity, playerId);
            if (CollectionTool.isEmpty(moneyCounts)) {//没有内定次数
                //没有内定红包数
                LOG.info("[玩家-{0}拆红包]没有内定红包数", SessionManagerBase.getUserId().toString());
                lotteryByRules(map, moneyActivity, period);
            } else {
                //有内定次数
                LOG.info("[玩家-{0}拆红包]有内定次数", SessionManagerBase.getUserId().toString());
                hasDefaultCount(map, moneyCounts, period, moneyActivity, playerId);
            }
        } else {
            map.put("gameNum", -1);
            map.put("award", 0);
        }
    }

    private void allDayLottery(Map map, Integer playerId, PlayerActivityMessage moneyActivity) {
        //查询内定红包数
        List<Map> moneyCounts = findCountByPlayerId(moneyActivity, playerId);
        if (CollectionTool.isEmpty(moneyCounts)) {//没有内定次数
            //
            LOG.info("[玩家-{0}拆红包]没有内定红包数", SessionManagerBase.getUserId().toString());
            lotteryByRules(map, moneyActivity, null);
        } else {
            //有内定次数
            LOG.info("[玩家-{0}拆红包]有内定次数", SessionManagerBase.getUserId().toString());
            hasDefaultCount(map, moneyCounts, null, moneyActivity, playerId);
        }
    }

    /**
     * 根据玩家ID查询内定红包开奖次数
     *
     * @param playerId
     * @return
     */
    private List<Map> findCountByPlayerId(PlayerActivityMessage moneyActivity, Integer playerId) {
        if (moneyActivity == null || playerId == null) {
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
     *
     * @param moneyActivity
     * @return
     */
    private Integer queryPlayerBetCountByRules(PlayerActivityMessage moneyActivity) {
        if (moneyActivity == null) {
            return 0;
        }
        LOG.info("[玩家-{0}计算红包次数]设置查询条件", SessionManagerBase.getUserId().toString());
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
     * 按规则抽奖
     *
     * @param map
     */
    private void lotteryByRules(Map map, PlayerActivityMessage moneyActivity, ActivityOpenPeriod openPeriod) {
        //查询玩家剩余抽奖次数
        Integer integer = queryPlayerBetCountByRules(moneyActivity);
        if (integer > 0) {
            //TODO:抽奖
            ActivityMoneyAwardsRulesVo activityMoneyAwardsRulesVo = new ActivityMoneyAwardsRulesVo();
            activityMoneyAwardsRulesVo.getSearch().setActivityMessageId(moneyActivity.getId());
            activityMoneyAwardsRulesVo.setPlayerId(SessionManagerBase.getUserId());
            activityMoneyAwardsRulesVo.setMoneyOpenPeriod(openPeriod);
            activityMoneyAwardsRulesVo.setActivityMessage(moneyActivity);
            VUserPlayer vUserPlayer = fetchUserPlayer(SessionManagerCommon.getUserId());
            ActivityPlayerApply activityPlayerApply = buildPlayerApplyData(moneyActivity, vUserPlayer, ActivityApplyCheckStatusEnum.TO_BE_CONFIRM.getCode());
            activityMoneyAwardsRulesVo.setActivityPlayerApply(activityPlayerApply);
            if (openPeriod == null) {
                activityMoneyAwardsRulesVo.getSearch().setStartTime(DateQuickPicker.getInstance().getToday());
                activityMoneyAwardsRulesVo.getSearch().setEndTime(DateQuickPicker.getInstance().getTomorrow());
            }
            activityMoneyAwardsRulesVo._setDataSourceId(SessionManagerBase.getSiteId());
            activityMoneyAwardsRulesVo = ServiceSiteTool.activityMoneyAwardsRulesService().lotteryByTime(activityMoneyAwardsRulesVo);
            ActivityMoneyAwardsRules lottery = activityMoneyAwardsRulesVo.getResult();
            if (lottery == null || lottery.getId() == null) {
                map.put("gameNum", -5);
                map.put("award", 0);
            } else {
                integer = integer - 1;
                map.put("gameNum", integer);
                map.put("award", lottery.getAmount());
                String winId = CryptoTool.aesEncrypt(lottery.getId().toString(), "PlayerActivityMessageListVo");
                map.put("id", winId);
                String recordId = CryptoTool.aesEncrypt(activityMoneyAwardsRulesVo.getActivityMoneyPlayRecord().getId().toString(), "PlayerActivityMessageListVo");
                map.put("recordId", recordId);
                if (activityMoneyAwardsRulesVo.getActivityPlayerApply().getId() != null) {
                    String applyId = CryptoTool.aesEncrypt(activityMoneyAwardsRulesVo.getActivityPlayerApply().getId().toString(), "PlayerActivityMessageListVo");
                    map.put("applyId", applyId);
                }
            }
            //ServiceSiteTool.activityMoneyAwardsRulesService().reduceMoneyByRule(activityMoneyAwardsRulesVo);
        } else {
            map.put("gameNum", -1);
            map.put("award", 0);
        }
    }

    /**
     * 有内定红包
     *
     * @param map
     * @param moneyCounts
     */
    private void hasDefaultCount(Map map, List<Map> moneyCounts, ActivityOpenPeriod openPeriod, PlayerActivityMessage moneyActivity, Integer playerId) {
        int gameNum = countAll(moneyActivity, playerId);
        if (gameNum <= 0) {
            map.put("gameNum", -1);
            map.put("award", 0);
            return;
        }
        //是否还有奖品
        Map money = swithDefaultWin(moneyCounts, openPeriod);
        if (money == null) {
            //看下是否按条件的红包
            lotteryByRules(map, moneyActivity, openPeriod);
            Integer num = MapTool.getInteger(map, "gameNum");
            if (num != null && num == -1) {
                map.put("gameNum", -5);
                map.put("award", 0);
            }
            return;
        }
        //先抽内定红包，一次取一个红包就可以
        //Map money = moneyCounts.get(0);
        Double award = MapTool.getDouble(money, "amount");
        Integer rules_id = MapTool.getInteger(money, "rules_id");
        String winId = CryptoTool.aesEncrypt(rules_id.toString(), "PlayerActivityMessageListVo");
        map.put("id", winId);
        //剩余抽奖次数-1
        gameNum--;
        ActivityMoneyDefaultWinPlayerVo winPlayerVo = reduceCount(money, openPeriod, moneyActivity);
        if (winPlayerVo.isSuccess()) {
            map.put("gameNum", gameNum);
            map.put("award", award);
            String recordId = CryptoTool.aesEncrypt(winPlayerVo.getActivityMoneyPlayRecord().getId().toString(), "PlayerActivityMessageListVo");
            map.put("recordId", recordId);
            String applyId = CryptoTool.aesEncrypt(winPlayerVo.getActivityPlayerApply().getId().toString(), "PlayerActivityMessageListVo");
            map.put("applyId", applyId);
        } else {
            map.put("gameNum", -2);
            map.put("award", 0);
        }
    }

    private VUserPlayer fetchUserPlayer(Integer playerId) {
        VUserPlayerVo playerVo = new VUserPlayerVo();
        playerVo.getSearch().setId(playerId);
        playerVo = ServiceSiteTool.vUserPlayerService().get(playerVo);
        return playerVo.getResult();

    }

    /**
     * 组装申请数据
     *
     * @param activityMessage
     * @param vUserPlayer
     * @return
     */
    private ActivityPlayerApply buildPlayerApplyData(PlayerActivityMessage activityMessage, VUserPlayer vUserPlayer, String checkState) {
        ActivityPlayerApply activityPlayerApply = new ActivityPlayerApply();
        activityPlayerApply.setActivityMessageId(activityMessage.getId());
        activityPlayerApply.setUserId(vUserPlayer.getId());
        activityPlayerApply.setUserName(vUserPlayer.getUsername());
        activityPlayerApply.setRegisterTime(vUserPlayer.getCreateTime());
        activityPlayerApply.setRankId(vUserPlayer.getRankId());
        activityPlayerApply.setRankName(vUserPlayer.getRankName());
        activityPlayerApply.setRiskMarker(vUserPlayer.getRiskMarker());
        activityPlayerApply.setApplyTime(new Date());
        activityPlayerApply.setCheckState(checkState);
        activityPlayerApply.setStartTime(activityMessage.getStartTime());
        activityPlayerApply.setEndTime(activityMessage.getEndTime());
        activityPlayerApply.setIpApply(vUserPlayer.getLoginIp());
        activityPlayerApply.setIpDictCode(vUserPlayer.getLoginIpDictCode());
        return activityPlayerApply;
    }

    /**
     * 查询内空否红是否还有名额
     *
     * @param moneyCounts
     * @return
     */
    private Map swithDefaultWin(List<Map> moneyCounts, ActivityOpenPeriod openPeriod) {
        if (CollectionTool.isEmpty(moneyCounts)) {
            LOG.info("[玩家-{0}拆红包]内定为空", SessionManagerBase.getUserId().toString());
            return null;
        }
        Map resMap = null;
        for (Map moneyCount : moneyCounts) {
            if (hasRemainCount(moneyCount, openPeriod)) {
                resMap = moneyCount;
                break;
            }
        }
        LOG.info("[玩家-{0}拆红包]内定结果{1}", SessionManagerBase.getUserId().toString(), resMap);
        return resMap;
    }

    /**
     * 红包数-1并且插入资金 记录
     *
     * @param money
     */
    private ActivityMoneyDefaultWinPlayerVo reduceCount(Map money, ActivityOpenPeriod openPeriod, PlayerActivityMessage moneyActivity) {
        ActivityMoneyDefaultWinPlayerVo winPlayerVo = new ActivityMoneyDefaultWinPlayerVo();
        winPlayerVo.setMoneyOpenPeriod(openPeriod);
        Integer id = MapTool.getInteger(money, "id");
        Integer rules_id = MapTool.getInteger(money, "rules_id");
        if (id == null) {
            winPlayerVo.setSuccess(false);
            return winPlayerVo;
        }
        winPlayerVo.getSearch().setId(id);
        winPlayerVo.setActivityMessage(moneyActivity);
        VUserPlayer vUserPlayer = fetchUserPlayer(SessionManagerCommon.getUserId());
        ActivityPlayerApply activityPlayerApply = buildPlayerApplyData(moneyActivity, vUserPlayer, ActivityApplyCheckStatusEnum.TO_BE_CONFIRM.getCode());
        winPlayerVo.setActivityPlayerApply(activityPlayerApply);
        ActivityPlayerPreferential activityPlayerPreferential = buildPlayerPreferential(moneyActivity, rules_id);
        winPlayerVo.setPreferential(activityPlayerPreferential);
        winPlayerVo = ServiceSiteTool.activityMoneyAwardsRulesService().reducePlayerCount(winPlayerVo);
        return winPlayerVo;
    }

    /**
     * 查询奖项是否还有名额
     *
     * @param money
     * @return
     */
    private boolean hasRemainCount(Map money, ActivityOpenPeriod openPeriod) {
        Integer rules_id = MapTool.getInteger(money, "rules_id");
        if (rules_id == null) {
            return false;
        }
        ActivityMoneyAwardsRulesVo rulesVo = new ActivityMoneyAwardsRulesVo();
        rulesVo.setPlayerId(SessionManagerCommon.getUserId());
        rulesVo.getSearch().setId(rules_id);
        rulesVo.getSearch().setStartTime(DateQuickPicker.getInstance().getToday());
        rulesVo.getSearch().setEndTime(DateQuickPicker.getInstance().getTomorrow());
        rulesVo.setPlayerId(SessionManagerCommon.getUserId());
        rulesVo.setMoneyOpenPeriod(openPeriod);
        Boolean hasCount = ServiceSiteTool.activityMoneyAwardsRulesService().hasCountByAward(rulesVo);
        LOG.info("[玩家-{0}拆红包]奖项是否有剩余：{1}", SessionManagerBase.getUserId().toString(), hasCount);
        return hasCount;
    }

    /**
     * 组装优惠记录
     *
     * @param activityMessage
     * @return
     */
    private ActivityPlayerPreferential buildPlayerPreferential(PlayerActivityMessage activityMessage, Integer rulesId) {
        ActivityPlayerPreferential preferential = new ActivityPlayerPreferential();
        preferential.setActivityMessageId(activityMessage.getId());
        if (rulesId != null) {
            ActivityMoneyAwardsRules moneyRules = findMoneyRules(rulesId);
            if (moneyRules != null) {
                preferential.setPreferentialAudit(moneyRules.getAudit());
                // 获取优惠值
                preferential.setPreferentialValue(moneyRules.getAmount());
            }
        }
        // 设置优惠形式
        preferential.setPreferentialForm(ActivityPreferentialFormEnum.REGULAR_HANDSEL.getCode());
        return preferential;
    }

    /**
     * 获取中奖奖项
     *
     * @param id
     * @return
     */
    private ActivityMoneyAwardsRules findMoneyRules(Integer id) {
        ActivityMoneyAwardsRulesVo rulesVo = new ActivityMoneyAwardsRulesVo();
        rulesVo.getSearch().setId(id);
        rulesVo = ServiceSiteTool.activityMoneyAwardsRulesService().get(rulesVo);
        return rulesVo.getResult();
    }

}
