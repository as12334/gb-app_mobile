package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ApiGameTool;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.CurrencyEnum;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;
import so.wwb.gamebox.model.master.player.vo.VUserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by jeff on 2016/1/12.
 */
@Controller
@RequestMapping("/api")
public class ApiController extends BaseApiController {
    private static final Log LOG = LogFactory.getLog(ApiController.class);

    @Value("${site.res}")
    private String freemakerTemplateRootPath;

    @RequestMapping("/login")
    @ResponseBody
    public String login(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        setAccount(playerApiAccountVo, request);

        if (checkApiStatus(playerApiAccountVo)) {
            DemoModelEnum demoModel = SessionManagerCommon.getDemoModelEnum();
            if (demoModel != null) {
                //平台试玩免转不可用
                //纯彩票试玩免转不可用
                playerApiAccountVo.setTrial(true);
                Integer apiId = playerApiAccountVo.getApiId();
                if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(demoModel) && (
                        apiId == Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                                apiId == Integer.valueOf(ApiProviderEnum.DWT.getCode()))) {
                    //模拟账号免转可用
                    playerApiAccountVo.setTrial(false);
                }
            }
            if(ParamTool.apiSeparat()){
                //todo by mical
                playerApiAccountVo = ServiceSiteTool.playerApiAccountService().loginApi(playerApiAccountVo);
            }else{
                playerApiAccountVo = ServiceSiteTool.playerApiAccountService().loginApi(playerApiAccountVo);
            }
        } else {
            playerApiAccountVo.setLoginSuccess(false);
            resultMap.put("maintain", true);
        }

        /* 构造返回的结果 */
        resultMap.put("loginSuccess", playerApiAccountVo.getLoginSuccess());
        resultMap.put("gameApiResult", playerApiAccountVo.getGameApiResult());
        resultMap.put("errMsg", playerApiAccountVo.getErrMsg());

        return JsonTool.toJson(resultMap);
    }

    @RequestMapping("/getSiteApi")
    @ResponseBody
    public Map<String, Object> getSiteAPi(HttpServletRequest request) {
        SysUser user = SessionManager.getUser();
        if (user != null) {
            Map<String, Object> map = new HashMap<>();
            if (SessionManagerCommon.isLotteryDemo()) {
                map.put("username", StringTool.overlayName(user.getUsername()));
            } else {
                getAppUserInfo(request, user, map);
            }
            return map;
        } else {
            return null;
        }
    }

    private void getAppUserInfo(HttpServletRequest request, SysUser user, Map<String, Object> map) {
        Integer userId = user.getId();
        map.put("username", StringTool.overlayName(user.getUsername()));
        map.put("currSign", getCurrencySign(user.getDefaultCurrency()));

        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        listVo = ServiceSiteTool.playerApiService().fundRecord(listVo);
        // API 余额
        map.put("apis", handlePlayerApi(listVo.getResult()));
        Double balance = listVo.getUserPlayer().getWalletBalance();
        map.put("playerWallet", CurrencyTool.formatCurrency(balance == null ? 0.0d : balance));
        map.put("playerAssets", CurrencyTool.formatCurrency(listVo.getTotalAssets()));
    }

    /**
     * 刷新API余额
     */
    @RequestMapping("/refreshApi")
    @ResponseBody
    public Map<String, Object> refreshApi(HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        Integer userId = sysUser.getId();
        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        fetchPlayerApiBalance(listVo);
        listVo = ServiceSiteTool.playerApiService().fundRecord(listVo);
        Map<String, Object> map = new HashMap<>();
        map.put("currSign", getCurrencySign(sysUser.getDefaultCurrency()));
        map.put("apis", handlePlayerApi(listVo.getResult()));
        // 钱包余额
        Double balance = listVo.getUserPlayer().getWalletBalance();
        map.put("playerWallet", CurrencyTool.formatCurrency(balance == null ? 0.0d : balance));
        // 总资产
        map.put("playerAssets", CurrencyTool.formatCurrency(listVo.getTotalAssets()));
        return map;
    }

    private PlayerApiListVo initPlayerApiListVo(Integer userId) {
        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        return listVo;
    }

    /**
     * 查询玩家总资产
     */
    private String queryPlayerAssets(PlayerApiListVo listVo, Integer userId) {
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        double assets = ServiceSiteTool.playerApiService().queryPlayerAssets(listVo);
        return CurrencyTool.formatCurrency(assets);
    }

    private void fetchPlayerApiBalance(PlayerApiListVo listVo) {
        IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
        service.fetchPlayerAllApiBalance();
        listVo.getSearch().setApiId(null);
    }

    private List<Map<String, Object>> handlePlayerApi(List<PlayerApi> playerApis) {
          /* 翻译api */
        List<Map<String, Object>> maps = new ArrayList<>();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        for (PlayerApi playerApi : playerApis) {
            Map<String, Object> map = new HashMap<>(4, 1f);
            String apiId = playerApi.getApiId().toString();
            map.put("apiId", apiId);
            map.put("apiName", ApiGameTool.getSiteApiName(siteApiI18nMap, apiI18nMap, apiId));
            map.put("balance", playerApi.getMoney() == null ? 0 : playerApi.getMoney());
            map.put("status", playerApi.getStatus());
            maps.add(map);
        }
        return maps;
    }


    private List<Map<String, Object>> getSiteApis(PlayerApiListVo listVo, HttpServletRequest request, boolean isFetch) {
        //同步余额
        if (isFetch) {
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerAllApiBalance();
            listVo.getSearch().setApiId(null);
        }
        listVo.setType(ApiQueryTypeEnum.ALL_API.getCode());
        listVo = ServiceSiteTool.playerApiService().fundRecord(listVo);
         /* 翻译api */
        List<Map<String, Object>> maps = new ArrayList<>();
        List<PlayerApi> playerApis = listVo.getResult();
        if (CollectionTool.isEmpty(playerApis)) {
            return maps;
        }
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Map<String, Object> map;
        for (PlayerApi api : playerApis) {
            map = new HashMap<>(4, 1f);
            String apiId = api.getApiId().toString();
            map.put("apiId", apiId);
            map.put("apiName", ApiGameTool.getSiteApiName(siteApiI18nMap, apiI18nMap, apiId));
            map.put("balance", api.getMoney() == null ? 0.00 : api.getMoney());
            map.put("status", getApiStatus(apiMap, siteApiMap, apiId));
            maps.add(map);
        }

        //根据API余额降序 Add by Bruce.QQ
        Collections.sort(maps, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return ((Double) o2.get("balance")).compareTo((Double) o1.get("balance"));
            }
        });
        return maps;
    }

    /**
     * api状态
     *
     * @param apiMap
     * @param siteApiMap
     * @param apiId
     * @return
     */
    protected String getApiStatus(Map<String, Api> apiMap, Map<String, SiteApi> siteApiMap, String apiId) {
        Api api = apiMap.get(apiId);
        SiteApi siteApi = siteApiMap.get(apiId);
        String disable = GameStatusEnum.DISABLE.getCode();
        if (api == null || siteApi == null) {
            return disable;
        }
        String apiStatus = api.getSystemStatus();
        String siteApiStatus = siteApi.getSystemStatus();
        if (disable.equals(apiStatus) || disable.equals(siteApiStatus)) {
            return disable;
        }
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        if (maintain.equals(apiStatus) || maintain.equals(siteApiStatus)) {
            return maintain;
        }
        return GameStatusEnum.NORMAL.getCode();//app判断空是正常
    }

    /**
     * 这段代码要包装成一个独立的类
     */
    private VUserPlayer getPlayer(Integer userId) {
        VUserPlayerVo vo = new VUserPlayerVo();
        vo.getSearch().setId(userId);
        VUserPlayer player = ServiceSiteTool.vUserPlayerService().queryPlayer4App(vo);
        if (player != null) {
            player.setCurrencySign(getCurrencySign(player.getDefaultCurrency()));
        }
        return player;
    }

    /**
     * 获取货币标志
     */
    private String getCurrencySign(String defaultCurrency) {
        defaultCurrency = StringTool.isNotBlank(defaultCurrency) ? defaultCurrency : CurrencyEnum.CNY.getCode();
        SysCurrency currency = Cache.getSysCurrency().get(defaultCurrency);
        return currency == null ? "" : currency.getCurrencySign();
    }

    private List<SiteApi> getSiteApi() {
        Map<String, SiteApi> siteApiMap = CacheBase.getSiteApi();
        List<SiteApi> siteApis = new ArrayList<>();
        Map<String, Api> apiMap = CacheBase.getApi();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        if (MapTool.isNotEmpty(siteApiMap)) {
            for (SiteApi siteApi : siteApiMap.values()) {
                Api api = apiMap.get(String.valueOf(siteApi.getApiId()));
                if (api != null && !disable.equals(api.getSystemStatus()) && !disable.equals(siteApi.getSystemStatus())) {
                    if (maintain.equals(api.getSystemStatus()) || maintain.equals(siteApi.getSystemStatus())) {
                        siteApi.setStatus(maintain);
                    }
                    siteApis.add(siteApi);
                }
            }
        }
        return siteApis;
    }

    /**
     * API详细页
     */
    @RequestMapping("/detail")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String apiDetail(Integer apiId, Integer apiTypeId, Model model) {
        model.addAttribute("apiList", getSiteApiRelation(null));
        model.addAttribute("apiDetail", getApiDetail(apiId, apiTypeId));
        return "/game/Api";
    }

    @RequestMapping("/apiBalance")
    @ResponseBody
    public String getApiBalance(PlayerApiListVo listVo, String isRefresh, HttpServletRequest request) {

        if (StringTool.isNotBlank(isRefresh) && isRefresh.equals("true") && listVo.getSearch() != null && listVo.getSearch().getApiId() != null) {
            //同步玩家api余额
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerApiBalance(listVo);
        }

        Map<String, Object> map = new HashMap<>(2, 1f);
        Integer apiId = listVo.getSearch().getApiId();
        PlayerApi playerApi = getPlayerApi(apiId);
        if (playerApi != null) {
            map.put("apiMoney", CurrencyTool.formatCurrency(playerApi.getMoney()));
        } else {
            map.put("apiMoney", "0.00");
        }
        return JsonTool.toJson(map);
    }

    @RequestMapping("asyncApiDetail")
    @ResponseBody
    private String asyncApiDetail(Integer apiId, Integer apiTypeId) {
        return JsonTool.toJson(getApiDetail(apiId, apiTypeId));
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

    private Map<String, Object> getApiDetail(Integer apiId, Integer apiTypeId) {
        Map<String, Object> apiDetail = new HashMap<>();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        PlayerApi playerApi = getPlayerApi(apiId);
        SysUser user = SessionManager.getUser();
        apiDetail.put("apiI18n", apiI18nMap.get(String.valueOf(apiId)));
        apiDetail.put("apiTypeId", apiTypeId);
        apiDetail.put("currSign", getCurrencySign(user.getDefaultCurrency()));
        if (playerApi != null) {
            apiDetail.put("apiMoney", CurrencyTool.formatCurrency(playerApi.getMoney()));
        } else {
            apiDetail.put("apiMoney", "0.00");
        }
        return apiDetail;
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}
