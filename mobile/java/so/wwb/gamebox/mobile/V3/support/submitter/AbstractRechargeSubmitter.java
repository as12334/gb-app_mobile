package so.wwb.gamebox.mobile.V3.support.submitter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.soul.commons.bean.Pair;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.support._Module;
import org.springframework.validation.BindingResult;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.support.DepositTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.master.content.enums.PayAccountStatusEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public abstract class AbstractRechargeSubmitter {
    public DespositResult saveRecharge(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request) {
        Pair<Boolean, DespositResult> validate = validate(playerRechargeVo, result);
        if (!validate.getKey()) {
            return validate.getValue();
        }
        PayAccount account = validate.getValue().getAccount();
        //计算优惠信息
        playerRechargeVo = counterDiscounts(playerRechargeVo, account);
        //保存充值信息
        playerRechargeVo = fillPlayRecharge(playerRechargeVo, account, request);
        //保存充值订单
        playerRechargeVo = ServiceSiteTool.playerRechargeService().savePlayerRecharge(playerRechargeVo);
        //进行消息提示
        doNotice(playerRechargeVo);
        String payUrl = loadPayUrl(playerRechargeVo, account, request);
        return convertResult(playerRechargeVo, payUrl);
    }

    /**
     * 根据账号设置随机额度
     *
     * @param playerRechargeVo
     * @param payAccount
     * @return
     */
    protected PlayerRechargeVo counterDiscounts(PlayerRechargeVo playerRechargeVo, PayAccount payAccount) {
        return playerRechargeVo;
    }

    /**
     * 保存充值记录
     *
     * @return
     */
    protected abstract PlayerRechargeVo fillPlayRecharge(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request);

    /**
     * 进行消息提醒
     */
    protected abstract void doNotice(PlayerRechargeVo playerRechargeVo);

    /**
     * 返回第三方支付url
     *
     * @param playerRechargeVo
     * @param payAccount
     * @param request
     * @return
     */
    protected String loadPayUrl(PlayerRechargeVo playerRechargeVo, PayAccount payAccount, HttpServletRequest request) {
        return "";
    }

    /**
     * @param playerRechargeVo
     * @return
     */
    protected PlayerRechargeVo counterAndValidateFee(PlayerRechargeVo playerRechargeVo, PlayerRank rank) {
        playerRechargeVo.setSuccess(true);
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        if (playerRecharge.getCounterFee() == null) {
            double counterFee = DepositTool.calculateFee(rank, playerRecharge.getRechargeAmount());
            playerRecharge.setCounterFee(counterFee);
        }
        //存款总额（存款金额+手续费）>0才能继续执行
        if (playerRecharge.getCounterFee() + playerRecharge.getRechargeAmount() <= 0) {
            playerRechargeVo.setSuccess(false);
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_AMOUNT_LT_FEE));
            return playerRechargeVo;
        }
        return playerRechargeVo;
    }

    /**
     * 转换保存结果返回到页面
     *
     * @param playerRechargeVo
     * @return
     */
    protected DespositResult convertResult(PlayerRechargeVo playerRechargeVo, String payUrl) {
        DespositResult result = new DespositResult();
        if (!playerRechargeVo.isSuccess()) {
            result.setToken(TokenHandler.generateGUID());
        } else {
            result.setOrderNo(playerRechargeVo.getResult().getTransactionNo());
        }
        if (playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getOkMsg())) {
            playerRechargeVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));

        } else if (!playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getErrMsg())) {
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }
        result.setState(playerRechargeVo.isSuccess());
        result.setPayUrl(payUrl);
        result.setMsg(StringTool.isNotBlank(playerRechargeVo.getOkMsg()) ? playerRechargeVo.getOkMsg() : playerRechargeVo.getErrMsg());
        return result;
    }

    /**
     * 提交钱验证
     *
     * @return
     */
    private Pair<Boolean, DespositResult> validate(PlayerRechargeVo playerRechargeVo, BindingResult bind) {
        Boolean first = true;
        DespositResult drs = new DespositResult();
        if (bind.hasErrors()) {
            //表单验证不通过
            first = false;
            drs.setState(false);
            drs.setToken(TokenHandler.generateGUID());
            drs.setMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
            first = false;
        } else {
            PayAccountVo payAccountVo = new PayAccountVo();
            payAccountVo.setSearchId(playerRechargeVo.getAccount());
            PayAccount payAccount = getPayAccountById(payAccountVo.getSearch().getId());
            if (payAccount == null || !PayAccountStatusEnum.USING.getCode().equals(payAccount.getStatus())) {
                drs.setState(false);
                drs.setOrderNo(playerRechargeVo.getResult().getTransactionNo());
                drs.setAccountNotUsing(true);
                drs.setMsg(LocaleTool.tranMessage(Module.FUND.getCode(), MessageI18nConst.RECHARGE_PAY_ACCOUNT_LOST));
            }
            drs.setAccount(payAccount);
        }
        return new Pair<Boolean, DespositResult>(first, drs);
    }

    /**
     * 根据id获取收款账号
     *
     * @param payAccountId
     * @return
     */
    private PayAccount getPayAccountById(Integer payAccountId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.getSearch().setId(payAccountId);
        payAccountVo.getSearch().setPlayerId(SessionManager.getUserId());
        payAccountVo = ServiceSiteTool.payAccountService().queryAccountByAccountIdAndPlayerId(payAccountVo);//查询可用的账户
        return payAccountVo.getResult();
    }

    /**
     * 支付结果状态
     */
    public static class DespositResult implements Serializable {
        private static final long serialVersionUID = -3763799197695761974L;
        private String token;
        private Boolean state;//处理状态
        private String msg;//处理结果
        private String orderNo;
        private Boolean accountNotUsing = false;
        private String payUrl;
        @JsonIgnore
        private PayAccount account;//交易账号

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Boolean getState() {
            return state;
        }

        public void setState(Boolean state) {
            this.state = state;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public Boolean getAccountNotUsing() {
            return accountNotUsing;
        }

        public void setAccountNotUsing(Boolean accountNotUsing) {
            this.accountNotUsing = accountNotUsing;
        }

        public PayAccount getAccount() {
            return account;
        }

        public void setAccount(PayAccount account) {
            this.account = account;
        }

        public String getPayUrl() {
            return payUrl;
        }

        public void setPayUrl(String payUrl) {
            this.payUrl = payUrl;
        }
    }
}
