package so.wwb.gamebox.mobile.tools;

import so.wwb.gamebox.model.enums.OSTypeEnum;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * Created by fei on 17-10-9.
 */
public class OsTool {
    public static String getOsInfo(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        if (agent.contains("iPhone") || agent.contains("iPad")) {
            return OSTypeEnum.IOS.getCode();
        } else if (agent.contains("Android")) {
            return OSTypeEnum.ANDROID.getCode();
        } else {
            return OSTypeEnum.PC.getCode();
        }
    }
}
