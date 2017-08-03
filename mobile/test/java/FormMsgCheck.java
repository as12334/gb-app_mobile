import org.soul.commons.io.FileTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.I18nType;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import so.wwb.gamebox.model.common.Const;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Water on 7/31/17.
 */
public class FormMsgCheck {

    static Log log = LogFactory.getLog(FormMsgCheck.class);


    public static void main(String[] args) {
        I18nTool.initI18n(Const.DEFAUTL_LOCALE);
        Map<String, Map<String, String>> stringMapMap = I18nTool.getI18nMap(Const.DEFAUTL_LOCALE).get(I18nType.MESSAGE.getCode());

        File file = new File("/home/longer/workspace/gamebox/app_mobile/i18n.txt");
        List<String> context = FileTool.readLines(file);
        for (String s : context) {
            int dot = s.indexOf(".");
            String module = s.substring(0,dot);
            String key = s.substring(dot+1);
            if (stringMapMap == null || stringMapMap.get(module) == null) {
                log.info("i18n message miss module:{0}",module);
                continue;
            }
            String value = stringMapMap.get(module).get(key);
            if (StringTool.isBlank(value)){
               log.info(s);
            }
        }
    }
}
