package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.enums.DomainPageUrlEnum;
import so.wwb.gamebox.model.company.site.po.SiteAppUpdate;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/app")
public class AppCheckController {
    private static final Log LOG = LogFactory.getLog(AppCheckController.class);

    @RequestMapping(value = "/update")
    @ResponseBody
    public String appUpdate(HttpServletRequest request) {
        String encryptCode = request.getParameter("code");
        String type = request.getParameter("type");
        String siteId = request.getParameter("siteId");


        SiteAppUpdate app = Cache.getSiteAppUpdate(siteId, type);
        LOG.info("app获取版本号参数:{0},{1},{2},结果:{3}", encryptCode, type, siteId, JsonTool.toJson(app));
        if (app != null && app.getAppType().equals(type)) {
            return JsonTool.toJson(app);
        } else {
            return JsonTool.toJson("");
        }
    }

    /**
     * 域名check  /app/getHost
     */
    @RequestMapping(value = "/getHost")
    @ResponseBody
    public String appHost(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Integer siteId = SessionManager.getSiteId();
        String times = request.getParameter("times");
        int num = StringTool.isBlank(times) ? 0 : Integer.parseInt(times);
        List<VSysSiteDomain> siteDomainIndex = Cache.getSiteDomain(siteId, DomainPageUrlEnum.INDEX.getCode());
        List<VSysSiteDomain> siteDomain = getIndexDomains(siteDomainIndex);
        if (siteDomain == null) {
            LOG.info("app获取域名错误:站点ID={0},获取次数={1}, 缓存DOMAIN_SITE is null", siteId, num);
            map.put("data", null);
            return JsonTool.toJson(map);
        }
        Integer begin = num * 10;
        if (begin > siteDomain.size()) {
            map.put("data", null);
            return JsonTool.toJson(map);
        }
        List<String> domains = new ArrayList<>();
        for (int i = begin; i < begin + 10; i++) {
            if (i < siteDomain.size()) {
                domains.add(siteDomain.get(i).getDomain());
            }
        }
        map.put("data", domains);
        return JsonTool.toJson(map);

    }

    private List<VSysSiteDomain> getIndexDomains(List<VSysSiteDomain> siteDomainIndex) {
        List<VSysSiteDomain> siteDomain = new ArrayList<>();
        //先返回主页域名
        for (VSysSiteDomain domain : siteDomainIndex) {
            if (domain.getAgentId() == null) {
                siteDomain.add(domain);
            }
        }
        //再返回代理域名
        for (VSysSiteDomain domain : siteDomainIndex) {
            if (domain.getAgentId() != null) {
                siteDomain.add(domain);
            }
        }
        return siteDomain;
    }
}
