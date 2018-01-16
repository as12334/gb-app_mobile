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
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * Created by Cherry on 16-6-24.
 */
@Comment("电子支付验证")
public class CompanyElectronicDepositCashForm implements IForm {
    private String result_rechargeAmount;

    @Comment("存款金额")
    @NotBlank(message = "valid.rechargeForm.rechargeAmountNotBlank")
    @Pattern(message = "valid.rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = CompanyElectronicDepositController.class, checkMethod = "checkAmount")
    @Max(message = "valid.rechargeForm.rechargeAmountMax", value = 99999999)
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }
}
