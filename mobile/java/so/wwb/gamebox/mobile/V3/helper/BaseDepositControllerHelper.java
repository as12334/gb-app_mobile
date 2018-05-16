package so.wwb.gamebox.mobile.V3.helper;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.web.support.IForm;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;

import java.util.*;

public abstract class BaseDepositControllerHelper<P extends PayAccount> implements IDepositControllerHelper<P> {

    protected List<PayAccount> convertAliasName(List<PayAccount> payAccounts) {
        if (CollectionTool.isEmpty(payAccounts)) {
            return null;
        }
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.BANKNAME);
        Map<String, List<PayAccount>> accountMap = CollectionTool.groupByProperty(payAccounts, PayAccount.PROP_BANK_CODE, String.class);
        for (PayAccount payAccount : payAccounts) {
            if (StringTool.isBlank(payAccount.getAliasName())) {
                String bankCode = payAccount.getBankCode();
                if (countMap.get(bankCode) == null) {
                    countMap.put(bankCode, 1);
                } else {
                    countMap.put(bankCode, countMap.get(bankCode) + 1);
                }
                if (BankCodeEnum.OTHER.getCode().equals(bankCode) || BankCodeEnum.OTHER_BANK.getCode().equals(bankCode)) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                } else {
                    if (accountMap.get(bankCode).size() > 1) {
                        payAccount.setAliasName(i18n.get(bankCode) + countMap.get(bankCode));
                    } else {
                        payAccount.setAliasName(i18n.get(bankCode));
                    }
                }
            }
        }
        return payAccounts;
    }

    /**
     * 针对银行类型去重
     *
     * @param payAccs
     * @return
     */
    protected List<PayAccount> distinctAccountByBankCode(List<PayAccount> payAccs) {
        if (CollectionTool.isEmpty(payAccs)) {
            return null;
        }
        List<PayAccount> distPays = new ArrayList<>();
        Set<String> bankSet = new HashSet<>();
        for (PayAccount acc : payAccs) {
            if (!bankSet.contains(acc.getBankCode())) {
                bankSet.add(acc.getBankCode());
                distPays.add(acc);
            }
        }
        return distPays;
    }

    /**
     * 查询符合玩家条件收款账号
     */
    protected List<PayAccount> searchPayAccount(String type, String accountType) {
        Map<String, Object> map = new HashMap<>(4, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", type);
        map.put("accountType", accountType);
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        map.put("terminal", TerminalEnum.MOBILE.getCode());
        PayAccountListVo listVo = new PayAccountListVo();
        listVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
    }

    /**
     * 查询玩家可选择的存款渠道
     *
     * @param type
     */
    protected List<Bank> searchBank(String type) {
        Map<String, Bank> bankMap = Cache.getBank();
        if (bankMap == null || bankMap.size() <= 0) {
            return null;
        }
        return CollectionQueryTool.query(bankMap.values(), Criteria.add(Bank.PROP_TYPE, Operator.EQ, type));
    }

    public String getAccountJson(List<P> accouts) {
        return null;
    }

    public String getNextStepUrl() {
        return null;
    }

    public Class<? extends IForm> getSecondValidateFormClazz() {
        return getIndexValidateFormClazz();
    }
}
