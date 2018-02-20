package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.ActivityTypeApp;
import so.wwb.gamebox.mobile.app.model.ActivityTypeListApp;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

@Controller
@RequestMapping("/discountsOrigin")
public class DiscountsAppController {
    private static final String ACTIVITY_DETAIL_URL = "/promo/promoDetail.html";
    private Log LOG = LogFactory.getLog(DiscountsAppController.class);

    /**
     * 获取优惠活动类型
     */
    @RequestMapping(value = "/getActivityType")
    @ResponseBody
    public String getActivityType() {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypes(), APP_VERSION);
    }

    /**
     * 获取优惠活动
     */
    @RequestMapping(value = "/getActivityTypeList")
    @ResponseBody
    public String getActivityType(VActivityMessageListVo listVo, HttpServletRequest request) {
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getActivityMessages();
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityMessages(listVo, activityMessageMap), APP_VERSION);
    }

    /**
     * 获取优惠活动和类型
     */
    @RequestMapping(value = "/getActivityTypes")
    @ResponseBody
    public String getActivityTypes(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo), APP_VERSION);
    }

    /**
     * 获取活动类型
     *
     * @return types
     */
    private List<ActivityTypeApp> getActivityTypes() {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        List<ActivityTypeApp> types = new ArrayList<>();
        for (SiteI18n site : siteI18nMap.values()) {
            if (StringTool.equalsIgnoreCase(site.getLocale(), SessionManager.getLocale().toString())) {
                ActivityTypeApp type = new ActivityTypeApp();
                type.setActivityKey(site.getKey());
                type.setActivityTypeName(site.getValue());
                types.add(type);
            }
        }
        return types;
    }

    /**
     * 获取活动和类型
     */
    private Map getActivityTypeMessages(VActivityMessageListVo listVo) {
        List<ActivityTypeApp> types = new ArrayList<>();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        if (StringTool.isNotBlank(activityClassifyKey)) {
            ActivityTypeApp typeApp = new ActivityTypeApp();
            typeApp.setActivityKey(activityClassifyKey);
            types.add(typeApp);
        } else {
            types = getActivityTypes();
        }
        Map<String, Object> activityTypes = new HashMap<>(types.size(), 1f);
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getActivityMessages();
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            Map messages = getActivityMessages(listVo, activityMessageMap);
            activityTypes.put(type.getActivityKey(), messages);
        }
        return activityTypes;
    }

    /**
     * 获取正在进行中的活动 转换接口数据
     */
    private Map getActivityMessages(VActivityMessageListVo listVo, Map<String, PlayerActivityMessage> activityMessageMap) {
        Map<String, Object> map = new HashMap<>(2, 1f);
        if (MapTool.isEmpty(activityMessageMap)) {
            map.put("list", new ArrayList<>(0));
            map.put("total", 0);
            return map;
        }
        String locale = SessionManager.getLocale().toString();
        Integer rankId = null;
        if (SessionManager.getUser() != null && !SessionManager.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            rankId = ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo).getId();
        }
        long nowTime = SessionManager.getDate().getNow().getTime();
        boolean isDisplay;
        boolean isDelete;
        boolean isAllRank;
        boolean hasRank;
        String release = ActivityStateEnum.RELEASE.getCode();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        List<ActivityTypeListApp> activityTypeListApps = new ArrayList<>();
        for (PlayerActivityMessage playerActivityMessage : activityMessageMap.values()) {
            isDisplay = playerActivityMessage.getIsDisplay() != null && playerActivityMessage.getIsDisplay();
            isDelete = playerActivityMessage.getIsDeleted() != null && playerActivityMessage.getIsDeleted();
            if (release.equals(playerActivityMessage.getActivityState()) && locale.equals(playerActivityMessage.getActivityVersion()) && !isDelete && isDisplay && (StringTool.isBlank(activityClassifyKey) || activityClassifyKey.equals(playerActivityMessage.getActivityClassifyKey())) && playerActivityMessage.getStartTime().getTime() <= nowTime && playerActivityMessage.getEndTime().getTime() > nowTime) {
                isAllRank = playerActivityMessage.getAllRank() != null && playerActivityMessage.getAllRank();
                hasRank = true;
                if (rankId != null && !isAllRank && !playerActivityMessage.getRankid().contains(rankId + ",") && !playerActivityMessage.getRankid().contains("," + rankId)) {
                    hasRank = false;
                }
                if (hasRank) {
                    ActivityTypeListApp activityApp = new ActivityTypeListApp();
                    activityApp.setId(playerActivityMessage.getId());
                    activityApp.setPhoto(playerActivityMessage.getActivityAffiliated() == null ? playerActivityMessage.getActivityCover() : playerActivityMessage.getActivityAffiliated());
                    activityApp.setUrl(ACTIVITY_DETAIL_URL + "?searchId=" + listVo.getSearchId(playerActivityMessage.getId()));
                    activityApp.setName(playerActivityMessage.getActivityName());
                    activityTypeListApps.add(activityApp);
                }
            }
        }
        map.put("list", activityTypeListApps);
        int total = 0;
        if (CollectionTool.isNotEmpty(activityTypeListApps)) {
            total = activityTypeListApps.size();
        }
        map.put("total", total);
        return map;
    }
}
