package so.wwb.gamebox.mobile.V3.support.builder;

import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;
import java.util.Map;

/**
 * 扫码\电子支付
 */
public interface IScanCodeControllerBuilder {

    /**
     * 获取扫码账号
     *
     * @param rank 用户层级
     * @return
     */
    Map<String, PayAccount> getScanAccount(PlayerRank rank);

    /**
     * 获取电子支付渠道
     *
     * @param rank
     * @return
     */
    List<PayAccount> getElectronicAccount(PlayerRank rank);


    /**
     * 在线支付类型
     * OnlineType
     *
     * @return
     */
    String getOnlineRechargeType();

    /**
     * 公司入款类型
     *
     * @return
     */
    String getCompanyRechargeType();
}
