package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.net.ServletTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 玩家交易表控制器
 *
 * @author jeff
 * @time 2015-10-30 15:43:35
 */
@Controller
@RequestMapping("/wallet/trading")
public class TradingController extends WalletBaseController {

    @Override
    protected String index(Model model) {
        return null;
    }

    @RequestMapping("/deposit")
    public String deposit(VPlayerTransactionListVo listVo, Model model, HttpServletRequest request) {
        model.addAttribute("channel", "trading");
        model.addAttribute("player", getPlayer());

        // 初始化ListVo
        initListVo(listVo, TransactionTypeEnum.DEPOSIT.getCode());
        model.addAttribute("command", ServiceTool.vPlayerTransactionService().search(listVo));

        return gotoPage(request, "Deposit");
    }

    @RequestMapping("/withdraw")
    public String Withdraw(VPlayerTransactionListVo listVo, Model model, HttpServletRequest request) {
        model.addAttribute("channel", "trading");
        model.addAttribute("player", getPlayer());

        // 初始化ListVo
        initListVo(listVo, TransactionTypeEnum.WITHDRAWALS.getCode());
        model.addAttribute("command", ServiceTool.vPlayerTransactionService().search(listVo));

        return gotoPage(request, "Withdraw");
    }

    /**
     * 初始化ListVon
     */
    private void initListVo(VPlayerTransactionListVo listVo, String code) {
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setTransactionType(code);
        listVo.getSearch().setBeginCreateTime(DateTool.addDays(SessionManager.getDate().getToday(), -6));
        listVo.getSearch().setEndCreateTime(SessionManager.getDate().getNow());
    }

    /**
     * 跳转页面
     */
    private String gotoPage(HttpServletRequest request, String type) {
        if (ServletTool.isAjaxSoulRequest(request)) {
            return "/wallet/trading/" + type + "Partial ";
        } else {
            return "/wallet/trading/" + type;
        }
    }

    @RequestMapping("/refreshPlayer")
    @ResponseBody
    public Map<String, Object> refreshPlayer() {
        Map<String, Object> map = new HashMap<>(2);
        VUserPlayer player = getPlayer();
        if (player != null) {
            map.put("walletBalance", CurrencyTool.formatCurrency(player.getWalletBalance()==null?0:player.getWalletBalance()));
            map.put("freezingBalance", CurrencyTool.formatCurrency(player.getFreezingBalance()==null?0:player.getFreezingBalance()));
        }
        return map;
    }

}