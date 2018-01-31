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
public class CompanyElectronicDepositForm implements IForm {
    private String result_payerBankcard;
    private String result_bankOrder;
    private String result_payerName;
//    private String $code;

    @Comment("存款账号")
    @Depends(property ="result.rechargeType", operator = Operator.NE,value= RechargeTypeEnum.RECHARGE_TYPE_ONECODEPAY_FASE,message = "valid.rechargeForm.payerBankcardNotBlank")
    //@NotBlank(message = "valid.rechargeForm.payerBankcardNotBlank")
    @Length(message = "valid.rechargeForm.payerBankcardLength", max = 20)
    public String getResult_payerBankcard() {
        return result_payerBankcard;
    }

    public void setResult_payerBankcard(String result_payerBankcard) {
        this.result_payerBankcard = result_payerBankcard;
    }

    @Comment("订单号后5位")
    @Length(message = "valid.rechargeForm.bankOrderNotBlank", min = 5, max = 5)
    public String getResult_bankOrder() {
        return result_bankOrder;
    }

    public void setResult_bankOrder(String result_bankOrder) {
        this.result_bankOrder = result_bankOrder;
    }

    @Comment("支付户名")
    @Depends(property = "result.rechargeType", operator = {Operator.EQ}, value = {RechargeTypeEnum.RECHARGE_TYPE_ALIPAY_FAST})
    public String getResult_payerName() {
        return result_payerName;
    }

    public void setResult_payerName(String result_payerName) {
        this.result_payerName = result_payerName;
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
