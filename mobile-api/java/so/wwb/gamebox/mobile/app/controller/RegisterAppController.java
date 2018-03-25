package so.wwb.gamebox.mobile.app.controller;


import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.collections.SetTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.RandomStringTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.IpTool;
import org.soul.commons.net.ServletTool;
import org.soul.model.ip.IpBean;
import org.soul.model.msg.notice.vo.EmailMsgVo;
import org.soul.model.msg.notice.vo.NoticeContactWayListVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.model.sys.po.SysDict;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.validateForm.SignUpForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.common.RegisterConst;
import so.wwb.gamebox.model.common.notice.enums.ContactWayType;
import so.wwb.gamebox.model.company.site.po.SiteCurrency;
import so.wwb.gamebox.model.company.site.po.SiteLanguage;
import so.wwb.gamebox.model.company.sys.po.SysSite;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.enums.CreateChannelEnum;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserRegisterVo;
import so.wwb.gamebox.model.master.setting.enums.FieldSortEnum;
import so.wwb.gamebox.model.master.setting.enums.SiteCurrencyEnum;
import so.wwb.gamebox.model.master.setting.po.FieldSort;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.defense.biz.annotataion.Defense;
import so.wwb.gamebox.web.defense.biz.enums.DefenseAction;
import so.wwb.gamebox.web.defense.core.DefenseRs;
import so.wwb.gamebox.web.defense.core.IDefenseRs;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
     * 玩家注册
     *
     * @param userRegisterVo
     * @param form
     * @param result
     * @param request
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    @Defense(action = DefenseAction.PLAYER_REGISTER)
    public String saveUserInfo(UserRegisterVo userRegisterVo,
                               @FormModel @Valid SignUpForm form,
                               BindingResult result,
                               HttpServletRequest request) {
        //表单验证
        if (result.hasErrors()) {
            LOG.debug("站长站注册:表单验证未通过，error:{0}", result.getAllErrors());
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_FAIL.getCode(),
                    getErrorMessage(result.getAllErrors().get(0).getDefaultMessage()),
                    null,
                    APP_VERSION);
        }
        //验证码
        if (!checkedCaptcha(userRegisterVo.getCaptchaCode())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.VALIDATE_ERROR.getCode(),
                    AppErrorCodeEnum.VALIDATE_ERROR.getMsg(),
                    null,
                    APP_VERSION);
        }
        //用户名是否存在
        if (checkUserNameExist(userRegisterVo.getSysUser().getUsername())) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_USER_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_USER_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //判断真实姓名的唯一性
        if (StringTool.equals(checkRealNameExist(userRegisterVo.getSysUser().getRealName()), "false")) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_REAL_NAME_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_REAL_NAME_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //QQ
        if (StringTool.equals(checkQqExist(userRegisterVo.getQq().getContactValue()), "false")) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_QQ_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_QQ_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //手机号码
        if (StringTool.equals(checkPhoneExist(userRegisterVo.getPhone().getContactValue()), "false")) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_PHONE_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_PHONE_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //邮箱唯一性
        if (StringTool.equals(checkMailExist(userRegisterVo.getEmail().getContactValue()), "false")) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_EMAIL_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_EMAIL_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //微信
        if (StringTool.equals(checkWeixinExist(userRegisterVo.getWeixin().getContactValue()), "false")) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_WEIXIN_EXIST.getCode(),
                    AppErrorCodeEnum.REGISTER_WEIXIN_EXIST.getMsg(),
                    null,
                    APP_VERSION);
        }
        //站长中心是否允许注册
        if (!isAllowRegister()) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.NOT_ALLOW_REGISTER.getCode(),
                    AppErrorCodeEnum.NOT_ALLOW_REGISTER.getMsg(),
                    null,
                    APP_VERSION);
        }
        //注册防御
        Map<String, Object> resultMap = isAllowDefense(request);
        if (MapTool.isNotEmpty(resultMap) && !MapTool.getBoolean(resultMap, "state")) {
            MapTool.getString(resultMap, "msg");
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_FAIL.getCode(),
                    MapTool.getString(resultMap, "msg"),
                    null,
                    APP_VERSION);
        }
        userRegisterVo = doRegister(userRegisterVo, request);
         /*设置注册防御结果*/
        request.setAttribute(IDefenseRs.R_ACTION_RS, true);
        if (!userRegisterVo.isSuccess()) {
            return AppModelVo.getAppModeVoJson(false,
                    AppErrorCodeEnum.REGISTER_FAIL.getCode(),
                    AppErrorCodeEnum.REGISTER_FAIL.getMsg(),
                    null,
                    APP_VERSION);
        }
        return AppModelVo.getAppModeVoJson(true,
                AppErrorCodeEnum.REGISTER_SUCCESS.getCode(),
                AppErrorCodeEnum.REGISTER_SUCCESS.getMsg(),
                null,
                APP_VERSION);
    }

    //验证码
    public boolean checkedCaptcha(@RequestParam("captchaCode") String captchaCode) {
        if (StringTool.isEmpty(captchaCode)) {
            return false;
        }
        return captchaCode.equalsIgnoreCase(SessionManager.getCaptcha(SessionKey.S_CAPTCHA_PREFIX +
                CaptchaUrlEnum.CODE_PLAYER_REGISTER_MOBILE.getSuffix()));
    }

    /***
     * 用户名是否已存在
     */
    public boolean checkUserNameExist(@RequestParam("sysUser.username") String userName) {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setSubsysCode(SubSysCodeEnum.PCENTER.getCode());
        sysUserVo.getSearch().setUsername(userName);
        sysUserVo.getSearch().setSiteId(SessionManager.getSiteId());
        return ServiceTool.sysUserService().isExists(sysUserVo);
    }

    //email验证码
    public String checkEmailCode(@RequestParam("emailCode") String code, @RequestParam("email.contactValue") String email) {
        if (StringTool.isBlank(email) || StringTool.isBlank(code))
            return "false";
        Map<String, String> params = SessionManager.getCheckRegisterEmailInfo();
        if (params == null) {
            return "false";
        }
        if (StringTool.isBlank(params.get("email")) || StringTool.isBlank(params.get("code")))
            return "false";
        return (email.equals(params.get("email")) && params.get("code").equals(code)) + "";
    }

    @RequestMapping(value = "/checkEmail", method = RequestMethod.POST)
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email, @RequestParam("locale") String locale) {
        if (SessionManager.canSendRegisterEmail()) {
            String verificationCode = RandomStringTool.randomNumeric(6);
            SessionManager.setRegisterCheckEmailTime(new Date());
            try {
                String tmplContent = LocaleTool.tranMessage("notice", "tmpl.content.BIND_EMAIL_VERIFICATION_CODE");
                String tmplTitle = LocaleTool.tranMessage("notice", "tmpl.title.BIND_EMAIL_VERIFICATION_CODE");
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());

                int day = cal.get(Calendar.DAY_OF_MONTH);
                int mon = cal.get(Calendar.MONTH) + 1;//Calendar里取出来的month比实际的月份少1，所以要加上
                int year = cal.get(Calendar.YEAR);
                tmplContent = StringTool.fillTemplate(tmplContent, MapTool.newHashMap(
                        new Pair<>("verificationCode", verificationCode),
                        new Pair<>("sitename", Cache.getSiteI18n(SiteI18nEnum.SETTING_SITE_NAME).get(SessionManager.getLocale().toString()).getValue()),
                        new Pair<>("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl()),
                        new Pair<>("year", Integer.toString(year)),
                        new Pair<>("month", Integer.toString(mon)),
                        new Pair<>("day", Integer.toString(day)))
                );
                tmplTitle = StringTool.fillTemplate(tmplTitle, MapTool.newHashMap(new Pair<>("sitename", Cache.getSiteI18n(SiteI18nEnum.SETTING_SITE_NAME).get(SessionManager.getLocale().toString()).getValue())));

                EmailMsgVo emailMsgVo = new EmailMsgVo();
                emailMsgVo.setContent(tmplContent);
                emailMsgVo.setTitle(tmplTitle);
                emailMsgVo.setToAddressSet(SetTool.newHashSet(email));
                ServiceTool.messageService().sendEmail(emailMsgVo);
                Map<String, String> param = MapTool.newHashMap(
                        new Pair<>("code", verificationCode),
                        new Pair<>("email", email)
                );
                SessionManager.setCheckRegisterEmailInfo(param);
            } catch (Exception e) {
                LOG.debug("站长站：注册发送邮件 报错,本次验证码无效:{0}", e);
            } finally {
                LOG.debug("站长站：注册发送邮件验证码{0}，邮箱{1}", verificationCode, email);
            }
            return true;
        }
        return true;
    }

    /**
     * 判断真实姓名的唯一性
     *
     * @return
     */
    public String checkRealNameExist(@RequestParam("sysUser.realName") String realName) {
        if (!ParamTool.isOnlyFiled("realName")) {
            return "true";
        }
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setRealName(realName);
        sysUserVo.getSearch().setSiteId(SessionManager.getSiteId());
        sysUserVo.getSearch().setSubsysCode(SubSysCodeEnum.PCENTER.getCode());
        return ServiceSiteTool.userAgentService().isExistRealName(sysUserVo);
    }

    /**
     * 验证QQ唯一性
     *
     * @param qqContactValue
     * @return
     */
    public String checkQqExist(@RequestParam("qq.contactValue") String qqContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.QQ.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.QQ.getCode());
        listVo.getSearch().setContactValue(qqContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }

    /**
     * 判断手机号码的唯一性
     *
     * @return
     */
    public String checkPhoneExist(@RequestParam("phone.contactValue") String phoneContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.CELLPHONE.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.CELLPHONE.getCode());
        listVo.getSearch().setContactValue(phoneContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }

    /**
     * 判断邮箱的唯一性
     *
     * @return
     */
    public String checkMailExist(@RequestParam("email.contactValue") String mailContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.EMAIL.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.EMAIL.getCode());
        listVo.getSearch().setContactValue(mailContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }

    /**
     * 确认微信唯一性
     *
     * @return
     */
    public String checkWeixinExist(@RequestParam("weixin.contactValue") String weixinContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.WEIXIN.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.WEIXIN.getCode());
        listVo.getSearch().setContactValue(weixinContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }

    //注册
    private UserRegisterVo doRegister(UserRegisterVo userRegisterVo, HttpServletRequest request) {
        checkRegisterFormAgentDomain(userRegisterVo, request);
        userRegisterVo.getSysUser().setRegisterSite(request.getServerName());
        userRegisterVo.getSysUser().setRegisterIpDictCode(SessionManager.getIpDictCode());
        userRegisterVo.getSysUser().setRegisterIp(IpTool.ipv4StringToLong(ServletTool.getIpAddr(request)));

        String registerCode = SessionManager.getRecommendUserCode();
        if (StringTool.isNotBlank(registerCode)) {
            userRegisterVo.setRecommendRegisterCode(registerCode);
        }

        if (StringTool.isBlank(userRegisterVo.getSysUser().getDefaultCurrency())) {
            userRegisterVo.getSysUser().setDefaultCurrency(getSysSite().getMainCurrency());
        }
        UserPlayer userPlayer = new UserPlayer();
        userPlayer.setCreateChannel(CreateChannelEnum.MOBILE.getCode());
        userRegisterVo.setUserPlayer(userPlayer);
        return ServiceSiteTool.userPlayerService().register(userRegisterVo);
    }

    private SysSite getSysSite() {
        return Cache.getSysSite().get(SessionManager.getSiteId().toString());
    }

    /**
     * 是否通过代理独立域名推广注册
     *
     * @param userRegisterVo
     * @param request
     */
    private void checkRegisterFormAgentDomain(UserRegisterVo userRegisterVo, HttpServletRequest request) {
        String domain = SessionManager.getDomain(request);
        VSysSiteDomain sysDomain = Cache.getSiteDomain(domain);
        if (sysDomain != null && sysDomain.getAgentId() != null) {
            userRegisterVo.getSysUser().setOwnerId(sysDomain.getAgentId());
            LOG.debug("【玩家注册】通过代理独立域名{0}－代理id{1}", domain, sysDomain.getAgentId());
        }
        userRegisterVo.getSysUser().setRegisterSite(domain);
    }

    /**
     * 是否通过注册防御
     *
     * @param request
     * @return
     */
    private Map<String, Object> isAllowDefense(HttpServletRequest request) {
         /*注册防御*/
        IDefenseRs defense = (IDefenseRs) request.getAttribute(IDefenseRs.R_DEFENSE_RS);
        if (defense != null && !defense.isAvalable()) {
            Map<String, Object> resultMap = new HashMap<>(2, 1f);
            DefenseRs defenseRs = defense.getDefenseRs();
            resultMap.put("state", false);
            resultMap.put("msg", defenseRs.getMessage());
            return resultMap;
        }
        return null;
    }

    /**
     * 是否允许注册
     *
     * @return
     */
    private boolean isAllowRegister() {
        /*禁止注册*/
        Boolean register = Boolean.valueOf(ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_PLAYER).getParamValue());
        return register;
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
     * 获取失败提示信息
     *
     * @param messageCode
     * @return
     */
    protected String getErrorMessage(String messageCode) {
        return LocaleTool.tranMessage(Module.REGISTER.getCode(), messageCode);
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
