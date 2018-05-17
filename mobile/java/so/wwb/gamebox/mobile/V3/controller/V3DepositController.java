package so.wwb.gamebox.mobile.V3.controller;

import org.apache.tools.ant.taskdefs.optional.pvcs.Pvcs;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.sys.po.SysParam;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.security.Main;
import so.wwb.gamebox.mobile.V3.enums.DepositChannelEnum;
import so.wwb.gamebox.mobile.V3.helper.DepositControllerHelperFactory;
import so.wwb.gamebox.mobile.V3.helper.IDepositControllerHelper;
import so.wwb.gamebox.mobile.V3.support.*;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.digiccy.po.DigiccyAccountInfo;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.common.demomodel.DemoMenuEnum;
import so.wwb.gamebox.web.common.demomodel.DemoModel;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wallet/v3/deposit")
public class V3DepositController extends V3BaseDepositController {
    /**
     * 存款首页
     *
     * @param model
     * @return
     */
    @RequestMapping("/index")
    @DemoModel(menuCode = DemoMenuEnum.CKZQ)
    @Upgrade(upgrade = true)
    public String index(Model model) {
        //设置foot所选择的位置
        model.addAttribute("skip", 0);
        //取得用户所支持的存款通道
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.getSearch().setTerminal(TerminalEnum.MOBILE.getCode());
        payAccountListVo.setPlayerId(SessionManager.getUserId());
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());

        Map<String, Long> channelCountMap = ServiceSiteTool.payAccountService().queryChannelCount(payAccountListVo);

        model.addAttribute("map", channelCountMap);
        //是否支持数字货币
        DigiccyAccountInfo digiccyAccountInfo = ParamTool.getDigiccyAccountInfo();
        model.addAttribute("digiccyAccountInfo", digiccyAccountInfo);
        //快速充值地址
        model.addAttribute("isFastRecharge", true);
        SysParam rechargeUrlParam = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL);
        if (rechargeUrlParam == null || StringTool.isBlank(rechargeUrlParam.getParamValue())) {
            model.addAttribute("isFastRecharge", false);
        }
        String fastRechargeUrl = getFastRechargeUrl();
        model.addAttribute("rechargeUrlParam", fastRechargeUrl);
        return "/deposit/index/Deposit";
    }

    /**
     * 各种存款通道跳转的页面
     *
     * @param model
     * @param channel
     * @return
     */
    @RequestMapping("/{channel}/index")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String chanleIndex(Model model, @PathVariable String channel) {
        IDepositControllerHelper helper = DepositControllerHelperFactory.getHelper(channel);
        //获取用户层级
        PlayerRank rank = getRank();
        model.addAttribute("rank", rank);
        //根据通道类型及用户层级获取该通道支持的账号列表
        List<? extends PayAccount> payAccounts = helper.getPayAccounts(rank, channel);
        model.addAttribute("accounts", payAccounts);//账号列表
        model.addAttribute("validateRule", JsRuleCreator.create(helper.getIndexValidateFormClazz()));//表单提交验证器
        model.addAttribute("accountJson", helper.getAccountJson(payAccounts));//账号列表转json，有些页面要用到它来判断
        //根据层级获取是否需要手续费
        model.addAttribute("command", new PayAccountVo());
        //当前选择的通道
        model.addAttribute("channel", channel);
        //随机额度
        Double rechargeDecimals = Math.random() * 99 + 1;
        model.addAttribute("rechargeDecimals", rechargeDecimals.intValue());
        model.addAttribute("rechargeType", helper.getRechargeType(channel));
        return helper.getIndexUrl();
    }

    /**
     * 公司入款的账号显示页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/nextStep")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String nextStep(PayAccountVo payAccountVo, @RequestParam("channel") String channel, Model model) {
        //当前选择的通道
        model.addAttribute("channel", channel);
        IDepositControllerHelper helper = DepositControllerHelperFactory.getHelper(channel);
        //获取收款账号
        PayAccount payAccount = DepositAccountSearcher.getInstance().searchById(payAccountVo.getSearch().getId());
        if (payAccount != null) {
            model.addAttribute("rank", getRank());
            //是否隐藏收款账号
            SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.CONTENT_PAY_ACCOUNT_HIDE);
            if (sysParam != null) {
                SysParam hideParam = ParamTool.getSysParam(SiteParamEnum.PAY_ACCOUNT_HIDE_ONLINE_BANKING);
                // 判断是否隐藏收款账号
                if ("true".equals(sysParam.getParamValue()) && "true".equals(hideParam.getParamValue())) {
                    model.addAttribute("isHide", true);
                    model.addAttribute("hideContent", Cache.getSiteI18n(SiteI18nEnum.MASTER_CONTENT_HIDE_ACCOUNT_CONTENT).get(SessionManager.getLocale().toString()));
                    model.addAttribute("customerService", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
                }
            }
            model.addAttribute("bank", Cache.getBank().get(payAccount.getBankCode()));
            model.addAttribute("validateRule", JsRuleCreator.create(helper.getSecondValidateFormClazz()));
            //微信支付宝等电子支付后，自动带出上次填写的信息
            //model.addAttribute("lastTimeAccount", getLastDepositName(playerRechargeVo.getResult().getRechargeType(), SessionManager.getUserId()));
        }
        if (payAccountVo.getDepositCash() != null) {
            model.addAttribute("rechargeAmount", payAccountVo.getDepositCash());
        }
        model.addAttribute("payAccount", payAccount);
        model.addAttribute("rechargeType", payAccountVo.getPayType());
        return helper.getNextStepUrl();
    }

    private String getLastDepositName(String rechargeType, Integer userId) {
        PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
        PlayerRecharge playerRecharge = new PlayerRecharge();
        playerRecharge.setRechargeType(rechargeType);
        playerRecharge.setPlayerId(userId);
        playerRechargeVo.setResult(playerRecharge);
        return ServiceSiteTool.playerRechargeService().searchLastPayerBankcard(playerRechargeVo);
    }

    /**
     * 获取活动信息
     *
     * @param playerRechargeVo
     * @param model
     * @return
     */
    @RequestMapping("/activity")
    @Upgrade(upgrade = true)
    public String showActivity(PlayerRechargeVo playerRechargeVo, @RequestParam("channel") String channel, Model model) {
        //根据界面选择内容显示活动:同时提示手续费信息
        double amount = 0;
        if (DepositChannelEnum.BITCOIN.getCode().equals(channel)) {
            amount = playerRechargeVo.getResult().getBitAmount();
        } else {
            amount = playerRechargeVo.getResult().getRechargeAmount();
        }
        double fee = calculateFee(getRank(), amount);
        model.addAttribute("rechargeAmount", DepositTool.getCurrencySign() + CurrencyTool.formatCurrency(amount));
        model.addAttribute("counterFee", DepositTool.getCurrencySign() + CurrencyTool.formatCurrency(Math.abs(fee)));
        model.addAttribute("fee", fee);
        boolean isOpenActivityHall = ParamTool.isOpenActivityHall();
        if (!isOpenActivityHall) {
            model.addAttribute("sales", searchSaleByAmount(amount, playerRechargeVo.getResult().getRechargeType()));
        }
        model.addAttribute("isOpenActivityHall", isOpenActivityHall);
        return "/deposit/Sale2";
    }

    /**
     * 前台提交支付
     *
     * @param playerRechargeVo
     * @param result
     * @return
     */
    @RequestMapping("/submit")
    @ResponseBody
    @Token(valid = true)
    public AbstractRechargeSubmitter.DespositResult submit(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request) {
        RechargeTypeEnum rechargeTypeEnum = RechargeTypeEnum.enumOf(playerRechargeVo.getResult().getRechargeType());
        if (rechargeTypeEnum == null) {
            return null;
        }
        IDepositSubmitter submitter = null;
        if (rechargeTypeEnum.getCode().equals("BITCOIN_FAST")) {
            submitter = new DepositSubmitterBitcoin();
        }
        if (rechargeTypeEnum.getParentEnum().equals(RechargeTypeParentEnum.ONLINE_DEPOSIT)) {
            submitter = new DepositSubmitterOnline();
        }
        if (rechargeTypeEnum.getParentEnum().equals(RechargeTypeParentEnum.COMPANY_DEPOSIT)) {
            submitter = new DepositSubmitterCompany();
        }
        return submitter.saveRecharge(playerRechargeVo, result, request);
    }
}
