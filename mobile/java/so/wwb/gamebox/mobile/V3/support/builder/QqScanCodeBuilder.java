package so.wwb.gamebox.mobile.V3.support.builder;

import org.springframework.stereotype.Component;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;
import java.util.Map;

@Component
public class QqScanCodeBuilder extends BaseScanCodeControllerBuilder implements IScanCodeControllerBuilder {
    /**
     * 返回扫码支付渠道
     *
     * @param rank 用户层级
     * @return
     */
    public Map<String, PayAccount> getScanAccount(PlayerRank rank) {
        return getScanAccount(rank, null, new String[]{PayAccountAccountType.QQWALLET.getCode(), PayAccountAccountType.QQ_MICROPAY.getCode()});
    }

    /**
     * 返回电子支付渠道
     *
     * @param rank
     * @return
     */
    public List<PayAccount> getElectronicAccount(PlayerRank rank) {
        return getElectronicAccount(BankCodeEnum.QQWALLET.getCode());
    }


    public String getOnlineRechargeType() {
        return RechargeTypeEnum.QQWALLET_SCAN.getCode();
    }

    @Override
    public String getCompanyRechargeType() {
        return RechargeTypeEnum.QQWALLET_FAST.getCode();
    }
}
