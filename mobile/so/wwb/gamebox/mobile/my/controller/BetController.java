package so.wwb.gamebox.mobile.my.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 投注记录
 * Created by Fei on 16-7-28.
 */
@Controller
@RequestMapping("/bet")
public class BetController {

    private static final String UNSETTLED = "/my/bet/Unsettled";

    /**
     * 未结算投注
     */
    @RequestMapping("/unsettled")
    public String unsettled(Model model) {
        return UNSETTLED;
    }

}
