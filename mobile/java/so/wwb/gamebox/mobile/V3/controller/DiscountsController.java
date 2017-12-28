package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/discounts")
public class DiscountsController extends BaseApiController{

    private static final Log LOG = LogFactory.getLog(DiscountsController.class);
    private static final Integer pageSize = 8;

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model,Integer skip,HttpServletRequest request) {
        model.addAttribute("skip",skip);
        model.addAttribute("messageVo",getActivity(request));
        return "/discounts/Promo";
    }







    @Override
    protected String getDemoIndex() {
        return null;
    }
}
