package so.wwb.gamebox.mobile.V3.handler;

import org.springframework.stereotype.Component;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;
import java.util.Map;

@Component
public class UniontScanCodeHandler extends BaseScanCodeControllerHandler implements IScanCodeControllerHandler {
    /**
     * 返回扫码支付渠道
     *
     * @param rank 用户层级
     * @return
     */
    public Map<String, PayAccount> getScanAccount(PlayerRank rank) {
        return getScanAccount(rank, PayAccountAccountType.UNION_PAY.getCode(), null);
    }

    /**
     * 返回电子支付渠道
     *
     * @param rank
     * @return
     */
    public List<PayAccount> getElectronicAccount(PlayerRank rank) {
        return null;
    }


    public String getCompanyType() {
        return RechargeTypeEnum.UNION_PAY_SCAN.getCode();
    }

    public String getBankCode() {
        return BankCodeEnum.UNIONPAY.getCode();
    }

    public String getOnlineType() {
        return null;
    }
}
