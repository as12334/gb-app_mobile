package so.wwb.gamebox.mobile.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateFormat;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.security.Base36;
import org.soul.model.msg.notice.po.VNoticeReceivedText;
import org.soul.model.msg.notice.vo.NoticeReceiveVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.po.SysUserStatus;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.iservice.company.sys.ISysDomainService;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.DomainPageUrlEnum;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.enums.ResolveStatusEnum;
import so.wwb.gamebox.model.company.operator.po.VSystemAnnouncement;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.GameI18n;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.company.site.po.SiteGameI18n;
import so.wwb.gamebox.model.company.sys.vo.SysDomainVo;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.enums.AnnouncementTypeEnum;
import so.wwb.gamebox.model.master.enums.CommonStatusEnum;
import so.wwb.gamebox.model.master.enums.DepositWayEnum;
import so.wwb.gamebox.model.master.fund.enums.FundTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.fund.vo.VPlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.player.enums.PlayerAdvisoryEnum;
import so.wwb.gamebox.model.master.player.po.PlayerAdvisoryReply;
import so.wwb.gamebox.model.master.player.po.PlayerGameOrder;
import so.wwb.gamebox.model.master.player.po.VPlayerAdvisory;
import so.wwb.gamebox.model.master.player.so.PlayerGameOrderSo;
import so.wwb.gamebox.model.master.player.so.VPlayerAdvisorySo;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.so.VPlayerTransactionSo;
import so.wwb.gamebox.model.master.report.vo.*;
import so.wwb.gamebox.model.master.setting.po.GradientTemp;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.SupportLocale;
import so.wwb.gamebox.web.init.ConfigBase;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;
import static so.wwb.gamebox.model.CacheBase.getApiI18n;
import static so.wwb.gamebox.model.CacheBase.getSiteApiI18n;

/**
 * Created by ed on 17-12-31.
 */
public class BaseMineController extends BaseMobileApiController {
    private static final String SYSTEM_NOTICE_LINK = "/mobile-api/mineOrigin/getSysNoticeDetail.html";
    private static final String GAME_NOTICE_LINK = "/mobile-api/mineOrigin/getGameNoticeDetail.html";
    private static final String SITE_SYSTEM_NOTICE = "/mobile-api/mineOrigin/getSiteSysNoticeDetail.html";
    private Log LOG = LogFactory.getLog(BaseMineController.class);
    private IPlayerTransferService playerTransferService;
    private static final int DEFAULT_MIN_TIME = -6;

    /**
     * 组装数据 我的优惠
     *
     * @param recodeList
     * @return
     */
    protected List<MyPromoApp> buildingMyPromoApp(List<VPreferentialRecode> recodeList) {
        Integer userId = SessionManager.getUser().getId();
        List<MyPromoApp> myPromoApps = ListTool.newArrayList();
        for (VPreferentialRecode recode : recodeList) {
            MyPromoApp promoApp = new MyPromoApp();
            promoApp.setId(recode.getId());
            promoApp.setApplyTime(recode.getApplyTime());
            if (recode.getPreferentialAudit() != null && recode.getPreferentialAudit() != 0) {
                promoApp.setPreferentialAuditName("倍稽核");  // 倍稽核
            } else {
                promoApp.setPreferentialAuditName("免稽核");
            }
            promoApp.setPreferentialAudit(recode.getPreferentialAudit());
            promoApp.setActivityName(recode.getActivityName());
            if (StringTool.isBlank(recode.getActivityName())) { //如果名称为空那么就是 人工存取优惠 展示“系统优惠”
                promoApp.setActivityName("系统优惠");
            }
            promoApp.setPreferentialValue(recode.getPreferentialValue());
            promoApp.setUserId(userId);
            promoApp.setCheckState(recode.getCheckState());
            String checkState = recode.getCheckState();

            if (StringTool.equalsIgnoreCase(CommonStatusEnum.SUCCESS.getCode(), checkState) || StringTool.equals(ActivityApplyCheckStatusEnum.SUCCESS.getCode(), checkState)
                    || StringTool.equals(ActivityApplyCheckStatusEnum.NOT_REACHED.getCode(), checkState)) {
                promoApp.setCheckStateName("已发放");
            } else if (StringTool.equals(ActivityApplyCheckStatusEnum.PENDING.getCode(), checkState)) {
                promoApp.setCheckStateName("待审核");
            } else if (StringTool.equals(ActivityApplyCheckStatusEnum.TO_BE_CONFIRM.getCode(), checkState)) {
                promoApp.setCheckStateName("进行中");
            } else {
                promoApp.setCheckStateName("未通过");
            }
            promoApp.setCheckState(recode.getCheckState());
            myPromoApps.add(promoApp);
        }
        return myPromoApps;
    }

    protected Map getSumPlayerFunds(VPlayerTransactionListVo listVo) {
        if (listVo != null) {
            PlayerTransactionListVo transactionListVo = new PlayerTransactionListVo();
            transactionListVo.getSearch().setPlayerId(listVo.getSearch().getPlayerId());
            transactionListVo.getSearch().setBeginCreateTime(listVo.getSearch().getBeginCreateTime());
            transactionListVo.getSearch().setEndCreateTime(listVo.getSearch().getEndCreateTime());
//            transactionListVo.getSearch().setTransactionType(listVo.getSearch().getTransactionType());
            transactionListVo.getSearch().setStates(new String[]{CommonStatusEnum.SUCCESS.getCode(), CommonStatusEnum.LSSUING.getCode()});

            return ServiceSiteTool.playerTransactionService().sumPlayerFunds(transactionListVo);
        }

        return null;
    }

    /**
     * 一键回收
     *
     * @return
     */
    protected Map appRecovery(PlayerApiVo playerApiVo, HttpServletRequest request) {
        playerApiVo.setOrigin(SessionManagerCommon.getTerminal(request));
        Map map = doRecovery(playerApiVo);
        return map;
    }

    /**
     * 回收资金需赋值条件
     *
     * @param playerApiVo
     * @return
     */
    public Map doRecovery(PlayerApiVo playerApiVo) {
        Integer apiId = playerApiVo.getSearch().getApiId();
        //是否允许回收资金
        Map map = isAllowRecovery(apiId);
        if (MapTool.isNotEmpty(map) && !MapTool.getBooleanValue(map, "isSuccess")) {
            return map;
        }
        SysUser user = SessionManagerBase.getUser();
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo._setSiteId(playerApiVo._getSiteId());
        sysUserVo.setResult(user);
        //回收单个玩家所有api
        if (apiId == null) {
            SessionManagerCommon.setUserRecoveryAllApiTime(new Date());
            ServiceSiteTool.freeTranferServcice().transferBackByTransRecord(sysUserVo, playerApiVo.getOrigin());
        } else { //回收单个玩家单个api
            SessionManagerCommon.setUserRecoveryApiTime(new Date());
            return ServiceSiteTool.freeTranferServcice().recoverMoney(sysUserVo, apiId, playerApiVo.getOrigin());
        }
        return getMsg(true, null, null);
    }

    /**
     * 获取消息提示
     *
     * @param isSuccess
     * @param msgConst
     * @return
     */
    protected Map<String, Object> getMsg(boolean isSuccess, String msgConst, String code) {
        HashMap<String, Object> map = new HashMap(2, 1f);
        map.put("isSuccess", isSuccess);
        if (StringTool.isNotBlank(msgConst) && StringTool.isNotBlank(code)) {
            map.put("msg", LocaleTool.tranMessage(code, msgConst));
        }
        return map;
    }

    private Map<String, Object> isAllowRecovery(Integer apiId) {
        if (!SessionManagerCommon.isAutoPay()) {
            return getMsg(false, MessageI18nConst.IS_NOT_AUTO_PAY, Module.FUND_TRANSFER.getCode());
        }
        //回收时间间隔是否符合
        if (!isAllowRecoveryTimeInterval(apiId)) {
            return getMsg(false, MessageI18nConst.RECOVERY_TIME_FREQUENTLY, Module.FUND_TRANSFER.getCode());
        }
        //api状态回收是否符合
        if (apiId != null && !isAllowRecoveryApiStatus(apiId)) {
            return getMsg(false, MessageI18nConst.RECOVERY_API_MAINTAIN, Module.FUND_TRANSFER.getCode());
        }
        //模拟账号且是自主api可用,其他试玩模式下不支持转账
        if (SessionManagerCommon.getDemoModelEnum() != null) {
            if (DemoModelEnum.MODEL_4_MOCK_ACCOUNT.equals(SessionManagerCommon.getDemoModelEnum()) && (
                    apiId.equals(Integer.valueOf(ApiProviderEnum.PL.getCode())) ||
                            apiId.equals(Integer.valueOf(ApiProviderEnum.DWT.getCode())))) {
            } else {
                return getMsg(false, MessageI18nConst.RECOVERY_DEMO_UNSUPPORTED, Module.FUND_TRANSFER.getCode());
            }
        }
        return null;
    }

    /**
     * 是否允许回收
     *
     * @param apiId
     * @return
     */
    public boolean isAllowRecoveryApiStatus(Integer apiId) {
        if (apiId == null)
            return false;
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Api api = apiMap.get(apiId.toString());
        SiteApi siteApi = siteApiMap.get(apiId.toString());
        if (api == null || siteApi == null) {
            return false;
        }
        if (GameStatusEnum.MAINTAIN.getCode().equals(api.getSystemStatus()) || GameStatusEnum.MAINTAIN.getCode().equals(siteApi.getSystemStatus()))
            return false;
        return true;
    }


    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 获取货币标志
     *
     * @param currency
     * @return
     */
    protected String getCurrencySign(String currency) {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(SessionManagerCommon.getUser().getDefaultCurrency());
        if (sysCurrency != null && StringTool.isNotBlank(sysCurrency.getCurrencySign())) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 设置app投注记录默认查询时间
     *
     * @param playerGameOrderListVo
     */
    protected void initQueryDateForgetBetting(PlayerGameOrderListVo playerGameOrderListVo) {
        Date minDate = SessionManager.getDate().addDays(TIME_INTERVAL);
        playerGameOrderListVo.setMinDate(minDate);
        PlayerGameOrderSo playerGameOrderSo = playerGameOrderListVo.getSearch();
        if (playerGameOrderSo.getBeginBetTime() == null && playerGameOrderSo.getEndBetTime() == null) {
            playerGameOrderSo.setBeginBetTime(SessionManager.getDate().getYestoday());//默认查询昨天至今天
        } else if (playerGameOrderSo.getBeginBetTime() != null && playerGameOrderSo.getBeginBetTime().getTime() < minDate.getTime()) {//开始时间不能小于最小时间
            playerGameOrderSo.setBeginBetTime(minDate);
        }
        if (playerGameOrderSo.getEndBetTime() == null || playerGameOrderSo.getBeginBetTime().after(playerGameOrderSo.getEndBetTime())) {
            playerGameOrderSo.setEndBetTime(SessionManager.getDate().getTomorrow());
        }
        playerGameOrderListVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(playerGameOrderListVo.getSearch().getEndBetTime(), 1), -1));
        if (playerGameOrderListVo.getSearch().getBeginBetTime().getTime() == playerGameOrderListVo.getSearch().getEndBetTime().getTime()) { //如果两个时间一样，用户要查一天之内的数据
            playerGameOrderListVo.getSearch().setEndBetTime(DateTool.addDays(playerGameOrderListVo.getSearch().getEndBetTime(), 1));
        }
    }

    /**
     * 统计当前页数据
     */
    protected Map<String, Object> statisticsData(PlayerGameOrderListVo listVo) {
        // 统计数据
        Map map = ServiceSiteTool.playerGameOrderService().queryTotalPayoutAndEffect(listVo);
        map.put("currency", getCurrencySign());
        return map;
    }

    protected List<BettingInfoApp> buildBetting(List<PlayerGameOrder> list) {
        List<BettingInfoApp> bettingInfoAppList = new ArrayList<>();
        Map<String, SiteGameI18n> siteGameI18nMap = Cache.getSiteGameI18n();
        Map<String, GameI18n> gameI18nMap = Cache.getGameI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = getSiteApiI18n();
        Map<String, ApiI18n> apiI18nMap = getApiI18n();
        String url = "/fund/betting/gameRecordDetail.html?searchId=%s";
        PlayerGameOrderVo playerGameOrderVo = new PlayerGameOrderVo();
        for (PlayerGameOrder order : list) {
            BettingInfoApp infoApp = new BettingInfoApp();
            infoApp.setId(playerGameOrderVo.getSearchId(order.getId()));//加密后的id
            infoApp.setApiId(order.getApiId());
            infoApp.setGameId(order.getGameId());
            infoApp.setTerminal(order.getTerminal());
            infoApp.setBetTime(order.getBetTime());
            infoApp.setSingleAmount(order.getSingleAmount());
            infoApp.setOrderState(order.getOrderState());
            infoApp.setActionIdJson(order.getActionIdJson());
            infoApp.setProfitAmount(order.getProfitAmount());
            infoApp.setUrl(String.format(url, infoApp.getId()));
            infoApp.setApiName(ApiGameTool.getSiteApiName(siteApiI18nMap, apiI18nMap, String.valueOf(order.getApiId())));
            infoApp.setGameName(ApiGameTool.getSiteGameName(siteGameI18nMap, gameI18nMap, String.valueOf(order.getGameId())));
            bettingInfoAppList.add(infoApp);
        }
        return bettingInfoAppList;
    }

    protected void initQueryDate(VPlayerTransactionListVo listVo) {

        listVo.setMinDate(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
        VPlayerTransactionSo vPlayerTransactionSo = listVo.getSearch();
        if (vPlayerTransactionSo.getBeginCreateTime() == null) {
            vPlayerTransactionSo.setBeginCreateTime(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
        } else if (vPlayerTransactionSo.getBeginCreateTime().before(listVo.getMinDate())) {
            vPlayerTransactionSo.setBeginCreateTime(listVo.getMinDate());
        }
        if (vPlayerTransactionSo.getEndCreateTime() == null) {
            vPlayerTransactionSo.setEndCreateTime(SessionManager.getDate().getTomorrow());
        } else {// app查询是根据日期查询的， 所以要日期 + 1天
            listVo.getSearch().setEndCreateTime(DateTool.addDays(listVo.getSearch().getEndCreateTime(), 1));
        }

    }

    protected VPlayerTransactionListVo preList(VPlayerTransactionListVo playerTransactionListVo) {
        Map<String, Serializable> transactionMap = DictTool.get(DictEnum.COMMON_TRANSACTION_TYPE);
        /*if (transactionMap != null) {   // 过滤转账类型
            transactionMap.remove(TransactionTypeEnum.TRANSFERS.getCode());
        }*/
        playerTransactionListVo.setDictCommonTransactionType(transactionMap);
        Map<String, Serializable> dictCommonStatus = DictTool.get(DictEnum.COMMON_STATUS);
        /*删掉稽核失败待处理状态*/
        dictCommonStatus.remove(CommonStatusEnum.DEAL_AUDIT_FAIL.getCode());
        playerTransactionListVo.setDictCommonStatus(dictCommonStatus);
        /*将 返水 推荐 的成功状态 修改为已发放*/
        Criteria criteriaType = Criteria.add(VPlayerTransaction.PROP_TRANSACTION_TYPE, Operator.IN, ListTool.newArrayList(TransactionTypeEnum.BACKWATER.getCode(), TransactionTypeEnum.RECOMMEND.getCode()));
        Criteria criteria = Criteria.add(VPlayerTransaction.PROP_STATUS, Operator.EQ, CommonStatusEnum.LSSUING.getCode());
        if (!playerTransactionListVo.getResult().isEmpty()) {
            CollectionTool.batchUpdate(playerTransactionListVo.getResult(), Criteria.and(criteria, criteriaType), MapTool.newHashMap(new Pair<String, Object>(VPlayerTransaction.PROP_STATUS, CommonStatusEnum.SUCCESS.getCode())));
        }
        return playerTransactionListVo;
    }

    protected FundRecordApp buildDictCommonTransactionType(Map<String, Object> map, FundRecordApp fundRecordApp) {
        if (map == null) {
            return fundRecordApp;
        }
        Map<String, String> transactionMap = new HashMap<>();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_TRANSACTION_TYPE);
        for (String key : map.keySet()) {
            transactionMap.put(key, i18n.get(key));
        }
        fundRecordApp.setTransactionMap(transactionMap);
        return fundRecordApp;
    }

    protected List<FundListApp> buildList(List<VPlayerTransaction> list) {
        List<FundListApp> fundListAppList = ListTool.newArrayList();
        Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_TRANSACTION_TYPE);
        Map<String, String> i18nStatus = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_STATUS);

        for (VPlayerTransaction vplayer : list) {
            Map map = vplayer.get_describe();

            FundListApp app = new FundListApp();
            String typeName = i18n.get(vplayer.getTransactionType());
            app.setTransaction_typeName(typeName);
            app.setStatusName(i18nStatus.get(vplayer.getStatus()));
            app.setId(vplayer.getId());
            app.setCreateTime(vplayer.getCreateTime());

            if (vplayer.getTransactionMoney() != 0 && map.get("bitAmount") == null) {
                app.setTransactionMoney(getCurrencySign(SessionManager.getUser().getDefaultCurrency()) + CurrencyTool.formatCurrency(vplayer.getTransactionMoney()));
            } else if (map.get("bankCode") != null && StringTool.equals(String.valueOf(map.get("bankCode")), "bitcoin")) {
                app.setTransactionMoney("Ƀ" + CurrencyTool.formatCurrency(vplayer.getTransactionMoney()));
            } else {
                app.setTransactionMoney(getCurrencySign(SessionManager.getUser().getDefaultCurrency()) + CurrencyTool.formatCurrency(0));
            }

            if (map.get("bitAmount") != null && map.get("bankCode") != null) {//针对于比特币存款
                if ((Double) map.get("bitAmount") > 0 && vplayer.getTransactionMoney() == 0 && StringTool.isNotBlank((String) map.get("bankCode"))) {
                    app.setTransactionMoney("Ƀ" + map.get("bitAmount"));
                } else {
                    app.setTransactionMoney("Ƀ" + CurrencyTool.formatCurrency(0));
                }
            }

            app.setTransactionType(vplayer.getTransactionType());
            app.setStatus(vplayer.getStatus());
            fundListAppList.add(app);
        }
        return fundListAppList;
    }

    /**
     *
     */
    protected RecordDetailApp buildRecordDetailApp(RecordDetailApp detailApp, VPlayerTransactionVo vo, VPlayerWithdrawVo withdrawVo, HttpServletRequest request) {

        VPlayerTransaction po = vo.getResult();

        Map<String, String> dictMapByEnum = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_FUND_TYPE);
        Map<String, String> dictMapByCurrency = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_CURRENCY_SYMBOL);
        detailApp.setTransactionNo(po.getTransactionNo());  //交易号
        detailApp.setCreateTime(po.getCreateTime());  //创建时间

        Map<String, Object> map = po.get_describe(); //取json对象里面的值
        String moneyType = SupportLocale.querySiteCurrencySignBySiteId();
        String statusName = LocaleTool.tranMessage(Module.COMMON, "status." + po.getStatus());

        if (StringTool.equals(po.getFundType(), FundTypeEnum.TRANSFER_INTO.getCode())) { //表示外面的钱转入我的钱包
            Integer apiId = (Integer) map.get("API");
            detailApp.setTransferOut(CacheBase.getSiteApiName(String.valueOf(apiId)));
            detailApp.setTransferInto(LocaleTool.tranMessage(Module.COMMON, "FundRecord.record.playerWallet"));
            detailApp.setTransactionMoney(moneyType + CurrencyTool.formatCurrency(po.getTransactionMoney()));  //金额
            detailApp.setStatusName(statusName); //状态
        }
        if (StringTool.equals(po.getFundType(), FundTypeEnum.TRANSFER_OUT.getCode())) { //从我的钱包转出外面
            detailApp.setTransferOut(LocaleTool.tranMessage(Module.COMMON, "FundRecord.record.playerWallet"));
            Integer apiId = (Integer) map.get("API");
            detailApp.setTransactionMoney(moneyType + CurrencyTool.formatCurrency(po.getTransactionMoney())); //金额
            detailApp.setTransferInto(CacheBase.getSiteApiName(String.valueOf(apiId)));
            detailApp.setStatusName(statusName); //状态
        }


        detailApp.setStatusName(statusName);

        if (StringTool.equals(po.getTransactionType(), TransactionTypeEnum.DEPOSIT.getCode())) { //存款
            detailApp.setRechargeAmount(moneyType + " " + CurrencyTool.formatCurrency(po.getRechargeAmount()));  //存款金额
            detailApp.setPoundage(moneyType + " " + CurrencyTool.formatCurrency((Number) map.get("poundage"))); //手续费
            detailApp.setRechargeTotalAmount(moneyType + " " + CurrencyTool.formatCurrency(po.getRechargeTotalAmount())); //实际到账
            detailApp.setRealName(StringTool.overlayName(SessionManager.getUser().getRealName())); //真实姓名
            detailApp.setStatusName(statusName); //状态
            if (map.get("returnTime") != null) {
                detailApp.setReturnTime(((Date) map.get("returnTime")).getTime()); //交易时间
            }
            if (StringTool.equals(po.getFundType(), DepositWayEnum.BITCOIN_FAST.getCode())) {//如果存款类型是比特币支付

                detailApp.setTransactionWayName(dictMapByEnum.get(DepositWayEnum.BITCOIN_FAST.getCode()));//比特币支付
                detailApp.setTxId(String.valueOf(map.get("bankOrder")));
                detailApp.setBitcoinAdress(String.valueOf(map.get("payerBankcard")));

                detailApp.setRechargeAmount(dictMapByCurrency.get("BTC") + map.get("bitAmount"));
            }
            Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_FUND_TYPE);

            detailApp.setTransactionWayName(i18n.get(po.getFundType()));

            if (FundTypeEnum.ARTIFICIAL_WITHDRAW.getCode().equals(po.getFundType())) {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND.getCode(), "FundRecord.view.tips"));  //人工存款
            } else if (FundTypeEnum.ARTIFICIAL_DEPOSIT.getCode().equals(po.getFundType())) {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND.getCode(), "FundRecord.view.tips")); //人工存款
            }

            if (map.get("bankCode") != null) {
                detailApp.setBankCode(String.valueOf(map.get("bankCode")));  //银行卡code icbc
                String bankName = LocaleTool.tranMessage(Module.COMMON, "bankname." + map.get("bankCode")); //将ICBC转换工商银行
                detailApp.setBankCodeName(bankName);
                detailApp.setBankUrl(setBankPictureUrl(request, po));
            }
            if (map.get("customBankName") != null && "other".equals(map.get("bankCode"))) {
                detailApp.setBankCodeName(String.valueOf(map.get("customBankName")));
            }
        }

        if (StringTool.equals(po.getTransactionType(), TransactionTypeEnum.WITHDRAWALS.getCode())) { //取款
            detailApp.setBankCode((String) map.get("bankCode"));
            String bankName = LocaleTool.tranMessage(Module.COMMON, "bankname." + detailApp.getBankCode());
            detailApp.setBankCodeName(bankName);
            detailApp.setBankUrl(setBankPictureUrl(request, detailApp.getBankCode()));

            String bankNo = String.valueOf(map.get("bankNo"));
            if (StringTool.isBlank(bankName) && !"artificial_withdraw".equals(po.getFundType())) {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND, "otherBankDescribe", StringTool.substring(bankNo, bankNo.length() - 4, bankNo.length()))); //描述
            } else if (FundTypeEnum.ARTIFICIAL_WITHDRAW.getCode().equals(po.getFundType())) {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND.getCode(), "FundRecord.view.tips"));  //人工取款
            } else {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND, "bankNumDescribe", bankName, StringTool.substring(bankNo, bankNo.length() - 4, bankNo.length()))); //描述
            }
            if (withdrawVo.getResult().getDeductFavorable() != null && withdrawVo.getResult().getDeductFavorable() > 0) {
                detailApp.setDeductFavorable(moneyType + " " + CurrencyTool.formatCurrency(withdrawVo.getResult().getDeductFavorable()));//扣除优惠
                detailApp.setAdministrativeFee(moneyType + " " + CurrencyTool.formatCurrency(withdrawVo.getResult().getAdministrativeFee())); //行政费用
                detailApp.setRechargeTotalAmount(moneyType + " " + "-" + CurrencyTool.formatCurrency(withdrawVo.getResult().getWithdrawActualAmount()));  //实际到账
            }
            detailApp.setRealName(StringTool.overlayName(SessionManager.getUser().getRealName())); //姓名
            if (map.get("bankNo") != null && "bitcoin".equals(map.get("bankCode"))) {
                detailApp.setWithdrawMoney(dictMapByCurrency.get("BTC") + " " + CurrencyTool.formatCurrency(po.getTransactionMoney()));  //取款金额
                detailApp.setPoundage(dictMapByCurrency.get("BTC") + withdrawVo.getResult().getCounterFee()); //手续费
                double totalAmount = withdrawVo.getResult().getWithdrawAmount() - withdrawVo.getResult().getCounterFee();
                detailApp.setRechargeTotalAmount(dictMapByCurrency.get("BTC") + " " + "-" + CurrencyTool.formatCurrency(totalAmount));  //实际到账
            } else {
                detailApp.setWithdrawMoney(moneyType + " " + CurrencyTool.formatCurrency(po.getTransactionMoney()));
                detailApp.setPoundage(moneyType + withdrawVo.getResult().getCounterFee()); //手续费
                detailApp.setRechargeTotalAmount(moneyType + " " + "-" + CurrencyTool.formatCurrency(withdrawVo.getResult().getWithdrawActualAmount())); //实际到账
            }

            detailApp.setFailureReason(po.getFailureReason()); //失败原因
            detailApp.setStatusName(statusName); //状态
        }

        if (StringTool.equalsIgnoreCase(po.getTransactionType(), TransactionTypeEnum.FAVORABLE.getCode())) { //优惠
            Map<String, String> i18n = I18nTool.getDictMapByEnum(SessionManager.getLocale(), DictEnum.COMMON_FUND_TYPE);
            detailApp.setTransactionWayName(String.valueOf(map.get(SessionManager.getLocale().toString()))); //描述
            if (StringTool.isBlank(detailApp.getTransactionWayName()) || "null".equals(detailApp.getTransactionWayName())) {
                detailApp.setTransactionWayName(String.valueOf(map.get("activityName"))); //描述
            }
            if ((StringTool.isBlank(detailApp.getTransactionWayName()) || "null".equals(detailApp.getTransactionWayName()))
                    && "refund_fee".equals(po.getFundType())) {

                detailApp.setTransactionWayName(i18n.get(po.getFundType()));
            }
            detailApp.setTransactionMoney(moneyType + CurrencyTool.formatCurrency(po.getTransactionMoney()));  //金额
            detailApp.setStatusName(statusName); //状态
        }

        if (StringTool.equalsIgnoreCase(po.getTransactionType(), TransactionTypeEnum.BACKWATER.getCode())) {  //返水
            String dateStr = LocaleDateTool.formatDate((Date) map.get("date"), new DateFormat().getYEARMONTH(), SessionManagerCommon.getTimeZone());
            detailApp.setTransactionWayName(dateStr.replace("-", "年") + "月" + map.get("period") + "期");//描述
            detailApp.setDeductFavorable(moneyType + CurrencyTool.formatCurrency(po.getTransactionMoney()));  //金额
            detailApp.setStatusName(statusName); //状态
        }

        if (StringTool.equalsIgnoreCase(po.getTransactionType(), TransactionTypeEnum.RECOMMEND.getCode())) { //推荐
            //单次奖励推荐
            if (TransactionWayEnum.SINGLE_REWARD.getCode().equals(po.getTransactionWay()) && MapTool.getInteger(map, "rewardType") == 2) {
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND, "friendRecommend", map.get("username")));
            } else if (TransactionWayEnum.SINGLE_REWARD.getCode().equals(po.getTransactionWay()) && MapTool.getInteger(map, "rewardType") == 3) { //单次奖励被推荐
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND, "friendBeUsedToRecommend", map.get("username"))); //描述
            } else {//红利推荐
                detailApp.setTransactionWayName(LocaleTool.tranView(Module.FUND, "FundRecord.record.singleReward"));
            }
            detailApp.setTransactionMoney(moneyType + CurrencyTool.formatCurrency(po.getTransactionMoney()));  //金额
            detailApp.setStatusName(statusName); //状态
        }
        if (map.get("bitAmount") != null) {
            detailApp.setBitAmount(String.valueOf(map.get("bitAmount")));
        }

        return detailApp;
    }

    /**
     * 根据银行code获取银行url图片
     *
     * @param request
     * @param bankNameCode
     * @return
     */
    private String setBankPictureUrl(HttpServletRequest request, String bankNameCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(ConfigBase.get().getResRoot(), request.getServerName()));
        sb.append(COMMON_PAYBANK_PHOTO);
        sb.append(bankNameCode);
        sb.append(".png");
        return sb.toString();
    }


    private String setBankPictureUrl(HttpServletRequest request, VPlayerTransaction po) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(ConfigBase.get().getResRoot(), request.getServerName()));
        sb.append(COMMON_PAYBANK_PHOTO);

        Map map = po.get_describe();
        sb.append(map.get("bankCode"));
        sb.append(".png");
        return sb.toString();
    }

    /**
     * 取款处理中/转账处理中的金额
     */
    protected void getFund(FundRecordApp fundRecordApp) {
        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(SessionManager.getUserId());

        fundRecordApp.setWithdrawSum(ServiceSiteTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));
        if (!ParamTool.isLotterySite()) {
            //正在转账中金额
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
            fundRecordApp.setTransferSum(getPlayerTransferService().queryProcessAmount(playerTransferVo));
        }
    }

    private IPlayerTransferService getPlayerTransferService() {
        if (playerTransferService == null)
            playerTransferService = ServiceSiteTool.playerTransferService();
        return playerTransferService;
    }

    /**
     * 获取系统公告转换成App接口数据
     *
     * @param vListVo
     * @return
     */
    protected Map<String, Object> getSystemNotice(VSystemAnnouncementListVo vListVo) {
        vListVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.SYSTEM.getCode());
        vListVo = getNotice(vListVo);

        if (CollectionTool.isNotEmpty(vListVo.getResult())) {
            for (VSystemAnnouncement vSystemAnnouncement : vListVo.getResult()) {
                vSystemAnnouncement.setContent(StringTool.replaceHtml(vSystemAnnouncement.getContent()));
            }
        }

        Map<String, Object> map = new HashMap<>(FOUR, ONE_FLOAT);
        List<AppSystemNotice> sysNotices = ListTool.newArrayList();
        for (VSystemAnnouncement sysAnnounce : vListVo.getResult()) {
            AppSystemNotice sysNotice = new AppSystemNotice();
            sysNotice.setSearchId(vListVo.getSearchId(sysAnnounce.getId()));
            sysNotice.setContent(sysAnnounce.getShortContentText50().replace("&nbsp;", ""));
            sysNotice.setPublishTime(sysAnnounce.getPublishTime());
            sysNotice.setLink(SYSTEM_NOTICE_LINK + "?searchId=" + vListVo.getSearchId(sysAnnounce.getId()));
            sysNotices.add(sysNotice);
        }
        map.put("list", sysNotices);
        map.put("pageTotal", vListVo.getPaging().getTotalCount());
        map.put("minDate", SessionManager.getDate().addDays(SYSTEM_NOTICE_MIN_TIME));
        map.put("maxDate", new Date());
        return map;
    }

    /**
     * 系统详情公告
     *
     * @param vSystemAnnouncementListVo
     * @return
     */
    protected AppSystemNotice getSystemNoticeDetail(VSystemAnnouncementListVo vSystemAnnouncementListVo) {
        vSystemAnnouncementListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        vSystemAnnouncementListVo = ServiceTool.vSystemAnnouncementService().search(vSystemAnnouncementListVo);

        AppSystemNotice sysNotice = new AppSystemNotice();
        for (VSystemAnnouncement sysAnnounce : vSystemAnnouncementListVo.getResult()) {
            sysNotice.setTitle(sysAnnounce.getTitle());
            sysNotice.setPublishTime(sysAnnounce.getPublishTime());
            sysNotice.setContent(StringTool.replaceHtml(sysAnnounce.getContent()));
        }
        return sysNotice;
    }

    protected AdvisoryMessageDetailApp buildingAdvisoryMessageDetailApp(AdvisoryMessageDetailApp detailApp, VPlayerAdvisory advisory) {
        detailApp.setAdvisoryTitle(advisory.getAdvisoryTitle());
        detailApp.setAdvisoryContent(advisory.getAdvisoryContent());
        detailApp.setQuestionType(advisory.getQuestionType());
        detailApp.setAdvisoryTime(advisory.getAdvisoryTime().getTime());
        return detailApp;
    }

    /**
     * 获取游戏公告
     *
     * @param listVo
     */
    protected Map<String, Object> getAppGameNotice(VSystemAnnouncementListVo listVo) {
        /*if (listVo.getSearch().getEndTime() != null) {
            listVo.getSearch().setEndTime(DateTool.addDays(listVo.getSearch().getEndTime(), DEFAULT_TIME));
        } 因为在 getNotice(listvo)中已经给endTime +1 了，所以这里 +1 的操作要注掉*/

        SysParam param = ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_IS_LOTTERY_SITE);
        if (param != null && param.getParamValue() != null && param.getParamValue().equals("true")) {
            listVo.getSearch().setApiId(Integer.parseInt(ApiProviderEnum.PL.getCode()));
        }

        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        listVo = getNotice(listVo);

        List<AppGameNotice> gameNotices = ListTool.newArrayList();
        for (VSystemAnnouncement sysAnnounce : listVo.getResult()) {
            for (SiteApiI18n siteApi : Cache.getSiteApiI18n().values()) {
                if (siteApi.getApiId().equals(sysAnnounce.getApiId())) {
                    AppGameNotice gameNotice = new AppGameNotice();
                    gameNotice.setId(listVo.getSearchId(sysAnnounce.getId()));
                    gameNotice.setTitle(sysAnnounce.getShortTitle80().replace("&nbsp;", ""));
                    gameNotice.setContext(sysAnnounce.getShortContentText80().replace("&nbsp;", ""));
                    gameNotice.setLink(GAME_NOTICE_LINK + "?searchId=" + listVo.getSearchId(sysAnnounce.getId()));

                    //游戏拼接
                    StringBuilder sb = new StringBuilder();
                    if (sysAnnounce.getApiId() != null) {
                        sb.append(CacheBase.getSiteApiName(sysAnnounce.getApiId().toString()));
                    }
                    if (sysAnnounce.getGameId() != null) {
                        sb.append("——").append(CacheBase.getSiteGameName(sysAnnounce.getGameId().toString()));
                    }
                    gameNotice.setGameName(sb.toString());
                    gameNotice.setPublishTime(sysAnnounce.getPublishTime());
                    gameNotices.add(gameNotice);
                }
            }
        }
        Map<String, Object> map = new HashMap<>(APP_ERROR_TIMES, ONE_FLOAT);
        map.put("list", gameNotices);
        map.put("pageTotal", listVo.getPaging().getTotalCount());
        map.put("maxDate", SessionManager.getDate().getNow());
        map.put("minDate", SessionManager.getDate().addDays(SYSTEM_NOTICE_MIN_TIME));

        List<AppSelectSiteApiI18n> appSiteApis = ListTool.newArrayList();
        for (SiteApiI18n siteApi : Cache.getSiteApiI18n().values()) {
            AppSelectSiteApiI18n appSiteApi = new AppSelectSiteApiI18n();
            appSiteApi.setApiId(siteApi.getApiId());
            appSiteApi.setApiName(CacheBase.getSiteApiName(siteApi.getApiId().toString()));
            appSiteApis.add(appSiteApi);
        }
        map.put("apiSelect", appSiteApis);//获取SiteApi

        return map;
    }

    /**
     * 获取游戏公告详情
     *
     * @param vSystemAnnouncementListVo
     * @return
     */
    protected AppGameNotice getAppGameNoticeDetail(VSystemAnnouncementListVo vSystemAnnouncementListVo) {
        vSystemAnnouncementListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        vSystemAnnouncementListVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        vSystemAnnouncementListVo = ServiceTool.vSystemAnnouncementService().search(vSystemAnnouncementListVo);
        AppGameNotice gameNotice = new AppGameNotice();
        for (VSystemAnnouncement sysAnnounce : vSystemAnnouncementListVo.getResult()) {
            gameNotice.setContext(StringTool.replaceHtml(sysAnnounce.getContent()));
            gameNotice.setPublishTime(sysAnnounce.getPublishTime());
        }
        return gameNotice;
    }

    /**
     * 提取公共 获取公告信息
     */
    private VSystemAnnouncementListVo getNotice(VSystemAnnouncementListVo listVo) {
        if (listVo.getSearch().getStartTime() == null) {
            listVo.getSearch().setStartTime(DateTool.addMonths(SessionManager.getDate().getNow(), RECOMMEND_DAYS));
        }
        if (listVo.getSearch().getEndTime() == null) {
            listVo.getSearch().setEndTime(SessionManager.getDate().getTomorrow());
        } else {//时间加一天，因为app是以日期查询的
            listVo.getSearch().setEndTime(DateTool.addDays(listVo.getSearch().getEndTime(), 1));
        }
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        return ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
    }

    /**
     * 站点消息-->系统消息
     *
     * @return
     */
    protected Map getAppSiteSysNotice(VNoticeReceivedTextListVo listVo) {
        listVo.getSearch().setReceiverId(SessionManager.getUserId());
        listVo = ServiceTool.noticeService().fetchReceivedSiteMsg(listVo);
        List<AppSystemNotice> sysNotices = ListTool.newArrayList();
        for (VNoticeReceivedText text : listVo.getResult()) {
            text.setContent(text.getContent().replaceAll("\\$\\{user\\}", SessionManager.getUserName()));
            text.setTitle(text.getTitle().replaceAll("\\$\\{user\\}", SessionManager.getUserName()));

            AppSystemNotice sysNotice = new AppSystemNotice();
            if (text.getId() != null) {
                sysNotice.setId(text.getId().toString());
            }
            sysNotice.setSearchId(listVo.getSearchId(text.getId()));
            sysNotice.setTitle(text.getShortTitle50());
            sysNotice.setPublishTime(text.getReceiveTime());
            sysNotice.setLink(SITE_SYSTEM_NOTICE + "?searchId=" + listVo.getSearchId(text.getId()));
            sysNotice.setRead(StringTool.equalsIgnoreCase(text.getReceiveStatus(), IS_READ) ? true : false);
            sysNotices.add(sysNotice);
        }
        Map<String, Object> map = new HashMap<>(TWO, ONE_FLOAT);
        map.put("list", sysNotices);
        map.put("pageTotal", listVo.getPaging().getTotalCount());
        return map;
    }

    /**
     * 系统信息详情
     *
     * @param noticeReceiveVo
     * @param request
     * @return
     */
    protected AppSystemNotice getAppSiteNoticeDetail(VNoticeReceivedTextVo vo, NoticeReceiveVo noticeReceiveVo, HttpServletRequest request) {
        //标记为已读
        List list = new ArrayList();
        list.add(noticeReceiveVo.getSearch().getId());
        noticeReceiveVo.setIds(list);
        ServiceTool.noticeService().markSiteMsg(noticeReceiveVo);

        //查询详情信息
        vo = ServiceTool.noticeService().fetchReceivedSiteMsgDetail(vo);
        if (StringTool.isNotBlank(vo.getResult().getContent())) {
            vo.getResult().setContent(vo.getResult().getContent().replaceAll("\\$\\{user\\}", SessionManager.getUserName()));
        }
        if (StringTool.isNotBlank(vo.getResult().getContent())) {
            vo.getResult().setContent(vo.getResult().getContent().replaceAll("\\$\\{sitename\\}", SessionManager.getSiteName(request)));
        }
        if (StringTool.isNotBlank(vo.getResult().getTitle())) {
            vo.getResult().setTitle(vo.getResult().getTitle().replaceAll("\\$\\{user\\}", SessionManager.getUserName()));
        }

        AppSystemNotice sysNotice = new AppSystemNotice();
        sysNotice.setTitle(vo.getResult().getTitle());
        sysNotice.setContent(StringTool.replaceHtml(vo.getResult().getContent()));
        sysNotice.setPublishTime(vo.getResult().getReceiveTime());
        return sysNotice;
    }

    /**
     * 获取站点信息未读总数
     *
     * @param listVo
     * @return
     */
    protected Map unReadCount(VPlayerAdvisoryListVo listVo) {
        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        long length = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        //玩家咨询-未读数量
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceSiteTool.vPlayerAdvisoryService().search(listVo);
        int advisoryUnReadCount = 0;
        String tag = "";

        PlayerAdvisoryReplyListVo replyListVo = new PlayerAdvisoryReplyListVo();
        Integer[] ids = new Integer[listVo.getResult().size()];
        for (int i = 0; i < listVo.getResult().size(); i++) {
            ids[i] = listVo.getResult().get(i).getId();
        }
        replyListVo.getSearch().setIds(ids);
        //根据ids查，如果ids为空，就不用在查了
        if (ids.length > 0) {
            //查询回复表每一条在已读表是否存在
            replyListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdsPlayerReply(replyListVo);
        }

        for (PlayerAdvisoryReply replay : replyListVo.getResult()) {
            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryReplyId(replay.getId());
            readVo = ServiceSiteTool.playerAdvisoryReadService().search(readVo);
            //不存在未读+1，标记已读咨询Id
            if (readVo.getResult() == null && !tag.contains(replay.getPlayerAdvisoryId().toString())) {
                advisoryUnReadCount++;
                tag += replay.getPlayerAdvisoryId().toString() + ",";
            }
        }
        Map<String, Object> map = new HashMap<>(TWO, ONE_FLOAT);
        map.put("sysMessageUnReadCount", length);
        map.put("advisoryUnReadCount", advisoryUnReadCount);
        return map;
    }

    /**
     * 获取玩家咨询的List
     *
     * @param listVo
     * @return
     */
    protected VPlayerAdvisoryListVo searchAdvisoryList(VPlayerAdvisoryListVo listVo) {
        if (listVo.getSearch() == null) {
            listVo.setSearch(new VPlayerAdvisorySo());
        }
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo.getSearch().setQuestionType(PlayerAdvisoryEnum.QUESTION.getCode());
        listVo = ServiceSiteTool.vPlayerAdvisoryService().search(listVo);

        return listVo;
    }

    protected VPlayerAdvisoryListVo getUnReadList(VPlayerAdvisoryListVo listVo) {

        String tag = "";
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
                    obj.setIsRead(false);
                } else {
                    obj.setIsRead(true);
                }
            }
        }

        return listVo;
    }


    /**
     * 获取玩家推荐信息
     */
    protected Map<String, Object> getPlayerRecommend(HttpServletRequest request, PlayerRecommendAwardListVo listVo) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        Map<String, Object> map = new HashMap<>(7, 1f);
        if (userPlayerVo.getResult() != null) {
            StringBuilder sb = new StringBuilder();

            ISysDomainService domainService = ServiceTool.sysDomainService();
            SysDomainVo vo = new SysDomainVo();
            vo.getSearch().setSiteId(SessionManager.getSiteId());
            vo.getSearch().setPageUrl(DomainPageUrlEnum.INDEX.getCode());
            vo.getSearch().setSubsysCode(SubSysCodeEnum.MSITES.getCode());
            vo.getSearch().setResolveStatus(ResolveStatusEnum.SUCCESS.getCode());
            List<String> domainLst = domainService.querySiteDomain(vo);
            String domain;
            if(CollectionTool.isNotEmpty(domainLst) && domainLst.size() < 2){
                domain = request.getServerName();
            }else {
                domainLst.remove(request.getServerName());
                //随机返回个domain
                int index = (int) (Math.random() * domainLst.size());
                domain = domainLst.get(index);
            }
            sb.append(domain);
            sb.append("/register.html?c=");
            String invitationCode = userPlayerVo.getResult().getRegistCode() + SessionManager.getUserId().toString();
            sb.append(Base36.encryptIgnoreCase(invitationCode));
            //分享码
            map.put("code", sb.toString());
            LOG.info("玩家邀请码:[" + userPlayerVo.getResult().getRegistCode() + "][" + SessionManager.getUserId().toString() + "]" + Base36.encryptIgnoreCase(invitationCode));
        }
        //如果这个参数有值，表示有单次推荐奖励
        map.put("theWay", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_THEWAY).getParamValue());
        //满足存款条件后谁获利：1 表示双方获取奖励 2表示你将会得到 其他表示你推荐的好友会获取到
        map.put("reward", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD).getParamValue());
        //将会获取到的金额值
        map.put("money", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_MONEY).getParamValue());
        //有红利奖励显示分享红利标志，没有不显示
        map.put("isBonus", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_BONUS).getActive());
        // 存款金额满多少钱
        map.put("witchWithdraw", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_THEWAY).getParamValue());

        //有效分享人数 和所对应的红利
        SysParam bonusJson = ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_BONUS_JSON);
        ArrayList<GradientTemp> gradientTempArrayList = JsonTool.fromJson(bonusJson.getParamValue(), new TypeReference<ArrayList<GradientTemp>>() {
        });
        map.put("gradientTempArrayList", gradientTempArrayList);

        PlayerRecommendAwardVo playerVo = new PlayerRecommendAwardVo();
        playerVo.getSearch().setUserId(SessionManager.getUserId());
        map.put("sign", getCurrencySign());
        map.put("recommend", ServiceSiteTool.playerRecommendAwardService().searchRewardUserAndBonus(playerVo));
        Map siteI18nMap = Cache.getSiteI18n(SiteI18nEnum.MASTER_RECOMMEND_RULE);
        if (MapTool.isNotEmpty(siteI18nMap)) {
            map.put("activityRules", Cache.getSiteI18n(SiteI18nEnum.MASTER_RECOMMEND_RULE).get(SessionManager.getLocale().toString()).getValue()); //活动规则
        } else {
            map.put("activityRules", "站长未开启分享活动."); //活动规则
        }
        return map;
    }

    /**
     * 根据条件获取分享好友记录
     *
     * @param request
     * @param listVo
     * @return
     */
    protected Map<String, Object> findPlayerRecommentRecord(HttpServletRequest request, PlayerRecommendAwardListVo listVo) {
        Map<String, Object> map = new HashMap<>();
        //查询被该玩家推荐的好友记录奖励表
        listVo.getSearch().setUserId(SessionManager.getUserId());
        initSearchDate(listVo);
        listVo = ServiceSiteTool.playerRecommendAwardService().searchRewardRecodeToApp(listVo);
        List<PlayerRecommendAwardRecord> list = changeToApp(listVo.getRecommendAwardRecords());
        map.put("command", list);
        if (listVo == null || CollectionTool.isEmpty(listVo.getRecommendAwardRecords())) {
            map.put("total", 0);
        } else {
            map.put("total", listVo.getPaging().getTotalCount());
        }
        return map;
    }


    /**
     * 转换推荐记录到原生
     *
     * @param records
     * @return
     */
    private List<PlayerRecommendAwardRecord> changeToApp(List<PlayerRecommendAwardRecord> records) {
        for (PlayerRecommendAwardRecord record : records) {
            record.setRecommendUserName(StringTool.overlayString(record.getRecommendUserName()));
            record.setStatus(SysUserStatus.enumOf(record.getStatus()).getTrans());
        }
        return records;
    }

    /**
     * 初始化 推荐记录 时间区间
     *
     * @param listVo
     */
    private void initSearchDate(PlayerRecommendAwardListVo listVo) {
        if (listVo.getSearch().getStartTime() == null) {
            listVo.getSearch().setStartTime(SessionManager.getDate().addDays(DEFAULT_MIN_TIME));
        } else if (listVo.getSearch().getEndTime() == null) {
            listVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        }
    }

}
