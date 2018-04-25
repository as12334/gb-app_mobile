package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.EncodeTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.qrcode.QrcodeDisTool;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceBossTool;
import so.wwb.gamebox.iservice.boss.IAppUpdateService;
import so.wwb.gamebox.iservice.company.site.ISiteAppUpdateService;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.boss.vo.AppUpdateVo;
import so.wwb.gamebox.model.company.site.po.SiteAppUpdate;
import so.wwb.gamebox.model.company.site.vo.SiteAppUpdateVo;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/downLoad")
public class DownLoadController extends BaseDemoController {

    private static final Log LOG = LogFactory.getLog(DownLoadController.class);

    /**
     * 跳转下载手机客户端之前先确认是否登录才显示二维码
     *
     * @return
     */
    @RequestMapping("/downLoadShowQrcode")
    @ResponseBody
    public Map<String, Object> downLoadShowQrcode() {
        Map<String, Object> map = new HashMap<>(2, 1f);
        map.put("showQrCode", ParamTool.isLoginShowQrCode());
        map.put("isLogin", SessionManager.getUserId() != null);
        return map;
    }

    @RequestMapping("/downLoad")
    @Upgrade(upgrade = true)
    public String downLoad(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (ParamTool.isLoginShowQrCode() && SessionManager.getUser() == null) {//是否登录才显示二维码
            String url = "/login/commonLogin.html";
            response.setStatus(302);
            response.setHeader("Location", SessionManagerCommon.getRedirectUrl(request, url));
            return "/passport/login";
        }

        String code = CommonContext.get().getSiteCode();
        IAppUpdateService appUpdateService = ServiceBossTool.appUpdateService();
        ISiteAppUpdateService siteAppUpdateService = ServiceBossTool.siteAppUpdateService();
        getAndroidInfo(model, request, code, appUpdateService, siteAppUpdateService);

        return "/download/DownLoad";
    }

    @RequestMapping("/downLoadIOS")
    @Upgrade(upgrade = true)
    public String downLoadIOS(Model model, HttpServletRequest request) {
        String code = CommonContext.get().getSiteCode();
        IAppUpdateService appUpdateService = ServiceBossTool.appUpdateService();
        ISiteAppUpdateService siteAppUpdateService = ServiceBossTool.siteAppUpdateService();
        getIosInfo(model, code, appUpdateService, siteAppUpdateService);

        return "/download/DownLoadIOS";
    }

    /**
     * 获取android APP信息
     */
    private void getAndroidInfo(Model model, HttpServletRequest request, String code, IAppUpdateService appUpdateService, ISiteAppUpdateService siteAppUpdateService) {
        boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
        String appDomain = fetchAppDownloadDomain(request);
        if (StringTool.isBlank(appDomain)) {
            appDomain = ParamTool.appDmain(request.getServerName());
        }
        if (isMobileUpgrade) {
            Integer siteId = CommonContext.get().getSiteId();
            SiteAppUpdateVo androidVo = new SiteAppUpdateVo();
            androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
            androidVo.getSearch().setSiteId(siteId);
            SiteAppUpdate androidApp = siteAppUpdateService.queryNewApp(androidVo);
            if (androidApp != null) {
                String versionName = androidApp.getVersionName();
                String appUrl = androidApp.getAppUrl();
                fillAndroidInfo(model, code, appDomain, versionName, appUrl);
            }
        } else {
            AppUpdateVo androidVo = new AppUpdateVo();
            androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
            AppUpdate androidApp = appUpdateService.queryNewApp(androidVo);
            if (androidApp != null) {
                String versionName = androidApp.getVersionName();
                fillAndroidInfo(model, code, appDomain, versionName, androidApp.getAppUrl());
            }
        }

    }

    /**
     * 获取参数表中android下载地址
     *
     * @return
     */
    private String getAndroidDownloadUrl() {
        String addressUrl = "";
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.SETTING_ANDROID_DOWNLOAD_ADDRESS);
        if (sysParam != null && StringTool.isNotBlank(sysParam.getParamValue())) {
            addressUrl = sysParam.getParamValue();
        }
        return addressUrl;
    }

    /**
     * 获取参数表中ios下载地址
     *
     * @return
     */
    private String getIosDownloadUrl() {
        String addressUrl = "";
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.SETTING_ISO_DOWNLOAD_ADDRESS);
        if (sysParam != null && StringTool.isNotBlank(sysParam.getParamValue())) {
            addressUrl = sysParam.getParamValue();
        }
        return addressUrl;
    }

    /**
     * 填充ANDROID信息
     *
     * @param model
     * @param code
     * @param appDomain
     * @param versionName
     * @param appUrl
     */
    private void fillAndroidInfo(Model model, String code, String appDomain, String versionName, String appUrl) {
        //获取参数表中下载地址
        String addressUrl = getAndroidDownloadUrl();
        String url;
        if (StringTool.isNotBlank(addressUrl)) {
            url = addressUrl;
        } else {
            url = String.format("https://%s%s%s/app_%s_%s.apk", appDomain, appUrl,
                    versionName, code, versionName);
        }

        model.addAttribute("androidQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
        model.addAttribute("androidUrl", url);
    }

    /**
     * 获取IOS信息
     */
    private void getIosInfo(Model model, String code, IAppUpdateService appUpdateService, ISiteAppUpdateService siteAppUpdateService) {
        boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
        if (isMobileUpgrade) {
            Integer siteId = CommonContext.get().getSiteId();
            SiteAppUpdateVo iosVo = new SiteAppUpdateVo();
            iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
            iosVo.getSearch().setSiteId(siteId);
            SiteAppUpdate iosApp = siteAppUpdateService.queryNewApp(iosVo);
            if (iosApp != null) {
                String versionName = iosApp.getVersionName();
                String appUrl = iosApp.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        } else {
            AppUpdateVo iosVo = new AppUpdateVo();
            iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
            AppUpdate iosApp = appUpdateService.queryNewApp(iosVo);
            if (iosApp != null) {
                String versionName = iosApp.getVersionName();
                String appUrl = iosApp.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        }

    }

    /**
     * 填充IOS信息
     *
     * @param model
     * @param code
     * @param versionName
     * @param appUrl
     */
    private void fillIosInfo(Model model, String code, String versionName, String appUrl) {
        //获取参数表中下载地址
        String addressUrl = getIosDownloadUrl();
        String url;
        if (StringTool.isNotBlank(addressUrl)) {
            url = addressUrl;
        } else {
            url = String.format("itms-services://?action=download-manifest&url=https://%s%s/%s/app_%s_%s.plist", appUrl,
                    versionName, code, code, versionName);
        }

        model.addAttribute("iosQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
        model.addAttribute("iosUrl", url);
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}
