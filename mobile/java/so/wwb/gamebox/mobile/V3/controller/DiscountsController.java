package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.controller.BaseDiscountsController;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;

import javax.servlet.http.HttpServletRequest;

/**
 */
@Controller
@RequestMapping("/discounts")
public class DiscountsController extends BaseDiscountsController{

    private static final Log LOG = LogFactory.getLog(DiscountsController.class);
    private static final Integer pageSize = 8;

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model,Integer skip,HttpServletRequest request) {
        model.addAttribute("skip",skip);
        model.addAttribute("messageVo",getActivity(request));
        return "/discounts/Promo";
    }






}
