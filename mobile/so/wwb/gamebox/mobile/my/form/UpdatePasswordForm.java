package so.wwb.gamebox.mobile.my.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.constraints.Compare;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.commons.validation.form.support.CompareLogic;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.my.controller.UpdatePasswordController;

import javax.validation.constraints.Pattern;

/**
 * Created by eagle on 15-11-1.
 */
@Comment("玩家中心账号设置密码修改")
public class UpdatePasswordForm implements IForm {

    private String password;
    private String newPassword;
    private String newRePassword;

    @NotBlank(message = "passport.edit.info.password.notBlank")
    @Pattern(message = "common.valid.loginPWDFormat",regexp = FormValidRegExps.LOGIN_PWD)
    @Remote(checkClass = UpdatePasswordController.class, checkMethod = "checkWeakPassword", additionalProperties = {"newPassword"}, message = "passport.edit.info.security.easy")
//    @Compare(anotherProperty = "password",logic = CompareLogic.NE,message = "新密码不能和旧密码相同,请重新填写")
    @Comment("新密码")
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @NotBlank(message = "passport.edit.info.password.notBlank")
    @Compare(message = "passport.edit.info.password.same", logic = CompareLogic.EQ, anotherProperty = "newPassword")
    @Comment("确认密码")
    public String getNewRePassword() {
        return newRePassword;
    }

    public void setNewRePassword(String newRePassword) {
        this.newRePassword = newRePassword;
    }

    @NotBlank(message = "passport.edit.info.password.notBlank")
//    @Remote(checkClass = PasswordController.class, checkMethod = "checkPassword", additionalProperties = {"password"}, message = "密码错误")
    @Comment("旧密码")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
