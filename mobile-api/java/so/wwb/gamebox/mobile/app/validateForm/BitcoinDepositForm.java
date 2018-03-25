package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.controller.BaseDepositController;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;

/**
 * 比特币支付表单验证
 */
@Comment("比特币支付验证")
public class BitcoinDepositForm implements IForm {
    private String result_payerBankcard;
    private String result_bankOrder;
    private String result_bitAmount;
//    private String $code;
    private String result_returnTime;

    @Comment("玩家比特币钱包地址")
    @NotBlank
    @Length(min = 26, max = 34)
    public String getResult_payerBankcard() {
        return result_payerBankcard;
    }

    public void setResult_payerBankcard(String result_payerBankcard) {
        this.result_payerBankcard = result_payerBankcard;
    }

    @Comment("txId")
    @NotBlank
    @Length(max = 64)
    @Remote(message = "deposit_auto.txId已存在", checkClass = BaseDepositController.class, checkMethod = "checkTxId")
    public String getResult_bankOrder() {
        return result_bankOrder;
    }

    public void setResult_bankOrder(String result_bankOrder) {
        this.result_bankOrder = result_bankOrder;
    }

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

    @Comment("比特币")
    @NotBlank
    //@Pattern(message = "请输入大于0.00001且至多只有8位小数的数字", regexp = FormValidRegExps.BIT_AMOUNT)
    @DecimalMin(value = "0.00010001")
    @Digits(integer = 8, fraction = 8)
    public String getResult_bitAmount() {
        return result_bitAmount;
    }

    public void setResult_bitAmount(String result_bitAmount) {
        this.result_bitAmount = result_bitAmount;
    }

    @Comment("交易时间")
    @NotBlank
    public String getResult_returnTime() {
        return result_returnTime;
    }

    public void setResult_returnTime(String result_returnTime) {
        this.result_returnTime = result_returnTime;
    }
}
