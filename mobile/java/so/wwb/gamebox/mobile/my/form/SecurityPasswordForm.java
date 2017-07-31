package so.wwb.gamebox.mobile.my.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.constraints.Compare;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.commons.validation.form.support.CompareLogic;
import org.soul.web.support.IForm;
import so.wwb.gamebox.model.common.RegExpConstants;
import so.wwb.gamebox.web.privilege.controller.PrivilegeController;

import javax.validation.constraints.Pattern;

/**
 * Created by eagle on 15-11-1.
 */
@Comment("安全密码设置")
public class SecurityPasswordForm implements IForm {

    private String result_permissionPwd;
    private String $confirmPermissionPwd;

    @NotBlank(message = "passport.edit.info.security.notBlank")
    @Pattern(regexp = RegExpConstants.SECURITY_PWD,message = "common.valid.securityPWDFormat")
    @Remote(checkClass = PrivilegeController.class, checkMethod = "checkPrivilegePwd", additionalProperties = {"result.permissionPwd"}, message = "passport.edit.info.security.easy")
    public String getResult_permissionPwd() {
        return result_permissionPwd;
    }

    public void setResult_permissionPwd(String result_permissionPwd) {
        this.result_permissionPwd = result_permissionPwd;
    }
    @NotBlank(message="passport.edit.info.security.again")
    @Compare(logic = CompareLogic.EQ, anotherProperty = "result.permissionPwd",message = "passport.edit.info.security.same")
    public String get$confirmPermissionPwd() {
        return $confirmPermissionPwd;
    }

    public void set$confirmPermissionPwd(String $confirmPermissionPwd) {
        this.$confirmPermissionPwd = $confirmPermissionPwd;
    }
}
