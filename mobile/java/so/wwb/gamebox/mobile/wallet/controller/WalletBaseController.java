package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.VUserPlayerVo;
import so.wwb.gamebox.web.api.controller.BaseApiServiceController;
import so.wwb.gamebox.common.cache.Cache;

/**
 * 钱包基类
 * Created by fei on 16-7-29.
 */
@Controller
public abstract class WalletBaseController extends BaseApiServiceController{

    @RequestMapping("/index")
    protected abstract String index(Model model);

    @RequestMapping("/getPlayer")
    @ResponseBody
    public VUserPlayer getPlayer() {
        Integer userId = SessionManager.getUserId();
        if (userId != null) {
            VUserPlayerVo vo = new VUserPlayerVo();
            vo.getSearch().setId(userId);
            VUserPlayer player = ServiceSiteTool.vUserPlayerService().queryPlayer4App(vo);
            if (player != null) {
                player.setCurrencySign(getCurrencySign(player.getDefaultCurrency()));
            }
            return player;
        }
        return null;
    }

    /**
     * 获取货币标志
     */
    public String getCurrencySign(String defaultCurrency) {
        if (StringTool.isNotBlank(defaultCurrency)) {
            SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
            if (sysCurrency != null) {
                return sysCurrency.getCurrencySign();
            }
        }
        return "";
    }
}
