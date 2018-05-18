package so.wwb.gamebox.mobile.deposit.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.mobile.deposit.controller.BaseDepositController;
import so.wwb.gamebox.model.common.RegExpConstants;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("电子支付验证")
public class CompanyBankDeposit2Form implements IForm {
    private String result_payAccountId;
    private String result_payerName;
    private String result_rechargeAddress;
//    private String $code;

    @Comment("存入银行")
    @NotBlank(message = "valid.rechargeForm.payAccountIdNotBlank")
    public String getResult_payAccountId() {
        return result_payAccountId;
    }

    public void setResult_payAccountId(String result_payAccountId) {
        this.result_payAccountId = result_payAccountId;
    }

    @Comment("存款人")
    @Pattern(message = "valid.rechargeForm.payerName.pattern", regexp = RegExpConstants.PAYERNAME)
    @Depends(message = "deposit_auto.存款人不能为空",property = "result.rechargeType",operator = Operator.NE,value = RechargeTypeEnum.RECHARGE_TYPE_ATM_MONEY)
    @Length(min = 2, max = 30)
    public String getResult_payerName() {
        return result_payerName;
    }

    public void setResult_payerName(String result_payerName) {
        this.result_payerName = result_payerName;
    }

    @Comment("交易地点")
    @Length(max = 30)
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
}
