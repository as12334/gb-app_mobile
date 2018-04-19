package so.wwb.gamebox.mobile.transfer.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.BooleanTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.commons.support._Module;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.transfer.form.PlayerTransferForm;
import so.wwb.gamebox.mobile.wallet.controller.WalletBaseController;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.FundTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransferResultStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.TransferSourceEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerTransfer;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.po.PlayerApiAccount;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.validation.Valid;
import java.util.*;

/**
 * 转账
 * Created by jessie on 16-7-21.
 */
@Controller
@RequestMapping(value = "/transfer")
public class TransferController extends WalletBaseController {
    //记录日志
    private static final Log LOG = LogFactory.getLog(TransferController.class);
    //转账主页
    private static final String TRANSFER_INDEX_URL = "/transfer/Index";
    private static final String TRANSFER_WALLET = "wallet";//转入转出选择我的钱包
    //免转-转帐主页
    private static final String AUTO_TRANSFER_INDEX_URL = "/transfer/auto/Index";

    @Override
    @Token(generate = true)
    @Upgrade(upgrade = true)
    protected String index(Model model) {
        //玩家信息
        model.addAttribute("player", getPlayer());
        if (SessionManagerCommon.isAutoPay()) {
            PlayerApiListVo playerApiListVo = fundRecord(false);
            model.addAttribute("apis", getAllAPI(playerApiListVo.getResult()));
            return AUTO_TRANSFER_INDEX_URL;
        } else {
            PlayerApiListVo playerApiListVo = fundRecord(false);
            model.addAttribute("apis", getAllAPI(playerApiListVo.getResult()));
            model.addAttribute("validateRule", JsRuleCreator.create(PlayerTransferForm.class));
            //正在处理中转账金额(额度转换)
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
            model.addAttribute("transferPendingAmount", ServiceSiteTool.playerTransferService().queryProcessAmount(playerTransferVo));
            return TRANSFER_INDEX_URL;

        }
    }

    @RequestMapping("/transferBack")
    @ResponseBody
    public Map<String, Object> transfer() {
        Map<String, Object> map = new HashMap<>(1,1f);
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        return map;
    }

    @RequestMapping("/checkTransferAmount")
    @ResponseBody
    public boolean checkTransferAmount(@RequestParam("result.transferAmount") String transferAmount,
                                       @RequestParam("transferOut") String transferOut) {
        double amount = NumberTool.toDouble(transferAmount);
        if (TRANSFER_WALLET.equals(transferOut)) {
            VUserPlayer player = getPlayer();
            if (player.getWalletBalance() != null && player.getWalletBalance() >= amount) {
                return true;
            }
        } else if (StringTool.isNotBlank(transferOut)) {
            Integer apiId = NumberTool.toInt(transferOut);
            PlayerApi playerApi = getPlayerApi(apiId);
            if (playerApi != null && playerApi.getMoney() != null && playerApi.getMoney() >= amount) {
                return true;
            }
        }
        return false;
    }

    private PlayerApi getPlayerApi(Integer apiId) {
        if (apiId == null) {
            return null;
        }
        PlayerApiVo playerApiVo = new PlayerApiVo();
        playerApiVo.getSearch().setApiId(apiId);
        playerApiVo.getSearch().setPlayerId(SessionManager.getUserId());
        playerApiVo = ServiceSiteTool.playerApiService().search(playerApiVo);
        return playerApiVo.getResult();
    }

    @RequestMapping("/queryApis")
    @ResponseBody
    private List<Map> queryApis() {
        List<Map> transableApis = new ArrayList<>();
        Map<String, Api> apis = Cache.getApi();
        Map<String, SiteApi> siteApis = Cache.getSiteApi();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        Api api;
        SiteApi siteApi;
        Map map = new HashMap();
        map.put("text", "我的钱包");
        map.put("value", "wallet");
        transableApis.add(map);
        for (String apiId : siteApis.keySet()) {
            api = apis.get(apiId);
            //额度转换的ｂｓｇ不支持
            if(ApiProviderEnum.BSG.getCode().equals(apiId)){
                continue;
            }
            siteApi = siteApis.get(apiId);
            if (api != null
                    && !StringTool.equals(api.getSystemStatus(), disable)
                    && !StringTool.equals(siteApi.getSystemStatus(), disable)
                    && !StringTool.equals(api.getSystemStatus(), maintain)
                    && !StringTool.equals(siteApi.getSystemStatus(), maintain)
                    && (api.getTransferable() == null || api.getTransferable())) {
                Map apiMap = new HashMap();
                apiMap.put("text", CacheBase.getSiteApiName(apiId));
                apiMap.put("value", api.getId());
                transableApis.add(apiMap);
            }
        }

        return transableApis;
    }


    private IPlayerTransferService playerTransferService() {
        return ServiceSiteTool.playerTransferService();
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
    //@Token(valid = true)
    public Map transfersMoney(PlayerTransferVo playerTransferVo, @FormModel @Valid PlayerTransferForm form, BindingResult result) {
        LOG.info("【玩家[{0}]转账】:从[{1}]转到[{2}]", SessionManager.getUserName(), playerTransferVo.getTransferOut(), playerTransferVo.getTransferInto());
        if (!isTimeToTransfer()) {//是否已经过了允许转账的间隔
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_TIME_INTERVAL.getCode(), playerTransferVo.getResult().getApiId());
        }
        if (result.hasErrors()) {
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
        }
        loadTransferInfo(playerTransferVo);
        Map<String, Object> resultMap = isAbleToTransfer(playerTransferVo);
        if (MapTool.isNotEmpty(resultMap) && !MapTool.getBoolean(resultMap, "state")) {
            return resultMap;
        }

        try {
            PlayerApiAccountVo playerApiAccountVo = createVoByTransfer(playerTransferVo);
            PlayerApiAccount playerApiAccount = ServiceSiteTool.playerApiAccountService().queryApiAccountForTransfer(playerApiAccountVo);
            if (playerApiAccount == null) {
                return getErrorMessage(TransferResultStatusEnum.API_ACCOUNT_NOT_FOUND.getCode(), playerTransferVo.getResult().getApiId());
            }
            playerApiAccount = ServiceSiteTool.playerApiAccountService().queryPlayerApiAccount(playerApiAccountVo);
            playerTransferVo.setPlayerApiAccount(playerApiAccount);
        } catch (Exception e) {
            LOG.error(e, "【玩家[{0}]转账】:API账号注册超时。", playerTransferVo.getResult().getUserName());
            return getErrorMessage(TransferResultStatusEnum.API_ACCOUNT_NOT_FOUND.getCode(), playerTransferVo.getResult().getApiId());
        }

        try {
            TransferResultStatusEnum transferResultStatusEnum = playerTransferService().isBalanceEnough(playerTransferVo);
            if (transferResultStatusEnum != null)
                return getErrorMessage(transferResultStatusEnum.getCode(), playerTransferVo.getResult().getApiId());
        } catch (Exception e) {
            LOG.error(e, "【玩家[{0}]转账】:账户余额验证出现异常!", playerTransferVo.getResult().getUserName());
            return getErrorMessage(TransferResultStatusEnum.API_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
        }

        return doTransfer(playerTransferVo);
    }

    /**
     * 封装playerTransferVo
     *
     * @param playerTransferVo
     * @return
     */
    public PlayerApiAccountVo createVoByTransfer(PlayerTransferVo playerTransferVo) {
        PlayerApiAccountVo playerApiAccountVo = new PlayerApiAccountVo();
        playerApiAccountVo.setSysUser(playerTransferVo.getSysUser());
        playerApiAccountVo.setApiId(playerTransferVo.getResult().getApiId());
        playerApiAccountVo.getSearch().setApiId(playerTransferVo.getResult().getApiId());
        playerApiAccountVo.getSearch().setUserId(SessionManager.getUserId());
        return playerApiAccountVo;
    }

    /**
     * 封装当前转账的基本信息
     *
     * @param playerTransferVo
     */
    private void loadTransferInfo(PlayerTransferVo playerTransferVo) {
        if (TRANSFER_WALLET.equals(playerTransferVo.getTransferInto())) {//转入钱包
            playerTransferVo.getResult().setTransferType(FundTypeEnum.TRANSFER_INTO.getCode());
            playerTransferVo.getResult().setApiId(NumberTool.toInt(playerTransferVo.getTransferOut()));
        } else if (TRANSFER_WALLET.equals(playerTransferVo.getTransferOut())) {//转出钱包
            playerTransferVo.getResult().setTransferType(FundTypeEnum.TRANSFER_OUT.getCode());
            playerTransferVo.getResult().setApiId(NumberTool.toInt(playerTransferVo.getTransferInto()));
        }
        playerTransferVo.setSysUser(SessionManager.getUser());
        playerTransferVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerTransferVo.getResult().setUserId(SessionManager.getUserId());
        playerTransferVo.getResult().setUserName(SessionManager.getUserName());
        playerTransferVo.getResult().setIp(SessionManager.getIpDb().getIp());
        playerTransferVo.getResult().setTransferSource(TransferSourceEnum.PLAYER.getCode());
    }

    /**
     * 当前请求是否可以转账
     *
     * @param playerTransferVo
     * @return 为空则本次转账请求正常
     */
    private Map<String, Object> isAbleToTransfer(PlayerTransferVo playerTransferVo) {
        PlayerTransfer result = playerTransferVo.getResult();
        if (playerTransferVo.getResult().getTransferAmount() <= 0) {
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_ERROR_AMOUNT.getCode(), playerTransferVo.getResult().getApiId());
        }
        if (!ParamTool.isAllowTransfer(SessionManager.getSiteId())) {//站点是否允许转账
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_SWITCH_CLOSE.getCode(), playerTransferVo.getResult().getApiId());
        }
        Integer apiId = playerTransferVo.getResult().getApiId();
        if(NumberTool.toInt(ApiProviderEnum.BSG.getCode())==apiId){
            return getErrorMessage(TransferResultStatusEnum.API_TRANSFER_UNSUPPORTED.getCode(), playerTransferVo.getResult().getApiId());
        }
        Api api = CacheBase.getApi().get(String.valueOf(apiId));
        SiteApi siteApi = CacheBase.getSiteApi().get(String.valueOf(apiId));
        if (api.getTransferable() == null || !api.getTransferable())
            return getErrorMessage(TransferResultStatusEnum.API_TRANSFER_SWITCH_COLSE.getCode(), playerTransferVo.getResult().getApiId());

        if (isMaintain(api, siteApi))
            return getErrorMessage(TransferResultStatusEnum.API_STATUS_MAINTAIN.getCode(), playerTransferVo.getResult().getApiId());
        //模拟账号且是自主api可用,其他试玩模式下不支持转账
        if (SessionManagerCommon.getDemoModelEnum() != null) {
            if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(SessionManagerCommon.getDemoModelEnum()) && (
                    apiId == Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                            apiId == Integer.valueOf(ApiProviderEnum.DWT.getCode()))) {
            } else {
                return getErrorMessage(TransferResultStatusEnum.TRANSFER_DEMO_UNSUPPORTED.getCode(), playerTransferVo.getResult().getApiId());
            }
        }
        //实时更新转账上限统计值，判断是否超出转账上限
        boolean isOverTransfer = playerTransferService().transferLimit(playerTransferVo);
        //转账上限
        if ( FundTypeEnum.TRANSFER_OUT.getCode().equals(playerTransferVo.getResult().getTransferType()) && (ParamTool.getTransLimit() || isOverTransfer)) {
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_LIMIT.getCode(), playerTransferVo.getResult().getApiId());
        }
        return null;
    }

    /**
     * api是否维护中，包含禁用
     *
     * @param api
     * @param siteApi
     * @return
     */
    private boolean isMaintain(Api api, SiteApi siteApi) {
        return GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(api.getSystemStatus()) || GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(siteApi.getSystemStatus());
    }

    /**
     * 是否满足允许转账间隔（3秒）
     *
     * @return
     */
    private boolean isTimeToTransfer() {
        Date transferTime = SessionManager.getTransferTime();
        Date nowTime = SessionManager.getDate().getNow();
        SessionManager.setTransferTime(SessionManager.getDate().getNow());
        return transferTime == null || DateTool.secondsBetween(nowTime, transferTime) > 3;
    }

    /**
     * 开始进行转账
     *
     * @param playerTransferVo
     * @return
     */
    private Map<String, Object> doTransfer(PlayerTransferVo playerTransferVo) {
        LOG.info("【玩家[{0}]转账】:开始处理,当前时间【{1}】", playerTransferVo.getResult().getUserName(), SessionManager.getTransferTime());
        try {
            playerTransferVo = playerTransferService().saveTransferAndTransaction(playerTransferVo);
        } catch (Exception e) {
            LOG.error("【玩家[{0}]转账】:生成额度转换记录失败。", playerTransferVo.getResult().getUserName());
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
        }
        if (!playerTransferVo.isSuccess()) {
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
        }

        try {
            playerTransferVo = playerTransferService().handleTransferResult(playerTransferVo);
        } catch (Exception e) {
            LOG.error("【玩家[{0}]转账】:额度转换处理超时。", playerTransferVo.getResult().getUserName());
            return getErrorMessage(TransferResultStatusEnum.TRANSFER_TIME_OUT.getCode(), playerTransferVo.getResult().getApiId());
        }
        Map<String, Object> resultMap;
        if (playerTransferVo.isSuccess()) {
            //转账后更新player_api_account表的登录时间，以判断余额是否更新
            updatePlayerApiAccountLoginTime(playerTransferVo);
            resultMap = getSuccessMessage(playerTransferVo.getResult().getApiId());
        } else {
            resultMap = getErrorMessage(TransferResultStatusEnum.TRANSFER_INTERFACE_BUSY.getCode(), playerTransferVo.getResult().getApiId());
        }
        resultMap.put("orderId", playerTransferVo.getResult().getTransactionNo());
        resultMap.put("result", playerTransferVo.getResultCode());
        return resultMap;
    }

    private void updatePlayerApiAccountLoginTime(PlayerTransferVo playerTransferVo) {
        PlayerApiAccountVo playerApiAccountVo = new PlayerApiAccountVo();
        PlayerApiAccount playerApiAccount = playerTransferVo.getPlayerApiAccount();
        playerApiAccount.setLastLoginIp(SessionManager.getIpDb().getIp());
        playerApiAccount.setLastLoginTime(SessionManager.getDate().getNow());
        playerApiAccountVo.setProperties(PlayerApiAccount.PROP_LAST_LOGIN_TIME, PlayerApiAccount.PROP_LAST_LOGIN_IP);
        playerApiAccountVo.setResult(playerApiAccount);
        ServiceSiteTool.playerApiAccountService().updateOnly(playerApiAccountVo);
    }

    /**
     * 获取成功提示信息
     *
     * @param apiId
     * @return
     */
    private Map<String, Object> getSuccessMessage(Integer apiId) {
        Map<String, Object> resultMap = new HashMap<>(5,1f);
        resultMap.put("state", true);
        resultMap.put("apiId", apiId);
        return resultMap;
    }

    /**
     * 获取失败提示信息
     *
     * @param messageCode
     * @param apiId
     * @return
     */
    private Map<String, Object> getErrorMessage(String messageCode, Integer apiId) {
        Map<String, Object> resultMap = new HashMap<>(5,1f);
        resultMap.put("state", false);
        resultMap.put("msg", LocaleTool.tranMessage(Module.FUND.getCode(), messageCode));
        resultMap.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        return resultMap;
    }


    /**
     * 转账结果消息提示
     *
     * @param isSuccess
     * @param msg
     * @param map
     * @return
     */
    private Map<String, Object> getMessage(boolean isSuccess, String msg, Map<String, Object> map) {
        map.put("state", isSuccess);
        if (msg == null && isSuccess) {
            msg = LocaleTool.tranMessage(Module.FUND, "Transfer.transfer.success");
        } else if (msg == null && !isSuccess) {
            msg = LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED);
        }
        map.put("msg", msg);
        if (!isSuccess) {
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        }
        return map;
    }

    /**
     * 刷新单个api
     *
     * @param listVo
     * @param isRefresh
     * @return
     */
    @RequestMapping("refreshApi")
    @ResponseBody
    public String refreshApi(PlayerApiListVo listVo, String isRefresh) {
        if (StringTool.isBlank(isRefresh)) {
            //同步玩家api余额
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerApiBalance(listVo);
        }

        Map<String, Object> map = new HashMap<>(2,1f);
        map.put("player", getPlayer());
        Integer apiId = listVo.getSearch().getApiId();
        PlayerApi playerApi = getPlayerApi(apiId);
        Api api = Cache.getApi().get(apiId.toString());
        SiteApi siteApi = Cache.getSiteApi().get(apiId.toString());
        map.put("status", getApiStatus(api, siteApi));
        map.put("apiId", apiId);
        if (playerApi != null) {
            map.put("apiMoney", CurrencyTool.formatCurrency(playerApi.getMoney()));
        } else {
            map.put("apiMoney", "0.00");
        }
        return JsonTool.toJson(map);
    }

    /**
     * 再试一次
     *
     * @param playerTransferVo
     * @return
     */
    @RequestMapping("/reconnectTransfer")
    @ResponseBody
    public Map reconnectTransfer(PlayerTransferVo playerTransferVo) {
        Map<String, Object> map = new HashMap<>(5,1f);
        if (StringTool.isBlank(playerTransferVo.getSearch().getTransactionNo())) {
            return getMessage(playerTransferVo.isSuccess(), null, map);
        }
        try {
            playerTransferVo.setResult(playerTransferService().queryTransfer(playerTransferVo));
            playerTransferVo = ServiceSiteTool.playerTransferService().checkTransferByPlayerTransfer(playerTransferVo);
        } catch (Exception e) {
            LOG.error(e);
        }
        map.put("apiId", playerTransferVo.getResult().getApiId());
        map.put("orderId", playerTransferVo.getSearch().getTransactionNo());
        map.put("result", playerTransferVo.getResultCode());
        return getMessage(playerTransferVo.isSuccess(), null, map);
    }

    /********************************************
     * Jeff 转账页面查询api余额   复制自msite
     ***************************************/
    @RequestMapping(value = "/refreshBalance")
    @ResponseBody
    public Map refreshApiBalance() {
        Map<String, Object> infoMap = new HashMap<>();
        PlayerApiVo playerApiVo = new PlayerApiVo();
        playerApiVo.getSearch().setPlayerId(SessionManager.getUserId());
        PlayerApiListVo playerApiListVo = fundRecord(true);
        infoMap.put("isLogin", SessionManager.getUser() != null);
        infoMap.put("totalBalance", CurrencyTool.formatCurrency(playerApiListVo.getTotalAssets()));
        infoMap.put("walletBalance", CurrencyTool.formatCurrency(playerApiListVo.getUserPlayer().getWalletBalance()));
        infoMap.put("api", getAllAPI(playerApiListVo.getResult()));
        infoMap.put("currency", getCurrencySign(SessionManager.getUser().getDefaultCurrency()));
        return infoMap;
    }

    /**
     * 获取所有api余额，并且翻译名称
     *
     * @return
     */
    public List<Map<String, Object>> getAllAPI(List<PlayerApi> playerApiList) {
         /*翻译api*/
        List<Map<String, Object>> playerApis = new ArrayList<>();
        Map<String, Api> apis = Cache.getApi();
        Map<String, SiteApi> siteApis = Cache.getSiteApi();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Api api;
        SiteApi siteApi;
        for (PlayerApi playerApi : playerApiList) {
            String apiId = String.valueOf(playerApi.getApiId());
            Map<String, Object> playerApiMap = new HashMap<>(3,1f);
            playerApiMap.put("apiName", getApiName(apiI18nMap,siteApiI18nMap,apiId));
            if (playerApi.getMoney() == null) {
                playerApiMap.put("balance", "0.00");
            } else {
                playerApiMap.put("balance", CurrencyTool.formatCurrency(playerApi.getMoney()));
            }
            playerApiMap.put("id", apiId);
            api = apis.get(apiId);
            siteApi = siteApis.get(apiId);
            playerApiMap.put("status", getApiStatus(api, siteApi));
            playerApis.add(playerApiMap);
        }
        return playerApis;
    }

    /**
     * 获取api名称
     *
     * @param apiI18nMap
     * @param siteApiI18nMap
     * @param apiId
     * @return
     */
    private String getApiName(Map<String, ApiI18n> apiI18nMap, Map<String, SiteApiI18n> siteApiI18nMap, String apiId) {
        SiteApiI18n siteApiI18n = siteApiI18nMap.get(apiId);
        String apiName = null;
        if (siteApiI18n != null) {
            apiName = siteApiI18n.getName();
        }
        if (StringTool.isNotBlank(apiName)) {
            return apiName;
        }
        ApiI18n apiI18n = apiI18nMap.get(apiId);
        if (apiI18n != null) {
            apiName = apiI18n.getName();
        }
        return apiName;
    }

    private PlayerApiListVo fundRecord(boolean isRefreshAllApiBalance) {
        PlayerApiListVo playerApiListVo = new PlayerApiListVo();
        playerApiListVo.getSearch().setPlayerId(SessionManager.getUserId());

        if (isRefreshAllApiBalance) {
            //同步余额
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerAllApiBalance();
            playerApiListVo.getSearch().setApiId(null);
        }
        playerApiListVo.setType(ApiQueryTypeEnum.ALL_API.getCode());

        //api余额
        playerApiListVo = ServiceSiteTool.playerApiService().fundRecord(playerApiListVo);
        return playerApiListVo;
    }

    private String getApiStatus(Api api, SiteApi siteApi) {
        if (api != null && siteApi != null && BooleanTool.isTrue(api.getTransferable())) {
            String apiStatus = api.getSystemStatus();
            String siteApiStatus = siteApi.getSystemStatus();
            if (GameStatusEnum.DISABLE.getCode().equals(apiStatus) || GameStatusEnum.DISABLE.getCode().equals(siteApiStatus)) {
                return GameStatusEnum.DISABLE.getCode();
            } else if (GameStatusEnum.MAINTAIN.getCode().equals(apiStatus) || GameStatusEnum.MAINTAIN.getCode().equals(siteApiStatus)) {
                return GameStatusEnum.MAINTAIN.getCode();
            } else {
                return GameStatusEnum.NORMAL.getCode();
            }
        }
        return GameStatusEnum.DISABLE.getCode();
    }

    @RequestMapping(value = "/refreshAllApiBalance")
    @Upgrade(upgrade = true)
    public String refreshAllApiBalance(Model model) {
        PlayerApiListVo playerApiListVo = fundRecord(true);
        model.addAttribute("apis", getAllAPI(playerApiListVo.getResult()));
        return "/transfer/include/api";
    }
}
