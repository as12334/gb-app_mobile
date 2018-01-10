package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.tag.ImageTag;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.model.ActivityTypeApp;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.enums.ActivityStateEnum;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by legend on 17-12-31.
 */
public class BaseDiscountsController {


    /**
     * 获取正在进行中的活动
     */
    protected VActivityMessageListVo getActivityMessage(VActivityMessageListVo vActivityMessageListVo ){
        vActivityMessageListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vActivityMessageListVo.getSearch().setIsDeleted(Boolean.FALSE);
        vActivityMessageListVo.getSearch().setIsDisplay(Boolean.TRUE);
        vActivityMessageListVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        //vActivityMessageListVo.getPaging().setPageSize(pageSize);
        vActivityMessageListVo.getSearch().setActivityClassifyKey(vActivityMessageListVo.getSearch().getActivityClassifyKey());
        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            vActivityMessageListVo.getSearch().setRankId(ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo).getId());
        }
        vActivityMessageListVo = ServiceSiteTool.vActivityMessageService().getActivityList(vActivityMessageListVo);
        return vActivityMessageListVo;
    }

    protected List<VActivityMessage> setDefaultImage(VActivityMessageListVo vActivityMessageListVo, HttpServletRequest request){
        for(VActivityMessage a : vActivityMessageListVo.getResult()){
            String resRootFull = MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName());
            String activityAffiliated = ImageTag.getImagePathWithDefault(request.getServerName(),a.getActivityAffiliated(),resRootFull.concat("'/images/img-sale1.jpg'"));
            a.setActivityAffiliated(activityAffiliated);
        }
        return vActivityMessageListVo.getResult();
    }

    protected List<ActivityTypeApp> getActivity(HttpServletRequest request){
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();

        Map<String,List<VActivityMessage>> activityMessage = MapTool.newHashMap();
        List<SiteI18n> siteI18nTemp = ListTool.newArrayList();

        List<ActivityTypeApp> activityTypeApps = ListTool.newArrayList();

        for (SiteI18n site : siteI18nMap.values()) {
            if(StringTool.equalsIgnoreCase(site.getLocale(), SessionManager.getLocale().toString())){
                VActivityMessageListVo vActivityMessageListVo = new VActivityMessageListVo();
                vActivityMessageListVo.getSearch().setActivityClassifyKey(site.getKey());
                activityMessage.put(site.getKey(),setDefaultImage(getActivityMessage(vActivityMessageListVo),request));

                siteI18nTemp.add(site);
            }
        }

        for (SiteI18n i18n : siteI18nTemp) {
            ActivityTypeApp activityTypeApp = new ActivityTypeApp();

            activityTypeApp.setId(i18n.getId());
            activityTypeApp.setModule(i18n.getModule());
            activityTypeApp.setType(i18n.getType());
            activityTypeApp.setKey(i18n.getKey());
            activityTypeApp.setLocale(i18n.getLocale());
            activityTypeApp.setValue(i18n.getValue());
            activityTypeApp.setSiteId(i18n.getSiteId());
            activityTypeApp.setRemark(i18n.getRemark());
            activityTypeApp.setDefaultValue(i18n.getDefaultValue());
            activityTypeApp.setBuiltIn(i18n.getBuiltIn());
            activityTypeApp.setCacheKey(i18n.getCacheKey());
            activityTypeApp.setCacheKeyLocale(i18n.getCacheKeyLocale());

            Iterator<String> iterator = activityMessage.keySet().iterator();
            Map<String,List<VActivityMessage>> messageMap = MapTool.newHashMap();
            while(iterator.hasNext()){
                String key = iterator.next();
                if (StringTool.equalsIgnoreCase(activityTypeApp.getKey(),key)) {
                    messageMap.put("list", activityMessage.get(key));
                    activityTypeApp.setTypeMessageMap(messageMap);
                }
            }

            activityTypeApps.add(activityTypeApp);
        }

        return activityTypeApps;
    }

}
