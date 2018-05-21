package so.wwb.gamebox.mobile.app.controller;

import org.apache.commons.collections.map.HashedMap;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.BooleanTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.spring.utils.SpringTool;
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
import so.wwb.gamebox.mobile.app.form.AppPlayerTransferForm;
import so.wwb.gamebox.mobile.app.form.UserBankcardAppForm;
import so.wwb.gamebox.mobile.controller.BaseUserInfoController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.fund.enums.TransferResultStatusEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.po.PlayerApiAccount;
import so.wwb.gamebox.model.master.player.po.UserBankcard;
import so.wwb.gamebox.model.master.player.po.UserPlayerTransfer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.UserBankcardVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.fund.form.BtcBankcardForm;
import so.wwb.gamebox.web.passport.form.CheckRealNameForm;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.common.dubbo.ServiceSiteTool.playerTransferService;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

/**
 * Created by legend on 18-1-22.
 */

@Controller
@RequestMapping("/userInfoOrigin")
public class UserInfoAppController extends BaseUserInfoController {
    private Log LOG = LogFactory.getLog(UserInfoAppController.class);

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
     * 非免转初始化
     *
     * @return
     */
    @RequestMapping(value = "/getNoAutoTransferInfo")
    @ResponseBody
    public String getNoAutoTransferInfo() {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getTransferApi(),
                APP_VERSION);
    }

    /**
     * 确认转账
     *
     * @param playerTransferVo
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/transfersMoney", method = RequestMethod.POST)
    @ResponseBody
    @Token(valid = true)
    public String transfersMoney(PlayerTransferVo playerTransferVo, @FormModel @Valid AppPlayerTransferForm form, BindingResult result,HttpServletRequest request) {
        LOG.info("【玩家[{0}]转账】:从[{1}]转到[{2}]", SessionManager.getUserName(), playerTransferVo.getTransferOut(), playerTransferVo.getTransferInto());
        if (!isTimeToTransfer()) {//是否已经过了允许转账的间隔
            Map map = getErrorMessage(TransferResultStatusEnum.TRANSFER_TIME_INTERVAL.getCode(), playerTransferVo.getResult().getApiId());
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                    map.get("msg").toString(),
                    map,
                    APP_VERSION);
        }
        if (result.hasErrors()) {
            Map map = getErrorMessage(TransferResultStatusEnum.TRANSFER_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                    map.get("msg").toString(),
                    map,
                    APP_VERSION);
        }
        if (!checkTransferAmount(playerTransferVo.getResult().getTransferAmount(), playerTransferVo.getTransferOut())) {
            Map map = new HashMap();
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ACCOUNT_NOT_ENOUGH.getCode(),
                    AppErrorCodeEnum.TRANSFER_ACCOUNT_NOT_ENOUGH.getMsg(),
                    map,
                    APP_VERSION);
        }
        loadTransferInfo(playerTransferVo,request);
        Map<String, Object> resultMap = isAbleToTransfer(playerTransferVo);
        if (MapTool.isNotEmpty(resultMap) && !MapTool.getBoolean(resultMap, "state")) {
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                    resultMap.get("msg").toString(),
                    resultMap,
                    APP_VERSION);
            //return resultMap;
        }

        try {
            PlayerApiAccountVo playerApiAccountVo = createVoByTransfer(playerTransferVo);
            PlayerApiAccount playerApiAccount = ServiceSiteTool.playerApiAccountService().queryApiAccountForTransfer(playerApiAccountVo);
            if (playerApiAccount == null) {
                Map map = getErrorMessage(TransferResultStatusEnum.API_ACCOUNT_NOT_FOUND.getCode(), playerTransferVo.getResult().getApiId());
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                        map.get("msg").toString(),
                        map,
                        APP_VERSION);
                //return
            }
            playerApiAccount = ServiceSiteTool.playerApiAccountService().queryPlayerApiAccount(playerApiAccountVo);
            playerTransferVo.setPlayerApiAccount(playerApiAccount);
        } catch (Exception e) {
            LOG.error(e, "【玩家[{0}]转账】:API账号注册超时。", playerTransferVo.getResult().getUserName());
            Map map = getErrorMessage(TransferResultStatusEnum.API_ACCOUNT_NOT_FOUND.getCode(), playerTransferVo.getResult().getApiId());
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                    map.get("msg").toString(),
                    map,
                    APP_VERSION);
            //return
        }

        try {
            TransferResultStatusEnum transferResultStatusEnum = playerTransferService().isBalanceEnough(playerTransferVo);
            if (transferResultStatusEnum != null) {
                Map map = getErrorMessage(transferResultStatusEnum.getCode(), playerTransferVo.getResult().getApiId());
                return AppModelVo.getAppModeVoJson(true,
                        AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                        map.get("msg").toString(),
                        map,
                        APP_VERSION);
                //return
            }
        } catch (Exception e) {
            LOG.error(e, "【玩家[{0}]转账】:账户余额验证出现异常!", playerTransferVo.getResult().getUserName());
            Map map = getErrorMessage(TransferResultStatusEnum.API_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
            return AppModelVo.getAppModeVoJson(true,
                    AppErrorCodeEnum.TRANSFER_ERROR.getCode(),
                    map.get("msg").toString(),
                    map,
                    APP_VERSION);
            //return
        }

        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                doTransfer(playerTransferVo),
                APP_VERSION);
    }

    /**
     * 异常再次请求转账
     *
     * @param playerTransferVo
     * @return
     */
    @RequestMapping(value = "/reconnectTransfer")
    @ResponseBody
    @Token(valid = true)
    public String reconnectTransfer(PlayerTransferVo playerTransferVo) {
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                reconnectTransferApp(playerTransferVo),
                APP_VERSION);
    }

    /**
     * 刷新单个api
     *
     * @param listVo
     * @param isRefresh
     * @return
     */
    @RequestMapping("/refreshApi")
    @ResponseBody
    public String refreshApi(PlayerApiListVo listVo, String isRefresh) {
        if (StringTool.isBlank(isRefresh)) {
            //同步玩家api余额
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerApiBalance(listVo);
        }

        Map<String, Object> map = new HashMap<>(2, 1f);
        //map.put("player", getPlayer());
        Integer apiId = listVo.getSearch().getApiId();
        PlayerApi playerApi = getPlayerApi(apiId);
        Map apiMap = Cache.getApi();
        Map siteApiMap = Cache.getSiteApi();
        map.put("status", getApiStatus(apiMap, siteApiMap, apiId.toString()));
        map.put("apiId", apiId);
        if (playerApi != null) {
            map.put("balance", playerApi.getMoney());
        } else {
            map.put("balance", "0.00");
        }
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
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