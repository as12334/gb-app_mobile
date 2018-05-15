package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.V3.enums.ScanCodeTypeEnum;
import so.wwb.gamebox.mobile.V3.handler.IScanCodeControllerHandler;
import so.wwb.gamebox.mobile.deposit.controller.BaseOnlineDepositController;
import so.wwb.gamebox.mobile.deposit.form.CompanyElectronicDepositForm;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDeposit2Form;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDepositForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * create by hanson 18-05-13
 */
@Controller
@RequestMapping("/wallet/v3/deposit/online/scan")
public class V3OnlineScanDePositController extends BaseOnlineDepositController {

    private Log LOG = LogFactory.getLog(V3OnlineScanDePositController.class);

    /**
     * 扫码支付
     */
    @RequestMapping("/scanCode/{type}")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String scanCode(Model model, @PathVariable String type) {
        PlayerRank rank = getRank();
        ScanCodeTypeEnum scanType = ScanCodeTypeEnum.enumOf(type);
        IScanCodeControllerHandler handler = SpringTool.getBean(scanType.getHandlerClazz());

        Map<String, PayAccount> scanAccount = handler.getScanAccount(rank);
        if (scanAccount != null) {
            model.addAttribute("scan", scanAccount);
        }
        //获取支持配置的电子账号类型
        List<PayAccount> electronicAccount = handler.getElectronicAccount(rank);
        if (electronicAccount != null) {
            model.addAttribute("electronic", electronicAccount);
        }
        commonPage(model, rank, handler.getOnlineType(), handler.getCompanyType());
        model.addAttribute("bankCode", handler.getBankCode());
        return "/deposit/ScanCode";
    }

    /**
     * 支付页面公共页面元素部分
     *
     * @param model
     */
    private void commonPage(Model model, PlayerRank rank, String onlineType, String companyType) {
        model.addAttribute("command", new PayAccountVo());
        model.addAttribute("username", SessionManager.getUserName());
        model.addAttribute("rank", rank);
        isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT);
        model.addAttribute("validateRule", JsRuleCreator.create(OnlineScanDepositForm.class));
        Double rechargeDecimals = Math.random() * 99 + 1;
        model.addAttribute("rechargeDecimals", rechargeDecimals.intValue());
        model.addAttribute("onlineType", onlineType);
        model.addAttribute("companyType", companyType);
        model.addAttribute("currency", SessionManager.getUser().getDefaultCurrency());
        model.addAttribute("isOpenActivityHall", ParamTool.isOpenActivityHall());
    }

    @RequestMapping("/index")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String index(PayAccountVo payAccountVo, Model model, HttpServletRequest request) {
        //获取收款账号
        PayAccount payAccount = getPayAccountById(payAccountVo.getSearch().getId());
        if (payAccount != null) {
            //是否隐藏收款账号
            isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT);
            model.addAttribute("rank", getRank());
            model.addAttribute("currency", getCurrencySign());
            String rechargeType = getElectronicRechargeType(payAccount.getBankCode());
            model.addAttribute("rechargeType", rechargeType);
            model.addAttribute("validateRule", JsRuleCreator.create(CompanyElectronicDepositForm.class));
            //上一次填写的账号/昵称
            model.addAttribute("lastTimeAccount", getPlayerPerDepositName(rechargeType, SessionManager.getUserId()));
        }
        if (payAccountVo.getDepositCash() != null) {
            model.addAttribute("rechargeAmount", payAccountVo.getDepositCash());
        }
        model.addAttribute("payAccount", payAccount);
        return "/deposit/Electronic";
    }

    /**
     * 提交存款金额:计算手续费和优惠
     *
     * @param playerRechargeVo
     * @return
     */
    @RequestMapping("/electronicSubmit")
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

    @RequestMapping("/scanCodeSubmit")
    @ResponseBody /*支付宝*/
    @Token(valid = true)
    public Map<String, Object> ScanCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result, HttpServletRequest request) {
        return commonDeposit(playerRechargeVo, result, request);
    }

    /**
     * 开启随机额度时提交方法
     *
     * @param playerRechargeVo
     * @param form
     * @param result
     * @return
     */
    @RequestMapping("/scanRandomCodeSubmit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> scanRandomCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDeposit2Form form, BindingResult result, HttpServletRequest request) {
        return commonDeposit(playerRechargeVo, result, request);
    }
}
