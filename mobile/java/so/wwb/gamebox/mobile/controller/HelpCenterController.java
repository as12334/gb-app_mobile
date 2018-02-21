package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.help.po.HelpDocumentI18n;
import so.wwb.gamebox.model.company.help.po.VHelpTypeAndDocument;
import so.wwb.gamebox.model.company.help.vo.VHelpTypeAndDocumentListVo;
import so.wwb.gamebox.web.cache.Cache;

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
    /**
     * 获取帮助文档父类型
     * @param vHelpTypeAndDocumentListVo
     * @param model
     * @return
     */
    @RequestMapping("/firstType")
    @Upgrade(upgrade = true)
    public String getHelpParentType(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, Model model) {
        //先查出帮助中心父菜单
        Criteria criteria = Criteria.add(VHelpTypeAndDocument.PROP_PARENT_ID, Operator.IS_NULL, true);
        List<VHelpTypeAndDocument> helpTypeAndDocumentList ;
        helpTypeAndDocumentList = CollectionQueryTool.query(Cache.getHelpTypeAndDocument().values(), criteria);

        model.addAttribute("command", getTypeI18n(helpTypeAndDocumentList));
        return "/help/Index";
    }
    /**
     * 获取帮助文档子类型
     * @param vHelpTypeAndDocumentListVo
     * @param model
     * @return
     */
    @RequestMapping("/secondType")
    @Upgrade(upgrade = true)
    public String getHelpChildType(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, String name, Model model) {
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        if(searchId !=null) {
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
     * @param vHelpTypeAndDocumentListVo
     * @param name
     * @param model
     * @return
     */
    @RequestMapping("/detail")
    @Upgrade(upgrade = true)
    public String getHelpDetail(VHelpTypeAndDocumentListVo vHelpTypeAndDocumentListVo, String name,Model model){
        Integer searchId = vHelpTypeAndDocumentListVo.getSearch().getId();
        VHelpTypeAndDocument vHelpTypeAndDocument = Cache.getHelpTypeAndDocument().get(searchId.toString());
        List<Map<String, String>> list = JsonTool.fromJson(vHelpTypeAndDocument.getDocumentIdJson(), List.class);
        List<HelpDocumentI18n> documentI18nList = new ArrayList<>();
        if(list != null){
            for(Map<String, String> map : list){
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
     * @param helpTypeAndDocumentList
     * @return
     */
    public List<Map<String, String>> getTypeI18n(List<VHelpTypeAndDocument> helpTypeAndDocumentList){
        List<Map<String, String>> typeList ;
        List<Map<String, String>> typeI18nList = new ArrayList<>();
        String local = SessionManager.getLocale().toString();
        for(VHelpTypeAndDocument document : helpTypeAndDocumentList){
            typeList = JsonTool.fromJson(document.getHelpTypeNameJson(), List.class);
            Map<String, String> documentMap = new HashMap<>();
            for(Map<String, String> map : typeList){
                if(map.get("language").equals(local)){
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
     * @param id
     * @return
     */
    public String getTypeName(Integer id){
        String typeName = "";
        List<Map<String, String>> list = JsonTool.fromJson(Cache.getHelpTypeAndDocument().get(id.toString()).getHelpTypeNameJson(), List.class);
        String local = SessionManager.getLocale().toString();
        for(Map<String, String> map : list){
            if(map.get("language").equals(local)){
                typeName = map.get("name");
            }
        }
        return typeName;
    }
}
