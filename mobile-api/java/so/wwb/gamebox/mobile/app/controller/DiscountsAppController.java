package so.wwb.gamebox.mobile.app.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

@Controller
@RequestMapping("/discountsOrigin")
public class DiscountsAppController {
    private static final String ACTIVITY_DETAIL_URL = "/promo/promoDetail.html";
    private Log LOG = LogFactory.getLog(DiscountsAppController.class);

    /**
     * 获取优惠活动类型
     */
    @RequestMapping(value = "/getActivityType")
    @ResponseBody
    public String getActivityType() {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypes(), APP_VERSION);
    }

    /**
     * 获取优惠活动
     */
    @RequestMapping(value = "/getActivityTypeList")
    @ResponseBody
    public String getActivityType(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityMessages(listVo, request), APP_VERSION);
    }

    /**
     * 获取优惠活动和类型
     */
    @RequestMapping(value = "/getActivityTypes")
    @ResponseBody
    public String getActivityTypes(VActivityMessageListVo listVo, HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(), AppErrorCodeEnum.SUCCESS.getMsg(), getActivityTypeMessages(listVo, request), APP_VERSION);
    }

    /**
     * 获取活动类型
     *
     * @return types
     */
    private List<ActivityTypeApp> getActivityTypes() {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        List<ActivityTypeApp> types = new ArrayList<>();
        for (SiteI18n site : siteI18nMap.values()) {
            if (StringTool.equalsIgnoreCase(site.getLocale(), SessionManager.getLocale().toString())) {
                ActivityTypeApp type = new ActivityTypeApp();
                type.setActivityKey(site.getKey());
                type.setActivityTypeName(site.getValue());
                types.add(type);
            }
        }
        return types;
    }

    /**
     * 获取活动和类型
     */
    private Map getActivityTypeMessages(VActivityMessageListVo listVo, HttpServletRequest request) {
        List<ActivityTypeApp> types = ListTool.newArrayList();
        if (StringTool.isNotBlank(listVo.getSearch().getActivityClassifyKey())) {
            ActivityTypeApp typeApp = new ActivityTypeApp();
            typeApp.setActivityKey(listVo.getSearch().getActivityClassifyKey());
            types.add(typeApp);
        } else {
            types = getActivityTypes();
        }

        Map<String, Object> activityTypes = new HashMap<>(types.size(), ONE_FLOAT);
        for (ActivityTypeApp type : types) {
            listVo.getSearch().setActivityClassifyKey(type.getActivityKey());
            Map messages = getActivityMessages(listVo, request);

            activityTypes.put(type.getActivityKey(), messages);
        }
        return activityTypes;
    }

    /**
     * 获取正在进行中的活动 转换接口数据
     */
    private Map getActivityMessages(VActivityMessageListVo listVo, HttpServletRequest request) {
        listVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        listVo.getSearch().setIsDisplay(Boolean.TRUE);
        listVo.getSearch().setIsDeleted(Boolean.FALSE);
        listVo.getSearch().setStates(ActivityStateEnum.PROCESSING.getCode());
        if (StringTool.isNotBlank(listVo.getSearch().getActivityClassifyKey())) {
            listVo.getSearch().setActivityClassifyKey(listVo.getSearch().getActivityClassifyKey());
        }

        //通过玩家层级判断是否显示活动
        if (SessionManager.getUser() != null && !SessionManagerCommon.isLotteryDemo()) {
            SysUserVo sysUserVo = new SysUserVo();
            sysUserVo.getSearch().setId(SessionManager.getUserId());
            listVo.getSearch().setRankId(ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo).getId());
        }
        listVo = ServiceActivityTool.vActivityMessageService().getActivityList(listVo);
        listVo = setDefaultImages(listVo, request);

        //转换接口所需数据
        Map<String, Object> map = new HashMap<>(TWO, ONE_FLOAT);
        List<ActivityTypeListApp> messages = ListTool.newArrayList();
        for (VActivityMessage message : listVo.getResult()) {
            ActivityTypeListApp activityApp = new ActivityTypeListApp();
            activityApp.setId(message.getId());
            activityApp.setPhoto(message.getActivityAffiliated());
            activityApp.setUrl(ACTIVITY_DETAIL_URL + "?search.id=" + message.getId());
            activityApp.setName(message.getActivityName());
            messages.add(activityApp);
        }
        map.put("list", messages);
        map.put("total", listVo.getPaging().getTotalCount());

        return map;
    }

    /**
     * 没有图片为默认图片
     */
    private VActivityMessageListVo setDefaultImages(VActivityMessageListVo vActivityMessageListVo, HttpServletRequest request) {
        for (VActivityMessage a : vActivityMessageListVo.getResult()) {
            String resRootFull = MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName());
            String activityAffiliated = ImageTag.getImagePathWithDefault(request.getServerName(), a.getActivityAffiliated(), resRootFull.concat("'/images/img-sale1.jpg'"));
            a.setActivityAffiliated(activityAffiliated);
        }
        return vActivityMessageListVo;
    }


}
