package so.wwb.gamebox.mobile.init;

import org.soul.commons.lang.string.StringTool;
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
                }
            }
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * app 端控制走手机端版本 v2或者v3 默认走v2版本
     *
     * @param request
     * @return
     */
    private boolean isAppUpdate(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String appVersion = request.getHeader("app_version");
        boolean isApp = false;
        boolean isVersionUpdate = false;
        if (StringTool.isNotBlank(userAgent) && (userAgent.contains("app_ios") || userAgent.contains("app_android"))) {
            isApp = true;
        }
        if ("v3.0".equals(appVersion)) {
            isVersionUpdate = true;
        }
        return isVersionUpdate && isApp;
    }
    //Method postHandle
}