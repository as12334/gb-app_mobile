package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;


/**
 * 线上支付表单验证
 */
@Comment("线上支付表单验证")
public class DepositForm implements IForm {

    //region your codes
    private String result_rechargeAmount;
    private String result_payAccountId;
    private String depositWay;
//    private String $code;

    @NotBlank(message = "rechargeForm.rechargeAmountNotBlank")
    @Pattern(message = "rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = OnlineDepositController.class, checkMethod = "checkAmount", additionalProperties = {"result.payerBank"})
    @Max(message = "rechargeForm.rechargeAmountMax", value = 99999999)
    @Min(message = "rechargeForm.rechargeAmountMin", value = 0)
    @Comment("存款金额")
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }

    @Comment("存款渠道")
    @NotBlank(message = "rechargeForm.payAccountNotBlank")
    public String getResult_payAccountId() {
        return result_payAccountId;
    }

    public void setResult_payAccountId(String result_payAccountId) {
        this.result_payAccountId = result_payAccountId;
    }

    @NotBlank(message = "rechargeForm.depositWayNotBlank")
    public String getDepositWay() {
        return depositWay;
    }

    public void setDepositWay(String depositWay) {
        this.depositWay = depositWay;
    }

//    @Comment("验证码")
//    @Depends(message = "fund.rechargeForm.code.notBlank", operator = {Operator.GE}, property = {"$rechargeCount"}, value = {"3"}, jsValueExp = {"parseInt($(\"[name=rechargeCount]\").val())"})
//    @Remote(message = "fund.rechargeForm.code.correct", checkMethod = "checkCaptcha", checkClass = BaseDepositController.class)
//    public String get$code() {
//        return $code;
//    }

//    public void set$code(String $code) {
//        this.$code = $code;
//    }
    //endregion your codes

}