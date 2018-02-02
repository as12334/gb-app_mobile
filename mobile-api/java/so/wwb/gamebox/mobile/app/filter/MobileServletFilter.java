package so.wwb.gamebox.mobile.app.filter;


import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Mobile default use ajax request
 */
public class MobileServletFilter implements Filter {

    private static Log LOG = LogFactory.getLog(MobileServletFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        request = new MobileServletWapper(request);
        chain.doFilter(request,servletResponse);

    }

    @Override
    public void destroy() {

    }
}
