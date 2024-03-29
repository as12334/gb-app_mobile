package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.enums.DepositChannelEnum;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.common.cache.Cache;

import java.util.*;

public class V3BaseDepositController {
    protected String getDepositWay(String channel, String rechargeType) {
        if (DepositChannelEnum.COUNTER.getCode().equals(channel) || DepositChannelEnum.E_BANK.getCode().equals(channel)) {
            return DepositWayEnum.COMPANY_DEPOSIT.getCode();
        }
        return rechargeType;
    }

    /**
     * 计算手续费
     *
     * @param rechargeAmount 存款金额
     * @return
     */
    protected double calculateFee(PlayerRank rank, double rechargeAmount,String account) {
        return DepositTool.calculateFeeSchemaAndRank(getRank(), rechargeAmount,account);
    }


    /**
     * 获取当前用户所在层级
     *
     * @return
     */
    protected PlayerRank getRank() {
        return DepositTool.searchRank();
    }

    /**
     * 获取主货币符号
     *
     * @return
     */

    protected List<VActivityMessage> searchSaleByAmount(Double rechargeAmount, String type) {

        UserPlayer userPlayer = getUserPlayer();
        VActivityMessageVo vActivityMessageVo = new VActivityMessageVo();
        vActivityMessageVo.getSearch().setDepositWay(type);
        vActivityMessageVo.setDepositAmount(rechargeAmount);
        vActivityMessageVo.setRankId(userPlayer.getRankId());
        vActivityMessageVo.setLocal(SessionManager.getLocale().toString());
        vActivityMessageVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        vActivityMessageVo = ServiceActivityTool.vActivityMessageService().searchDepositPromotions(vActivityMessageVo);
        LinkedHashSet<VActivityMessage> vActivityMessages = vActivityMessageVo.getvActivityMessageList();

        if (CollectionTool.isEmpty(vActivityMessages)) {
            return new ArrayList<>();
        }
        //如果玩家首存可同时显示首存送和存就送
        if (userPlayer.getIsFirstRecharge() != null && userPlayer.getIsFirstRecharge()) {
            return setClassifyKeyName(new ArrayList(vActivityMessages));
        }
        //玩家非首存，查询存就送优惠
        List<VActivityMessage> activityList = CollectionQueryTool.query(vActivityMessages, Criteria.add(VActivityMessage.PROP_CODE, Operator.EQ, ActivityTypeEnum.DEPOSIT_SEND.getCode()));
        return setClassifyKeyName(activityList);
    }

    /**
     * 设置分类
     */
    private List<VActivityMessage> setClassifyKeyName(List<VActivityMessage> vActivityMessages) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        for (VActivityMessage message : vActivityMessages) {
            String str = message.getActivityClassifyKey() + ":" + SessionManager.getLocale();
            message.setClassifyKeyName(siteI18nMap.get(str).getValue());
        }
        return vActivityMessages;
    }

    /**
     * 获取玩家信息
     */
    private UserPlayer getUserPlayer() {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult();
    }

    protected String getLastDepositName(String rechargeType, Integer userId) {
        PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
        PlayerRecharge playerRecharge = new PlayerRecharge();
        playerRecharge.setRechargeType(rechargeType);
        playerRecharge.setPlayerId(userId);
        playerRechargeVo.setResult(playerRecharge);
        return ServiceSiteTool.playerRechargeService().searchLastPayerBankcard(playerRechargeVo);
    }

    protected Integer convertAccountId(String searchId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.setSearchId(searchId);
        return payAccountVo.getSearch().getId();
    }
}
