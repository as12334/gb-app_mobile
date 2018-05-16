package so.wwb.gamebox.mobile.V3.support;

import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.springframework.stereotype.Component;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountListVo;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付账号查找器
 */
@Component
public class DepositAccountSearcher {

    public static DepositAccountSearcher getInstance() {
        return SpringTool.getBean(DepositAccountSearcher.class);
    }

    public List<PayAccount> searchPayAccount(String type, String accountType, Boolean supportAtmCounter, String[] accountTypes, String bankCode) {
        Map<String, Object> map = new HashMap<>(7, 1f);
        map.put("playerId", SessionManager.getUserId());
        map.put("type", type);
        map.put("accountType", accountType);
        map.put("currency", SessionManager.getUser().getDefaultCurrency());
        map.put("terminal", TerminalEnum.MOBILE.getCode());
        map.put("supportAtmCounter", supportAtmCounter);
        map.put("accountTypes", accountTypes);
        map.put(PayAccount.PROP_BANK_CODE, bankCode);
        PayAccountListVo listVo = new PayAccountListVo();
        listVo.setConditions(map);
        return ServiceSiteTool.payAccountService().searchPayAccountByRank(listVo);
    }

    public PayAccount searchById(Integer payAccountId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.getSearch().setId(payAccountId);
        payAccountVo = ServiceSiteTool.payAccountService().get(payAccountVo);
        return payAccountVo.getResult();
    }
}
