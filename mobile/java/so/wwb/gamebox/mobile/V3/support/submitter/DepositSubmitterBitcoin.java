package so.wwb.gamebox.mobile.V3.support.submitter;

import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import javax.servlet.http.HttpServletRequest;

/**
 * 比特币存款
 */
public class DepositSubmitterBitcoin extends DepositSubmitterCompany implements IDepositSubmitter {
    @Override
    protected PlayerRechargeVo fillPlayRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        PlayerRank rank = DepositTool.searchRank();
        //比特币充值,设置0.0
        playerRechargeVo.getResult().setRechargeAmount(0.0);
        return super.fillCompanyRecharge(playerRechargeVo, payAccount, request, rank);
    }

    @Override
    protected PlayerRechargeVo counterDiscounts(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        return playerRechargeVo;
    }

}
