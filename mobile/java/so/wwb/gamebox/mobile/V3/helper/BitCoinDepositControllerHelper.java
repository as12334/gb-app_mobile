package so.wwb.gamebox.mobile.V3.helper;

import org.soul.commons.collections.CollectionTool;
import org.soul.web.support.IForm;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.deposit.form.BitcoinDepositForm;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.*;

@Component
public class BitCoinDepositControllerHelper extends BaseDepositControllerHelper<PayAccount> {

    public List<PayAccount> getPayAccounts(PlayerRank rank, String channel) {
        List<PayAccount> payAccounts = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode(), null, null, null);
        List<PayAccount> accounts = distinctAccountByBankCode(payAccounts);
        Map<String, List<PayAccount>> payAccountMap = CollectionTool.groupByProperty(accounts, PayAccount.PROP_BANK_CODE, String.class);
        return payAccountMap.get("bitcoin");
    }


    public Class<? extends IForm> getIndexValidateFormClazz() {
        return BitcoinDepositForm.class;
    }

    public String getIndexUrl() {
        return "/deposit/channel/Deposit.bitcoin";
    }

    @Override
    public String getNextStepUrl() {
        return "/deposit/channel/Deposit.bitcoin.second";
    }

    public String getRechargeType(String channel) {
        return RechargeTypeEnum.BITCOIN_FAST.getCode();
    }
}
