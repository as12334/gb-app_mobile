package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.BooleanTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.operation.vo.VPlayerActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.msites.controller.ActivityHallController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;


/**
 * 棋牌包网首页控制器
 */
@Controller
@RequestMapping("/chessActivity")
public class DiscountController extends ActivityHallController {
    private Log LOG = LogFactory.getLog(IndexController.class);
    private static final String[] NEED_FORECAST_ACTIVITYS = new String[]{
            ActivityTypeEnum.DEPOSIT_SEND.getCode(),  //存就送
            ActivityTypeEnum.EFFECTIVE_TRANSACTION.getCode(), //有效交易量
            ActivityTypeEnum.PROFIT.getCode()};  //盈亏返利

    /**
     * 获取优惠活动类型和标题
     */
    @RequestMapping(value = "/getActivityTypes")
    @ResponseBody
    public String getActivityTypes(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo, request), APP_VERSION);
    }

    /**
     * 获取优惠活动信息
     */
    @RequestMapping(value = "/getActivityById")
    @ResponseBody
    public String getActivityById(VActivityMessageListVo listVo, HttpServletRequest request) {
        if (listVo.getSearch() == null || listVo.getSearch().getId() == null) {
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.ACTIVITY_ID_EMPTY.getCode(), AppErrorCodeEnum.ACTIVITY_ID_EMPTY.getMsg(), null, APP_VERSION);
        }
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getMobileActivityMessages();
        List<PlayerActivityMessage> activityList = getActivityMessages(listVo, activityMessageMap);
        if (CollectionTool.isNotEmpty(activityList)) {
            PlayerActivityMessage playerActivityMessage = activityList.get(0);
            AppSimpleModel appSimpleModel = new AppSimpleModel();
            //appSimpleModel.setCode(RegexTools.replaceImgSrc(playerActivityMessage.getActivityDescription(),request.getServerName()));
            appSimpleModel.setCode(playerActivityMessage.getActivityDescription());
            appSimpleModel.setName(listVo.getSearchId(playerActivityMessage.getId()));
            //TODO 替换H5中路径，开始时间
            return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), appSimpleModel, APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.ACTIVITY_IS_INVALID.getCode(), AppErrorCodeEnum.ACTIVITY_IS_INVALID.getMsg(), null, APP_VERSION);
    }

    /**
     * 活动申请
     */
    @RequestMapping(value = "/toApplyActivity")
    @ResponseBody
    public String toApplyActivity(VActivityMessageVo vActivityMessageVo, HttpServletRequest request) {
        AppErrorCodeEnum resutl;
        if (vActivityMessageVo.getSearch() == null || vActivityMessageVo.getSearch().getId() == null) {
            resutl = AppErrorCodeEnum.ACTIVITY_ID_EMPTY;
        } else {
            PlayerActivityMessage playerActivityMessage = Cache.getMobileActivityMessageInfo(vActivityMessageVo.getSearch().getId().toString());
            long time = SessionManager.getDate().getNow().getTime();
            String code = playerActivityMessage.getCode();
            if (playerActivityMessage.getStartTime() != null && playerActivityMessage.getStartTime().getTime() >= time) {
                resutl = AppErrorCodeEnum.ACTIVITY_NOTSTARTED;
            } else if (playerActivityMessage.getEndTime() != null && playerActivityMessage.getEndTime().getTime() < time) {
                resutl = AppErrorCodeEnum.ACTIVITY_FINISHED;
            } else if (BooleanTool.isTrue(playerActivityMessage.getIsDeleted()) || StringTool.isEmpty(code)) { //活动是否已删除
                resutl = AppErrorCodeEnum.ACTIVITY_IS_INVALID;
            } else if (ActivityTypeEnum.MONEY.getCode().equals(code)) { //红包
                return doApplyRedPacketeActivity(playerActivityMessage, request);
            } else if (Arrays.asList(NEED_FORECAST_ACTIVITYS).contains(code)) {  //需先报名活动
                return doFetchActivity(playerActivityMessage, request);
            } else {
                return doApplyActivity(playerActivityMessage, request); //申请活动
            }
        }
        return AppModelVo.getAppModeVoJson(true, resutl.getCode(), resutl.getMsg(), null, APP_VERSION);
    }

    /**
     * 打开红包
     */
    @RequestMapping(value = "/openRedPackete")
    @ResponseBody
    public String openRedPackete(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo, request), APP_VERSION);
    }

    /**
     * 抢红包
     *
     * @return
     */
    private String doApplyRedPacketeActivity(PlayerActivityMessage playerActivityMessage, HttpServletRequest request) {
        return null;
    }

    /**
     * 报名活动
     *
     * @return
     */
    private String doFetchActivity(PlayerActivityMessage playerActivityMessage, HttpServletRequest request) {
        VPlayerActivityMessageVo vPlayerActivityMessageVo = new VPlayerActivityMessageVo();
        vPlayerActivityMessageVo.setResultId(playerActivityMessage.getSearchId());
        vPlayerActivityMessageVo.setCode(playerActivityMessage.getCode());
        Map<String, Object> stringObjectMap = applyActivities(vPlayerActivityMessageVo, request);
        if (MapTool.isEmpty(stringObjectMap) || !MapTool.getBooleanValue(stringObjectMap, "state")) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.ACTIVITY_APPLY_FAIL.getCode(), AppErrorCodeEnum.ACTIVITY_APPLY_FAIL.getMsg(), stringObjectMap, APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), stringObjectMap, APP_VERSION);
    }

    /**
     * 申请活动
     *
     * @return
     */
    private String doApplyActivity(PlayerActivityMessage playerActivityMessage, HttpServletRequest request) {
        AppDiscountApplyResult appDiscountApplyResult = new AppDiscountApplyResult();
        appDiscountApplyResult.setActibityTitle(playerActivityMessage.getActivityName()); //活动标题
        Integer status = 2;    // 申请失败
        String applyResult;
        List<AppActivityApply> list = new ArrayList<>();
        if (ActivityTypeEnum.BACK_WATER.getCode().equals(playerActivityMessage.getCode())) {
            status = 1;//申请成功
            //TODO base 国际化文件 无权增量更新 暂时写死
            //applyResult = LocaleTool.tranMessage(Module.MASTER_CONTENT, "back_water_participation");
            applyResult = "申请成功，您正在参与中！";
        } else {
            VPlayerActivityMessageVo vPlayerActivityMessageVo = new VPlayerActivityMessageVo();
            vPlayerActivityMessageVo.setResultId(playerActivityMessage.getSearchId());
            vPlayerActivityMessageVo.setCode(playerActivityMessage.getCode());
            Map<String, Object> stringObjectMap = applyActivities(vPlayerActivityMessageVo, request);
            if (MapTool.isEmpty(stringObjectMap)) {
                //applyResult = LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_result_empty");
                applyResult = "很抱歉！当前网络不稳定，请稍后重试！";
            } else {
                boolean state = MapTool.getBooleanValue(stringObjectMap, "state");
                status = state ? 1 : 2;
                //String defaultMsg = state ? LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_success") :
                //        LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_fail");
                String defaultMsg = state ? "您所提交的申请已经进入审批阶段，请及时跟进申请状况。如有问题，请与客服人员联系。" :
                        "您所提交的申请还未达到活动要求，请多多努力！如有问题，请与客服人员联系。";
                String error = stringObjectMap.get("error") == null ? defaultMsg : stringObjectMap.get("msg").toString();
                applyResult = stringObjectMap.get("msg") == null ? error : stringObjectMap.get("msg").toString();
            }
        }
        appDiscountApplyResult.setStatus(status);//申请失败
        appDiscountApplyResult.setApplyResult(applyResult);
        appDiscountApplyResult.setApplyDetails(list);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), appDiscountApplyResult, APP_VERSION);
    }


    /**
     * 获取活动和类型
     */
    private List<ActivityTypeApp> getActivityTypeMessages(VActivityMessageListVo listVo, HttpServletRequest request) {
        List<ActivityTypeApp> types = new ArrayList<>();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        if (StringTool.isNotBlank(activityClassifyKey)) {
            ActivityTypeApp typeApp = new ActivityTypeApp();
            typeApp.setActivityKey(activityClassifyKey);
            types.add(typeApp);
        } else {
            types = getActivityTypes();
        }
        List<ActivityTypeApp> result = new ArrayList<>();
        Map<String, PlayerActivityMessage> activityMessageMap = Cache.getMobileActivityMessages();
        String domain = ServletTool.getDomainFullAddress(request);
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            List<PlayerActivityMessage> activityList = getActivityMessages(listVo, activityMessageMap);

            if (CollectionTool.isNotEmpty(activityList)) {
                List<ActivityTypeListApp> activitys = new ArrayList<>();
                for (PlayerActivityMessage playerActivityMessage : activityList) {
                    ActivityTypeListApp activityTypeListApp = new ActivityTypeListApp();
                    activityTypeListApp.setSearchId(listVo.getSearchId(playerActivityMessage.getId()));
                    activityTypeListApp.setPhoto(ImageTag.getImagePath(domain, playerActivityMessage.getActivityAffiliated() == null ? playerActivityMessage.getActivityCover() : playerActivityMessage.getActivityAffiliated()));
                    activityTypeListApp.setName(playerActivityMessage.getActivityName());
                    activityTypeListApp.setStatus(playerActivityMessage.getStates());
                    activitys.add(activityTypeListApp);
                }
                type.setActivityList(activitys);
                result.add(type);
            }
        }
        return result;
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
     * 筛选出正在进行中的活动
     */
    private List<PlayerActivityMessage> getActivityMessages(VActivityMessageListVo listVo, Map<String, PlayerActivityMessage> activityMessageMap) {
        List<PlayerActivityMessage> result = new ArrayList<>();
        if (MapTool.isEmpty(activityMessageMap)) {
            return result;
        }
        String locale = SessionManager.getLocale().toString();
        Integer rankId = null;
        if (SessionManager.getUser() != null && !SessionManager.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            PlayerRank playerRank = ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
            if (playerRank != null) {
                rankId = playerRank.getId();
            }
        }
        long nowTime = SessionManager.getDate().getNow().getTime();
        boolean isDisplay;
        boolean isDelete;
        boolean isAllRank;
        boolean hasRank;
        String release = ActivityStateEnum.RELEASE.getCode();
        String activityClassifyKey = listVo.getSearch().getActivityClassifyKey();
        String activityId = listVo.getSearch().getId() == null ? null : String.valueOf(listVo.getSearch().getId());

        for (PlayerActivityMessage playerActivityMessage : activityMessageMap.values()) {
            isDisplay = playerActivityMessage.getIsDisplay() != null && playerActivityMessage.getIsDisplay();
            isDelete = playerActivityMessage.getIsDeleted() != null && playerActivityMessage.getIsDeleted();
            if (release.equals(playerActivityMessage.getActivityState()) && locale.equals(playerActivityMessage.getActivityVersion()) && !isDelete && isDisplay && (StringTool.isBlank(activityClassifyKey) || activityClassifyKey.equals(playerActivityMessage.getActivityClassifyKey())) && playerActivityMessage.getEndTime().getTime() > nowTime) {
                isAllRank = playerActivityMessage.getAllRank() != null && playerActivityMessage.getAllRank();
                hasRank = true;
                if (rankId != null && !isAllRank && playerActivityMessage.getRankid() != null && !playerActivityMessage.getRankid().contains(rankId + ",")
                        && playerActivityMessage.getRankid() != null && !playerActivityMessage.getRankid().contains("," + rankId)) {
                    hasRank = false;
                }
                if (hasRank && (StringTool.isEmpty(activityId) || activityId.equals(String.valueOf(playerActivityMessage.getId())))) {
                    result.add(playerActivityMessage);
                }
            }
        }

        return result;
    }
}


