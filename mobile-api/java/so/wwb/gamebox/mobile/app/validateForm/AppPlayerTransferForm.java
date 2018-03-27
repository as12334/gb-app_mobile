package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.validation.form.constraints.Compare;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.commons.validation.form.support.CompareLogic;
import org.soul.commons.validation.form.support.RegExpConstants;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.app.controller.UserInfoAppController;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;


/**
 * 玩家转账表-susu表单验证对象
 *
 * @author cheery
 * @time 2015-11-2 20:09:37
 */
//region your codes 1
@Comment("转账验证")
public class AppPlayerTransferForm implements IForm {
//endregion your codes 1

    //region your codes 2
    private String transferInto;//转入对象
    private String transferOut;//转出对象
    private String result_transferAmount;//转账金额

    @Comment("转入")
    @NotBlank(message = "fund.transferForm.selectIntoApi")
    @Compare(message = "fund.transferForm.transferNotEqual", anotherProperty = "transferOut", logic = CompareLogic.NE)
    public String getTransferInto() {
        return transferInto;
    }

    public void setTransferInto(String transferInto) {
        this.transferInto = transferInto;
    }

    @Comment("转出")
    @NotBlank(message = "fund.transferForm.selectOutApi")
    public String getTransferOut() {
        return transferOut;
    }

    public void setTransferOut(String transferOut) {
        this.transferOut = transferOut;
    }

    @Comment("转账金额")
    @NotBlank(message = "fund.transferForm.transferAmountNotBlank")
    @Pattern(message = "fund.transferForm.transferAmountCorrect", regexp = RegExpConstants.POSITIVE_INTEGER)
    @Min(message = "fund.transferForm.transferAmountCorrect", value = 1)
    public String getResult_transferAmount() {
        return result_transferAmount;
    }

    public void setResult_transferAmount(String result_transferAmount) {
        this.result_transferAmount = result_transferAmount;
    }
    //endregion your codes 2

}