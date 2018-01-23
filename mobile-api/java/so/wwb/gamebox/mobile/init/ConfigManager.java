package so.wwb.gamebox.mobile.init;

import org.soul.commons.spring.utils.SpringTool;
import so.wwb.gamebox.web.init.ExtBaseConfigManager;

/**
 * Created by tony on 15-4-28.
 */
public class ConfigManager extends ExtBaseConfigManager {

    public static MobileConfigration getConfigration() {
        return SpringTool.getBean(MobileConfigration.class);
    }

}
