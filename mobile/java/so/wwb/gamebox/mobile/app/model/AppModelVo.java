package so.wwb.gamebox.mobile.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.data.json.JsonTool;

import java.io.Serializable;

public class AppModelVo implements Serializable {
    private static final long serialVersionUID = 8257995679263616666L;

    private Integer error;
    private Integer code;
    private String msg;
    private Object data;
    private String version;

    public AppModelVo(Integer error, Integer code, String msg, Object data, String version) {
        this.error = error;
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.version = version;
    }


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
    public static String getAppModeVoJson(Integer error, Integer code, String msg, Object data, String version) {
        return JsonTool.toJson(new AppModelVo(error, code, msg, data, version));
    }
}
