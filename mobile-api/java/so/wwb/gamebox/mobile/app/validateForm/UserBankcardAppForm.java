package so.wwb.gamebox.mobile.app.validateForm;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.constraints.Remote;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.model.common.RegExpConstants;
import so.wwb.gamebox.web.fund.controller.UserBankcardController;

import javax.validation.constraints.Pattern;

/**
 * Created by legend on 18-1-19.
 */
public class UserBankcardAppForm implements IForm {

    private String result_bankcardMasterName;
    private String result_bankName;
    private String result_bankcardNumber;
    private String result_bankDeposit;
    private String $userType;


    //    @NotBlank
//    @Pattern(message = "player.bankCardCorrect",regexp = RegExpConstants.DIGITS)
//    @Remote(message = "player.bankCardCorrect", checkClass = PlayerController.class, checkMethod = "checkBankcardNumber", additionalProperties = "result.bankName")

    @NotBlank(message = "player_auto.请输入10")
    @Length(min = 10,max = 25)
    @Pattern(message = "player_auto.银行卡格式错误",regexp = RegExpConstants.BANK)
    @Comment("银行卡号")
    public String getResult_bankcardNumber() {
        return result_bankcardNumber;
    }

    public void setResult_bankcardNumber(String result_bankcardNumber) {
        this.result_bankcardNumber = result_bankcardNumber;
    }

    @NotBlank(message = "player_auto.请选择银行")
    public String getResult_bankName() {
        return result_bankName;
    }

    public void setResult_bankName(String result_bankName) {
        this.result_bankName = result_bankName;
    }

    @Depends(property ={"result_bankName"}, operator = {Operator.EQ}, value = {"other_bank"})
    @Length(max = 200)
    public String getResult_bankDeposit() {
        return result_bankDeposit;
    }

    public void setResult_bankDeposit(String result_bankDeposit) {
        this.result_bankDeposit = result_bankDeposit;
    }

    public String getResult_bankcardMasterName() {
        return result_bankcardMasterName;
    }

    public void setResult_bankcardMasterName(String result_bankcardMasterName) {
        this.result_bankcardMasterName = result_bankcardMasterName;
    }

    public String get$userType() {
        return $userType;
    }

    public void set$userType(String $userType) {
        this.$userType = $userType;
    }

}
