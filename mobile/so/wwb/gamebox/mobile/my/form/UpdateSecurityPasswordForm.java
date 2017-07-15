package so.wwb.gamebox.mobile.my.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Compare;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.commons.validation.form.support.CompareLogic;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.my.controller.UpdateSecurityPasswordController;

/**
 * Created by eagle on 15-11-1.
 */
@Comment("修改安全密码设置")
public class UpdateSecurityPasswordForm implements IForm {

    private String privilegePwd;
    private String confirmPrivilegePwd;
    private String oldPrivilegePwd;

    private String code;

    private String flag;

    //真实姓名没有设置,此处为什么会出现两个realName 是因为远程验证不支持多个message
    private String realName;
    //有设置真实姓名
    private String realName2;

    @NotBlank(message = "请输入安全密码")
    @Length(max = 6)
//    @Pattern(message = "common.valid.securityPWDFormat",regexp = FormValidRegExps.SECURITY_PWD)
//    @Remote(checkClass = PasswordController.class, checkMethod = "checkPrivilegePassword", additionalProperties = {"oldPrivilegePwd"}, message = "安全密码错误")
    @Comment("旧安全密码")
    public String getOldPrivilegePwd() {
        return oldPrivilegePwd;
    }

    public void setOldPrivilegePwd(String oldPrivilegePwd) {
        this.oldPrivilegePwd = oldPrivilegePwd;
    }

    @NotBlank(message = "请输入安全密码")
    @Length(max = 6)
//    @Pattern(message = "common.valid.securityPWDFormat",regexp = FormValidRegExps.SECURITY_PWD)
    @Remote(checkClass = UpdateSecurityPasswordController.class, checkMethod = "checkPrivilegePwd", additionalProperties = {"privilegePwd"}, message = "当前密码过于简单,请重新输入")
    @Comment("新安全密码")
    public String getPrivilegePwd() {
        return privilegePwd;
    }

    public void setPrivilegePwd(String privilegePwd) {
        this.privilegePwd = privilegePwd;
    }

    @NotBlank(message = "请再次输入安全密码")
    @Length(max = 6)
    @Compare(message = "两次密码不一致", logic = CompareLogic.EQ, anotherProperty = "privilegePwd")
    @Comment("确认新安全密码")
    public String getConfirmPrivilegePwd() {
        return confirmPrivilegePwd;
    }

    public void setConfirmPrivilegePwd(String confirmPrivilegePwd) {
        this.confirmPrivilegePwd = confirmPrivilegePwd;
    }

    @NotBlank(message = "请输入真实姓名")
    @Remote(checkClass = UpdateSecurityPasswordController.class,checkMethod = "checkRealName",additionalProperties = {"realName"},message = "您暂未设置真实姓名,请先设置!")
    @Comment("真实姓名")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @NotBlank(message = "请输入真实姓名")
    @Remote(checkClass = UpdateSecurityPasswordController.class,checkMethod = "checkRealName2",additionalProperties = {"realName2"},message = "输入错误,验证失败!")
    @Comment("真实姓名")
    public String getRealName2() {
        return realName2;
    }

    public void setRealName2(String realName2) {
        this.realName2 = realName2;
    }

    @Depends(property = "flag",operator = Operator.EQ,value = {"true"},jsValueExp ="$(\"[name=\\'flag\\']\").val()=='true'",message = "请输入验证码")
    @Remote(checkClass = UpdateSecurityPasswordController.class, checkMethod = "checkValiCode", additionalProperties = {"code"}, message = "验证码错误")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
