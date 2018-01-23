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
