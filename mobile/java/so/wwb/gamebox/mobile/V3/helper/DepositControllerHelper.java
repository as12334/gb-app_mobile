package so.wwb.gamebox.mobile.V3.helper;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.query.sort.Order;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.enums.BankEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class DepositControllerHelper {
    public static DepositControllerHelper getInstance() {
        return SpringTool.getBean(DepositControllerHelper.class);
    }

    /**
     * 根据玩家获取可供存款使用的银行账号信息
     *
     * @return
     */
    public PayAccountListVo loadPayAccountListVo() {
        //可用银行
        List<Bank> banks = searchBank(BankEnum.TYPE_BANK.getCode());
        //层级
        PlayerRank rank = getRank();
        //玩家可用收款账号
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode(), TerminalEnum.MOBILE.getCode(), null, null);
        deleteMaintainChannel(payAccounts);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        payAccountListVo.setBanks(banks);
        return payAccountListVo;
    }

    /**
     * 获取主货币符号
     *
     * @return
     */
    public String getCurrencySign() {
        String defaultCurrency = SessionManager.getUser().getDefaultCurrency();
        if (StringTool.isBlank(defaultCurrency)) {
            return "";
        }
        SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 去除维护中收款账户
     *
     * @param payAccounts
     */
    private void deleteMaintainChannel(List<PayAccount> payAccounts) {
        Map<String, Bank> bankMap = CacheBase.getBank();
        Bank bank;
        Iterator<PayAccount> accountIterator = payAccounts.iterator();
        while (accountIterator.hasNext()) {
            PayAccount payAccount = accountIterator.next();
            bank = bankMap.get(payAccount.getBankCode());
            if (bank == null || (bank.getIsUse() != null && !bank.getIsUse())) {
                accountIterator.remove();
            }
        }
    }

    /**
     * 查询收款账号
     */
    public List<PayAccount> searchPayAccount(String type, String accountType, String terminal, Boolean supportAtmCounter, String[] accountTypes) {
        PayAccountListVo listVo = new PayAccountListVo();
        Map<String, Object> map = new HashMap<>(7, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", type);
        map.put("accountType", accountType);
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        if (StringTool.isNotBlank(terminal)) {
            map.put("terminal", terminal);
        }
        map.put("supportAtmCounter", supportAtmCounter);
        map.put("accountTypes", accountTypes);
        //map.put(PayAccount.PROP_BANK_CODE,)
        listVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
    }

    /**
     * 获取层级
     */
    public PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    /**
     * 查询玩家可选择的存款渠道
     *
     * @param type String
     */
    public List<Bank> searchBank(String type) {
        Map<String, Bank> bankMap = Cache.getBank();
        return CollectionQueryTool.query(bankMap.values(), Criteria.add(Bank.PROP_TYPE, Operator.EQ, type), Order.asc(Bank.PROP_ORDER_NUM));
    }
}
