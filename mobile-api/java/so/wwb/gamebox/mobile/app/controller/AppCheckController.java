package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.AesTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.common.dubbo.ServiceBossTool;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.common.BoxTypeEnum;
import so.wwb.gamebox.model.company.site.po.SiteAppUpdate;
import so.wwb.gamebox.model.company.site.vo.SiteAppUpdateVo;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.GeneralSecurityException;

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


}
