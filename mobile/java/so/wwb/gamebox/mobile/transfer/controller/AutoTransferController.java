package so.wwb.gamebox.mobile.transfer.controller;

import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.lang.SystemTool;
import org.soul.commons.net.ServletTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;
import so.wwb.gamebox.web.SessionManagerCommon;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by cherry on 17-3-6.
 */
@Controller
@RequestMapping("/transfer/auto")
public class AutoTransferController extends so.wwb.gamebox.web.fund.controller.AutoTransferController {
    private String TRANSFERS_URL = "/transfer/index.html";

    @Override
    public PlayerApiAccountVo doLogin(PlayerApiAccountVo playerApiAccountVo, HttpServletRequest request) {
        String fullAddress = ServletTool.getDomainFullAddress(request);
        playerApiAccountVo.setTransfersUrl(fullAddress + TRANSFERS_URL);
        playerApiAccountVo.setLobbyUrl(fullAddress);
        playerApiAccountVo.setPlatformType(SessionManagerCommon.getTerminal(request));
        playerApiAccountVo.setSysUser(SessionManager.getUser());
        return playerApiAccountVo;
    }

    @Override
    public Map doRecovery(PlayerApiVo playerApiVo,HttpServletRequest request) {
        playerApiVo.setOrigin(SessionManagerCommon.getTerminal(request));
        return super.doRecovery(playerApiVo,request);
    }
}
