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
import so.wwb.gamebox.mobile.deposit.form.OnlineScanDepositForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.enums.PayAccountAccountType;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
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
    private static final String BDWALLET ="bdwallet";
    /*银联*/
    private static final String UNIONPAY = "unionpay";

    /**
     * 扫码支付
     */
    @RequestMapping("/scanCode/{type}")
    @Token(generate = true)
    public String scanCode(Model model, @PathVariable String type) {
        PlayerRank rank = getRank();
        PayAccount payAccountForScan = null;
        if (ALIPAY.equals(type)) {
            //支付宝收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.ALIPAY.getCode(), RechargeTypeEnum.ALIPAY_SCAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.ALIPAY_SCAN.getCode());
        }
        if (WECHATPAY.equals(type)) {
            //微信支付收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.WECHAT.getCode(), RechargeTypeEnum.WECHATPAY_SCAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.WECHATPAY_SCAN.getCode());
        }
        if (QQWALLET.equals(type)) {
            //微信支付收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.QQWALLET.getCode(), RechargeTypeEnum.QQWALLET_SCAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.QQWALLET_SCAN.getCode());
        }
        if(JDWALLET.equals(type)){
            //京东支付收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.JD_PAY.getCode(), RechargeTypeEnum.JDPAY_SCAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.JDPAY_SCAN.getCode());
        }
        if(BDWALLET.equals(type)){
            //百度支付收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.BAIFU_PAY.getCode(), RechargeTypeEnum.BDWALLET_SAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.BDWALLET_SAN.getCode());
        }
        if(UNIONPAY.equals(type)){
            //银联支付收款账号
            payAccountForScan = getScanPay(rank, PayAccountAccountType.UNION_PAY.getCode(), RechargeTypeEnum.UNION_PAY_SCAN.getCode());
            model.addAttribute("scanPay", RechargeTypeEnum.UNION_PAY_SCAN.getCode());
        }
        model.addAttribute("payAccountForScan", payAccountForScan);
        model.addAttribute("currency", getCurrencySign());
        model.addAttribute("username", SessionManager.getUserName());

        //验证规则
        model.addAttribute("validateRule", JsRuleCreator.create(OnlineScanDepositForm.class));
        model.addAttribute("rank", rank);
        return "/deposit/ScanCode";
    }

    @RequestMapping("/scanCodeSubmit")
    @ResponseBody
    @Token(valid = true)
    public Map<String, Object> ScanCodeSubmit(PlayerRechargeVo playerRechargeVo,
                                              @FormModel @Valid OnlineScanDepositForm form, BindingResult result) {
        return commonDeposit(playerRechargeVo, result);
    }

    /**
     * 设置线上支付（含微信、支付宝）的session最后一次存款时间及次数
     */
    /*public void setRechargeCount() {
        Date date = SessionManager.getRechargeLastTime();
        Date nowDate = SessionManager.getDate().getNow();
        if (date == null) {
            SessionManager.setRechargeCount(1);
        } else if (DateTool.minutesBetween(date, nowDate) > RECHARGE_TIME) {
            SessionManager.setRechargeCount(1);
        } else {
            SessionManager.setRechargeCount(SessionManager.getRechargeCount() + 1);
        }
        SessionManager.setRechargeLastTime(nowDate);
    }*/

}
