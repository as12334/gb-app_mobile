package so.wwb.gamebox.mobile.my.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.support._Module;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.common.BaseVo;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.my.form.SecurityPasswordForm;
import so.wwb.gamebox.mobile.my.form.UpdateSecurityPasswordForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.enums.UserTypeEnum;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.AccountVo;
import so.wwb.gamebox.model.master.player.vo.UpdatePasswordVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;
import so.wwb.gamebox.web.privilege.controller.PrivilegeController;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by bruce on 16-6-10.
 */
@Controller
@RequestMapping("/password/security")
public class UpdateSecurityPasswordController {
    private static final Log LOG = LogFactory.getLog(UpdateSecurityPasswordController.class);

    private static final String PERSON_INFO_SECURITY_PASSWORD = "password/SecurityPassword";
    private static final String PERSON_INFO_UPDATE_SECURITY_PASSWORD = "password/UpdateSecurityPassword";
    private static final String PERSON_INFO_SECURITY_PWD_LOCK = "password/SecurityPwdLock";

    private final static int TRY_TIMES = PrivilegeController.TRY_TIMES;

    /*
     * 跳转到设置安全密码
     *
     * @param model
     * @return
     */
    @RequestMapping("/toSecurityPassword")
    public String toSecurityPassword(Model model) {
        if (StringTool.isBlank(SessionManager.getUser().getPermissionPwd())) {
            model.addAttribute("validateRule", JsRuleCreator.create(SecurityPasswordForm.class));
            return PERSON_INFO_SECURITY_PASSWORD;
        } else {
            return toUpdateSecurityPassword(model);
        }
    }

    /**
     * 跳转到修改安全密码
     *
     * @param model
     * @return
     */
    @RequestMapping("/toUpdateSecurityPassword")
    public String toUpdateSecurityPassword(Model model) {
        //如果冻结
        if (isLock()) {
            SysUser curUser = SessionManagerCommon.getUser();
            String customer = LocaleTool.tranMessage(Module.COMPANY_SETTING, MessageI18nConst.NOTICE_PARAM_CUSTOMER);
            String url = SiteCustomerServiceHelper.getMobileCustomerServiceUrl();
            if (url != null) {
                customer = "<a href=\"" + url + "\" target=\"_blank\">" + customer + "</a>";
            }
            model.addAttribute("customer", customer);
            model.addAttribute("curUser", curUser);
            setLockTime(curUser, model);
            return PERSON_INFO_SECURITY_PWD_LOCK;
        } else {
            model.addAttribute("validateRule", JsRuleCreator.create(UpdateSecurityPasswordForm.class));
            model.addAttribute("realName", SessionManager.getUser().getRealName());
            //判断是否出现验证码,大于3显示验证码
            model.addAttribute("leftTimes", TRY_TIMES-getErrorTimes());
            return PERSON_INFO_UPDATE_SECURITY_PASSWORD;
        }
    }

    private void setLockTime(SysUser curUser, Model model) {
        final Date lastTime = curUser.getSecpwdFreezeStartTime();
        long seconds = DateTool.secondsBetween(new Date(), lastTime);
        long hour = DateTool.hoursBetween(new Date(), lastTime);
        long min = DateTool.minutesBetween(new Date(), lastTime);
        model.addAttribute("hour", (3 - hour - 1));
        model.addAttribute("min", (60 - min - 1));
        model.addAttribute("sec", (60 - (seconds - min * 60) - 1));
    }

    /**
     * 是否冻结安全密码修改
     *
     * @return
     */
    private boolean isLock() {
        SysUser curUser = SessionManagerCommon.getUser();
        Date now = DateQuickPicker.getInstance().getNow();
        if (curUser != null) {
            if (curUser.getSecpwdFreezeEndTime() == null) {
                return false;
            }
            if (now.before(curUser.getSecpwdFreezeEndTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 修改权限密码--保存修改
     *
     * @param updatePasswordVo
     * @return
     */
    @RequestMapping(value = "/updatePrivilegePassword")
    @ResponseBody
    public Map updatePrivilegePassword(UpdatePasswordVo updatePasswordVo,String code) {

        Map map = new HashMap(2,1f);

        //判断安全密码是否输入正确
        String inputCode = AuthTool.md5SysUserPermission(updatePasswordVo.getOldPrivilegePwd(), SessionManager.getUserName());
        if (!inputCode.equals(SessionManager.getPrivilegeCode())) {
            //不通过
            return inputFault();
        }

        int errTimes = getErrorTimes();
        //第二次以上重试时，判断图片验证码
        if (!isLock() && errTimes >= TRY_TIMES) {
            errTimes = 0;
        }
        if (errTimes >= 2) {
            map =  validateCheckCode(code);
            if (!map.get("stateCode").equals(PrivilegeStatusEnum.CODE_100.getCode())) {
                return map;
            }
        }

        /*Map privilegeUsers = new HashMap();
        privilegeUsers.put("state", PrivilegeStatusEnum.STATUS_OK.getCode());
        privilegeUsers.put("time", new Date());
        SessionManagerCommon.setPrivilegeStatus(privilegeUsers);*/

        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = new SysUser();
        sysUser.setId(SessionManager.getUserId());
        String newPrivilegePwd = AuthTool.md5SysUserPermission(updatePasswordVo.getPrivilegePwd(),
                SessionManager.getUserName());
        sysUser.setPermissionPwd(newPrivilegePwd);
        sysUser.setSecpwdErrorTimes(null);
        sysUser.setSecpwdFreezeEndTime(new Date());
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_PERMISSION_PWD, SysUser.PROP_SECPWD_ERROR_TIMES,
                SysUser.PROP_SECPWD_FREEZE_END_TIME);
        boolean success = ServiceTool.sysUserService().updateOnly(sysUserVo).isSuccess();
        map.put("state", success);
        if (success) {
            SessionManager.refreshUser();
            map.put("msg", LocaleTool.tranMessage("player", LocaleTool.tranMessage(Module.Passport.getCode(), "security.update.success")));
            map.put("stateCode", PrivilegeStatusEnum.CODE_100.getCode());
            SessionManager.clearPrivilegeStatus();
            resetSecPwdFreezen(sysUser);
            resetBalanceFreeze(sysUser);
        } else {
            map.put("msg", LocaleTool.tranMessage("player", LocaleTool.tranMessage(Module.Passport.getCode(), "security.update.failed")));
        }
        return map;
    }

    private void resetSecPwdFreezen(SysUser sysUser) {
        if (sysUser == null) {
            return;
        }
        Date now = DateQuickPicker.getInstance().getNow();
        //还在冻结区间才解冻
        if (sysUser.getSecpwdFreezeEndTime() != null && now.before(sysUser.getSecpwdFreezeEndTime())) {
            sysUser.setSecpwdFreezeEndTime(new Date());
            sysUser.setSecpwdErrorTimes(0);
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.setResult(sysUser);
            sysUserVo.setProperties(SysUser.PROP_SECPWD_FREEZE_END_TIME, SysUser.PROP_SECPWD_ERROR_TIMES);
            ServiceTool.sysUserService().updateOnly(sysUserVo);
        }
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

    @RequestMapping(value = "/checkPrivilegePwd")
    @ResponseBody
    public String checkPrivilegePwd(@RequestParam("privilegePwd") String password) {

        // 弱密码过滤
        if (PasswordRule.isWeak(password)) {
            return "false";
        }
        return "true";
    }

    /**
     * 修改权限密码--检查密码
     *
     * @param password
     * @return
     */
    @RequestMapping(value = "/checkPrivilegePassword")
    @ResponseBody
    public String checkPrivilegePassword(@RequestParam("oldPrivilegePwd") String password) {
        SysUser user = SessionManager.getUser();
        String inputPassword = AuthTool.md5SysUserPermission(password, user.getUsername());
        return user.getPermissionPwd().equals(inputPassword) ? "true" : "false";
    }

    /**
     * 真实姓名远程验证
     *
     * @param realName
     * @return
     */
    @RequestMapping(value = "/checkRealName")
    @ResponseBody
    public String checkRealName(@RequestParam("realName") String realName) {
        return checkName(realName);
    }

    @RequestMapping(value = "/checkRealName2")
    @ResponseBody
    public String checkRealName2(@RequestParam("realName2") String realName) {
        return checkName(realName);
    }

    private String checkName(String realName) {
        if (StringTool.isBlank(realName)) {
            return "false";
        }
        String name = SessionManager.getUser().getRealName();
        return realName.equals(name) ? "true" : "false";
    }

    /**
     * 安全码错误次数
     *
     * @return
     */
    private int getErrorTimes() {
        SysUser curUser = SessionManagerCommon.getUser();
        if (curUser != null) {
            return curUser.getSecpwdErrorTimes() == null ? -1 : curUser.getSecpwdErrorTimes();
        }
        //判断不出来默认需要权限
        return 1;
    }


    /**
     * 验证吗remote验证
     *
     * @param code
     * @return
     */
    @RequestMapping("/checkValiCode")
    @ResponseBody
    public boolean checkValiCode(@RequestParam("code") String code) {
        String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_SECURITY_PASSWORD.getSuffix());
        return StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code);
    }

    private void updateUserErrorTimes(int times, Date startTime, Date endTime) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.setTimes(times);
        userPlayerVo.setStartTime(startTime);
        userPlayerVo.setEndTime(endTime);
        SysUser user = SessionManagerCommon.getUser();

        userPlayerVo.setSysUser(user);
        SysUser sysUser = ServiceSiteTool.userPlayerService().updateUserErrorTimes(userPlayerVo);
        SessionManagerCommon.setUser(sysUser);
    }

    private HashMap inputFault() {
        HashMap map = new HashMap(2,1f);
        Date now = DateQuickPicker.getInstance().getNow();
        int errTimes = getErrorTimes();
        if (errTimes == 0) {
            map = firstInputFalut();
            updateUserErrorTimes(1, now, null);
        } else {
            if (errTimes == -1) {
                errTimes = 0;
            }
            int times = errTimes;
            times++;
            if (times >= TRY_TIMES) {
                map = inputMaxFalut();
                updateUserErrorTimes(TRY_TIMES, now, DateTool.addHours(now, 3));
                if (UserTypeEnum.PLAYER.getCode().equals(SessionManagerCommon.getUserType().getCode())) {
                    freezeAccountBalance();
                }
            } else {
                map = inputFalutNoAtMax(times);
                updateUserErrorTimes(times, null, null);
            }
        }
        return map;
    }

    private HashMap inputFalutNoAtMax(int times) {
        Map privilegeUsers = new HashMap();
        privilegeUsers.put("state", PrivilegeStatusEnum.STATUS_ERRORS.getCode());
        privilegeUsers.put("times", times);
        SessionManagerCommon.setPrivilegeStatus(privilegeUsers);
        //返回页面消息
        HashMap map = new HashMap(3,1f);
        map.put("msg", LocaleTool.tranMessage("privilege", "input.wrong"));
        map.put("stateCode", PrivilegeStatusEnum.CODE_98.getCode());
        map.put("state", false);
        map.put("leftTimes", TRY_TIMES - times);
        return map;
    }

    private HashMap inputMaxFalut() {

        Map privilegeUsers = new HashMap();
        privilegeUsers.put("state", PrivilegeStatusEnum.STATUS_LOCK.getCode());
        privilegeUsers.put("time", new Date());
        SessionManagerCommon.setPrivilegeStatus(privilegeUsers);
        //返回页面消息
        HashMap map = new HashMap(3,1f);
        map.put("msg", LocaleTool.tranMessage("privilege", "input.freeze"));
        map.put("stateCode", PrivilegeStatusEnum.CODE_99.getCode());
        map.put("state", false);
        return map;
    }

    private HashMap firstInputFalut() {
        Map privilegeUsers = new HashMap();
        privilegeUsers.put("state", PrivilegeStatusEnum.STATUS_ERRORS.getCode());
        privilegeUsers.put("times", 1);
        SessionManagerCommon.setPrivilegeStatus(privilegeUsers);
        //返回页面消息
        HashMap map = new HashMap(3,1f);
        map.put("msg", LocaleTool.tranMessage("privilege", "input.wrong"));
        map.put("state", false);
        map.put("leftTimes", TRY_TIMES - 1);
        return map;
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

    private HashMap validateCheckCode(String valiCode) {
        HashMap map = new HashMap(3,1f);
        String msg;
        if (StringTool.isBlank(valiCode)) {
            msg = LocaleTool.tranMessage("privilege", "captcha.input");
            map.put("msg", msg);
            map.put("stateCode", PrivilegeStatusEnum.CODE_98.getCode());
            map.put("state", false);
        } else {
            if (!checkValiCode(valiCode)) {
                msg = LocaleTool.tranMessage("privilege", "captcha.wrong");
                map.put("msg", msg);
                map.put("stateCode", PrivilegeStatusEnum.CODE_97.getCode());
                map.put("state", false);
            } else {
                map.put("stateCode", PrivilegeStatusEnum.CODE_100.getCode());
                map.put("state", true);
            }
        }
        return map;
    }


    @RequestMapping("/setPrivilegePassword")
    @ResponseBody
    public Map setPrivilegePassword(SysUserVo sysUserVo, @FormModel @Valid SecurityPasswordForm form,  BindingResult result) {
        if (result.hasErrors()) {
            return null;
        }

        sysUserVo = updateUserPermissionPwd(sysUserVo);
        Map privilegeUsers = new HashMap();
        privilegeUsers.put("state", PrivilegeStatusEnum.STATUS_OK.getCode());
        privilegeUsers.put("time", new Date());
        SessionManagerCommon.setPrivilegeStatus(privilegeUsers);
        return getVoMessage(sysUserVo);
    }

    private SysUserVo updateUserPermissionPwd(SysUserVo sysUserVo) {
        //设置id
        sysUserVo.getResult().setId(SessionManagerCommon.getUser().getId());
        sysUserVo.setProperties(SysUser.PROP_PERMISSION_PWD,
                SysUser.PROP_SECPWD_ERROR_TIMES,
                SysUser.PROP_SECPWD_FREEZE_END_TIME,
                SysUser.PROP_SECPWD_FREEZE_START_TIME);
        //密码加密
        String md5Pwd = AuthTool.md5SysUserPermission(sysUserVo.getResult().getPermissionPwd(),
                SessionManagerCommon.getUser().getUsername());
        sysUserVo.getResult().setPermissionPwd(md5Pwd);
        sysUserVo.getResult().setSecpwdErrorTimes(0);
        sysUserVo.getResult().setSecpwdFreezeEndTime(new Date());
        sysUserVo.getResult().setSecpwdFreezeStartTime(new Date());
        sysUserVo = ServiceTool.sysUserService().updateOnly(sysUserVo);
        if(SessionManagerCommon.isCurrentSiteMaster()){
            sysUserVo._setDataSourceId(SessionManagerCommon.getSiteParentId());
            sysUserVo.getResult().setId(SessionManagerCommon.getSiteUserId());
            sysUserVo = ServiceTool.sysUserService().updateOnly(sysUserVo);
        }
        //改变session中user的权限密码
        if (sysUserVo.isSuccess()) {
            /*SysUser sysUser = SessionManagerBase.getUser();
            sysUser.setPermissionPwd(sysUserVo.getResult().getPermissionPwd());
            sysUser.setSecpwdErrorTimes(0);
            sysUser.setSecpwdFreezeStartTime(sysUserVo.getResult().getSecpwdFreezeStartTime());
            sysUser.setSecpwdFreezeEndTime(sysUserVo.getResult().getSecpwdFreezeEndTime());
            SessionManagerBase.setUser(sysUser);*/
            SessionManager.refreshUser();
        }
        return sysUserVo;
    }

    private Map getVoMessage(BaseVo baseVo) {
        Map<String, Object> map = new HashMap<>(2,1f);
        if (baseVo.isSuccess() && StringTool.isBlank(baseVo.getOkMsg())) {
            baseVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));

        } else if (!baseVo.isSuccess() && StringTool.isBlank(baseVo.getErrMsg())) {
            baseVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }
        map.put("msg", StringTool.isNotBlank(baseVo.getOkMsg()) ? baseVo.getOkMsg() : baseVo.getErrMsg());
        map.put("state", baseVo.isSuccess());
        return map;
    }

    /**
     * 检查权限密码状态
     *
     * @return
     */
    @RequestMapping("/checkPrivilege")
    @ResponseBody
    public Map checkPrivilege() {
        final HashMap<Object, Object> map = new HashMap<>(2,1f);
        /*if (StringTool.isBlank(SessionManagerCommon.getUser().getPermissionPwd())) {
            //未设置权限密码
            LOG.info("未设置安全密码");
            map.put("state", PrivilegeStatusEnum.CODE_96.getCode());
            return map;
        }*/
        int errTimes = getErrorTimes();
        boolean isLock = isLock();
        LOG.info("是否被锁定了：{0},密码错误次数：{1}",isLock,errTimes);
        if (isLock) {
            map.put("state", PrivilegeStatusEnum.CODE_99.getCode());
        } else {
            SysUser curUser = SessionManagerCommon.getUser();
            if (errTimes == 0) {
                //上次密码正常
                successLastTime(map, curUser);
            } else if (errTimes > 0 || errTimes == -1) {
                //上次密码不正确
                if (errTimes == -1) {
                    errTimes = 1;
                }
                failLastTime(map, errTimes);

            } else {
                SessionManagerCommon.clearPrivilegeStatus();
                map.put("state", PrivilegeStatusEnum.CODE_0.getCode());
            }
        }
        map.put("title", LocaleTool.tranView("common", "securityPassword"));
        return map;
    }

    private void successLastTime(HashMap<Object, Object> map, SysUser curUser) {
        Date now = DateQuickPicker.getInstance().getNow();
        Date start = DateTool.addDays(now, -1);
        final Date lastTime = curUser.getSecpwdFreezeStartTime() == null ? start : curUser.getSecpwdFreezeStartTime();
        long minutesBetween = DateTool.minutesBetween(now, lastTime);
        String paramValue = "5";
        if(ParamTool.getSysParam(SiteParamEnum.SETTING_PRIVILAGE_PASS_TIME)!=null){
            ParamTool.getSysParam(SiteParamEnum.SETTING_PRIVILAGE_PASS_TIME).getParamValue();
        }
        Integer privilegeTime = 0;
        if (StringTool.isNotBlank(paramValue)) {
            privilegeTime = Integer.parseInt(paramValue);
        }
        if (minutesBetween < privilegeTime) {
            //还在允许的时间范围内
            map.put("state", PrivilegeStatusEnum.CODE_100.getCode());
        } else {
            //已超时，重新验证
            SessionManagerCommon.clearPrivilegeStatus();
            map.put("state", PrivilegeStatusEnum.CODE_0.getCode());
        }
    }

    private void failLastTime(HashMap<Object, Object> map, int errTimes) {
        if (errTimes >= TRY_TIMES) {
            map.put("times", 0);
            map.put("state", PrivilegeStatusEnum.CODE_0.getCode());
            updateUserErrorTimes(0, null, null);
        } else {
            final Integer times = errTimes;//(Integer) privilegeUsers.get("times");
            map.put("state", PrivilegeStatusEnum.CODE_98.getCode());
            map.put("times", TRY_TIMES - times);
        }
    }

    /**
     * 显示密码锁定页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/showLockPrivilege")
    public String showLockPrivilege(Model model) {
        SysUser curUser = SessionManagerCommon.getUser();
        String customer = LocaleTool.tranMessage(Module.COMPANY_SETTING, MessageI18nConst.NOTICE_PARAM_CUSTOMER);
        String url = SiteCustomerServiceHelper.getMobileCustomerServiceUrl();
        if (url != null) {
            customer = "<a href=\"" + url + "\" target=\"_blank\">" + customer + "</a>";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("curUser", curUser);
        setLockTime(curUser, model);
        return PERSON_INFO_SECURITY_PWD_LOCK;
    }

}
