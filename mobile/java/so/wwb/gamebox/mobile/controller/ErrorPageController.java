package so.wwb.gamebox.mobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

/**
 * Created by fei on 17-3-21.
 */
@Controller
@RequestMapping("/commonPage")
public class ErrorPageController {
    @RequestMapping("/error")
    @Upgrade(upgrade = true)
    public String error(Model model) {
        model.addAttribute("customerServiceUrl", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
        return "/errors/error";
    }

    @RequestMapping("/e404")
    @Upgrade(upgrade = true)
    public String e404(Model model) {
        model.addAttribute("customerServiceUrl", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
        return "/errors/404";
    }

    @RequestMapping("/unnet")
    public String unnet() {
        return "/errors/unnet";
    }
}
