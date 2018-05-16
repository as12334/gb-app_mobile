package so.wwb.gamebox.mobile.V3.support;

import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import javax.servlet.http.HttpServletRequest;

/**
 * 比特币存款
 */
public class DepositSubmitterBitcoin extends DepositSubmitterCompany implements IDepositSubmitter {

    protected PlayerRechargeVo fillPlayRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        PlayerRank rank = DepositTool.searchRank();
        return super.saveRecharge(playerRechargeVo, payAccount, request, rank);
    }

}
