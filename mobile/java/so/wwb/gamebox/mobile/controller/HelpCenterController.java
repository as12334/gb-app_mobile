package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.Criterion;
import org.soul.commons.query.enums.Operator;
import org.soul.model.msg.notice.po.NoticeContactWay;
import org.soul.model.msg.notice.vo.NoticeContactWayListVo;
import org.soul.model.msg.notice.vo.NoticeContactWayVo;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.form.BindMobileForm;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.common.notice.enums.ContactWayType;
import so.wwb.gamebox.model.company.help.po.HelpDocumentI18n;
import so.wwb.gamebox.model.company.help.po.VHelpTypeAndDocument;
import so.wwb.gamebox.model.company.help.vo.VHelpTypeAndDocumentListVo;
import so.wwb.gamebox.model.master.enums.ContactWayStatusEnum;
import so.wwb.gamebox.model.master.enums.ContactWayTypeEnum;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 17-4-3.
 */
@Controller
@RequestMapping("/help")
public class HelpCenterController {

    private static final Log LOG = LogFactory.getLog(HelpCenterController.class);

    /**
     * 获取帮助文档父类型
     *
     * @param vHelpTypeAndDocumentListVo
     * @param model
     * @return
     */
    @RequestMapping("/firstType")
    @Upgrade(upgrade = true)
    public String getHelpParentType(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, Model model) {
        //先查出帮助中心父菜单
        Criteria criteria = Criteria.add(VHelpTypeAndDocument.PROP_PARENT_ID, Operator.IS_NULL, true);
        List<VHelpTypeAndDocument> helpTypeAndDocumentList;
        helpTypeAndDocumentList = CollectionQueryTool.query(Cache.getHelpTypeAndDocument().values(), criteria);

        model.addAttribute("command", getTypeI18n(helpTypeAndDocumentList));
        return "/help/Index";
    }

    /**
     * 获取帮助文档子类型
     *
     * @param vHelpTypeAndDocumentListVo
     * @param model
     * @return
     */
    @RequestMapping("/secondType")
    @Upgrade(upgrade = true)
    public String getHelpChildType(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, String name, Model model) {
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        if (searchId != null) {
            Criteria criteria = Criteria.add(VHelpTypeAndDocument.PROP_PARENT_ID, Operator.EQ, searchId);
            List<VHelpTypeAndDocument> typeList;
            typeList = CollectionQueryTool.query(Cache.getHelpTypeAndDocument().values(), criteria);

            model.addAttribute("command", getTypeI18n(typeList));
        }
        model.addAttribute("name", getTypeName(searchId));
        return "/help/SubList";
    }

    /**
     * 获取帮助文档详情国际化
     *
     * @param vHelpTypeAndDocumentListVo
     * @param name
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    @Upgrade(upgrade = true)
    public String getHelpDetail(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, String name, Model model) {
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        VHelpTypeAndDocument vHelpTypeAndDocument = Cache.getHelpTypeAndDocument().get(searchId.toString());
        List<Map<String, String>> list = JsonTool.fromJson(vHelpTypeAndDocument.getDocumentIdJson(), List.class);
        List<HelpDocumentI18n> documentI18nList = new ArrayList<>();
        if (list != null) {
            for (Map<String, String> map : list) {
                HelpDocumentI18n helpDocumentI18n = Cache.getHelpDocumentI18n().get(String.valueOf(map.get("id")));
                if (helpDocumentI18n != null) {
                    String content = helpDocumentI18n.getHelpContent().replaceAll("\\$\\{customerservice}", "在线客服");
                    helpDocumentI18n.setHelpContent(content);
                }
                documentI18nList.add(helpDocumentI18n);
            }
        }
        model.addAttribute("command", documentI18nList);
        model.addAttribute("name", getTypeName(searchId));
        return "/help/Detail";
    }

    /**
     * 在帮助类型和文档缓存中拿出 i18n信息
     *
     * @param helpTypeAndDocumentList
     * @return
     */
    public List<Map<String, String>> getTypeI18n(List<VHelpTypeAndDocument> helpTypeAndDocumentList) {
        List<Map<String, String>> typeList;
        List<Map<String, String>> typeI18nList = new ArrayList<>();
        String local = SessionManager.getLocale().toString();
        for (VHelpTypeAndDocument document : helpTypeAndDocumentList) {
            typeList = JsonTool.fromJson(document.getHelpTypeNameJson(), List.class);
            Map<String, String> documentMap = new HashMap<>();
            for (Map<String, String> map : typeList) {
                if (map.get("language").equals(local)) {
                    documentMap.put("id", document.getId().toString());
                    documentMap.put("name", map.get("name"));
                }
            }
            documentMap.put("ids", document.getDocumentIdJson());
            typeI18nList.add(documentMap);
        }
        return typeI18nList;
    }

    /**
     * 通过id获取类型当前国际化信息
     *
     * @param id
     * @return
     */
    public String getTypeName(Integer id) {
        String typeName = "";
        List<Map<String, String>> list = JsonTool.fromJson(Cache.getHelpTypeAndDocument().get(id.toString()).getHelpTypeNameJson(), List.class);
        String local = SessionManager.getLocale().toString();
        for (Map<String, String> map : list) {
            if (map.get("language").equals(local)) {
                typeName = map.get("name");
            }
        }
        return typeName;
    }

    @RequestMapping(value = "/forgetPassword")
    @Upgrade(upgrade = true)
    public String gotoFindPassword() {
        return "/help/forget/ForgetPassword";
    }

    /**
     * 当前用户是否绑定了手机
     *
     * @param model
     * @param contactVo
     * @return
     */
    @RequestMapping(value = "/bindMobile")
    @Upgrade(upgrade = true)
    public String bindMobile(Model model, NoticeContactWayVo contactVo) {
        NoticeContactWay contactWay = getUserPhoneNumber(contactVo);
        model.addAttribute("rule", JsRuleCreator.create(BindMobileForm.class, "result"));
        if (contactWay != null) {
            model.addAttribute("phone", StringTool.overlayTel(contactWay.getContactValue()));
            return "/help/bind/PhoneNumber";
//            return "/help/bind/BindMobile";
        } else {
            return "/help/bind/BindMobile";
        }

    }

    /**
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/updataMobile")
    @Upgrade(upgrade = true)
    public String UpdateMobile(Model model) {
        model.addAttribute("rule", JsRuleCreator.create(BindMobileForm.class, "result"));
        return "/help/bind/UpDataMobile";
    }

    /**
     * 手机绑定页面2
     *
     * @return
     */
    @RequestMapping(value = "/phoneNumber")
    @Upgrade(upgrade = true)
    public String PhoneNumber(Model model, NoticeContactWayVo contactVo) {
        NoticeContactWay contactWay = getUserPhoneNumber(contactVo);
        model.addAttribute("phone", StringTool.overlayTel(contactWay.getContactValue()));
        return "/help/bind/PhoneNumber";
    }


    /**
     * 手机绑定
     *
     * @param contactVo
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(value = "/savePhone")
    @ResponseBody
    @Upgrade(upgrade = true)
    public Boolean savePhone(NoticeContactWayVo contactVo, String oldPhone, @FormModel @Valid BindMobileForm form, BindingResult result) {
        NoticeContactWay contactWay = getUserPhoneNumber(contactVo);
        /*if (StringTool.isNotBlank(oldPhone)&&!oldPhone.equals(contactWay.getContactValue())){
            return false;
        }*/
        if(result.hasErrors()){
            return false;
        }
        if (contactWay != null) {//修改
            contactVo.getResult().setId(contactWay.getId());
            contactVo.setProperties(NoticeContactWay.PROP_CONTACT_VALUE);
            contactVo.getResult().setContactValue(contactVo.getSearch().getContactValue());
            contactVo = ServiceTool.noticeContactWayService().updateOnly(contactVo);
        } else {//新增
            contactVo.getResult().setUserId(SessionManager.getUserId());
            contactVo.getResult().setContactType(ContactWayTypeEnum.MOBILE.getCode());
            contactVo.getResult().setStatus(ContactWayStatusEnum.CONTENT_STATUS_USING.getCode());
            contactVo.getResult().setContactValue(contactVo.getSearch().getContactValue());
            contactVo = ServiceTool.noticeContactWayService().insert(contactVo);
        }
        if (!contactVo.isSuccess()) {//不成功
            return false;
        }
        return true;
    }

    /**
     * 获取用户手机号
     *
     * @param contactVo
     * @return
     */
    private NoticeContactWay getUserPhoneNumber(NoticeContactWayVo contactVo) {
        contactVo.setResult(new NoticeContactWay());
        contactVo.getQuery().setCriterions(
                new Criterion[]{
                        new Criterion(NoticeContactWay.PROP_USER_ID, Operator.EQ, SessionManager.getUser().getId())
                        , new Criterion(NoticeContactWay.PROP_CONTACT_TYPE, Operator.EQ, ContactWayTypeEnum.MOBILE.getCode())}
        );
        contactVo = ServiceTool.noticeContactWayService().search(contactVo);
        return contactVo.getResult();
    }

    /**
     * 远程验证手机号跟验证码
     * @param code
     * @param phone
     * @return
     */
    @RequestMapping("/checkPhoneCode")
    @ResponseBody
    public String checkPhoneCode(@RequestParam("phoneCode")String code , @RequestParam("search.contactValue")String phone){
        if(StringTool.isBlank(phone) || StringTool.isBlank(code)) {
            return "false";
        }

        Map<String,String > params = SessionManagerCommon.getCheckRegisterPhoneInfo();
        if(params == null){
            return "false";
        }

        if(StringTool.isBlank(params.get("phone")) || StringTool.isBlank(params.get("code"))) {
            return "false";
        }

        //验证码30分钟内有效
        if (DateTool.minutesBetween(SessionManagerCommon.getDate().getNow(), SessionManagerCommon.getSendRegisterPhone()) > 30) {
            return "false";
        }

        return ( phone.equals(params.get("phone"))&& params.get("code").equals(code) ) +"";
    }

    /**
     * 判断手机号码的唯一性
     *
     * @return
     */
    @RequestMapping(value = "/checkPhoneExist")
    @ResponseBody
    public String checkPhoneExist(@RequestParam("search.contactValue") String searchContactValue) {
        if (!ParamTool.isOnlyFiled(ContactWayType.CELLPHONE.getCode())) {
            return "true";
        }
        NoticeContactWayListVo listVo = new NoticeContactWayListVo();
        listVo.getSearch().setContactType(ContactWayType.CELLPHONE.getCode());
        listVo.getSearch().setContactValue(searchContactValue);
        return ServiceSiteTool.userAgentService().isExistContactWay(listVo);
    }
}
