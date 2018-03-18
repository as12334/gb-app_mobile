package so.wwb.gamebox.mobile.init;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.support.CommonConf;
import org.soul.watcher.zookeeper.NotifyTool;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import so.wwb.gamebox.web.init.CommonCtxLoaderListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Created by Kevice on 2015/3/23 0023.
 */
public class CtxLoaderListener extends CommonCtxLoaderListener {
    private static final Log LOG = LogFactory.getLog(CtxLoaderListener.class);
    @Override
    protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
        super.customizeContext(sc, wac);
        PassportContextPath="";
    }
    @Override
    public void contextInitialized(ServletContextEvent event) {

        super.contextInitialized(event);
        if (!NotifyTool.isInited()) {
            LOG.debug("Mobile-Api-Context上下文启动失败...");
            super.stopService();
        }else {
            CommonConf.isStoped = false;
            LOG.debug("Mobile-Api-Context上下文启动完成...");
        }
    }
}
