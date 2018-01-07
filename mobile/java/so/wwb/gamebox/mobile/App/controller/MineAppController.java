package so.wwb.gamebox.mobile.App.controller;

import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.App.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.App.enums.AppMineLinkEnum;
import so.wwb.gamebox.mobile.App.model.*;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerGameOrder;
import so.wwb.gamebox.model.master.player.po.UserBankcard;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderListVo;
import so.wwb.gamebox.model.master.player.vo.PlayerGameOrderVo;
import so.wwb.gamebox.model.master.player.vo.UserBankcardVo;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static so.wwb.gamebox.mobile.App.constant.AppConstant.*;

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
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (!isLoginUser()) {
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }

        Map<String, Object> map = MapTool.newHashMap();
        Map<String, Object> userInfoMap = MapTool.newHashMap();
        map.put("isBit", ParamTool.isBit());
        map.put("isCash", ParamTool.isCash());
        map.put("link", setLink());
        getMineLinkInfo(userInfoMap, request);
        map.put("user", userInfoMap);
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    @RequestMapping("/getWithDraw")
    @ResponseBody
    public String getWithDraw() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (!isLoginUser()) {
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }

        //是否已存在取款订单
        if (hasOrder()) {
            vo.setCode(AppErrorCodeEnum.hasOrder.getCode());
            vo.setMsg(AppErrorCodeEnum.hasOrder.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //是否被冻结
        if (hasFreeze()) {
            vo.setCode(AppErrorCodeEnum.hasFreeze.getCode());
            vo.setMsg(AppErrorCodeEnum.hasFreeze.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //今日取款是否达到上限
        if (isFull()) {
            vo.setCode(AppErrorCodeEnum.IsFull.getCode());
            vo.setMsg(AppErrorCodeEnum.IsFull.getMsg());
            vo.setError(1);
            return JsonTool.toJson(vo);
        }
        //余额是否充足
        Map<String, Object> map = MapTool.newHashMap();
        if(isBalanceAdequate(map)){
            vo.setCode(AppErrorCodeEnum.IsBalanceAdequate.getCode());
            vo.setMsg(AppErrorCodeEnum.IsBalanceAdequate.getMsg().replace(targetRegex,map.get("withdrawMinNum").toString()));
            vo.setError(1);
            return JsonTool.toJson(vo);
        }

        withdraw(map);
        vo.setCode(AppErrorCodeEnum.Success.getCode());
        vo.setMsg(AppErrorCodeEnum.Success.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
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

        vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);
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

    @RequestMapping("/addCard")
    @ResponseBody
    public String addCard() {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }


        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BANK);
        AppModelVo appModelVo = new AppModelVo();
        if (userBankcard == null) {
            //获取银行列表
            mapJson.put("data", bankList());
            appModelVo.setCode(200);
            appModelVo.setMsg("用户添加银行卡");
        }else {
            appModelVo.setCode(201);
            appModelVo.setMsg("展示银行卡信息");
            appModelVo.setData(userBankcard);
        }
        setMapJson(appModelVo);
        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/addBtc")
    @ResponseBody
    public String addBtc() {
        if (!isLoginUser()) {
            return JsonTool.toJson(mapJson);
        }

        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BTC);
        AppModelVo appModelVo = new AppModelVo();

        if (userBankcard == null) {
            appModelVo.setCode(202);
            appModelVo.setMsg("用户添加比特币");
        }else {
            appModelVo.setMsg("展示比特币信息");
            appModelVo.setData(userBankcard);
        }
        setMapJson(appModelVo);

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/submitBtc")
    @ResponseBody
    public String submitBtc(String bankcardNumber) {
        /*比特币*/
        final String BITCOIN = "bitcoin";
        UserBankcardVo bankcardVo = new UserBankcardVo();
        bankcardVo.setResult(new UserBankcard()); //暂时写死，为了测试接口是否成功
        bankcardVo.getResult().setBankcardNumber("abcdefghiklmnopqrstuvwxyz");

        String userName = SessionManagerCommon.getUserName();
        AppModelVo appModelVo = new AppModelVo();
        if (checkCardIsExistsByUserId(bankcardVo)) {
            appModelVo.setCode(204);
            appModelVo.setMsg("用户绑定比特币已存在");
        } else {
            UserBankcard bankcard = bankcardVo.getResult();
            bankcard.setUserId(getAgentId());
            bankcard.setType(UserBankcardTypeEnum.BITCOIN.getCode());
            bankcard.setBankName(BITCOIN);
            bankcardVo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(bankcardVo);
            appModelVo.setCode(205);
            appModelVo.setMsg("用户绑定比特币成功");
        }
        setMapJson(appModelVo);


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
        listVo = ServiceSiteTool.vPlayerTransactionService().search(listVo);
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
            vo = ServiceSiteTool.vPlayerTransactionService().get(vo);
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
    public String getBettingList(Date beginBetTime, Date endBetTime,Integer pageSize,Integer currentIndex) {

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

        listVo.getPaging().setPageSize(pageSize);
        listVo.getPaging().setPageNumber((pageSize - currentIndex % pageSize + currentIndex) / pageSize);//计算出页码


        initQueryDateForgetBetting(listVo,TIME_INTERVAL,DEFAULT_TIME);
        listVo = ServiceSiteTool.playerGameOrderService().search(listVo);

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
        vo = ServiceSiteTool.playerGameOrderService().getGameOrderDetail(vo);
//        如果不是这个玩家投注的订单，则无视该笔订单
        if (vo.getResult() == null || vo.getResult().getPlayerId() != SessionManager.getUserId().intValue()) {
            vo.setResult(null);
            vo.setResultArray(null);
        }
        setMapJson(new AppModelVo());
        mapJson.put("data", buildBettingDetail(vo));

        return JsonTool.toJson(mapJson);
    }

    @RequestMapping("/updateSafePassword")
    @ResponseBody
    public String updateSafePassword(){
        AppModelVo vo = new AppModelVo();
        vo.setVersion(appVersion);

        if (!isLoginUser()) {
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setError(1);
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            return JsonTool.toJson(vo);
        }

        SysUser user = SessionManager.getUser();
        Map<String,Object> map = MapTool.newHashMap();
        map.put("hasRealName",StringTool.isNotBlank(user.getRealName()));
        if(StringTool.isBlank(user.getPermissionPwd())){
            map.put("hasPermissionPwd",false);
            vo.setData(map);
        }else{
            map.put("hasPermissionPwd",true);
            if (isLock(user)) {//如果冻结
                map.put("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
                map.put("lockTime", formatLockTime(user.getSecpwdFreezeStartTime()));
                vo.setData(map);
                return JsonTool.toJson(vo);
            }else{//判断是否出现验证码,大于2显示验证码
                Integer errorTimes = user.getSecpwdErrorTimes();
                errorTimes = errorTimes == null ? 0 : errorTimes;
                map.put("isOpenCaptcha", errorTimes > 1);
                map.put("remindTimes", appErrorTimes - errorTimes);
                vo.setData(map);
            }
        }

        return JsonTool.toJson(vo);
    }

    private String formatLockTime(Date date) {
        return LocaleDateTool.formatDate(date, DateTool.yyyy_MM_dd_HH_mm_ss, SessionManager.getTimeZone());
    }
    /**
     * 是否锁定
     */
    private boolean isLock(SysUser user) {
        Date now = SessionManager.getDate().getNow();
        if (user != null) {
            if (user.getSecpwdFreezeEndTime() == null) {
                return false;
            }
            if (now.before(user.getSecpwdFreezeEndTime())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 设置我的链接地址
     * @return
     */
    private List<AppMineLinkVo> setLink() {
        List<AppMineLinkVo> links = ListTool.newArrayList();
        for(AppMineLinkEnum linkEnum : AppMineLinkEnum.values()){
            AppMineLinkVo vo = new AppMineLinkVo();
            vo.setCode(linkEnum.getCode());
            vo.setName(linkEnum.getName());
            vo.setLink(linkEnum.getLink());
            links.add(vo);
        }

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
            mapJson.put("data", app.getData());
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
