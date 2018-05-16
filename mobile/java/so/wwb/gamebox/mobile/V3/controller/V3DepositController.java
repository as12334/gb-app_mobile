package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.V3.helper.DepositControllerHelper;
import so.wwb.gamebox.mobile.deposit.controller.BaseCommonDepositController;
import so.wwb.gamebox.mobile.deposit.form.DepositForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.digiccy.po.DigiccyAccountInfo;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.web.common.demomodel.DemoMenuEnum;
import so.wwb.gamebox.web.common.demomodel.DemoModel;

import java.util.*;

@Controller
@RequestMapping("/wallet/v3/deposit")
public class V3DepositController extends BaseCommonDepositController {

    @RequestMapping("/index")
    @DemoModel(menuCode = DemoMenuEnum.CKZQ)
    @Upgrade(upgrade = true)
    public String index(Model model, Integer skip) {
        model.addAttribute("skip", skip);
        //根据用户取得存款通道及账号信息
        PayAccountListVo payAccountListVo = new PayAccountListVo();
        payAccountListVo.getSearch().setTerminal(TerminalEnum.MOBILE.getCode());
        payAccountListVo.setPlayerId(SessionManager.getUserId());
        payAccountListVo.setCurrency(SessionManager.getUser().getDefaultCurrency());
        Map<String, Long> channelCountMap = ServiceSiteTool.payAccountService().queryChannelCount(payAccountListVo);
        model.addAttribute("map", channelCountMap);
        //是否支持数字货币
        DigiccyAccountInfo digiccyAccountInfo = ParamTool.getDigiccyAccountInfo();
        model.addAttribute("digiccyAccountInfo", digiccyAccountInfo);
        //快速充值地址
        String fastRechargeUrl = getFastRechargeUrl();
        model.addAttribute("rechargeUrlParam", fastRechargeUrl);
        return "/deposit/index/Deposit";
    }

    @Override
    protected String getFastRechargeUrl() {
        String url = super.getFastRechargeUrl();
        if (StringTool.isNotBlank(url)) {
            if (!url.startsWith("http")) {
                url = "http://" + url;
            }
        }
        return url;
    }

}
