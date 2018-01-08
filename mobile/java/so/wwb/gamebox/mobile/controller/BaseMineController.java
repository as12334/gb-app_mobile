package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import org.springframework.ui.Model;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.mobile.App.model.BettingDetailsApp;
import so.wwb.gamebox.mobile.App.model.BettingInfoApp;
import so.wwb.gamebox.mobile.App.model.UserInfoApp;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.vo.BankListVo;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.enums.UserTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerWithdraw;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.PlayerRecommendAward;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.soul.commons.currency.CurrencyTool.formatCurrency;
import static so.wwb.gamebox.mobile.App.constant.AppConstant.*;

/**
 * Created by ed on 17-12-31.
 */
public class BaseMineController {
    private Log LOG = LogFactory.getLog(BaseMineController.class);

    /**
     * 获取我的个人数据
     */
    protected void getMineLinkInfo(Map<String, Object> userInfo, HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        Integer userId = SessionManager.getUserId();
        try {
            //总资产
            PlayerApiListVo playerApiListVo = new PlayerApiListVo();
            playerApiListVo.getSearch().setPlayerId(userId);
            playerApiListVo.setApis(Cache.getApi());
            playerApiListVo.setSiteApis(Cache.getSiteApi());
            double totalAssets = ServiceSiteTool.playerApiService().queryPlayerAssets(playerApiListVo);
            userInfo.put("totalAssets", totalAssets);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        //钱包余额
        userInfo.put("walletBalance", getWalletBalance(userId));

        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(userId);
        userInfo.put("withdrawAmount", ServiceSiteTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));

        //正在处理中转账金额(额度转换)
        PlayerTransferVo playerTransferVo = new PlayerTransferVo();
        playerTransferVo.getSearch().setUserId(userId);
        userInfo.put("transferAmount", ServiceSiteTool.playerTransferService().queryProcessAmount(playerTransferVo));

        //计算近7日收益（优惠金额）
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();
        vPreferentialRecodeListVo.getSearch().setUserId(userId);
        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo.getSearch().setCheckState(ActivityApplyCheckStatusEnum.SUCCESS.getCode());
        vPreferentialRecodeListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), PROMO_RECORD_DAYS));
        vPreferentialRecodeListVo.setPropertyName(VPreferentialRecode.PROP_PREFERENTIAL_VALUE);
        userInfo.put("preferentialAmount", ServiceSiteTool.vPreferentialRecodeService().sum(vPreferentialRecodeListVo));

        //银行卡信息
        List<UserBankcard> userBankcards = BankHelper.getUserBankcardList();
        Map<String, String> bankcardNumMap = new HashMap<>(1, 1f);
        for (UserBankcard userBankcard : userBankcards) {
            int length = userBankcard.getBankcardNumber().length();
            if (UserBankcardTypeEnum.BITCOIN.getCode().equals(userBankcard.getType())) {
                userInfo.put("btcNum", StringTool.overlay(userBankcard.getBankcardNumber(), "*", 0, length - 4));
            } else {
                bankcardNumMap.put(UserBankcard.PROP_BANK_NAME, userBankcard.getBankName());
                bankcardNumMap.put(UserBankcard.PROP_BANKCARD_NUMBER, StringTool.overlay(userBankcard.getBankcardNumber(), "*", 0, length - 4));
                userInfo.put("bankcard", bankcardNumMap);
            }
        }

        //推荐好友,昨日收益
        PlayerRecommendAwardListVo playerRecommendAwardListVo = new PlayerRecommendAwardListVo();
        playerRecommendAwardListVo.getSearch().setUserId(userId);
        playerRecommendAwardListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), RECOMMEND_DAYS));
        playerRecommendAwardListVo.getSearch().setEndTime(SessionManager.getDate().getToday());
        userInfo.put("recomdAmount", ServiceSiteTool.playerRecommendAwardService().searchRecomdAmount(playerRecommendAwardListVo, PlayerRecommendAward.PROP_REWARD_AMOUNT));

        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long number = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        VPlayerAdvisoryListVo listVo = new VPlayerAdvisoryListVo();
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
        String[] tags = tag.split(SplitRegex);
        for (VPlayerAdvisory vo : listVo.getResult()) {
            for (int i = 0; i < tags.length; i++) {
                if (tags[i] != "") {
                    VPlayerAdvisoryVo pa = new VPlayerAdvisoryVo();
                    pa.getSearch().setId(Integer.valueOf(tags[i]));
                    VPlayerAdvisoryVo vpaVo = ServiceSiteTool.vPlayerAdvisoryService().get(pa);
                    if (vo.getId().equals(vpaVo.getResult().getContinueQuizId()) || vo.getId().equals(vpaVo.getResult().getId())) {
                        vo.setIsRead(false);
                    }
                }
            }
        }
        userInfo.put("unReadCount", number + advisoryUnReadCount);
        //用户个人信息
        userInfo.put("username", StringTool.overlayString(sysUser.getUsername()));
        userInfo.put("avatarUrl", ImageTag.getThumbPathWithDefault(SessionManager.getDomain(request), sysUser.getAvatarUrl(), 46, 46, null));
        //有上次登录时间就不展示本次登录时间，否则展示本次登录时间
        if (sysUser.getLastLoginTime() != null) {
            userInfo.put("lastLoginTime", LocaleDateTool.formatDate(sysUser.getLastLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        } else if (sysUser.getLoginTime() != null) {
            userInfo.put("loginTime", LocaleDateTool.formatDate(sysUser.getLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        }
        userInfo.put("currency", getCurrencySign());
    }

    protected PlayerApiListVo initPlayerApiListVo(Integer userId) {
        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        return listVo;
    }

    protected void getAppUserInfo(HttpServletRequest request, SysUser user,UserInfoApp userInfoApp) {
        PlayerApiListVo listVo = initPlayerApiListVo(user.getId());
        VUserPlayer player = getVPlayer(user.getId());
        // API 余额
        userInfoApp.setApis(getSiteApis(listVo, request, false));
        if (player != null) {
            userInfoApp.setCurrSign(player.getCurrencySign());
            userInfoApp.setUsername(StringTool.overlayName(player.getUsername()));
            // 钱包余额
            Double balance = player.getWalletBalance();
            userInfoApp.setBalance(formatCurrency(balance == null ? 0.0d : balance));
        }
        // 总资产
        userInfoApp.setAssets(queryPlayerAssets(listVo, user.getId()));
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

    protected Double getWalletBalance(Integer userId) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(userId);
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        UserPlayer player = userPlayerVo.getResult();
        if (player == null) {
            return 0.0d;
        } else {
            Double balance = player.getWalletBalance();
            return balance == null ? 0.0d : balance;
        }
    }

    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 取款
     */
    protected void withdraw(Map map) {
        //获取稽核相关
        map.put("auditMap", getAuditMap());
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        hasBank(map);
    }

    /**
     * 判断银行卡是否存在
     * @param vo
     * @return
     */
    protected boolean checkCardIsExistsByUserId(UserBankcardVo vo) {
        String bankcardNumber = vo.getResult().getBankcardNumber();
        if(StringTool.isBlank(vo.getUserType())){
            //用户类型为空，不能判断银行卡是否存在，所以判断为不能添加
            LOG.info("保存银行卡{0}时，用户类型为空，不能判断银行卡是否存在，所以判断为不能添加",bankcardNumber);
            return true;
        }
        vo.getSearch().setBankcardNumber(bankcardNumber);
        vo.getSearch().setUserType(vo.getUserType());
        UserBankcard isExists = ServiceSiteTool.userBankcardService().cardIsExists(vo);
        if (isExists != null && isExists.getIsDefault() && !isExists.getUserId().equals(Integer.valueOf(SessionManagerBase.getUserId()))) {
            return true;
        }
        return false;
    }

    protected Integer getAgentId() {
        Integer agentId = SessionManagerBase.getUserId();
        if (UserTypeEnum.AGENT_SUB.getCode().equals(SessionManagerBase.getUser().getUserType())) {
            agentId = SessionManagerBase.getUser().getOwnerId();
        }
        return agentId;
    }

    /**
     * 判断是否含有用户取款卡号
     *
     * @param map
     * @return
     */
    public boolean hasBank(Map map) {
        // 是否设置收款账号
        Map<String, UserBankcard> bankcardMap = BankHelper.getUserBankcards();
        SysParam cashParam = ParamTool.getSysParam(SiteParamEnum.SETTING_WITHDRAW_TYPE_IS_CASH);
        SysParam bitParam = ParamTool.getSysParam(SiteParamEnum.SETTING_WITHDRAW_TYPE_IS_BITCOIN);
        boolean isCash = cashParam != null && "true".equals(cashParam.getParamValue());
        boolean isBit = bitParam != null && "true".equals(bitParam.getParamValue());
        map.put("isBit", isBit);
        map.put("isCash", isCash);
        map.put("bankcardMap", bankcardMap);
        boolean hasBank = true;
        if (MapTool.isEmpty(bankcardMap)) {
            hasBank = false;
        } else if (isCash && isBit && MapTool.isNotEmpty(bankcardMap)) {
            hasBank = true;
        } else if (isCash && bankcardMap.get(UserBankcardTypeEnum.TYPE_BANK) == null) {
            hasBank = false;
        } else if (isBit && bankcardMap.get(UserBankcardTypeEnum.TYPE_BTC) == null) {
            hasBank = false;
        }
        if (isCash) {
            bankcard(map);
        }

        map.put("hasBank", hasBank);
        return hasBank;
    }

    public void bankcard(Map map) {
        //model.addAttribute("validate", JsRuleCreator.create(AddBankcardForm.class));
        map.put("user", SessionManagerCommon.getUser());
        map.put("bankListVo", BankHelper.getBankListVo());
    }

    private double queryLotteryApiBalance() {
        PlayerApiVo apiVo = new PlayerApiVo();
        apiVo.getSearch().setApiId(Integer.valueOf(ApiProviderEnum.PL.getCode()));
        apiVo.getSearch().setPlayerId(SessionManagerBase.getUserId());
        double apiBalance = ServiceSiteTool.playerApiService().queryApiBalance(apiVo);

        return apiBalance;
    }

    /**
     * 取款稽核
     */
    public Map getAuditMap() {
        if (SessionManagerCommon.getUserId() == null) {
            throw new RuntimeException("玩家ID不存在");
        }
        PlayerTransactionVo vo = new PlayerTransactionVo();
        vo.setResult(new PlayerTransaction());
        vo.setPlayerId(SessionManagerCommon.getUserId());
        vo.setAuditDate(new Date());
        Map map = ServiceSiteTool.playerTransactionService().getTransactionMap(vo);
        return toAuditObjectMap(vo, map);
    }

    public Map<String, Object> toAuditObjectMap(PlayerTransactionVo transactionVo, Map auditMap) {
        double favorableSum = MapTool.getDouble(auditMap, "favorableSum");
        double depositSum = MapTool.getDouble(auditMap, "depositSum");
        double withdrawAmount = 0;
        if (auditMap.get("withdrawAmount") != null) {
            withdrawAmount = MapTool.getDouble(auditMap, "withdrawAmount");
        }
        double poundage = getPoundage(transactionVo, withdrawAmount);
        double actualWithdraw = withdrawAmount - depositSum - favorableSum - poundage;
        //用于显示用的手续用，不能用来计算
        String counterFee = ServiceSiteTool.playerWithdrawService().getDisplayCounterFee(transactionVo);

        Map<String, Object> result = new HashMap<>();
        result.put("actualWithdraw", actualWithdraw);
        result.put("deductFavorable", auditMap.get("favorableSum"));
        result.put("transactionNo", auditMap.get("transactionNo"));
        result.put("administrativeFee", depositSum);
        result.put("withdrawAmount", withdrawAmount);
        result.put("withdrawFeeMoney", poundage);
        result.put("counterFee", counterFee);
        boolean flag = MapTool.getBoolean(auditMap, "depositRecord");
        boolean flag2 = MapTool.getBoolean(auditMap, "favorableRecord");
        if (flag || flag2) {
            result.put("recordList", true);
        } else {
            result.put("recordList", false);
        }
        return result;
    }

    /**
     * 获取手续费
     */
    public double getPoundage(PlayerTransactionVo transactionVo, double withdrawAmount) {
        PlayerWithdrawVo withdrawVo = new PlayerWithdrawVo();
        withdrawVo.setResult(new PlayerWithdraw());
        withdrawVo.getSearch().setTransactionNo(transactionVo.getResult().getTransactionNo());
        withdrawVo.getResult().setWithdrawAmount(withdrawAmount);
        Double poundage = ServiceSiteTool.playerWithdrawService().getWithdrawFeeNum(transactionVo, withdrawVo, transactionVo.getPlayerId());
        return poundage == null ? 0 : poundage;
    }

    protected BettingDetailsApp buildBettingDetail(PlayerGameOrderVo playerGameOrderVo) {
        PlayerGameOrder gameOrder = playerGameOrderVo.getResult();
        BettingDetailsApp detailsApp = new BettingDetailsApp();
        detailsApp.setUserName(gameOrder.getUsername());
        detailsApp.setTerminal(gameOrder.getTerminal());
        detailsApp.setBetId(gameOrder.getBetId());
        detailsApp.setApiId(gameOrder.getApiId());
        detailsApp.setApiTypeId(gameOrder.getApiTypeId());
        detailsApp.setBetTime(gameOrder.getBetTime());
        detailsApp.setSingleAmount(gameOrder.getSingleAmount());
        detailsApp.setOrderState(gameOrder.getOrderState());
        detailsApp.setEffectiveTradeAmount(gameOrder.getEffectiveTradeAmount());
        detailsApp.setPayoutTime(gameOrder.getPayoutTime());
        detailsApp.setProfitAmount(gameOrder.getProfitAmount());
        detailsApp.setContributionAmount(gameOrder.getContributionAmount());
        detailsApp.setBetDetail(gameOrder.getBetDetail());
        detailsApp.setResultArray(playerGameOrderVo.getResultArray());
        return detailsApp;
    }

    /**
     * 查询是否已存在取款订单
     */
    protected boolean hasOrder() {
        if (SessionManagerCommon.getUserId() == null) {
            return true;
        }
        PlayerWithdrawVo vo = new PlayerWithdrawVo();
        vo.setResult(new PlayerWithdraw());
        vo.getSearch().setPlayerId(SessionManagerCommon.getUserId());
        Long result = ServiceSiteTool.playerWithdrawService().existPlayerWithdrawCount(vo);
        boolean hasOrder = result > 0;
        LOG.info("玩家{0}取款订单是否已存在{1}", SessionManagerCommon.getUserName(), hasOrder);
        return hasOrder;
    }

    /**
     * 验证是否余额冻结
     *
     * @param map
     * @return
     */
    public boolean hasFreeze(Map map) {
        UserPlayer player = getPlayer();
        map.put("player", player);
        return hasFreeze(map, player);
    }

    protected boolean hasFreeze(){
        UserPlayer player = getPlayer();
        if(player == null){
            return true;
        }
        boolean hasFreeze = player.getBalanceFreezeEndTime() != null
                && player.getBalanceFreezeEndTime().getTime() > SessionManagerCommon.getDate().getNow().getTime();
        LOG.info("取款玩家{0}是否冻结{1}", SessionManagerCommon.getUserName(), hasFreeze);
        return hasFreeze;
    }

    public boolean hasFreeze(Map map, UserPlayer player) {
        map.put("currencySign", getCurrencySign(SessionManagerCommon.getUser().getDefaultCurrency()));
        boolean hasFreeze = player.getBalanceFreezeEndTime() != null
                && player.getBalanceFreezeEndTime().getTime() > SessionManagerCommon.getDate().getNow().getTime();
        map.put("hasFreeze", hasFreeze);
        LOG.info("取款玩家{0}是否冻结{1}", SessionManagerCommon.getUserName(), hasFreeze);

        return hasFreeze;
    }

    private String getCurrencySign(String currency) {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(SessionManagerCommon.getUser().getDefaultCurrency());
        if (sysCurrency != null && StringTool.isNotBlank(sysCurrency.getCurrencySign())) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 余额是否充足
     * @return
     */
    protected boolean isBalanceAdequate(Map map){
        PlayerRank rank = getRank();
        UserPlayer player = getPlayer();
        if(rank == null || player == null){
            return true;
        }
        //取款时同步彩票余额
        double totalBalance = 0;
        if (ParamTool.isLotterySite()) {
            double apiBalance = queryLotteryApiBalance();
            totalBalance = apiBalance + totalBalance;
        }
        if(rank.getWithdrawMinNum() > player.getWalletBalance() + totalBalance){
            map.put("withdrawMinNum",formatCurrency(rank.getWithdrawMinNum()));
            return true;
        }
        return false;
    }

    /**
     * 获取银行列表
     * @return
     */
    public List<Map> bankList() {
        BankListVo bankListVo = BankHelper.getBankListVo();
        List<Map> maps = new ArrayList<>();
        Map map;
        for (Bank bank : bankListVo.getResult()) {
            map = new HashMap();
            map.put("value", bank.getBankName());
            map.put("text", bank.getBankShortName());
            maps.add(map);
        }
        return maps;
    }



    /**
     * 获取玩家层级
     *
     * @return 层级信息
     */
    private PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManagerCommon.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }

    /**
     * 验证是否今日取款是否达到上限
     *
     * @param rank
     * @return
     */
    private boolean isFull(Map map, PlayerRank rank) {
        //层级信息
        map.put("rank", rank);
        int count = get24HHasCount();
        if (rank.getIsWithdrawLimit() != null && rank.getIsWithdrawLimit() && rank.getWithdrawCount() != null && count >= rank.getWithdrawCount()) {
            // 已达取款次数上限
            map.put("isFull", true);
            LOG.info("取款玩家{0}取款次数已达到上限{1},当前玩家取款次数{2}", SessionManagerCommon.getUserName(), rank.getWithdrawCount(), count);
            return true;
        }
        if (rank.getWithdrawCount() != null) {
            // 还剩取款次数
            map.put("reminder", rank.getWithdrawCount() - count);
            LOG.info("取款玩家{0}取款次数{1},剩余取款次数{2}", SessionManagerCommon.getUserName(), count, rank.getWithdrawCount() - count);
        }
        return false;
    }

    protected boolean isFull(){
        PlayerRank rank = getRank();
        if(rank == null){
            return true;
        }

        int count = get24HHasCount();
        if (rank.getIsWithdrawLimit() != null && rank.getIsWithdrawLimit() && rank.getWithdrawCount() != null && count >= rank.getWithdrawCount()) {
            LOG.info("取款玩家{0}取款次数已达到上限{1},当前玩家取款次数{2}", SessionManagerCommon.getUserName(), rank.getWithdrawCount(), count);
            return true;
        }
        return false;
    }

    /**
     * 取得24H内已取款次数
     */
    private Integer get24HHasCount() {
        Date nowTime = SessionManagerCommon.getDate().getToday(); // 今天零时时间

        PlayerWithdrawVo playVo = new PlayerWithdrawVo();
        playVo.getSearch().setCreateTime(nowTime);
        playVo.getSearch().setPlayerId(SessionManagerCommon.getUserId());
        Long count = ServiceSiteTool.playerWithdrawService().searchPlayerWithdrawNum(playVo);
        count = (count == null) ? 0L : count;
        return count.intValue();
    }

    /**
     * 获取玩家信息
     *
     * @return 玩家信息
     */
    private UserPlayer getPlayer() {
        if (SessionManagerCommon.getUserId() == null) {
            return null;
        }
        UserPlayerVo playerVo = new UserPlayerVo();
        playerVo.getSearch().setId(SessionManagerCommon.getUserId());
        playerVo.setResult(new UserPlayer());
        playerVo = ServiceSiteTool.userPlayerService().get(playerVo);
        return playerVo.getResult();
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


    protected void initQueryDateForgetBetting(PlayerGameOrderListVo playerGameOrderListVo, int TIME_INTERVAL, int DEFAULT_TIME) {
        playerGameOrderListVo.setMinDate(SessionManager.getDate().addDays(TIME_INTERVAL));
        if (playerGameOrderListVo.getSearch().getBeginBetTime() == null) {
            playerGameOrderListVo.getSearch().setBeginBetTime(DateTool.addDays(SessionManager.getDate().getTomorrow(), -DEFAULT_TIME));//拿到明天在-1相当于拿到今天时间00:00:00
        }
        if (playerGameOrderListVo.getSearch().getEndBetTime() == null||playerGameOrderListVo.getSearch().getBeginBetTime().after(playerGameOrderListVo.getSearch().getEndBetTime())) {
            playerGameOrderListVo.getSearch().setEndBetTime(DateTool.addSeconds(SessionManager.getDate().getTomorrow(),-1));
        }
    }

    /**
     * 统计当前页数据
     * @param listVo
     */
    protected Map<String,Object> statisticsData(PlayerGameOrderListVo listVo, int TIME_INTERVAL, int DEFAULT_TIME) {
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDateForgetBetting(listVo,TIME_INTERVAL,DEFAULT_TIME);
        // 统计数据
        listVo.getSearch().setEndBetTime(DateTool.addSeconds(DateTool.addDays(listVo.getSearch().getEndBetTime(), 1),-1));
        Map map = ServiceSiteTool.playerGameOrderService().queryTotalPayoutAndEffect(listVo);
        map.put("currency", getCurrencySign());
        return map;
    }

    protected List<BettingInfoApp> buildBetting(List<PlayerGameOrder> list) {
        List<BettingInfoApp> bettingInfoAppList = new ArrayList<>();
        for (PlayerGameOrder order : list) {
            BettingInfoApp infoApp = new BettingInfoApp();
            infoApp.setId(order.getId());
            infoApp.setApiId(order.getApiId());
            infoApp.setGameId(order.getGameId());
            infoApp.setTerminal(order.getTerminal());
            infoApp.setBetTime(order.getBetTime());
            infoApp.setSingleAmount(order.getSingleAmount());
            infoApp.setOrderState(order.getOrderState());
            infoApp.setActionIdJson(order.getActionIdJson());
            infoApp.setProfitAmount(order.getProfitAmount());

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

    /**
     * 取款处理中/转账处理中的金额
     */
    protected void getFund(Map map) {
        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(SessionManager.getUserId());

        map.put("withdrawSum", ServiceSiteTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));
        if (!ParamTool.isLotterySite()) {
            //正在转账中金额
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
//            model.addAttribute("transferSum", playerTransferService().queryProcessAmount(playerTransferVo));
            map.put("transferSum", getPlayerTransferService().queryProcessAmount(playerTransferVo));
        }
    }

    private IPlayerTransferService playerTransferService;
    private IPlayerTransferService getPlayerTransferService() {
        if (playerTransferService == null)
            playerTransferService = ServiceSiteTool.playerTransferService();
        return playerTransferService;
    }

}
