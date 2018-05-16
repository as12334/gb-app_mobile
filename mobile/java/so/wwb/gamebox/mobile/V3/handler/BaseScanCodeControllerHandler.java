package so.wwb.gamebox.mobile.V3.handler;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.so.PayAccountSo;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseScanCodeControllerHandler {
    /**
     * 获取电子支付账号
     *
     * @param rank
     * @param bankCode
     * @param rechargeType
     * @return
     */
    protected List<PayAccount> getElectronicAccount(PlayerRank rank, String bankCode, String rechargeType) {
        //获取该渠道下电子支付账号
        List<PayAccount> payAccounts = getElectronicPayAccount(bankCode);
        if (CollectionTool.isEmpty(payAccounts)) {
            return null;
        }
        //电子支付是否展示多个支付
        boolean display = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        List<PayAccount> payAccountList;
        if (display) {
            payAccountList = getCompanyPayAccounts(payAccounts, rechargeType);
        } else {
            //默认只展示一个
            PayAccount payAccount = payAccounts.get(0);
            Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.FUND_RECHARGE_TYPE);
            payAccount.setAliasName(i18n.get(rechargeType));
            payAccountList = new ArrayList<>(1);
            payAccountList.add(payAccount);
        }
        return payAccountList;
    }

    /**
     * 根据bankCode获取电子支付收款账号
     *
     * @param bankCode
     * @return
     */
    protected List<PayAccount> getElectronicPayAccount(String bankCode) {
        PayAccountSo payAccountSo = new PayAccountSo();
        payAccountSo.setType(PayAccountType.COMMPANY_ACCOUNT_CODE);
        payAccountSo.setAccountType(PayAccountAccountType.THIRTY.getCode());
        payAccountSo.setBankCode(bankCode);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setSearch(payAccountSo);
        return searchPayAccount(payAccountListVo, null, null);
    }

    /**
     * 查询收款账号
     */
    protected List<PayAccount> searchPayAccount(PayAccountListVo payAccountListVo, Boolean supportAtmCounter, String[] accountTypes) {
        PayAccountSo payAccountSo = payAccountListVo.getSearch();
        return DepositAccountSearcher.getInstance().searchPayAccount(payAccountSo.getType(), payAccountSo.getAccountType(), supportAtmCounter, accountTypes, payAccountSo.getBankCode());

    }

    /**
     * 展示电子支付多个收款账号
     *
     * @param accounts
     * @return
     */
    protected List<PayAccount> getCompanyPayAccounts(List<PayAccount> accounts, String rechargeType) {
        if (CollectionTool.isEmpty(accounts)) {
            return null;
        }
        int size = accounts.size();
        int count = 1;
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.FUND_RECHARGE_TYPE);
        String bankName = i18n.get(rechargeType);
        String other = RechargeTypeEnum.OTHER_FAST.getCode();
        for (PayAccount payAccount : accounts) {
            if (StringTool.isBlank(payAccount.getAliasName())) {
                if (other.equals(rechargeType) || other.equals(rechargeType)) {
                    payAccount.setAliasName(payAccount.getCustomBankName());
                } else if (size > 1) {
                    payAccount.setAliasName(bankName + count);
                    count++;
                } else {
                    payAccount.setAliasName(bankName);
                }
            }
        }
        return accounts;
    }

    /**
     * 获取扫码支付对应收款帐号
     *
     * @param rank
     * @param accountType
     * @param accountTypes
     * @return
     */
    protected Map<String, PayAccount> getScanAccount(PlayerRank rank, String accountType, String[] accountTypes) {
        List<PayAccount> payAccounts = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType, null, accountTypes, null);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        return ServiceSiteTool.payAccountService().getScanAccount(payAccountListVo);
    }
}
