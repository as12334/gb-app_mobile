package so.wwb.gamebox.mobile.V3.support.helper;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.web.support.IForm;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.mobile.V3.enums.ScanCodeTypeEnum;
import so.wwb.gamebox.mobile.V3.support.builder.IScanCodeControllerBuilder;
import so.wwb.gamebox.mobile.V3.support.helper.BaseDepositControllerHelper;
import so.wwb.gamebox.mobile.deposit.form.CompanyElectronicDepositForm;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDepositForm;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ScancodeDepositControllerHelper extends BaseDepositControllerHelper<ScancodeDepositControllerHelper.PayAccountScancode> {
    public List<PayAccountScancode> getPayAccounts(PlayerRank rank, String channel) {
        ScanCodeTypeEnum scanCodeEnum = ScanCodeTypeEnum.enumOf(channel);
        IScanCodeControllerBuilder handler = SpringTool.getBean(scanCodeEnum.getHandlerClazz());
        List<PayAccountScancode> payaccounts = new ArrayList<>();
        //获取扫码账号
        Map<String, PayAccount> scanAccount = handler.getScanAccount(rank);
        if (MapTool.isNotEmpty(scanAccount)) {
            for (String key : scanAccount.keySet()) {
                PayAccountScancode account = new PayAccountScancode("sacn", handler.getOnlineRechargeType(), scanAccount.get(key));
                account.setBankCode(key);
                payaccounts.add(account);
            }
        }
        //获取电子账号
        List<PayAccount> electronicAccount = handler.getElectronicAccount(rank);
        boolean display = rank != null && rank.getDisplayCompanyAccount() != null && rank.getDisplayCompanyAccount();
        if (!display) {
            electronicAccount = distinctAccountByBankCode(electronicAccount);
        } else {
            //相同账号设置别名
            electronicAccount = convertAliasName(electronicAccount);
        }
        if (CollectionTool.isNotEmpty(electronicAccount)) {
            for (PayAccount account : electronicAccount) {
                payaccounts.add(new PayAccountScancode("electroin", handler.getOnlineRechargeType(), account));
            }
        }
        return payaccounts;
    }

    public String getAccountJson(List<PayAccountScancode> accouts) {
        return null;
    }

    public Class<? extends IForm> getIndexValidateFormClazz() {
        return OnlineScanDepositForm.class;
    }

    public Class<? extends IForm> getSecondValidateFormClazz() {
        return CompanyElectronicDepositForm.class;
    }

    public String getIndexUrl() {
        return "/deposit/channel/Deposit.scanCode";
    }

    public String getNextStepUrl() {
        return "/deposit/channel/Deposit.scanCode.Electronic";
    }

    public String getRechargeType(String channel) {
        //扫码支付因为多个代码共用，故目前无效
        return null;
    }

    /**
     * 界面展现VO
     */
    public static class PayAccountScancode extends PayAccount {
        private static final long serialVersionUID = 9157968307775362412L;
        private String scanType;

        public PayAccountScancode(String scanType, String rechargeType, PayAccount account) {
            BeanUtils.copyProperties(account, this);
            this.scanType = scanType;
            super.setRechargeType(rechargeType);
        }

        public String getScanType() {
            return scanType;
        }

        public void setScanType(String scanType) {
            this.scanType = scanType;
        }
    }
}
