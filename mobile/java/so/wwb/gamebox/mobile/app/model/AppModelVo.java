package so.wwb.gamebox.mobile.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.data.json.JsonTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;

public class AppModelVo {
    private Integer error;
    private Integer code;
    private String msg;
    private Object data;
    private String version;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    @JsonIgnore
    public static AppModelVo createAppModeVo(Integer error, Integer code, String msg, Object data, String version) {
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setCode(code);
        appModelVo.setError(error);
        appModelVo.setMsg(msg);
        appModelVo.setData(data);
        appModelVo.setVersion(version);
        return appModelVo;
    }

    @JsonIgnore
    public static String getAppModeVoJson(Integer error, Integer code, String msg, Object data, String version) {
        return JsonTool.toJson(createAppModeVo(error, code, msg, data, version));
    }

    public static void main(String[] args) {
        AppErrorCodeEnum[] appErrorCodeEnumList = AppErrorCodeEnum.values();
        for (AppErrorCodeEnum appErrorCodeEnum : appErrorCodeEnumList) {
            System.out.println(appErrorCodeEnum.getMsg() + "=" + appErrorCodeEnum.getMsg());
        }
    }
}
