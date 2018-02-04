package so.wwb.gamebox.mobile.app.model;

/**
 * Created by ed on 18-1-2.
 */
public class AppRequestModelVo {
    /*app标志*/
    private String terminal;
    /*版本*/
    private String version;
    /*主题*/
    private String theme;
    /*分辨率*/
    private String resolution;
    /*是否原生(原生涉及H5页面跳转)*/
    private boolean is_native;
    /*语言*/
    private String locale;

    public boolean isIs_native() {
        return is_native;
    }

    public void setIs_native(boolean is_native) {
        this.is_native = is_native;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
