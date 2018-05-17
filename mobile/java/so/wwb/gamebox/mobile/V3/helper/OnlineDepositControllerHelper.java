package so.wwb.gamebox.mobile.V3.helper;

import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.support.IForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.support.DepositAccountSearcher;
import so.wwb.gamebox.mobile.deposit.form.DepositForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.company.enums.BankEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.*;
import java.util.List;

@Component
public class OnlineDepositControllerHelper extends BaseDepositControllerHelper<OnlineDepositControllerHelper.PayAccountOnline> {
    public List<PayAccountOnline> getPayAccounts(PlayerRank rank, String channel) {
        //查询在线转账账号
        List<PayAccount> payAccounts = DepositAccountSearcher.getInstance().searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), PayAccountAccountType.THIRTY.getCode(), null, null, null);
        //根据银行列表找到排在最前的账号信息
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setBanks(searchBank(BankEnum.TYPE_BANK.getCode()));
        Map<String, PayAccount> payAccountMap = ServiceSiteTool.payAccountService().getOnlineAccount(payAccountListVo);
        //根据银行和账号对应关系
        if (!MapTool.isEmpty(payAccountMap)) {
            Map<String, String> i18nMap = I18nTool.getDictsMap(SessionManagerBase.getLocale().toString()).get(Module.COMMON.getCode()).get(DictEnum.BANKNAME.getType());
            List<PayAccountOnline> results = new ArrayList<>();
            for (Map.Entry<String, PayAccount> bank : payAccountMap.entrySet()) {
                PayAccountOnline pay = new PayAccountOnline(bank.getKey(), i18nMap.get(bank.getKey()), bank.getValue());
                results.add(pay);
            }
            return results;
        }
        return new ArrayList<PayAccountOnline>();
    }

    /**
     * 界面转json 为了界面控制
     *
     * @param accouts
     * @return
     */
    public String getAccountJson(List<PayAccountOnline> accouts) {
        List<Map<String, String>> bankList = new ArrayList<>();
        PayAccountVo payAccountVo = new PayAccountVo();
        for (PayAccountOnline online : accouts) {
            Map<String, String> map = new HashMap<>(4, 1f);
            map.put("value", online.getBank());
            map.put("text", online.getBankName());
            map.put("min", online.getSingleDepositMin() == null ? null : CurrencyTool.formatCurrency(online.getSingleDepositMin()));
            map.put("max", online.getSingleDepositMax() == null ? null : CurrencyTool.formatCurrency(online.getSingleDepositMax()));
            map.put("account", payAccountVo.getSearchId(online.getId()));
            bankList.add(map);
        }
        return JsonTool.toJson(bankList);
    }


    /**
     * 页面输入验证器
     *
     * @return
     */
    public Class<? extends IForm> getIndexValidateFormClazz() {
        return DepositForm.class;
    }

    /**
     * 跳转页面
     *
     * @return
     */
    public String getIndexUrl() {
        return "/deposit/channel/Deposit.online";
    }

    public String getRechargeType(String channel) {
        return RechargeTypeEnum.ONLINE_DEPOSIT.getCode();
    }

    /**
     * 界面展现VO
     */
    public static class PayAccountOnline extends PayAccount {
        private static final long serialVersionUID = 9157968307775362412L;
        private String bank;
        private String bankName;

        public PayAccountOnline(String bank, String text, PayAccount account) {
            BeanUtils.copyProperties(account, this);
            this.bank = bank;
            this.bankName = text;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }
}
