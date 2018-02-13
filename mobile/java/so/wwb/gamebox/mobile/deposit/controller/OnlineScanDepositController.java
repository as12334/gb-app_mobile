package so.wwb.gamebox.mobile.deposit.controller;

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
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDeposit2Form;
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDepositForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.enums.BankCodeEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;

import javax.validation.Valid;
import java.util.Map;

/**
 * Created by bruce on 16-12-7.
 */
@Controller
@RequestMapping("/wallet/deposit/online/scan")
public class OnlineScanDepositController extends BaseOnlineDepositController {
    private Log LOG = LogFactory.getLog(OnlineScanDepositController.class);

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

    /**
     * 扫码支付
     */
    @RequestMapping("/scanCode/{type}")
    @Token(generate = true)
    @Upgrade(upgrade = true)
    public String scanCode(Model model, @PathVariable String type) {
        PlayerRank rank = getRank();
        PayAccount payAccountForScan = null;
        String scanPay = null;
        switch (type) {
            case ALIPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.ALIPAY.getCode(), RechargeTypeEnum.ALIPAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.ALIPAY_SCAN.getCode();
                break;
            case WECHATPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.WECHAT.getCode(), RechargeTypeEnum.WECHATPAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.WECHATPAY_SCAN.getCode();
                break;
            case QQWALLET:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.QQWALLET.getCode(), RechargeTypeEnum.QQWALLET_SCAN.getCode());
                scanPay = RechargeTypeEnum.QQWALLET_SCAN.getCode();
                break;
            case JDWALLET:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.JD_PAY.getCode(), RechargeTypeEnum.JDPAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.JDPAY_SCAN.getCode();
                break;
            case BDWALLET:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.BAIFU_PAY.getCode(), RechargeTypeEnum.BDWALLET_SAN.getCode());
                scanPay = RechargeTypeEnum.BDWALLET_SAN.getCode();
                break;
            case UNIONPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.UNION_PAY.getCode(), RechargeTypeEnum.UNION_PAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.UNION_PAY_SCAN.getCode();
                break;
            case BankCodeEnum.CODE_WECHAT_MICROPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.WECHAT_MICROPAY.getCode(), RechargeTypeEnum.WECHATPAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.WECHATPAY_SCAN.getCode();
                break;
            case BankCodeEnum.CODE_ALIPAY_MICROPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.ALIPAY_MICROPAY.getCode(), RechargeTypeEnum.ALIPAY_SCAN.getCode());
                scanPay = RechargeTypeEnum.ALIPAY_SCAN.getCode();
                break;
            case BankCodeEnum.CODE_QQ_MICROPAY:
                payAccountForScan = getScanPay(rank, PayAccountAccountType.QQ_MICROPAY.getCode(), RechargeTypeEnum.QQWALLET_SCAN.getCode());
                scanPay = RechargeTypeEnum.QQWALLET_SCAN.getCode();
                break;
            default:
                break;
        }
        model.addAttribute("scanPay", scanPay);
        model.addAttribute("payAccountForScan", payAccountForScan);
        model.addAttribute("currency", getCurrencySign());
        model.addAttribute("username", SessionManager.getUserName());
        //验证规则
        boolean isRandomAmount = payAccountForScan == null || payAccountForScan.getRandomAmount() == null || !payAccountForScan.getRandomAmount() ? false : payAccountForScan.getRandomAmount();
        model.addAttribute("validateRule", JsRuleCreator.create(isRandomAmount ? OnlineScanDeposit2Form.class : OnlineScanDepositForm.class));
        model.addAttribute("rank", rank);
        model.addAttribute("command", new PayAccountVo());
        return "/deposit/ScanCode";
    }

    @RequestMapping("/scanCodeSubmit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> ScanCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDepositForm form, BindingResult result) {
        return commonDeposit(playerRechargeVo, result);
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
    public Map<String, Object> scanRandomCodeSubmit(PlayerRechargeVo playerRechargeVo, @FormModel @Valid OnlineScanDeposit2Form form, BindingResult result) {
        return commonDeposit(playerRechargeVo, result);
    }

}
