package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.master.enums.AppErrorCodeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.setting.vo.AppMineLinkVo;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by ed on 17-12-31.
 */
@Controller
@RequestMapping("/mineOrigin")
public class MineAppController extends BaseMineController{
    private final String version = "app_01";
    Map<String, Object> mapJson = MapTool.newHashMap();

    @RequestMapping("/getLink")
    @ResponseBody
    public String getLink(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();

        if(!isLoginUser()){
            return JsonTool.toJson(mapJson);
        }

        map.put("isBit", ParamTool.isBit());
        map.put("isCash", ParamTool.isCash());

        map.put("link", setLink());

        Map<String,Object> userInfoMap = MapTool.newHashMap();
        getMineLinkInfo(userInfoMap,request);
        map.put("user", userInfoMap);

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getWithDraw")
    @ResponseBody
    public String getWithDraw(){
        if(!isLoginUser()){
            return JsonTool.toJson(mapJson);
        }

        if(hasOrder()){
            AppModelVo order = new AppModelVo();
        }

        Map<String,Object> map = MapTool.newHashMap();

        withdraw(map);

        setMapJson(new AppModelVo());
        mapJson.put("data",map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getMyPromo")
    @ResponseBody
    public String getMyPromo(HttpServletRequest request) {
        isLoginUser();
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();

        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setUserId(SessionManager.getUserId());
        vPreferentialRecodeListVo.getSearch().setCurrentDate(SessionManager.getDate().getNow());

        vPreferentialRecodeListVo = ServiceTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
        setMapJson(new AppModelVo());
        mapJson.put("data", vPreferentialRecodeListVo);
        return JsonTool.toJson(mapJson);

    }

    @RequestMapping("/getFundRecord")
    @ResponseBody
    public String getFundRecord() {
        if(!isLoginUser()){
            return JsonTool.toJson(mapJson);
        }

        VPlayerTransactionListVo listVo = new VPlayerTransactionListVo();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        if (listVo.getSearch().getEndCreateTime() != null){
            listVo.getSearch().setEndCreateTime(DateTool.addDays(listVo.getSearch().getEndCreateTime(), 1));
        }
        getFund(mapJson);
        listVo.getSearch().setNoDisplay(TransactionWayEnum.MANUAL_PAYOUT.getCode());
        listVo.getSearch().setLotterySite(ParamTool.isLotterySite());
        listVo = ServiceTool.vPlayerTransactionService().search(listVo);
        setMapJson(new AppModelVo());
        mapJson.put("data", listVo);
        return JsonTool.toJson(mapJson);
    }

    private List<AppMineLinkVo> setLink() {
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

        return links;
    }
    private void setMapJson(AppModelVo app) {
        if (app.getError() != 0) {
            mapJson.put("error", app.getError());
        } else {
            mapJson.put("error", 0);
        }

        if (app.getCode() != 0) {
            mapJson.put("code", app.getCode());
        } else {
            mapJson.put("code", AppErrorCodeEnum.Success.getCode());
        }

        if (StringTool.isNotBlank(app.getMsg())) {
            mapJson.put("msg", app.getMsg());
        } else {
            mapJson.put("msg", AppErrorCodeEnum.Success.getMsg());
        }

        if (StringTool.isNotBlank(app.getVersion())) {
            mapJson.put("version", app.getVersion());
        } else {
            mapJson.put("version", version);
        }
    }

    /**
     * 是否有登陆账号
     */
    public boolean isLoginUser() {
        if (SessionManager.getUser() == null) {
            AppModelVo appVo = new AppModelVo();
            appVo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            appVo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            appVo.setError(1);

            setMapJson(appVo);

            return false;
        }
        return true;
    }
}
