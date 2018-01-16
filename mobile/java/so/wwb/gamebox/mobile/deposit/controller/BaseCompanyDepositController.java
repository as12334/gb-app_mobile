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
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.po.SysUserDataRight;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightVo;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

import java.util.*;

/**
 * Created by cherry on 16-8-12.
 */
public abstract class BaseCompanyDepositController extends BaseDepositController {

    /* 公司入款地址 */
     private static final String MCENTER_COMPANY_RECHARGE_URL = "/fund/deposit/company/confirmCheck.html";

    private void filterUnavailableSubAccount(List<Integer> userIdByUrl) {
        SysUserDataRightVo sysUserDataRightVo = new SysUserDataRightVo();
        sysUserDataRightVo.getSearch().setModuleType(DataRightModuleType.COMPANYDEPOSIT.getCode());
        Map<Integer,List<SysUserDataRight>> udrMap = ServiceSiteTool.sysUserDataRightService().searchDataRightsByModuleType(sysUserDataRightVo);

        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        Integer rankId = userPlayerVo.getResult().getRankId();
        for (Iterator<Integer> iterator = userIdByUrl.iterator(); iterator.hasNext(); ) {
            Integer userId =  iterator.next();
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

    /**
     * 是否隐藏收款账号
     * @param model
     * @param paramEnum
     */
     void isHide(Model model, SiteParamEnum paramEnum) {
         // 查询隐藏参数
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.CONTENT_PAY_ACCOUNT_HIDE);
        if (sysParam == null) {
            return;
        }
        SysParam hideParam = ParamTool.getSysParam(paramEnum);
        // 判断是否隐藏收款账号
        if ("true".equals(sysParam.getParamValue()) && "true".equals(hideParam.getParamValue())) {
            model.addAttribute("isHide", true);
            model.addAttribute("hideContent", Cache.getSiteI18n(SiteI18nEnum.MASTER_CONTENT_HIDE_ACCOUNT_CONTENT).get(SessionManager.getLocale().toString()));
            model.addAttribute("customerService", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
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
        Map<String, Object> map = new HashMap<>(3,1f);
        map.put("date", recharge.getCreateTime() == null ? new Date() : recharge.getCreateTime());
        map.put("currency", getCurrencySign());
        map.put("type", recharge.getRechargeTypeParent());
        map.put("amount", CurrencyTool.formatCurrency(recharge.getRechargeAmount()));
        map.put("id", recharge.getId());
        map.put("transactionNo",recharge.getTransactionNo());
        message.setMsgBody(JsonTool.toJson(map));
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());
        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl(MCENTER_COMPANY_RECHARGE_URL);
        List<Integer> userIdByUrl = ServiceSiteTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        userIdByUrl.add(Const.MASTER_BUILT_IN_ID);

        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(userIdByUrl);
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
    public String submit(PlayerRechargeVo playerRechargeVo,Model model) {

        boolean unCheckSuccess = false;
        boolean pop = true;
        String tips = "";
        Double rechargeAmount = playerRechargeVo.getResult().getRechargeAmount();
        //验证存款金额的合法性
        if (rechargeAmount==null || rechargeAmount <= 0) {
            tips = LocaleTool.tranMessage(Module.FUND,"rechargeForm.rechargeAmountCorrect");
            model.addAttribute("tips",tips);
        } else if (rechargeAmount > 0) {
            PlayerRank rank = getRank();
            Integer max = rank.getOnlinePayMax();
            Integer min = rank.getOnlinePayMin();
            //单笔存款金额提示
            if ((max != null && max < rechargeAmount) || (min != null && min > rechargeAmount)) {
                tips = LocaleTool.tranMessage(Module.FUND,"rechargeForm.rechargeAmountOver",min,max);
                model.addAttribute("tips",tips);
            } else {
                double fee = calculateFee(rank, rechargeAmount);
                if (rechargeAmount + fee <= 0) {
                    model.addAttribute("tips","存款金额加手续费必须大于0");
                } else {
                    unCheckSuccess = true;
                    //如果没有开启手续费和返还手续费,并且没有可参与优惠,不显示提交弹窗
                    //手续费标志
                    boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
                    //返手续费标志
                    boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());

                    List<VActivityMessage> activityMessages = searchSaleByAmount(rechargeAmount,
                            playerRechargeVo.getResult().getRechargeType());
                    if (!isFee && !isReturnFee && activityMessages.size()<=0 ) {
                        pop = false;
                    }else if("1".equals(playerRechargeVo.getStatusNum())){ //状态为"1"，不显示优惠信息
                        pop = false;
                    }else{
                        String counterFee = getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee));
                        model.addAttribute("counterFee", counterFee);
                        model.addAttribute("fee", fee);
                        model.addAttribute("sales", activityMessages);
                        String msg = "";
                        if (fee > 0) {
                            msg = LocaleTool.tranMessage(Module.FUND,"Recharge.recharge.returnFee",counterFee);
                        } else if (fee < 0) {
                            msg = LocaleTool.tranMessage(Module.FUND,"Recharge.recharge.needFee",counterFee);
                        } else if (fee == 0) {
                            msg = LocaleTool.tranMessage(Module.FUND,"Recharge.recharge.freeFee",counterFee);
                        }
                        model.addAttribute("msg",msg);
                    }
                }
            }
        }
        model.addAttribute("unCheckSuccess",unCheckSuccess);
        model.addAttribute("pop",pop);
        model.addAttribute("rechargeAmount",rechargeAmount);
        model.addAttribute("submitType","company");
        model.addAttribute("statusNum",playerRechargeVo.getStatusNum());
        return "/deposit/Sale2";
    }

    public Map<String, Object> commonDeposit(PlayerRechargeVo playerRechargeVo, BindingResult result) {
        Map<String, Object> map = new HashMap<>(3,1f);
        if (result.hasErrors()) {
            playerRechargeVo.setSuccess(false);
            return getVoMessage(map, playerRechargeVo);
        }
        PayAccount payAccount = getPayAccountById(playerRechargeVo.getResult().getPayAccountId());
        if (payAccount == null) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST));
            return getVoMessage(map, playerRechargeVo);
        }
        playerRechargeVo = saveRecharge(playerRechargeVo,payAccount);
        //保存订单
        playerRechargeVo = ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
        if (playerRechargeVo.isSuccess()) {
            tellerReminder(playerRechargeVo);
        }
        return getVoMessage(map, playerRechargeVo);
    }

    protected abstract PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo,PayAccount payAccount);
}
