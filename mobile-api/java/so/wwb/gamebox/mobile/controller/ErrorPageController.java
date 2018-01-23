package so.wwb.gamebox.mobile.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by fei on 17-3-21.
 */
@Controller
@RequestMapping("/commonPage")
public class ErrorPageController {
    @RequestMapping("/error")
    public String error() {
        return "/errors/error";
    }

    @RequestMapping("/e404")
    public String e404() {
        return "/errors/404";
    }

    @RequestMapping("/unnet")
    public String unnet() {
        return "/errors/unnet";
    }
}
