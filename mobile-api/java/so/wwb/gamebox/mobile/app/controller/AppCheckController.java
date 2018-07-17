package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.AesTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceBossTool;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.common.BoxTypeEnum;
import so.wwb.gamebox.model.company.site.po.SiteAppUpdate;
import so.wwb.gamebox.model.company.site.vo.SiteAppUpdateVo;

import javax.servlet.http.HttpServletRequest;

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
        String ischress = request.getParameter("ischress");
        SiteAppUpdateVo vo = new SiteAppUpdateVo();
        vo.getSearch().setAppType(type);
        vo.getSearch().setSiteId(Integer.parseInt(siteId));
        if (StringTool.isNotBlank(ischress) && "1".equals(ischress)) {
            vo.getSearch().setBoxType(BoxTypeEnum.CB.getCode());
        } else {
            vo.getSearch().setBoxType(BoxTypeEnum.GB.getCode());
        }
        SiteAppUpdate app = ServiceBossTool.siteAppUpdateService().queryNewApp(vo);
        LOG.info("app获取版本号参数:{0},{1},{2},结果:{3}", encryptCode, type, siteId, JsonTool.toJson(app));
        int code = 0;
        try {
            code = Integer.valueOf(AesTool.decrypt(encryptCode, AppUpdate.KEY_UPDATE));
        } catch (Exception gse) {
            LOG.error(gse.getLocalizedMessage(), gse);
        }
        if (app != null && app.getAppType().equals(type) && app.getVersionCode() > code) {
            return JsonTool.toJson(app);
        } else {
            return JsonTool.toJson("");
        }
    }
}
