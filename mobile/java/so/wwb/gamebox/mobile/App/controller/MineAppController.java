package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.App.common.CommonApp;
import so.wwb.gamebox.mobile.App.constant.AppConstant;
import so.wwb.gamebox.mobile.App.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.App.enums.AppMineLinkEnum;
import so.wwb.gamebox.mobile.App.model.*;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerGameOrder;
import so.wwb.gamebox.model.master.player.po.UserBankcard;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;
import org.soul.commons.bean.Pair;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.App.constant.AppConstant.*;

/**
 * Created by ed on 17-12-31.
 */
@Controller
@RequestMapping("/mineOrigin")
public class MineAppController extends BaseMineController {
    private Log LOG = LogFactory.getLog(MineAppController.class);

    @RequestMapping("/getLink")
    @ResponseBody
    public String getLink(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (LoginReady(vo)) return JsonTool.toJson(vo);

        Map<String, Object> map = MapTool.newHashMap();
        Map<String, Object> userInfoMap = MapTool.newHashMap();
        map.put("isBit", ParamTool.isBit());
        map.put("isCash", ParamTool.isCash());
        map.put("link", setLink());
        getMineLinkInfo(userInfoMap, request);
        map.put("user", userInfoMap);
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    @RequestMapping("/getWithDraw")
    @ResponseBody
    public String getWithDraw() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (LoginReady(vo)) return JsonTool.toJson(vo);

        //是否已存在取款订单
        if (hasOrder()) {
            vo.setCode(AppErrorCodeEnum.hasOrder.getCode());
            vo.setMsg(AppErrorCodeEnum.hasOrder.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //是否被冻结
        if (hasFreeze()) {
            vo.setCode(AppErrorCodeEnum.hasFreeze.getCode());
            vo.setMsg(AppErrorCodeEnum.hasFreeze.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //今日取款是否达到上限
        if (isFull()) {
            vo.setCode(AppErrorCodeEnum.IsFull.getCode());
            vo.setMsg(AppErrorCodeEnum.IsFull.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //余额是否充足
        Map<String, Object> map = MapTool.newHashMap();
        if (isBalanceAdequate(map)) {
            vo.setCode(AppErrorCodeEnum.IsBalanceAdequate.getCode());
            vo.setMsg(AppErrorCodeEnum.IsBalanceAdequate.getMsg().replace(targetRegex, map.get("withdrawMinNum").toString()));
            vo.setError(1);
            return JsonTool.toJson(vo);
        }

        withdraw(map);
        vo.setCode(AppErrorCodeEnum.Success.getCode());
        vo.setMsg(AppErrorCodeEnum.Success.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    @RequestMapping("/getMyPromo")
    @ResponseBody
    public String getMyPromo() {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();

        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setUserId(SessionManager.getUserId());
        vPreferentialRecodeListVo.getSearch().setCurrentDate(SessionManager.getDate().getNow());

        vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
        vo = CommonApp.buildAppModelVo(vPreferentialRecodeListVo);
        return JsonTool.toJson(vo);

    }

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);

        UserInfoApp userApp = new UserInfoApp();
        SysUser user = SessionManager.getUser();

        getAppUserInfo(request, user, userApp);

        vo = CommonApp.buildAppModelVo(userApp);

        return JsonTool.toJson(vo);
    }


    @RequestMapping("/refresh")
    @ResponseBody
    public String refresh(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);
        Integer userId = SessionManager.getUserId();
        PlayerApiListVo listVo = initPlayerApiListVo(userId);
        VUserPlayer player = getVPlayer(userId);

        UserInfoApp infoApp = new UserInfoApp();
        infoApp.setApis(getSiteApis(listVo, request, true));
        infoApp.setCurrSign(player.getCurrencySign());
        infoApp.setAssets(queryPlayerAssets(listVo, userId));
        infoApp.setUsername(player.getUsername());

        vo = CommonApp.buildAppModelVo(infoApp);
        return JsonTool.toJson(vo);
    }

    @RequestMapping("/addCard")
    @ResponseBody
    public String addCard() {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);

        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BANK);
        AppModelVo appModelVo = new AppModelVo();
        if (userBankcard == null) {
            //获取银行列表
            appModelVo.setCode(AppErrorCodeEnum.addCard.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.addCard.getMsg());
            appModelVo.setData(bankList());
        }else {
            appModelVo.setCode(AppErrorCodeEnum.showBankCardInfomation.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.showBankCardInfomation.getMsg());
            appModelVo.setData(userBankcard);
        }
        return JsonTool.toJson(appModelVo);
    }

    @RequestMapping("/addBtc")
    @ResponseBody
    public String addBtc() {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);

        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BTC);
        AppModelVo appModelVo = new AppModelVo();

        if (userBankcard == null) {
            vo.setCode(AppErrorCodeEnum.addBtc.getCode());
            vo.setMsg(AppErrorCodeEnum.addBtc.getMsg());
        }else {
            vo = CommonApp.buildAppModelVo(userBankcard);
            vo.setMsg("展示比特币信息");

        }

        return JsonTool.toJson(vo);
    }

    @RequestMapping("/submitBtc")
    @ResponseBody
    public String submitBtc(String bankcardNumber) {
        /*比特币*/
        final String BITCOIN = "bitcoin";
        UserBankcardVo bankcardVo = new UserBankcardVo();
        bankcardVo.setResult(new UserBankcard()); //暂时写死，为了测试接口是否成功
        bankcardVo.getResult().setBankcardNumber("abcdefghiklmnopqrstuvwxyz");
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(AppConstant.appVersion);
        if (checkCardIsExistsByUserId(bankcardVo)) {
            AppErrorCodeEnum.hasBtc.getCode();
            appModelVo.setCode(AppErrorCodeEnum.hasBtc.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.hasBtc.getMsg());
            return JsonTool.toJson(appModelVo);
        }

        UserBankcard bankcard = bankcardVo.getResult();
        bankcard.setUserId(getAgentId());
        bankcard.setType(UserBankcardTypeEnum.BITCOIN.getCode());
        bankcard.setBankName(BITCOIN);
        bankcardVo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(bankcardVo);
        if (!bankcardVo.isSuccess()) {
            appModelVo.setCode(AppErrorCodeEnum.submitBtcfild.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.submitBtcfild.getMsg());
            return JsonTool.toJson(appModelVo);
        }
        appModelVo.setCode(AppErrorCodeEnum.bindingSuccess.getCode());
        appModelVo.setMsg(AppErrorCodeEnum.bindingSuccess.getMsg());

        return JsonTool.toJson(appModelVo);
    }



    @RequestMapping("/getFundRecord")
    @ResponseBody
    public String getFundRecord() {
        AppModelVo vo = new AppModelVo();
        if (LoginReady(vo)) return JsonTool.toJson(vo);

        VPlayerTransactionListVo listVo = new VPlayerTransactionListVo();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        if (listVo.getSearch().getEndCreateTime() != null) {
            listVo.getSearch().setEndCreateTime(DateTool.addDays(listVo.getSearch().getEndCreateTime(), 1));
        }
        Map map = MapTool.newHashMap();
        getFund(map);
        listVo.getSearch().setNoDisplay(TransactionWayEnum.MANUAL_PAYOUT.getCode());
        listVo.getSearch().setLotterySite(ParamTool.isLotterySite());
        listVo = ServiceSiteTool.vPlayerTransactionService().search(listVo);
        vo = CommonApp.buildAppModelVo(listVo);
        return JsonTool.toJson(vo);
    }

    @RequestMapping("/getFundRecordDetails")
    @ResponseBody
    public String getFundRecordDetails(String searchId) {
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(appVersion);
        if (LoginReady(appModelVo)) return JsonTool.toJson(appModelVo);
        searchId = String.valueOf(5002473);     //测试数据，暂时写死
        if (StringTool.isNotBlank(searchId)) {
            VPlayerTransactionVo vo = new VPlayerTransactionVo();
            vo.getSearch().setId(Integer.valueOf(searchId));
            vo = ServiceSiteTool.vPlayerTransactionService().get(vo);

            VPlayerTransaction po = vo.getResult();

            RecordDetailApp recordDetailApp = new RecordDetailApp();
            recordDetailApp.setId(po.getId());
            recordDetailApp.setTransactionNo(po.getTransactionNo());
            recordDetailApp.setCreateTime(po.getCreateTime());
            recordDetailApp.setTransactionType(po.getTransactionType());
            recordDetailApp.setTransactionMoney(po.getTransactionMoney());
            recordDetailApp.setStatus(po.getStatus());
            recordDetailApp.setFailureReason(po.getFailureReason());
            recordDetailApp.setAdministrativeFee(po.getAdministrativeFee());
            recordDetailApp.setDeductFavorable(po.getDeductFavorable());
            recordDetailApp.setFundType(po.getFundType());
            recordDetailApp.setTransactionWay(po.getTransactionWay());
            recordDetailApp.setUsername(po.getUsername());
            recordDetailApp.setPayerBankcard(po.getPayerBankcard());
            recordDetailApp.setRechargeTotalAmount(po.getRechargeTotalAmount());
            recordDetailApp.setRechargeAmount(po.getRechargeAmount());
            recordDetailApp.setRechargeAddress(po.getRechargeAddress());
            appModelVo = CommonApp.buildAppModelVo(recordDetailApp);
        }

        return JsonTool.toJson(appModelVo);
    }

    @RequestMapping("/getBettingList")
    @ResponseBody
    public String getBettingList(PlayerGameOrderListVo listVo) {

        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);
        if (LoginReady(vo)) return JsonTool.toJson(vo);

        BettingDataApp bettingDataApp = new BettingDataApp();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        if (listVo.getSearch().getEndBetTime() != null) {
            listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1),-1));
        }

        initQueryDateForgetBetting(listVo,TIME_INTERVAL,DEFAULT_TIME);
        listVo = ServiceSiteTool.playerGameOrderService().search(listVo);
        List<PlayerGameOrder> gameOrderList = listVo.getResult();

        bettingDataApp.setTotalSize(listVo.getPaging().getTotalCount());
        bettingDataApp.setStatisticsData(statisticsData(listVo, TIME_INTERVAL, DEFAULT_TIME));
        bettingDataApp.setList(buildBetting(gameOrderList));

        //设置默认时间
        bettingDataApp.setMinDate(SessionManager.getDate().addDays(TIME_INTERVAL));
        bettingDataApp.setMaxDate(SessionManager.getDate().getNow());

        vo = CommonApp.buildAppModelVo(bettingDataApp);
        return JsonTool.toJson(vo);
    }

    private boolean LoginReady(AppModelVo vo) {
        if (!isLoginUser()) {
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            vo.setError(1);
            return true;
        }
        return false;
    }

    @RequestMapping("/getBettingDetails")
    @ResponseBody
    public String getBettingDetails(Integer id) {
        PlayerGameOrderVo vo = new PlayerGameOrderVo();
        id = 10111337;//暂时写死，为了验证json
        vo.getSearch().setId(id);
        vo = ServiceSiteTool.playerGameOrderService().getGameOrderDetail(vo);
//        如果不是这个玩家投注的订单，则无视该笔订单
        if (vo.getResult() == null || vo.getResult().getPlayerId() != SessionManager.getUserId().intValue()) {
            vo.setResult(null);
            vo.setResultArray(null);
        }
        AppModelVo appModelVo = CommonApp.buildAppModelVo(buildBettingDetail(vo));
        appModelVo.setVersion(appVersion);

        return JsonTool.toJson(appModelVo);
    }

    @RequestMapping("/initSafePassword")
    @ResponseBody
    public String initSafePassword() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (!isLoginUser()) {
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setError(1);
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            return JsonTool.toJson(vo);
        }

        SysUser user = SessionManager.getUser();
        Map<String, Object> map = MapTool.newHashMap();
        map.put("hasRealName", StringTool.isNotBlank(user.getRealName()));
        if (StringTool.isBlank(user.getPermissionPwd())) {
            map.put("hasPermissionPwd", false);
            vo.setData(map);
            return JsonTool.toJson(vo);
        }

        map.put("hasPermissionPwd", true);
        if (isLock(user)) {//如果冻结
            map.put("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
            map.put("lockTime", formatLockTime(user.getSecpwdFreezeStartTime()));
            vo.setData(map);
            return JsonTool.toJson(vo);
        }

        //判断是否出现验证码,大于2显示验证码
        Integer errorTimes = user.getSecpwdErrorTimes();
        errorTimes = errorTimes == null ? 0 : errorTimes;
        map.put("isOpenCaptcha", errorTimes > 1);
        map.put("remindTimes", appErrorTimes - errorTimes);
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    @RequestMapping("/setRealName")
    @ResponseBody
    public String setRealNameApp(String realName){
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);
        if(StringTool.isBlank(realName)){
            vo.setCode(AppErrorCodeEnum.realName.getCode());
            vo.setMsg(AppErrorCodeEnum.realName.getMsg());
            return JsonTool.toJson(vo);
        }

        if(!setRealName(realName)){
            vo.setCode(AppErrorCodeEnum.realNameSetError.getCode());
            vo.setMsg(AppErrorCodeEnum.realNameSetError.getMsg());
            return JsonTool.toJson(vo);
        }
        vo.setCode(AppErrorCodeEnum.Success.getCode());
        vo.setMsg(AppErrorCodeEnum.Success.getMsg());
        return JsonTool.toJson(vo);
    }

    @RequestMapping("/updateSafePassword")
    @ResponseBody
    public String updateSafePassword(SecurityPassword password){
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);
        if(LoginReady(vo)){
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            return JsonTool.toJson(vo);
        }
        if(StringTool.isBlank(password.getRealName())){
            vo.setCode(AppErrorCodeEnum.realName.getCode());
            vo.setMsg(AppErrorCodeEnum.realName.getMsg());
            return JsonTool.toJson(vo);
        }
        if(StringTool.isBlank(password.getPwd1())){
            vo.setCode(AppErrorCodeEnum.safePwdNotNull.getCode());
            vo.setMsg(AppErrorCodeEnum.safePwdNotNull.getMsg());
            return JsonTool.toJson(vo);
        }
        if (verifyCode(password)) {
            vo.setCode(AppErrorCodeEnum.sysCode.getCode());
            vo.setMsg(AppErrorCodeEnum.sysCode.getMsg());
            return JsonTool.toJson(vo);
        }
        if (!verifyRealName(password)) {
            vo.setCode(AppErrorCodeEnum.realNameError.getCode());
            vo.setMsg(AppErrorCodeEnum.realNameError.getMsg());
            return JsonTool.toJson(vo);
        }
        if (!verifyOriginPwd(password)) {
            Map<String, Object> map = MapTool.newHashMap();
            SysUser user = SessionManager.getUser();
            Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();
            setErrorTimes(map, user, errorTimes);
            vo.setCode(AppErrorCodeEnum.originSafePwd.getCode());
            vo.setMsg(AppErrorCodeEnum.originSafePwd.getMsg());
            return JsonTool.toJson(vo);
        }
        if(!setRealName(password.getRealName())){
            vo.setCode(AppErrorCodeEnum.realNameSetError.getCode());
            vo.setMsg(AppErrorCodeEnum.realNameSetError.getMsg());
            return JsonTool.toJson(vo);
        }
        boolean isSuccess = savePassword(password.getPwd1());
        if(isSuccess){
            SessionManager.clearPrivilegeStatus();
        }
        vo.setCode(AppErrorCodeEnum.Success.getCode());
        vo.setMsg(AppErrorCodeEnum.Success.getMsg());

        return JsonTool.toJson(vo);
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
        map.put(keyState, PrivilegeStatusEnum.CODE_100.getCode());
        map.put(keyTimes, appErrorTimes);
        map.put(keyForceStart, SessionManager.getDate().getNow().getTime());
        map.put(keyCaptcha, false);

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

    private boolean setRealName(String realName){
        SysUser user = SessionManager.getUser();
        user.setRealName(realName);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_REAL_NAME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
        return vo.isSuccess();
    }

    private void setErrorTimes(Map<String, Object> map, SysUser user, Integer errorTimes) {
        errorTimes += 1;
        user.setSecpwdErrorTimes(errorTimes);
        if (errorTimes == 1) {
            this.updateErrorTimes(user);
        } else if (errorTimes > 1 && errorTimes < 5) {
            map.put(keyCaptcha, true);
            map.put(keyTimes, appErrorTimes - errorTimes);
            this.updateErrorTimes(user);
        } else if (errorTimes >= appErrorTimes) {
            initPwdLock(map, SessionManager.getDate().getNow());
            this.setSecPwdFreezeTime(user);
            freezeAccountBalance();
        }
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
        map.put(keyState, PrivilegeStatusEnum.CODE_99.getCode());
        map.put(keyTimes, 0);
        map.put(keyForceStart, formatLockTime(date));
        map.put(customerService, SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
    }

    /**
     * 验证原密码
     */
    private boolean verifyOriginPwd(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if(StringTool.isBlank(user.getPermissionPwd())){
            return true;
        }
        return StringTool.equals(AuthTool.md5SysUserPermission(password.getOriginPwd(), user.getUsername()), user.getPermissionPwd());
    }
    /**
     * 验证真实姓名
     */
    private boolean verifyRealName(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if(StringTool.isBlank(user.getRealName())){
            return true;
        }
        return StringTool.equals(password.getRealName(), user.getRealName());
    }

    /**
     * 验证验证码
     */
    private boolean verifyCode(SecurityPassword password) {
        if (password.isNeedCaptcha()) {
            String sysCode = (String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_SECURITY_PASSWORD.getSuffix());
            return !password.getCode().equalsIgnoreCase(sysCode);
        }
        return false;
    }

    private String formatLockTime(Date date) {
        return LocaleDateTool.formatDate(date, DateTool.yyyy_MM_dd_HH_mm_ss, SessionManager.getTimeZone());
    }

    /**
     * 是否锁定
     */
    private boolean isLock(SysUser user) {
        Date now = SessionManager.getDate().getNow();
        if (user != null) {
            if (user.getSecpwdFreezeEndTime() == null) {
                return false;
            }
            if (now.before(user.getSecpwdFreezeEndTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置我的链接地址
     *
     * @return
     */
    private List<AppMineLinkVo> setLink() {
        List<AppMineLinkVo> links = ListTool.newArrayList();
        for (AppMineLinkEnum linkEnum : AppMineLinkEnum.values()) {
            AppMineLinkVo vo = new AppMineLinkVo();
            vo.setCode(linkEnum.getCode());
            vo.setName(linkEnum.getName());
            vo.setLink(linkEnum.getLink());
            links.add(vo);
        }

        return links;
    }


    /**
     * 是否有登陆账号
     */
    public boolean isLoginUser() {
        if (SessionManager.getUser() == null) {
            AppModelVo appVo = new AppModelVo();
            appVo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            appVo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            appVo.setError(1);
            appVo.setData(null);

            return false;
        }
        return true;
    }
}

