package so.wwb.gamebox.mobile.passport.controller;

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
import org.soul.model.common.BaseObjectVo;
import org.soul.model.ip.IpBean;
import org.soul.model.msg.notice.enums.NoticePublishMethod;
import org.soul.model.msg.notice.po.NoticeContactWay;
import org.soul.model.msg.notice.po.NoticeTmpl;
import org.soul.model.msg.notice.vo.EmailMsgVo;
import org.soul.model.msg.notice.vo.NoticeContactWayListVo;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.model.sys.po.SysDict;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.passport.form.SignUpForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.RegisterConst;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.common.notice.enums.ContactWayType;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.company.site.po.SiteCurrency;
import so.wwb.gamebox.model.company.site.po.SiteLanguage;
import so.wwb.gamebox.model.company.sys.po.SysSite;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.master.enums.CreateChannelEnum;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserAgentVo;
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
import so.wwb.gamebox.web.lottery.controller.BaseDemoController;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;


/**
 * Created by jessie on 16-7-19.
 */

@Controller
@RequestMapping("/signUp")
public class SignUpController extends BaseDemoController {

    private static final Log LOG = LogFactory.getLog(SignUpController.class);

    private static final String SIGN_UP_URL = "/passport/SignUp";

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
     * 跳转到注册页面
     *
     * @param model
     * @return
     */
    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String signUp(Model model, HttpServletRequest request) {
        getRegisterData(model, request);
        model.addAttribute("rule", JsRuleCreator.create(SignUpForm.class, "result"));
        return SIGN_UP_URL;
    }

    /**
     * 动态取注册需要的数据
     */
    private void getRegisterData(Model model, HttpServletRequest request) {
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
        model.addAttribute("params", params);

        //注册字段
        SysParam playerRegisterParam = ParamTool.getSysParam(SiteParamEnum.SETTING_REG_SETTING_FIELD_SETTING);//注册字段设置
        List<FieldSort> playerFieldSorts = (List<FieldSort>) JsonTool.fromJson(playerRegisterParam == null ? null : playerRegisterParam.getParamValue(), JsonTool.createCollectionType(ArrayList.class, FieldSort.class));
        getOptionData(playerFieldSorts, model);
        model.addAttribute("signUpDataMap", SIGN_UP_DATA_MAP);
        //邮箱验证
        model.addAttribute("isEmail", isValid(SiteParamEnum.SETTING_REG_SETTING_MAIL_VERIFCATION));
        //手机验证
        model.addAttribute("isPhone", isValid(SiteParamEnum.SETTING_REG_SETTING_PHONE_VERIFCATION));

    }

    private SysSite getSysSite() {
        return Cache.getSysSite().get(SessionManager.getSiteId().toString());
    }

    /**
     * 注册字段
     *
     * @param fieldSorts
     * @param model
     */
    private void getOptionData(List<FieldSort> fieldSorts, Model model) {
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
                        model.addAttribute("isRequiredForRegisterCode", isRequiredForRegisterCode);
                    }
                    iter.remove();
                    continue;
                } else {
                    if (RegisterConst.REGCODE.equals(fieldSort.getName())) {
                        isRequiredForRegisterCode = !FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired());
                        model.addAttribute("isRequiredForRegisterCode", isRequiredForRegisterCode);
                    }
                }
                if (!FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired())) {
                    requiredJson.add(fieldSort.getName());
                }
            }
            model.addAttribute("field", fieldSorts);
            model.addAttribute("requiredJson", requiredJson);
            model.addAttribute("registCodeField", registCodeField);
        }
    }

    /**
     * 下拉框选项内容
     *
     * @param request
     * @return
     */
    @RequestMapping("/optionText")
    @ResponseBody
    public String optionText(HttpServletRequest request) {
        DictEnum dictEnum = null;
        String option = request.getParameter("option");
        if (RegisterConst.MAIN_CURRENCY.equals(option)) {
            return currencyOption();
        } else if (RegisterConst.SEX.equals(option)) {//性别
            dictEnum = DictEnum.COMMON_SEX;
        } else if (RegisterConst.DEFAULT_LOCALE.equals(option)) {//语言
            return getAvailableLanguage();
        } else if (RegisterConst.SECURITY_ISSUES.equals(option)) {//安全问题
            dictEnum = DictEnum.SETTING_MASTER_QUESTIONS;
        }
        return optionText(dictEnum);
    }

    private String getAvailableLanguage() {
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
        return JsonTool.toJson(mapList);
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
            isValid = param.getActive();
        }


        return isValid;
    }

    /**
     * 获取货币下拉框
     *
     * @return
     */
    private String currencyOption() {
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
        return JsonTool.toJson(mapList);
    }

    /**
     * 获取下拉选项
     *
     * @param dictEnum
     * @return
     */
    private String optionText(DictEnum dictEnum) {
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
            return JsonTool.toJson(mapList);
        } else {
            return null;
        }
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
    public Map saveUserInfo(UserRegisterVo userRegisterVo, @FormModel @Valid SignUpForm form, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            LOG.debug("站长站注册:表单验证未通过，error:{0}", result.getAllErrors());
            return getMessage(false, MessageI18nConst.REGISTER_FAIL);
        }
        //站长中心是否允许注册
        if (!isAllowRegister()) {
            return getMessage(false, MessageI18nConst.NOT_ALLOW_REGISTER);
        }
        //注册防御
        Map<String, Object> resultMap = isAllowDefense(request);
        if (MapTool.isNotEmpty(resultMap) && !MapTool.getBoolean(resultMap, "state")) {
            return resultMap;
        }
        userRegisterVo = doRegister(userRegisterVo, request);
        sendRegSuccessMsg(request, userRegisterVo);
         /*设置注册防御结果*/
        request.setAttribute(IDefenseRs.R_ACTION_RS, true);
        String messageCode = userRegisterVo.isSuccess() ? MessageI18nConst.REGISTER_SUCCESS : MessageI18nConst.REGISTER_FAIL;
        return getMessage(userRegisterVo.isSuccess(), messageCode);
    }

    private Map<String, Object> getMessage(boolean isSuccess, String messageCode) {
        Map<String, Object> resultMap = new HashMap<>(2, 1f);
        resultMap.put("state", isSuccess);
        resultMap.put("msg", LocaleTool.tranMessage(Module.REGISTER.getCode(), messageCode));
        return resultMap;
    }

    /**
     * 注册成功后发送站内消息
     *
     * @param request
     * @param userRegisterVo
     */
    private void sendRegSuccessMsg(HttpServletRequest request, UserRegisterVo userRegisterVo) {
        try {
            NoticeContactWay email = userRegisterVo.getEmail();
            //发送 注册成功 站内信
            LOG.info("站长站：玩家注册成功发送站内信");
            NoticeVo noticeVo = NoticeVo.autoNotify(AutoNoticeEvent.PLAYER_REGISTER_SUCCESS, userRegisterVo.getSysUser().getId());
            noticeVo.setSubscribeType(CometSubscribeType.READ_COUNT);
            noticeVo.setSendUserId(NoticeVo.SEND_USER_ID);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SessionManager.getDate().getNow());
            noticeVo.addParams(
                    new Pair<String, String>(NoticeParamEnum.WEB_SITE.getCode(), request.getServerName()),
                    new Pair<String, String>(NoticeParamEnum.CUSTOMER.getCode(), NoticeParamEnum.CUSTOMER.getDesc()),
                    new Pair<String, String>(NoticeParamEnum.YEAR.getCode(), String.valueOf(calendar.get(Calendar.YEAR))),
                    new Pair<String, String>(NoticeParamEnum.MONTH.getCode(), String.valueOf(calendar.get(Calendar.MONTH) + 1)),
                    new Pair<String, String>(NoticeParamEnum.DAY.getCode(), String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)))
            );
            try {
                ServiceTool.noticeService().publish(noticeVo);
            } catch (Exception ex) {
                LogFactory.getLog(this.getClass()).error(ex, "发布消息不成功");
            }

            if (userRegisterVo.isSuccess() && email != null && StringTool.isNotBlank(email.getContactValue())) {
                //发送 注册成功 邮件
                NoticeTmpl noticeEmailTmpl = getNoticeTmplWherePublishMethodIsEmail(userRegisterVo);

                if (noticeEmailTmpl != null) {
                    LOG.info("站长站：玩家注册成功发送邮件 地址：{0}", email.getContactValue());
                    EmailMsgVo emailMsgVo = replaceVarTagInEmailMsg(request, userRegisterVo, noticeEmailTmpl);
                    emailMsgVo.setToAddressSet(SetTool.newHashSet(email.getContactValue()));
                    ServiceTool.messageService().sendEmail(emailMsgVo);
                }
            }
        } catch (Exception e) {
            LOG.error(e, "[玩家注册成功]消息发送失败!");
        }
    }

    /**
     * 根据发送类型 获取注册成功的通知模版
     *
     * @param baseObjectVo
     * @return
     */
    private NoticeTmpl getNoticeTmplWherePublishMethodIsEmail(BaseObjectVo baseObjectVo) {
        NoticeVo noticeVo = new NoticeVo();
        noticeVo.setSubscribeType(CometSubscribeType.READ_COUNT);
        noticeVo.setEventType(AutoNoticeEvent.PLAYER_REGISTER_SUCCESS);
        noticeVo.setPublishMethod(NoticePublishMethod.EMAIL);
        Map<NoticePublishMethod, Set<NoticeTmpl>> s = ServiceTool.noticeService().fetchTmpls(noticeVo);
        Map<String, NoticeTmpl> noticeTmplHashMap = CollectionTool.toEntityMap(s.get(NoticePublishMethod.EMAIL), NoticeTmpl.PROP_LOCALE, String.class);
        String locale = "";
        if (baseObjectVo instanceof UserRegisterVo) {
            locale = StringTool.isBlank(((UserRegisterVo) baseObjectVo).getSysUser().getDefaultLocale()) ? SessionManager.getLocale().toString() : ((UserRegisterVo) baseObjectVo).getSysUser().getDefaultLocale();
        } else if (baseObjectVo instanceof UserAgentVo) {
            locale = StringTool.isBlank(((UserAgentVo) baseObjectVo).getSysUser().getDefaultLocale()) ? SessionManager.getLocale().toString() : ((UserAgentVo) baseObjectVo).getSysUser().getDefaultLocale();
        }
        return noticeTmplHashMap.get(locale);
    }

    private EmailMsgVo replaceVarTagInEmailMsg(HttpServletRequest request, UserRegisterVo userRegisterVo, NoticeTmpl noticeEmailTmpl) {
        String tmplContent = noticeEmailTmpl.getContent();
        String tmplTitle = noticeEmailTmpl.getTitle();
        EmailMsgVo emailMsgVo = new EmailMsgVo();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int mon = cal.get(Calendar.MONTH) + 1;//Calendar里取出来的month比实际的月份少1，所以要加上
        int year = cal.get(Calendar.YEAR);
        tmplTitle = StringTool.fillTemplate(tmplTitle, MapTool.newHashMap(
                new Pair<String, String>("sitename", SessionManager.getSiteName(request))
        ));
        tmplContent = StringTool.fillTemplate(tmplContent, MapTool.newHashMap(
                new Pair<String, String>("user", userRegisterVo.getSysUser().getUseLine()),
                new Pair<String, String>("sitename", Cache.getSiteI18n(SiteI18nEnum.SETTING_SITE_NAME).get(noticeEmailTmpl.getLocale()).getValue()),
                new Pair<String, String>("year", Integer.toString(year)),
                new Pair<String, String>("month", Integer.toString(mon)),
                new Pair<String, String>("day", Integer.toString(day)))
        );

        emailMsgVo.setContent(tmplContent);
        emailMsgVo.setTitle(tmplTitle);
        return emailMsgVo;
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

    @RequestMapping("/checkedCaptcha")
    @ResponseBody
    public boolean checkedCaptcha(@RequestParam("captchaCode") String captchaCode, HttpServletRequest request) {
        if (StringTool.isEmpty(captchaCode)) {
            return false;
        }
        return captchaCode.equalsIgnoreCase(SessionManager.getCaptcha(SessionKey.S_CAPTCHA_PREFIX +
                CaptchaUrlEnum.CODE_PLAYER_REGISTER_MOBILE.getSuffix()));
    }

    /***
     * 用户名是否已存在
     */
    @RequestMapping(value = "/checkUserNameExist")
    @ResponseBody
    public String checkUserNameExist(@RequestParam("sysUser.username") String userName) {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setSubsysCode(SubSysCodeEnum.PCENTER.getCode());
        sysUserVo.getSearch().setUsername(userName);
        sysUserVo.getSearch().setSiteId(SessionManager.getSiteId());
        /*boolean exists = ServiceTool.sysUserService().isExists(sysUserVo);
        return (!exists) + "";*/
        return ServiceSiteTool.userAgentService().isExistAgent(sysUserVo);
    }

    @RequestMapping("/checkEmailCode")
    @ResponseBody
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
     * 是否通过代理独立域名推广注册
     *
     * @param userRegisterVo
     * @param request
     */
    private void checkRegisterFormAgentDomain(UserRegisterVo userRegisterVo, HttpServletRequest request) {
        VSysSiteDomain sysDomain = SessionManager.getSiteDomain(request);
        if (sysDomain != null && sysDomain.getAgentId() != null) {
            userRegisterVo.getSysUser().setOwnerId(sysDomain.getAgentId());
            LOG.debug("【玩家注册】通过代理独立域名{0}－代理id{1}", sysDomain.getDomain(), sysDomain.getAgentId());
        }
        userRegisterVo.getSysUser().setRegisterSite(request.getServerName());
    }

    /**
     * 判断真实姓名的唯一性
     *
     * @return
     */
    @RequestMapping(value = "/checkRealNameExist")
    @ResponseBody
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
    @RequestMapping(value = "/checkQqExist")
    @ResponseBody
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
    @RequestMapping(value = "/checkPhoneExist")
    @ResponseBody
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
    @RequestMapping(value = "/checkMailExist")
    @ResponseBody
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
    @RequestMapping(value = "/checkWeixinExist")
    @ResponseBody
    public String checkWeixinExist(@RequestParam("weixin.contactValue") String weixinContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.WEIXIN.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.WEIXIN.getCode());
        listVo.getSearch().setContactValue(weixinContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }

    @Override
    protected String getDemoIndex() {
        return "/";
    }
}
