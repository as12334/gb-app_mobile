package so.wwb.gamebox.mobile.V3.support;

import org.soul.commons.lang.string.StringTool;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.web.session.SessionManagerBase;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 在线支付方式充值提交者
 */
public class DepositSubmitterOnline extends AbstractRechargeSubmitter implements IDepositSubmitter {


    protected PlayerRechargeVo fillPlayRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        PlayerRank rank = DepositTool.searchRank();
        //设置存款其他数据
        playerRechargeVo = counterAndValidateFee(playerRechargeVo, rank);
        if (!playerRechargeVo.isSuccess()) {
            return playerRechargeVo;
        }
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setRankId(rank.getId());
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.ONLINE_DEPOSIT.getCode());
        playerRecharge.setRechargeType(playerRechargeVo.getResult().getRechargeType());
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());
        return playerRechargeVo;
    }

    @Override
    protected String loadPayUrl(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        return DepositTool.getOnlinePayUrl(payAccount, playerRechargeVo.getResult(), request);
    }

    @Override
    protected PlayerRechargeVo counterDiscounts(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        if (payAccount.getRandomAmount() != null && payAccount.getRandomAmount()) {
            Double rechargeAmount = playerRecharge.getRechargeAmount();
            if (rechargeAmount.intValue() == rechargeAmount) {
                rechargeAmount += playerRechargeVo.getResult().getRandomCash() / 100;
                playerRecharge.setRechargeAmount(rechargeAmount);
            }
        }
        return playerRechargeVo;
    }

    protected void doNotice(PlayerRechargeVo playerRechargeVo) {
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_ONLINE_RECHARGE_REMINDER.getCode());
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());
        message.setMsgBody(SiteParamEnum.WARMING_TONE_ONLINEPAY.getType());
        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl("/fund/deposit/online/list.html");
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }
}
