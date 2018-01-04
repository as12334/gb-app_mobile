package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.App.model.BettingDataApp;
import so.wwb.gamebox.mobile.App.model.BettingInfoApp;
import so.wwb.gamebox.mobile.App.model.RecordDetailApp;
import so.wwb.gamebox.mobile.App.model.UserInfoApp;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.master.enums.AppErrorCodeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.po.PlayerGameOrder;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderVo;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.model.master.setting.vo.AppMineLinkVo;
import so.wwb.gamebox.model.master.setting.vo.AppModelVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ed on 17-12-31.
 */
@Controller
@RequestMapping("/mineOrigin")
public class MineAppController extends BaseMineController {
    private final String version = "app_01";
    Map<String, Object> mapJson = MapTool.newHashMap();

    @RequestMapping("/getLink")
    @ResponseBody
    public String getLink(HttpServletRequest request) {
        Map<String, Object> map = MapTool.newHashMap();

        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }

        map.put("isBit", ParamTool.isBit());
        map.put("isCash", ParamTool.isCash());

        map.put("link", setLink());

        Map<String, Object> userInfoMap = MapTool.newHashMap();
        getMineLinkInfo(userInfoMap, request);
        map.put("user", userInfoMap);

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getWithDraw")
    @ResponseBody
    public String getWithDraw() {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }
        //是否已存在取款订单
        if (hasOrder()) {
            AppModelVo order = new AppModelVo();
            order.setCode(AppErrorCodeEnum.hasOrder.getCode());
            order.setMsg(AppErrorCodeEnum.hasOrder.getMsg());
            order.setError(1);
            setMapJson(order);

            return JsonTool.toJson(mapJson);
        }
        //是否被冻结
        if (hasFreeze()) {
            AppModelVo freeze = new AppModelVo();
            freeze.setCode(AppErrorCodeEnum.hasFreeze.getCode());
            freeze.setMsg(AppErrorCodeEnum.hasFreeze.getMsg());
            freeze.setError(1);
            setMapJson(freeze);

            return JsonTool.toJson(mapJson);
        }
        //今日取款是否达到上限
        if (isFull()) {
            AppModelVo full = new AppModelVo();
            full.setCode(AppErrorCodeEnum.IsFull.getCode());
            full.setMsg(AppErrorCodeEnum.IsFull.getMsg());
            full.setError(1);
            setMapJson(full);

            return JsonTool.toJson(mapJson);
        }
        //余额是否充足
        Map<String, Object> map = MapTool.newHashMap();
        if(isBalanceAdequate(map)){
            AppModelVo balance = new AppModelVo();
            balance.setCode(AppErrorCodeEnum.IsBalanceAdequate.getCode());
            balance.setMsg(AppErrorCodeEnum.IsBalanceAdequate.getMsg().replace("x",map.get("withdrawMinNum").toString()));
            balance.setError(1);

            return JsonTool.toJson(mapJson);
        }



        withdraw(map);

        setMapJson(new AppModelVo());
        mapJson.put("data", map);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getMyPromo")
    @ResponseBody
    public String getMyPromo(HttpServletRequest request) {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();

        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setUserId(SessionManager.getUserId());
        vPreferentialRecodeListVo.getSearch().setCurrentDate(SessionManager.getDate().getNow());

        vPreferentialRecodeListVo = ServiceTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
        setMapJson(new AppModelVo());
        mapJson.put("data", vPreferentialRecodeListVo);
        return JsonTool.toJson(mapJson);

    }

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpServletRequest request) {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }

        UserInfoApp userApp = new UserInfoApp();
        SysUser user = SessionManager.getUser();

        getAppUserInfo(request, user, userApp);

        setMapJson(new AppModelVo());
        mapJson.put("data", userApp);

        return JsonTool.toJson(mapJson);
    }


    @RequestMapping("/refresh")
    @ResponseBody
    public String refresh(HttpServletRequest request) {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }
        Integer userId = SessionManager.getUserId();
        PlayerApiListVo listVo = initPlayerApiListVo(userId);
        VUserPlayer player = getVPlayer(userId);

        UserInfoApp infoApp = new UserInfoApp();
        infoApp.setApis(getSiteApis(listVo, request, true));
        infoApp.setCurrSign(player.getCurrencySign());
        infoApp.setAssets(queryPlayerAssets(listVo, userId));
        infoApp.setUsername(player.getUsername());

        setMapJson(new AppModelVo());
        mapJson.put("data", infoApp);
        return JsonTool.toJson(mapJson);
    }




    @RequestMapping("/getFundRecord")
    @ResponseBody
    public String getFundRecord() {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }

        VPlayerTransactionListVo listVo = new VPlayerTransactionListVo();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        if (listVo.getSearch().getEndCreateTime() != null) {
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



    @RequestMapping("/getFundRecordDetails")
    @ResponseBody
    public String getFundRecordDetails(String searchId) {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }
        searchId = String.valueOf(5002473);
        if (StringTool.isNotBlank(searchId)) {
            VPlayerTransactionVo vo = new VPlayerTransactionVo();
            vo.getSearch().setId(Integer.valueOf(searchId));
            vo = ServiceTool.vPlayerTransactionService().get(vo);
            setMapJson(new AppModelVo());

            VPlayerTransaction po = vo.getResult();

            RecordDetailApp recordDetailApp = new RecordDetailApp();
            recordDetailApp.setId(po.getId());
            recordDetailApp.setTransactionNo(po.getTransactionNo());
            recordDetailApp.setCreateTime(po.getCreateTime());
            recordDetailApp.setTransactionType(po.getTransactionType());
            recordDetailApp.setTransactionMoney(po.getTransactionMoney());
            recordDetailApp.setStatus(po.getStatus());
            recordDetailApp.setFailureReason(po.getFailureReason());
            recordDetailApp.setAdministrativeFee(po.getAdministrativeFee());
            recordDetailApp.setDeductFavorable(po.getDeductFavorable());
            recordDetailApp.setFundType(po.getFundType());
            recordDetailApp.setTransactionWay(po.getTransactionWay());
            recordDetailApp.setUsername(po.getUsername());
            recordDetailApp.setPayerBankcard(po.getPayerBankcard());
            recordDetailApp.setRechargeTotalAmount(po.getRechargeTotalAmount());
            recordDetailApp.setRechargeAmount(po.getRechargeAmount());
            recordDetailApp.setRechargeAddress(po.getRechargeAddress());
            mapJson.put("data", recordDetailApp);
        }

        return JsonTool.toJson(mapJson);
    }


    @RequestMapping("/getBettingList")
    @ResponseBody
    public String getBettingList(Date beginBetTime, Date endBetTime) {

        final int TIME_INTERVAL = -30;
        final int DEFAULT_TIME = 1;
        if (!isLoginUser()) {
            JsonTool.toJson(mapJson);
        }
        PlayerGameOrderListVo listVo = new PlayerGameOrderListVo();
        BettingDataApp bettingDataApp = new BettingDataApp();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setBeginBetTime(beginBetTime);
        listVo.getSearch().setEndBetTime(endBetTime);
        if (listVo.getSearch().getEndBetTime() != null) {
            listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1),-1));
        }


        initQueryDateForgetBetting(listVo,TIME_INTERVAL,DEFAULT_TIME);
        listVo = ServiceTool.playerGameOrderService().search(listVo);

        List<PlayerGameOrder> gameOrderList = listVo.getResult();

        bettingDataApp.setStatisticsData(statisticsData(listVo, TIME_INTERVAL, DEFAULT_TIME));
        bettingDataApp.setList(buildBetting(gameOrderList));

        //设置默认时间
        bettingDataApp.setMinDate(SessionManager.getDate().addDays(TIME_INTERVAL));
        bettingDataApp.setMaxDate(SessionManager.getDate().getNow());

        setMapJson(new AppModelVo());
        mapJson.put("data", bettingDataApp);
        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/getBettingDetails")
    @ResponseBody
    public String getBettingDetails(Integer id) {
        PlayerGameOrderVo vo = new PlayerGameOrderVo();
        id = 10111337;//暂时写死，为了验证json
        vo.getSearch().setId(id);
        vo = ServiceTool.playerGameOrderService().getGameOrderDetail(vo);
//        如果不是这个玩家投注的订单，则无视该笔订单
        if (vo.getResult() == null || vo.getResult().getPlayerId() != SessionManager.getUserId().intValue()) {
            vo.setResult(null);
            vo.setResultArray(null);
        }
        setMapJson(new AppModelVo());
        mapJson.put("data", buildBettingDetail(vo));

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
        if (app.getData() != null) {
            mapJson.put("version", app.getData());
        } else {
            mapJson.put("data", null);
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
            appVo.setData(null);
            setMapJson(appVo);

            return false;
        }
        return true;
    }
}
