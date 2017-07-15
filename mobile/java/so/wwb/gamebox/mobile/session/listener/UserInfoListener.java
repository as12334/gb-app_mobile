package so.wwb.gamebox.mobile.session.listener;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.shiro.common.delegate.PassportEvent;
import org.soul.web.shiro.common.delegate.PassportListenerAdapter;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;

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
        sysUserVo._setDataSourceId(SessionManager.getSiteParentId());
        sysUserVo.getSearch().setId(SessionManager.getSiteUserId());
        sysUserVo = ServiceTool.sysUserService().get(sysUserVo);
        SessionManager.setMasterInfo(sysUserVo.getResult());
        SessionManager.setIsReminderMsg(true);
    }
}
