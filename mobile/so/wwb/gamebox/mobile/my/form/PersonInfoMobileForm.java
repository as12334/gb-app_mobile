package so.wwb.gamebox.mobile.my.form;

import org.hibernate.validator.constraints.Length;
import org.soul.commons.validation.form.constraints.Qq;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Pattern;

/**
 * Created by eagle on 15-11-1.
 */
@Comment("玩家中心个人信息编辑页面表单验证")
public class PersonInfoMobileForm implements IForm {

    private String result_realName;
    private String $phone_contactValue;
    private String $email_contactValue;
    private String $weixin_contactValue;
    private String $qq_contactValue;
//    private String $skype_contactValue;

    @Length(min = 2,max = 30,message = "passport.edit.info.realName")
    @Pattern(regexp = FormValidRegExps.REALNAME,message = "passport.edit.info.name.format.error")
    @Comment("姓名")
    public String getResult_realName() {
        return result_realName;
    }

    public void setResult_realName(String result_realName) {
        this.result_realName = result_realName;
    }

    @Qq(message = "passport.edit.info.qq")
//    @Length(min = 5,max = 11,message = "请输入5~11位0-9的纯数字")
    @Comment("qq")
    public String get$qq_contactValue() {
        return $qq_contactValue;
    }

    public void set$qq_contactValue(String $qq_contactValue) {
        this.$qq_contactValue = $qq_contactValue;
    }

    @Pattern(message = "passport.edit.info.phone.format.error",regexp = FormValidRegExps.NUMBER_PHONE)
    @Comment("手机号码")
    public String get$phone_contactValue() {
        return $phone_contactValue;
    }

    public void set$phone_contactValue(String $phone_contactValue) {
        this.$phone_contactValue = $phone_contactValue;
    }

    @Pattern(message = "passport.edit.info.email.format.error",regexp = FormValidRegExps.EMAIL)
    @Comment("邮箱")
    public String get$email_contactValue() {
        return $email_contactValue;
    }

    public void set$email_contactValue(String $email_contactValue) {
        this.$email_contactValue = $email_contactValue;
    }

    @Length(min = 2,max = 20,message = "passport.edit.info.weixin.format.error")
    @Comment("微信")
    public String get$weixin_contactValue() {
        return $weixin_contactValue;
    }

    public void set$weixin_contactValue(String $weixin_contactValue) {
        this.$weixin_contactValue = $weixin_contactValue;
    }

    /*@Pattern(message = "请输入6-32个字符(由大小写英文字母、特殊符号和数字组成)",regexp = FormValidRegExps.SKYPE)
    @Comment("Skype")
    public String get$skype_contactValue() {
        return $skype_contactValue;
    }

    public void set$skype_contactValue(String $skype_contactValue) {
        this.$skype_contactValue = $skype_contactValue;
    }*/
}
