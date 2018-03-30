package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.model.common.RegExpConstants;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("网银存款,银行柜台存款,柜员机转账,柜员机现金存款")
public class CompanyBankDepositForm implements IForm {

    private String result_payerName;
    private String result_rechargeAddress;
    private String result_rechargeAmount;

//    private String $code;


    private String account;

    @Comment("存款渠道")
    @NotBlank(message = "rechargeForm.payAccountNotBlank")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Comment("存款人")
    @Pattern(message = "rechargeForm.payerName.pattern", regexp = RegExpConstants.PAYERNAME)
    @Depends(message = "deposit_auto.存款人不能为空",property = "result.rechargeType",operator = Operator.NE,value = RechargeTypeEnum.RECHARGE_TYPE_ATM_MONEY)
    @Length(min = 2, max = 30,message = "rechargeForm.payAccountIdNotBlank")
    public String getResult_payerName() {
        return result_payerName;
    }

    public void setResult_payerName(String result_payerName) {
        this.result_payerName = result_payerName;
    }

    @Comment("交易地点")
    @Length(max = 20,message = "rechargeForm.result.StringMax")
    public String getResult_rechargeAddress() {
        return result_rechargeAddress;
    }

    public void setResult_rechargeAddress(String result_rechargeAddress) {
        this.result_rechargeAddress = result_rechargeAddress;
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

    @Comment("存款金额")
    @NotBlank(message = "rechargeForm.rechargeAmountNotBlank")
    @Pattern(message = "rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = CompanyBankDepositController.class, checkMethod = "checkAmount")
    @Max(message = "rechargeForm.rechargeAmountMax", value = 99999999)
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }
}
