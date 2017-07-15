package so.wwb.gamebox.mobile.passport.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.ip.IpBean;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.sys.po.SysParam;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.passport.form.SignUpForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.RegisterConst;
import so.wwb.gamebox.model.company.sys.so.SysSiteSo;
import so.wwb.gamebox.model.company.sys.vo.SysSiteVo;
import so.wwb.gamebox.model.master.setting.enums.FieldSortEnum;
import so.wwb.gamebox.model.master.setting.po.FieldSort;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 通过代理推广链接访问
 *
 * Created by bruce on 16-9-5.
 */
@Controller
public class RegisterController {

    private static final Log LOG = LogFactory.getLog(RegisterController.class);

    private  static final String SIGN_UP_URL = "/passport/SignUp";

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

    @RequestMapping("/register")
    public String  toRegister(Model model, HttpServletRequest request) {
        String rule = JsRuleCreator.create(SignUpForm.class, "result");
        model.addAttribute("rule",rule);

        String registCode = request.getParameter("registeredCode");
        String c = request.getParameter("c");

        if (StringTool.isNotBlank(registCode)) {
            model.addAttribute("registCode",registCode);
        }

        if (StringTool.isNotBlank(c)) {
            model.addAttribute("registCode",c);
        }

        getRegisterData(model, request);

        return SIGN_UP_URL;
    }

    /**
     * 动态取注册需要的数据
     */
    private void getRegisterData(Model model, HttpServletRequest request) {
        Map<String, Object> params = new HashMap();
        String recommendRegisterCode = "";
        if (StringTool.equals(request.getParameter("utype"), "agent")) {
            recommendRegisterCode = SessionManager.getAgentRecommendUserCode();
            LOG.debug("代理注册-介绍人加密原码{0}-", recommendRegisterCode);
        } else {
            recommendRegisterCode = SessionManager.getRecommendUserCode();
            LOG.debug("玩家注册-介绍人加密原码{0}-", recommendRegisterCode);
        }

        /*当前ip的国家地区*/
        IpBean ipBean = SessionManager.getIpDb();
        Map<String, String> ipLocaleMap = MapTool.newHashMap(
                new Pair<String, String>(SysUser.PROP_COUNTRY, ipBean.getCountry()),
                new Pair<String, String>(SysUser.PROP_CITY, ipBean.getCity()),
                new Pair<String, String>(SysUser.PROP_REGION, ipBean.getStateprov())
        );
        params.put("ipLocale", ipLocaleMap);
        /*邀请人*/
        params.put("recommendCode", recommendRegisterCode);
        /*站点主货币*/
        SysSiteVo sysSiteVo = new SysSiteVo();
        sysSiteVo.setSearch(new SysSiteSo());
        sysSiteVo.getSearch().setId(SessionManager.getSiteId());
        sysSiteVo = ServiceTool.sysSiteService().get(sysSiteVo);
        params.put("currency", sysSiteVo.getResult().getMainCurrency());
        params.put("timezone", sysSiteVo.getResult().getTimezone());

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

    /**
     * 注册字段
     */
    private void getOptionData(List<FieldSort> fieldSorts, Model model) {
        if (CollectionTool.isNotEmpty(fieldSorts)) {
            List<String> requiredJson = new ArrayList<>();//注册必填字段
            /*介绍人要放在首位,是否是注册字段*/
            boolean registCodeField = true;
            Iterator<FieldSort> iter = fieldSorts.iterator();
            while (iter.hasNext()) {
                FieldSort fieldSort = iter.next();
                if (FieldSortEnum.NOT_REG_FIELD.getCode().equals(fieldSort.getIsRegField())) {
                    if (RegisterConst.REGCODE.equals(fieldSort.getName())) {
                        registCodeField = false;
                    }
                    iter.remove();
                    continue;
                }
                if (!FieldSortEnum.NOT_REQUIRED.getCode().equals(fieldSort.getIsRequired())) {
                    requiredJson.add(fieldSort.getName());
                }
            }
            model.addAttribute("field", fieldSorts);
            model.addAttribute("requiredJson", requiredJson);
            model.addAttribute("registCodeField",registCodeField);
        }
    }

    /**
     * 判断是否需要验证
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
