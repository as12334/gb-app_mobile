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
public class JdScanCodeHandler extends BaseScanCodeControllerHandler implements IScanCodeControllerHandler {
    /**
     * 返回扫码支付渠道
     *
     * @param rank 用户层级
     * @return
     */
    public Map<String, PayAccount> getScanAccount(PlayerRank rank) {
        return getScanAccount(rank, PayAccountAccountType.JD_PAY.getCode(), null);
    }

    /**
     * 返回电子支付渠道
     *
     * @param rank
     * @return
     */
    public List<PayAccount> getElectronicAccount(PlayerRank rank) {
        return getElectronicAccount(rank, BankCodeEnum.JDWALLET.getCode(), RechargeTypeEnum.JDWALLET_FAST.getCode());
    }


    public String getCompanyType() {
        return RechargeTypeEnum.JDWALLET_FAST.getCode();
    }

    public String getBankCode() {
        return BankCodeEnum.JDWALLET.getCode();
    }

    public String getOnlineType() {
        return RechargeTypeEnum.JDPAY_SCAN.getCode();
    }
}
