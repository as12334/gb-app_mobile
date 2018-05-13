package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.deposit.controller.BaseOnlineDepositController;
import so.wwb.gamebox.mobile.deposit.controller.OnlineScanDepositController;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDeposit2Form;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDepositForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.enums.PayAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.common.token.Token;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * create by hanson 18-05-13
 */
@Controller
@RequestMapping("/wallet/deposit/online/scan")
public class V3OnlineScanDePositController extends BaseOnlineDepositController {
    /*支付宝*/
    private static final String ALIPAY = "alipay";
    /*微信*/
    private static final String WECHATPAY = "wechatpay";
    /*QQ钱包*/
    private static final String QQWALLET = "qqwallet";
    /*京东*/
    private static final String JDWALLET = "jdwallet";
    /*百度*/
    private static final String BDWALLET = "bdwallet";
    /*银联*/
    private static final String UNIONPAY = "unionpay";
    private Log LOG = LogFactory.getLog(V3OnlineScanDePositController.class);

    /**
     * 扫码支付
     */
    @RequestMapping("/scanCode/{type}")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String scanCode(Model model, @PathVariable String type) {
        PlayerRank rank = getRank();
        Map<String, PayAccount> scanAccount = null;
        switch (type) {
            case ALIPAY:
                scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.ALIPAY.getCode(),PayAccountAccountType.ALIPAY_MICROPAY.getCode()});
                break;
            case WECHATPAY:
                scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.WECHAT.getCode(),PayAccountAccountType.WECHAT_MICROPAY.getCode()});
                break;
            case QQWALLET:
                scanAccount = getScanAccount(rank, null, new String[]{PayAccountAccountType.QQWALLET.getCode(),PayAccountAccountType.QQ_MICROPAY.getCode()});
                break;
            case JDWALLET:
                break;
            case BDWALLET:
                break;
            case UNIONPAY:
                break;
            case BankCodeEnum.CODE_WECHAT_MICROPAY:
                break;
            case BankCodeEnum.CODE_ALIPAY_MICROPAY:
                break;
            case BankCodeEnum.CODE_QQ_MICROPAY:
            case BankCodeEnum.CODE_EASY_PAY:
                break;
            default:
                break;
        }
        model.addAttribute("scan", scanAccount);
        return "/deposit/ScanCode";
    }

    private Map<String, PayAccount> getScanAccount(PlayerRank rank, String accountType, String[] accountTypes) {
        List<PayAccount> payAccounts = searchPayAccount(PayAccountType.ONLINE_ACCOUNT.getCode(), accountType, accountTypes);
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.setResult(payAccounts);
        payAccountListVo.setPlayerRank(rank);
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        return ServiceSiteTool.payAccountService().getScanAccount(payAccountListVo);
    }

    @RequestMapping("/scanCodeSubmit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> ScanCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result, HttpServletRequest request) {
        return commonDeposit(playerRechargeVo, result, request);
    }

    /**
     * 开启随机额度时提交方法
     *
     * @param playerRechargeVo
     * @param form
     * @param result
     * @return
     */
    @RequestMapping("/scanRandomCodeSubmit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> scanRandomCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDeposit2Form form, BindingResult result, HttpServletRequest request) {
        return commonDeposit(playerRechargeVo, result, request);
    }
}
