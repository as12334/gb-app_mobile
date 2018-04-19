package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
@Controller
@RequestMapping("/discounts")
public class DiscountsController {

    private static final Log LOG = LogFactory.getLog(DiscountsController.class);
    private static final Integer pageSize = 8;

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model, Integer skip, HttpServletRequest request) {
        model.addAttribute("skip", skip);
        MobileActivityMessageVo messageVo = getActivity(request);
        model.addAttribute("messageVo", messageVo);
        return "/discounts/Promo";
    }


    private MobileActivityMessageVo getActivity(HttpServletRequest request) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        String locale = SessionManager.getLocale().toString();
        List<SiteI18n> siteI18nTemp = new ArrayList<>();
        MobileActivityMessageVo messageVo = new MobileActivityMessageVo();
        for (SiteI18n siteI18n : siteI18nMap.values()) {
            if(siteI18n.getLocale().equals(locale)) {
                siteI18nTemp.add(siteI18n);
            }
        }
        messageVo.setTypeList(siteI18nTemp);
        VActivityMessageListVo vActivityMessageListVo = new VActivityMessageListVo();
        vActivityMessageListVo = getActivityMessage(vActivityMessageListVo);
        List<VActivityMessage> vActivityMessages = vActivityMessageListVo.getResult();
        if (CollectionTool.isEmpty(vActivityMessages)) {
            return messageVo;
        }
        Map<String, List<VActivityMessage>> activityMessageByClassifyKey = CollectionTool.groupByProperty(vActivityMessages, VActivityMessage.PROP_ACTIVITY_CLASSIFY_KEY, String.class);
        messageVo.setTypeMessageMap(activityMessageByClassifyKey);
        return messageVo;
    }

    /**
     * 获取正在进行中的活动
     */
    private VActivityMessageListVo getActivityMessage(VActivityMessageListVo vActivityMessageListVo) {
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            PlayerRank playerRank = ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
            if(playerRank != null){
                vActivityMessageListVo.getSearch().setRankId(playerRank.getId());
            }
        }
        vActivityMessageListVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        vActivityMessageListVo.setPaging(null);
        vActivityMessageListVo = ServiceActivityTool.vActivityMessageService().getActivityList(vActivityMessageListVo);
        return vActivityMessageListVo;
    }


}
