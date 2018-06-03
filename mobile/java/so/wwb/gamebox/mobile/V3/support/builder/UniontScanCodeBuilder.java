package so.wwb.gamebox.mobile.V3.support.builder;

import org.springframework.stereotype.Component;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;
import java.util.Map;

@Component
public class UniontScanCodeBuilder extends BaseScanCodeControllerBuilder implements IScanCodeControllerBuilder {
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


    public String getOnlineRechargeType() {
        return RechargeTypeEnum.UNION_PAY_SCAN.getCode();
    }

    public String getCompanyRechargeType() {
        return null;
    }
}
