package so.wwb.gamebox.mobile.V3.support.helper;

import org.soul.commons.collections.CollectionTool;
import org.soul.web.support.IForm;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.mobile.V3.enums.DepositChannelEnum;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.V3.support.helper.BaseDepositControllerHelper;
import so.wwb.gamebox.mobile.deposit.form.CompanyBankDeposit2Form;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyDepositControllerHelper extends BaseDepositControllerHelper<PayAccount> {
    public List<PayAccount> getPayAccounts(PlayerRank rank, String channel) {
        List<PayAccount> allAccount = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.BANKACCOUNT.getCode(), null, null, null);
        List<PayAccount> payAccounts = new ArrayList<>();
        //如果是柜员机，则根据账号过滤柜员机开关
        if (DepositChannelEnum.COUNTER.getCode().equals(channel)) {
            for (PayAccount acc : allAccount) {
                if (acc.getSupportAtmCounter()) {
                    payAccounts.add(acc);
                }
            }
        } else {
            payAccounts.addAll(allAccount);
        }
        if (CollectionTool.isNotEmpty(payAccounts)) {
            return DepositTool.convertCompanyAccount(rank, payAccounts, getRechargeType(channel));
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
