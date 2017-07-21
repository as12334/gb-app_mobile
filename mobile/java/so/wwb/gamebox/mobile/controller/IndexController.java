package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dubbo.DubboTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.EncodeTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.qrcode.QrcodeDisTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.iservice.boss.IAppUpdateService;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.boss.vo.AppUpdateVo;
import so.wwb.gamebox.model.company.enums.DomainPageUrlEnum;
import so.wwb.gamebox.model.company.site.po.SiteApiType;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.company.sys.po.SysSite;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttDocumentEnum;
import so.wwb.gamebox.model.master.content.po.CttAnnouncement;
import so.wwb.gamebox.model.master.content.po.CttCarouselI18n;
import so.wwb.gamebox.model.master.content.po.CttDocumentI18n;
import so.wwb.gamebox.model.master.content.vo.CttDocumentI18nListVo;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 首页 Controller
 * Created by jeffrey on 16-7-15.
 */
@Controller
public class IndexController extends BaseApiController {
    private Log LOG = LogFactory.getLog(IndexController.class);

    @RequestMapping("/game")
    public String game(Integer typeId, Model model,HttpServletRequest request) {
        List<SiteApiType> apiTypes = getApiTypes();
        if (typeId == null || !NumberTool.isNumber(String.valueOf(typeId))) {
            if (CollectionTool.isNotEmpty(apiTypes)) {
                // 取出第一个API-TYPE
                typeId = apiTypes.get(0).getApiTypeId();
            } else {
                // 没有，默认取真人
                typeId = ApiTypeEnum.LIVE_DEALER.getCode();
            }
        }
        if(typeId == 5)
            model.addAttribute("channel", "activity");
        else
            model.addAttribute("channel", "game");
        model.addAttribute("apiTypeId", typeId);
        model.addAttribute("apiTypes", apiTypes);
        model.addAttribute("carousels", getCarousel());
        model.addAttribute("announcement", getAnnouncement());
        model.addAttribute("sysUser", SessionManager.getUser());
        model.addAttribute("sysDomain",getSiteDomain(request));
        return "/game/Game";
    }

    @RequestMapping("/index")
    public String toIndex(HttpServletRequest request) {
        String c = request.getParameter("c");
        if (StringTool.isNotBlank(c)) {
            SessionManager.setRecommendUserCode(c);
        }
        return "/ToIndex";
    }

    @RequestMapping("/mainIndex")
    public String index(Model model,HttpServletRequest request) {
        model.addAttribute("channel", "index");
        model.addAttribute("apiTypes", getApiTypes());
        model.addAttribute("announcement", getAnnouncement());
        model.addAttribute("sysUser", SessionManager.getUser());
        model.addAttribute("sysDomain",getSiteDomain(request));
        model.addAttribute("code", CommonContext.get().getSiteCode());
        return "/Index";
    }

    private List<SiteApiType> getApiTypes() {
        Criteria siteId = Criteria.add(SiteApiType.PROP_SITE_ID, Operator.EQ, SessionManager.getSiteId());
        return CollectionQueryTool.query(Cache.getSiteApiType().values(), siteId, Order.asc(SiteApiType.PROP_ORDER_NUM));
    }

    /** 查询Banner */
    private List<Map> getCarousel() {
        Map<String, Map> carousels = (Map) Cache.getSiteCarousel();
        List<Map> resultList = new ArrayList<>();
        if (carousels != null) {
            for (Map m : carousels.values()) {
                if (CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode().equals(m.get("type"))) {
                    if (StringTool.equals(m.get(CttCarouselI18n.PROP_LANGUAGE).toString(), SessionManager.getLocale().toString())) {
                        //验证比对缓存结果中的起止时间是否过期
                        if (((Date) m.get("start_time")).before(new Date()) && ((Date) m.get("end_time")).after(new Date())) {
                            resultList.add(m);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /** 查询公告 */
    private List<CttAnnouncement> getAnnouncement() {
        Map<String, CttAnnouncement> announcement = Cache.getSiteAnnouncement();
        List<CttAnnouncement> resultList = new ArrayList<>();
        if (announcement != null) {
            for (CttAnnouncement an : announcement.values()) {
                if (StringTool.equals(an.getAnnouncementType(), CttAnnouncementTypeEnum.SITE_ANNOUNCEMENT.getCode())
                        && StringTool.equals(an.getLanguage(), SessionManager.getLocale().toString())) {
                    resultList.add(an);
                }
            }
        }
        return resultList;
    }

    /** 关于/条款等 */
    @RequestMapping("/index/{path}")
    public String staticPage(@PathVariable("path") String path, Model model, HttpServletRequest request) {
        if ("agent".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("agent&cooperation");
            model.addAttribute("agents", ServiceTool.cttDocumentI18nService().queryAgentDocument(listVo));
        } else if ("about".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("aboutUs");
            CttDocumentI18n cttDocumentI18n = ServiceTool.cttDocumentI18nService().queryAboutDocument(listVo);
            cttDocumentI18n.setContent(cttDocumentI18n.getContent().replaceAll("\\$\\{weburl\\}", request.getServerName()));
            model.addAttribute("about", cttDocumentI18n);
        } else if ("terms".equals(path) || "protocol".equals(path)) {
            SiteI18n terms = Cache.getSiteI18n(SiteI18nEnum.MASTER_SERVICE_TERMS).get(SessionManager.getLocale().toString());
            model.addAttribute("terms", terms);
        }
        return "/index/" + path;
    }

    private CttDocumentI18nListVo initDocument(String code) {
        CttDocumentI18nListVo listVo = new CttDocumentI18nListVo();
        listVo.getSearch().setStatus(CttDocumentEnum.ON.getCode());
        listVo.getSearch().setCode(code);
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        return listVo;
    }

    @RequestMapping("/index/getCustomerService")
    @ResponseBody
    public String getCustomerService() {
        return SiteCustomerServiceHelper.getMobileCustomerServiceUrl();
    }

    @RequestMapping("/index/gotoCustomerService")
    public void gotoCustomerService(HttpServletResponse response) {
        try {
            response.sendRedirect(SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /** 用户所在时区时间 */
    @RequestMapping(value = "/index/getUserTimeZoneDate")
    @ResponseBody
    public String getUserTimeZoneDate() {
        Map<String, String> map = new HashMap<>(2);
        map.put("dateTimeFromat", CommonContext.getDateFormat().getDAY_SECOND());
        map.put("dateTime", SessionManager.getUserDate(CommonContext.getDateFormat().getDAY_SECOND()));
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("displayName", SessionManager.getTimeZone().getID());
        return JsonTool.toJson(map);
    }

    static String getSiteDomain(HttpServletRequest request){
        //125的记忆域名特殊处理
        if(SessionManager.getAttribute("SESSION_DEFAULTSITE")!=null){
            return SessionManager.getAttribute("SESSION_DEFAULTSITE").toString();
        }
        String defaultSite="";
        if(CommonContext.get().getSiteId()==125){
            SysSite site=Cache.getSysSite().get(CommonContext.get().getSiteId().toString());
            defaultSite= site.getWebSite();
        }else {
            //其他的都是取默认域名
            for (VSysSiteDomain o : Cache.getSiteDomain().values()) {
                if (o.getSiteId().intValue() == CommonContext.get().getSiteId()) {
                    if (o.getPageUrl() != null && o.getIsDefault() != null && o.getPageUrl().equals(DomainPageUrlEnum.INDEX.getCode()) && o.getIsDefault()) {
                        defaultSite = o.getDomain();
                        break;
                    }
                }
            }
        }
        if(StringTool.isBlank(defaultSite)){
            //未设置的取当前域名
            defaultSite = SessionManager.getDomain(request);
        }
        SessionManager.setAttribute("SESSION_DEFAULTSITE",defaultSite);
        return defaultSite;
    }

    @RequestMapping("/index/getLogoUrl")
    @ResponseBody
    public String getLogoUrl() {
        Map<String,String> urlMap = new HashMap<>(2);
        urlMap.put("logoUrl", "/ftl/commonPage/images/app_logo/app_logo_"+ SessionManager.getSiteId() +".png");
        urlMap.put("iconUrl", "/ftl/commonPage/images/app_icon/app_icon_"+ SessionManager.getSiteId() +".png");
        return JsonTool.toJson(urlMap);
    }

    @RequestMapping("/index/getAppUrl")
    @ResponseBody
    public String getAppUrl(ServletRequest request) {
        Integer siteId = SessionManager.getSiteId();
        String code = "";
        for(SysSite sysSite : Cache.getSysSite().values()){
            if(siteId.equals(sysSite.getId())){
                code = sysSite.getCode();
            }
        }
        AppUpdateVo vo = new AppUpdateVo();
        vo.getSearch().setAppType("android");
        IAppUpdateService appUpdateService = DubboTool.getService(IAppUpdateService.class);
        AppUpdate app = appUpdateService.queryNewApp(vo);

        return "/android/" + code + "/app_" + code + "_" +
                app.getVersionName() + ".apk";
    }

    @RequestMapping("/index/getBanner")
    public String getBanner(Model model) {
        model.addAttribute("carousels", getCarousel());
        model.addAttribute("announcement", getAnnouncement());
        return "/game/include/include.banner";
    }

    @RequestMapping("/index/getApiType")
    public String getApiType(Model model) {
        model.addAttribute("apiList", getApiType());
        return "/game/include/include.api";
    }

    /**
     * app下载页
     * 获取下载地址/二维码
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/app/download")
    public String downloadApp(Model model, ServletRequest request){
        //获取站点信息
        Integer siteId = SessionManager.getSiteId();
        String code = Cache.getSysSite().get(siteId.toString()).getCode();
        Cache.getSysSite().get(siteId.toString());

        //获取android APP信息
        AppUpdateVo androidVo = new AppUpdateVo();
        androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
        IAppUpdateService appUpdateService = DubboTool.getService(IAppUpdateService.class);
        AppUpdate androidApp = appUpdateService.queryNewApp(androidVo);

        AppUpdateVo iosVo = new AppUpdateVo();
        iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
        AppUpdate iosApp = appUpdateService.queryNewApp(iosVo);

        String androidUrl = "";
        if(androidApp != null)
            androidUrl = "http://" + request.getServerName() + androidApp.getAppUrl() + androidApp.getVersionName() + "/app_" + code + "_" + androidApp.getVersionName() + ".apk";

        String iosUrl = "";
        if(iosApp != null)
            iosUrl = "itms-services://?action=download-manifest&url=" +
                    "https://" + iosApp.getAppUrl() + "/" + code + "/app_" + code +"_" + iosApp.getVersionName() + ".plist";

        model.addAttribute("androidQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(androidUrl, 6)));
        model.addAttribute("iosQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(iosUrl, 6)));
        model.addAttribute("androidUrl", androidUrl);
        model.addAttribute("iosUrl", iosUrl);
        return "/app/Index";
    }

    /**
     * 查询用户信息,判断是否登录
     * @return
     */
    @RequestMapping("/sysUser")
    @ResponseBody
    public String getSysUser(){
        SysUser sysUser = SessionManager.getUser();
        if(sysUser != null){
            return JsonTool.toJson(sysUser);
        }else {
            return "unLogin";
        }
    }
}