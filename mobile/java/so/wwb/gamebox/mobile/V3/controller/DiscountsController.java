package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.common.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 */
@Controller
@RequestMapping("/discounts")
public class DiscountsController {

    private static final Log LOG = LogFactory.getLog(DiscountsController.class);

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model, Integer skip, HttpServletRequest request) {
        model.addAttribute("skip", skip);
        //活动分类
        getActivityClassifyMap(model);
        //活动
        getActivities(model);
        //用于活动id加密
        model.addAttribute("command", new VActivityMessageVo());
        return "/discounts/Promo";
    }

    /**
     * 获取活动分类名称
     *
     * @param model
     */
    private void getActivityClassifyMap(Model model) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        if (MapTool.isEmpty(siteI18nMap)) {
            return;
        }
        String locale = SessionManager.getLocale().toString();
        Map<String, String> activityClassifyMap = new HashMap<>();
        for (SiteI18n siteI18n : siteI18nMap.values()) {
            if (siteI18n.getLocale().equals(locale)) {
                activityClassifyMap.put(siteI18n.getKey(), siteI18n.getValue());
            }
        }
        model.addAttribute("activityClassifyMap", activityClassifyMap);
    }

    /**
     * 获取正在进行中、将来进行的活动
     *
     * @param model
     */
    private void getActivities(Model model) {
        Map<String, PlayerActivityMessage> activityMap = Cache.getMobileActivityMessages();
        if (MapTool.isEmpty(activityMap)) {
            return;
        }
        Map<String, List<PlayerActivityMessage>> activityGroupByClassify = new HashMap<>();
        //进行中和将来进行中活动展示
        long time = SessionManager.getDate().getNow().getTime();
        String classify;
        String locale = SessionManager.getLocale().toString();
        List<PlayerActivityMessage> activityMessages = new ArrayList<>();
        for (PlayerActivityMessage playerActivityMessage : activityMap.values()) {
            if (playerActivityMessage.getIsDisplay() == null) {
                continue;
            }
            if (!playerActivityMessage.getIsDisplay()) {
                continue;
            }
            if (!locale.equals(playerActivityMessage.getActivityVersion())) {
                continue;
            }
            if (playerActivityMessage.getEndTime() != null
                    && playerActivityMessage.getEndTime().getTime() < time) {
                continue;
            }
            classify = playerActivityMessage.getActivityClassifyKey();
            if (activityGroupByClassify.get(classify) == null) {
                activityGroupByClassify.put(classify, new ArrayList<>());
            }
            activityGroupByClassify.get(classify).add(playerActivityMessage);
            activityMessages.add(playerActivityMessage);
        }
        model.addAttribute("activityMessages", activityMessages);
        model.addAttribute("activityMap", activityGroupByClassify);
    }

}
