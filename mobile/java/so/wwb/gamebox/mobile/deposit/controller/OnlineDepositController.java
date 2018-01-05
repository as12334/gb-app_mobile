package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.deposit.form.DepositForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.company.enums.BankEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;

import javax.validation.Valid;
import java.util.*;

/**
 * Created by bruce on 16-8-9.
 */
@Controller
@RequestMapping("/wallet/deposit/online")
public class OnlineDepositController extends BaseOnlineDepositController {

    private static final String ONLINE_URI = "/deposit/Online";

    @RequestMapping("/index")
    @Token(generate = true)
    protected String index(Model model) {
        PlayerRank pr = getRank();
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode());
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        payAccountListVo.setPlayerRank(pr);
        payAccountListVo.setBanks(searchBank(BankEnum.TYPE_BANK.getCode()));
        Map<String, PayAccount> payAccountMap = ServiceSiteTool.payAccountService().getOnlineAccount(payAccountListVo);
        model.addAttribute("validateRule", JsRuleCreator.create(DepositForm.class));
        //收款账号
        getOnlineAccounts(model, payAccountMap);
        model.addAttribute("rank", pr);
        model.addAttribute("currency", getCurrencySign());
        model.addAttribute("rechargeType", RechargeTypeEnum.ONLINE_DEPOSIT.getCode());
        model.addAttribute("payAccountMap", payAccountMap);
        model.addAttribute("command", payAccountListVo);
        return ONLINE_URI;
    }

    private void getOnlineAccounts(Model model, Map<String, PayAccount> payAccountMap) {
        List<Map<String, String>> bankList = new ArrayList<>();
        Map<String, String> i18nMap = I18nTool.getDictsMap(SessionManagerBase.getLocale().toString())
                .get(Module.COMMON.getCode()).get(DictEnum.BANKNAME.getType());
        Set<Map.Entry<String, PayAccount>> entrySet = payAccountMap.entrySet();
        PayAccountVo payAccountVo = new PayAccountVo();
        for (Map.Entry<String, PayAccount> entry : entrySet) {
            String bankCode = entry.getKey();
            PayAccount payAccount = entry.getValue();
            Map<String, String> map = new HashMap<>(4, 1f);
            map.put("value", bankCode);
            map.put("text", i18nMap.get(bankCode));
            map.put("min", payAccount.getSingleDepositMin() == null ? null : CurrencyTool.formatCurrency(payAccount.getSingleDepositMin()));
            map.put("max", payAccount.getSingleDepositMax() == null ? null : CurrencyTool.formatCurrency(payAccount.getSingleDepositMax()));
            map.put("account", payAccountVo.getSearchId(payAccount.getId()));
            bankList.add(map);
        }
        model.addAttribute("bankList", bankList);
        model.addAttribute("bankJson", JsonTool.toJson(bankList));

    }

    @RequestMapping("/deposit")
    @ResponseBody
    @Token(valid = true)
    public Map deposit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid DepositForm form, BindingResult result) {
        return commonDeposit(playerRechargeVo, result);
    }

    /**
     * 查询玩家可选择的存款渠道
     *
     * @param type
     */
    private List<Bank> searchBank(String type) {
        Map<String, Bank> bankMap = Cache.getBank();
        if (bankMap == null || bankMap.size() <= 0) {
            return null;
        }
        return CollectionQueryTool.query(bankMap.values(), Criteria.add(Bank.PROP_TYPE, Operator.EQ, type));
    }
}
