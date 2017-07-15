package so.wwb.gamebox.mobile.controller;

import org.soul.web.controller.BaseIndexController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.po.SiteApiTypeRelation;
import so.wwb.gamebox.model.company.site.po.SiteApiTypeRelationI18n;
import so.wwb.gamebox.web.cache.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by LeTu on 2017/3/31.
 */
public class BaseApiController extends BaseIndexController {

    List<Map<String,Object>> getApiType() {
        List<SiteApiTypeRelationI18n> siteApiTypeRelationI18nList = new ArrayList<>();
        List<SiteApiTypeRelation> siteApiTypeRelationList = new GameController().getSiteApiTypeRelationList(null);
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,SiteApiTypeRelationI18n> siteApiTypeRelationI18nMap = Cache.getSiteApiTypeRelactionI18n(SessionManager.getSiteId());

        Map<Integer, String> map = setApiOrder();
        siteApiTypeRelationI18nList = orderByApi(siteApiTypeRelationI18nMap,map);

        //通过查出的手机端API,再找出API的I18N
        for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nList) {
            for(SiteApiTypeRelation apiTypeRelation:siteApiTypeRelationList){
                if(apiTypeRelation.getApiId().equals(siteApiTypeRelationI18n.getApiId())&&apiTypeRelation.getApiTypeId().equals(siteApiTypeRelationI18n.getApiTypeId())){
                    if(!(apiTypeRelation.getApiId().equals(10)&&!siteApiTypeRelationI18n.getApiTypeId().equals(1))) {
                        //BBIN只能显示一个
                        Map<String, Object> apiTypeRelationI18nMap = new HashMap<>();
                        apiTypeRelationI18nMap.put("apiTypeRelation", siteApiTypeRelationI18n);
                        list.add(apiTypeRelationI18nMap);
                    }
                }
            }
        }
        return list;
    }

    /** 首页显示api顺序 */
    private Map<Integer, String> setApiOrder() {
        Map<Integer ,String> map = new HashMap<>();
        map.put(1,"9,1");
        map.put(2,"3,2");
        map.put(3,"16,1");
        map.put(4,"10,1");
        map.put(5,"12,3");
        map.put(6,"7,1");
        map.put(7,"5,1");
        return map;
    }

    /**
     * 按指定顺序排序
     */
    private List<SiteApiTypeRelationI18n> orderByApi(Map<String, SiteApiTypeRelationI18n> siteApiTypeRelationI18nMap,Map<Integer,String> map){
        List<SiteApiTypeRelationI18n> list = new ArrayList<>();
        List<SiteApiTypeRelationI18n> removeList = new ArrayList<>();
        removeList.addAll(siteApiTypeRelationI18nMap.values());
        for(int i=1;i<map.size()+1;i++) {
            for (SiteApiTypeRelationI18n siteApiTypeRelationI18n : siteApiTypeRelationI18nMap.values()) {
                String[] apiString = map.get(i).split(",");
                if (apiString[0].equals(siteApiTypeRelationI18n.getApiId()+"") && apiString[1].equals(siteApiTypeRelationI18n.getApiTypeId()+"")) {
                    list.add(siteApiTypeRelationI18n);
                    removeList.remove(siteApiTypeRelationI18n);
                    break;
                }
            }
        }

        list.addAll(removeList);
        return list;
    }

}
