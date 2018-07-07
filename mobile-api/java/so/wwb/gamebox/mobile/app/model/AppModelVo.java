package so.wwb.gamebox.mobile.app.model;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.model.error.ErrorMessage;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.model.Module;

public class AppModelVo extends ErrorMessage {
    private static final long serialVersionUID = 404144692505585980L;

    private Object data;
    private String version;

    public AppModelVo() {

    }

    public AppModelVo(boolean success, String code, String message) {
        setSuccess(success);
        setCode(code);
        setMessage(message);
        this.version = AppConstant.APP_VERSION;
    }

    public AppModelVo(boolean success, String code, String message, Object data) {
        setSuccess(success);
        setCode(code);
        setMessage(message);
        this.data = data;
        this.version = AppConstant.APP_VERSION;
    }

    public AppModelVo(boolean success, String code, String message, Object data, String version) {
        setSuccess(success);
        setCode(code);
        setMessage(message);
        this.data = data;
        this.version = version;
    }


    @JsonIgnore
    public static String getAppModeVoJson(boolean success, String code, String message, Object data) {
        return JsonTool.toJson(new AppModelVo(success, code, message, data));
    }

    @JsonIgnore
    public static String getAppModeVoJson(boolean success, String code, String message, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(success, code, message, data, version));
    }

    public static String getAppModeVoJsonUseFastjson(boolean success, String code, String message, Object data, String version) {
        return JSONObject.toJSONString(new AppModelVo(success, code, message, data, version));
    }

    @JsonIgnore
    public static String getAppModeVoJson(boolean success, String code, Object data) {
        return JsonTool.toJson(new AppModelVo(success, code, LocaleTool.tranMessage(Module.APP.getCode(), code), data));
    }

    @JsonIgnore
    public static String getAppModeVoJson(AppModelVo appModelVo) {
        return JsonTool.toJson(appModelVo);
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
