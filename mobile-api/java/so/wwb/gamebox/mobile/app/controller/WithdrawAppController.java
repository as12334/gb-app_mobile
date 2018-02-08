package so.wwb.gamebox.mobile.app.controller;


import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.math.NumberTool;
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
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
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
    public String submitWithdraw(HttpServletRequest request, PlayerTransactionVo playerVo, BindingResult result, SecurityPassword password) {
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
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SAFE_PASSWORD_ERROR.getCode(),
                    AppErrorCodeEnum.SAFE_PASSWORD_ERROR.getMsg(),
                    tokenMap,
                    APP_VERSION);
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
