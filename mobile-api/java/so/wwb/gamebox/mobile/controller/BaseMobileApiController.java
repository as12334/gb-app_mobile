package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.model.MyUserInfo;
import so.wwb.gamebox.mobile.app.model.UserInfoApp;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ApiGameTool;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.controller.BaseApiServiceController;
import so.wwb.gamebox.web.cache.Cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseMobileApiController extends BaseApiServiceController {
    private Log LOG = LogFactory.getLog(BaseMobileApiController.class);

    public UserInfoApp loadUserInfoApp() {
        MyUserInfo userInfo = new MyUserInfo();
        getUserAssertInfo(userInfo, SessionManager.getUserId());
        UserInfoApp userInfoApp = new UserInfoApp();
        userInfoApp.setApis(userInfo.getApis());
        userInfoApp.setAssets(String.valueOf(userInfo.getTotalAssets()));
        userInfoApp.setBalance(String.valueOf(userInfo.getWalletBalance()));
        userInfoApp.setUsername(SessionManager.getUserName());
        userInfoApp.setCurrSign(getCurrencySign(SessionManager.getUser().getDefaultCurrency()));
        return userInfoApp;
    }

    /**
     * 获取货币标志
     *
     * @param currency
     * @return
     */
    protected String getCurrencySign(String currency) {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(SessionManagerCommon.getUser().getDefaultCurrency());
        if (sysCurrency != null && StringTool.isNotBlank(sysCurrency.getCurrencySign())) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 获取用户资产信息（总资产、钱包余额）
     *
     * @param userInfo
     */
    public void getUserAssertInfo(MyUserInfo userInfo, Integer userId) {
        PlayerApiListVo playerApiListVo = new PlayerApiListVo();
        playerApiListVo.getSearch().setPlayerId(userId);
        try {
            playerApiListVo = ServiceSiteTool.playerApiService().fundRecord(playerApiListVo);
            userInfo.setTotalAssets(playerApiListVo.getTotalAssets());
            userInfo.setWalletBalance(playerApiListVo.getUserPlayer().getWalletBalance());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        List<PlayerApi> playerApis = playerApiListVo.getResult();
        if (CollectionTool.isEmpty(playerApis)) {
            return;
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
            userInfo.addApi(map);
        }
    }

    protected String getApiStatus(Map<String, Api> apiMap, Map<String, SiteApi> siteApiMap, String apiId) {
        Api api = apiMap.get(apiId);
        SiteApi siteApi = siteApiMap.get(apiId);
        String disable = GameStatusEnum.DISABLE.getCode();
        if (api == null || siteApi == null) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.disable");
        }
        String apiStatus = api.getSystemStatus();
        String siteApiStatus = siteApi.getSystemStatus();
        if (disable.equals(apiStatus) || disable.equals(siteApiStatus)) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.disable");
        }
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        if (maintain.equals(apiStatus) || maintain.equals(siteApiStatus)) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.maintain.transferable");
        }
        return "";//app判断空是正常
    }
}
