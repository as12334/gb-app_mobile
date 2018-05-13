package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.I18nTool;
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
import so.wwb.gamebox.mobile.V3.helper.DepositControllerHelper;
import so.wwb.gamebox.mobile.deposit.controller.BaseCompanyDepositController;
import so.wwb.gamebox.mobile.deposit.form.CompanyBankDeposit2CashForm;
import so.wwb.gamebox.mobile.deposit.form.CompanyBankDeposit2Form;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.BankEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeParentEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.ActivityDepositWay;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网银存款,银行柜台存款,柜员机转账,柜员机现金存款
 * <p/>
 * Created by hanson on 18-05-12.
 */
@Controller
@RequestMapping("/wallet/v3/deposit/company")
public class V3CompanyBankDepositControllor extends BaseCompanyDepositController {

    //公司入款支持的存款类型
    private static String[] RECHARGE_TYPE = {
            RechargeTypeEnum.ONLINE_BANK.getCode(),
            RechargeTypeEnum.ATM_COUNTER.getCode(),
            RechargeTypeEnum.ATM_MONEY.getCode(),
            RechargeTypeEnum.ATM_RECHARGE.getCode()
    };


    /**
     * 存款金额
     */
    @RequestMapping("/depositCash")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String depositCash(Model model) {
        DepositControllerHelper helper = DepositControllerHelper.getInstance();
        List<PayAccount> payAccounts = helper.searchPayAccount(PayAccountType.COMPANY_ACCOUNT.getCode(), PayAccountAccountType.BANKACCOUNT.getCode(), null, null, null);
        PlayerRank rank = helper.getRank();
        boolean display = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        //获取公司入款收款账号
        model.addAttribute("accounts", getCompanyPayAccount(payAccounts));
        model.addAttribute("rank", rank);
        model.addAttribute("currency", getCurrencySign());
        //验证规则
        model.addAttribute("validateRule", JsRuleCreator.create(CompanyBankDeposit2CashForm.class));
        model.addAttribute("isRealName", StringTool.isBlank(SessionManager.getUser().getRealName()));
        model.addAttribute("displayAccounts", display);
        isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_ONLINE_BANKING);
        //可用的网银转账链接
        model.addAttribute("bankList", searchBank(BankEnum.TYPE_BANK.getCode()));
        return "/deposit/DepositCompanyCash";
    }

    /**
     * 获取公司入款收款账号（根据固定顺序获取收款账户）
     *
     * @param accounts
     * @return
     */
    private List<PayAccount> getCompanyPayAccount(List<PayAccount> accounts) {
        List<String> bankCodes = new ArrayList<>();
        List<PayAccount> payAccounts = new ArrayList<>();
        for (PayAccount payAccount : accounts) {
            if (!bankCodes.contains(payAccount.getBankCode())) {
                payAccounts.add(payAccount);
                bankCodes.add(payAccount.getBankCode());
            }
        }
        return payAccounts;
    }

    @RequestMapping("/index")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String company(PayAccountVo payAccountVo, Model model) {
        //获取收款账号
        PayAccount payAccount = getPayAccountById(payAccountVo.getSearch().getId());
        if (payAccount != null) {
            model.addAttribute("rank", getRank());
            model.addAttribute("currency", getCurrencySign());
            //是否隐藏收款账号
            isHide(model, SiteParamEnum.PAY_ACCOUNT_HIDE_ONLINE_BANKING);
            model.addAttribute("bank", Cache.getBank().get(payAccount.getBankCode()));
            model.addAttribute("validateRule", JsRuleCreator.create(CompanyBankDeposit2Form.class));
            getRechargeType(model, payAccount);
        }
        if (payAccountVo.getDepositCash() != null) {
            model.addAttribute("rechargeAmount", payAccountVo.getDepositCash());
        }
        model.addAttribute("payAccount", payAccount);
        return "/deposit/Company";
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
    public Map<String, Object> deposit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid CompanyBankDeposit2Form form,
                                       BindingResult result) {
        return commonDeposit(playerRechargeVo, result);
    }

    /**
     * 保存提交存款
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    public PlayerRechargeVo saveRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        PlayerRank rank = getRank();
        if (playerRecharge.getCounterFee() == null) {
            playerRecharge.setCounterFee(calculateFee(rank, playerRecharge.getRechargeAmount()));
        }

        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }
        boolean flag = false;//判断传回来的数据类型是否属于银行存款类型
        String rechargeType = playerRecharge.getRechargeType();
        for (String type : RECHARGE_TYPE) {
            if (type.equals(rechargeType)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            playerRecharge.setRechargeType(RechargeTypeEnum.ONLINE_BANK.getCode());
        }
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

        playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
        playerRechargeVo.setSysUser(SessionManager.getUser());
        playerRechargeVo.setRankId(rank.getId());

        return playerRechargeVo;
    }

    /**
     * 获取存款类型
     *
     * @param model
     */
    private void getRechargeType(Model model, PayAccount payAccount) {
        List<Map<String, Object>> rechargeTypeList = new ArrayList<>(RECHARGE_TYPE.length);
        Map<String, String> i18nMap = I18nTool.getDictsMap(SessionManagerBase.getLocale().toString())
                .get(Module.FUND.getCode()).get(DictEnum.FUND_RECHARGE_TYPE.getType());
        for (String rechargeType : RECHARGE_TYPE) {
            if (payAccount.getSupportAtmCounter() != null && !payAccount.getSupportAtmCounter() && !RechargeTypeEnum.ONLINE_BANK.getCode().equals(rechargeType)) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("value", rechargeType);
            map.put("text", i18nMap.get(rechargeType));
            rechargeTypeList.add(map);
        }
        model.addAttribute("rechargeType", rechargeTypeList.get(0));
        model.addAttribute("rechargeTypeJson", JsonTool.toJson(rechargeTypeList));
    }

}
