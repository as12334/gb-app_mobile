package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.model.security.privilege.vo.SysUserVo;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

/**
 * Created by bruce on 17-4-29.
 */
public abstract class BaseCommonDepositController {

    /**
     * 获取层级
     */
    public PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        return ServiceTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }
}
