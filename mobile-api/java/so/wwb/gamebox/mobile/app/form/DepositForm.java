package so.wwb.gamebox.mobile.app.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.validation.form.constraints.Depends;
import org.soul.commons.validation.form.support.Comment;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;

import javax.validation.constraints.*;


/**
 * 线上支付表单验证
 */
@Comment("线上支付表单验证")
public class DepositForm implements IForm {

    //region your codes
    private String account;
    private String result_rechargeAmount;
    private String $depositWay;
    private String result_bitAmount;
    private String result_bankOrder;
//    private String $code;



    @Comment("存款渠道")
    @NotBlank(message = "rechargeForm.payAccountNotBlank")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Depends(message = "rechargeForm.rechargeAmountNotBlank",property = "depositWay",operator = Operator.NE,value = DepositWayEnum.DEPOSIT_WAY_BITCOIN_FAST)
    @Pattern(message = "rechargeForm.rechargeAmountCorrect", regexp = FormValidRegExps.MONEY)
    //@Remote(message = "valid.rechargeForm.rechargeAmountOver", checkClass = OnlineDepositController.class, checkMethod = "checkAmount", additionalProperties = {"result.payerBank"})
    @Max(message = "rechargeForm.rechargeAmountMax", value = 99999999)
    @Min(message = "rechargeForm.rechargeAmountMin", value = 0)
    @Comment("存款金额")
    public String getResult_rechargeAmount() {
        return result_rechargeAmount;
    }

    public void setResult_rechargeAmount(String result_rechargeAmount) {
        this.result_rechargeAmount = result_rechargeAmount;
    }

    @Comment("获取优惠类型")
    @NotBlank(message = "rechargeForm.depositWayNotBlank")
    public String get$depositWay() {
        return $depositWay;
    }

    public void set$depositWay(String $depositWay) {
        this.$depositWay = $depositWay;
    }

    @Comment("比特币数量")
    @Depends(message = "rechargeForm.result.bitAmount",property = "depositWay",operator = Operator.EQ,value = DepositWayEnum.DEPOSIT_WAY_BITCOIN_FAST)
    //@Pattern(message = "请输入大于0.00001且至多只有8位小数的数字", regexp = FormValidRegExps.BIT_AMOUNT)
    @Digits(integer = 8, fraction = 8,message = "rechargeForm.result.bitAmountFormat")
    @DecimalMin(value = "0.00010001",message = "rechargeForm.result.bitAmountMin")
    public String getResult_bitAmount() {
        return result_bitAmount;
    }

    public void setResult_bitAmount(String result_bitAmount) {
        this.result_bitAmount = result_bitAmount;
    }

    @Comment("比特币Txid")
    @Depends(message = "rechargeForm.payerTxIdNotBlank",property = "depositWay",operator = Operator.EQ,value = DepositWayEnum.DEPOSIT_WAY_BITCOIN_FAST)
    @Length(max = 64,message = "rechargeForm.result.bitTxIdMax")
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

//    public void set$code(String $code) {
//        this.$code = $code;
//    }
    //endregion your codes

}