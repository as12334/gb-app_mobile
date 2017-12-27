package so.wwb.gamebox.mobile.init;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by tony on 17-7-24.
 */
@Component
public class BaseInterceptor extends HandlerInterceptorAdapter {
    private static final Log LOG = LogFactory.getLog(BaseInterceptor.class);

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            String url = modelAndView.getViewName();
            if (!url.startsWith("redirect:") && !url.startsWith("forward:") /*&& !url.startsWith("/errors/")*/) {
                //全局跳转皮肤
                SysParam param = ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_IS_LOTTERY_SITE);
                if (param != null && param.getParamValue() != null && param.getParamValue().equals("true")) {
                    modelAndView.setViewName("/themes/lottery" + url);
                } else {
                    boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
                    if (isMobileUpgrade && handler instanceof HandlerMethod && isAppUpdate(request)) {
                        HandlerMethod handlerMethod = (HandlerMethod) handler;
                        Upgrade upgrade = handlerMethod.getMethodAnnotation(Upgrade.class);
                        if (upgrade != null && upgrade.upgrade()) {
                            modelAndView.setViewName("/themes/v3" + url);
                        } else {
                            modelAndView.setViewName("/themes/default" + url);
                        }
                    } else {
                        modelAndView.setViewName("/themes/default" + url);
                    }
                    LOG.info("请求链接url{0}跳转viewName:{1}", request.getRequestURI(), modelAndView.getViewName());
                }
            }
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * app 端控制走手机端版本 v2或者v3 默认走v2版本 非app直接按照h5走
     *
     * @param request
     * @return
     */
    private boolean isAppUpdate(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        boolean isApp = false;
        if (StringTool.isNotBlank(userAgent) && (userAgent.contains("app_ios") || userAgent.contains("app_android"))) {
            isApp = true;
        }
        if (!isApp) {
            return true;
        }
        boolean isVersionUpdate = false;
        if (userAgent.contains("app_version") && userAgent.contains("3.0")) {
            isVersionUpdate = true;
        }
        return isVersionUpdate;
    }
    //Method postHandle
}