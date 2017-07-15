package so.wwb.gamebox.mobile.wallet.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.wallet.controller.BankCardController;

import javax.validation.constraints.Pattern;

/**
 * Created by eagle on 15-11-1.
 */
@Comment("绑定银行卡")
public class BankcardForm implements IForm {

    private String realName;
    private String result_bankcardNumber;
    private String result_bankDeposit;
    private String result_bankName;

    @NotBlank(message = "请输入真实姓名")
    @Length(min = 2,max = 30,message = "请输入2-30个字符(由汉字和大小写英文字母组成)")
    @Pattern(regexp = FormValidRegExps.REALNAME,message = "格式不正确，请重新输入")
    @Comment("真实姓名")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @NotBlank(message = "请输入10-25位数字")
    @Length(min = 10,max = 25,message = "请输入10-25位数字")
    @Pattern(message = "银行卡格式错误",regexp = FormValidRegExps.BANK)
    @Remote(checkClass = BankCardController.class,checkMethod = "checkCardIsExists",additionalProperties = {"result.bankcardNumber"}, message = "银行卡已存在,请更换")
    @Comment("银行卡")
    public String getResult_bankcardNumber() {
        return result_bankcardNumber;
    }

    public void setResult_bankcardNumber(String result_bankcardNumber) {
        this.result_bankcardNumber = result_bankcardNumber;
    }

    @Pattern(regexp = FormValidRegExps.REALNAME,message = "请输入2—20个大小写英文字母及汉字")
    @Length(min = 2,max = 20,message = "请输入2—20个大小写英文字母及汉字")
    @Comment("开户行")
    public String getResult_bankDeposit() {
        return result_bankDeposit;
    }

    public void setResult_bankDeposit(String result_bankDeposit) {
        this.result_bankDeposit = result_bankDeposit;
    }

    @NotBlank(message = "请选择银行")
//    @Depends(property = "",operator = Operator.EQ,value = {"0"},jsValueExp ="$(\"[name=\\'realNameStatus\\']\").val()",message = "")
    @Comment("银行")
    public String getResult_bankName() {
        return result_bankName;
    }

    public void setResult_bankName(String result_bankName) {
        this.result_bankName = result_bankName;
    }
}
