package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.locale.LocaleTool;
import org.soul.commons.support._Module;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.mobile.wallet.form.BankcardForm;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.vo.BankListVo;
import so.wwb.gamebox.model.master.player.vo.UserBankcardVo;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jerry on 16-8-4.
 * 银行卡管理控制器
 */
@Controller
@RequestMapping("/bankCard/submitBankCard.html")
public class BankCardController {

    @RequestMapping("/page/bankCard")
    public String bankCard(Model model){
        model.addAttribute("userBankCard",BankHelper.getUserBankcard());
        return "/withdraw/BankCard";
    }

    @RequestMapping("/page/addCard")
    @Token(generate = true)
    public String addCard(Model model, HttpServletRequest request){

        model.addAttribute("userBankCard",BankHelper.getUserBankcard());
        model.addAttribute("realName", SessionManager.getUser().getRealName());
        model.addAttribute("validateRule", JsRuleCreator.create(BankcardForm.class));
        model.addAttribute("action",request.getParameter("action"));
        return "/withdraw/AddCard";
    }

    @RequestMapping("/bankList")
    @ResponseBody
    public List<Map> bankList(){
        BankListVo bankListVo = BankHelper.getBankListVo();
        List<Map> maps = new ArrayList<>();
        Map map;
        for (Bank bank : bankListVo.getResult()) {
            map = new HashMap();
            map.put("value",bank.getBankName());
            map.put("text",bank.getBankShortName());
            maps.add(map);
        }
        return maps;
    }

    /**
     * 添加银行卡
     */
    @Token(valid = true)
    @RequestMapping("/submitBankCard")
    @ResponseBody
    public Map submitBankCard(UserBankcardVo bankcardVo,String realName,String action,
                              @Valid @FormModel BankcardForm bankcardForm,
                              BindingResult result) {
        Map<String, Object> map = new HashMap<>(3);
        if (result.hasErrors()) {
            map.put("state", false);
            map.put("msg", "请输入正确的信息");
            map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
            return map;
        }
        bankcardVo.setSuccess(false);

        bankcardVo.getResult().setUserId(SessionManager.getUserId());
        bankcardVo.getResult().setBankcardMasterName(realName);
        bankcardVo.getResult().setCreateTime(SessionManager.getDate().getNow());
        bankcardVo.getResult().setUseCount(0);
        bankcardVo.getResult().setUseStauts(false);
        bankcardVo.getResult().setIsDefault(true);
        boolean success = ServiceTool.userBankcardService().insertUserBankCard(bankcardVo);
        map.put("state", success);
        if (success) {
            SessionManager.refreshUser();
            map.put("msg", LocaleTool.tranMessage("common", "save.success"));
            map.put("action",action);
        } else {
            map.put("msg", LocaleTool.tranMessage("common", "save.failed"));
            map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
        }
        return map;
    }
    /**
     * 添加银行卡信息
     * @param bankcardVo 银行卡信息
     * @return bankcardVo
     */
    private UserBankcardVo insertBankcard(UserBankcardVo bankcardVo) {
        bankcardVo.getResult().setUserId(SessionManager.getUserId());
        bankcardVo.getResult().setBankcardMasterName(SessionManager.getUser().getUsername());
        bankcardVo.getResult().setCreateTime(SessionManager.getDate().getNow());
        bankcardVo.getResult().setUseCount(0);
        bankcardVo.getResult().setUseStauts(false);
        bankcardVo.getResult().setIsDefault(true);
        bankcardVo = ServiceTool.userBankcardService().insert(bankcardVo);

        if (bankcardVo.isSuccess()) {
            bankcardVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "save.success"));
        } else {
            bankcardVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "save.failed"));
        }
        return bankcardVo;
    }

    /**
     * 检测卡号是否存在
     */
    @RequestMapping(value = "/checkCardIsExists")
    @ResponseBody
    public String checkCardIsExists(@RequestParam("result.bankcardNumber") String bankcardNumber) {
        return BankHelper.checkUserCardIsExists(bankcardNumber)?"false":"true";
    }
}
