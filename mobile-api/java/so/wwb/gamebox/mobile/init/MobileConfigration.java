package so.wwb.gamebox.mobile.init;

import org.soul.web.init.BaseCtxLoaderListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.web.init.ExtBaseWebConf;

/**
 * Created by Kevice on 2015/3/26 0026.
 */
@Component
public class MobileConfigration extends ExtBaseWebConf {
    @Value("${freemaker.template.root.path}")
    private String freemakerTemplateRootPath;
    @Value("${pcenter.context}")
    private String pcentercontext;
    @Value("${freemaker.template.server.path}")
    private String freemakerTemplateServerPath;

    public String getFreemakerTemplateRootPath() {
        return freemakerTemplateRootPath.replace("{site.context.path}", BaseCtxLoaderListener.ContextPath);
    }

    public String getFreemakerTemplateServerPath() {
        return freemakerTemplateServerPath;
    }

    public String getPcentercontext() {
        return pcentercontext;
    }

    public void setPcentercontext(String pcentercontext) {
        this.pcentercontext = pcentercontext;
    }
}
