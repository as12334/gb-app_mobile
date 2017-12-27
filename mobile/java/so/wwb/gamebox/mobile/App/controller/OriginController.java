package so.wwb.gamebox.mobile.App.controller;


import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.controller.BaseApiController;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.setting.vo.AppMineLinkVo;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by  on 17-4-3.
 */
@Controller
@RequestMapping("/origin")
public class OriginController extends BaseApiController {
    private final String version = "app_01";
    Map<String,Object> mapJson = MapTool.newHashMap();

    //region mainIndex
    @RequestMapping("/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request) {
        Map<String,Object> map = MapTool.newHashMap();
        Map<String,Object> lotteryGame = MapTool.newHashMap();
        Map<String,Object> casinoMap = MapTool.newHashMap();
        List<Map> floatList = ListTool.newArrayList();

        //浮动图
        showMoneyActivityFloat(floatList);

        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        map.put("announcement", getAnnouncement());

        map.put("siteApiRelation", getSiteApiRelationI18n(lotteryGame,casinoMap));
        map.put("activity", floatList);
        map.put("lotteryGame",lotteryGame);
        map.put("casinoMap",casinoMap);

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getCarouse")
    @ResponseBody
    public String getCarouse(HttpServletRequest request){
        Map<String,Object> map = MapTool.newHashMap();
        //轮播图
        map.put("banner", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getAnnouncement")
    @ResponseBody
    public String getAnnounce(){
        Map<String,Object> map = MapTool.newHashMap();
        //公告
        map.put("announcement", getAnnouncement());

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getSiteApiRelation")
    @ResponseBody
    public String getSiteApi(){
        Map<String,Object> map = MapTool.newHashMap();
        Map<String,Object> lotteryGame = MapTool.newHashMap();
        Map<String,Object> casinoMap = MapTool.newHashMap();
        //公告
        map.put("siteApiRelation", getSiteApiRelationI18n(lotteryGame,casinoMap));
        map.put("lotteryGame",lotteryGame);
        map.put("casinoMap",casinoMap);
        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getFloat")
    @ResponseBody
    public String getFloat(){
        Map<String,Object> map = MapTool.newHashMap();
        List<Map> floatList = ListTool.newArrayList();
        //浮动图
        showMoneyActivityFloat(floatList);
        map.put("activity", floatList);

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }
    //endregion mainIndex

    //region mine
    @RequestMapping("/getLink")
    @ResponseBody
    public String getLink(){
        Map<String,Object> map = MapTool.newHashMap();

        map.put("isBit", ParamTool.isBit());
        map.put("isCash", ParamTool.isCash());

        map.put("link",setLink());

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public String getUser(HttpServletRequest request){
        Map<String,Object> map = MapTool.newHashMap();

        getUserInfo(map,request);

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    //endregion mine

    private List<AppMineLinkVo> setLink(){
        List<AppMineLinkVo> links = ListTool.newArrayList();
        AppMineLinkVo deposit = new AppMineLinkVo();
        deposit.setCode("deposit");
        deposit.setName("存款");
        deposit.setLink("/wallet/deposit/index.html");
        links.add(deposit);

        AppMineLinkVo withdraw = new AppMineLinkVo();
        withdraw.setCode("withdraw");
        withdraw.setName("取款");
        withdraw.setLink("/wallet/withdraw/index.html");
        links.add(withdraw);

        AppMineLinkVo transfer = new AppMineLinkVo();
        transfer.setCode("transfer");
        transfer.setName("额度转换");
        transfer.setLink("/transfer/index.html");
        links.add(transfer);

        AppMineLinkVo record = new AppMineLinkVo();
        record.setCode("record");
        record.setName("资金记录");
        record.setLink("/fund/record/index.html");
        links.add(record);

        AppMineLinkVo betting = new AppMineLinkVo();
        betting.setCode("betting");
        betting.setName("投注记录");
        betting.setLink("/fund/betting/index.html");
        links.add(betting);

        AppMineLinkVo myPromo = new AppMineLinkVo();
        myPromo.setCode("myPromo");
        myPromo.setName("优惠记录");
        myPromo.setLink("/promo/myPromo.html");
        links.add(myPromo);

        AppMineLinkVo bankCard = new AppMineLinkVo();
        bankCard.setCode("bankCard");
        bankCard.setName("银行卡");
        bankCard.setLink("/bankCard/page/addCard.html");
        links.add(bankCard);

        AppMineLinkVo btc = new AppMineLinkVo();
        btc.setCode("btc");
        btc.setName("比特币钱包");
        btc.setLink("/bankCard/page/addBtc.html");
        links.add(btc);

        AppMineLinkVo gameNotice = new AppMineLinkVo();
        gameNotice.setCode("gameNotice");
        gameNotice.setName("申请优惠");
        gameNotice.setLink("/message/gameNotice.html?isSendMessage=true");
        links.add(gameNotice);

        AppMineLinkVo securityPassword = new AppMineLinkVo();
        securityPassword.setCode("securityPassword");
        securityPassword.setName("修改安全密码");
        securityPassword.setLink("/passport/securityPassword/edit.html");
        links.add(securityPassword);

        AppMineLinkVo loginPassword = new AppMineLinkVo();
        loginPassword.setCode("loginPassword");
        loginPassword.setName("修改登录密码");
        loginPassword.setLink("/my/password/editPassword.html");
        links.add(loginPassword);

        ///wallet/deposit/index.html 存款
        ///wallet/withdraw/index.html 取款
        ///transfer/index.html 额度转换
        ///fund/record/index.html 资金记录
        ///fund/betting/index.html 投注记录
        ///promo/myPromo.html 优惠记录
        ///bankCard/page/addCard.html 银行卡
        ///bankCard/page/addBtc.html 比特币钱包
        ///message/gameNotice.html?isSendMessage=true 申请优惠
        ///passport/securityPassword/edit.html 修改安全密码
        ///my/password/editPassword.html 修改登录密码
        return links;
    }

    private void setMapJson(AppModelVo app){
        if(StringTool.isNotBlank(app.getError())){
            mapJson.put("error",app.getError());
        }else{
            mapJson.put("error", 0);
        }

        if(StringTool.isNotBlank(app.getCode())){
            mapJson.put("code", app.getCode());
        }else{
            mapJson.put("code", null);
        }

        if(StringTool.isNotBlank(app.getMsg())){
            mapJson.put("msg", app.getMsg());
        }else{
            mapJson.put("msg", "请求成功");
        }

        if(StringTool.isNotBlank(app.getVersion())){
            mapJson.put("version", app.getVersion());
        }else{
            mapJson.put("version", version);
        }
    }

    @Override
    protected String getDemoIndex() {
        return null;
    }
}