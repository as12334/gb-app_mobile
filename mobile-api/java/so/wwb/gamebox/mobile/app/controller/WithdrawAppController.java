package so.wwb.gamebox.mobile.app.controller;


import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.controller.BaseWithDrawController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.player.vo.PlayerTransactionVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.TARGET_REGEX;

@Controller
@RequestMapping("/withdrawOrigin")
public class WithdrawAppController extends BaseWithDrawController {

    /**
     * 获取取款信息
     *
     * @return
     */
    @RequestMapping(value = "/getWithDraw")
    @ResponseBody
    public String getWithDraw(HttpServletRequest request) {
        //判断是否达到取款要求
        AppModelVo vo = new AppModelVo();
        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMsg())) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    vo.getCode(),
                    vo.getMsg(),
                    vo.getData(),
                    APP_VERSION);
        }

        Map<String, Object> map = MapTool.newHashMap();
        withdraw(map, request);

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 提交取款
     */
    @RequestMapping(value = "/submitWithdraw", method = RequestMethod.POST)
    @ResponseBody
    @Token(valid = true)
    public String submitWithdraw(HttpServletRequest request, PlayerTransactionVo playerVo, BindingResult result, SecurityPassword password) {
        Map<String, Object> tokenMap = new HashMap<>(1, 1f);
        tokenMap.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        if (result.hasErrors()) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //判断用户是否存在安全码
        if (!isSafePassword()) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.NOT_SET_SAFE_PASSWORD.getCode(),
                    AppErrorCodeEnum.NOT_SET_SAFE_PASSWORD.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        if (StringTool.isBlank(password.getOriginPwd())) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        if (!verifyOriginPwd(password)) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getCode(),
                    AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //是否有取款银行卡
        Map map = MapTool.newHashMap();
        if (!hasBank(map, request)) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.NO_BANK.getCode(),
                    AppErrorCodeEnum.NO_BANK.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //判断取款是否达到要求
        AppModelVo vo = new AppModelVo();
        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMsg())) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    vo.getCode(),
                    vo.getMsg(),
                    tokenMap,
                    APP_VERSION);
        }
        //是否符合取款金额设置
        if (isInvalidAmount(playerVo, map)) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                    AppErrorCodeEnum.WITHDRAW_BETWEEN_MIN_MAX.getCode(),
                    map.get("msg").toString(),
                    tokenMap,
                    APP_VERSION);
        }
        //取款
        map = addWithdraw(request, playerVo);
        //成功
        if (map.get("state") != null && MapTool.getBoolean(map, "state")) {
            return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.SUCCESS_CODE,
                    AppErrorCodeEnum.SUCCESS.getCode(),
                    AppErrorCodeEnum.SUCCESS.getMsg(),
                    map,
                    APP_VERSION);
        }

        return AppModelVo.getAppModeVoJson(AppErrorCodeEnum.FAIL_COED,
                AppErrorCodeEnum.WITHDRAW_FAIL.getCode(),
                AppErrorCodeEnum.WITHDRAW_FAIL.getMsg(),
                tokenMap,
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
     * 取款判断
     *
     * @param vo
     * @return
     */
    private AppModelVo withDraw(AppModelVo vo) {
        //是否已存在取款订单
        if (hasOrder()) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_HAS_ORDER.getCode());
            vo.setMsg(AppErrorCodeEnum.WITHDRAW_HAS_ORDER.getMsg());
            vo.setError(AppErrorCodeEnum.FAIL_COED);
            return vo;
        }
        //是否被冻结
        if (hasFreeze()) {
            vo.setCode(AppErrorCodeEnum.USER_HAS_FREEZE.getCode());
            vo.setMsg(AppErrorCodeEnum.USER_HAS_FREEZE.getMsg());
            vo.setError(AppErrorCodeEnum.FAIL_COED);
            return vo;
        }
        //今日取款是否达到上限
        if (isFull()) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_IS_FULL.getCode());
            vo.setMsg(AppErrorCodeEnum.WITHDRAW_IS_FULL.getMsg());
            vo.setError(AppErrorCodeEnum.FAIL_COED);
            return vo;
        }
        //余额是否充足
        Map<String, Object> map = new HashMap<>();
        if (isBalanceAdequate(map)) {
            vo.setCode(AppErrorCodeEnum.WITHDRAW_MIN_AMOUNT.getCode());
            vo.setMsg(AppErrorCodeEnum.WITHDRAW_MIN_AMOUNT.getMsg().replace(TARGET_REGEX, map.get("withdrawMinNum").toString()));
            vo.setError(AppErrorCodeEnum.FAIL_COED);
            return vo;
        }

        return vo;
    }
}
