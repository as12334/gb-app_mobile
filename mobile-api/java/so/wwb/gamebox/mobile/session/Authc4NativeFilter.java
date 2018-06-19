package so.wwb.gamebox.mobile.session;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by longer on 7/6/15.
 */
public class Authc4NativeFilter extends AuthenticationFilter {

    private static final Log LOG = LogFactory.getLog(Authc4NativeFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String rs = AppModelVo.getAppModeVoJson(false, AppErrorCodeEnum.UN_LOGIN.getCode(), null);
        response.setContentType("application/json");
        response.getWriter().write(rs);
        return false;
    }
}
