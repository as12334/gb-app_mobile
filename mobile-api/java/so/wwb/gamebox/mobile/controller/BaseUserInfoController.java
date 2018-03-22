package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleDateTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.IpTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.spring.utils.SpringTool;
import org.soul.model.msg.notice.po.NoticeContactWay;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.app.model.MyUserInfo;
import so.wwb.gamebox.mobile.app.model.UserInfoApp;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.CacheBase;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SubSysCodeEnum;
import so.wwb.gamebox.model.common.SessionKey;
import so.wwb.gamebox.model.company.enums.GameStatusEnum;
import so.wwb.gamebox.model.company.po.Bank;
import so.wwb.gamebox.model.company.setting.po.Api;
import so.wwb.gamebox.model.company.setting.po.ApiI18n;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApi;
import so.wwb.gamebox.model.company.site.po.SiteApiI18n;
import so.wwb.gamebox.model.company.vo.BankListVo;
import so.wwb.gamebox.model.enums.ApiQueryTypeEnum;
import so.wwb.gamebox.model.enums.UserTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
import so.wwb.gamebox.model.master.enums.ContactWayTypeEnum;
import so.wwb.gamebox.model.master.enums.UserPlayerTransferStateEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.operation.po.VPreferentialRecode;
import so.wwb.gamebox.model.master.operation.vo.VPreferentialRecodeListVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.report.po.PlayerRecommendAward;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.api.IApiBalanceService;
import so.wwb.gamebox.web.bank.BankCardTool;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;

import static org.soul.commons.currency.CurrencyTool.formatCurrency;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.COMMON_PAYBANK_PHOTO;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.PROMO_RECORD_DAYS;

/**
 * Created by legend on 18-1-22.
 */
public class BaseUserInfoController {

    private Log LOG = LogFactory.getLog(BaseUserInfoController.class);

    /**
     * 获取我的个人数据
     */
    protected MyUserInfo getMineLinkInfo(HttpServletRequest request) {
        MyUserInfo userInfo = new MyUserInfo();
        userInfo.setAutoPay(SessionManager.isAutoPay());
        userInfo.setIsBit(ParamTool.isBit());
        userInfo.setIsCash(ParamTool.isCash());
        Integer userId = SessionManager.getUserId();
        //获取用户资产相关信息（总资产、钱包余额）
        getUserAssertInfo(userInfo, userId);
        //正在处理中取款金额
        userInfo.setWithdrawAmount(getDealWithdrawAmount(userId));
        //正在处理中转账金额(额度转换)
        // userInfo.setTransferAmount(getProcessTransferAmount(userId));
        //计算近7日收益（优惠金额）
        // userInfo.setPreferentialAmount(getRecent7DayFavorable(userId));
        //用户个人信息
        SysUser sysUser = SessionManager.getUser();
        //比特币信息
        if (userInfo.getIsBit()) {
            UserBankcard userBtc = BankHelper.getUserBankcard(userId, UserBankcardTypeEnum.TYPE_BTC);
            if (userBtc != null) {
                Map<String, String> bankcardNumMap = new HashMap<>(2, 1f);
                bankcardNumMap.put("btcNumber", BankCardTool.overlayBankcard(userBtc.getBankcardNumber()));//隐藏比特币账户
                bankcardNumMap.put("btcNum", StringTool.overlay(userBtc.getBankcardNumber(), "*", 0, userBtc.getBankcardNumber().length()));
                userInfo.setBtc(bankcardNumMap);
            }
        } else if (userInfo.getIsCash()) { //用户银行卡信息
            UserBankcard bankcard = BankHelper.getUserBankcard(userId, UserBankcardTypeEnum.TYPE_BANK);
            if (bankcard != null) {
                Map<String, String> bankcardNumMap = new HashMap<>(7, 1f);
                bankcardNumMap.put("bankcardMasterName", StringTool.overlayName(bankcard.getBankcardMasterName())); //隐藏部分真实姓名
                String bankName = LocaleTool.tranMessage(Module.COMMON, "bankname." + bankcard.getBankName()); //将ICBC转换工商银行
                bankcardNumMap.put("bankName", bankName);
                bankcardNumMap.put("bankNameCode", bankcard.getBankName());
                bankcardNumMap.put("bankUrl", setBankPictureUrl(request, bankcard));
                bankcardNumMap.put("bankcardNumber", BankCardTool.overlayBankcard(bankcard.getBankcardNumber()));
                bankcardNumMap.put("bankDeposit", bankcard.getBankDeposit());
                bankcardNumMap.put("realName", sysUser.getRealName());  //真实姓名
                userInfo.setBankcard(bankcardNumMap);
            }
        }
        //推荐好友,昨日收益
        //userInfo.setRecomdAmount(getYesterdayRecommend(userId));
        userInfo.setUsername(StringTool.overlayString(sysUser.getUsername()));
        userInfo.setAvatarUrl(sysUser.getAvatarUrl());
        //有上次登录时间就不展示本次登录时间，否则展示本次登录时间
        if (sysUser.getLastLoginTime() != null) {
            userInfo.setLastLoginTime(LocaleDateTool.formatDate(sysUser.getLastLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        } else if (sysUser.getLoginTime() != null) {
            userInfo.setLastLoginTime(LocaleDateTool.formatDate(sysUser.getLoginTime(), CommonContext.getDateFormat().getDAY_SECOND(), SessionManager.getTimeZone()));
        }
        userInfo.setCurrency(getCurrencySign());
        userInfo.setRealName(sysUser.getRealName());
        return userInfo;
    }

    /**
     * 获取用户资产信息（总资产、钱包余额）
     *
     * @param userInfo
     */
    public void getUserAssertInfo(MyUserInfo userInfo, Integer userId) {
        PlayerApiListVo playerApiListVo = new PlayerApiListVo();
        playerApiListVo.getSearch().setPlayerId(userId);
        try {
            playerApiListVo = ServiceSiteTool.playerApiService().fundRecord(playerApiListVo);
            userInfo.setTotalAssets(playerApiListVo.getTotalAssets());
            userInfo.setWalletBalance(playerApiListVo.getUserPlayer().getWalletBalance());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        List<PlayerApi> playerApis = playerApiListVo.getResult();
        if (CollectionTool.isEmpty(playerApis)) {
            return;
        }
        Map<String, Api> apiMap = Cache.getApi();
        Map<String, SiteApi> siteApiMap = Cache.getSiteApi();
        Map<String, ApiI18n> apiI18nMap = Cache.getApiI18n();
        Map<String, SiteApiI18n> siteApiI18nMap = Cache.getSiteApiI18n();
        Map<String, Object> map;
        for (PlayerApi api : playerApis) {
            map = new HashMap<>(4, 1f);
            String apiId = api.getApiId().toString();
            map.put("apiId", apiId);
            map.put("apiName", getApiName(apiI18nMap, siteApiI18nMap, apiId));
            map.put("balance", api.getMoney() == null ? 0.00 : CurrencyTool.formatCurrency(api.getMoney()));
            map.put("status", getApiStatus(apiMap, siteApiMap, apiId));
            userInfo.addApi(map);
        }
    }

    private String getApiStatus(Map<String, Api> apiMap, Map<String, SiteApi> siteApiMap, String apiId) {
        Api api = apiMap.get(apiId);
        SiteApi siteApi = siteApiMap.get(apiId);
        String disable = GameStatusEnum.DISABLE.getCode();
        if (api == null || siteApi == null) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.disable");
        }
        String apiStatus = api.getSystemStatus();
        String siteApiStatus = siteApi.getSystemStatus();
        if (disable.equals(apiStatus) || disable.equals(siteApiStatus)) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.disable");
        }
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        if (maintain.equals(apiStatus) || maintain.equals(siteApiStatus)) {
            return LocaleTool.tranMessage(Module.FUND, "transfer.api.maintain.transferable");
        }
        return "";//app判断空是正常
    }

    /**
     * 获取api名称
     *
     * @param apiI18nMap
     * @param siteApiI18nMap
     * @param apiId
     * @return
     */
    private String getApiName(Map<String, ApiI18n> apiI18nMap, Map<String, SiteApiI18n> siteApiI18nMap, String apiId) {
        SiteApiI18n siteApiI18n = siteApiI18nMap.get(apiId);
        String apiName = null;
        if (siteApiI18n != null) {
            apiName = siteApiI18n.getName();
        }
        if (StringTool.isNotBlank(apiName)) {
            return apiName;
        }
        ApiI18n apiI18n = apiI18nMap.get(apiId);
        if (apiI18n != null) {
            apiName = apiI18n.getName();
        }
        return apiName;
    }

    /**
     * 正在处理中取款金额
     *
     * @param userId
     * @return
     */
    private Double getDealWithdrawAmount(Integer userId) {
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(userId);
        return ServiceSiteTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo);
    }

    /**
     * 计算近7日优惠
     *
     * @param userId
     * @return
     */
    private Double getRecent7DayFavorable(Integer userId) {
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();
        vPreferentialRecodeListVo.getSearch().setUserId(userId);
        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo.getSearch().setCheckState(ActivityApplyCheckStatusEnum.SUCCESS.getCode());
        vPreferentialRecodeListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), PROMO_RECORD_DAYS));
        vPreferentialRecodeListVo.setPropertyName(VPreferentialRecode.PROP_PREFERENTIAL_VALUE);
        Number preferential = ServiceSiteTool.vPreferentialRecodeService().sum(vPreferentialRecodeListVo);
        if (preferential == null) {
            return 0d;
        } else {
            return preferential.doubleValue();
        }
    }

    /**
     * 获取昨日推荐奖励金额
     *
     * @param userId
     * @return
     */
    private Double getYesterdayRecommend(Integer userId) {
        PlayerRecommendAwardListVo playerRecommendAwardListVo = new PlayerRecommendAwardListVo();
        playerRecommendAwardListVo.getSearch().setUserId(userId);
        playerRecommendAwardListVo.getSearch().setStartTime(SessionManager.getDate().getYestoday());
        playerRecommendAwardListVo.getSearch().setEndTime(SessionManager.getDate().getToday());
        Number recommend = ServiceSiteTool.playerRecommendAwardService().searchRecomdAmount(playerRecommendAwardListVo, PlayerRecommendAward.PROP_REWARD_AMOUNT);
        if (recommend == null) {
            return 0d;
        } else {
            return recommend.doubleValue();
        }
    }

    /**
     * 正在处理中转账金额
     *
     * @param userId
     * @return
     */
    private Double getProcessTransferAmount(Integer userId) {
        PlayerTransferVo playerTransferVo = new PlayerTransferVo();
        playerTransferVo.getSearch().setUserId(userId);
        return ServiceSiteTool.playerTransferService().queryProcessAmount(playerTransferVo);
    }

    private String setBankPictureUrl(HttpServletRequest request, UserBankcard bankcard) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()));
        sb.append(COMMON_PAYBANK_PHOTO);
        sb.append(bankcard.getBankName());
        sb.append(".png");
        return sb.toString();
    }


    protected void getAppUserInfo(HttpServletRequest request, SysUser user, UserInfoApp userInfoApp) {
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


    protected PlayerApiListVo initPlayerApiListVo(Integer userId) {
        PlayerApiListVo listVo = new PlayerApiListVo();
        listVo.getSearch().setPlayerId(userId);
        listVo.setApis(Cache.getApi());
        listVo.setSiteApis(Cache.getSiteApi());
        return listVo;
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


    /**
     * 获取银行列表
     *
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

    /**
     * 根据账号查找玩家信息
     */
    private SysUser getUserByUsername(String username, String subSysCode) {
        SysUserVo userVo = new SysUserVo();
        userVo.getSearch().setUsername(username);
        userVo.getSearch().setSubsysCode(subSysCode);
        userVo.getSearch().setSiteId(SessionManagerCommon.getSiteId());
        return ServiceTool.sysUserService().findByUsername(userVo);
    }

    /**
     * 账号是否冲突
     */
    public boolean isConflict(UserPlayerTransferVo vo) {
        String username = vo.getResult().getPlayerAccount();
        if (StringTool.isNotBlank(username))
            username = username.toLowerCase();
        SysUser user = getUserByUsername(username, SubSysCodeEnum.PCENTER.getCode());
        return user != null;
    }

    /**
     * 是否开启验证码
     */
    private void isOpenCaptcha(boolean isOpen) {
        SessionManagerCommon.setAttribute(SessionKey.S_IS_CAPTCHA_CODE, isOpen);
    }

    /**
     * 验证真实姓名
     *
     * @param vo
     * @param map
     * @param inputName
     * @param player
     */
    protected void checkName(UserPlayerTransferVo vo, Map<String, Object> map, String inputName, UserPlayerTransfer player) {
        if (player != null) {
            // 导入的玩家账号与现有库玩家账号冲突
            map.put("conflict", isConflict(vo));

            boolean isNameOk = StringTool.equals(inputName, player.getRealName());
            map.put("nameSame", isNameOk);
            if (isNameOk) {
                isOpenCaptcha(false);
            } else {
                boolean hasName = StringTool.isNotBlank(player.getRealName());
                isOpenCaptcha(hasName);
                map.put("nameSame", !hasName);
            }

            map.put("playerAccount", player.getPlayerAccount());
        }
        SessionManagerCommon.setAttribute("tempPass", vo.getTempPass());
        SessionManagerCommon.setAttribute("tempCode", vo.getTempCode());
    }

    /**
     * 老玩家站点前台导入
     *
     * @param vo
     * @param request
     * @return
     */
    public Boolean importOldPlayer(UserPlayerTransferVo vo, HttpServletRequest request) {
        UserPlayerVo playerVo = new UserPlayerVo();
        String username = vo.getResult().getPlayerAccount();
        List<UserPlayerTransfer> userPlayerTransfers = getUserPlayerTransfers(vo);
        if (CollectionTool.isEmpty(userPlayerTransfers)) {
            return false;
        }
        UserPlayerTransfer transfer = userPlayerTransfers.get(0);
        if (UserPlayerTransferStateEnum.ACTIVATE.getCode().equals(transfer.getIsActive())) {
            return false;
        }
        playerVo = importPlayerData(vo, request, playerVo, username, transfer);
        return playerVo.isSuccess();
    }

    private UserPlayerVo importPlayerData(UserPlayerTransferVo vo, HttpServletRequest request, UserPlayerVo playerVo, String username, UserPlayerTransfer transfer) {
        if (StringTool.isNotBlank(username)) {
            username = username.toLowerCase();
        }
        SysUser agent = getUserByUsername(transfer.getAgent(), SubSysCodeEnum.MCENTER_AGENT.getCode());

        // 设置账户基本信息
        playerVo.setSysUser(setSysUser(vo, transfer, request, username, agent));
        // 设置玩家信息
        playerVo.setResult(setPlayer(transfer, agent));
        // 设置联系方式
        playerVo.setNoticeContactWays(setContactWay(transfer));
        // 设置银行卡
        playerVo.setUserBankcard(setBankCard(transfer));
        // 修改导入表玩家状态
        playerVo.setPlayerTransfer(transfer);

        playerVo = ServiceSiteTool.userPlayerService().importPlayer(playerVo);
        return playerVo;
    }

    /**
     * 设置银行卡信息
     */
    private UserBankcard setBankCard(UserPlayerTransfer pt) {
        String cardNo = pt.getBankcardNumber();
        if (StringTool.isNotBlank(cardNo)) {
            UserBankcard card = new UserBankcard();
            card.setBankcardNumber(cardNo);
            card.setBankDeposit(pt.getBankDeposit());
            card.setCreateTime(SessionManagerCommon.getDate().getNow());
            card.setType(UserBankcardTypeEnum.BANK.getCode());
            return card;
        }
        return null;
    }

    /**
     * 设置联系方式
     */
    private List<NoticeContactWay> setContactWay(UserPlayerTransfer pt) {
        List<NoticeContactWay> ways = new ArrayList<>(2);
        if (StringTool.isNotBlank(pt.getMobilePhone())) {
            NoticeContactWay mobile = new NoticeContactWay();
            mobile.setContactType(ContactWayTypeEnum.MOBILE.getCode());
            mobile.setContactValue(pt.getMobilePhone());
            ways.add(mobile);
        }
        if (StringTool.isNotBlank(pt.getEmail())) {
            NoticeContactWay email = new NoticeContactWay();
            email.setContactType(ContactWayTypeEnum.EMAIL.getCode());
            email.setContactValue(pt.getEmail());
            ways.add(email);
        }
        return ways;
    }

    /**
     * 设置玩家信息
     */
    private UserPlayer setPlayer(UserPlayerTransfer pt, SysUser agent) {
        UserPlayer player = new UserPlayer();
        try {
            player.setRankId(pt.getPlayerRankId());
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).warn("导入玩家验证时转换层级{0}出错", pt.getPlayerRank());
        }

        player.setTotalAssets(pt.getAccountBalance() == null ? 0d : pt.getAccountBalance());
        player.setWalletBalance(player.getTotalAssets());

        if (agent != null) {
            player.setUserAgentId(agent.getId());
            player.setAgentName(agent.getUsername());
            UserAgentVo agentVo = new UserAgentVo();
            agentVo.getSearch().setId(agent.getId());
            UserAgent userAgent = ServiceSiteTool.userAgentService().get(agentVo).getResult();
            if (player.getRankId() == null) {
                player.setRankId(userAgent.getPlayerRankId());
            }

        } else {
            player.setUserAgentId(-2);
            if (player.getRankId() == null) {
                player.setRankId(-1);
            }
            agent = new SysUser();
            agent.setOwnerId(-1);
        }

        SysUser topAgent = getTopAgent(agent);
        if (topAgent != null) {
            player.setGeneralAgentId(topAgent.getId());
            player.setGeneralAgentName(topAgent.getUsername());
        }


        PlayerRankVo vo = new PlayerRankVo();
        vo.getSearch().setId(player.getRankId());
        vo = ServiceSiteTool.playerRankService().get(vo);
        if (vo.getResult() != null) {
            player.setRakebackId(vo.getResult().getRakebackId());
        }

        return player;
    }

    private SysUser getTopAgent(SysUser agent) {
        if (agent == null || agent.getOwnerId() == null) {
            return null;
        }
        SysUserVo topAgentVo = new SysUserVo();
        topAgentVo.getSearch().setId(agent.getOwnerId());
        topAgentVo = ServiceTool.sysUserService().get(topAgentVo);
        return topAgentVo.getResult();
    }

    /**
     * 设置玩家基本信息
     */
    private SysUser setSysUser(UserPlayerTransferVo vo, UserPlayerTransfer pt, HttpServletRequest request, String username, SysUser agent) {
        SysUser user = new SysUser();
        user.setUsername(username);
        String password = vo.getTempPass();
        if (StringTool.isBlank(password)) {
            password = (String) SessionManagerCommon.getAttribute("tempPass");
        }
        user.setPassword(AuthTool.md5SysUserPassword(password, username));
        user.setRealName(pt.getRealName());
        user.setCreateTime(SessionManagerCommon.getDate().getNow());
        user.setRegisterIp(IpTool.ipv4StringToLong(ServletTool.getIpAddr(request)));
        user.setRegisterIpDictCode(SessionManagerCommon.getIpDictCode());
        user.setDefaultLocale(String.valueOf(SessionManagerCommon.getLocale()));
        user.setDefaultTimezone(SessionManagerCommon.getTimeZone().getDisplayName());
        user.setSiteId(SessionManagerCommon.getSiteId());
        String domain = request.getServerName();
        user.setUseLine(domain);
        user.setRegisterSite(domain.startsWith("www.") ? domain.substring(4) : domain);
        user.setPasswordLevel(vo.getPassLevel());
        if (agent != null) {
            user.setOwnerId(agent.getId());
            user.setDefaultCurrency(agent.getDefaultCurrency());
        } else {
            user.setOwnerId(-2);
        }
        return user;
    }

    /**
     * 查询账号是否存在
     */
    public List<UserPlayerTransfer> getUserPlayerTransfers(UserPlayerTransferVo vo) {
        UserPlayerTransferListVo listVo = new UserPlayerTransferListVo();
        listVo.setSearch(vo.getSearch());
        listVo.getSearch().setIsActive(UserPlayerTransferStateEnum.INACTIVE.getCode());
        listVo = ServiceSiteTool.userPlayerTransferService().search(listVo);
        return listVo.getResult();
    }


    /**
     * 判断银行卡是否存在
     *
     * @param vo
     * @return
     */
    protected boolean checkCardIsExistsByUserId(UserBankcardVo vo) {
        String bankcardNumber = vo.getResult().getBankcardNumber();
        if (StringTool.isBlank(vo.getUserType())) {
            //用户类型为空，不能判断银行卡是否存在，所以判断为不能添加
            LOG.info("保存银行卡{0}时，用户类型为空，不能判断银行卡是否存在，所以判断为不能添加", bankcardNumber);
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
     * 获取额度转换api
     *
     * @return
     */
    protected Map getTransferApi() {
        Map map = new HashMap();
        //玩家信息
        MyUserInfo userInfo = new MyUserInfo();
        getUserAssertInfo(userInfo, SessionManager.getUserId());
        map.put("apis", userInfo.getApis());
        map.put("currency",userInfo.getCurrency());
        if (!SessionManagerCommon.isAutoPay()) {
            //正在处理中转账金额(额度转换)
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
            map.put("transferPendingAmount", ServiceSiteTool.playerTransferService().queryProcessAmount(playerTransferVo));
        }
        map.put("select", queryApis());
        map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
        return map;
    }

    /**
     * 获取转出转入账户api
     *
     * @return
     */
    private List<Map> queryApis() {
        List<Map> transableApis = new ArrayList<>();
        Map<String, Api> apis = Cache.getApi();
        Map<String, SiteApi> siteApis = Cache.getSiteApi();
        String disable = GameStatusEnum.DISABLE.getCode();
        String maintain = GameStatusEnum.MAINTAIN.getCode();
        Api api;
        SiteApi siteApi;
        Map map = new HashMap();
        map.put("text", "我的钱包");
        map.put("value", "wallet");
        transableApis.add(map);
        for (String apiId : siteApis.keySet()) {
            api = apis.get(apiId);
            //额度转换的ｂｓｇ不支持
            if (ApiProviderEnum.BSG.getCode().equals(apiId)) {
                continue;
            }
            siteApi = siteApis.get(apiId);
            if (api != null
                    && !StringTool.equals(api.getSystemStatus(), disable)
                    && !StringTool.equals(siteApi.getSystemStatus(), disable)
                    && !StringTool.equals(api.getSystemStatus(), maintain)
                    && !StringTool.equals(siteApi.getSystemStatus(), maintain)
                    && (api.getTransferable() == null || api.getTransferable())) {
                Map apiMap = new HashMap();
                apiMap.put("text", CacheBase.getSiteApiName(apiId));
                apiMap.put("value", api.getId());
                transableApis.add(apiMap);
            }
        }

        return transableApis;
    }
}
