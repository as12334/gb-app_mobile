package so.wwb.gamebox.mobile.app.controller;


import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.ip.IpBean;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.sys.po.SysDict;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.RegisterConst;
import so.wwb.gamebox.model.company.site.po.SiteCurrency;
import so.wwb.gamebox.model.company.site.po.SiteLanguage;
import so.wwb.gamebox.model.company.sys.po.SysSite;
import so.wwb.gamebox.model.master.setting.enums.FieldSortEnum;
import so.wwb.gamebox.model.master.setting.enums.SiteCurrencyEnum;
import so.wwb.gamebox.model.master.setting.po.FieldSort;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

@Controller
@RequestMapping("/registerOrigin")
public class RegisterAppController {
    private static final Log LOG = LogFactory.getLog(RegisterAppController.class);

    /*注册设置和页面设定name映射*/
    private static final Map<String, String> SIGN_UP_DATA_MAP = new HashMap<String, String>() {
        {
            put(RegisterConst.DEFAULT_TIME_ZONE, "sysUser.defaultTimezone");
            put(RegisterConst.MAIN_CURRENCY, "sysUser.defaultCurrency");
            put(RegisterConst.DEFAULT_LOCALE, "sysUser.defaultLocale");
            put(RegisterConst.USER_NAME, "sysUser.username");
            put(RegisterConst.PASSWORD, "sysUser.password");
            put(RegisterConst.PAYMENT_PASSWORD, "sysUser.permissionPwd");
            put(RegisterConst.SEX, "sysUser.sex");
            put(RegisterConst.REAL_NAME, "sysUser.realName");
            put(RegisterConst.BIRTHDAY, "sysUser.birthday");
            put(RegisterConst.SECURITY_ISSUES, "sysUserProtection.question1");
            put(RegisterConst.QQ, "qq.contactValue");
            put(RegisterConst.EMAIL, "email.contactValue");
            put(RegisterConst.MOBILE, "phone.contactValue");
            put(RegisterConst.WECHAT, "weixin.contactValue");
        }
    };

    /**
     * 查询轮播图
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getRegisterInfo")
    @ResponseBody
    public String getRegisterInfo(HttpServletRequest request) {
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                getRegisterData(request),
                APP_VERSION);
    }

    /**
     * 动态取注册需要的数据
     */
    private Map getRegisterData(HttpServletRequest request) {
        Map map = new HashMap();
        Map<String, Object> params = new HashMap<>();
        String recommendRegisterCode = "";
        if (StringTool.equals(request.getParameter("utype"), "agent")) {
            recommendRegisterCode = SessionManager.getAgentRecommendUserCode();
            LOG.debug("代理注册-介绍人加密原码{0}-", recommendRegisterCode);
        } else {
            recommendRegisterCode = SessionManager.getRecommendUserCode();
            LOG.debug("玩家注册-介绍人加密原码{0}-", recommendRegisterCode);
        }

        /*邀请人*/
        params.put("registCode", recommendRegisterCode);

        /*当前ip的国家地区*/
        IpBean ipBean = SessionManager.getIpDb();
        Map<String, String> ipLocaleMap = MapTool.newHashMap(
                new Pair<String, String>(SysUser.PROP_COUNTRY, ipBean.getCountry()),
                new Pair<String, String>(SysUser.PROP_CITY, ipBean.getCity()),
                new Pair<String, String>(SysUser.PROP_REGION, ipBean.getStateprov())
        );
        params.put("ipLocale", ipLocaleMap);

        /*站点主货币*/
        SysSite sysSite = Cache.getSysSite().get(SessionManager.getSiteIdString());

        params.put("currency", sysSite.getMainCurrency());
        params.put("timezone", sysSite.getTimezone());

        //生日选择最大为18年前，最小100年前（满18周岁才可）
        Date today = SessionManager.getDate().getToday();
        params.put("minDate", DateTool.addYears(today, -100));
        params.put("maxDate", DateTool.addYears(today, -18));
        map.put("params", params);

        //注册字段
        SysParam playerRegisterParam = ParamTool.getSysParam(SiteParamEnum.SETTING_REG_SETTING_FIELD_SETTING);//注册字段设置
        List<FieldSort> playerFieldSorts = (List<FieldSort>) JsonTool.fromJson(playerRegisterParam == null ? null : playerRegisterParam.getParamValue(), JsonTool.createCollectionType(ArrayList.class, FieldSort.class));
        getOptionData(playerFieldSorts, map);
        map.put("signUpDataMap", SIGN_UP_DATA_MAP);
        //邮箱验证
        map.put("isEmail", isValid(SiteParamEnum.SETTING_REG_SETTING_MAIL_VERIFCATION));
        //手机验证
        map.put("isPhone", isValid(SiteParamEnum.SETTING_REG_SETTING_PHONE_VERIFCATION));
        map.put("selectOption", optionText(request));
        return map;
    }

    /**
     * 下拉框选项内容
     *
     * @param request
     * @return
     */
    public Map optionText(HttpServletRequest request) {
        Map map = new HashMap();
        DictEnum dictEnum = null;
        //主货币
        map.put("mainCurrency", currencyOption());
        //性别
        dictEnum = DictEnum.COMMON_SEX;
        map.put("sex", optionText(dictEnum));
        //语言
        map.put("defaultLocale", getAvailableLanguage());
        //密保
        dictEnum = DictEnum.SETTING_MASTER_QUESTIONS;
        map.put("securityIssues", optionText(dictEnum));
        return map;
    }

    private List<Map<String, String>> getAvailableLanguage() {
        Map<String, SiteLanguage> languageMap = Cache.getAvailableSiteLanguage();
        List<SiteLanguage> languageList = new ArrayList<>(languageMap.values());
        List<Map<String, String>> mapList = new ArrayList<>(languageList.size());
            /*取字典国际化*/
        Map i18nMap = I18nTool.getDictsMap(SessionManagerCommon.getLocale().toString())
                .get(DictEnum.COMMON_LANGUAGE.getModule().getCode())
                .get(DictEnum.COMMON_LANGUAGE.getType());

        if (!languageList.isEmpty()) {
            for (SiteLanguage siteLanguage : languageList) {
                String lang = StringTool.substringBefore(i18nMap.get(siteLanguage.getLanguage()).toString(), "#");
                Map<String, String> map = new HashMap<>(2, 1f);
                map.put("text", lang);
                map.put("value", siteLanguage.getLanguage());
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 获取下拉选项
     *
     * @param dictEnum
     * @return
     */
    private List<Map<String, String>> optionText(DictEnum dictEnum) {
        if (dictEnum != null) {
            Map<String, SysDict> dicts = DictTool.get(dictEnum);
            List<Map<String, String>> mapList = new ArrayList<>(dicts.size());
            Map<String, String> i18nMap = I18nTool.getDictsMap(SessionManagerBase.getLocale().toString()).get(dictEnum.getModule().getCode()).get(dictEnum.getType());
            if (i18nMap != null) {
                for (SysDict sysDict : dicts.values()) {
                    Map<String, String> map = new HashMap<>(2, 1f);
                    map.put("text", i18nMap.get(sysDict.getDictCode()));
                    map.put("value", sysDict.getDictCode());
                    mapList.add(map);
                }
            }
            return mapList;
        } else {
            return null;
        }
    }

    /**
     * 获取货币下拉框
     *
     * @return
     */
    private List<Map<String, String>> currencyOption() {
        List<SiteCurrency> siteCurrencies = new ArrayList<>(Cache.getSiteCurrency().values());
        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map;
        Map<String, String> i18nMap = I18nTool.getDictsMap(SessionManagerBase.getLocale().toString()).get(Module.COMMON.getCode()).get(DictEnum.COMMON_CURRENCY.getType());
        for (SiteCurrency siteCurrency : siteCurrencies) {
            if (SiteCurrencyEnum.NORMAL.getCode().equals(siteCurrency.getStatus())) {
                map = new HashMap<>(2, 1f);
                map.put("text", i18nMap.get(siteCurrency.getCode()));
                map.put("value", siteCurrency.getCode());
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 注册字段
     *
     * @param fieldSorts
     * @param
     */
    private void getOptionData(List<FieldSort> fieldSorts, Map map) {
        if (CollectionTool.isNotEmpty(fieldSorts)) {
            List<String> requiredJson = new ArrayList<>();//注册必填字段
            /*介绍人要放在首位,是否是注册字段*/
            boolean registCodeField = true;
            //介绍人是否必填
            boolean isRequiredForRegisterCode = false;
            Iterator<FieldSort> iter = fieldSorts.iterator();
            while (iter.hasNext()) {
                FieldSort fieldSort = iter.next();
                if (FieldSortEnum.NOT_REG_FIELD.getCode().equals(fieldSort.getIsRegField())) {
                    if (RegisterConst.REGCODE.equals(fieldSort.getName())) {
                        registCodeField = false;
                        isRequiredForRegisterCode = !FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired());
                        map.put("isRequiredForRegisterCode", isRequiredForRegisterCode);
                    }
                    iter.remove();
                    continue;
                } else {
                    if (RegisterConst.REGCODE.equals(fieldSort.getName())) {
                        isRequiredForRegisterCode = !FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired());
                        map.put("isRequiredForRegisterCode", isRequiredForRegisterCode);
                    }
                }
                if (!FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired())) {
                    requiredJson.add(fieldSort.getName());
                }
            }
            map.put("field", fieldSorts);
            map.put("requiredJson", requiredJson);
            map.put("registCodeField", registCodeField);
        }
    }

    /**
     * 判断是否需要验证
     *
     * @param paramEnum
     * @return
     */
    private boolean isValid(SiteParamEnum paramEnum) {
        boolean isValid = false;
        SysParam param = ParamTool.getSysParam(paramEnum);
        if (param != null) {
            isValid = "before".equals(param.getParamValue()) && param.getActive();
        }
        return isValid;
    }

}
