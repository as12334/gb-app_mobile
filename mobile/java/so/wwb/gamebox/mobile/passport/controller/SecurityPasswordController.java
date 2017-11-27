package so.wwb.gamebox.mobile.passport.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.AccountVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import java.util.*;

import static so.wwb.gamebox.web.privilege.controller.PrivilegeController.TRY_TIMES;

/**
 * 安全密码操作
 * Created by fei on 17-1-7.
 */
@Controller
@RequestMapping("/passport/securityPassword")
public class SecurityPasswordController {
    private static final String SET_PASSWORD_URL = "/passport/password/SetSecurityPassword";
    private static final String PASSWORD_LOCKED_URL = "/passport/password/SecurityPasswordLocked";
    private static final String UPDATE_PASSWORD_URL = "/passport/password/UpdateSecurityPassword";
    private static final Log LOG = LogFactory.getLog(SecurityPasswordController.class);
    /** 安全密码最大错误次数 */
    private static final int SECURITY_PWD_MAX_ERROR_TIMES = TRY_TIMES;
    /** 安全密码状态 @memo PrivilegeStatusEnum */
    private static final String KEY_STATE = "state";
    /** 安全密码错误剩余次数 */
    private static final String KEY_TIMES= "times";
    /** 安全密码生效开始时间 */
    private static final String KEY_FORCE_START = "force";
    /** 需要安全密码验证码 */
    private static final String KEY_CAPTCHA = "captcha";
    /** 客服 */
    private static final String CUSTOMER_SERVICE = "customer";

    /**
     * 跳转设置或修改安全密码页面
     */
    @RequestMapping("/edit")
    public String gotoSetOrUpdatePassword(Model model) {
        SysUser user = SessionManager.getUser();

        model.addAttribute("hasName", StringTool.isNotBlank(user.getRealName()));
        // 设置安全密码
        if (StringTool.isBlank(user.getPermissionPwd())) {
            return SET_PASSWORD_URL;
        } else {
            return gotoUpdatePassword(user, model);
        }
    }

    /**
     * 修改安全密码
     */
    private String gotoUpdatePassword(SysUser user, Model model) {
        //如果冻结
        if (isLock(user)) {
            model.addAttribute("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
            model.addAttribute("lockTime", formatLockTime(user.getSecpwdFreezeStartTime()));
            return PASSWORD_LOCKED_URL;
        } else {
            //判断是否出现验证码,大于2显示验证码
            Integer errorTimes = user.getSecpwdErrorTimes();
            errorTimes = errorTimes == null ? 0 : errorTimes;
            model.addAttribute("isOpenCaptcha", errorTimes > 1);
            model.addAttribute("remindTimes", SECURITY_PWD_MAX_ERROR_TIMES - errorTimes);
            return UPDATE_PASSWORD_URL;
        }
    }

    /**
     * 设置真实姓名
     */
    @RequestMapping("/setRealName")
    @ResponseBody
    public boolean setRealName(String realName) {
        SysUser user = SessionManager.getUser();
        user.setRealName(realName);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_REAL_NAME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);

        return StringTool.isNotBlank(vo.getResult().getRealName());
    }

    @RequestMapping("/updatePassword")
    @ResponseBody
    public Map<String, Object> updatePassword(SecurityPassword password) {
        Map<String, Object> map = new HashMap<>();

        SysUser user = SessionManager.getUser();
        Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();

        if (verifyCode(password)) {
            map.put(KEY_STATE, PrivilegeStatusEnum.CODE_97.getCode());
            return map;
        }

        if (!verifyRealName(password)) {
            map.put(KEY_STATE, PrivilegeStatusEnum.CODE_94.getCode());
            return map;
        }

        if (!verifyOriginPwd(password)) {
            map.put(KEY_STATE, PrivilegeStatusEnum.CODE_98.getCode());
            setErrorTimes(map, user, errorTimes);
            return map;
        }

        boolean isSuccess = savePassword(password.getPwd1());
        if (isSuccess) {
            map.put(KEY_STATE, PrivilegeStatusEnum.CODE_100.getCode());
            SessionManager.clearPrivilegeStatus();
        }

        return map;
    }

    private void setErrorTimes(Map<String, Object> map, SysUser user, Integer errorTimes) {
        errorTimes += 1;
        user.setSecpwdErrorTimes(errorTimes);
        if (errorTimes == 1) {
            this.updateErrorTimes(user);
        } else if (errorTimes > 1 && errorTimes < 5) {
            map.put(KEY_CAPTCHA, true);
            map.put(KEY_TIMES, SECURITY_PWD_MAX_ERROR_TIMES - errorTimes);
            this.updateErrorTimes(user);
        } else if (errorTimes >= SECURITY_PWD_MAX_ERROR_TIMES) {
            initPwdLock(map, SessionManager.getDate().getNow());
            this.setSecPwdFreezeTime(user);
            freezeAccountBalance();

        }
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

    /**
     * 验证真实姓名
     */
    private boolean verifyRealName(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        return StringTool.equals(password.getRealName(), user.getRealName());
    }

    /**
     * 验证原密码
     */
    private boolean verifyOriginPwd(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        return StringTool.equals(AuthTool.md5SysUserPermission(password.getOriginPwd(), user.getUsername()), user.getPermissionPwd());
    }

    /**
     * 检测安全密码状态
     */
    @RequestMapping("/checkSecurityPassword")
    @ResponseBody
    public Map checkSecurityPassword() {
        Map<String, Object> map = initSecurityPwdMap();
        SysUser user = SessionManager.getUser();

        if (!hasSetSecurityPassword(user)) {
            map.put(KEY_STATE, PrivilegeStatusEnum.CODE_96.getCode());
        } else if (isLock(user)) {
            initPwdLock(map, user.getSecpwdFreezeStartTime());
        } else {
            Long forceStartTime = (Long) map.get(KEY_FORCE_START);
            Long currentTime = SessionManager.getDate().getNow().getTime();
            if (forceStartTime != null && currentTime - forceStartTime > forceTime()) {
                map.put(KEY_STATE, PrivilegeStatusEnum.CODE_95.getCode());
            }
        }

        return map;
    }

    /**
     * 密码锁定时的提示内容
     */
    private void initPwdLock(Map<String, Object> map, Date date) {
        map.put(KEY_STATE, PrivilegeStatusEnum.CODE_99.getCode());
        map.put(KEY_TIMES, 0);
        map.put(KEY_FORCE_START, formatLockTime(date));
        map.put(CUSTOMER_SERVICE, SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
    }

    private String formatLockTime(Date date) {
//        String timezone = SessionManager.getTimeZone().getID();
        return LocaleDateTool.formatDate(date, DateTool.yyyy_MM_dd_HH_mm_ss, SessionManager.getTimeZone());
    }

    /**
     * 是否已设置安全密码
     */
    private boolean hasSetSecurityPassword(SysUser user) {
        return StringTool.isNotBlank(user.getPermissionPwd());
    }

    /**
     * 验证安全密码
     */
    @RequestMapping("/verifySecurityPassword")
    @ResponseBody
    public Map<String, Object> verifySecurityPassword(@RequestParam("pwd") String password, @RequestParam("code") String code) {
        SysUser user = SessionManager.getUser();
        Map<String, Object> map = initSecurityPwdMap();
        Boolean captcha = (Boolean) map.get(KEY_CAPTCHA);
        if (captcha == null ? false : captcha) {
            String sysCode = (String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_PRIVILEGE.getSuffix());
            if (!code.equalsIgnoreCase(sysCode)) {
                map.put(KEY_STATE, PrivilegeStatusEnum.CODE_97.getCode());
                return map;
            }
        }

        // 安全密码已锁定
        if (isLock(user)) return map;

        Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();
        if (errorTimes > 0) {
            map.put(KEY_CAPTCHA, true);
        }

        // 错误次数未达上限
        if (errorTimes < SECURITY_PWD_MAX_ERROR_TIMES - 1) {
            boolean result = isSecurityPwdCorrect(password);
            // 密码验证正确
            if (result) {
                securityPasswordCorrect(map, user);
            } else {
                map.put(KEY_STATE, PrivilegeStatusEnum.CODE_98.getCode());
                map.put(KEY_TIMES, (SECURITY_PWD_MAX_ERROR_TIMES - 1) - errorTimes);
                user.setSecpwdErrorTimes(user.getSecpwdErrorTimes() == null ? 1 : (user.getSecpwdErrorTimes() + 1));
                updateErrorTimes(user);
            }
        } else {    // 错误次数达上限，密码锁定
            initPwdLock(map, SessionManager.getDate().getNow());
            setSecPwdFreezeTime(user);
            freezeAccountBalance();
        }

        SessionManager.setPrivilegeStatus(map);

        return map;
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
     * 安全密码有效时间
     */
    private long forceTime() {
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.SETTING_PRIVILAGE_PASS_TIME);
        if (sysParam != null) {
            long time;
            String paramValue = sysParam.getParamValue();
            if (StringTool.isNotBlank(paramValue)) {
                time = Long.valueOf(String.valueOf(paramValue));
            } else {
                time = Long.valueOf(sysParam.getDefaultValue());
            }
            return time * 60 * 1000;
        } else {
            return 5 * 60 * 1000;
        }
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

    private void updateErrorTimes(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_SECPWD_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
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
     * 检测密码强度
     */
    @RequestMapping("/checkPwdStrength")
    @ResponseBody
    public boolean checkPwdStrength(@RequestParam("pwd") String password) {
        return !PasswordRule.isWeak(password);
    }

    @RequestMapping("/saveSecurityPassword")
    @ResponseBody
    public boolean saveSecurityPassword(@RequestParam("pwd") String password) {
        return savePassword(password);
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
        map.put(KEY_STATE, PrivilegeStatusEnum.CODE_100.getCode());
        map.put(KEY_TIMES, SECURITY_PWD_MAX_ERROR_TIMES);
        map.put(KEY_FORCE_START, SessionManager.getDate().getNow().getTime());
        map.put(KEY_CAPTCHA, false);

        user.setSecpwdErrorTimes(0);
        updateErrorTimes(user);
        resetBalanceFreeze(user);
    }
    //玩家冻结账户余额
    private void freezeAccountBalance() {
        AccountVo accountVo = new AccountVo();
        SysUser user = SessionManagerCommon.getUser();
        accountVo.setResult(user);
        accountVo.setChooseFreezeTime(FreezeTime.THREE.getCode());
        UserPlayer userPlayer = ServiceTool.userPlayerService().freezeAccountBalance(accountVo);
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
    private void resetBalanceFreeze(SysUser sysUser) {
        if (sysUser == null || sysUser.getId() == null) {
            return;
        }
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(sysUser.getId());
        userPlayerVo = ServiceTool.userPlayerService().get(userPlayerVo);
        UserPlayer player = userPlayerVo.getResult();
        if (player != null) {
            Date now = DateQuickPicker.getInstance().getNow();
            //自冻冻结且还在冻结区间才解冻
            if (FreezeType.AUTO.getCode().equals(player.getBalanceType()) && player.getBalanceFreezeEndTime() != null
                    && now.before(player.getBalanceFreezeEndTime())) {
                player.setBalanceFreezeEndTime(new Date());
                userPlayerVo.setResult(player);
                userPlayerVo.setProperties(UserPlayer.PROP_BALANCE_FREEZE_END_TIME);
                ServiceTool.userPlayerService().updateOnly(userPlayerVo);
            }
        }
    }

}