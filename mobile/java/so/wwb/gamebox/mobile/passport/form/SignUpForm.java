package so.wwb.gamebox.mobile.passport.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Compare;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.AndOr;
import org.soul.commons.validation.form.support.CompareLogic;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.passport.controller.SignUpController;
import so.wwb.gamebox.web.common.controller.VerificationCodeController;
import so.wwb.gamebox.web.validate.controller.ValidateController;

import javax.validation.constraints.Pattern;

/**
 * Created by jessie on 16-7-20.
 */
public class SignUpForm implements IForm {
    private String sysUser_defaultTimezone;
    private String sysUser_defaultCurrency;
    private String sysUser_defaultLocale;
    private String sysUser_username;
    private String sysUser_password;
    private String $confirmPassword;
    private String sysUser_permissionPwd;
    private String $confirmPermissionPwd;
    private String sysUser_sex;
    private String sysUser_realName;
    private String sysUser_birthday;

    /*安全问题*/
    private String sysUserProtection_question1;
    private String sysUserProtection_answer1;

    /*联系方式*/
    private String qq_contactValue;
    private String email_contactValue;
    private String phone_contactValue;
    private String weixin_contactValue;

    /*验证码*/
    private String $captchaCode;
    /*服务条款*/
    private String $termsOfService;
    /*邮件验证码*/
    private String $emailCode;
    private String $phoneCode;
    /*介绍人*/
    private String $recommendUserInputCode;

    @NotBlank(message = "Register.username.notBlank")
    @Pattern(regexp = FormValidRegExps.ACCOUNT, message = "Register.username.format")
    @Remote(message = "Register.username.exist", checkMethod = "checkUserNameExist", checkClass = SignUpController.class, additionalProperties = {"$editType"})
    public String getSysUser_username() {
        return sysUser_username;
    }

    public void setSysUser_username(String sysUser_username) {
        this.sysUser_username = sysUser_username;
    }

    @NotBlank(message = "Register.password.notBlank")
    @Pattern(message = "Register.password.format", regexp = FormValidRegExps.LOGIN_PWD)
    @Remote(message = "Register.password.remote", checkClass = ValidateController.class, checkMethod = "passwordNotWeak", additionalProperties = "sysUser.username")
    public String getSysUser_password() {
        return sysUser_password;
    }

    public void setSysUser_password(String sysUser_password) {
        this.sysUser_password = sysUser_password;
    }

    @NotBlank(message = "Register.confirmPassword.notBlank")
    @Compare(message = "Register.confirmPassword.notEqWithPassword", anotherProperty = "sysUser.password", logic = CompareLogic.EQ)
    public String get$confirmPassword() {
        return $confirmPassword;
    }

    public void set$confirmPassword(String $confirmPassword) {
        this.$confirmPassword = $confirmPassword;
    }

    @Depends(message = "Register.defaultTimezone.notBlank", operator = Operator.IN, value = "defaultTimezone", property = "$requiredJson")
    public String getSysUser_defaultTimezone() {
        return sysUser_defaultTimezone;
    }

    public void setSysUser_defaultTimezone(String sysUser_defaultTimezone) {
        this.sysUser_defaultTimezone = sysUser_defaultTimezone;
    }

    @Depends(message = "Register.defaultCurrency.notBlank", operator = Operator.IN, value = "mainCurrency", property = "$requiredJson")
    public String getSysUser_defaultCurrency() {
        return sysUser_defaultCurrency;
    }

    public void setSysUser_defaultCurrency(String sysUser_defaultCurrency) {
        this.sysUser_defaultCurrency = sysUser_defaultCurrency;
    }

    @Depends(message = "Register.defaultLocale.notBlank", operator = Operator.IN, value = "defaultLocale", property = "$requiredJson")
    public String getSysUser_defaultLocale() {
        return sysUser_defaultLocale;
    }

    public void setSysUser_defaultLocale(String sysUser_defaultLocale) {
        this.sysUser_defaultLocale = sysUser_defaultLocale;
    }

    @Depends(message = "Register.permissionPwd.noBlank", operator = Operator.IN, value = "paymentPassword", property = "$requiredJson")
    @Pattern(message = "Register.permissionPwd.format", regexp = FormValidRegExps.SECURITY_PWD)
    @Remote(message = "Register.permissionPwd.remote", checkClass = ValidateController.class, checkMethod = "paymentPasswordNotWeak", additionalProperties = "sysUser.username")
    public String getSysUser_permissionPwd() {
        return sysUser_permissionPwd;
    }

    public void setSysUser_permissionPwd(String sysUser_permissionPwd) {
        this.sysUser_permissionPwd = sysUser_permissionPwd;
    }

    @Depends(message = "Register.confirmPermissionPwd.notBlank", operator = Operator.IN, value = "paymentPassword", property = "$requiredJson")
    @Compare(message = "Register.confirmPermissionPwd.notEqWithPermissionPwd", anotherProperty = "sysUser.permissionPwd", logic = CompareLogic.EQ)
    public String get$confirmPermissionPwd() {
        return $confirmPermissionPwd;
    }

    public void set$confirmPermissionPwd(String $confirmPermissionPwd) {
        this.$confirmPermissionPwd = $confirmPermissionPwd;
    }

    @Depends(message = "Register.qq.notBlank", operator = Operator.IN, value = "301", property = "$requiredJson")
    @Pattern(regexp = FormValidRegExps.QQ, message = "Register.qq.format")
    @Remote(message = "Register.qq.exist", checkClass = SignUpController.class, checkMethod = "checkQqExist")
    public String getQq_contactValue() {
        return qq_contactValue;
    }

    public void setQq_contactValue(String qq_contactValue) {
        this.qq_contactValue = qq_contactValue;
    }

    @Depends(message = "Register.phone.notBlank", operator = {Operator.IN, Operator.IS_NOT_EMPTY}, value = {"110", ""}, property = {"$requiredJson", "$checkPhone"}, andOr = AndOr.OR)
    @Pattern(regexp = FormValidRegExps.NUMBER_PHONE, message = "Register.phone.format")
    @Remote(message = "Register.phone.exist", checkClass = SignUpController.class, checkMethod = "checkPhoneExist")
    public String getPhone_contactValue() {
        return phone_contactValue;
    }

    public void setPhone_contactValue(String phone_contactValue) {
        this.phone_contactValue = phone_contactValue;
    }

    @Pattern(regexp = FormValidRegExps.EMAIL, message = "Register.email.format")
    @Depends(message = "Register.email.notBlank", operator = {Operator.IN, Operator.IS_NOT_EMPTY}, value = {"201", ""}, property = {"$requiredJson", "$checkEmail"}, andOr = AndOr.OR)
    @Remote(message = "Register.mail.exist", checkClass = SignUpController.class, checkMethod = "checkMailExist")
    public String getEmail_contactValue() {
        return email_contactValue;
    }

    public void setEmail_contactValue(String email_contactValue) {
        this.email_contactValue = email_contactValue;
    }

    @Depends(message = "Register.weixin.notBlank", operator = Operator.IN, value = "304", property = "$requiredJson")
    @Length(min = 2, max = 20, message = "Register.weixin.format")
    @Remote(message = "Register.weixin.exist", checkClass = SignUpController.class, checkMethod = "checkWeixinExist")
    public String getWeixin_contactValue() {
        return weixin_contactValue;
    }

    public void setWeixin_contactValue(String weixin_contactValue) {
        this.weixin_contactValue = weixin_contactValue;
    }

    @Depends(message = "Register.sex.notBlank", operator = Operator.IN, value = "sex", property = "$requiredJson")
    public String getSysUser_sex() {
        return sysUser_sex;
    }

    public void setSysUser_sex(String sysUser_sex) {
        this.sysUser_sex = sysUser_sex;
    }

    @Depends(message = "Register.realname.notBlank", operator = Operator.IN, value = "realName", property = "$requiredJson")
    @Pattern(regexp = FormValidRegExps.NAME_D, message = "Register.realname.format")
    @Remote(message = "Register.realName.exist", checkMethod = "checkRealNameExist", checkClass = SignUpController.class)
    public String getSysUser_realName() {
        return sysUser_realName;
    }

    public void setSysUser_realName(String sysUser_realName) {
        this.sysUser_realName = sysUser_realName;
    }

    @Depends(message = "Register.question.notBlank", operator = Operator.IN, value = "securityIssues", property = "$requiredJson")
    public String getSysUserProtection_question1() {
        return sysUserProtection_question1;
    }

    public void setSysUserProtection_question1(String sysUserProtection_question1) {
        this.sysUserProtection_question1 = sysUserProtection_question1;
    }

    @Pattern(regexp = FormValidRegExps.ANSWER, message = "Register.answer.format")
    @Depends(message = "Register.answer.notBlank", operator = {Operator.IN, Operator.IS_NOT_EMPTY}, value = {"securityIssues", ""}, property = {"$requiredJson", "sysUserProtection_question1"}, andOr = AndOr.OR)
    public String getSysUserProtection_answer1() {
        return sysUserProtection_answer1;
    }

    public void setSysUserProtection_answer1(String sysUserProtection_answer1) {
        this.sysUserProtection_answer1 = sysUserProtection_answer1;
    }

    @NotBlank(message = "Register.captchaCode.notBlank")
    @Remote(message = "Register.captchaCode.notRight", checkClass = SignUpController.class, checkMethod = "checkedCaptcha", additionalProperties = {"$editType"})
    public String get$captchaCode() {
        return $captchaCode;
    }

    public void set$captchaCode(String $captchaCode) {
        this.$captchaCode = $captchaCode;
    }

    @Depends(message = "Register.termsOfService.notBlank", operator = Operator.IS_NOT_EMPTY, property = "$requiredJson")
    public String get$termsOfService() {
        return $termsOfService;
    }

    public void set$termsOfService(String $termsOfService) {
        this.$termsOfService = $termsOfService;
    }

    @Depends(message = "Register.birthday.notBlank", operator = Operator.IN, value = "birthday", property = "$requiredJson")
    public String getSysUser_birthday() {
        return sysUser_birthday;
    }

    public void setSysUser_birthday(String sysUser_birthday) {
        this.sysUser_birthday = sysUser_birthday;
    }

    @Depends(message = "Register.emailCode.notBlank", operator = Operator.IS_NOT_EMPTY, property = "$checkEmail")
    @Remote(message = "Register.emailCode.remote", checkClass = SignUpController.class, checkMethod = "checkEmailCode", additionalProperties = "email.contactValue")
    public String get$emailCode() {
        return $emailCode;
    }

    public void set$emailCode(String $emailCode) {
        this.$emailCode = $emailCode;
    }

    @Depends(message = "Register.phoneCode.notBlank", operator = Operator.IS_NOT_EMPTY, property = "$checkPhone")
    @Remote(message = "Register.phoneCode.remote", checkClass = VerificationCodeController.class, checkMethod = "checkPhoneCode", additionalProperties = "phone.contactValue")
    public String get$phoneCode() {
        return $phoneCode;
    }

    public void set$phoneCode(String $phoneCode) {
        this.$phoneCode = $phoneCode;
    }

    @Depends(message = "Register.recomendUser.notBlank", operator = Operator.IN, value = "regCode", property = "$requiredJson")
    @Pattern(regexp = FormValidRegExps.REC_CODE, message = "Register.recomendUser.format")
    public String get$recommendUserInputCode() {
        return $recommendUserInputCode;
    }

    public void set$recommendUserInputCode(String $recommendUserInputCode) {
        this.$recommendUserInputCode = $recommendUserInputCode;
    }
}
