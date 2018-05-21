package so.wwb.gamebox.mobile.V3.support.builder;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.so.PayAccountSo;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;
import java.util.Map;

public class BaseScanCodeControllerBuilder {
    /**
     * 获取电子支付账号
     *
     * @param bankCode
     * @return
     */
    protected List<PayAccount> getElectronicAccount(String bankCode) {
        //获取该渠道下电子支付账号
        return searchElectroinAccount(bankCode);
    }

    /**
     * 根据bankCode获取电子支付收款账号
     *
     * @param bankCode
     * @return
     */
    protected List<PayAccount> searchElectroinAccount(String bankCode) {
        //根据公司入款是否显示多个账号控制账号的返回
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
     * 获取扫码支付对应收款帐号
     *
     * @param rank
     * @param accountType
     * @param accountTypes
     * @return
     */
    protected Map<String, PayAccount> getScanAccount(PlayerRank rank, String accountType, String[] accountTypes) {
        //根据线上支付是否显示多个账号控制账号的返回
        List<PayAccount> payAccounts = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType, null, accountTypes, null);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        return ServiceSiteTool.payAccountService().getScanAccount(payAccountListVo);
    }
}
