package so.wwb.gamebox.mobile.session.listener;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.web.shiro.common.delegate.PassportEvent;
import org.soul.web.shiro.common.delegate.PassportListenerAdapter;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;

/**
 * Created by longer on 6/18/15.
 */
public class UserInfoListener extends PassportListenerAdapter {

    private Log log = LogFactory.getLog(UserInfoListener.class);

    @Override
    public void onLoginSuccess(PassportEvent passportEvent) {
        SysUser sysUser = passportEvent.getSysUser();
        log.debug( "移动站点登录成功:{0}-{1}-{2}" ,sysUser.getUserType(),sysUser.getId(),sysUser.getUsername());
        SysUserVo sysUserVo = new SysUserVo();
        UserPlayerVo userPlayerVo=new UserPlayerVo();
        UserPlayer userPlayer=new UserPlayer();
        sysUserVo._setDataSourceId(SessionManager.getSiteParentId());
        sysUserVo.getSearch().setId(SessionManager.getSiteUserId());
        sysUserVo = ServiceTool.sysUserService().get(sysUserVo);
        SessionManager.setMasterInfo(sysUserVo.getResult());
        SessionManager.setIsReminderMsg(true);
        userPlayer.setId(SessionManager.getUserId());
        userPlayer.setChannelTerminal(SessionManager.getAttribute(SessionKey.S_USER_CLINT_INFO).toString());
        userPlayerVo.setResult(userPlayer);
        userPlayerVo.setProperties(UserPlayer.PROP_CHANNEL_TERMINAL);
        ServiceTool.userPlayerService().updateOnly(userPlayerVo);
    }
}
