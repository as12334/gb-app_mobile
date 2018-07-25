package so.wwb.gamebox.mobile.session;

import org.soul.commons.lang.DateTool;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.session.SessionKey;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.init.ConfigBase;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created by tony on 15-4-29.
 */
public class SessionManager extends SessionManagerCommon {
    public static final String SESSION_MASTER_INFO = "SESSION_MASTER_INFO";
    private static final String SESSION_TOKEN = "SESSION_TOKEN_";

    //玩家中心-转账-转账提交时间
    private static final String S_TRANSFER_TIME = "S_TRANSFER_TIME";
    //注册验证邮箱发送时间
    private static final String S_REGISTER_SEND_EMAIL_TIME = "S_REGISTER_SEND_EMAIL_TIME";
    //注册邮箱验证码
    private static final String S_REGISTER_CHECK_EMAIL_CODE = "S_REGISTER_CHECK_EMAIL_CODE";
    /*代理推荐注册－推荐人*/
    private static final String S_AGENT_RECOMMEND_USER_CODE = "S_AGENT_RECOMMEND_USER_CODE";
    /*玩家推荐注册－推荐人*/
    private static final String S_RECOMMEND_USER_CODE = "S_RECOMMEND_USER_CODE";
    /*消息页－发送数量*/
    private static final String S_SEND_MESSAGE_COUNT = "S_SEND_MESSAGE_COUNT";

    /**
     * 站长中心子账号处理
     *
     * @return
     */
    /**
     * 站长中心子账号处理
     *
     * @return
     */
    public static String getSubSysCode() {
        return ConfigBase.get().getSubsysCode();
    }

    /**
     * 获取运营商Logo
     *
     * @return
     */
    public static String getCompanyLogo(HttpServletRequest request) {
        return SessionManager.getSiteDomain(request).getLogoPath();
    }

    public static SysUser getMasterInfo() {
        return (SysUser) getAttribute(SESSION_MASTER_INFO);
    }

    public static void setMasterInfo(SysUser user) {
        setAttribute(SESSION_MASTER_INFO, user);
    }

    /**
     * 登录是否需要验证码
     *
     * @return
     */
    public static boolean isOpenCaptcha() {
        return (Boolean) (getAttribute(SessionKey.S_IS_CAPTCHA_CODE) == null ? false : getAttribute(SessionKey.S_IS_CAPTCHA_CODE));
    }

    public static void setToken(String token) {
        setAttribute("S_SESSION_TOKEN", token);
    }

    public synchronized static String getToken(String code) {
        return (String) getAttribute(SESSION_TOKEN);
    }

    public static Date getTransferTime() {
        return (Date) getAttribute(S_TRANSFER_TIME);
    }

    /**
     * 玩家中心-转账-转账提交时间
     *
     * @param date
     */
    public static void setTransferTime(Date date) {
        setAttribute(S_TRANSFER_TIME, date);
    }

    /**
     * 消息页－发送消息数量(玩家咨询)
     *
     * @param count
     */
    public static void setsSendMessageCount(Integer count) {
        setAttribute(S_SEND_MESSAGE_COUNT, count);
    }

    public static Integer getSendMessageCount() {
        return (Integer) getAttribute(S_SEND_MESSAGE_COUNT);
    }

    /**
     * 是否允许发送注册邮件
     * @return
     */
    public static boolean canSendRegisterEmail() {
        Date lastSendTime = (Date) getAttribute(S_REGISTER_SEND_EMAIL_TIME);
        return lastSendTime == null || DateTool.addSeconds(lastSendTime, Const.REGISTER_SEND_EMAIL_INTERVAL_SECONDS).getTime() < new Date().getTime();
    }

    public static Map<String, String> getCheckRegisterEmailInfo() {
        return (Map) getAttribute(S_REGISTER_CHECK_EMAIL_CODE);
    }

    /**
     * 验证邮件注册码
     *
     * @param code
     */
    public static void setCheckRegisterEmailInfo(Map<String, String> code) {
        setAttribute(S_REGISTER_CHECK_EMAIL_CODE, code);
    }

    /**
     * 代理推荐
     *
     * @return
     */
    public static String getAgentRecommendUserCode() {
        return (String) getAttribute(S_AGENT_RECOMMEND_USER_CODE);
    }

    public static void setAgentRecommendUserCode(String code) {
        setAttribute(S_AGENT_RECOMMEND_USER_CODE, code);
    }

    public static String getRecommendUserCode() {
        return (String) getAttribute(S_RECOMMEND_USER_CODE);
    }

    public static void setRecommendUserCode(String code) {
        setAttribute(S_RECOMMEND_USER_CODE, code);
    }

    /*public static void setToken(String s, String code, Integer id) {
    }

    public static CharSequence getToken(String code, Integer id) {
        return null;
    }*/
}
