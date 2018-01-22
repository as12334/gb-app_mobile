package so.wwb.gamebox.mobile.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.DateQuickPicker;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.security.CryptoTool;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.msg.notice.po.VNoticeReceivedText;
import org.soul.model.msg.notice.vo.NoticeReceiveVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.mobile.app.model.*;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.*;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.operator.po.VSystemAnnouncement;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.company.vo.BankListVo;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.enums.DemoModelEnum;
import so.wwb.gamebox.model.enums.UserTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.enums.AnnouncementTypeEnum;
import so.wwb.gamebox.model.master.enums.CommonStatusEnum;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.PlayerRecommendAward;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.bank.BankCardTool;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

import static org.soul.commons.currency.CurrencyTool.formatCurrency;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;

/**
 * Created by ed on 17-12-31.
 */
public class BaseMineController {
    private Log LOG = LogFactory.getLog(BaseMineController.class);
    private static final String SYSTEM_NOTICE_LINK = "/mineOrigin/getSysNoticeDetail.html";
    private static final String GAME_NOTICE_LINK = "/mineOrigin/getGameNoticeDetail.html";
    private static final String SITE_SYSTEM_NOTICE = "/mineOrigin/getSiteSysNoticeDetail.html";


    protected PlayerApiListVo initPlayerApiListVo(Integer userId) {
        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        return listVo;
    }

    /**
     * 刷新额度
     *
     * @param request
     * @return
     */
    protected UserInfoApp appRefresh(HttpServletRequest request) {
        Integer userId = SessionManager.getUserId();
        PlayerApiListVo listVo = initPlayerApiListVo(userId);
        VUserPlayer player = getVPlayer(userId);

        UserInfoApp infoApp = new UserInfoApp();
        infoApp.setApis(getSiteApis(listVo, request, true));
        infoApp.setCurrSign(player.getCurrencySign());
        infoApp.setAssets(queryPlayerAssets(listVo, userId));
        infoApp.setUsername(player.getUsername());
        return infoApp;
    }

    /**
     * 组装数据 我的优惠
     * @param recodeList
     * @return
     */
    protected List<MyPromoApp> buildingMyPromoApp(List<VPreferentialRecode> recodeList) {
        Integer userId = SessionManager.getUser().getId();
        List<MyPromoApp> myPromoApps = ListTool.newArrayList();
        for (VPreferentialRecode recode : recodeList) {
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
        return myPromoApps;
    }



    /**
     * 一键回收
     *
     * @return
     */
    protected Map appRecovery() {
        PlayerApiVo playerApiVo = new PlayerApiVo();
        playerApiVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
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
        if (StringTool.isBlank(playerApiVo.getOrigin())) {
            playerApiVo.setOrigin(TransactionOriginEnum.PC.getCode());
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
                    apiId == Integer.valueOf(ApiProviderEnum.PL.getCode()) ||
                            apiId == Integer.valueOf(ApiProviderEnum.DWT.getCode()))) {
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

    /**
     * 回收是否允许时间间隔
     *
     * @return
     */
    private boolean isAllowRecoveryTimeInterval(Integer apiId) {
        Date date = SessionManagerBase.getDate().getNow();
        if (apiId == null) {
            Date lastRecoveryTime = SessionManagerCommon.getUserRecoveryAllApiTime();
            if (lastRecoveryTime == null) {
                return true;
            }
            return DateTool.secondsBetween(date, lastRecoveryTime) > RECOVERY_TIME_INTERVAL;
        } else {
            Date lastRecoveryTime = SessionManagerCommon.getUserRecoveryApiTime();
            if (lastRecoveryTime == null) {
                return true;
            }
            return DateTool.secondsBetween(date, lastRecoveryTime) > API_RECOVERY_TIME_INTERVAL;
        }
    }

    /**
     * 查询玩家总资产
     */
    protected String queryPlayerAssets(PlayerApiListVo listVo, Integer userId) {
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        double assets = ServiceSiteTool.playerApiService().queryPlayerAssets(listVo);
        return formatCurrency(assets);
    }

    protected List<Map<String, Object>> getSiteApis(PlayerApiListVo listVo, HttpServletRequest request, boolean isFetch) {
        //同步余额
        if (isFetch) {
            IApiBalanceService service = (IApiBalanceService) SpringTool.getBean("apiBalanceService");
            service.fetchPlayerAllApiBalance();
            listVo.getSearch().setApiId(null);
        }
        listVo.setType(ApiQueryTypeEnum.ALL_API.getCode());
        listVo = ServiceSiteTool.playerApiService().fundRecord(listVo);
         /* 翻译api */
        List<Map<String, Object>> maps = new ArrayList<>();
        List<SiteApi> apis = getSiteApi();
        for (SiteApi siteApi : apis) {
            for (PlayerApi api : listVo.getResult()) {
                if (siteApi.getApiId().intValue() == api.getApiId().intValue()) {
                    Map<String, Object> map = new HashMap<>();
                    String apiId = api.getApiId().toString();
                    map.put("apiId", apiId);
                    map.put("apiName", CacheBase.getSiteApiName(apiId));
                    map.put("balance", api.getMoney() == null ? 0 : api.getMoney());
                    map.put("status", siteApi.getStatus());

                    maps.add(map);
                }
            }
        }

        //根据API余额降序 Add by Bruce.QQ
        Collections.sort(maps, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return ((Double) o2.get("balance")).compareTo((Double) o1.get("balance"));
            }
        });
        return maps;
    }

    private List<SiteApi> getSiteApi() {
        Map<String, SiteApi> siteApiMap = CacheBase.getSiteApi();
        List<SiteApi> siteApis = new ArrayList<>();
        Map<String, Api> apiMap = CacheBase.getApi();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        if (MapTool.isNotEmpty(siteApiMap)) {
            for (SiteApi siteApi : siteApiMap.values()) {
                Api api = apiMap.get(String.valueOf(siteApi.getApiId()));
                if (api != null && !disable.equals(api.getSystemStatus()) && !disable.equals(siteApi.getSystemStatus())) {
                    if (maintain.equals(api.getSystemStatus()) || maintain.equals(siteApi.getSystemStatus())) {
                        siteApi.setStatus(maintain);
                    }
                    siteApis.add(siteApi);
                }
            }
        }
        return siteApis;
    }

    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }


    /**
     * 获取用户银行卡信息
     *
     * @param map
     */
    public void bankcard(Map map) {
        map.put("user", SessionManagerCommon.getUser());
        map.put("bankListVo", BankHelper.getBankListVo());
    }



    protected VUserPlayer getVPlayer(Integer userId) {
        VUserPlayerVo vo = new VUserPlayerVo();
        vo.getSearch().setId(userId);
        VUserPlayer player = ServiceSiteTool.vUserPlayerService().queryPlayer4App(vo);
        if (player != null) {
            player.setCurrencySign(getCurrencySign(player.getDefaultCurrency()));
        }
        return player;
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

    protected void initQueryDateForgetBetting(PlayerGameOrderListVo playerGameOrderListVo, int TIME_INTERVAL, int DEFAULT_TIME) {
        playerGameOrderListVo.setMinDate(SessionManager.getDate().addDays(TIME_INTERVAL));
        if (playerGameOrderListVo.getSearch().getBeginBetTime() == null) {
            playerGameOrderListVo.getSearch().setBeginBetTime(DateTool.addDays(SessionManager.getDate().getTomorrow(), -DEFAULT_TIME));//拿到明天在-1相当于拿到今天时间00:00:00
        }
        if (playerGameOrderListVo.getSearch().getEndBetTime() == null || playerGameOrderListVo.getSearch().getBeginBetTime().after(playerGameOrderListVo.getSearch().getEndBetTime())) {
            playerGameOrderListVo.getSearch().setEndBetTime(DateTool.addSeconds(SessionManager.getDate().getTomorrow(), -1));
        }
    }

    /**
     * 统计当前页数据
     */
    protected Map<String, Object> statisticsData(PlayerGameOrderListVo listVo, int TIME_INTERVAL, int DEFAULT_TIME) {
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDateForgetBetting(listVo, TIME_INTERVAL, DEFAULT_TIME);
        // 统计数据
        listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1), -1));
        Map map = ServiceSiteTool.playerGameOrderService().queryTotalPayoutAndEffect(listVo);
        map.put("currency", getCurrencySign());
        return map;
    }

    protected List<BettingInfoApp> buildBetting(List<PlayerGameOrder> list) {
        List<BettingInfoApp> bettingInfoAppList = new ArrayList<>();
        for (PlayerGameOrder order : list) {
            BettingInfoApp infoApp = new BettingInfoApp();

            PlayerActivityMessage message = new PlayerActivityMessage();
            message.setId(order.getId());

            infoApp.setId(message.getSearchId());//加密后的id
            infoApp.setApiId(order.getApiId());
            infoApp.setGameId(order.getGameId());
            infoApp.setTerminal(order.getTerminal());
            infoApp.setBetTime(order.getBetTime());
            infoApp.setSingleAmount(order.getSingleAmount());
            infoApp.setOrderState(order.getOrderState());
            infoApp.setActionIdJson(order.getActionIdJson());
            infoApp.setProfitAmount(order.getProfitAmount());

//解密后的id
            infoApp.setUrl("/fund/betting/gameRecordDetail.html?search.id=" + Integer.valueOf(CryptoTool.aesDecrypt(message.getSearchId(), "PlayerActivityMessageListVo")));

            String apiName = CacheBase.getSiteApiName(String.valueOf(order.getApiId()));
            infoApp.setApiName(apiName);

            String gameName = CacheBase.getSiteGameName(String.valueOf(order.getGameId()));
            infoApp.setGameName(gameName);
            bettingInfoAppList.add(infoApp);
        }
        return bettingInfoAppList;
    }


    protected void initQueryDate(VPlayerTransactionListVo listVo) {

        listVo.setMinDate(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
        if (listVo.getSearch().getBeginCreateTime() == null) {
            listVo.getSearch().setBeginCreateTime(SessionManager.getDate().addDays(LAST_WEEK__MIN_TIME));
        } else if (listVo.getSearch().getBeginCreateTime().before(listVo.getMinDate())) {
            listVo.getSearch().setBeginCreateTime(listVo.getMinDate());
        }
        if (listVo.getSearch().getEndCreateTime() == null) {
            listVo.getSearch().setEndCreateTime(SessionManager.getDate().getNow());
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

    protected FundRecordApp buildDictCommonTransactionType(Map map, FundRecordApp fundRecordApp) {
        Set entries = map.entrySet();
        Map<String, String> transactionMap = MapTool.newHashMap();
        if (entries != null) {
            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String transactionTypeName = LocaleTool.tranMessage(Module.COMMON, "transaction_type." + key);
                transactionMap.put(key, transactionTypeName);
            }
            fundRecordApp.setTransactionMap(transactionMap);
        }

        return fundRecordApp;
    }


    protected List<FundListApp> buildList(List<VPlayerTransaction> list) {
        List<FundListApp> fundListAppList = ListTool.newArrayList();

        for (VPlayerTransaction vplayer : list) {
            FundListApp app = new FundListApp();
            String typeName = LocaleTool.tranMessage(Module.COMMON, "transaction_type." + vplayer.getTransactionType());
            app.setTransaction_typeName(typeName);


            String statusName = LocaleTool.tranMessage(Module.COMMON, "status." + vplayer.getStatus());
            app.setStatusName(statusName);

            app.setId(vplayer.getId());
            app.setCreateTime(vplayer.getCreateDate());
            app.setTransactionMoney(vplayer.getTransactionMoney());
            app.setTransactionType(vplayer.getTransactionType());
            app.setStatus(vplayer.getStatus());
            fundListAppList.add(app);
        }
        return fundListAppList;
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
//            model.addAttribute("transferSum", playerTransferService().queryProcessAmount(playerTransferVo));
            fundRecordApp.setTransferSum(getPlayerTransferService().queryProcessAmount(playerTransferVo));
        }
    }

    private IPlayerTransferService playerTransferService;

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
            sysNotice.setContent(sysAnnounce.getShortContentText50());
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
            sysNotice.setContent(sysAnnounce.getContent());
        }
        return sysNotice;
    }

    /**
     * 获取游戏公告
     *
     * @param listVo
     */
    protected Map<String, Object> getAppGameNotice(VSystemAnnouncementListVo listVo) {
        if (listVo.getSearch().getEndTime() != null) {
            listVo.getSearch().setEndTime(DateTool.addDays(listVo.getSearch().getEndTime(), DEFAULT_TIME));
        }

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
                    gameNotice.setTitle(sysAnnounce.getShortTitle80());
                    gameNotice.setContext(sysAnnounce.getShortContentText80());
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
            appSiteApi.setApiName(siteApi.getName());
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
            gameNotice.setContext(sysAnnounce.getContent());
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
            listVo.getSearch().setEndTime(DateQuickPicker.getInstance().getNow());
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
        sysNotice.setContent(vo.getResult().getContent());
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
        Map<String, Object> map = new HashMap<>(TWO, ONE_FLOAT);
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
        //所有咨询数据
        for (VPlayerAdvisory obj : listVo.getResult()) {
            //查询回复表每一条在已读表是否存在
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceSiteTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);
            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
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
        }
        map.put("sysMessageUnReadCount", length);
        map.put("advisoryUnReadCount", advisoryUnReadCount);
        return map;
    }

}
