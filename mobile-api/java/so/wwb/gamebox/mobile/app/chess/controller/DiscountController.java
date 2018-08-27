package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.cache.locale.LocaleTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.BooleanTool;
import org.soul.commons.lang.DateTool;
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
import so.wwb.gamebox.mobile.tools.RegexTools;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.operation.po.ActivityPreferentialRelation;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.operation.vo.VPlayerActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.msites.controller.ActivityHallController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;


/**
 * 棋牌包网活动大厅控制器
 */
@Controller
@RequestMapping("/chessActivity")
public class DiscountController extends ActivityHallController {
    private Log LOG = LogFactory.getLog(IndexController.class);
    /**报名参与活动*/
    private static final String[] NEED_FORECAST_ACTIVITYS = new String[]{
            ActivityTypeEnum.DEPOSIT_SEND.getCode(),  //存就送
            ActivityTypeEnum.EFFECTIVE_TRANSACTION.getCode(), //有效交易量
            ActivityTypeEnum.PROFIT.getCode()};  //盈亏返利

    /**无需申请活动*/
    private static final String[] UNNEED_APPLY_ACTIVITYS = new String[]{
            ActivityTypeEnum.CONTENT.getCode(),  //活动内容
            ActivityTypeEnum.BACK_WATER.getCode()};  //反水活动

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
            appSimpleModel.setCode(RegexTools.replaceImgSrc(playerActivityMessage.getActivityDescription(),request.getServerName()));
            appSimpleModel.setName(listVo.getSearchId(playerActivityMessage.getId()));
            boolean unneedApply = Arrays.asList(UNNEED_APPLY_ACTIVITYS).contains(playerActivityMessage.getCode()) ? true : false;
            appSimpleModel.setStatus(unneedApply); //是否无需申请
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
            if(playerActivityMessage == null){
                resutl = AppErrorCodeEnum.ACTIVITY_APPLY_FAIL;
            }else {
                //申请多笔存款奖励时传入订单号，以“，”分隔
                String[] transactionNos = StringTool.isEmpty(vActivityMessageVo.getSearch().getCode()) ? null : vActivityMessageVo.getSearch().getCode().split(",");
                long time = SessionManager.getDate().getNow().getTime();
                String code = playerActivityMessage.getCode();
                String states = vActivityMessageVo.getSearch().getStates();
                if (playerActivityMessage.getStartTime() != null && playerActivityMessage.getStartTime().getTime() >= time) {
                    resutl = AppErrorCodeEnum.ACTIVITY_NOTSTARTED;
                } else if (playerActivityMessage.getEndTime() != null && playerActivityMessage.getEndTime().getTime() < time) {
                    resutl = AppErrorCodeEnum.ACTIVITY_FINISHED;
                } else if (BooleanTool.isTrue(playerActivityMessage.getIsDeleted()) || StringTool.isEmpty(code)) { //活动是否已删除
                    resutl = AppErrorCodeEnum.ACTIVITY_IS_INVALID;
                } else if (ActivityTypeEnum.MONEY.getCode().equals(code)) { //红包
                    return doApplyRedPacketeActivity(playerActivityMessage, request);
                } else if (Arrays.asList(NEED_FORECAST_ACTIVITYS).contains(code) && (transactionNos == null || transactionNos.length == 0) && StringTool.isEmpty(states)) {  //需先报名活动
                    return doFetchActivity(playerActivityMessage, request, code);
                } else {
                    return doApplyActivity(playerActivityMessage, transactionNos, request, code); //申请活动
                }
            }
        }
        AppDiscountApplyResult appDiscountApplyResult = new AppDiscountApplyResult();
        appDiscountApplyResult.setSearchId(vActivityMessageVo.getSearchId(vActivityMessageVo.getSearch().getId()));
        appDiscountApplyResult.setActibityTitle(vActivityMessageVo.getSearch().getActivityName());
        appDiscountApplyResult.setStatus(2); //失败
        appDiscountApplyResult.setApplyResult("您所提交的申请失败！如有问题，请与客服人员联系。");
        appDiscountApplyResult.setTips(resutl.getMsg());
        return AppModelVo.getAppModeVoJson(true, resutl.getCode(), resutl.getMsg(), appDiscountApplyResult, APP_VERSION);
    }

    /**
     * 报名活动
     *
     * @return
     */
    private String doFetchActivity(PlayerActivityMessage playerActivityMessage, HttpServletRequest request,String code) {
        VPlayerActivityMessageVo vPlayerActivityMessageVo = new VPlayerActivityMessageVo();
        vPlayerActivityMessageVo.setResultId(playerActivityMessage.getSearchId());
        vPlayerActivityMessageVo.setCode(playerActivityMessage.getCode());
        Map<String, Object> stringObjectMap = fetchActivityProcess(vPlayerActivityMessageVo, request);
        AppDiscountApplyResult appDiscountApplyResult = new AppDiscountApplyResult();
        appDiscountApplyResult.setSearchId(vPlayerActivityMessageVo.getSearchId(playerActivityMessage.getId()));
        appDiscountApplyResult.setActibityTitle(playerActivityMessage.getActivityName());
        appDiscountApplyResult.setStatus(3); //参与中
        long applyNum = stringObjectMap.get("ApplyNum") == null  ? 0l : (long)stringObjectMap.get("ApplyNum");//当前活动参与人数

        List resultList = new ArrayList<>();
        if (MapTool.isEmpty(stringObjectMap)) {
            appDiscountApplyResult.setStatus(2);
            appDiscountApplyResult.setApplyResult("您所提交的申请失败！如有问题，请与客服人员联系。");
            appDiscountApplyResult.setTips("您还没有符合活动要求的订单，请继续努力哦！");
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.ACTIVITY_APPLY_FAIL.getCode(), AppErrorCodeEnum.ACTIVITY_APPLY_FAIL.getMsg(), appDiscountApplyResult, APP_VERSION);
        }else if(stringObjectMap.get("transactions") != null){  //存就送
            appDiscountApplyResult.setApplyResult("您可以选择申请已满足要求的订单，建议您查看活动细则后，再决定是否立即申请。");
            resultList = (List<PlayerRecharge>)stringObjectMap.get("transactions");
        }else if(stringObjectMap.get("preferentialRelations") != null) {  //有效投注额，盈亏送
            resultList = (List<ActivityPreferentialRelation>) stringObjectMap.get("preferentialRelations");
            String awardTime = stringObjectMap.get("deadLineTime") == null ? " - - " : stringObjectMap.get("deadLineTime").toString();
            if (ActivityTypeEnum.PROFIT.getCode().equals(code)) { //盈亏
                appDiscountApplyResult.setApplyResult("以下是您当前盈亏,统计周期请查看活动细则,申请报名活动后显示派奖时间。");
                appDiscountApplyResult.setTips("当前报名人数：" + applyNum + "人");
            } else {
                appDiscountApplyResult.setTips("活动派奖时间：" + awardTime);
                appDiscountApplyResult.setApplyResult("以下是您当前投注额,统计周期请查看活动细则,加油吧。");
            }
        }
        boolean hasApply = BooleanTool.isFalse((Boolean) stringObjectMap.get("hasApply"));//是否已经申请
        appDiscountApplyResult.setHasApply(hasApply);
        appDiscountApplyResult.setApplyDetails(returnApplyActivityResult(resultList,code,stringObjectMap));
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), appDiscountApplyResult, APP_VERSION);
    }

    /**
     *设置活动参与添加集
     * @return
     */
    private  List<AppActivityApply> returnApplyActivityResult(List<?> list,String code,Map<String, Object> map){
        List<AppActivityApply> result = new ArrayList<>();
        if(CollectionTool.isEmpty(list)) {
            return result;
        }
        PlayerRecharge playerRecharge ;
        ActivityPreferentialRelation relation;
        Object effectivetransaction = map.get("effectivetransaction");//当前投注额
        double effectivetransactionMoney = effectivetransaction == null || effectivetransaction == "" ? 0d : Double.valueOf(String.valueOf(effectivetransaction));
        Object profitloss = map.get("profitloss");//当前盈利额
        double profitlossMoney = profitloss == null || profitloss == "" ? 0d : Double.valueOf(String.valueOf(profitloss));
        TimeZone timeZone = SessionManager.getTimeZone();
        boolean profit_ge = false; //盈利送
        boolean loss_ge = false;  //亏损送
        boolean profit_loss = false; //同时开启
        for(Object obj : list){
            AppActivityApply appActivityApply = new AppActivityApply();
            if(obj instanceof PlayerRecharge){ //存就送
                playerRecharge = (PlayerRecharge)obj;
                appActivityApply.setTransactionNo(playerRecharge.getTransactionNo());
                appActivityApply.setShowSchedule(false);
                appActivityApply.setMayApply(true);
                appActivityApply.setCheckTime(DateTool.formatDate(playerRecharge.getCheckTime(),timeZone,DateTool.yyyy_MM_dd_HH_mm_ss));
                appActivityApply.setReached(playerRecharge.getRechargeAmount()); //存款金额
            }else if(obj instanceof ActivityPreferentialRelation){ //有效投注额，盈亏送
                relation = (ActivityPreferentialRelation)obj;
                appActivityApply.setShowSchedule(true);
                appActivityApply.setMayApply(false);
                Double preferentialValue = relation.getPreferentialValue() == null ? 0d : relation.getPreferentialValue();
                appActivityApply.setStandard(preferentialValue);
                if(ActivityTypeEnum.PROFIT.getCode().equals(code)){ //盈亏
                    if("profit_ge".equals(relation.getPreferentialCode())){
                        profit_ge = true;
                    }else if("loss_ge".equals(relation.getPreferentialCode())){
                        loss_ge = true;
                    }
                    profit_loss = profit_ge && loss_ge;
                    appActivityApply.setReached(Math.abs(profitlossMoney));  //当前值
                    if("profit_ge".equals(relation.getPreferentialCode()) || (profit_loss && profitlossMoney >= 0)){ //盈
                        if("loss_ge".equals(relation.getPreferentialCode())){
                            continue;
                        }
                        if(profitlossMoney < 0){
                            appActivityApply.setReached(0d);
                            appActivityApply.setSatisfy(false);
                        }else{
                            appActivityApply.setSatisfy(Math.abs(profitlossMoney) >= preferentialValue);
                        }
                        appActivityApply.setCondition("盈利"+relation.getOrderColumn());
                    }else if(("loss_ge".equals(relation.getPreferentialCode()) || (profit_loss && profitlossMoney < 0 && !"profit_ge".equals(relation.getPreferentialCode()))) && relation.getOrderColumn() == 1){  //亏  亏损时只对比第一梯度
                        if(profitlossMoney >= 0){
                            appActivityApply.setSatisfy(false);
                            appActivityApply.setReached(0d);
                            appActivityApply.setCondition("亏损金额:0.0");
                        }else{
                            appActivityApply.setSatisfy(Math.abs(profitlossMoney) >= preferentialValue);
                            appActivityApply.setReached(Math.abs(profitlossMoney));
                            appActivityApply.setCondition("亏损金额:"+ Math.abs(profitlossMoney));
                        }
                        result = new ArrayList<>();
                        result.add(appActivityApply);
                        return result;
                    }

                }else{ //有效投注额
                    appActivityApply.setReached(effectivetransactionMoney);
                    appActivityApply.setCondition("条件"+relation.getOrderColumn()+"(有效投注额)");
                    appActivityApply.setSatisfy(effectivetransactionMoney >= preferentialValue);
                }
            }
            result.add(appActivityApply);
        }
        return result;
    }
    /**
     * 申请活动
     *
     * @return
     */
    private String doApplyActivity(PlayerActivityMessage playerActivityMessage,String[] transactionNos, HttpServletRequest request,String code) {
        AppDiscountApplyResult appDiscountApplyResult = new AppDiscountApplyResult();
        appDiscountApplyResult.setActibityTitle(playerActivityMessage.getActivityName()); //活动标题
        Integer status = 2;    // 申请失败
        String applyResult;
        List<AppActivityApply> list = new ArrayList<>();
        if (ActivityTypeEnum.BACK_WATER.getCode().equals(code)) {
            status = 1;//申请成功
            //TODO base 国际化文件 无权增量更新 暂时写死
            //applyResult = LocaleTool.tranMessage(Module.MASTER_CONTENT, "back_water_participation");
            applyResult = "申请成功，您正在参与中！";
        }else if(ActivityTypeEnum.CONTENT.getCode().equals(code)){
            status = 1;
            applyResult = "该活动无需申请！";
        } else {
            VPlayerActivityMessageVo vPlayerActivityMessageVo = new VPlayerActivityMessageVo();
            vPlayerActivityMessageVo.setResultId(playerActivityMessage.getSearchId());
            vPlayerActivityMessageVo.setCode(code);
            vPlayerActivityMessageVo.setTransactionNos(transactionNos);
            Map<String, Object> stringObjectMap = applyActivities(vPlayerActivityMessageVo, request);
            if (MapTool.isEmpty(stringObjectMap)) {
                //applyResult = LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_result_empty");
                applyResult = "很抱歉！当前网络不稳定，请稍后重试！";
            } else {
                appDiscountApplyResult.setSearchId(vPlayerActivityMessageVo.getSearchId(playerActivityMessage.getId())); //活动标题
                boolean state = MapTool.getBooleanValue(stringObjectMap, "state");
                status = state ? 1 : 2;
                //String defaultMsg = state ? LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_success") :
                //        LocaleTool.tranMessage(Module.MASTER_CONTENT, "activity_apply_fail");
                String defaultMsg = state ? "您所提交的申请已经进入审批阶段，请及时跟进申请状况。如有问题，请与客服人员联系。" :
                        "您所提交的申请还未达到活动要求，请多多努力！如有问题，请与客服人员联系。";
                String error = stringObjectMap.get("error") == null ? defaultMsg : stringObjectMap.get("msg").toString();
                applyResult = stringObjectMap.get("msg") == null ? error : stringObjectMap.get("msg").toString();
                if(ActivityTypeEnum.BACK_WATER.getCode().equals(code) || applyResult.lastIndexOf("您的注册时间为：{1}") >= 0){
                    applyResult = applyResult.replace("您的注册时间为：{1}","并且在活动期间注册。");
                }else if("apply_activitty_transaction_times_error".equals(applyResult)){
                    applyResult = "存款次数不足或者存款未完成，请检查存款状态";
                }
                String tips = state && ActivityTypeEnum.DEPOSIT_SEND.getCode().equals(code) ? "操作成功,审核通过后彩金将直接发放到您的账户,请注意查收!" : null;
                appDiscountApplyResult.setTips(tips);
                Object transactionErrorList = stringObjectMap.get("transactionErrorList");
                List<Map<String,Object>> reaultMaps = transactionErrorList == null ? null : (List<Map<String,Object>>)transactionErrorList;
                if(CollectionTool.isNotEmpty(reaultMaps)){
                    String msg,transactionNo,money; //消息提示，订单号，存款金额
                    boolean state4condition;
                    for(Map<String,Object> map : reaultMaps){
                        msg = String.valueOf(map.get("msg"));
                        transactionNo = String.valueOf(map.get("transactionNo"));
                        state4condition = MapTool.getBooleanValue(map, "state");
                        money = map.get("money") == null ? "" : String.valueOf(map.get("money"));
                        AppActivityApply apply = new AppActivityApply();
                        apply.setSatisfy(true);
                        apply.setCondition("存款订单"+transactionNo);
                        list.add(apply); //添加订单
                        apply = new AppActivityApply();
                        apply.setSatisfy(state4condition);
                        apply.setCondition(LocaleTool.tranMessage("apply_activity", msg) + money);
                        list.add(apply); //添加订单申请说明
                    }
                }
            }
        }

        appDiscountApplyResult.setStatus(status);//申请失败
        appDiscountApplyResult.setApplyResult(applyResult);
        appDiscountApplyResult.setApplyDetails(list);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), appDiscountApplyResult, APP_VERSION);
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
        TimeZone timeZone = SessionManager.getTimeZone();
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            List<PlayerActivityMessage> activityList = getActivityMessages(listVo, activityMessageMap);
            if (CollectionTool.isNotEmpty(activityList)) {
                List<ActivityTypeListApp> activitys = new ArrayList<>();
                for (PlayerActivityMessage playerActivityMessage : activityList) {
                    if(ActivityTypeEnum.MONEY.getCode().equals(playerActivityMessage.getCode())){ //排除红包活动
                        continue;
                    }
                    ActivityTypeListApp activityTypeListApp = new ActivityTypeListApp();
                    activityTypeListApp.setSearchId(listVo.getSearchId(playerActivityMessage.getId()));
                    activityTypeListApp.setPhoto(ImageTag.getImagePath(domain, playerActivityMessage.getActivityAffiliated() == null ? playerActivityMessage.getActivityCover() : playerActivityMessage.getActivityAffiliated()));
                    activityTypeListApp.setName(playerActivityMessage.getActivityName());
                    activityTypeListApp.setStatus(playerActivityMessage.getStates());
                    Date beginTime = playerActivityMessage.getStartTime();
                    Date endTime = playerActivityMessage.getEndTime();
                    activityTypeListApp.setTime(DateTool.formatDate(beginTime,timeZone,DateTool.yyyy_MM_dd_HH_mm_ss) +" - "+ DateTool.formatDate(endTime,timeZone,DateTool.yyyy_MM_dd_HH_mm_ss));
                    activitys.add(activityTypeListApp);
                }
                if(CollectionTool.isNotEmpty(activitys)) {
                    type.setActivityList(activitys);
                    result.add(type);
                }
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


