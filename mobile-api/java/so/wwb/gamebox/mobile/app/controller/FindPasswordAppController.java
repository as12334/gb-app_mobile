package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criterion;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.msg.notice.po.NoticeContactWay;
import org.soul.model.msg.notice.vo.NoticeContactWayVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.so.SysUserSo;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.SubSysCodeEnum;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.enums.ContactWayTypeEnum;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UpdatePasswordVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

@Controller
@RequestMapping("/findPasswordOrigin")
public class FindPasswordAppController {
    private Log LOG = LogFactory.getLog(FindPasswordAppController.class);

    /**
     * 根据用户名判断用户是否绑定手机号
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "findUserPhone")
    @ResponseBody
    public String findUserPhone(SysUser user) {
        if (StringTool.isBlank(user.getUsername())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        //根据用户名查找用户信息
        Map map = findWay(user);
        if (MapTool.isEmpty(map)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (map.get("phone") != null && StringTool.isBlank(map.get("phone").toString())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UNBOUND_PHONE.getCode(),
                    AppErrorCodeEnum.UNBOUND_PHONE.getMsg(),
                    null,
                    APP_VERSION);
        }
        Map dataMap = new HashMap(2, 1f);
        dataMap.put("encryptedId", map.get("encryptedId"));
        dataMap.put("phone", map.get("phone"));
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                dataMap,
                APP_VERSION);
    }

    /**
     * 验证手机短信验证码
     *
     * @param phone
     * @param code
     * @return
     */
    @RequestMapping(value = "checkPhoneCode")
    @ResponseBody
    public String checkPhoneCode(String phone, String code) {
        if (StringTool.isBlank(phone) && StringTool.isBlank(code)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                    AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (!checkPhoneCode(code)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                    AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
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
     * 找回登录密码
     *
     * @return
     */
    @RequestMapping(value = "findLoginPassword")
    @ResponseBody
    public String findLoginPassword(SysUser user, UpdatePasswordVo updatePasswordVo) {
        if (StringTool.isBlank(user.getUsername())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        //验证密码强度
        if (!checkWeakPassword(updatePasswordVo.getNewPassword())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SAFE_PASSWORD_TOO_SIMPLE.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_TOO_SIMPLE.getMsg(),
                    null,
                    APP_VERSION);
        }
        //用户信息
        user = getUserInfo(user);
        if (user == null) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        String newPwd = AuthTool.md5SysUserPassword(updatePasswordVo.getNewPassword(), user.getUsername());
        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = new SysUser();
        sysUser.setId(user.getId());
        sysUser.setPassword(newPwd);
        sysUser.setPasswordLevel(updatePasswordVo.getPasswordLevel());
        //修改成功需将登录错误次数修改为null
        sysUser.setLoginErrorTimes(null);
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_PASSWORD, SysUser.PROP_PASSWORD_LEVEL, SysUser.PROP_LOGIN_ERROR_TIMES);
        sysUserVo = ServiceTool.sysUserService().updateOnly(sysUserVo);
        if (!sysUserVo.isSuccess()) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getMsg(),
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
     * 找回安全密码
     *
     * @param password
     * @param user
     * @return
     */
    @RequestMapping(value = "findSafePassword")
    @ResponseBody
    public String findSafePassword(SecurityPassword password, SysUser user) {
        if (StringTool.isBlank(user.getUsername())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.USER_NAME_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (StringTool.isBlank(password.getPwd1())) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg(),
                    null,
                    APP_VERSION);
        }
        //用户信息
        user = getUserInfo(user);
        if (user == null) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getCode(),
                    AppErrorCodeEnum.USER_INFO_NOT_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        if (!savePassword(password.getPwd1(), user)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getCode(),
                    AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getMsg(),
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
     * 存储新密码
     */
    private boolean savePassword(String password, SysUser user) {
        SysUserVo vo = new SysUserVo();
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

    private void updateErrorTimes(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setProperties(SysUser.PROP_SECPWD_ERROR_TIMES);
        vo.setResult(user);
        ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
    }

    /**
     * 根据用户账号获取用户信息
     *
     * @param user
     * @return
     */
    private SysUser getUserInfo(SysUser user) {
        SysUserVo userVo = new SysUserVo();
        userVo.getSearch().setUsername(user.getUsername());
        userVo.getSearch().setSubsysCode(SubSysCodeEnum.PCENTER.getCode());
        userVo.getSearch().setSiteId(SessionManager.getSiteId());
        return ServiceTool.sysUserService().findByUsername(userVo);
    }

    /**
     * 根据用户名查询
     *
     * @param user
     * @return
     */
    private Map findWay(SysUser user) {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setSearch(new SysUserSo());
        sysUserVo.getSearch().setUsername(user.getUsername());
        String subSysCode = BaseConfigManager.getConfigration().getSubsysCode();
        if (SubSysCodeEnum.MSITES.getCode().equals(subSysCode)) {
            subSysCode = SubSysCodeEnum.PCENTER.getCode();
        }
        sysUserVo.getSearch().setSubsysCode(subSysCode);
        sysUserVo.getSearch().setSiteId(SessionManagerCommon.getSiteId());

        return ServiceSiteTool.userPlayerService().getFindWayByUsername(sysUserVo);
    }

    /**
     * 获取用户手机号
     *
     * @return
     */
    private NoticeContactWay getUserPhoneNumber(Integer userId) {
        NoticeContactWayVo contactVo = new NoticeContactWayVo();
        contactVo.setResult(new NoticeContactWay());
        contactVo.getQuery().setCriterions(
                new Criterion[]{
                        new Criterion(NoticeContactWay.PROP_USER_ID, Operator.EQ, userId)
                        , new Criterion(NoticeContactWay.PROP_CONTACT_TYPE, Operator.EQ, ContactWayTypeEnum.MOBILE.getCode())}
        );
        contactVo = ServiceTool.noticeContactWayService().search(contactVo);
        return contactVo.getResult();
    }

    /**
     * 修改账户密码--检查密码规则是否弱密码
     *
     * @param password
     * @return
     */
    private boolean checkWeakPassword(@RequestParam("newPassword") String password) {
        // 弱密码过滤
        if (PasswordRule.isWeak(password)) {
            return false;
        }
        return true;
    }

    /**
     * 找回密码手机短信验证
     *
     * @param phoneCode
     * @return
     */
    public boolean checkPhoneCode(String phoneCode) {
        if (StringTool.isBlank(phoneCode)) {
            return false;
        }
        try {
            return phoneCode.equals(SessionManagerCommon.getCheckRegisterPhoneInfo().get("code"));
        } catch (Exception e) {
            LOG.error(e, "验证手机验证码错误！");
        }
        return false;
    }
}
