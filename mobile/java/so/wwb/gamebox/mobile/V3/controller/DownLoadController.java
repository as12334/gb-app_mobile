package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.EncodeTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.qrcode.QrcodeDisTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.iservice.boss.IAppUpdateService;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.tools.OsTool;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.boss.vo.AppUpdateVo;
import so.wwb.gamebox.model.enums.OSTypeEnum;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 */
@Controller
@RequestMapping("/downLoad")
public class DownLoadController {

    private static final Log LOG = LogFactory.getLog(DownLoadController.class);

    @RequestMapping("/downLoad")
    @Upgrade(upgrade = true)
    public String downLoad(Model model,HttpServletRequest request) {
        getAppPath(model,request);
        return "/download/DownLoad";
    }

    @RequestMapping("/downLoadIOS")
    @Upgrade(upgrade = true)
    public String downLoadIOS(Model model,HttpServletRequest request) {
        getAppPath(model,request);
        return "/download/DownLoadIOS";
    }

    /**
     * 获取APP下载地址
     */
    private void getAppPath(Model model, HttpServletRequest request) {
        //获取站点信息
        String code = CommonContext.get().getSiteCode();
        IAppUpdateService appUpdateService = ServiceTool.appUpdateService();

        String os = OsTool.getOsInfo(request);
        if (OSTypeEnum.IOS.getCode().equals(os)) {
            // 获取IOS信息
            getIosInfo(model, code, appUpdateService);
        } else if (OSTypeEnum.ANDROID.getCode().equals(os)) {
            // 获取android APP信息
            getAndroidInfo(model, request, code, appUpdateService);
        } else {
            getIosInfo(model, code, appUpdateService);
            getAndroidInfo(model, request, code, appUpdateService);
        }
    }

    /**
     * 获取android APP信息
     */
    private void getAndroidInfo(Model model, HttpServletRequest request, String code, IAppUpdateService appUpdateService) {
        AppUpdateVo androidVo = new AppUpdateVo();
        androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
        AppUpdate androidApp = appUpdateService.queryNewApp(androidVo);
        if (androidApp != null) {
            String versionName = androidApp.getVersionName();
            String url = String.format("https://%s%s%s/app_%s_%s.apk", ParamTool.appDmain(request.getServerName()), androidApp.getAppUrl(),
                    versionName, code, versionName);
            model.addAttribute("androidQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
            model.addAttribute("androidUrl", url);
        }
    }

    /**
     * 获取IOS信息
     */
    private void getIosInfo(Model model, String code, IAppUpdateService appUpdateService) {
        AppUpdateVo iosVo = new AppUpdateVo();
        iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
        AppUpdate iosApp = appUpdateService.queryNewApp(iosVo);
        if (iosApp != null) {
            String versionName = iosApp.getVersionName();
            String url = String.format("itms-services://?action=download-manifest&url=https://%s%s/%s/app_%s_%s.plist", iosApp.getAppUrl(),
                    versionName, code, code, versionName);
            model.addAttribute("iosQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
            model.addAttribute("iosUrl", url);
        }
    }


}
