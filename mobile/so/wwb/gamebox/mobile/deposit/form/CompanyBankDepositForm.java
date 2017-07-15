package so.wwb.gamebox.mobile.deposit.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("网银存款,银行柜台存款,柜员机转账,柜员机现金存款")
public class CompanyBankDepositForm implements IForm {

    private String result_rechargeAmount;

    @Comment("存款金额")
    @NotBlank(message = "valid.rechargeForm.rechargeAmountNotBlank")
    @Pattern(message = "valid.rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = CompanyBankDepositController.class, checkMethod = "checkAmount")
    @Max(message = "valid.rechargeForm.rechargeAmountMax", value = 99999999)
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }
}
