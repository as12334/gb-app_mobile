package so.wwb.gamebox.mobile.app.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("电子支付验证")
public class CompanyElectronicDepositForm implements IForm {
    private String result_rechargeAmount;
    private String result_payerBankcard;
    private String result_bankOrder;
    private String result_payerName;
    private String account;
    private String result_rechargeType;
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

    @Comment("存款账号")
    @Depends(message = "rechargeForm.payerBankcard",property = "result.rechargeType", operator = {Operator.IN}, value = {RechargeTypeEnum.RECHARGE_TYPE_WECHATPAY_FAST, RechargeTypeEnum.RECHARGE_TYPE_BDWALLET_FAST, RechargeTypeEnum.RECHARGE_TYPE_JDWALLET_FAST, RechargeTypeEnum.RECHARGE_TYPE_QQWALLET_FAST, RechargeTypeEnum.RECHARGE_TYPE_OTHER_FAST})
    @Length(message = "rechargeForm.payerBankcardLength", max = 20)
    public String getResult_payerBankcard() {
        return result_payerBankcard;
    }

    public void setResult_payerBankcard(String result_payerBankcard) {
        this.result_payerBankcard = result_payerBankcard;
    }

    @Comment("订单号后5位")
    @Length(message = "rechargeForm.bankOrderNotBlank", min = 5, max = 5)
    public String getResult_bankOrder() {
        return result_bankOrder;
    }

    public void setResult_bankOrder(String result_bankOrder) {
        this.result_bankOrder = result_bankOrder;
    }

    @Comment("支付户名")
    @Depends(message = "rechargeForm.alipayPayerNameNotBlank",property = "result.rechargeType", operator = {Operator.EQ}, value = {RechargeTypeEnum.RECHARGE_TYPE_ALIPAY_FAST})
    public String getResult_payerName() {
        return result_payerName;
    }

    public void setResult_payerName(String result_payerName) {
        this.result_payerName = result_payerName;
    }

    @Comment("存款类型")
    @NotBlank(message = "rechargeForm.rechargeType")
    public String getResult_rechargeType() {
        return result_rechargeType;
    }

    public void setResult_rechargeType(String result_rechargeType) {
        this.result_rechargeType = result_rechargeType;
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
}
