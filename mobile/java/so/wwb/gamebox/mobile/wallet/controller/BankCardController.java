package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.vo.BankListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.fund.form.AddBankcardForm;
import so.wwb.gamebox.web.fund.form.BtcBankcardForm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jerry on 16-8-4.
 * 银行卡管理控制器
 */
@Controller
@RequestMapping("/bankCard")
public class BankCardController {
    @RequestMapping("/page/addCard")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String addCard(Model model, HttpServletRequest request) {
        model.addAttribute("userBankCard", BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BANK));
        model.addAttribute("realName", SessionManager.getUser().getRealName());
        model.addAttribute("validate", JsRuleCreator.create(AddBankcardForm.class));
        model.addAttribute("action", request.getParameter("action"));
        return "/withdraw/bankcard/Bankcard";
    }

    @RequestMapping("/bankList")
    @ResponseBody
    public List<Map> bankList() {
        BankListVo bankListVo = BankHelper.getBankListVo();
        List<Map> maps = new ArrayList<>();
        Map map;
        for (Bank bank : bankListVo.getResult()) {
            map = new HashMap();
            map.put("value", bank.getBankName());
            map.put("text", bank.getBankShortName());
            maps.add(map);
        }
        return maps;
    }

    @RequestMapping("/page/addBtc")
    @Token(generate = true)
    public String addBtc(Model model, HttpServletRequest request) {
        model.addAttribute("userBankCard", BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BTC));
        model.addAttribute("validate", JsRuleCreator.create(BtcBankcardForm.class));
        model.addAttribute("action", request.getParameter("action"));
        return "/withdraw/bankcard/Btc";
    }
}
