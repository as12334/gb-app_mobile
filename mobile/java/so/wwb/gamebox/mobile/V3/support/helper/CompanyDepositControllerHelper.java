package so.wwb.gamebox.mobile.V3.support.helper;

import org.soul.web.support.IForm;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.V3.support.helper.BaseDepositControllerHelper;
import so.wwb.gamebox.mobile.deposit.form.CompanyBankDeposit2Form;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;

@Component
public class CompanyDepositControllerHelper extends BaseDepositControllerHelper<PayAccount> {
    public List<PayAccount> getPayAccounts(PlayerRank rank, String channel) {
        List<PayAccount> payAccounts = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.BANKACCOUNT.getCode(), null, null, null);
        boolean display = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        if (!display) {
            return distinctAccountByBankCode(payAccounts);
        } else {
            //相同账号设置别名
            convertAliasName(payAccounts);
        }
        return payAccounts;
    }

    /**
     * 界面转json 为了界面控制
     *
     * @param accouts
     * @return
     */
    public String getAccountJson(List<PayAccount> accouts) {
        return null;
    }


    /**
     * 页面输入验证器
     *
     * @return
     */
    public Class<? extends IForm> getIndexValidateFormClazz() {
        return CompanyBankDeposit2Form.class;
    }

    @Override
    public String getRechargeType(String channel) {
        return RechargeTypeEnum.ONLINE_BANK.getCode();
    }

    /**
     * 跳转页面
     *
     * @return
     */
    public String getIndexUrl() {
        return "/deposit/channel/Deposit.company";
    }

    public String getNextStepUrl() {
        return "/deposit/channel/Deposit.company.second";
    }


}
