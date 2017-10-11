package so.wwb.gamebox.mobile.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.enums.SupportTerminal;
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
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.GameSupportTerminalEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.setting.vo.GameVo;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.VSiteApi;
import so.wwb.gamebox.model.company.site.vo.VSiteApiListVo;
import so.wwb.gamebox.model.company.site.vo.VSiteApiVo;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.master.enums.CurrencyEnum;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;
import so.wwb.gamebox.model.master.player.vo.VUserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.tools.ServiceTool.playerApiService;
import static so.wwb.gamebox.mobile.tools.ServiceTool.vSiteApiService;

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
            playerApiAccountVo = ServiceTool.playerApiAccountService().loginApi(playerApiAccountVo);
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

    private void setAccount(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        Integer apiId = playerApiAccountVo.getApiId();

        StringBuilder domain = new StringBuilder();
        domain.append(request.getServerName());
        if (!domain.toString().contains("http")) {
            domain.insert(0, "http://");
        }

        String transferUrl = domain + "/transfer/index.html"
                + "?apiId=" + apiId
                + "&apiTypeId=" + playerApiAccountVo.getApiTypeId();
        playerApiAccountVo.setTransfersUrl(transferUrl);

        playerApiAccountVo.setLobbyUrl(domain.toString());
        if (request.getHeader("User-Agent").contains("app_android")) {
            playerApiAccountVo.setLobbyUrl("javascript:window.gb.finish()");
        }

        playerApiAccountVo.setSysUser(SessionManager.getUser());
        if (StringTool.isNotBlank(playerApiAccountVo.getGameCode())) {
            GameVo gameVo = new GameVo();
            gameVo.getSearch().setApiId(apiId);
            gameVo.getSearch().setCode(playerApiAccountVo.getGameCode());
            gameVo.getSearch().setSupportTerminal(GameSupportTerminalEnum.PHONE.getCode());
            gameVo = ServiceTool.gameService().search(gameVo);
            if (gameVo.getResult() != null) {
                playerApiAccountVo.setGameId(gameVo.getResult().getId());
                playerApiAccountVo.setPlatformType(gameVo.getResult().getSupportTerminal());
            }
        }
        playerApiAccountVo.setPlatformType(SupportTerminal.PHONE.getCode());
    }

    private boolean checkApiStatus(PlayerApiAccountVo playerApiAccountVo) {
        Integer apiId = playerApiAccountVo.getApiId();
        if (apiId == null)
            return false;
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api = apiMap.get(apiId.toString());
        SiteApi siteApi = siteApiMap.get(apiId.toString());
        if (api == null || siteApi == null) {
            return false;
        }
        return isAllowToLogin(api, siteApi);
    }

    private boolean isAllowToLogin(Api api, SiteApi siteApi) {
        if (GameStatusEnum.DISABLE.getCode().equals(api.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(api.getSystemStatus()))
            return false;
        return !(GameStatusEnum.DISABLE.getCode().equals(siteApi.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(siteApi.getSystemStatus()));
    }

    @RequestMapping("/getSiteApi")
    @ResponseBody
    public Map<String, Object> getSiteAPi(HttpServletRequest request) {
        SysUser user = SessionManager.getUser();
        if(user != null) {
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
        PlayerApiListVo listVo = initPlayerApiListVo(user.getId());
        VUserPlayer player = getPlayer(user.getId());
        // API 余额
        map.put("apis", getSiteApis(listVo, request, false));
        if (player != null) {
            map.put("username", StringTool.overlayName(player.getUsername()));
            map.put("currSign", player.getCurrencySign());
            // 钱包余额
            Double balance = player.getWalletBalance();
            map.put("playerWallet", CurrencyTool.formatCurrency(balance == null ? 0.0d : balance));
        }
        // 总资产
        map.put("playerAssets", queryPlayerAssets(listVo, user.getId()));
    }

    /**
     * 刷新API余额
     */
    @RequestMapping("/refreshApi")
    @ResponseBody
    public Map<String, Object> refreshApi(HttpServletRequest request) {
        Integer userId = SessionManager.getUserId();
        PlayerApiListVo listVo = initPlayerApiListVo(userId);
        Map<String, Object> map = new HashMap<>();
        VUserPlayer player = getPlayer(userId);
        map.put("currSign", player.getCurrencySign());
        map.put("apis", getSiteApis(listVo, request, true));
        // 钱包余额
        map.put("playerWallet", CurrencyTool.formatCurrency(player.getWalletBalance()));
        // 总资产
        map.put("playerAssets", queryPlayerAssets(listVo, userId));
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
        double assets = playerApiService().queryPlayerAssets(listVo);
        return CurrencyTool.formatCurrency(assets);
    }

    private List<Map<String, Object>> getSiteApis(PlayerApiListVo listVo, HttpServletRequest request, boolean isFetch) {
        //同步余额
        if (isFetch) {
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerAllApiBalance();
            listVo.getSearch().setApiId(null);
        }
        listVo.setType(ApiQueryTypeEnum.ALL_API.getCode());
        listVo = ServiceTool.playerApiService().fundRecord(listVo);
         /* 翻译api */
        List<Map<String, Object>> maps = new ArrayList<>();
        List<VSiteApi> apis = getSiteAPi();
        for (VSiteApi siteApi : apis) {
            for (PlayerApi api : listVo.getResult()) {
                if (siteApi.getApiId().intValue() == api.getApiId().intValue()) {
                    Map<String, Object> map = new HashMap<>();

                    String apiId = api.getApiId().toString();
                    map.put("apiId", apiId);
                    map.put("apiName", CacheBase.getSiteApiName(apiId));
                    map.put("balance", api.getMoney() == null ? 0 : api.getMoney());
                    map.put("status", siteApi.getApiRealStatus());

                    maps.add(map);
                }
            }
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
     * 这段代码要包装成一个独立的类
     */
    private VUserPlayer getPlayer(Integer userId) {
        VUserPlayerVo vo = new VUserPlayerVo();
        vo.getSearch().setId(userId);
        VUserPlayer player = ServiceTool.vUserPlayerService().queryPlayer4App(vo);
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

    /**
     * 站点API
     */
    private List<VSiteApi> getSiteAPi() {
        VSiteApiListVo listVo = new VSiteApiListVo();
        listVo.getSearch().setStatus(GameStatusEnum.DISABLE.getCode());
        listVo.getSearch().setSiteId(SessionManager.getSiteId());
        listVo.getSearch().setLocale(SessionManager.getSiteLocale().toString());
        listVo = vSiteApiService().queryAllSiteApi(listVo);
        return listVo.getResult();
    }

    /**
     * API详细页
     */
    @RequestMapping("/detail")
    @Token(generate = true)
    public String apiDetail(Integer apiId, Integer apiTypeId, Model model) {
        model.addAttribute("apiList", getApiType());
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

        Map<String, Object> map = new HashMap<>(2,1f);
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
        playerApiVo = ServiceTool.playerApiService().search(playerApiVo);
        return playerApiVo.getResult();
    }

    private String getApiStatus(Integer apiId) {
        VSiteApiVo vSiteApiVo = new VSiteApiVo();
        vSiteApiVo.getSearch().setSiteId(SessionManager.getSiteId());
        vSiteApiVo.getSearch().setLocale(SessionManager.getSiteLocale().toString());
        vSiteApiVo.getSearch().setApiId(apiId);
        return vSiteApiService().queryOneApiStatus(vSiteApiVo);
    }

    private Map<String, Object> getApiDetail(Integer apiId, Integer apiTypeId) {
        Map<String, Object> apiDetail = new HashMap<>();
        String apiStatus = getApiStatus(apiId);
        if (apiStatus != null && getApiStatus(apiId).equals("normal")) {
            Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
            for (ApiI18n apiI18n : apiI18nMap.values()) {
                if (apiI18n.getApiId().equals(apiId)) {
                    apiDetail.put("apiI18n", apiI18n);
                    apiDetail.put("apiTypeId", apiTypeId);
                }
            }
        }
        return apiDetail;
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}