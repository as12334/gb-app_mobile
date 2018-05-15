package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.model.sys.po.SysParam;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightListVo;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

import java.util.*;

/**
 * Created by cherry on 16-8-12.
 */
public abstract class BaseCompanyDepositController extends BaseDepositController {

    /* 公司入款地址 */
    private static final String MCENTER_COMPANY_RECHARGE_URL = "fund/deposit/company/confirmCheck.html";

    private void filterUnavailableSubAccount(List<Integer> userIdByUrl) {
        SysUserDataRightListVo sysUserDataRightListVo = new SysUserDataRightListVo();
        sysUserDataRightListVo.getSearch().setUserId(SessionManager.getUserId());
        sysUserDataRightListVo.getSearch().setModuleType(DataRightModuleType.COMPANYDEPOSIT.getCode());
        List<Integer> dataRightEntityIds = ServiceSiteTool.sysUserDataRightService().searchPlayerDataRightEntityId(sysUserDataRightListVo);

        for (Iterator<Integer> iterator = userIdByUrl.iterator(); iterator.hasNext(); ) {
            Integer userId = iterator.next();
            if (!dataRightEntityIds.contains(userId)) {
                iterator.remove();
            }
        }
    }


    /**
     * 存款消息提醒发送消息给前端
     *
     * @param playerRechargeVo
     */
    void tellerReminder(PlayerRechargeVo playerRechargeVo) {
        PlayerRecharge recharge = playerRechargeVo.getResult();
        if (recharge == null || recharge.getId() == null) {
            return;
        }
        //推送消息给前端
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_RECHARGE_REMINDER.getCode());
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("date", recharge.getCreateTime() == null ? new Date() : recharge.getCreateTime());
        map.put("currency", getCurrencySign());
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
        sysResourceListVo.getSearch().setUrl(MCENTER_COMPANY_RECHARGE_URL);
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(userIdByUrl);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);
        message.addUserIds(userIdByUrl);
        ServiceTool.messageService().sendToMcenterMsg(message);
    }

    /**
     * 提交存款金额:计算手续费和优惠
     *
     * @param playerRechargeVo
     * @return
     */
    @RequestMapping("/submit")
    @Upgrade(upgrade = true)
    public String submit(PlayerRechargeVo playerRechargeVo, Model model) {

        boolean unCheckSuccess = false;
        boolean pop = true;
        String tips = "";
        Double rechargeAmount = playerRechargeVo.getResult().getRechargeAmount();
        //验证存款金额的合法性
        if (rechargeAmount == null || rechargeAmount <= 0) {
            tips = LocaleTool.tranMessage(Module.FUND, "rechargeForm.rechargeAmountCorrect");
            model.addAttribute("tips", tips);
        } else if (rechargeAmount > 0) {
            PlayerRank rank = getRank();
            Integer max = rank.getOnlinePayMax();
            Integer min = rank.getOnlinePayMin();
            //单笔存款金额提示
            if ((max != null && max < rechargeAmount) || (min != null && min > rechargeAmount)) {
                tips = LocaleTool.tranMessage(Module.FUND, "rechargeForm.rechargeAmountOver", min, max);
                model.addAttribute("tips", tips);
            } else {
                double fee = calculateFee(rank, rechargeAmount);
                if (rechargeAmount + fee <= 0) {
                    model.addAttribute("tips", "存款金额加手续费必须大于0");
                } else if ("1".equals(playerRechargeVo.getStatusNum())) {//状态为"1"，不显示优惠信息
                    pop = false;
                    unCheckSuccess = true;
                    /*Integer failureCount = ServiceSiteTool.playerRechargeService().statisticalFailureCount(playerRechargeVo, SessionManager.getUserId());
                    model.addAttribute("failureCount",failureCount);*/
                } else {
                    unCheckSuccess = true;
                    //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示提交弹窗
                    //手续费标志
                    boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
                    //返手续费标志
                    boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
                    /*String depositWay = playerRechargeVo.getResult().getRechargeType();
                    if (RechargeTypeEnum.ONLINE_BANK.getCode().equals(depositWay)) {
                        depositWay = DepositWayEnum.COMPANY_DEPOSIT.getCode();
                    }*/
                    String depositWay = playerRechargeVo.getResult().getRechargeType();
                    if ("company".equals(playerRechargeVo.getDepositChannel())) {
                        depositWay = DepositWayEnum.COMPANY_DEPOSIT.getCode();
                    }
                    if (isFee || isReturnFee) {
                        String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
                        model.addAttribute("counterFee", counterFee);
                        model.addAttribute("fee", fee);
                        String msg = "";
                        if (fee > 0) {
                            msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.returnFee", counterFee);
                        } else if (fee < 0) {
                            msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.needFee", counterFee);
                        } else if (fee == 0) {
                            msg = LocaleTool.tranMessage(Module.FUND, "Recharge.recharge.freeFee", counterFee);
                        }
                        model.addAttribute("depositChannel", playerRechargeVo.getDepositChannel());
                        model.addAttribute("msg", msg);
                    }
                    boolean isOpenActivityHall = ParamTool.isOpenActivityHall();
                    model.addAttribute("isOpenActivityHall", isOpenActivityHall);
                    if (!isOpenActivityHall) {
                        List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount, depositWay);
                        model.addAttribute("sales", activityMessages);
                    }
                }
            }
        }
        model.addAttribute("unCheckSuccess", unCheckSuccess);
        model.addAttribute("pop", pop);
        model.addAttribute("rechargeAmount", rechargeAmount);
        model.addAttribute("submitType", "company");
        model.addAttribute("statusNum", playerRechargeVo.getStatusNum());
        return "/deposit/Sale2";
    }

    public Map<String, Object> commonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result) {
        Map<String, Object> map = new HashMap<>(3, 1f);
        if (result.hasErrors()) {
            playerRechargeVo.setSuccess(false);
            return getVoMessage(map, playerRechargeVo);
        }
        PayAccount payAccount = getPayAccountById(playerRechargeVo.getResult().getPayAccountId());
        if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST));
            map.put("accountNotUsing", true);
            return getVoMessage(map, playerRechargeVo);
        }
        playerRechargeVo = saveRecharge(playerRechargeVo, payAccount);
        //保存订单
        playerRechargeVo = ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
        if (playerRechargeVo.isSuccess()) {
            tellerReminder(playerRechargeVo);
        }
        return getVoMessage(map, playerRechargeVo);
    }

    protected abstract PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount);
}
