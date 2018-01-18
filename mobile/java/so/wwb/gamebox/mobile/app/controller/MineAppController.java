package so.wwb.gamebox.mobile.app.controller;

import org.apache.shiro.session.SessionException;
import org.soul.commons.bean.Pair;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateFormat;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criterion;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.support._Module;
import org.soul.model.log.audit.enums.OpMode;
import org.soul.model.msg.notice.vo.NoticeReceiveVo;
import org.soul.model.msg.notice.vo.NoticeVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.session.SessionKey;
import org.soul.model.sys.po.SysDict;
import org.soul.web.shiro.local.PassportResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.app.common.CommonApp;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.enums.AppMineLinkEnum;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.controller.BaseMineController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.common.notice.enums.AutoNoticeEvent;
import so.wwb.gamebox.model.common.notice.enums.NoticeParamEnum;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.listop.FreezeTime;
import so.wwb.gamebox.model.listop.FreezeType;
import so.wwb.gamebox.model.master.enums.UserTaskEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.fund.vo.VPlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.PlayerAdvisoryEnum;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.model.master.tasknotify.vo.UserTaskReminderVo;
import so.wwb.gamebox.model.passport.vo.SecurityPassword;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;
import so.wwb.gamebox.web.shiro.common.delegate.IPassportDelegate;
import so.wwb.gamebox.web.shiro.common.filter.KickoutFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

/**
 * Created by ed on 17-12-31.
 */
@Controller
@RequestMapping("/mineOrigin")
public class MineAppController extends BaseMineController {
    private Log LOG = LogFactory.getLog(MineAppController.class);
    private static final String safePasswordUrl = "/captcha/securityPwd.html";

    /**
     * 我的用户信息，链接
     *
     * @param request
     * @return
     */
    @RequestMapping("/getLink")
    @ResponseBody
    public String getLink(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        Map<String, Object> map = new HashMap<>(FOUR, ONE_FLOAT);
        Map<String, Object> userInfoMap = MapTool.newHashMap();
        map.put("isBit", ParamTool.isBit());//是否存在比特币
        map.put("isCash", ParamTool.isCash());//是否存在银行卡
        map.put("link", setLink());//链接地址
        getMineLinkInfo(userInfoMap, request);//用户金额信息
        map.put("user", userInfoMap);
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 获取取款信息
     *
     * @return
     */
    @RequestMapping("/getWithDraw")
    @ResponseBody
    public String getWithDraw() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMsg())) {
            return JsonTool.toJson(vo);
        }

        Map<String, Object> map = MapTool.newHashMap();
        withdraw(map);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 提交取款
     */
    @RequestMapping("/submitWithdraw")
    @ResponseBody
    @Token(valid = true)
    public String submitWithdraw(HttpServletRequest request, PlayerTransactionVo playerVo) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        vo = withDraw(vo);
        if (StringTool.isNotBlank(vo.getMsg())) {
            return JsonTool.toJson(vo);
        }

        //是否有取款银行卡
        Map map = MapTool.newHashMap();
        if (!hasBank(map)) {
            vo.setCode(AppErrorCodeEnum.NO_BANK.getCode());
            vo.setMsg(AppErrorCodeEnum.NO_BANK.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        //是否符合取款金额设置
        if (isInvalidAmount(playerVo, map)) {
            vo.setError(DEFAULT_TIME);
            vo.setMsg(map.get("msg").toString());
            vo.setCode(AppErrorCodeEnum.WITH_DRAW_BETWEEN_MIN_MAX.getCode());
            return JsonTool.toJson(vo);
        }

        //取款
        map = addWithdraw(request, playerVo);
        //成功
        if (map.get("state") != null && MapTool.getBoolean(map, "state")) {
            vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
            vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
            vo.setData(map);
            return JsonTool.toJson(vo);
        }

        vo.setData(map);
        vo.setCode(AppErrorCodeEnum.WITH_DRAW_FAIL.getCode());
        vo.setMsg(AppErrorCodeEnum.WITH_DRAW_FAIL.getMsg());

        return JsonTool.toJson(vo);
    }

    /**
     * 取款判断
     *
     * @param vo
     * @return
     */
    private AppModelVo withDraw(AppModelVo vo) {
        if (!isLoginUser(vo)) {
            return vo;
        }

        //是否已存在取款订单
        if (hasOrder()) {
            vo.setCode(AppErrorCodeEnum.WITH_DRAW_HAS_ORDER.getCode());
            vo.setMsg(AppErrorCodeEnum.WITH_DRAW_HAS_ORDER.getMsg());
            vo.setError(DEFAULT_TIME);
            return vo;
        }
        //是否被冻结
        if (hasFreeze()) {
            vo.setCode(AppErrorCodeEnum.USER_HAS_FREEZE.getCode());
            vo.setMsg(AppErrorCodeEnum.USER_HAS_FREEZE.getMsg());
            vo.setError(DEFAULT_TIME);
            return vo;
        }
        //今日取款是否达到上限
        if (isFull()) {
            vo.setCode(AppErrorCodeEnum.WITH_DRAW_IS_FULL.getCode());
            vo.setMsg(AppErrorCodeEnum.WITH_DRAW_IS_FULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return vo;
        }
        //余额是否充足
        Map<String, Object> map = MapTool.newHashMap();
        if (isBalanceAdequate(map)) {
            vo.setCode(AppErrorCodeEnum.WITH_DRAW_MIN_AMOUNT.getCode());
            vo.setMsg(AppErrorCodeEnum.WITH_DRAW_MIN_AMOUNT.getMsg().replace(TARGET_REGEX, map.get("withdrawMinNum").toString()));
            vo.setError(DEFAULT_TIME);
            return vo;
        }

        return vo;
    }

    /**
     * 　我的优惠记录
     *
     * @param vPreferentialRecodeListVo
     * @return
     */
    @RequestMapping("/getMyPromo")
    @ResponseBody
    public String getMyPromo(VPreferentialRecodeListVo vPreferentialRecodeListVo) {
        AppModelVo vo = new AppModelVo();

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }
        if (vPreferentialRecodeListVo == null) {
            vPreferentialRecodeListVo = new VPreferentialRecodeListVo();
        }


        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setUserId(SessionManager.getUserId());
        vPreferentialRecodeListVo.getSearch().setCurrentDate(SessionManager.getDate().getNow());

        vPreferentialRecodeListVo = ServiceSiteTool.vPreferentialRecodeService().search(vPreferentialRecodeListVo);

        List<MyPromoApp> myPromoApps = ListTool.newArrayList();

        List<VPreferentialRecode> vPreferentialRecodeList = vPreferentialRecodeListVo.getResult();
        Integer userId = SessionManager.getUser().getId();
        for (VPreferentialRecode recode : vPreferentialRecodeList) {

            MyPromoApp promoApp = new MyPromoApp();

            if (userId != null) {
                promoApp.setId(recode.getId());
            }
            promoApp.setApplyTime(recode.getApplyTime());
            if (recode.getPreferentialAudit() != null && recode.getPreferentialAudit() != 0) {
                promoApp.setPreferentialAuditName("倍稽核");  // 倍稽核
            } else {
                promoApp.setPreferentialAuditName("免稽核");
            }
            promoApp.setPreferentialAudit(recode.getPreferentialAudit());
            promoApp.setActivityName(recode.getActivityName());
            promoApp.setPreferentialValue(recode.getPreferentialValue());
            promoApp.setUserId(userId);
            promoApp.setCheckState(recode.getCheckState());
            String checkState = recode.getCheckState();
            if (StringTool.equalsIgnoreCase("success", checkState) || StringTool.equalsIgnoreCase("2", checkState)
                    || StringTool.equalsIgnoreCase("4", checkState)) {
                promoApp.setCheckStateName("已发放");
            } else if (StringTool.equalsIgnoreCase("1", checkState)) {
                promoApp.setCheckStateName("待审核");
            } else if (StringTool.equalsIgnoreCase("0", checkState)) {
                promoApp.setCheckStateName("进行中");
            }

            promoApp.setCheckState(recode.getCheckState());

            myPromoApps.add(promoApp);

        }

        Map<String, Object> map = MapTool.newHashMap();
        map.put("totalCount", vPreferentialRecodeListVo.getPaging().getTotalCount()); // 总数
        map.put("list", myPromoApps);

        vo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(vo);

    }

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        UserInfoApp userApp = new UserInfoApp();
        SysUser user = SessionManager.getUser();
        getAppUserInfo(request, user, userApp);

        Map<String, Object> map = MapTool.newHashMap();
        Map<String, Object> userInfoMap = MapTool.newHashMap();

        map.put("link", setLink());//链接地址
        getMineLinkInfo(userInfoMap, request);//用户金额信息
        map.put("user", userInfoMap);
        map.put("bankList", bankList());
        map.put("userApi", userApp);
        vo = CommonApp.buildAppModelVo(map);

        return JsonTool.toJson(vo);
    }


    /**
     * 一键刷新
     *
     * @param request
     * @return
     */
    @RequestMapping("/refresh")
    @ResponseBody
    public String refresh(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }
        UserInfoApp userInfo = appRefresh(request);
        vo = CommonApp.buildAppModelVo(userInfo);

        return JsonTool.toJson(vo);
    }

    /**
     * 跳转和获取用户银行卡信息
     *
     * @return
     */
    @RequestMapping("/addCard")
    @ResponseBody
    public String addCard() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BANK);
        AppModelVo appModelVo = new AppModelVo();
        if (userBankcard == null) {
            //获取银行列表
            appModelVo.setCode(AppErrorCodeEnum.USER_ADD_BANK_CARD.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.USER_ADD_BANK_CARD.getMsg());
            appModelVo.setData(bankList());
        } else {
            appModelVo.setCode(AppErrorCodeEnum.SHOW_BANK_CARD_INFO.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.SHOW_BANK_CARD_INFO.getMsg());
            appModelVo.setData(userBankcard);
        }
        return JsonTool.toJson(appModelVo);
    }


    /**
     * 提交银行卡信息
     *
     * @param vo
     * @return
     */
    @RequestMapping("/submitBankCard")
    @ResponseBody
    public String submitBankCard(UserBankcardVo vo) {
        AppModelVo appModelVo = new AppModelVo();
        String userName = SessionManagerCommon.getUserName();
        UserBankcard userBankcard = vo.getResult();
        if (checkCardIsExistsByUserId(vo)) {
            appModelVo.setCode(AppErrorCodeEnum.USER_BINDING_BANK_CARD_EXIST.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.USER_BINDING_BANK_CARD_EXIST.getMsg());
            appModelVo.setError(DEFAULT_TIME);
        }
        if (StringTool.isNotBlank(SessionManager.getUser().getRealName())) {
            vo.getResult().setBankcardMasterName(SessionManager.getUser().getRealName());
        }
        vo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(vo);
        SessionManagerCommon.refreshUser();
        if (vo.isSuccess()) {
            appModelVo = CommonApp.buildAppModelVo("");
        }
        return JsonTool.toJson(appModelVo);
    }

    /**
     * 跳转和获取用户比特币信息，（暂时废弃）
     *
     * @return
     */
    @RequestMapping("/addBtc")
    @ResponseBody
    public String addBtc() {
        AppModelVo vo = new AppModelVo();

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        UserBankcard userBankcard = BankHelper.getUserBankcard(SessionManager.getUserId(), UserBankcardTypeEnum.TYPE_BTC);

        if (userBankcard == null) {
            vo.setCode(AppErrorCodeEnum.USER_ADD_BTC.getCode());
            vo.setMsg(AppErrorCodeEnum.USER_ADD_BTC.getMsg());
        } else {
            vo = CommonApp.buildAppModelVo(userBankcard);
            vo.setMsg("展示比特币信息");

        }

        return JsonTool.toJson(vo);
    }

    /**
     * 提交比特币信息
     *
     * @param bankcardNumber
     * @return
     */
    @RequestMapping("/submitBtc")
    @ResponseBody
    public String submitBtc(String bankcardNumber) {
        /*比特币*/
        final String BITCOIN = "bitcoin";
        UserBankcardVo bankcardVo = new UserBankcardVo();
        bankcardVo.setResult(new UserBankcard()); //暂时写死，为了测试接口是否成功
        bankcardVo.getResult().setBankcardNumber(bankcardNumber);
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(AppConstant.APP_VERSION);
        if (checkCardIsExistsByUserId(bankcardVo)) {
            AppErrorCodeEnum.HAS_BTC.getCode();
            appModelVo.setCode(AppErrorCodeEnum.HAS_BTC.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.HAS_BTC.getMsg());
            return JsonTool.toJson(appModelVo);
        }

        UserBankcard bankcard = bankcardVo.getResult();
        bankcard.setUserId(getAgentId());
        bankcard.setType(UserBankcardTypeEnum.BITCOIN.getCode());
        bankcard.setBankName(BITCOIN);
        bankcardVo = ServiceSiteTool.userBankcardService().saveAndUpdateUserBankcard(bankcardVo);
        if (!bankcardVo.isSuccess()) {
            appModelVo.setCode(AppErrorCodeEnum.SUBMIT_BTC_FAIL.getCode());
            appModelVo.setMsg(AppErrorCodeEnum.SUBMIT_BTC_FAIL.getMsg());
            appModelVo.setError(DEFAULT_TIME);
            return JsonTool.toJson(appModelVo);
        }
        appModelVo.setCode(AppErrorCodeEnum.USER_BINDING_BTC_SUCCESS.getCode());
        appModelVo.setMsg(AppErrorCodeEnum.USER_BINDING_BTC_SUCCESS.getMsg());

        return JsonTool.toJson(appModelVo);
    }


    /**
     * 获取资金记录列表
     *
     * @param listVo
     * @return
     */
    @RequestMapping("/getFundRecord")
    @ResponseBody
    public String getFundRecord(VPlayerTransactionListVo listVo) {
        AppModelVo vo = new AppModelVo();

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        if (listVo.getSearch().getEndCreateTime() != null) {
            listVo.getSearch().setEndCreateTime(DateTool.addDays(listVo.getSearch().getEndCreateTime(), 1));
        }
        FundRecordApp fundRecordApp = new FundRecordApp();
        getFund(fundRecordApp);
        listVo.getSearch().setNoDisplay(TransactionWayEnum.MANUAL_PAYOUT.getCode());
        listVo.getSearch().setLotterySite(ParamTool.isLotterySite());
        listVo = ServiceSiteTool.vPlayerTransactionService().search(listVo);


        List<VPlayerTransaction> vPlayerTransactionList = listVo.getResult();

        List<FundListApp> fundListAppList = buildList(vPlayerTransactionList);


        fundRecordApp.setFundListApps(fundListAppList);
        if (listVo.getSearch().getBeginCreateTime() == null && listVo.getSearch().getEndCreateTime() == null) {
            fundRecordApp.setMinDate(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
            fundRecordApp.setMaxDate(SessionManager.getDate().getNow());
        } else {
            fundRecordApp.setMinDate(listVo.getSearch().getBeginCreateTime());
            fundRecordApp.setMaxDate(listVo.getSearch().getEndCreateTime());
        }
        fundRecordApp.setTotalCount(listVo.getPaging().getTotalCount());
        fundRecordApp.setCurrency(getCurrencySign(SessionManager.getUser().getDefaultCurrency()));

        vo = CommonApp.buildAppModelVo(fundRecordApp);
        return JsonTool.toJson(vo);
    }


    /**
     * 获取资金记录的，资金类型
     *
     * @return
     */
    @RequestMapping("/getTransactionType")
    @ResponseBody
    public String getTransactionType() {
        VPlayerTransactionListVo listVo = new VPlayerTransactionListVo();
        FundRecordApp recordApp = new FundRecordApp();
        listVo = preList(listVo);
        Map map = listVo.getDictCommonTransactionType();

        recordApp = buildDictCommonTransactionType(map, recordApp);
        map = recordApp.getTransactionMap();
        AppModelVo modelVo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(modelVo);
    }

    /**
     * 获取资金记录详情
     *
     * @param searchId
     * @return
     */
    @RequestMapping("/getFundRecordDetails")
    @ResponseBody
    public String getFundRecordDetails(Integer searchId) {
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(APP_VERSION);
        VPlayerWithdrawVo withdrawVo = new VPlayerWithdrawVo();
        if (!isLoginUser(appModelVo)) {
            return JsonTool.toJson(appModelVo);
        }

        if (searchId != null) {
            VPlayerTransactionVo vo = new VPlayerTransactionVo();
            vo.getSearch().setId(Integer.valueOf(searchId));
            vo = ServiceSiteTool.vPlayerTransactionService().get(vo);

            if (vo.getResult() != null && TransactionTypeEnum.WITHDRAWALS.getCode().equals(vo.getResult().getTransactionType())) {//如果是取款
                if (StringTool.isNotBlank(vo.getResult().getTransactionNo())) {
                    withdrawVo.getSearch().setId(vo.getResult().getSourceId());
                    withdrawVo = ServiceSiteTool.vPlayerWithdrawService().get(withdrawVo);
                }
            }


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
            recordDetailApp.setRealName(SessionManager.getUser().getRealName());

            Map<String, Object> map = po.get_describe();//取json对象里面的值

            if (StringTool.equalsIgnoreCase(po.getFundType(), "transfer_into")) {//表示外面的钱转入我的钱包
                Integer apiId = (Integer) map.get("API");
                recordDetailApp.setTransferOut(CacheBase.getSiteApiName(String.valueOf(apiId)));
                recordDetailApp.setTransferInto(LocaleTool.tranMessage(Module.COMMON, "FundRecord.record.playerWallet"));
            }
            if (StringTool.equalsIgnoreCase(po.getFundType(), "transfer_out")) {//从我的钱包转出外面
                recordDetailApp.setTransferOut(LocaleTool.tranMessage(Module.COMMON, "FundRecord.record.playerWallet"));
                Integer apiId = (Integer) map.get("API");
                recordDetailApp.setTransferInto(CacheBase.getSiteApiName(String.valueOf(apiId)));
            }

            recordDetailApp.setPoundage((Double) map.get("poundage"));  //手续费


            String statusName = LocaleTool.tranMessage(Module.COMMON, "status." + po.getStatus());
            recordDetailApp.setStatusName(statusName);

            if (StringTool.equalsIgnoreCase("deposit", recordDetailApp.getTransactionType())) { //存款
                recordDetailApp.setTransactionWayName(LocaleTool.tranMessage(Module.COMMON, "recharge_type." + recordDetailApp.getTransactionWay()));

            }
            if (StringTool.equalsIgnoreCase("withdrawals", recordDetailApp.getTransactionType())) {//取款

                recordDetailApp.setBankCode((String) map.get("bankCode"));
                String bankName = LocaleTool.tranMessage(Module.COMMON, "bankname." + recordDetailApp.getBankCode());
                recordDetailApp.setBankCodeName(bankName);
                recordDetailApp.setDeductFavorable(withdrawVo.getResult().getDeductFavorable());//扣除优惠
                recordDetailApp.setPoundage(withdrawVo.getResult().getCounterFee());  //手续费
                recordDetailApp.setAdministrativeFee(withdrawVo.getResult().getAdministrativeFee()); //行政费用
                recordDetailApp.setRechargeTotalAmount(withdrawVo.getResult().getWithdrawActualAmount());  //实际到账
            }
            recordDetailApp.setBitAmount((String) map.get("bitAmount"));


            appModelVo = CommonApp.buildAppModelVo(recordDetailApp);
        }

        return JsonTool.toJson(appModelVo);
    }

    /**
     * 获取投注记录列表
     *
     * @param listVo
     * @return
     */
    @RequestMapping("/getBettingList")
    @ResponseBody
    public String getBettingList(PlayerGameOrderListVo listVo) {

        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        BettingDataApp bettingDataApp = new BettingDataApp();
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        if (listVo.getSearch().getEndBetTime() != null) {
            listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1), -1));
        }

        initQueryDateForgetBetting(listVo, TIME_INTERVAL, DEFAULT_TIME);
        listVo = ServiceSiteTool.playerGameOrderService().search(listVo);
        List<PlayerGameOrder> gameOrderList = listVo.getResult();

        bettingDataApp.setTotalSize(listVo.getPaging().getTotalCount());
        bettingDataApp.setStatisticsData(statisticsData(listVo, TIME_INTERVAL, DEFAULT_TIME));
        bettingDataApp.setList(buildBetting(gameOrderList));

        //设置默认时间
        bettingDataApp.setMinDate(SessionManager.getDate().addDays(TIME_INTERVAL));
        bettingDataApp.setMaxDate(SessionManager.getDate().getNow());


        vo = CommonApp.buildAppModelVo(bettingDataApp);
        return JsonTool.toJson(vo);

    }

    /**
     * 初始化跳转站点消息－发送消息－获取类型
     *
     * @return
     */
    @RequestMapping("/goAddNoticeSite")
    @ResponseBody
    public String goAddNoticeSite() {
        AppModelVo vo = new AppModelVo();

        if (!isLoginUser(vo)) {
            return JsonTool.toJson(vo);
        }

        Map<String, SysDict> advisoryType = DictTool.get(DictEnum.ADVISORY_TYPE);
        Iterator<String> iter = advisoryType.keySet().iterator();
        List<AdvisoryType> advisoryTypeList = ListTool.newArrayList();
        while (iter.hasNext()) {
            String key = iter.next();
            AdvisoryType type = new AdvisoryType();
            SysDict dict = advisoryType.get(key);
            dict.getRemark();
            type.setAdvisoryType(key);
            type.setAdvisoryName(dict.getRemark());
            advisoryTypeList.add(type);

        }

        Map<String, Object> map = MapTool.newHashMap();
        map.put("advisoryTypeList", advisoryTypeList);
        map.put("isOpenCaptcha", false);
        if (SessionManager.getSendMessageCount() != null && SessionManager.getSendMessageCount() >= 3) {
            map.put("isOpenCaptcha", true);  //如果次数大于等于三次则页面出现验证码,同时给出验证码url
            map.put("captcha_value", "/captcha/feedback.html");
        }

        vo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(vo);
    }

    /**
     * 获取站点消息－我的消息　所有发送的问题
     *
     * @param listVo
     * @return
     */
    @RequestMapping("/advisoryMessage")
    @ResponseBody
    public String advisoryMessage(VPlayerAdvisoryListVo listVo) {

        if (listVo == null) {
            listVo = new VPlayerAdvisoryListVo();
        }


        //提问内容+未读数量
        Map<String, Object> map = MapTool.newHashMap();
        listVo = this.unReadCount(listVo, map);

        List<VPlayerAdvisory> advisoryList = listVo.getResult();
        List<AdvisoryMessageApp> messageAppList = ListTool.newArrayList();
        for (VPlayerAdvisory advisory : advisoryList) {
            AdvisoryMessageApp messageApp = new AdvisoryMessageApp();
            messageApp.setAdvisoryTitle(advisory.getAdvisoryTitle());
            messageApp.setAdvisoryContent(advisory.getAdvisoryContent());
            String time = LocaleDateTool.formatDate(advisory.getAdvisoryTime(), new DateFormat().getDAY_SECOND(), SessionManagerCommon.getTimeZone());
            messageApp.setAdvisoryTime(time);
            messageApp.setReplyTitle(advisory.getReplyTitle());
            messageApp.setId(advisory.getId());
            messageApp.setRead(advisory.getIsRead() == null ? true : advisory.getIsRead());
            messageAppList.add(messageApp);

        }
        AppModelVo vo = CommonApp.buildAppModelVo(messageAppList);


        return JsonTool.toJson(vo);
    }

    /**
     * 站点消息－我的消息　删除提问
     *
     * @param ids
     * @return
     */
    @RequestMapping("/deleteAdvisoryMessage")
    @ResponseBody
    public String deleteAdvisoryMessage(String ids) {
        String[] id = ids.split(",");
        PlayerAdvisoryVo vo = new PlayerAdvisoryVo();
        for (String messageId : id) {
            PlayerAdvisoryListVo listVo = new PlayerAdvisoryListVo();
            listVo.getSearch().setContinueQuizId(Integer.valueOf(messageId));
            listVo = ServiceSiteTool.playerAdvisoryService().search(listVo);
            for (PlayerAdvisory obj : listVo.getResult()) {
                vo.setSuccess(false);
                vo.setResult(new PlayerAdvisory());
                vo.getResult().setId(obj.getId());
                vo.getResult().setPlayerDelete(true);
                vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
                vo = ServiceSiteTool.playerAdvisoryService().updateOnly(vo);
            }
            vo.setSuccess(false);
            vo.setResult(new PlayerAdvisory());
            vo.getResult().setId(Integer.valueOf(messageId));
            vo.getResult().setPlayerDelete(true);
            vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
            vo = ServiceSiteTool.playerAdvisoryService().updateOnly(vo);
        }
        if (vo.isSuccess()) {
            vo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.DELETE_SUCCESS));
        } else {
            vo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.DELETE_FAILED));
        }
        HashMap map = new HashMap(2, 1f);
        map.put("msg", StringTool.isNotBlank(vo.getOkMsg()) ? vo.getOkMsg() : vo.getErrMsg());
        map.put("state", Boolean.valueOf(vo.isSuccess()));

        AppModelVo appModelVo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(appModelVo);
    }

    /**
     * 站点消息－我的消息　标记已读
     *
     * @param ids
     * @return
     */
    @RequestMapping("/getSelectAdvisoryMessageIds")
    @ResponseBody
    public String getSelectAdvisoryMessageIds(String ids) {
        String[] id = ids.split(",");
        for (String messageId : id) {
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            //判断是否已读
            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});

            ServiceSiteTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(Integer.valueOf(messageId));
                ServiceSiteTool.playerAdvisoryReadService().insert(parVo);
            }
        }

        HashMap map = new HashMap(1, 1f);

        map.put("state", true);
        AppModelVo appModelVo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(appModelVo);
    }

    /**
     * 我的消息，问题详细
     *
     * @param id
     * @return
     */
    @RequestMapping("/advisoryMessageDetail")
    @ResponseBody
    public String advisoryMessageDetail(Integer id) {
        //当前咨询信息
        VPlayerAdvisoryReplyListVo listVo = new VPlayerAdvisoryReplyListVo();
        VPlayerAdvisoryListVo vPlayerAdvisoryListVo = new VPlayerAdvisoryListVo();
        AdvisoryMessageDetailApp detailApp = new AdvisoryMessageDetailApp();
        AppModelVo appModelVo = new AppModelVo();
        listVo.getPaging().setPageSize(60);
        vPlayerAdvisoryListVo.getSearch().setId(id);

        List<VPlayerAdvisory> vPlayerAdvisoryList = ServiceSiteTool.vPlayerAdvisoryService().searchVPlayerAdvisoryReply(vPlayerAdvisoryListVo);

        Map map = new TreeMap(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Integer) o1) - ((Integer) o2);
            }
        });

        for (VPlayerAdvisory obj : vPlayerAdvisoryList) {
            //回复标题和内容
            listVo.getSearch().setPlayerAdvisoryId(obj.getId());
            listVo = ServiceSiteTool.vPlayerAdvisoryReplyService().search(listVo);
            map.put(obj.getId(), listVo);

            //判断是否已读
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(obj.getId());
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});
            ServiceSiteTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(obj.getId());
                ServiceSiteTool.playerAdvisoryReadService().insert(parVo);
            }
        }

        if (vPlayerAdvisoryList.size() == 1) {
            VPlayerAdvisory advisory = vPlayerAdvisoryList.get(0);

            detailApp.setAdvisoryTitle(advisory.getAdvisoryTitle());
            detailApp.setAdvisoryContent(advisory.getAdvisoryContent());
            detailApp.setQuestionType(advisory.getQuestionType());
            detailApp.setAdvisoryTime(LocaleDateTool.formatDate(advisory.getAdvisoryTime(), new DateFormat().getDAY_SECOND(), SessionManagerCommon.getTimeZone()));
            detailApp.setReplyTime(LocaleDateTool.formatDate(advisory.getReplyTime(), new DateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
            detailApp.setReplyTitle(advisory.getReplyTitle());
            detailApp.setReplyContent(advisory.getReplyContent());

        }
        appModelVo = CommonApp.buildAppModelVo(detailApp);

        return JsonTool.toJson(appModelVo);
    }

    /**
     * 申请优惠，保存发送消息
     * @param playerAdvisoryVo
     * @param code
     * @return
     */
    @RequestMapping("/addNoticeSite")
    @ResponseBody
    public String addNoticeSite(PlayerAdvisoryVo playerAdvisoryVo, String code) {
        AppModelVo appModelVo = new AppModelVo();
        appModelVo.setVersion(APP_VERSION);
        if (playerAdvisoryVo != null && playerAdvisoryVo.getResult() != null) {
            playerAdvisoryVo.setSuccess(false);
            playerAdvisoryVo.getResult().setAdvisoryTime(SessionManager.getDate().getNow());
            playerAdvisoryVo.getResult().setPlayerId(SessionManager.getUserId());
            playerAdvisoryVo.getResult().setReplyCount(0);
            playerAdvisoryVo.getResult().setQuestionType(PlayerAdvisoryEnum.QUESTION.getCode());
        }


        HashMap map = new HashMap(4, 1f);
        if (SessionManager.getSendMessageCount() != null && SessionManager.getSendMessageCount() >= 3) {

            map.put("isOpenCaptcha", true);
            map.put("captcha_value", "/captcha/feedback.html");
            if (!StringTool.isNotBlank(code)) {
                appModelVo.setCode(AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getCode());
                appModelVo.setMsg(AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getMsg());
                appModelVo.setError(DEFAULT_TIME);
                return JsonTool.toJson(appModelVo);
            }
            if (!checkFeedCode(code)) {
                appModelVo.setCode(AppErrorCodeEnum.VALIDATE_ERROR.getCode());
                appModelVo.setMsg(AppErrorCodeEnum.VALIDATE_ERROR.getMsg());
                appModelVo.setError(DEFAULT_TIME);
                return JsonTool.toJson(appModelVo);
            }

        }
        playerAdvisoryVo = ServiceSiteTool.playerAdvisoryService().insert(playerAdvisoryVo);

        if (playerAdvisoryVo.isSuccess()) {
            playerAdvisoryVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));
            //发送消息数量
            Integer sendMessageCount = SessionManager.getSendMessageCount() == null ? 0 : SessionManager.getSendMessageCount();
            SessionManager.setsSendMessageCount(sendMessageCount + 1);
            if (SessionManager.getSendMessageCount() >= 3) {
                map.put("isOpenCaptcha", true);
            }
            //生成任务提醒
            UserTaskReminderVo userTaskReminderVo = new UserTaskReminderVo();
            userTaskReminderVo.setTaskEnum(UserTaskEnum.PLAYERCONSULTATION);
            ServiceSiteTool.userTaskReminderService().addTaskReminder(userTaskReminderVo);
        } else {
            playerAdvisoryVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }

        map.put("msg", StringTool.isNotBlank(playerAdvisoryVo.getOkMsg()) ? playerAdvisoryVo.getOkMsg() : playerAdvisoryVo.getErrMsg());
        map.put("state", Boolean.valueOf(playerAdvisoryVo.isSuccess()));
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        appModelVo = CommonApp.buildAppModelVo(map);
        return JsonTool.toJson(appModelVo);
    }


    /**
     * 安全码信息
     *
     * @return
     */
    @RequestMapping("/initSafePassword")
    @ResponseBody
    public String initSafePassword() {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        vo = getSafePassword(vo);
        return JsonTool.toJson(vo);
    }

    private AppModelVo getSafePassword(AppModelVo vo) {
        SysUser user = SessionManager.getUser();
        Map<String, Object> map = MapTool.newHashMap();
        map.put("hasRealName", StringTool.isNotBlank(user.getRealName()));
        if (StringTool.isBlank(user.getPermissionPwd())) {
            map.put("hasPermissionPwd", false);
            vo.setData(map);
            return vo;
        }

        map.put("hasPermissionPwd", true);
        if (isLock(user)) {//如果冻结
            map.put("customer", SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
            map.put("lockTime", formatLockTime(user.getSecpwdFreezeStartTime()));
            vo.setData(map);
            return vo;
        }

        //判断是否出现验证码,大于2显示验证码
        Integer errorTimes = user.getSecpwdErrorTimes();
        errorTimes = errorTimes == null ? 0 : errorTimes;
        map.put("isOpenCaptcha", errorTimes > 1);
        map.put("remindTimes", APP_ERROR_TIMES - errorTimes);
        map.put("captChaUrl", safePasswordUrl);
        vo.setData(map);
        return vo;
    }

    /**
     * 设置真实姓名
     *
     * @param realName
     * @return
     */
    @RequestMapping("/setRealName")
    @ResponseBody
    public String setRealNameApp(String realName) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (StringTool.isBlank(realName)) {
            vo.setCode(AppErrorCodeEnum.REAL_NAME_NOT_NULL.getCode());
            vo.setMsg(AppErrorCodeEnum.REAL_NAME_NOT_NULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }

        if (!setRealName(realName)) {
            vo.setCode(AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getCode());
            vo.setMsg(AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        return JsonTool.toJson(vo);
    }

    /**
     * 修改安全码
     *
     * @param password
     * @return
     */
    @RequestMapping("/updateSafePassword")
    @ResponseBody
    public String updateSafePassword(SecurityPassword password) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        vo = getSafePassword(vo);
        //验证真实姓名
        if (StringTool.isBlank(password.getRealName())) {
            vo.setCode(AppErrorCodeEnum.REAL_NAME_NOT_NULL.getCode());
            vo.setMsg(AppErrorCodeEnum.REAL_NAME_NOT_NULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        //验证密码
        if (StringTool.isBlank(password.getPwd1())) {
            vo.setCode(AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getCode());
            vo.setMsg(AppErrorCodeEnum.SAFE_PASSWORD_NOT_NULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (verifyCode(password)) {
            vo.setCode(AppErrorCodeEnum.VALIDATE_ERROR.getCode());
            vo.setMsg(AppErrorCodeEnum.VALIDATE_ERROR.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (!verifyRealName(password)) {
            vo.setCode(AppErrorCodeEnum.REAL_NAME_ERROR.getCode());
            vo.setMsg(AppErrorCodeEnum.REAL_NAME_ERROR.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (!verifyOriginPwd(password)) {
            Map<String, Object> map = MapTool.newHashMap();
            SysUser user = SessionManager.getUser();
            Integer errorTimes = user.getSecpwdErrorTimes() == null ? 0 : user.getSecpwdErrorTimes();
            setErrorTimes(map, user, errorTimes);
            vo.setCode(AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getCode());
            vo.setMsg(AppErrorCodeEnum.ORIGIN_SAFE_PASSWORD_ERROR.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (!setRealName(password.getRealName())) {
            vo.setCode(AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getCode());
            vo.setMsg(AppErrorCodeEnum.UPDATE_REAL_NAME_FAIL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        boolean isSUCCESS = savePassword(password.getPwd1());
        if (isSUCCESS) {
            SessionManager.clearPrivilegeStatus();
        }
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());

        return JsonTool.toJson(vo);
    }

    /**
     * 修改登录密码
     *
     * @param updatePasswordVo
     * @param code
     * @return
     */
    @RequestMapping("/updateLoginPassword")
    @ResponseBody
    public String updateLoginPassword(UpdatePasswordVo updatePasswordVo, String code) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        if (!isLoginUser(vo)) {
            vo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            vo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (StringTool.isBlank(updatePasswordVo.getPassword())) {
            vo.setCode(AppErrorCodeEnum.PASSWORD_NOT_NULL.getCode());
            vo.setMsg(AppErrorCodeEnum.PASSWORD_NOT_NULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        if (StringTool.isBlank(updatePasswordVo.getNewPassword())) {
            vo.setCode(AppErrorCodeEnum.NEW_PASSWORD_NOT_NULL.getCode());
            vo.setMsg(AppErrorCodeEnum.NEW_PASSWORD_NOT_NULL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        //密码相同验证新密码不能和旧密码一样
        String newPwd = AuthTool.md5SysUserPassword(updatePasswordVo.getNewPassword(), SessionManager.getUserName());
        if (StringTool.equalsIgnoreCase(newPwd, SessionManager.getUser().getPassword())) {
            vo.setCode(AppErrorCodeEnum.PASSWORD_SAME.getCode());
            vo.setMsg(AppErrorCodeEnum.PASSWORD_SAME.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }
        SysUser curUser = SessionManagerCommon.getUser();
        int errorTimes = curUser.getLoginErrorTimes() == null ? -1 : curUser.getLoginErrorTimes();
        if (errorTimes >= TWO) {
            if (StringTool.isBlank(code)) {
                vo.setCode(AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getCode());
                vo.setMsg(AppErrorCodeEnum.SYSTEM_VALIDATE_NOT_NULL.getMsg());
                vo.setError(DEFAULT_TIME);
                return JsonTool.toJson(vo);
            }
            if (!checkCode(code)) {
                vo.setCode(AppErrorCodeEnum.VALIDATE_ERROR.getCode());
                vo.setMsg(AppErrorCodeEnum.VALIDATE_ERROR.getMsg());
                vo.setError(DEFAULT_TIME);
                return JsonTool.toJson(vo);
            }
        }
        //验证旧密码
        String oldPwd = AuthTool.md5SysUserPassword(updatePasswordVo.getPassword(), SessionManager.getUserName());
        if (!StringTool.equalsIgnoreCase(oldPwd, SessionManager.getUser().getPassword())) {
            Map map = setPwdErrorTimes(errorTimes);
            vo.setCode(AppErrorCodeEnum.PASSWORD_ERROR.getCode());
            vo.setMsg(AppErrorCodeEnum.PASSWORD_ERROR.getMsg());
            vo.setError(DEFAULT_TIME);
            vo.setData(map);
            return JsonTool.toJson(vo);
        }

        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = new SysUser();
        sysUser.setId(SessionManager.getUserId());
        sysUser.setPassword(newPwd);
        sysUser.setPasswordLevel(updatePasswordVo.getPasswordLevel());
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_PASSWORD, SysUser.PROP_PASSWORD_LEVEL);
        boolean success = ServiceTool.sysUserService().updateOnly(sysUserVo).isSuccess();
        if (!success) {
            vo.setCode(AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getCode());
            vo.setMsg(AppErrorCodeEnum.UPDATE_PASSWORD_FAIL.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }

        SessionManager.refreshUser();
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        return JsonTool.toJson(vo);
    }

    /**
     * 系统公告
     *
     * @return
     */
    @RequestMapping("/getSysNotice")
    @ResponseBody
    public String getSysNotice(VSystemAnnouncementListVo vListVo) {
        AppModelVo vo = new AppModelVo();

        Map map = getSystemNotice(vListVo);
        vo.setVersion(APP_VERSION);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 系统公告详情
     *
     * @return
     */
    @RequestMapping("/getSysNoticeDetail")
    @ResponseBody
    public String getSysNoticeDetail(VSystemAnnouncementListVo vListVo) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        if (vListVo.getSearch().getId() == null) {
            vo.setCode(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode());
            vo.setMsg(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }

        AppSystemNotice sysNotice = getSystemNoticeDetail(vListVo);
        vo.setData(sysNotice);
        return JsonTool.toJson(vo);
    }

    /**
     * 游戏公告
     *
     * @return
     */
    @RequestMapping("/getGameNotice")
    @ResponseBody
    public String getGameNotice(VSystemAnnouncementListVo listVo) {
        AppModelVo vo = new AppModelVo();

        Map map = getAppGameNotice(listVo);
        vo.setVersion(APP_VERSION);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 游戏公告详情
     *
     * @return
     */
    @RequestMapping("/getGameNoticeDetail")
    @ResponseBody
    public String getGameNoticeDetail(VSystemAnnouncementListVo listVo) {
        AppModelVo vo = new AppModelVo();

        if (listVo.getSearch().getId() == null) {
            vo.setCode(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode());
            vo.setError(DEFAULT_TIME);
            vo.setMsg(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg());
            return JsonTool.toJson(vo);
        }

        AppGameNotice gameNotice = getAppGameNoticeDetail(listVo);
        vo.setVersion(APP_VERSION);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(gameNotice);

        return JsonTool.toJson(vo);
    }

    /**
     * 站点消息-->系统消息
     *
     * @return
     */
    @RequestMapping("/getSiteSysNotice")
    @ResponseBody
    public String getSiteSysNotice(VNoticeReceivedTextListVo listVo) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        Map map = getAppSiteSysNotice(listVo);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 站点消息-->系统消息 -->标记已读
     *
     * @return
     */
    @RequestMapping("/setSiteSysNoticeStatus")
    @ResponseBody
    public String setSiteSysNoticeStatus(NoticeReceiveVo noticeReceiveVo, String ids) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (StringTool.isBlank(ids)) {
            vo.setCode(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode());
            vo.setMsg(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg());
            vo.setError(DEFAULT_TIME);
            return JsonTool.toJson(vo);
        }

        String[] idArray = ids.split(SPLIT_REGEX);
        List<Integer> list = ListTool.newArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        noticeReceiveVo.setIds(list);

        boolean b = ServiceTool.noticeService().markSiteMsg(noticeReceiveVo);
        if (!b) {
            vo.setError(DEFAULT_TIME);
            vo.setMsg(AppErrorCodeEnum.UPDATE_STATUS_FAIL.getMsg());
            vo.setCode(AppErrorCodeEnum.UPDATE_STATUS_FAIL.getCode());
            return JsonTool.toJson(vo);
        }

        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        return JsonTool.toJson(vo);
    }

    /**
     * 删除系统信息
     *
     * @param noticeVo
     * @param ids
     * @return
     */
    @RequestMapping("/deleteSiteSysNotice")
    @ResponseBody
    public String deleteSiteSysNotice(NoticeReceiveVo noticeVo, String ids) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (StringTool.isBlank(ids)) {
            vo.setCode(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode());
            vo.setError(DEFAULT_TIME);
            vo.setMsg(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg());
            return JsonTool.toJson(vo);
        }

        String[] idArray = ids.split(SPLIT_REGEX);
        List<Integer> list = new ArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        noticeVo.setIds(list);
        boolean bool = ServiceTool.noticeService().deleteSiteMsg(noticeVo);
        if (!bool) {
            vo.setError(DEFAULT_TIME);
            vo.setCode(AppErrorCodeEnum.UPDATE_STATUS_FAIL.getCode());
            vo.setMsg(AppErrorCodeEnum.UPDATE_STATUS_FAIL.getMsg());
            return JsonTool.toJson(vo);
        }

        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());

        return JsonTool.toJson(vo);
    }

    /**
     * 站点消息-->系统消息详情
     *
     * @return
     */
    @RequestMapping("/getSiteSysNoticeDetail")
    @ResponseBody
    public String getSiteSysNoticeDetail(VNoticeReceivedTextVo vReceivedVo, NoticeReceiveVo noticeReceiveVo, HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (noticeReceiveVo.getSearch().getId() == null) {
            vo.setError(DEFAULT_TIME);
            vo.setCode(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getCode());
            vo.setMsg(AppErrorCodeEnum.SYSTEM_INFO_NOT_EXIST.getMsg());
            return JsonTool.toJson(vo);
        }

        AppSystemNotice sysNotice = getAppSiteNoticeDetail(vReceivedVo, noticeReceiveVo, request);
        vo.setData(sysNotice);
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());

        return JsonTool.toJson(vo);
    }

    /**
     * 获取站点中心未读条数
     *
     * @return
     */
    @RequestMapping("/getUnReadCount")
    @ResponseBody
    public String getUnReadCount(VPlayerAdvisoryListVo listVo) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        Map map = unReadCount(listVo);
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(map);

        return JsonTool.toJson(vo);
    }

    /**
     * 一键回收
     *
     * @return
     */
    @RequestMapping("/recovery")
    @ResponseBody
    public String recovery(HttpServletRequest request) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);
        if (!SessionManagerCommon.isAutoPay()) {
            vo.setError(DEFAULT_TIME);
            vo.setMsg(AppErrorCodeEnum.NOT_RECOVER.getMsg());
            vo.setCode(AppErrorCodeEnum.NOT_RECOVER.getCode());
            return JsonTool.toJson(vo);
        }
        Map map = appRecovery();
        if (map.get("isSUCCESS") == null && MapTool.getBoolean(map, "isSUCCESS") == false) {
            vo.setError(DEFAULT_TIME);
            vo.setCode(AppErrorCodeEnum.UPDATE_STATUS_FAIL.getCode());
            vo.setMsg(map.get("msg") != null ? map.get("msg").toString() : AppErrorCodeEnum.UPDATE_STATUS_FAIL.getMsg());
            return JsonTool.toJson(vo);
        }

        vo.setData(appRefresh(request));
        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        return JsonTool.toJson(vo);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        AppModelVo vo = new AppModelVo();
        vo.setVersion(APP_VERSION);

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        Integer entranceId = passportDelegate.getEntranceType(contextPath, uri);
        SessionManagerCommon.setAttribute(SessionKey.S_ENTRANCE, String.valueOf(entranceId));
        try {
            passportDelegate.onLogoutDelegate(request, response);
            SessionManagerCommon.clearSession();
        } catch (SessionException ise) {
            LOG.debug("Encountered session exception during logout.  This can generally safely be ignored.", ise);
        }

        vo.setCode(AppErrorCodeEnum.SUCCESS.getCode());
        vo.setMsg(AppErrorCodeEnum.SUCCESS.getMsg());
        vo.setData(PassportResult.SUCCESS);
        return JsonTool.toJson(vo);
    }

    @Autowired(required = false)
    private IPassportDelegate passportDelegate;

    /**
     * 验证吗remote验证
     *
     * @param code
     * @return
     */
    @RequestMapping("/checkCode")
    @ResponseBody
    public boolean checkCode(@RequestParam("code") String code) {
        String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_LOGIN.getSuffix());
        return StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code);
    }

    /**
     * 验证吗remote验证
     *
     * 我的消息  保存申请验证吗remote验证
     * @param code
     * @return
     */
    @RequestMapping("/checkFeedCode")
    @ResponseBody
    public boolean checkFeedCode(@RequestParam("code") String code) {
        String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix());
        return StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code);
    }

    private Map setPwdErrorTimes(int errorTimes) {
        Map map = MapTool.newHashMap();
        errorTimes += DEFAULT_TIME;
        Date now = DateQuickPicker.getInstance().getNow();
        if (errorTimes == RECOMMEND_DAYS) {
            errorTimes = ZERO;
        }

        map.put("isOpenCaptcha", errorTimes >= TWO);
        if (errorTimes <= APP_ERROR_TIMES) {
            map.put("remainTimes", APP_ERROR_TIMES - errorTimes);
            updateSysUserErrorTimes(errorTimes, now, null);
        } else if (errorTimes >= APP_ERROR_TIMES) {
            map.put("remainTimes", errorTimes);
            updateSysUserErrorTimes(errorTimes, now, DateTool.addHours(now, 3));
            KickoutFilter.loginKickoutAll(SessionManager.getUserId(), OpMode.AUTO, "移动修改密码错误踢出用户");
        }

        return map;
    }

    private void setErrorTimes(Map<String, Object> map, SysUser user, Integer errorTimes) {
        errorTimes += 1;
        user.setSecpwdErrorTimes(errorTimes);
        if (errorTimes == 1) {
            this.updateErrorTimes(user);
        } else if (errorTimes > 1 && errorTimes < 5) {
            map.put(KEY_CAPTCHA, true);
            map.put(KEY_TIMES_KEY, APP_ERROR_TIMES - errorTimes);
            this.updateErrorTimes(user);
        } else if (errorTimes >= APP_ERROR_TIMES) {
            initPwdLock(map, SessionManager.getDate().getNow());
            this.setSecPwdFreezeTime(user);
            freezeAccountBalance();
        }
    }

    /**
     * 更新冻结开始,结束,错误次数
     *
     * @param times
     * @param startTime
     * @param endTime
     */
    private void updateSysUserErrorTimes(int times, Date startTime, Date endTime) {
        SysUserVo sysUserVo = new SysUserVo();
        SysUser sysUser = SessionManagerCommon.getUser();
        sysUser.setFreezeStartTime(startTime);
        sysUser.setLoginErrorTimes(times);
        sysUser.setFreezeEndTime(endTime);
        sysUserVo.setResult(sysUser);
        sysUserVo.setProperties(SysUser.PROP_FREEZE_START_TIME, SysUser.PROP_FREEZE_END_TIME, SysUser.PROP_LOGIN_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(sysUserVo);
        SessionManagerCommon.refreshUser();
    }

    //错误次数,错误次数达到直接踢出
    private int getErrorTimes() {
        SysUser curUser = SessionManagerCommon.getUser();
        if (curUser != null) {
            return curUser.getLoginErrorTimes() == null ? -1 : curUser.getLoginErrorTimes();
        }
        //判断不出来默认需要权限
        return 1;
    }

    /**
     * 存储新密码
     */
    private boolean savePassword(@RequestParam("pwd") String password) {
        SysUserVo vo = new SysUserVo();
        SysUser user = SessionManager.getUser();
        user.setPermissionPwd(AuthTool.md5SysUserPermission(password, user.getUsername()));
        user.setSecpwdErrorTimes(0);
        user.setSecpwdFreezeEndTime(new Date());

        vo.setResult(user);
        vo.setProperties(SysUser.PROP_PERMISSION_PWD, SysUser.PROP_SECPWD_ERROR_TIMES, SysUser.PROP_SECPWD_FREEZE_END_TIME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        if (SessionManager.isCurrentSiteMaster()) {
            vo._setDataSourceId(SessionManager.getSiteParentId());
            vo.getResult().setId(SessionManager.getSiteUserId());
            vo = ServiceTool.sysUserService().updateOnly(vo);
        }

        // 改变session中user的权限密码
        if (vo.isSuccess()) {
            SessionManager.setUser(user);

            Map<String, Object> map = new HashMap<>();
            securityPasswordCorrect(map, user);
            SessionManager.setPrivilegeStatus(map);
        }
        return vo.isSuccess();
    }

    /**
     * 安全密码正确
     */
    private void securityPasswordCorrect(Map<String, Object> map, SysUser user) {
        map.put(KEY_STATE_KEY, PrivilegeStatusEnum.CODE_100.getCode());
        map.put(KEY_TIMES_KEY, APP_ERROR_TIMES);
        map.put(KEY_FORCE_START, SessionManager.getDate().getNow().getTime());
        map.put(KEY_CAPTCHA, false);

        user.setSecpwdErrorTimes(0);
        updateErrorTimes(user);
        resetBalanceFreeze(user);
    }

    private void resetBalanceFreeze(SysUser sysUser) {
        if (sysUser == null || sysUser.getId() == null) {
            return;
        }
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(sysUser.getId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        UserPlayer player = userPlayerVo.getResult();
        if (player != null) {
            Date now = DateQuickPicker.getInstance().getNow();
            //自冻冻结且还在冻结区间才解冻
            if (FreezeType.AUTO.getCode().equals(player.getBalanceType()) && player.getBalanceFreezeEndTime() != null
                    && now.before(player.getBalanceFreezeEndTime())) {
                player.setBalanceFreezeEndTime(new Date());
                userPlayerVo.setResult(player);
                userPlayerVo.setProperties(UserPlayer.PROP_BALANCE_FREEZE_END_TIME);
                ServiceSiteTool.userPlayerService().updateOnly(userPlayerVo);
            }
        }
    }

    private boolean setRealName(String realName) {
        SysUser user = SessionManager.getUser();
        user.setRealName(realName);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_REAL_NAME);
        vo = ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
        return vo.isSuccess();
    }

    //玩家冻结账户余额
    private void freezeAccountBalance() {
        AccountVo accountVo = new AccountVo();
        SysUser user = SessionManagerCommon.getUser();
        accountVo.setResult(user);
        accountVo.setChooseFreezeTime(FreezeTime.THREE.getCode());
        UserPlayer userPlayer = ServiceSiteTool.userPlayerService().freezeAccountBalance(accountVo);
        sendNotice(user, userPlayer);
    }

    private void sendNotice(SysUser user, UserPlayer userPlayer) {
        try {
            Locale locale = LocaleTool.getLocale(user.getDefaultLocale());
            TimeZone timeZone = TimeZone.getTimeZone(user.getDefaultTimezone());
            NoticeVo noticeVo = NoticeVo.autoNotify(AutoNoticeEvent.BALANCE_AUTO_FREEZON, user.getId());
            noticeVo.addParams(
                    new Pair(NoticeParamEnum.UN_FREEZE_TIME.getCode(),
                            DateTool.formatDate(userPlayer.getBalanceFreezeEndTime(),
                                    locale, timeZone, CommonContext.getDateFormat().getDAY_SECOND())),
                    new Pair(NoticeParamEnum.USER.getCode(), user.getUsername()));
            ServiceTool.noticeService().publish(noticeVo);
            LOG.debug("余额自动冻结发送站内信成功");
        } catch (Exception ex) {
            LOG.error(ex, "安全码输入错误次数超过5次，余额自动冻结时发送站内信失败");
        }

    }

    /**
     * 设定安全密码冻结时间
     */
    private void setSecPwdFreezeTime(SysUser user) {
        Date date = SessionManager.getDate().getNow();
        user.setSecpwdFreezeStartTime(date);
        user.setSecpwdFreezeEndTime(DateTool.addHours(date, 3));
        user.setSecpwdErrorTimes(5);
        SessionManager.setUser(user);

        SysUserVo vo = new SysUserVo();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_SECPWD_FREEZE_START_TIME, SysUser.PROP_SECPWD_FREEZE_END_TIME, SysUser.PROP_SECPWD_ERROR_TIMES);
        ServiceTool.sysUserService().updateOnly(vo);
    }

    private void updateErrorTimes(SysUser user) {
        SysUserVo vo = new SysUserVo();
        vo.setProperties(SysUser.PROP_SECPWD_ERROR_TIMES);
        vo.setResult(user);
        ServiceTool.sysUserService().updateOnly(vo);

        SessionManager.setUser(user);
    }

    /**
     * 密码锁定时的提示内容
     */
    private void initPwdLock(Map<String, Object> map, Date date) {
        map.put(KEY_STATE_KEY, PrivilegeStatusEnum.CODE_99.getCode());
        map.put(KEY_TIMES_KEY, 0);
        map.put(KEY_FORCE_START, formatLockTime(date));
        map.put(CUSTOMER_SERVICE_KEY, SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
    }

    /**
     * 验证原密码
     */
    private boolean verifyOriginPwd(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if (StringTool.isBlank(user.getPermissionPwd())) {
            return true;
        }
        return StringTool.equals(AuthTool.md5SysUserPermission(password.getOriginPwd(), user.getUsername()), user.getPermissionPwd());
    }

    /**
     * 验证真实姓名
     */
    private boolean verifyRealName(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        if (StringTool.isBlank(user.getRealName())) {
            return true;
        }
        return StringTool.equals(password.getRealName(), user.getRealName());
    }

    /**
     * 验证验证码
     */
    private boolean verifyCode(SecurityPassword password) {
        SysUser user = SessionManager.getUser();
        Integer errorTimes = user.getSecpwdErrorTimes();
        errorTimes = errorTimes == null ? 0 : errorTimes;
        if (errorTimes > 1) {
            String sysCode = (String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_SECURITY_PASSWORD.getSuffix());
            return !StringTool.equalsIgnoreCase(password.getCode(), sysCode);
        }
        return false;
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
     *
     * @return
     */
    private List<AppMineLinkVo> setLink() {
        List<AppMineLinkVo> links = ListTool.newArrayList();
        for (AppMineLinkEnum linkEnum : AppMineLinkEnum.values()) {
            AppMineLinkVo vo = new AppMineLinkVo();
            vo.setCode(linkEnum.getCode());
            vo.setName(linkEnum.getName());
            vo.setLink(linkEnum.getLink());
            links.add(vo);
        }

        return links;
    }

    /**
     * 是否有登陆账号
     */
    public boolean isLoginUser(AppModelVo appVo) {
        if (SessionManager.getUser() == null) {
            appVo.setMsg(AppErrorCodeEnum.UN_LOGIN.getMsg());
            appVo.setCode(AppErrorCodeEnum.UN_LOGIN.getCode());
            appVo.setError(1);
            appVo.setData(null);

            return false;
        }
        return true;
    }

    private VPlayerAdvisoryListVo unReadCount(VPlayerAdvisoryListVo listVo, Map map) {
        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long length = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        map.put("length", length);
        //玩家咨询-未读数量
        listVo.setSearch(null);
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceSiteTool.vPlayerAdvisoryService().search(listVo);
        Integer advisoryUnReadCount = 0;
        String tag = "";
        //所有咨询数据
        for (VPlayerAdvisory obj : listVo.getResult()) {
            //查询回复表每一条在已读表是否存在
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);
            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
                readVo.setResult(new PlayerAdvisoryRead());
                readVo.getSearch().setUserId(SessionManager.getUserId());
                readVo.getSearch().setPlayerAdvisoryReplyId(replay.getId());
                readVo = ServiceSiteTool.playerAdvisoryReadService().search(readVo);
                //不存在未读+1，标记已读咨询Id
                if (readVo.getResult() == null && !tag.contains(replay.getPlayerAdvisoryId().toString())) {
                    advisoryUnReadCount++;
                    tag += replay.getPlayerAdvisoryId().toString() + ",";
                }
            }
        }
        //判断已标记的咨询Id除外的未读咨询id,添加未读标记isRead=false;
        String[] tags = tag.split(",");
        for (VPlayerAdvisory vo : listVo.getResult()) {
            for (int i = 0; i < tags.length; i++) {
                if (tags[i] != "") {
                    VPlayerAdvisoryVo pa = new VPlayerAdvisoryVo();
                    pa.getSearch().setId(Integer.valueOf(tags[i]));
                    VPlayerAdvisoryVo vpaVo = ServiceSiteTool.vPlayerAdvisoryService().get(pa);
                    if (vo.getId().equals(vpaVo.getResult().getContinueQuizId()) || vo.getId().equals(vpaVo.getResult().getId())) {
                        vo.setIsRead(false);
                    } else {
                        vo.setIsRead(true);
                    }
                }
            }
        }
        Long sysMessageUnReadCount = null;
        advisoryUnReadCount = 0;
        sysMessageUnReadCount = length;
        map.put("sysMessageUnReadCount", sysMessageUnReadCount);
        map.put("advisoryUnReadCount", advisoryUnReadCount);
        return listVo;
    }
}

