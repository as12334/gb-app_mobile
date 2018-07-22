package so.wwb.gamebox.mobile.init;

import org.soul.commons.spring.utils.SpringTool;

/**
 * Created by tony on 15-4-28.
 */
public class ConfigManager extends so.wwb.gamebox.web.init.ConfigBase {

    public static MobileConfigration get() {
        return SpringTool.getBean(MobileConfigration.class);
    }

}
