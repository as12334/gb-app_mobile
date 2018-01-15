package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.tag.ImageTag;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.common.CommonApp;
import so.wwb.gamebox.mobile.app.model.ActivityTypeApp;
import so.wwb.gamebox.mobile.app.model.ActivityTypeListApp;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
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

    protected List<ActivityTypeListApp> getActivityMessageList(String key, HttpServletRequest request) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        List<SiteI18n> siteI18nTemp = ListTool.newArrayList();
        List<VActivityMessage> vActivityMessageList = ListTool.newArrayList();
        List<ActivityTypeListApp> listApps = ListTool.newArrayList();
        for (SiteI18n site : siteI18nMap.values()) {
            if(StringTool.equalsIgnoreCase(site.getLocale(), SessionManager.getLocale().toString())){
                VActivityMessageListVo vActivityMessageListVo = new VActivityMessageListVo();
                vActivityMessageListVo.getSearch().setActivityClassifyKey(site.getKey());
                vActivityMessageList = setDefaultImage(getActivityMessage(vActivityMessageListVo),request);
                siteI18nTemp.add(site);
            }
        }

        for (VActivityMessage message : vActivityMessageList) {
            ActivityTypeListApp app = new ActivityTypeListApp();
            if (StringTool.isNotBlank(key)) {
                    if (StringTool.equalsIgnoreCase(key, message.getClassifyKeyName())) {
                        app.setPhoto(message.getActivityCover());
                        app.setUrl("promo/promoDetail.html?search.id=" + message.getId());
                        listApps.add(app);
                    }
            } else {
                message.getActivityCover();
                app.setPhoto(message.getActivityCover());
                app.setUrl("promo/promoDetail.html?search.id=" + message.getId());
                listApps.add(app);
            }

        }
        return listApps;
    }


    protected String getActivityMessageType(HttpServletRequest request) {
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
            activityTypeApp.setActivityTypeName(i18n.getValue());
            activityTypeApp.setActivityKey(i18n.getKey());
            activityTypeApps.add(activityTypeApp);
        }

        AppModelVo vo = CommonApp.buildAppModelVo(activityTypeApps);

        return JsonTool.toJson(vo);
    }

}
