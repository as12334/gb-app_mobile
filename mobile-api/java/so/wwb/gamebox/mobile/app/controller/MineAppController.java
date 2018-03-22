package so.wwb.gamebox.mobile.app.controller;

import org.apache.commons.collections.map.HashedMap;
import org.apache.shiro.session.SessionException;
import org.soul.commons.bean.Pair;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criterion;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.support._Module;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.log.audit.enums.OpMode;
import org.soul.model.msg.notice.vo.NoticeReceiveVo;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.model.sys.po.SysDict;
import org.soul.web.shiro.local.PassportResult;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.app.validateForm.PlayerAdvisoryAppForm;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.enums.UserTaskEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.fund.vo.VPlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.so.VPreferentialRecodeSo;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.PlayerAdvisoryEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.model.master.tasknotify.vo.UserTaskReminderVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;
import so.wwb.gamebox.web.shiro.common.delegate.IPassportDelegate;
import so.wwb.gamebox.web.shiro.common.filter.KickoutFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

/**
 * Created by ed on 17-12-31.
 */
@Controller
@RequestMapping("/mineOrigin")
public class MineAppController extends BaseMineController {
    private static final String SAFE_PASSWORD_VALIDATE_CODE_URL = "/captcha/securityPwd.html";
    private Log LOG = LogFactory.getLog(MineAppController.class);
    @Autowired(required = false)
    private IPassportDelegate passportDelegate;


    /**
     * 　我的优惠记录
     *
     * @param vPreferentialRecodeListVo
     * @return
     */
    @RequestMapping(value = "/getMyPromo")
    @ResponseBody
    public String getMyPromo(VPreferentialRecodeListVo vPreferentialRecodeListVo) {
        VPreferentialRecodeSo so = vPreferentialRecodeListVo.getSearch();
        so.setActivityVersion(SessionManager.getLocale().toString());
        so.setUserId(SessionManager.getUserId());
        so.setCurrentDate(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);

        Map<String, Object> map = new HashMap<>(2, 1f);
        map.put("totalCount", vPreferentialRecodeListVo.getPaging().getTotalCount()); // 总数
        map.put("list", buildingMyPromoApp(vPreferentialRecodeListVo.getResult()));

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), map, APP_VERSION);
    }

    /**
     * 一键刷新
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/refresh")
    @ResponseBody
    public String refresh(HttpServletRequest request) {
        UserInfoApp userInfo = appRefresh(request);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                userInfo,
                APP_VERSION);
    }

    /**
     * 获取资金记录列表
     *
     * @param listVo
     * @return
     */
    @RequestMapping(value = "/getFundRecord")
    @ResponseBody
    public String getFundRecord(VPlayerTransactionListVo listVo) {

        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);

        FundRecordApp fundRecordApp = new FundRecordApp();
        getFund(fundRecordApp);
        listVo.getSearch().setNoDisplay(TransactionWayEnum.MANUAL_PAYOUT.getCode());
        listVo.getSearch().setLotterySite(ParamTool.isLotterySite());
        listVo = ServiceSiteTool.vPlayerTransactionService().search(listVo);

        List<VPlayerTransaction> vPlayerTransactionList = listVo.getResult();
        List<FundListApp> fundListAppList = buildList(vPlayerTransactionList);
        fundRecordApp.setFundListApps(fundListAppList);

        //统计页面反水等等数据
        fundRecordApp.setSumPlayerMap(getSumPlayerFunds(listVo));
//        if (listVo.getSearch().getBeginCreateTime() == null && listVo.getSearch().getEndCreateTime() == null) {
        fundRecordApp.setMinDate(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
        fundRecordApp.setMaxDate(SessionManager.getDate().getNow());
//        }
        fundRecordApp.setTotalCount(listVo.getPaging().getTotalCount());
        fundRecordApp.setCurrency(getCurrencySign(SessionManager.getUser().getDefaultCurrency()));

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                fundRecordApp, APP_VERSION);
    }

    /**
     * 获取资金记录的，资金类型
     *
     * @return
     */
    @RequestMapping(value = "/getTransactionType")
    @ResponseBody
    public String getTransactionType() {
        VPlayerTransactionListVo listVo = new VPlayerTransactionListVo();
        FundRecordApp recordApp = new FundRecordApp();
        listVo = preList(listVo);
        Map map = listVo.getDictCommonTransactionType();
        recordApp = buildDictCommonTransactionType(map, recordApp);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                recordApp.getTransactionMap(), APP_VERSION);
    }

    /**
     * 获取资金记录详情
     *
     * @param searchId
     * @return
     */
    @RequestMapping(value = "/getFundRecordDetails")
    @ResponseBody
    public String getFundRecordDetails(Integer searchId, HttpServletRequest request) {
        VPlayerTransactionVo vo = new VPlayerTransactionVo();
        if (searchId != null) {
            vo.getSearch().setId(Integer.valueOf(searchId));
            vo = ServiceSiteTool.vPlayerTransactionService().get(vo);
        }

        VPlayerWithdrawVo withdrawVo = new VPlayerWithdrawVo();
        if (vo.getResult() != null && TransactionTypeEnum.WITHDRAWALS.getCode().equals(vo.getResult().getTransactionType())) {   //如果是取款
            if (StringTool.isNotBlank(vo.getResult().getTransactionNo())) {
                withdrawVo.getSearch().setId(vo.getResult().getSourceId());
                withdrawVo = ServiceSiteTool.vPlayerWithdrawService().get(withdrawVo);
            }
        }

        RecordDetailApp recordDetailApp = new RecordDetailApp();
        recordDetailApp = buildRecordDetailApp(recordDetailApp, vo, withdrawVo, request);

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                recordDetailApp, APP_VERSION);
    }

    /**
     * 获取投注记录列表
     *
     * @param listVo
     * @return
     */
    @RequestMapping(value = "/getBettingList")
    @ResponseBody
    public String getBettingList(PlayerGameOrderListVo listVo, Boolean isShowStatistics) {
        BettingDataApp bettingDataApp = new BettingDataApp();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDateForgetBetting(listVo);

        listVo = ServiceSiteTool.playerGameOrderService().search(listVo);
        List<PlayerGameOrder> gameOrderList = listVo.getResult();
        bettingDataApp.setTotalSize(listVo.getPaging().getTotalCount());
        //提高性能，查询分页时只统计一次
        if (isShowStatistics != null && isShowStatistics) {
            bettingDataApp.setStatisticsData(statisticsData(listVo));
        }
        bettingDataApp.setList(buildBetting(gameOrderList));

        //设置默认时间
        bettingDataApp.setMinDate(listVo.getSearch().getBeginBetTime());
        bettingDataApp.setMaxDate(listVo.getSearch().getEndBetTime());

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                bettingDataApp, APP_VERSION);
    }

    /**
     * 初始化跳转站点消息－发送消息－获取类型
     *
     * @return
     */
    @RequestMapping(value = "/getNoticeSiteType")
    @ResponseBody
    public String goAddNoticeSite() {

        Map<String, SysDict> advisoryType = DictTool.get(DictEnum.ADVISORY_TYPE);
        Iterator<String> iter = advisoryType.keySet().iterator();
        List<AdvisoryType> advisoryTypeList = ListTool.newArrayList();
        while (iter.hasNext()) {
            String key = iter.next();
            AdvisoryType type = new AdvisoryType();
            SysDict dict = advisoryType.get(key);
            dict.getRemark();
            type.setAdvisoryType(key);
            type.setAdvisoryName(dict.getRemark());
            advisoryTypeList.add(type);

        }

        Map<String, Object> map = MapTool.newHashMap();
        map.put("advisoryTypeList", advisoryTypeList);
        map.put("isOpenCaptcha", false);
        if (SessionManager.getSendMessageCount() != null && SessionManager.getSendMessageCount() >= SEND_MSG_CAPTCHA_COUNT) {
            map.put("isOpenCaptcha", true);  //如果次数大于等于三次则页面出现验证码,同时给出验证码url
            map.put("captcha_value", "/captcha/" + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix() + ".html?t=" + System.currentTimeMillis());
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 获取站点消息－我的消息　所有发送的问题
     *
     * @param listVo
     * @return
     */
    @RequestMapping(value = "/advisoryMessage")
    @ResponseBody
    public String advisoryMessage(VPlayerAdvisoryListVo listVo) {
        //提问内容+未读数量
        listVo = searchAdvisoryList(listVo);
        getUnReadList(listVo);
        List<AdvisoryMessageApp> messageAppList = ListTool.newArrayList();
        for (VPlayerAdvisory advisory : listVo.getResult()) {
            AdvisoryMessageApp messageApp = new AdvisoryMessageApp();
            messageApp.setAdvisoryTitle(advisory.getAdvisoryTitle());
            messageApp.setAdvisoryContent(advisory.getAdvisoryContent());

            messageApp.setAdvisoryTime(advisory.getAdvisoryTime().getTime());
            messageApp.setReplyTitle(advisory.getReplyTitle());
            messageApp.setId(advisory.getId());
            if (advisory.getIsRead() == null) {
                messageApp.setRead(true);
            } else {
                if (advisory.getIsRead() == false) { //代表未读,
                    messageApp.setRead(false);
                } else {
                    messageApp.setRead(true);
                }
            }
            messageAppList.add(messageApp);
        }
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("total", listVo.getPaging().getTotalCount());
        dataMap.put("dataList", messageAppList);

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                dataMap, APP_VERSION);
    }

    /**
     * 站点消息－我的消息　删除提问
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteAdvisoryMessage", method = RequestMethod.POST)
    @ResponseBody
    public String deleteAdvisoryMessage(String ids) {
        String[] id = ids.split(",");
        PlayerAdvisoryVo vo = new PlayerAdvisoryVo();
        for (String messageId : id) {
            PlayerAdvisoryListVo listVo = new PlayerAdvisoryListVo();
            listVo.getSearch().setContinueQuizId(Integer.valueOf(messageId));
            listVo = ServiceSiteTool.playerAdvisoryService().search(listVo);
            for (PlayerAdvisory obj : listVo.getResult()) {
                vo.setSuccess(false);
                vo.setResult(new PlayerAdvisory());
                vo.getResult().setId(obj.getId());
                vo.getResult().setPlayerDelete(true);
                vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
                vo = ServiceSiteTool.playerAdvisoryService().updateOnly(vo);
            }
            vo.setSuccess(false);
            vo.setResult(new PlayerAdvisory());
            vo.getResult().setId(Integer.valueOf(messageId));
            vo.getResult().setPlayerDelete(true);
            vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
            vo = ServiceSiteTool.playerAdvisoryService().updateOnly(vo);
        }
        if (vo.isSuccess()) {
            vo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.DELETE_SUCCESS));
        } else {
            vo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.DELETE_FAILED));
        }
        HashMap map = new HashMap(2, 1f);
        map.put("msg", StringTool.isNotBlank(vo.getOkMsg()) ? vo.getOkMsg() : vo.getErrMsg());
        map.put("state", Boolean.valueOf(vo.isSuccess()));

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 站点消息－我的消息　标记已读
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/getSelectAdvisoryMessageIds", method = RequestMethod.POST)
    @ResponseBody
    public String getSelectAdvisoryMessageIds(String ids) {
        String[] id = ids.split(",");
        for (String messageId : id) {
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            //判断是否已读
            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});

            ServiceSiteTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(Integer.valueOf(messageId));
                ServiceSiteTool.playerAdvisoryReadService().insert(parVo);
            }
        }

        HashMap map = new HashMap(1, 1f);

        map.put("state", true);
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 我的消息，问题详细
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/advisoryMessageDetail", method = RequestMethod.POST)
    @ResponseBody
    public String advisoryMessageDetail(Integer id) {
        //当前咨询信息
        VPlayerAdvisoryReplyListVo listVo = new VPlayerAdvisoryReplyListVo();
        listVo.getPaging().setPageSize(60);
        VPlayerAdvisoryListVo vPlayerAdvisoryListVo = new VPlayerAdvisoryListVo();
        vPlayerAdvisoryListVo.getSearch().setId(id);

        List<VPlayerAdvisory> vPlayerAdvisoryList = ServiceSiteTool.vPlayerAdvisoryService().searchVPlayerAdvisoryReply(vPlayerAdvisoryListVo);

        Map map = new TreeMap(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Integer) o1) - ((Integer) o2);
            }
        });

        List<AdvisoryMessageDetailApp> detailAppList = new ArrayList<>();

        for (VPlayerAdvisory obj : vPlayerAdvisoryList) {
            //回复标题和内容
            listVo.getSearch().setPlayerAdvisoryId(obj.getId());
            listVo = ServiceSiteTool.vPlayerAdvisoryReplyService().search(listVo);
            map.put(obj.getId(), listVo);

            //判断是否已读
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(obj.getId());
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});
            ServiceSiteTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            AdvisoryMessageDetailApp detailApp = new AdvisoryMessageDetailApp();
            detailApp = buildingAdvisoryMessageDetailApp(detailApp, obj);

            List<AdvisoryMessageReplyListApp> replyList = new ArrayList<>();
            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(obj.getId());
                ServiceSiteTool.playerAdvisoryReadService().insert(parVo);

                AdvisoryMessageReplyListApp replyApp = new AdvisoryMessageReplyListApp();
                replyApp.setReplyTitle(replay.getReplyTitle());
                replyApp.setReplyContent(replay.getReplyContent());
                replyApp.setReplyTime(replay.getReplyTime().getTime());
                replyList.add(replyApp);
            }
            detailApp.setReplyList(replyList);
            detailAppList.add(detailApp);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                detailAppList, APP_VERSION);
    }

    /**
     * 申请优惠，保存发送消息
     *
     * @param result
     * @param code
     * @return
     */
    @RequestMapping(value = "/addNoticeSite", method = RequestMethod.POST)
    @ResponseBody
    public String addNoticeSite(@FormModel @Valid PlayerAdvisoryAppForm form, BindingResult result, String code) {
        if (result.hasErrors()) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(),
                    null, APP_VERSION);
        }

        PlayerAdvisoryVo playerAdvisoryVo = new PlayerAdvisoryVo();
        if (playerAdvisoryVo.getResult() == null) {
            playerAdvisoryVo.setResult(new PlayerAdvisory());
        }

        playerAdvisoryVo.getResult().setAdvisoryType(form.getResult_advisoryType());
        playerAdvisoryVo.getResult().setAdvisoryTitle(form.getResult_advisoryTitle());
        playerAdvisoryVo.getResult().setAdvisoryContent(form.getResult_advisoryContent());

        if (playerAdvisoryVo != null && playerAdvisoryVo.getResult() != null) {
            playerAdvisoryVo.setSuccess(false);
            playerAdvisoryVo.getResult().setAdvisoryTime(SessionManager.getDate().getNow());
            playerAdvisoryVo.getResult().setPlayerId(SessionManager.getUserId());
            playerAdvisoryVo.getResult().setReplyCount(0);
            playerAdvisoryVo.getResult().setQuestionType(PlayerAdvisoryEnum.QUESTION.getCode());
        }


        HashMap map = new HashMap(4, 1f);
        if (SessionManager.getSendMessageCount() != null && SessionManager.getSendMessageCount() >= 3) {
            map.put("isOpenCaptcha", true);
            map.put("captcha_value", "/captcha/" + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix() + ".html?t=" + System.currentTimeMillis());
            if (!StringTool.isNotBlank(code)) {
                return AppModelVo.getAppModeVoJson(false,
                        AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getCode(),
                        AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getMsg(),
                        null, APP_VERSION);
            }
            if (!checkFeedCode(code)) {
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                        AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                        null, APP_VERSION);
            }

        }
        playerAdvisoryVo = ServiceSiteTool.playerAdvisoryService().insert(playerAdvisoryVo);

        if (playerAdvisoryVo.isSuccess()) {
            playerAdvisoryVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));
            //发送消息数量
            Integer sendMessageCount = SessionManager.getSendMessageCount() == null ? 0 : SessionManager.getSendMessageCount();
            SessionManager.setsSendMessageCount(sendMessageCount + 1);
            if (SessionManager.getSendMessageCount() >= SEND_MSG_CAPTCHA_COUNT) {
                map.put("isOpenCaptcha", true);
                map.put("captcha_value", "/captcha/" + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix() + ".html?t=" + System.currentTimeMillis());
            }
            //生成任务提醒
            UserTaskReminderVo userTaskReminderVo = new UserTaskReminderVo();
            userTaskReminderVo.setTaskEnum(UserTaskEnum.PLAYERCONSULTATION);
            ServiceSiteTool.userTaskReminderService().addTaskReminder(userTaskReminderVo);
        } else {
            playerAdvisoryVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }

        map.put("msg", StringTool.isNotBlank(playerAdvisoryVo.getOkMsg()) ? playerAdvisoryVo.getOkMsg() : playerAdvisoryVo.getErrMsg());
        map.put("state", Boolean.valueOf(playerAdvisoryVo.isSuccess()));
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 安全码信息
     *
     * @return
     */
    @RequestMapping(value = "/initSafePassword")
    @ResponseBody
    public String initSafePassword() {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getSafePassword(new AppModelVo()).getData(),
                APP_VERSION);
    }

    private AppModelVo getSafePassword(AppModelVo vo) {
        SysUser user = SessionManager.getUser();
        Map<String, Object> map = new HashMap<>(4, 1f);
        map.put("hasRealName", StringTool.isNotBlank(user.getRealName()));
        if (StringTool.isBlank(user.getPermissionPwd())) {
            map.put("hasPermissionPwd", false);
            vo.setData(map);
            return vo;
        }

        map.put("hasPermissionPwd", true);
        if (isLock()) {//如果冻结
            map.put("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
            map.put("lockTime", formatLockTime(user.getSecpwdFreezeStartTime()));
            vo.setData(map);
            return vo;
        }

        //判断是否出现验证码,大于2显示验证码
        Integer errorTimes = user.getSecpwdErrorTimes();
        errorTimes = errorTimes == null ? 0 : errorTimes;
        map.put("isOpenCaptcha", errorTimes > 1);
        map.put("remindTimes", APP_ERROR_TIMES - errorTimes);
        map.put("captChaUrl", SAFE_PASSWORD_VALIDATE_CODE_URL);
        vo.setData(map);
        return vo;
    }

    /**
     * 设置真实姓名
     *
     * @param realName
     * @return
     */
    @RequestMapping(value = "/setRealName", method = RequestMethod.POST)
    @ResponseBody
    public String setRealNameApp(String realName) {
        if (StringTool.isBlank(realName)) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }

        if (!setRealName(realName)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getMsg(),
                    null,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    /**
     * 修改安全码
     *
     * @param password
     * @return
     */
    @RequestMapping(value = "/updateSafePassword", method = RequestMethod.POST)
    @ResponseBody
    public String updateSafePassword(SecurityPassword password) {
        AppModelVo vo = new AppModelVo();

        vo = getSafePassword(vo);
        //验证真实姓名
        if (StringTool.isBlank(password.getRealName())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        //验证密码
        if (StringTool.isBlank(password.getPwd1())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        //验证码
        if (verifyCode(password)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                    AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        //真实姓名
        if (!verifyRealName(password)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.REAL_NAME_ERROR.getCode(),
                    AppErrorCodeEnum.REAL_NAME_ERROR.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        //密码复杂度
        if (PasswordRule.isWeak(password.getPwd1())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SAFE_PASSWORD_TOO_SIMPLE.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_TOO_SIMPLE.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        //验证原始密码不能与新密码相同
        if (StringTool.equals(password.getOriginPwd(), password.getPwd1())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.PASSWORD_SAME.getCode(),
                    AppErrorCodeEnum.PASSWORD_SAME.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        if (!verifyOriginPwd(password)) {
            Map<String, Object> map = new HashMap<>();
            SysUser user = SessionManager.getUser();
            Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();
            setErrorTimes(map, user, errorTimes);

            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getCode(),
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        if (!setRealName(password.getRealName())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }
        boolean isSUCCESS = savePassword(password.getPwd1());
        if (isSUCCESS) {
            SessionManager.clearPrivilegeStatus();
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                vo.getData(),
                APP_VERSION);
    }

    /**
     * 修改登录密码
     *
     * @param updatePasswordVo
     * @param code
     * @return
     */
    @RequestMapping(value = "/updateLoginPassword", method = RequestMethod.POST)
    @ResponseBody
    public String updateLoginPassword(UpdatePasswordVo updatePasswordVo, String code) {
        if (StringTool.isBlank(updatePasswordVo.getPassword())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.PASSWORD_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (StringTool.isBlank(updatePasswordVo.getNewPassword())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.NEW_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.NEW_PASSWORD_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        //用户是否被冻结
        if (isLock()) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_LOCK.getCode(),
                    AppErrorCodeEnum.USER_LOCK.getMsg(),
                    null,
                    APP_VERSION);
        }
        //2次错误以上需要验证码
        SysUser curUser = SessionManagerCommon.getUser();
        int errorTimes = curUser.getLoginErrorTimes() == null ? -1 : curUser.getLoginErrorTimes();
        if (errorTimes >= TWO) {
            if (StringTool.isBlank(code)) {
                return AppModelVo.getAppModeVoJson(false,
                        AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getCode(),
                        AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getMsg(),
                        null,
                        APP_VERSION);
            }
            if (!checkCode(code)) {
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                        AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                        null,
                        APP_VERSION);
            }
        }
        //验证旧密码
        String oldPwd = AuthTool.md5SysUserPassword(updatePasswordVo.getPassword(), SessionManager.getUserName());
        LOG.debug("玩家{0}旧密码：{1},新密码设置为{2},Md5后为{3}", SessionManager.getUserName(), SessionManager.getUser().getPassword(), updatePasswordVo.getPassword(), oldPwd);
        if (!StringTool.equalsIgnoreCase(oldPwd, SessionManager.getUser().getPassword())) {
            Map map = setPwdErrorTimes(errorTimes);
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.PASSWORD_ERROR.getCode(),
                    AppErrorCodeEnum.PASSWORD_ERROR.getMsg(),
                    map,
                    APP_VERSION);
        }
        //密码相同验证新密码不能和旧密码一样
        String newPwd = AuthTool.md5SysUserPassword(updatePasswordVo.getNewPassword(), SessionManager.getUserName());
        if (StringTool.equalsIgnoreCase(newPwd, SessionManager.getUser().getPassword())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.PASSWORD_SAME.getCode(),
                    AppErrorCodeEnum.PASSWORD_SAME.getMsg(),
                    null,
                    APP_VERSION);
        }

        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = new SysUser();
        sysUser.setId(SessionManager.getUserId());
        sysUser.setPassword(newPwd);
        sysUser.setPasswordLevel(updatePasswordVo.getPasswordLevel());
        //修改成功需将登录错误次数修改为null
        sysUser.setLoginErrorTimes(null);
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_PASSWORD, SysUser.PROP_PASSWORD_LEVEL, SysUser.PROP_LOGIN_ERROR_TIMES);
        boolean success = ServiceTool.sysUserService().updateOnly(sysUserVo).isSuccess();
        if (!success) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getMsg(),
                    null,
                    APP_VERSION);
        }

        SessionManager.refreshUser();
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    /**
     * 系统公告
     *
     * @return
     */
    @RequestMapping(value = "/getSysNotice")
    @ResponseBody
    public String getSysNotice(VSystemAnnouncementListVo vListVo) {
        Map map = getSystemNotice(vListVo);

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 登陆是否开启验证码
     */
    @RequestMapping(value = "/loginIsOpenVerify")
    @ResponseBody
    public String loginIsOpenVerify() {

        Map<String, Boolean> map = new HashedMap();
        map.put("isOpenCaptcha", SessionManagerCommon.isOpenCaptcha());
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 系统公告详情
     *
     * @return
     */
    @RequestMapping(value = "/getSysNoticeDetail")
    @ResponseBody
    public String getSysNoticeDetail(VSystemAnnouncementListVo vListVo) {
        if (vListVo.getSearch().getId() == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getSystemNoticeDetail(vListVo),
                APP_VERSION);
    }

    /**
     * 游戏公告
     *
     * @return
     */
    @RequestMapping(value = "/getGameNotice")
    @ResponseBody
    public String getGameNotice(VSystemAnnouncementListVo listVo) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getAppGameNotice(listVo),
                APP_VERSION);
    }

    /**
     * 游戏公告详情
     *
     * @return
     */
    @RequestMapping(value = "/getGameNoticeDetail")
    @ResponseBody
    public String getGameNoticeDetail(VSystemAnnouncementListVo listVo) {
        if (listVo.getSearch().getId() == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getAppGameNoticeDetail(listVo),
                APP_VERSION);
    }

    /**
     * 站点消息-->系统消息
     *
     * @return
     */
    @RequestMapping(value = "/getSiteSysNotice")
    @ResponseBody
    public String getSiteSysNotice(VNoticeReceivedTextListVo listVo) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getAppSiteSysNotice(listVo),
                APP_VERSION);
    }

    /**
     * 站点消息-->系统消息 -->标记已读
     *
     * @return
     */
    @RequestMapping(value = "/setSiteSysNoticeStatus")
    @ResponseBody
    public String setSiteSysNoticeStatus(NoticeReceiveVo noticeReceiveVo, String ids) {
        if (StringTool.isBlank(ids)) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }

        String[] idArray = ids.split(SPLIT_REGEX);
        List<Integer> list = ListTool.newArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        noticeReceiveVo.setIds(list);
        ServiceTool.noticeService().markSiteMsg(noticeReceiveVo);

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    /**
     * 删除系统信息
     *
     * @param noticeVo
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteSiteSysNotice", method = RequestMethod.POST)
    @ResponseBody
    public String deleteSiteSysNotice(NoticeReceiveVo noticeVo, String ids) {
        if (StringTool.isBlank(ids)) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }

        String[] idArray = ids.split(SPLIT_REGEX);
        List<Integer> list = new ArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        noticeVo.setIds(list);
        boolean bool = ServiceTool.noticeService().deleteSiteMsg(noticeVo);
        if (!bool) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_STATUS_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_STATUS_FAIL.getMsg(),
                    null,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    /**
     * 站点消息-->系统消息详情
     *
     * @return
     */
    @RequestMapping(value = "/getSiteSysNoticeDetail")
    @ResponseBody
    public String getSiteSysNoticeDetail(VNoticeReceivedTextVo vReceivedVo, NoticeReceiveVo noticeReceiveVo, HttpServletRequest request) {
        if (noticeReceiveVo.getSearch().getId() == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getAppSiteNoticeDetail(vReceivedVo, noticeReceiveVo, request),
                APP_VERSION);
    }

    /**
     * 获取站点中心未读条数
     *
     * @return
     */
    @RequestMapping(value = "/getUnReadCount")
    @ResponseBody
    public String getUnReadCount(VPlayerAdvisoryListVo listVo) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                unReadCount(listVo),
                APP_VERSION);
    }

    /**
     * 一键回收
     *
     * @return
     */
    @RequestMapping(value = "/recovery")
    @ResponseBody
    public String recovery(HttpServletRequest request, PlayerApiVo playerApiVo) {
        if (!SessionManagerCommon.isAutoPay()) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.NOT_RECOVER.getCode(),
                    AppErrorCodeEnum.NOT_RECOVER.getMsg(),
                    null,
                    APP_VERSION);
        }
        Map map = appRecovery(playerApiVo);
        Boolean isSuccess = (Boolean) map.get("isSuccess");
        if (isSuccess == null || !isSuccess) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_STATUS_FAIL.getCode(),
                    map.get("msg") != null ? map.get("msg").toString() : AppErrorCodeEnum.UPDATE_STATUS_FAIL.getMsg(),
                    null,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                new HashMap<>(0),
                APP_VERSION);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/logout")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        if (SessionManager.getUser() == null) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    PassportResult.SUCCESS,
                    APP_VERSION);
        }
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        Integer entranceId = passportDelegate.getEntranceType(contextPath, uri);
        SessionManagerCommon.setAttribute(SessionKey.S_ENTRANCE, String.valueOf(entranceId));
        try {
            passportDelegate.onLogoutDelegate(request, response);
            SessionManagerCommon.clearSession();
        } catch (SessionException ise) {
            LOG.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                PassportResult.SUCCESS,
                APP_VERSION);
    }

    /**
     * 获取分享好友相关信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserPlayerRecommend")
    @ResponseBody
    public String getUserPlayerRecommend(HttpServletRequest request, PlayerRecommendAwardListVo listVo) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getPlayerRecommend(request, listVo),
                APP_VERSION);
    }

    /**
     * 验证安全密码
     *
     * @param password
     * @return
     */
    @RequestMapping(value = "/checkSafePassword")
    @ResponseBody
    public String checkSafePassword(SecurityPassword password) {
        if (StringTool.isBlank(password.getOriginPwd())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (!verifyOriginPwd(password)) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getCode(),
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getMsg(),
                    null,
                    APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    /**
     * 定义一个接口，App端每隔一段时间请求一次，防止掉线
     *
     * @return
     */
    @RequestMapping(value = "/alwaysRequest", method = RequestMethod.POST)
    @ResponseBody
    public String alwaysRequest() {

        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), null, APP_VERSION);
    }


    /**
     * 验证吗remote验证
     *
     * @param code
     * @return
     */
    public boolean checkCode(@RequestParam("code") String code) {
        String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_LOGIN.getSuffix());
        return StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code);
    }

    /**
     * 验证吗remote验证
     * <p/>
     * 我的消息  保存申请验证吗remote验证
     *
     * @param code
     * @return
     */
    public boolean checkFeedCode(@RequestParam("code") String code) {
        String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix());
        return StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code);
    }

    private Map setPwdErrorTimes(int errorTimes) {
        Map map = MapTool.newHashMap();
        errorTimes += DEFAULT_TIME;
        Date now = DateQuickPicker.getInstance().getNow();
        if (errorTimes == RECOMMEND_DAYS) {
            errorTimes = ZERO;
        }

        map.put("isOpenCaptcha", errorTimes >= TWO);
        if (errorTimes < APP_ERROR_TIMES) {
            map.put("remainTimes", APP_ERROR_TIMES - errorTimes);
            updateSysUserErrorTimes(errorTimes, now, null);
        } else if (errorTimes >= APP_ERROR_TIMES) {
            map.put("remainTimes", errorTimes);
            updateSysUserErrorTimes(errorTimes, now, DateTool.addHours(now, 3));
            KickoutFilter.loginKickoutAll(SessionManager.getUserId(), OpMode.AUTO, "移动修改密码错误踢出用户");
        }

        return map;
    }

    /**
     * 修改错误次数
     *
     * @param map
     * @param user
     * @param errorTimes
     */
    private void setErrorTimes(Map<String, Object> map, SysUser user, Integer errorTimes) {
        errorTimes += 1;
        user.setSecpwdErrorTimes(errorTimes);
        if (errorTimes == 1) {
            this.updateErrorTimes(user);
        } else if (errorTimes > 1 && errorTimes < 5) {
            map.put(KEY_CAPTCHA, true);
            map.put(KEY_TIMES_KEY, APP_ERROR_TIMES - errorTimes);
            this.updateErrorTimes(user);
        } else if (errorTimes >= APP_ERROR_TIMES) {
            initPwdLock(map, SessionManager.getDate().getNow());
            this.setSecPwdFreezeTime(user);
            freezeAccountBalance();
        }
    }

    /**
     * 更新冻结开始,结束,错误次数
     *
     * @param times
     * @param startTime
     * @param endTime
     */
    private void updateSysUserErrorTimes(int times, Date startTime, Date endTime) {
        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = SessionManagerCommon.getUser();
        sysUser.setFreezeStartTime(startTime);
        sysUser.setLoginErrorTimes(times);
        sysUser.setFreezeEndTime(endTime);
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_FREEZE_START_TIME, SysUser.PROP_FREEZE_END_TIME, SysUser.PROP_LOGIN_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(sysUserVo);
        SessionManagerCommon.refreshUser();
    }

    //错误次数,错误次数达到直接踢出
    private int getErrorTimes() {
        SysUser curUser = SessionManagerCommon.getUser();
        if (curUser != null) {
            return curUser.getLoginErrorTimes() == null ? -1 : curUser.getLoginErrorTimes();
        }
        //判断不出来默认需要权限
        return 1;
    }

    /**
     * 存储新密码
     */
    private boolean savePassword(@RequestParam("pwd") String password) {
        SysUserVo vo = new SysUserVo();
        SysUser user = SessionManager.getUser();
        user.setPermissionPwd(AuthTool.md5SysUserPermission(password, user.getUsername()));
        user.setSecpwdErrorTimes(0);
        user.setSecpwdFreezeEndTime(new Date());

        vo.setResult(user);
        vo.setProperties(SysUser.PROP_PERMISSION_PWD, SysUser.PROP_SECPWD_ERROR_TIMES, SysUser.PROP_SECPWD_FREEZE_END_TIME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        if (SessionManager.isCurrentSiteMaster()) {
            vo._setDataSourceId(SessionManager.getSiteParentId());
            vo.getResult().setId(SessionManager.getSiteUserId());
            vo = ServiceTool.sysUserService().updateOnly(vo);
        }

        // 改变session中user的权限密码
        if (vo.isSuccess()) {
            SessionManager.setUser(user);

            Map<String, Object> map = new HashMap<>();
            securityPasswordCorrect(map, user);
            SessionManager.setPrivilegeStatus(map);
        }
        return vo.isSuccess();
    }

    /**
     * 安全密码正确
     */
    private void securityPasswordCorrect(Map<String, Object> map, SysUser user) {
        map.put(KEY_STATE_KEY, PrivilegeStatusEnum.CODE_100.getCode());
        map.put(KEY_TIMES_KEY, APP_ERROR_TIMES);
        map.put(KEY_FORCE_START, SessionManager.getDate().getNow().getTime());
        map.put(KEY_CAPTCHA, false);

        user.setSecpwdErrorTimes(0);
        updateErrorTimes(user);
        resetBalanceFreeze(user);
    }

    private void resetBalanceFreeze(SysUser sysUser) {
        if (sysUser == null || sysUser.getId() == null) {
            return;
        }
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(sysUser.getId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        UserPlayer player = userPlayerVo.getResult();
        if (player != null) {
            Date now = DateQuickPicker.getInstance().getNow();
            //自冻冻结且还在冻结区间才解冻
            if (FreezeType.AUTO.getCode().equals(player.getBalanceType()) && player.getBalanceFreezeEndTime() != null
                    && now.before(player.getBalanceFreezeEndTime())) {
                player.setBalanceFreezeEndTime(new Date());
                userPlayerVo.setResult(player);
                userPlayerVo.setProperties(UserPlayer.PROP_BALANCE_FREEZE_END_TIME);
                ServiceSiteTool.userPlayerService().updateOnly(userPlayerVo);
            }
        }
    }

    private boolean setRealName(String realName) {
        SysUser user = SessionManager.getUser();
        user.setRealName(realName);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_REAL_NAME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
        return vo.isSuccess();
    }

    //玩家冻结账户余额
    private void freezeAccountBalance() {
        AccountVo accountVo = new AccountVo();
        SysUser user = SessionManagerCommon.getUser();
        accountVo.setResult(user);
        accountVo.setChooseFreezeTime(FreezeTime.THREE.getCode());
        UserPlayer userPlayer = ServiceSiteTool.userPlayerService().freezeAccountBalance(accountVo);
        sendNotice(user, userPlayer);
    }

    private void sendNotice(SysUser user, UserPlayer userPlayer) {
        try {
            Locale locale = LocaleTool.getLocale(user.getDefaultLocale());
            TimeZone timeZone = TimeZone.getTimeZone(user.getDefaultTimezone());
            NoticeVo noticeVo = NoticeVo.autoNotify(AutoNoticeEvent.BALANCE_AUTO_FREEZON, user.getId());
            noticeVo.addParams(
                    new Pair(NoticeParamEnum.UN_FREEZE_TIME.getCode(),
                            DateTool.formatDate(userPlayer.getBalanceFreezeEndTime(),
                                    locale, timeZone, CommonContext.getDateFormat().getDAY_SECOND())),
                    new Pair(NoticeParamEnum.USER.getCode(), user.getUsername()));
            ServiceTool.noticeService().publish(noticeVo);
            LOG.debug("余额自动冻结发送站内信成功");
        } catch (Exception ex) {
            LOG.error(ex, "安全码输入错误次数超过5次，余额自动冻结时发送站内信失败");
        }

    }

    /**
     * 设定安全密码冻结时间
     */
    private void setSecPwdFreezeTime(SysUser user) {
        Date date = SessionManager.getDate().getNow();
        user.setSecpwdFreezeStartTime(date);
        user.setSecpwdFreezeEndTime(DateTool.addHours(date, 3));
        user.setSecpwdErrorTimes(5);
        SessionManager.setUser(user);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_SECPWD_FREEZE_START_TIME, SysUser.PROP_SECPWD_FREEZE_END_TIME, SysUser.PROP_SECPWD_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(vo);
    }

    private void updateErrorTimes(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setProperties(SysUser.PROP_SECPWD_ERROR_TIMES);
        vo.setResult(user);
        ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
    }

    /**
     * 密码锁定时的提示内容
     */
    private void initPwdLock(Map<String, Object> map, Date date) {
        map.put(KEY_STATE_KEY, PrivilegeStatusEnum.CODE_99.getCode());
        map.put(KEY_TIMES_KEY, 0);
        map.put(KEY_FORCE_START, formatLockTime(date));
        map.put(CUSTOMER_SERVICE_KEY, SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
    }

    /**
     * 验证原密码
     */
    private boolean verifyOriginPwd(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if (StringTool.isBlank(user.getPermissionPwd())) {
            return true;
        }
        return StringTool.equals(AuthTool.md5SysUserPermission(password.getOriginPwd(), user.getUsername()), user.getPermissionPwd());
    }

    /**
     * 验证真实姓名
     */
    private boolean verifyRealName(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if (StringTool.isBlank(user.getRealName())) {
            return true;
        }
        return StringTool.equals(password.getRealName(), user.getRealName());
    }

    /**
     * 验证验证码
     */
    private boolean verifyCode(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        Integer errorTimes = user.getSecpwdErrorTimes();
        errorTimes = errorTimes == null ? 0 : errorTimes;
        if (errorTimes > 1) {
            String sysCode = (String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_SECURITY_PASSWORD.getSuffix());
            return !StringTool.equalsIgnoreCase(password.getCode(), sysCode);
        }
        return false;
    }

    private String formatLockTime(Date date) {
        return LocaleDateTool.formatDate(date, DateTool.yyyy_MM_dd_HH_mm_ss, SessionManager.getTimeZone());
    }

    /**
     * 当前用户是否被冻结
     *
     * @return
     */
    private boolean isLock() {
        SysUser curUser = SessionManagerCommon.getUser();
        Date now = DateQuickPicker.getInstance().getNow();
        if (curUser != null) {
            if (curUser.getFreezeEndTime() == null) {
                return false;
            }
            if (now.before(curUser.getFreezeEndTime())) {
                return true;
            }
        }
        return false;
    }

}

