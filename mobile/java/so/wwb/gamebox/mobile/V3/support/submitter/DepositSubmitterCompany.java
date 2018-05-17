package so.wwb.gamebox.mobile.V3.support.submitter;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.web.session.SessionManagerBase;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.V3.support.AbstractRechargeSubmitter;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.po.SysUserDataRight;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightVo;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.SessionManagerCommon;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 公司入款方式-充值 提交者
 */
public class DepositSubmitterCompany extends AbstractRechargeSubmitter implements IDepositSubmitter {

    /**
     * 保存充值记录
     *
     * @param playerRechargeVo
     * @param payAccount
     * @param request
     * @return
     */
    protected PlayerRechargeVo fillPlayRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        PlayerRank rank = DepositTool.searchRank();
        counterAndValidateFee(playerRechargeVo, rank);
        if (playerRechargeVo.isSuccess()) {
            fillCompanyRecharge(playerRechargeVo, payAccount, request, rank);
        }
        return playerRechargeVo;
    }

    protected PlayerRechargeVo fillCompanyRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request, PlayerRank rank) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());
        playerRechargeVo.setOrigin(SessionManagerCommon.getTerminal(request));
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setRankId(rank.getId());
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());
        return playerRechargeVo;
    }

    protected void doNotice(PlayerRechargeVo playerRechargeVo) {
        PlayerRecharge recharge = playerRechargeVo.getResult();
        if (recharge == null || recharge.getId() == null) {
            return;
        }
        //推送消息给前端
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_RECHARGE_REMINDER.getCode());
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("date", recharge.getCreateTime() == null ? new Date() : recharge.getCreateTime());
        map.put("currency", DepositTool.getCurrencySign());
        map.put("type", recharge.getRechargeTypeParent());
        map.put("amount", CurrencyTool.formatCurrency(recharge.getRechargeAmount()));
        map.put("id", recharge.getId());
        map.put("transactionNo", recharge.getTransactionNo());
        message.setMsgBody(JsonTool.toJson(map));
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());


        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl("fund/deposit/company/confirmCheck.html");
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(userIdByUrl);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }

    private void filterUnavailableSubAccount(List<Integer> userIdByUrl) {
        SysUserDataRightVo sysUserDataRightVo = new SysUserDataRightVo();
        sysUserDataRightVo.getSearch().setModuleType(DataRightModuleType.COMPANYDEPOSIT.getCode());
        Map<Integer, List<SysUserDataRight>> udrMap = ServiceSiteTool.sysUserDataRightService().searchDataRightsByModuleType(sysUserDataRightVo);
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        Integer rankId = userPlayerVo.getResult().getRankId();
        for (Iterator<Integer> iterator = userIdByUrl.iterator(); iterator.hasNext(); ) {
            Integer userId = iterator.next();
            List<SysUserDataRight> dataRights = udrMap.get(userId);
            if (dataRights == null || dataRights.size() == 0) {
                continue;
            }
            if (rankId != null) {
                boolean flag = true;
                for (SysUserDataRight sysUserDataRight : dataRights) {
                    if (rankId.equals(sysUserDataRight.getEntityId())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    iterator.remove();
                }
            }
        }
    }
}
