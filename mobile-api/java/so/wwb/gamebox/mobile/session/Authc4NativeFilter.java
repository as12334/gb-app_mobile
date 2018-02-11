package so.wwb.gamebox.mobile.session;

import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.error.ErrorCodeEnum;
import org.soul.web.init.BaseCtxLoaderListener;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.soul.commons.init.context.Const.CUSTOM_HEADER_STATUS;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

/**
 * Created by longer on 7/6/15.
 */
public class Authc4NativeFilter extends AuthenticationFilter {

    private static final Log LOG = LogFactory.getLog(Authc4NativeFilter.class);

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String rs = AppModelVo.getAppModeVoJson(false,
                AppErrorCodeEnum.UN_LOGIN.getCode(),
                AppErrorCodeEnum.UN_LOGIN.getMsg(),
                null, APP_VERSION);
        response.setContentType("application/json");
        response.getWriter().write(rs);
        return false;
    }
}
