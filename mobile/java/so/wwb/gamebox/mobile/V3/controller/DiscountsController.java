package so.wwb.gamebox.mobile.V3.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dubbo.DubboTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.iservice.master.player.IPlayerRankService;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
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
    public String index(Model model) {
        return "/discounts/Promo";
    }

    /**
     * 获取活动类型
     */
    @RequestMapping("/activityType")
    @ResponseBody
    @Upgrade(upgrade = true)
    public String getActivityType(HttpServletRequest request) {
        String localLanguage = SessionManager.getLocale().toString();
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        Map<String, SiteI18n> tempMap = new LinkedHashMap<>();
        for (Map.Entry<String, SiteI18n> entry : siteI18nMap.entrySet()) {
            SiteI18n siteI18n = entry.getValue();
            if (localLanguage.equals(siteI18n.getLocale())) {
                tempMap.put(siteI18n.getKey(), siteI18n);
            }
        }

        VActivityMessageListVo vActivityMessageListVo = getActivityMessage(new VActivityMessageListVo());

        Map<String,Object> activity = MapTool.newHashMap();
        setMapValue(vActivityMessageListVo,activity,request);
        activity.put("type",tempMap);

        return JsonTool.toJson(activity);
    }

    private void setMapValue(VActivityMessageListVo vActivityMessageListVo,Map activity,HttpServletRequest request){
        for(VActivityMessage a : vActivityMessageListVo.getResult()){
            String resRootFull = MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName());
            String activityAffiliated = ImageTag.getImagePathWithDefault(request.getServerName(),a.getActivityAffiliated(),resRootFull.concat("'/images/img-sale1.jpg'"));
            a.setActivityAffiliated(activityAffiliated);
        }

        activity.put("message",vActivityMessageListVo.getResult());
        activity.put("pageNumber",vActivityMessageListVo.getPaging().getPageNumber());
        activity.put("lastPageNumber",vActivityMessageListVo.getPaging().getLastPageNumber());
    }

    /**
     * 获获取正在进行中的活动
     */
    @RequestMapping("/promo")
    @ResponseBody
    @Upgrade(upgrade = true)
    public String promo(VActivityMessageListVo vActivityMessageListVo ,String type,Integer pageNumber,HttpServletRequest request) {
        if(!StringTool.isBlank(type)){
            vActivityMessageListVo.getSearch().setActivityClassifyKey(type);
        }
        vActivityMessageListVo.getPaging().setPageNumber(pageNumber);
        vActivityMessageListVo = getActivityMessage(vActivityMessageListVo);

        Map<String,Object> activity = MapTool.newHashMap();
        setMapValue(vActivityMessageListVo,activity,request);

        return JsonTool.toJson(activity);
    }

    /**
     * 获取正在进行中的活动
     */
    private VActivityMessageListVo getActivityMessage(VActivityMessageListVo vActivityMessageListVo ){
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        vActivityMessageListVo.getPaging().setPageSize(pageSize);
        vActivityMessageListVo.getSearch().setActivityClassifyKey(vActivityMessageListVo.getSearch().getActivityClassifyKey());
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            vActivityMessageListVo.getSearch().setRankId(DubboTool.getService(IPlayerRankService.class).searchRankByPlayerId(sysUserVo).getId());
        }
        vActivityMessageListVo = ServiceTool.vActivityMessageService().getActivityList(vActivityMessageListVo);
        return vActivityMessageListVo;
    }

}
