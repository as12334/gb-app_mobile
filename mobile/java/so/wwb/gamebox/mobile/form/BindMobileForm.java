package so.wwb.gamebox.mobile.form;

import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.AndOr;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.passport.controller.SignUpController;
import so.wwb.gamebox.web.common.controller.VerificationCodeController;

import javax.validation.constraints.Pattern;

/**
 * Created by snake on 18-5-3.
 */
public class BindMobileForm implements IForm {

    //手机号
    private String phone_contactValue;

    //短信验证码
    private String $phoneCode;

    @Depends(message = "Register.phone.notBlank", operator = {Operator.IN, Operator.IS_NOT_EMPTY}, value = {"110", ""}, property = {"$requiredJson", "$checkPhone"}, andOr = AndOr.OR)
    @Pattern(regexp = FormValidRegExps.NUMBER_PHONE, message = "Register.phone.format")
    @Remote(message = "Register.phone.exist", checkClass = SignUpController.class, checkMethod = "checkPhoneExist")
    public String getPhone_contactValue() {
        return phone_contactValue;
    }

    public void setPhone_contactValue(String phone_contactValue) {
        this.phone_contactValue = phone_contactValue;
    }


    @Depends(message = "Register.phoneCode.notBlank", operator = Operator.IS_NOT_EMPTY, property = "$checkPhone")
    @Remote(message = "Register.phoneCode.remote", checkClass = VerificationCodeController.class, checkMethod = "checkPhoneCode", additionalProperties = "phone.contactValue")
    public String get$phoneCode() {
        return $phoneCode;
    }

    public void set$phoneCode(String $phoneCode) {
        this.$phoneCode = $phoneCode;
    }
}
