package so.wwb.gamebox.mobile.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.data.json.JsonTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;

import java.io.Serializable;

public class AppModelVo implements Serializable{

    private static final long serialVersionUID = 404144692505585980L;
    private String code;
    private String message;
    private Object data;
    private String version;

    public AppModelVo() {

    }

    public AppModelVo(String code, String message) {
        this.code = code;
        this.message = message;
        this.version = AppConstant.APP_VERSION;
    }

    public AppModelVo(String code, String message,Object data, String version) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.version = AppConstant.APP_VERSION;
    }

    /**
     * @deprecated
     * @param error
     * @param code
     * @param message
     * @param data
     * @param version
     */
    public AppModelVo(Integer error, String code, String message, Object data, String version) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.version = version;
    }


    @JsonIgnore
    public static String getAppModeVoJson(String code, String message, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(code, message, data, version));
    }

    /**
     * @deprecated
     * @param error
     * @param code
     * @param data
     * @param
     * @see #getAppModeVoJson(AppModelVo)
     * @see #AppModelVo(String, String)
     * @see #getAppModeVoJson(String, String, Object, String)
     */
    @JsonIgnore
    public static String getAppModeVoJson(Integer error, String code, String msg, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(error, code, msg, data, version));
    }

    public static String getAppModeVoJson(AppModelVo appModelVo) {
        return JsonTool.toJson(appModelVo);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
