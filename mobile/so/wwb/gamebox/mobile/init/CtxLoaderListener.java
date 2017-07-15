package so.wwb.gamebox.mobile.init;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import so.wwb.gamebox.web.init.CommonCtxLoaderListener;

import javax.servlet.ServletContext;

/**
 * Created by Kevice on 2015/3/23 0023.
 */
public class CtxLoaderListener extends CommonCtxLoaderListener {

    @Override
    protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
        super.customizeContext(sc, wac);
        ContextPath="";
    }

}
