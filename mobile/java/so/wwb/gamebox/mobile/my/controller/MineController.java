package so.wwb.gamebox.mobile.my.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.net.ServletTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.player.po.PlayerApi;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的
 *
 * @author Fei
 * @date 2016-07-28
 */
@Controller
@RequestMapping("/mine")
public class MineController extends BaseMineController {
    private static Log LOG = LogFactory.getLog(MineController.class);
    private static final String MY_INDEX = "/mine/Mine";
    private static final String GAME_PAGE = "/my/GamePage";

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model, Integer skip) {
        model.addAttribute("skip", skip);
        model.addAttribute("channel", "mine");
        //现金取款方式
        model.addAttribute("isBit", ParamTool.isBit());
        model.addAttribute("isCash", ParamTool.isCash());
        return MY_INDEX;
    }

    @RequestMapping("/gamePage")
    public String gamePage(Model model, HttpServletRequest request) {
        String url = request.getParameter("url");
        if (StringTool.isNotBlank(url)) {
            model.addAttribute("url", ServletTool.formatUrl(url));
        }
        return GAME_PAGE;
    }

    /**
     * 获取我的个人数据(异步加载)
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    @Upgrade(upgrade = true)
    public String getFund(HttpServletRequest request) {

        Map<String, Object> userInfo = new HashMap<>();
        getMineLinkInfo(userInfo,request);

        return JsonTool.toJson(userInfo);
    }

    @RequestMapping("/getBalance")
    @ResponseBody
    public String getBalance() {
        Integer userId = SessionManager.getUserId();
        double balance = getWalletBalance(userId);
        //纯彩票站点查询api余额
        if (ParamTool.isLotterySite()) {
            PlayerApiVo playerApiVo = new PlayerApiVo();
            playerApiVo.getSearch().setPlayerId(userId);
            playerApiVo.getSearch().setApiId(NumberTool.toInt(ApiProviderEnum.PL.getCode()));
            playerApiVo = ServiceSiteTool.playerApiService().search(playerApiVo);
            PlayerApi playerApi = playerApiVo.getResult();
            if (playerApi != null && playerApi.getMoney() != null) {
                balance = balance + playerApi.getMoney();
            }
            return CurrencyTool.formatCurrency(balance);
        }
        return CurrencyTool.formatCurrency(balance);
    }
}
