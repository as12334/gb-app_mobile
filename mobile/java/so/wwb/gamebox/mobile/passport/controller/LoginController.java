package so.wwb.gamebox.mobile.passport.controller;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.session.SessionKey;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.shiro.local.filter.LocalLoginFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录
 *
 * Created by admin on 16-4-14.
 */
@Controller
@RequestMapping("/login")
class LoginController {

    private Log LOG = LogFactory.getLog(LoginController.class);

    private static final String LOGIN_URI = "/passport/login";

    @RequestMapping(value = "/commonLogin")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String commonLogin(Model model, HttpServletRequest request) {
        //如果已经登录
        SysUser sysUser = SessionManager.getUser();
        if (sysUser != null) {
            return "redirect:"+ SessionManagerCommon.getRedirectUrl(request,"/");
        }
        model.addAttribute("isOpenCaptcha", SessionManager.isOpenCaptcha());
        model.addAttribute("apiId", request.getParameter("a"));
        model.addAttribute("gameCode", request.getParameter("c"));
        model.addAttribute("apiTypeId", request.getParameter("t"));
        return LOGIN_URI;
    }

    /**
     * 注册后自动登录，不用验证码
     *
     * @param servletRequest
     * @param servletResponse
     */
    @RequestMapping("/autoLogin")
    public void autoLogin(ServletRequest servletRequest, ServletResponse servletResponse) {
        LocalLoginFilter localLoginFilter = SpringTool.getBean(LocalLoginFilter.class);
        SessionManager.setAttribute(SessionKey.S_IS_CAPTCHA_CODE, false);
        try {
            localLoginFilter.executeLogin(servletRequest, servletResponse);
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
