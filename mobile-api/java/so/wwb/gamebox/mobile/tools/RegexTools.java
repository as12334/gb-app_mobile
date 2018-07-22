package so.wwb.gamebox.mobile.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTools {
    public static String replaceImgSrc(String htmlStr, String domain) {
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = "https://" + domain;
        }
        Pattern p_img = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"].*?>", Pattern.CASE_INSENSITIVE);
        Matcher m_img = p_img.matcher(htmlStr);
        String result = htmlStr;
        while (m_img.find()) {
            String src = m_img.group(1);
            String replaceSrc = "";
            if (src.lastIndexOf(".") > 0) {
                replaceSrc = src.substring(0, src.lastIndexOf(".")) + src.substring(src.lastIndexOf("."));
            }
            if (!src.startsWith("http://") && !src.startsWith("https://")) {
                replaceSrc = domain + replaceSrc;
            }
            result = result.replaceAll(src, replaceSrc);
        }
        return result;
    }
}
