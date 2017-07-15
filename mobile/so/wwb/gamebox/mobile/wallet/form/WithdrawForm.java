package so.wwb.gamebox.mobile.wallet.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.commons.validation.form.support.RegExpConstants;
import so.wwb.gamebox.mobile.wallet.controller.WithdrawController;

import javax.validation.constraints.Pattern;

/**
 * 手机端取款表单验证
 * Created by bruce on 16-8-11.
 */
@Comment("手机端取款表单验证")
public class WithdrawForm {

    private String withdrawAmount;

    @NotBlank(message = "valid.withdrawForm.withdrawAmountNotBlank")
    @Pattern(message = "valid.withdrawForm.withdrawAmountCorrect", regexp = RegExpConstants.POSITIVE_INTEGER)
    @Remote(message = "", checkClass = WithdrawController.class, checkMethod = "checkWithdrawAmount")
    @Comment("提款金额")
    public String getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
}
