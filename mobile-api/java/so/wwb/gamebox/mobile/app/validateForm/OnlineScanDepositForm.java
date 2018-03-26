package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("扫码支付验证")
public class OnlineScanDepositForm implements IForm {
    private String result_rechargeAmount;
    private String result_payerBankcard;
//    private String $code;

    @Comment("存款金额")
    @NotBlank(message = "deposit_auto.请输入存款金额")
    @Pattern(message = "valid.rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "单笔存款最低为{0}，最高为{1}", checkClass = OnlineScanDepositController.class, checkMethod = "checkScanCodeAmount", additionalProperties = {"result.rechargeType"})
    @Max(message = "deposit_auto.单笔存款最高为99", value = 99999999)
    @Min(message = "valid.rechargeForm.rechargeAmountMin", value = 0)
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }

    @Comment("授权码")
    @Depends(property = {"$isAuthCode"}, operator = {Operator.EQ}, value = {"true"},jsValueExp = "$(\"[name=isAuthCode]\").val() == 'true'")
    @Length(max = 20, min = 12)
    @Digits(fraction = 0, integer = 20)
    public String getResult_payerBankcard() {
        return result_payerBankcard;
    }

    public void setResult_payerBankcard(String result_payerBankcard) {
        this.result_payerBankcard = result_payerBankcard;
    }
//
//    @Comment("验证码")
//    @Depends(message = "fund.rechargeForm.code.notBlank", operator = {Operator.GE}, property = {"$rechargeCount"}, value = {"3"}, jsValueExp = {"parseInt($(\"[name=rechargeCount]\").val())"})
//    @Remote(message = "fund.rechargeForm.code.correct", checkMethod = "checkCaptcha", checkClass = BaseDepositController.class)
//    public String get$code() {
//        return $code;
//    }
//
//    public void set$code(String $code) {
//        this.$code = $code;
//    }
}
