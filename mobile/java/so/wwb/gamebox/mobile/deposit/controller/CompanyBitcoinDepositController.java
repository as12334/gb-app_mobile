package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.deposit.form.BitcoinDepositForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeListVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;

/**
 * 比特币支付
 */
@Controller
@RequestMapping("/wallet/deposit/company/bitcoin")
public class CompanyBitcoinDepositController extends BaseCompanyDepositController {

    /*比特币存款地址*/
    private static final String BITCION_URI = "/deposit/Bitcoin";

    @RequestMapping("/index")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String index(PayAccountVo payAccountVo, Model model, HttpServletRequest request) {
        //获取收款账号
        PayAccount payAccount = getPayAccountById(payAccountVo.getSearch().getId());
        if (payAccount != null) {
            //是否隐藏收款账号
            isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT);
            String rechargeType = RechargeTypeEnum.BITCOIN_FAST.getCode();
            model.addAttribute("rechargeType", rechargeType);
            model.addAttribute("validateRule", JsRuleCreator.create(BitcoinDepositForm.class));
            //上一次填写的账号/昵称
            model.addAttribute("lastTimeAccount", getLastDepositName(rechargeType, SessionManager.getUserId()));
            model.addAttribute("minDate", SessionManager.getDate().addDays(-30));
            model.addAttribute("date", SessionManager.getDate().getNow());
        }
        model.addAttribute("payAccount", payAccount);
        model.addAttribute("isOpenActivityHall", ParamTool.isOpenActivityHall());
        return BITCION_URI;
    }

    @RequestMapping("/getSales")
    @ResponseBody
    @Upgrade(upgrade = true)
    public String getSales() {
        if (!ParamTool.isOpenActivityHall()) {
            return JsonTool.toJson( searchSales(DepositWayEnum.BITCOIN_FAST.getCode()));
        } else {
            return JsonTool.toJson(new ArrayList<>(0));
        }
    }

    /**
     * 提交订单
     *
     * @param playerRechargeVo
     * @param form
     * @param result
     * @return
     */
    @RequestMapping("/deposit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> deposit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid BitcoinDepositForm form,
                                       BindingResult result,HttpServletRequest request) {
        return commonDeposit(playerRechargeVo, result,request);
    }

    /**
     * 保存存款数据
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount,HttpServletRequest request) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        playerRecharge.setRechargeAmount(0d);
        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());
        playerRecharge.setRechargeType(RechargeTypeEnum.BITCOIN_FAST.getCode());
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(SessionManagerCommon.getTerminal(request));
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());
        return playerRechargeVo;
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
     * 验证txId是否已提交过
     *
     * @param txId
     * @return
     */
    @RequestMapping("/checkTxId")
    @ResponseBody
    public boolean checkTxId(@RequestParam("result.bankOrder") String txId) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        listVo.getSearch().setBankOrder(txId);
        listVo.getSearch().setRechargeStatus(RechargeStatusEnum.FAIL.getCode());
        return ServiceSiteTool.playerRechargeService().isExistsTxId(listVo);
    }

}
