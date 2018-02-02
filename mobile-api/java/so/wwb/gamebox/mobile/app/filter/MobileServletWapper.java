package so.wwb.gamebox.mobile.app.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MobileServletWapper extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public MobileServletWapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        if ("Soul-Requested-With".equals(name)) {
            return "XMLHttpRequest";
        }
        return super.getHeader(name);
    }
}
