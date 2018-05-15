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
public class WechatScanCodeHandler extends BaseScanCodeControllerHandler implements IScanCodeControllerHandler {
    /**
     * 返回扫码支付渠道
     *
     * @param rank 用户层级
     * @return
     */
    public Map<String, PayAccount> getScanAccount(PlayerRank rank) {
        return getScanAccount(rank, null, new String[]{PayAccountAccountType.WECHAT.getCode(), PayAccountAccountType.WECHAT_MICROPAY.getCode()});
    }

    /**
     * 返回电子支付渠道
     *
     * @param rank
     * @return
     */
    public List<PayAccount> getElectronicAccount(PlayerRank rank) {
        return getElectronicAccount(rank, BankCodeEnum.FAST_WECHAT.getCode(), RechargeTypeEnum.WECHATPAY_FAST.getCode());
    }


    public String getCompanyType() {
        return RechargeTypeEnum.WECHATPAY_FAST.getCode();
    }

    public String getBankCode() {
        return BankCodeEnum.FAST_WECHAT.getCode();
    }

    public String getOnlineType() {
        return RechargeTypeEnum.WECHATPAY_SCAN.getCode();
    }
}
