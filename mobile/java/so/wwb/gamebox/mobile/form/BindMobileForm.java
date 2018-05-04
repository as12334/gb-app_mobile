package so.wwb.gamebox.mobile.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.AndOr;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.controller.HelpCenterController;
import so.wwb.gamebox.mobile.passport.controller.SignUpController;
import so.wwb.gamebox.web.common.controller.VerificationCodeController;

import javax.validation.constraints.Pattern;

/**
 * Created by snake on 18-5-3.
 */
public class BindMobileForm implements IForm {

    //手机号
    private String search_contactValue;

    //短信验证码
    private String $phoneCode;

    @NotBlank(message = "Register.phone.notBlank")
    @Pattern(regexp = FormValidRegExps.NUMBER_PHONE, message = "Register.phone.format")
    @Remote(message = "Register.phone.exist", checkClass = HelpCenterController.class, checkMethod = "checkPhoneExist")
    public String getSearch_contactValue() {
        return search_contactValue;
    }

    public void setSearch_contactValue(String phone_contactValue) {
        this.search_contactValue = phone_contactValue;
    }


    @NotBlank(message = "Register.phoneCode.notBlank")
    @Remote(message = "Register.phoneCode.remote", checkClass = HelpCenterController.class, checkMethod = "checkPhoneCode", additionalProperties = "search.contactValue")
    public String get$phoneCode() {
        return $phoneCode;
    }

    public void set$phoneCode(String $phoneCode) {
        this.$phoneCode = $phoneCode;
    }

}
