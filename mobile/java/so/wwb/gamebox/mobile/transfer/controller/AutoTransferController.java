package so.wwb.gamebox.mobile.transfer.controller;

import org.soul.commons.enums.SupportTerminal;
import org.soul.commons.lang.SystemTool;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.player.vo.PlayerApiAccountVo;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;

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
        String domain = request.getServerName();
        if (SystemTool.isDebug()) {
            domain = domain + "/mobile";
        }
        playerApiAccountVo.setTransfersUrl(domain + TRANSFERS_URL);
        playerApiAccountVo.setLobbyUrl(domain);
        playerApiAccountVo.setPlatformType(SupportTerminal.PHONE.getCode());
        playerApiAccountVo.setSysUser(SessionManager.getUser());
        return playerApiAccountVo;
    }

    @Override
    public Map doRecovery(PlayerApiVo playerApiVo) {
        playerApiVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        return super.doRecovery(playerApiVo);
    }
}
