package so.wwb.gamebox.mobile.app.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("扫码支付验证")
public class OnlineScanDepositForm implements IForm {
    private String result_rechargeAmount;
    private String account;
    private String result_rechargeType;
//    private String result_payerBankcard;
//    private String $code;

    @Comment("存款渠道")
    @NotBlank(message = "rechargeForm.payAccountNotBlank")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Comment("存款金额")
    @NotBlank(message = "rechargeForm.rechargeAmountNotBlank")
    @Pattern(message = "rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = CompanyElectronicDepositController.class, checkMethod = "checkAmount")
    @Max(message = "rechargeForm.rechargeAmountMax", value = 99999999)
    @Min(message = "rechargeForm.rechargeAmountMin", value = 0)
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }

    @Comment("存款类型")
    @NotBlank(message = "rechargeForm.rechargeType")
    public String getResult_rechargeType() {
        return result_rechargeType;
    }

    public void setResult_rechargeType(String result_rechargeType) {
        this.result_rechargeType = result_rechargeType;
    }

//    @Comment("授权码")
//    @Depends(property = {"$isAuthCode"}, operator = {Operator.EQ}, value = {"true"},jsValueExp = "$(\"[name=isAuthCode]\").val() == 'true'")
//    @Digits(fraction = 0, integer = 20,message = "rechargeForm.result.bitAmount")
//    @Depends(message = "rechargeForm.alipayPayerNameNotBlank",property = "result.rechargeType", operator = {Operator.IN}, value = {RechargeTypeEnum.RECHARGE_TYPE_ALIPAY_FAST})
//    @Length(max = 20, min = 12,message = "rechargeForm.rechargeAmountLength")
//    public String getResult_payerBankcard() {
//        return result_payerBankcard;
//    }
//
//    public void setResult_payerBankcard(String result_payerBankcard) {
//        this.result_payerBankcard = result_payerBankcard;
//    }

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
