package so.wwb.gamebox.mobile.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.data.json.JsonTool;
import so.wwb.gamebox.mobile.app.constant.AppConstant;

import java.io.Serializable;

public class AppModelVo implements Serializable{

    private Integer code;
    private String message;
    private Object data;
    private String version;

    public AppModelVo() {

    }

    public AppModelVo(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.version = AppConstant.APP_VERSION;
    }

    public AppModelVo(Integer code, String message,Object data, String version) {
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
    public AppModelVo(Integer error, Integer code, String message, Object data, String version) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.version = version;
    }


    @JsonIgnore
    public static String getAppModeVoJson(Integer code, String message, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(code, message, data, version));
    }

    /**
     * @deprecated
     * @param error
     * @param code
     * @param data
     * @param
     * @see #getAppModeVoJson(AppModelVo)
     * @see #AppModelVo(Integer, String)
     * @see #getAppModeVoJson(Integer, String, Object, String)
     */
    @JsonIgnore
    public static String getAppModeVoJson(Integer error, Integer code, String msg, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(error, code, msg, data, version));
    }

    public static String getAppModeVoJson(AppModelVo appModelVo) {
        return JsonTool.toJson(appModelVo);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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
