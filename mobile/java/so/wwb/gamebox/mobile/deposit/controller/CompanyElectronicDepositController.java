package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.deposit.form.CompanyElectronicDepositForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 电子支付(微信,支付宝,其他)
 *
 * Created by bruce on 16-12-10.
 */
@Controller
@RequestMapping("/wallet/deposit/company/electronic")
public class CompanyElectronicDepositController extends BaseCompanyDepositController {

    /*支付宝*/
    private static final String ALIPAY = "alipay";
    /*微信支付*/
    private static final String WECHATPAY = "wechatpay";

    @RequestMapping("/index")
    @Token(generate = true)
    public String index(PayAccountVo payAccountVo,Model model, HttpServletRequest request) {
        //获取收款账号
        PayAccount payAccount = getPayAccountById(payAccountVo.getSearch().getId());
        if (payAccount != null) {
            //是否隐藏收款账号
            isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_E_PAYMENT);
            model.addAttribute("rank", getRank());
            model.addAttribute("currency", getCurrencySign());

            String rechargeType = getRechargeType(payAccount.getBankCode());
            model.addAttribute("rechargeType",rechargeType);
            model.addAttribute("validateRule", JsRuleCreator.create(CompanyElectronicDepositForm.class));
            //上一次填写的账号/昵称
            model.addAttribute("lastTimeAccount", getLastDepositName(rechargeType,SessionManager.getUserId()));
        }
        model.addAttribute("payAccount", payAccount);
        return "/deposit/Electronic";
    }

    @RequestMapping("/deposit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> deposit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyElectronicDepositForm form,
                                      BindingResult result) {
        return commonDeposit(playerRechargeVo,result);
    }

    /**
     * 保存存款数据
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        playerRecharge.setRechargeTypeParent(RechargeTypeParentEnum.COMPANY_DEPOSIT.getCode());
        if (playerRecharge.getCounterFee() == null) {
            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
        }

        //存款总额（存款金额+手续费）>0才能继续执行
        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }


        playerRecharge.setPlayerId(SessionManager.getUserId());
        playerRecharge.setMasterBankType(payAccount.getAccountType());
        playerRecharge.setPayAccountId(payAccount.getId());
        if (StringTool.isBlank(playerRecharge.getPayerBank())) {
            playerRecharge.setPayerBank(payAccount.getBankCode());
        }
        //ip处理
        playerRecharge.setIpDeposit(SessionManagerBase.getIpDb().getIp());
        playerRecharge.setIpDictCode(SessionManagerBase.getIpDictCode());

        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setRankId(rank.getId());
        playerRechargeVo.setCustomBankName(payAccount.getCustomBankName());

        return playerRechargeVo;
    }

    private String getRechargeType(String bankCode) {
        String rechargeType = RechargeTypeEnum.OTHER_FAST.getCode();
        if (WECHATPAY.equals(bankCode)) {
            rechargeType = RechargeTypeEnum.WECHATPAY_FAST.getCode();
        } else if (ALIPAY.equals(bankCode)) {
            rechargeType = RechargeTypeEnum.ALIPAY_FAST.getCode();
        }
        return rechargeType;
    }

    private String getLastDepositName(String rechargeType,Integer userId) {
        PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
        PlayerRecharge playerRecharge = new PlayerRecharge();
        playerRecharge.setRechargeType(rechargeType);
        playerRecharge.setPlayerId(userId);
        playerRechargeVo.setResult(playerRecharge);
        return ServiceTool.playerRechargeService().searchLastPayerBankcard(playerRechargeVo);
    }
}
