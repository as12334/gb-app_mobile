package so.wwb.gamebox.mobile.app.controller;

import org.apache.commons.collections.map.HashedMap;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.lang.BooleanTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.enums.AppMineLinkEnum;
import so.wwb.gamebox.mobile.app.model.AppMineLinkVo;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.MyUserInfo;
import so.wwb.gamebox.mobile.app.model.UserInfoApp;
import so.wwb.gamebox.mobile.app.validateForm.UserBankcardAppForm;
import so.wwb.gamebox.mobile.controller.BaseUserInfoController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.UserBankcard;
import so.wwb.gamebox.model.master.player.po.UserPlayerTransfer;
import so.wwb.gamebox.model.master.player.vo.UserBankcardVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.fund.form.BtcBankcardForm;
import so.wwb.gamebox.web.passport.form.CheckRealNameForm;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

/**
 * Created by legend on 18-1-22.
 */

@Controller
@RequestMapping("/userInfoOrigin")
public class UserInfoAppController extends BaseUserInfoController {

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("link", setLink());//链接地址
        map.put("user", getMineLinkInfo(request));
        map.put("bankList", bankList());
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }

    /**
     * 获取用户资产信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/getUserAssert")
    @ResponseBody
    public String getUserAssert(HttpServletRequest request) {
        //获取用户资产相关信息（总资产、钱包余额）
        MyUserInfo userInfo = new MyUserInfo();
        getUserAssertInfo(userInfo, SessionManager.getUserId());
        UserInfoApp userInfoApp = new UserInfoApp();
        userInfoApp.setApis(userInfo.getApis());
        userInfoApp.setAssets(String.valueOf(userInfo.getTotalAssets()));
        userInfoApp.setBalance(String.valueOf(userInfo.getWalletBalance()));
        userInfoApp.setUsername(SessionManager.getUserName());
        userInfoApp.setCurrSign(getCurrencySign(SessionManager.getUser().getDefaultCurrency()));
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), userInfoApp, APP_VERSION);
    }

    /**
     * 提交比特币信息
     *
     * @return
     */
    @RequestMapping(value = "/submitBtc", method = RequestMethod.POST)
    @ResponseBody
    public String submitBtc(@FormModel @Valid BtcBankcardForm form, BindingResult result) {
        if (result.hasErrors()) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(),
                    null, APP_VERSION);
        }
        String bankcardNumber = form.getResult_bankcardNumber();
        /*比特币*/
        final String BITCOIN = "bitcoin";
        UserBankcardVo bankcardVo = new UserBankcardVo();
        bankcardVo.setResult(new UserBankcard());
        bankcardVo.getResult().setBankcardNumber(bankcardNumber);
        bankcardVo.getResult().setUserId(SessionManager.getUserId());
        bankcardVo.setUserType(SessionManagerCommon.getUserType().getCode());
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(AppConstant.APP_VERSION);
        if (checkCardIsExistsByUserId(bankcardVo)) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.HAS_BTC.getCode(),
                    AppErrorCodeEnum.HAS_BTC.getMsg(),
                    null, APP_VERSION);
        }

        UserBankcard bankcard = bankcardVo.getResult();
        bankcard.setUserId(getAgentId());
        bankcard.setType(UserBankcardTypeEnum.BITCOIN.getCode());
        bankcard.setBankName(BITCOIN);
        bankcardVo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(bankcardVo);
        if (!bankcardVo.isSuccess()) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.SUBMIT_BTC_FAIL.getCode(),
                    AppErrorCodeEnum.SUBMIT_BTC_FAIL.getMsg(),
                    null, APP_VERSION);
        }
        Map<String, String> map = new HashMap<>(1, 1f);
        map.put("btcNumber", bankcardVo.getResult().getBankcardNumber());

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map, APP_VERSION);
    }


    /**
     * 提交银行卡信息
     *
     * @param result
     * @return
     */
    @RequestMapping(value = "/submitBankCard", method = RequestMethod.POST)
    @ResponseBody
    public String submitBankCard(@FormModel @Valid UserBankcardAppForm form, BindingResult result) {
        if (result.hasErrors()) {
            return AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(), null, APP_VERSION);//当出现验证信息没有过的时候一律提示参数有误
        }
        UserBankcardVo vo = new UserBankcardVo();
        vo.setUserType(SessionManagerCommon.getUserType().getCode());
        if (vo.getResult() == null) {
            vo.setResult(new UserBankcard());
        }
        vo.getResult().setBankcardMasterName(form.getResult_bankcardMasterName());
        vo.getResult().setBankName(form.getResult_bankName());
        vo.getResult().setBankcardNumber(form.getResult_bankcardNumber());
        vo.getResult().setBankDeposit(form.getResult_bankDeposit());
        vo.getResult().setUserId(SessionManager.getUserId());
        if (StringTool.isBlank(SessionManager.getUser().getRealName()) && StringTool.isBlank(vo.getResult().getBankcardMasterName())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getCode(),
                    AppErrorCodeEnum.REAL_NAME_NOT_NULL.getMsg(), null, APP_VERSION);
        }
        if (checkCardIsExistsByUserId(vo)) {           //当银行卡号重复时，提示信息为银行卡号已存在
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.USER_BINDING_BANK_CARD_EXIST.getCode(),
                    AppErrorCodeEnum.USER_BINDING_BANK_CARD_EXIST.getMsg(), null, APP_VERSION);
        }
        if (StringTool.isNotBlank(SessionManager.getUser().getRealName())) {
            vo.getResult().setBankcardMasterName(SessionManager.getUser().getRealName());
        }
        vo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(vo);
        SessionManagerCommon.refreshUser();

        Map<String, String> map = new HashMap<>(4, 1f);
        if (vo.isSuccess()) {
            map.put("realName", vo.getResult().getBankcardMasterName());
            map.put("bankName", vo.getResult().getBankName());
            map.put("bankCardNumber", vo.getResult().getBankcardNumber());
            map.put("bankDeposit", vo.getResult().getBankDeposit());
        }
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(), map, APP_VERSION);
    }

  /*  *//**
     * 验证真实姓名
     * @param form
     * @param vo
     * @param result
     * @param request
     * @return
     *//*
    @RequestMapping(value = "/verifyRealNameForApp", method = RequestMethod.POST)
    @ResponseBody
    public String verifyRealNameForApp(@FormModel @Valid CheckRealNameForm form, BindingResult result, UserPlayerTransferVo vo, HttpServletRequest request) {


        boolean conflict = BooleanTool.toBoolean(MapTool.getBoolean(map,"conflict"));
        boolean nameSame = BooleanTool.toBoolean(MapTool.getBoolean(map,"nameSame"));
        Boolean importResult =false;
        if(nameSame&&!conflict){
            importResult = importOldPlayer(vo, request);
        }
        map.put("importResult",importResult);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(), map, APP_VERSION);

    }*/

    /**
     * 验证真实姓名
     */
    @RequestMapping(value = "verifyRealNameForApp", method = RequestMethod.POST)
    @ResponseBody
    public String verifyRealNameForApp(UserPlayerTransferVo vo, @FormModel("result") @Valid CheckRealNameForm form, BindingResult result, HttpServletRequest request) {

        if (result.hasErrors()) {

            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getCode(),
                    AppErrorCodeEnum.PARAM_HAS_ERROR.getMsg(),
                    null, APP_VERSION);
        }

        Map<String, Object> map = new HashMap<>(3, 1f);

        String inputName = vo.getResult().getRealName();
        vo = ServiceSiteTool.userPlayerTransferService().search(vo);
        UserPlayerTransfer player = vo.getResult();

        checkName(vo, map, inputName, player);

        if (MapTool.isEmpty(map)) {
            map = new HashedMap();
        }

        boolean conflict = BooleanTool.toBoolean(MapTool.getBoolean(map, "conflict"));
        boolean nameSame = BooleanTool.toBoolean(MapTool.getBoolean(map, "nameSame"));
        Boolean importResult = false;
        if (nameSame && !conflict) {
            importResult = importOldPlayer(vo, request);
        }

        map.put("importResult", importResult);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(), map, APP_VERSION);
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
}