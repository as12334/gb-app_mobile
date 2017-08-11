import org.soul.commons.lang.SystemTool;
import org.soul.model.gameapi.base.PlatformTypeEnum;
import org.springframework.stereotype.Controller;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by aaa on 17-8-10.
 */

@Controller
public class DemoController extends so.wwb.gamebox.web.msites.controller.DemoController {

    public PlayerApiAccountVo doLogin(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        StringBuilder domain = new StringBuilder();
        domain.append(request.getServerName());
        if (!domain.toString().contains("http")) {
            domain.insert(0, "http://");
        }
        playerApiAccountVo.setLobbyUrl(domain.toString());
        if (request.getHeader("User-Agent").contains("app_android")) {
            playerApiAccountVo.setLobbyUrl("javascript:window.gb.finish()");
        }
        playerApiAccountVo.setPlatformType(PlatformTypeEnum.mobile.getCode());
        return playerApiAccountVo;
    }
}
