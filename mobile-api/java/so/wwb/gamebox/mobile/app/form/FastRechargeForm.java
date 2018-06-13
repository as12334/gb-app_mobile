package so.wwb.gamebox.mobile.app.form;

import org.hibernate.validator.constraints.NotBlank;
import org.soul.web.support.IForm;
import so.wwb.gamebox.mobile.common.consts.FormValidRegExps;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

/**
 * Created by cherry on 18-6-12.
 */
public class FastRechargeForm implements IForm{
    private String $userName;
    private String rechargeAmount;
    private String bankOrder;
    private String returnTime;
    private String receiveAccount;
    private String receiveName;
    private String $ip;
    private String payUrl;

    @NotBlank
    public String get$userName() {
        return $userName;
    }

    public void set$userName(String $userName) {
        this.$userName = $userName;
    }

    @NotBlank
    @Pattern(regexp = FormValidRegExps.MONEY)
    @Max(value = 99999999)
    public String getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(String rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    @NotBlank
    public String getBankOrder() {
        return bankOrder;
    }

    public void setBankOrder(String bankOrder) {
        this.bankOrder = bankOrder;
    }

    @NotBlank
    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    @NotBlank
    public String getReceiveAccount() {
        return receiveAccount;
    }

    public void setReceiveAccount(String receiveAccount) {
        this.receiveAccount = receiveAccount;
    }

    @NotBlank
    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    @NotBlank
    public String get$ip() {
        return $ip;
    }

    public void set$ip(String $ip) {
        this.$ip = $ip;
    }

    @NotBlank
    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }
}
