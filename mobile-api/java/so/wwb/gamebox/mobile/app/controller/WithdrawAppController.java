package so.wwb.gamebox.mobile.app.controller;


import org.soul.commons.bean.Pair;
import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
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
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.controller.BaseWithDrawController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.AccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerTransactionVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

@Controller
@RequestMapping("/withdrawOrigin")
public class WithdrawAppController extends BaseWithDrawController {
    private static final Log LOG = LogFactory.getLog(WithdrawAppController.class);

    /**
     * 获取取款信息
     *
     * @return
     */
    @RequestMapping(value = "/getWithDraw")
    @ResponseBody
    public AppModelVo getWithDraw(HttpServletRequest request) {
        //判断是否达到取款要求
        AppModelVo vo = new AppModelVo();
        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMessage())) {
            return vo;
        }

        Map<String, Object> map = new HashMap<>();
        withdraw(map, request);
        vo.setData(map);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMessage(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setVersion(APP_VERSION);
        return vo;
    }

    /**
     * 提交取款
     */
    @RequestMapping(value = "/submitWithdraw", method = RequestMethod.POST)
    @ResponseBody
    @Token(valid = true)
    public String submitWithdraw(HttpServletRequest request, PlayerTransactionVo playerVo, BindingResult result, SecurityPassword password, String code) {
        Map<String, Object> tokenMap = new HashMap<>(1, 1f);
        tokenMap.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        if (result.hasErrors()) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //判断用户是否存在安全码
        if (!isSafePassword()) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.NOT_SET_SAFE_PASSWORD.getCode(),
                    AppErrorCodeEnum.NOT_SET_SAFE_PASSWORD.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        if (StringTool.isBlank(password.getOriginPwd())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        if (!verifyOriginPwd(password)) {
            //验证安全码
            String verifyResult = verifySafePwdErrorTime(password.getOriginPwd(), code, tokenMap);
            if (StringTool.isNotBlank(verifyResult)) {
                return verifyResult;
            }
        }
        //是否有取款银行卡
        Map map = new HashMap<>();
        if (!hasBank(map, request)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.NO_BANK.getCode(),
                    AppErrorCodeEnum.NO_BANK.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //判断取款是否达到要求
        AppModelVo vo = new AppModelVo();
        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMessage())) {
            return AppModelVo.getAppModeVoJson(true,
                    vo.getCode(),
                    vo.getMessage(),
                    tokenMap,
                    APP_VERSION);
        }
        //是否符合取款金额设置
        if (isInvalidAmount(playerVo, map)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.WITHDRAW_BETWEEN_MIN_MAX.getCode(),
                    map.get("msg").toString(),
                    tokenMap,
                    APP_VERSION);
        }
        //取款
        map = addWithdraw(request, playerVo);
        //成功
        if (map.get("state") != null && MapTool.getBoolean(map, "state")) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    map,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.WITHDRAW_FAIL.getCode(),
                AppErrorCodeEnum.WITHDRAW_FAIL.getMsg(),
                tokenMap,
                APP_VERSION);
    }

    /**
     * 计算取款各种费用
     *
     * @param withdrawAmount 存款金额
     * @return map
     */
    @RequestMapping("/withdrawFee")
    @ResponseBody
    public String withdrawFee(String withdrawAmount) {
        if (StringTool.isBlank(withdrawAmount) || !NumberTool.isNumber(withdrawAmount)) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.WITHDRAW_AMOUNT_ERROR.getCode(),
                    AppErrorCodeEnum.WITHDRAW_AMOUNT_ERROR.getMsg(),
                    null,
                    APP_VERSION);
        }

        PlayerRank playerRank = getRank();
        if (playerRank == null) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }

        Map<String, Object> map = new HashMap<>();
        double amount = Double.valueOf(withdrawAmount);
        Integer withdrawMinNum = playerRank.getWithdrawMinNum();
        Integer withdrawMaxNum = playerRank.getWithdrawMaxNum();
        if (withdrawMinNum != null && withdrawMaxNum != null) {
            if (!(playerRank.getWithdrawMinNum() <= amount && playerRank.getWithdrawMaxNum() >= amount)) {
                String msg = LocaleTool.tranMessage(Module.FUND, "withdraw.apply.amont.renge", withdrawMinNum, withdrawMaxNum);
                map.put("msg", msg);
            }
        }
        // 手续费
        Double poundage = getPoundage(amount, playerRank);
        Map auditMap = getAuditMap();
        Double administrativeFee = MapTool.getDouble(auditMap, "administrativeFee");
        Double deductFavorable = MapTool.getDouble(auditMap, "deductFavorable");
        double result = amount - poundage - administrativeFee - deductFavorable;

        map.put("deductFavorable", deductFavorable);//扣除优惠
        map.put("actualWithdraw", result);// 实际取款金额
        map.put("administrativeFee", administrativeFee);//行政费
        map.put("counterFee", poundage);//手续费
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 验证原密码
     */
    private boolean verifyOriginPwd(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        return StringTool.equals(AuthTool.md5SysUserPermission(password.getOriginPwd(), user.getUsername()), user.getPermissionPwd());
    }

    /**
     * 验证安全密码是否达到上线
     *
     * @return
     */
    private String verifySafePwdErrorTime(String password, String code, Map tokenMap) {
        SysUser user = SessionManager.getUser();
        Map<String, Object> map = initSecurityPwdMap();
        Boolean captcha = (Boolean) map.get(KEY_CAPTCHA);
        /*if (captcha == null ? false : captcha) {
            String sysCode = (String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_PRIVILEGE.getSuffix());
            if (!code.equalsIgnoreCase(sysCode)) {
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                        AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                        tokenMap,
                        APP_VERSION);
            }
        }*/

        // 安全密码已锁定
        if (isLock(user)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_LOCK.getCode(),
                    AppErrorCodeEnum.USER_LOCK.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }

        Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();
        if (errorTimes > 0) {
            tokenMap.put(KEY_CAPTCHA, true);
        }

        // 错误次数未达上限
        if (errorTimes < APP_ERROR_TIMES - 1) {
            boolean result = isSecurityPwdCorrect(password);
            // 密码验证正确
            if (result) {
                securityPasswordCorrect(map, user);
            } else {
                user.setSecpwdErrorTimes(user.getSecpwdErrorTimes() == null ? 1 : (user.getSecpwdErrorTimes() + 1));
                updateErrorTimes(user);

                tokenMap.put(KEY_TIMES_KEY, (APP_ERROR_TIMES - 1) - errorTimes);
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.SAFE_PASSWORD_ERROR.getCode(),
                        String.format(AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR_TIME.getMsg(),(APP_ERROR_TIMES - 1) - errorTimes),
                        tokenMap,
                        APP_VERSION);
            }
        } else {    // 错误次数达上限，密码锁定
            initPwdLock(map, SessionManager.getDate().getNow());
            setSecPwdFreezeTime(user);
            freezeAccountBalance();

            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_LOCK.getCode(),
                    AppErrorCodeEnum.USER_LOCK.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }

        SessionManager.setPrivilegeStatus(map);
        return "";
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

    /**
     * 密码锁定时的提示内容
     */
    private void initPwdLock(Map<String, Object> map, Date date) {
        map.put(KEY_STATE, PrivilegeStatusEnum.CODE_99.getCode());
        map.put(KEY_TIMES_KEY, 0);
        map.put(KEY_FORCE_START, formatLockTime(date));
        map.put(CUSTOMER_SERVICE_KEY, SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
    }

    private String formatLockTime(Date date) {
//        String timezone = SessionManager.getTimeZone().getID();
        return LocaleDateTool.formatDate(date, DateTool.yyyy_MM_dd_HH_mm_ss, SessionManager.getTimeZone());
    }

    /**
     * 安全密码正确
     */
    private void securityPasswordCorrect(Map<String, Object> map, SysUser user) {
        map.put(KEY_STATE, PrivilegeStatusEnum.CODE_100.getCode());
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

    private void updateErrorTimes(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_SECPWD_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
    }

    /**
     * 查询安全密码是否正确
     */
    private boolean isSecurityPwdCorrect(@RequestParam("code") String password) {
        String privilegeCode = SessionManager.getPrivilegeCode();
        SysUser user = SessionManager.getUser();
        String inputPassword = AuthTool.md5SysUserPermission(password, user.getUsername());
        return privilegeCode.equals(inputPassword);
    }

    /**
     * 初始化安全密码验证MAP
     */
    private Map<String, Object> initSecurityPwdMap() {
        Map<String, Object> map = SessionManager.getPrivilegeStatus();
        if (map == null) {
            map = new HashMap<>();
            map.put(KEY_CAPTCHA, false);
        }
        return map;
    }

    /**
     * 取款判断
     *
     * @param vo
     * @return
     */
    private AppModelVo withDraw(AppModelVo vo) {
        //是否已存在取款订单
        if (hasOrder()) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_HAS_ORDER.getCode());
            vo.setMessage(AppErrorCodeEnum.WITHDRAW_HAS_ORDER.getMsg());
            return vo;
        }
        //是否被冻结
        if (hasFreeze()) {
            vo.setCode(AppErrorCodeEnum.USER_HAS_FREEZE.getCode());
            vo.setMessage(AppErrorCodeEnum.USER_HAS_FREEZE.getMsg());
            return vo;
        }
        //今日取款是否达到上限
        if (isFull()) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_IS_FULL.getCode());
            vo.setMessage(AppErrorCodeEnum.WITHDRAW_IS_FULL.getMsg());
            return vo;
        }
        //余额是否充足
        Map<String, Object> map = new HashMap<>();
        if (isBalanceAdequate(map)) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_MIN_AMOUNT.getCode());
            vo.setMessage(AppErrorCodeEnum.WITHDRAW_MIN_AMOUNT.getMsg().replace(TARGET_REGEX, map.get("withdrawMinNum").toString()));
            return vo;
        }

        return vo;
    }
}
