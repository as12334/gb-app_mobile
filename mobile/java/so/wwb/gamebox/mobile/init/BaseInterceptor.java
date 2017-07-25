package so.wwb.gamebox.mobile.init;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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
            if (!url.startsWith("redirect:") && !url.startsWith("forward:") && !url.startsWith("/errors/"))            //过滤重定向
                //全局跳转皮肤
                modelAndView.setViewName("/themes/lottery"+url);
        }
        super.postHandle(request, response, handler, modelAndView);
    }//Method postHandle
}