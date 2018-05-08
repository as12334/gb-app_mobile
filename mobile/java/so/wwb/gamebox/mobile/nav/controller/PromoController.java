package so.wwb.gamebox.mobile.nav.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.enums.EnumTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.ArrayTool;
import org.soul.commons.lang.string.EncodeTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.commons.security.CryptoTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.site.po.SiteCustomerService;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.*;
import so.wwb.gamebox.model.master.operation.po.ActivityPlayerApply;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.*;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * 优惠活动
 *
 * @author bill
 * @date 2016-12-4
 */
@Controller
@RequestMapping("/promo")
public class PromoController {

    private static final String MY_PROMO_URL = "/promo/MyPromo";
    private static final String PROMO_RECORDS_URL = "/promo/PromoRecords";
    private static final String PROMO_RECORDS_DETAIL = "/promo/PromoDetail";
    private static final Log LOG = LogFactory.getLog(PromoController.class);
    private static final String APPLY_EXPIRED = "apply.tip.overdue";
    private static final String REGIST_APPLIED = "apply.tip.already";
    private static final String APPLIED = "apply.tip.next";
    private static final String PARTICIPATION = "apply.tip.doing";
    private static final String APPLY_FULL = "apply.tip.vacancies";
    private static final String APPLY_FAIL_TITLE = "apply.tip.fail.title";
    private static final String APPLY_HAVE_TITLE = "apply.tip.have.title";

    /**
     * 我的优惠记录
     */
    @RequestMapping("/myPromo")
    @Upgrade(upgrade = true)
    public String getMyPromo(VPreferentialRecodeListVo vPreferentialRecodeListVo, Model model, HttpServletRequest request) {
        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setUserId(SessionManager.getUserId());
        vPreferentialRecodeListVo.getSearch().setCurrentDate(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        /*if (ServletTool.isAjaxSoulRequest(request)) {
            vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
        }*/
        vPreferentialRecodeListVo.setPropertyName(VPreferentialRecode.PROP_PREFERENTIAL_VALUE);
        vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
        vPreferentialRecodeListVo.getSearch().setCheckState(ActivityApplyCheckStatusEnum.SUCCESS.getCode());
        Number money = ServiceSiteTool.vPreferentialRecodeService().sum(vPreferentialRecodeListVo);
        model.addAttribute("money",money);
        model.addAttribute("command", vPreferentialRecodeListVo);
        return ServletTool.isAjaxSoulRequest(request) ? MY_PROMO_URL + "Partial" : MY_PROMO_URL;
    }

    /**
     * 正在进行中的活动
     */
    @RequestMapping("/promo")
    public String processingPromoRecords(VActivityMessageListVo vActivityMessageListVo, Model model, HttpServletRequest request, boolean isTwoCount) {
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        vActivityMessageListVo.getPaging().setPageSize(8);
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            vActivityMessageListVo.getSearch().setRankId(ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo).getId());
        }
        vActivityMessageListVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        vActivityMessageListVo = ServiceActivityTool.vActivityMessageService().getActivityList(vActivityMessageListVo);
        //首页只展示部分优惠
        if (isTwoCount) {
            if (vActivityMessageListVo.getResult().size() > 2) {
                List<VActivityMessage> list = vActivityMessageListVo.getResult();
                vActivityMessageListVo.setResult(list.subList(0, 2));
            }
        }
        model.addAttribute("isTwoCount", isTwoCount);
        model.addAttribute("command", vActivityMessageListVo);
        return request.getMethod().equals("GET") ? PROMO_RECORDS_URL : PROMO_RECORDS_URL + "Partial";
    }

    /**
     * 获取活动类型
     */
    @RequestMapping("/activityType")
    @ResponseBody
    public String getActivityType() {
        String localLanguage = SessionManager.getLocale().toString();
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        Map<String, SiteI18n> tempMap = new LinkedHashMap<>();
        for (Map.Entry<String, SiteI18n> entry : siteI18nMap.entrySet()) {
            SiteI18n siteI18n = entry.getValue();
            if (localLanguage.equals(siteI18n.getLocale())) {
                tempMap.put(siteI18n.getKey(), siteI18n);
            }
        }
        return JsonTool.toJson(tempMap);
    }

    /**
     * 已经结束的活动
     */
    @RequestMapping("/finishedPromo")
    public String finishedPromoRecords(VActivityMessageListVo vActivityMessageListVo, Model model) {
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.FINISHED.getCode());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getPaging().setPageSize(8);
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo().booleanValue()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            vActivityMessageListVo.getSearch().setRankId(ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo).getId());
        }
        vActivityMessageListVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        vActivityMessageListVo = ServiceActivityTool.vActivityMessageService().getActivityList(vActivityMessageListVo);

        model.addAttribute("command", vActivityMessageListVo);

        return "/promo/FinishedPromoPartial";
    }

    @RequestMapping("/promoDetail")
    @Upgrade(upgrade = true)
    public String getPromoDetail(VPlayerActivityMessageVo vActivityMessageVo, Model model) {
        model.addAttribute("command", Cache.getMobileActivityMessageInfo(vActivityMessageVo.getSearch().getId().toString()));
        model.addAttribute("nowTime", SessionManager.getDate().getNow());
        //是否开启新活动大厅
        if(ParamTool.isOpenActivityHall()){
            return "/promo/GoToPromoDetail";
        }
        return PROMO_RECORDS_DETAIL;
    }

    @RequestMapping(value = "/applyPromoDetail")
    @Upgrade(upgrade = true)
    public String applyPromoDetail(String resultId, String activityName, String code, String type, Model model) {
        model.addAttribute("activityName", EncodeTool.urlDecode(activityName));
        model.addAttribute("resultId", resultId);
        model.addAttribute("code", code);
        model.addAttribute("type", type);
        //申请 存就送，有效投注额，盈亏送
        if ("fetch".equals(type)) {
            return "/promo/ProcessApplyDetail";
        } else {//其他活动申请后显示信息
            return "/promo/ApplyPromoDetail";
        }
    }

    @ResponseBody
    @RequestMapping("/getPlayerActivityIds")
    public String getPlayerActivityIds(HttpServletRequest request) {
        if (SessionManager.getUser() == null)
            return "false";
        Integer rankId = getPlayerRankId(SessionManager.getUserId());
        String result = getPlayerActivitiesIdsJsonByRankId(rankId, request);
        return result;
    }

    @ResponseBody
    @RequestMapping("/applyActivities")
    public Map applyActivities(VPlayerActivityMessageVo vPlayerActivityMessageVo) {
        Map map = new HashMap(2, 1f);
        String code = vPlayerActivityMessageVo.getCode();
        Integer id = Integer.valueOf(CryptoTool.aesDecrypt(vPlayerActivityMessageVo.getResultId(), "PlayerActivityMessageListVo"));
        PlayerActivityMessage activityMessage = Cache.getMobileActivityMessageInfo(id.toString());
        vPlayerActivityMessageVo.setId(id);
        if (StringTool.equals(code, ActivityTypeEnum.REGIST_SEND.getCode())) {
            applyRegisterSend(map, vPlayerActivityMessageVo, activityMessage);
        } else if (StringTool.equals(code, ActivityTypeEnum.EFFECTIVE_TRANSACTION.getCode())
                || StringTool.equals(code, ActivityTypeEnum.PROFIT.getCode())) {
            applyEffectiveOrProfit(vPlayerActivityMessageVo, map, activityMessage);
        } else if (StringTool.equals(code, ActivityTypeEnum.RELIEF_FUND.getCode())) {
            applyReliefFund(vPlayerActivityMessageVo, map, activityMessage);
        }
        return map;
    }

    private void applyRegisterSend(Map map, VPlayerActivityMessageVo vPlayerActivityMessageVo, PlayerActivityMessage activityMessage) {
        LOG.debug("优惠活动申请：id：{0},code：{1}", activityMessage.getId(), activityMessage.getCode());
        setPlayerApplyCountAndTIime(activityMessage);
        activityMessage.setRegisterTime(SessionManager.getUser().getCreateTime());
        if (activityMessage.getAcount() > 0) {
            if (activityMessage.getCompareActivityTime() || activityMessage.getCompareRegisterAndActivityTime()) {
                setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, APPLY_EXPIRED), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FAIL_TITLE));
            } else {
                setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, REGIST_APPLIED), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_HAVE_TITLE));
               /* map.put("msg", LocaleTool.tranMessage("player","已申请"));*/
            }
        } else {
            if ((Boolean) activityMessage.getRegistSendEffectiveTime().get("flag")) {
                apply(vPlayerActivityMessageVo, map);
            } else {
                setApplyFailReturnStates(map,
                        LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.qualified",
                                ActivityEffectiveTime.getEffectiveTime(activityMessage.getEffectiveTime()),
                                LocaleDateTool.formatDate(SessionManager.getUser().getCreateTime(),
                                        CommonContext.getDateFormat().getDAY_SECOND(), CommonContext.get().getSiteTimeZone())),
                        LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FAIL_TITLE));
            }
        }
    }

    private void applyEffectiveOrProfit(VPlayerActivityMessageVo vPlayerActivityMessageVo, Map map, PlayerActivityMessage activityMessage) {
        LOG.debug("优惠活动申请：id：{0},code：{1}", activityMessage.getId(), activityMessage.getCode());
        setPlayerApplyCountAndTIime(activityMessage);
        if (activityMessage.getCompareActivityTime()) {
            setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, APPLY_EXPIRED), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FAIL_TITLE));
        } else {
            if ((Boolean) activityMessage.getDeadlineTime().get("hasApplyFor") && !(Boolean) activityMessage.getDeadlineTime().get("isRepeat")) {
                setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, APPLIED), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_HAVE_TITLE));
            } else {
                apply(vPlayerActivityMessageVo, map);
            }
        }
    }

    private void applyReliefFund(VPlayerActivityMessageVo vPlayerActivityMessageVo, Map map, PlayerActivityMessage activityMessage) {
        LOG.debug("优惠活动申请：id：{0},code：{1}", activityMessage.getId(), activityMessage.getCode());
        setPlayerApplyCountAndTIime(activityMessage);
        if (ActivityTypeEnum.RELIEF_FUND.getCode().equals(activityMessage.getCode())) {
            // 获取周期内参加玩家数(统计优惠名额)
            Integer activityId = activityMessage.getId();
            Map deadlineMap = activityMessage.getDeadlineTime();
            Date applyStartTime = (Date) deadlineMap.get("deadLineStartTime");
            Date applyEndTime = (Date) deadlineMap.get("deadLineTime");
            Long count = countApplyPlayer(activityId, applyStartTime, applyEndTime);
            activityMessage.setCountPlaceNumber(count);
        }
        Integer placesNumber = activityMessage.getPlacesNumber() == null ? 0 : activityMessage.getPlacesNumber();
        if (placesNumber > 0) {
            if ((Boolean) activityMessage.getDeadlineTime().get("hasApplyFor") && !(Boolean) activityMessage.getDeadlineTime().get("isRepeat")) {

                setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, PARTICIPATION), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FAIL_TITLE));//参与中
            } else {
                if (activityMessage.getCountPlaceNumber() >= placesNumber) {
                    setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FULL), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_FAIL_TITLE));
                } else {
                    apply(vPlayerActivityMessageVo, map);
                }
            }
        } else {
            if ((Boolean) activityMessage.getDeadlineTime().get("hasApplyFor") && !(Boolean) activityMessage.getDeadlineTime().get("isRepeat")) {
                setApplyFailReturnStates(map, LocaleTool.tranMessage(Module.ACTIVITY, APPLIED), LocaleTool.tranMessage(Module.ACTIVITY, APPLY_HAVE_TITLE));//已申请
            } else {
                apply(vPlayerActivityMessageVo, map);
            }

        }
    }

    private Map apply(VPlayerActivityMessageVo vPlayerActivityMessageVo, Map map) {
        //处理表单多次提交
        if (StringTool.isBlank(SessionManager.getToken(vPlayerActivityMessageVo.getCode(), vPlayerActivityMessageVo.getId()))) {
            SessionManager.setToken(UUID.randomUUID().toString(), vPlayerActivityMessageVo.getCode(), vPlayerActivityMessageVo.getId());
        } else {
            LOG.debug("用户[{0}]活动[{1}]申请还在处理中，别再点了！", SessionManager.getUserId(), vPlayerActivityMessageVo.getId());
            return map;
        }

        ActivityPlayerApplyVo activityPlayerApplyVo = new ActivityPlayerApplyVo();
        ActivityPlayerApply activityPlayerApply = new ActivityPlayerApply();
        activityPlayerApply.setUserId(SessionManager.getUserId());

        activityPlayerApplyVo.setResult(activityPlayerApply);
        Map<String, Object> resultCode = ServiceActivityTool.vPlayerActivityMessageService().saveActivityApplyInfo(activityPlayerApplyVo, vPlayerActivityMessageVo);

        boolean flag = false;
        String msg = "";
        ActivityResultCodeEnum activityResultCodeEnum = EnumTool.enumOf(ActivityResultCodeEnum.class, resultCode.get("resultcode").toString());
        switch (activityResultCodeEnum) {
            case ACTIVITY_MEET_PRE:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.wanting");
                break;
            case ACTIVITY_MEET:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.check.handsel");
                /*TODO 提示信息待增加类型判断*/
                /*if (vPlayerActivityMessageVo.getIsAudit()){
                    msg = "操作成功,审核通过后彩金将直接发放到您的账户,请注意查收!";
                }else {
                    if (StringTool.equals(vPlayerActivityMessageVo.getCode(), ActivityTypeEnum.REGIST_SEND.getCode()) || StringTool.equals(vPlayerActivityMessageVo.getCode(), ActivityTypeEnum.RELIEF_FUND.getCode())) {
                        msg = "彩金稍后将发放至您的钱包，请注意查收！";
                    } else if (StringTool.equals(vPlayerActivityMessageVo.getCode(), ActivityTypeEnum.PROFIT.getCode()) || StringTool.equals(vPlayerActivityMessageVo.getCode(), ActivityTypeEnum.EFFECTIVE_TRANSACTION.getCode())) {
                        msg = "本期结束后，彩金将直接发放至您的钱包，请注意查收！";
                    }
                }*/
                flag = true;
                break;
            case ACTIVITY_NOT_EXIST:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.noactivity");
                break;
            case ACTIVITY_NOT_CHECK:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.uncheck");
                break;
            case ACTIVITY_NOT_START:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.unstart");
                break;
            case ACTIVITY_END:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.finish");
                break;
            case ACTIVITY_NOT_CONTAIN_RANK:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.uncondition", getCustomerService());
                break;
            case ACTIVITY_REGIST_EXPIRE:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.hasdue");
                break;
            case ACTIVITY_RELIEF_WITHDRAW_EXIST:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.has.withdraw");
                break;
            case ACTIVITY_RELIEF_WITHDRAW_SUCCESS:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.already.withdraw");//下次申领时间：
                break;
            case ACTIVITY_RELIEF_QUOTA_FULL:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.vacancies");
                break;
            case ACTIVITY_RELIEF_NOT_EFFECTIVE:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.effective.no");
                break;
            case ACTIVITY_RELIEF_NOT_LOSS:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.loss.no");
                break;
            case ACTIVITY_RELIEF_NOT_ASSET:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.assets.no");
                break;
            case ACTIVITY_RELIEF_NOT_EFFECTIVE_LOSS:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.effective.loss.no");
                break;
            case ACTIVITY_RELIEF_NOT_EFFECTIVE_ASSET:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.loss.assets.no");
                break;
            case ACTIVITY_RELIEF_NOT_EFFECTIVE_ASSET_LOSS:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.all.no");
                break;
            case ACTIVITY_REPEAT_APPLY:
                msg = LocaleTool.tranMessage(Module.ACTIVITY, "apply.tip.limit");
                break;
        }

        map.put("msg", msg);
        map.put("state", flag);
        SessionManager.setToken("", vPlayerActivityMessageVo.getCode(), vPlayerActivityMessageVo.getId());
        return map;
    }

    private void setPlayerApplyCountAndTIime(PlayerActivityMessage activityMessage) {
        ActivityPlayerApplyVo playerApplyVo = new ActivityPlayerApplyVo();
        playerApplyVo.getSearch().setUserId(SessionManager.getUserId());
        playerApplyVo.getSearch().setActivityMessageId(activityMessage.getId());
        Map result = ServiceActivityTool.activityPlayerApplyService().countPlayerApplyByActivityId(playerApplyVo);
        activityMessage.setAcount((int) (long) result.get("count"));
        activityMessage.setApplyTime((Timestamp) result.get("applytime"));
    }

    private void setApplyFailReturnStates(Map map, String msg, String title) {
        map.put("msg", msg);
        map.put("state", false);
        map.put("title", title);
    }

    private String getPlayerActivitiesIdsJsonByRankId(Integer rankId, HttpServletRequest request) {
        Map<String, PlayerActivityMessage> activities = Cache.getMobileActivityMessages();
        List<String> resultIdList = new ArrayList<>();
        String classifyKey = request.getParameter("classify");
        if (activities != null) {
            for (PlayerActivityMessage m : activities.values()) {
                /*过滤玩家注册时间在注册送活动开始时间之前的*/
                if (SessionManager.getUser() != null && StringTool.equals(m.getCode(), ActivityTypeEnum.REGIST_SEND.getCode()) &&
                        SessionManager.getUser().getCreateTime().before(m.getStartTime())) continue;
                if (StringTool.equals(m.getActivityVersion(), SessionManager.getLocale().toString())
                        && m.getIsDisplay() && isContainUserRank(m, rankId)
                        && (StringTool.equals(m.getStates(), ActivityStateEnum.PROCESSING.getCode()) || StringTool.equals(m.getStates(), ActivityStateEnum.NOTSTARTED.getCode()))) {
                   /*过滤前端活动筛选*/
                    if (StringTool.isBlank(classifyKey) || StringTool.equals(classifyKey, m.getActivityClassifyKey())) {
                        resultIdList.add(m.getSearchId());
                    }
                }

            }
        }
        return JsonTool.toJson(resultIdList);
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

    private Integer getPlayerRankId(Integer userId) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(userId);
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult().getRankId();
    }

    private String getCustomerService() {
        SiteCustomerService siteCustomerService = Cache.getDefaultCustomerService();
        String url = siteCustomerService.getParameter();
        if (StringTool.isNotBlank(url) && !url.contains("http")) {
            url = "http://" + url;
        }
        return url;
    }

    private Long countApplyPlayer(Integer activityId, Date applyStartTime, Date applyEndTime) {
        ActivityPlayerApplyVo activityPlayerApplyVo = new ActivityPlayerApplyVo();
        activityPlayerApplyVo.setActivityMessageId(activityId);
        activityPlayerApplyVo.setApplyStartTime(applyStartTime);
        activityPlayerApplyVo.setApplyEndTime(applyEndTime);
        return ServiceActivityTool.activityPlayerApplyService().countApplyPlayer(activityPlayerApplyVo);
    }
}
