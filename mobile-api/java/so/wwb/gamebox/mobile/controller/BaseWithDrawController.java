package so.wwb.gamebox.mobile.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.support._Module;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.init.BaseConfigManager;
import org.soul.web.session.SessionManagerBase;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.common.dubbo.ServiceTool;
import so.wwb.gamebox.mobile.app.model.AppPlayerRank;
import so.wwb.gamebox.mobile.app.model.AppUserBankCard;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.po.SysUserDataRight;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightListVo;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightVo;
import so.wwb.gamebox.model.master.enums.RankFeeType;
import so.wwb.gamebox.model.master.enums.UserTaskEnum;
import so.wwb.gamebox.model.master.fund.enums.WithdrawStatusEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerWithdraw;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.PlayerTransaction;
import so.wwb.gamebox.model.master.player.po.UserBankcard;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.PlayerApiVo;
import so.wwb.gamebox.model.master.player.vo.PlayerTransactionVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.master.tasknotify.vo.UserTaskReminderVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankCardTool;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;

import static org.soul.commons.currency.CurrencyTool.formatCurrency;
import static so.wwb.gamebox.mobile.app.constant.AppConstant.COMMON_PAYBANK_PHOTO;

/**
 * Created by ed on 18-1-21.
 */
public class BaseWithDrawController {
    private static final String WITHDRAW_INVALID_AMOUNT = "withdrawForm.withdrawAmountOver";
    private static final String WITHDRAW_AMOUNT_ZERO = "withdrawForm.withdrawAmountZero";
    private static final String WITHDRAW_AUDIT_LOG_URL = "/wallet/withdraw/showAuditLog.html";
    private Log LOG = LogFactory.getLog(BaseWithDrawController.class);

    /**
     * 取款
     */
    protected void withdraw(Map map, HttpServletRequest request) {
        //获取稽核相关
        map.put("auditMap", getAuditMap());
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        hasBank(map, request);
        //player
        UserPlayer player = getPlayer();
        double totalBalance = 0;
        if (ParamTool.isLotterySite()) {
            double apiBalance = queryLotteryApiBalance();
            totalBalance = apiBalance + totalBalance;
        }
        map.put("totalBalance", player.getWalletBalance() + totalBalance);
        map.put("currencySign", getCurrencySign(SessionManagerCommon.getUser().getDefaultCurrency()));
        map.put("auditLogUrl", WITHDRAW_AUDIT_LOG_URL);//查看稽核地址
        map.put("isSafePassword", isSafePassword());
        map.put("rank",getPlayerRank());
    }

    /**
     * 获取玩家层级取款金额最大小值
     * @return
     */
    private AppPlayerRank getPlayerRank(){
        PlayerRank rank = getRank();
        AppPlayerRank appRank = new AppPlayerRank();
        appRank.setWithdrawMinNum(rank.getWithdrawMinNum());
        appRank.setWithdrawMaxNum(rank.getWithdrawMaxNum());
        return appRank;
    }

    /**
     * 判断用户是否存在安全码
     *
     * @return
     */
    protected boolean isSafePassword() {
        SysUser user = SessionManager.getUser();
        if (StringTool.isBlank(user.getPermissionPwd())) {
            return false;
        }
        return true;
    }

    /**
     * 设置银行卡图片
     *
     * @param request
     * @param bankcard
     * @return
     */
    private String setBankPictureUrl(HttpServletRequest request, UserBankcard bankcard) {
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(), request.getServerName()));
        sb.append(COMMON_PAYBANK_PHOTO);
        sb.append(bankcard.getBankName());
        sb.append(".png");
        return sb.toString();
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

    /**
     * 判断是否含有用户取款卡号
     *
     * @param map
     * @return
     */
    public boolean hasBank(Map map, HttpServletRequest request) {
        // 是否设置收款账号
        Map<String, UserBankcard> bankcardMap = BankHelper.getUserBankcards();
        SysParam cashParam = ParamTool.getSysParam(SiteParamEnum.SETTING_WITHDRAW_TYPE_IS_CASH);
        SysParam bitParam = ParamTool.getSysParam(SiteParamEnum.SETTING_WITHDRAW_TYPE_IS_BITCOIN);
        boolean isCash = cashParam != null && "true".equals(cashParam.getParamValue());
        boolean isBit = bitParam != null && "true".equals(bitParam.getParamValue());
        map.put("isBit", isBit);
        map.put("isCash", isCash);
        map.put("bankcardMap", setUserBankCard(bankcardMap, request));
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

        map.put("hasBank", hasBank);
        return hasBank;
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

    private double queryLotteryApiBalance() {
        PlayerApiVo apiVo = new PlayerApiVo();
        apiVo.getSearch().setApiId(Integer.valueOf(ApiProviderEnum.PL.getCode()));
        apiVo.getSearch().setPlayerId(SessionManagerBase.getUserId());
        double apiBalance = ServiceSiteTool.playerApiService().queryApiBalance(apiVo);

        return apiBalance;
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
     * app隐藏银行卡信息
     *
     * @param bankcardMap
     * @return
     */
    private Map setUserBankCard(Map<String, UserBankcard> bankcardMap, HttpServletRequest request) {
        Map<String, AppUserBankCard> appMap = new HashMap<>();
        for (Map.Entry<String, UserBankcard> userMap : bankcardMap.entrySet()) {
            UserBankcard bank = userMap.getValue();
            AppUserBankCard appBank = new AppUserBankCard();
            appBank.setId(bank.getId());
            appBank.setUserId(bank.getUserId());
            appBank.setBankcardMasterName(StringTool.overlayName(bank.getBankcardMasterName()));
            appBank.setBankcardNumber(BankCardTool.overlayBankcard(bank.getBankcardNumber()));
            appBank.setCreateTime(bank.getCreateTime());
            appBank.setUseCount(bank.getUseCount());
            appBank.setUseStauts(bank.getUseStauts());
            appBank.setDefault(bank.getIsDefault());
            appBank.setBankName(bank.getBankName());
            appBank.setBankDeposit(bank.getBankDeposit());
            appBank.setCustomBankName(bank.getCustomBankName());
            appBank.setType(bank.getType());
            appBank.setBankUrl(setBankPictureUrl(request, bank));
            appMap.put(userMap.getKey(), appBank);
        }

        return appMap;
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

    protected boolean isInvalidAmount(PlayerTransactionVo vo, Map map) {
        Double withdrawAmount = vo.getWithdrawAmount();
        UserPlayer player = getPlayer();
        PlayerRank rank = getRank();
        if (withdrawAmount < rank.getWithdrawMinNum() || withdrawAmount > rank.getWithdrawMaxNum()) {
            String msg = LocaleTool.tranMessage(Module.FUND, WITHDRAW_INVALID_AMOUNT)
                    .replace("{min}", rank.getWithdrawMinNum() + "")
                    .replace("{max}", rank.getWithdrawMaxNum() + "")
                    .replace("{walletBalance}", player.getWalletBalance() + "");
            map.put("msg", msg);
            return true;
        }
        return false;
    }

    /**
     * 提交取款
     */
    public Map addWithdraw(HttpServletRequest request, PlayerTransactionVo vo) {
        Map map = MapTool.newHashMap();
        PlayerRank rank = getRank();

        try {
            map = toWithdraw(request, vo, rank);
        } catch (Exception ex) {
            LOG.error(ex, "申请取款出错");
            getErrorMsg(null, null);
        }
        if (map.get("state") != null && MapTool.getBoolean(map, "state")) {
            // 生成任务提醒
            tellerReminder(MapTool.getString(map, "transactionNo"));
            //钱包余额回收到API暂时不用。
            if (ParamTool.isLotterySite()) {
                transBalanceToLotteryApi();
            }
        }

        return map;
    }

    private void transBalanceToLotteryApi() {
        try {
            UserPlayerVo userPlayerVo = new UserPlayerVo();
            userPlayerVo.getSearch().setId(SessionManagerCommon.getUserId());
            userPlayerVo = ServiceSiteTool.userPlayerService().transBalanceToLotteryApi(userPlayerVo);
            LOG.info("更新API余额是否成功:{0}", userPlayerVo.isSuccess());
        } catch (Exception ex) {
            LOG.error(ex, "更新API余额出错");
        }
    }

    private Map getErrorMsg(String msg, String type) {
        Map<String, Object> map = new HashMap<>(4, 1f);
        map.put("state", false);
        if (StringTool.isBlank(msg)) {
            msg = LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED);
        }
        map.put("msg", msg);
        map.put("type", type);
        map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        return map;
    }

    public Map<String, Object> toWithdraw(HttpServletRequest request, PlayerTransactionVo vo, PlayerRank rank) {
        String userName = SessionManagerCommon.getUserName();
        // 取款金额
        Double withdrawAmount = vo.getWithdrawAmount();
        LOG.info("玩家{0}取款金额:{1}", userName, withdrawAmount);
        if (withdrawAmount == null) {
            return getErrorMsg(null, WITHDRAW_AMOUNT_ZERO);
        }
        // 手续费
        double poundage = getPoundage(withdrawAmount, rank);
        vo.setPoundage(poundage);
        LOG.info("计算出玩家{0}需要手续费:{1}", SessionManagerCommon.getUserName(), poundage);
        Map auditMap = getAuditMap();

        double administrativeFee = MapTool.getDouble(auditMap, "administrativeFee");
        double deductFavorable = MapTool.getDouble(auditMap, "deductFavorable");
        LOG.info("玩家{0}需扣除行政费用:{1}和扣除优惠:{2}", userName, administrativeFee, deductFavorable);
        DecimalFormat df = new DecimalFormat("#.00");
        poundage = Double.valueOf(df.format(poundage));
        administrativeFee = Double.valueOf(df.format(administrativeFee));
        deductFavorable = Double.valueOf(df.format(deductFavorable));
        double withdrawActualAmount = withdrawAmount - poundage - administrativeFee - deductFavorable;
        LOG.info("玩家{0}最终可取款金额:{1}", userName, withdrawAmount);
        vo.setActualWithdrawAmount(withdrawActualAmount);
        if (withdrawActualAmount <= 0) {
            return getErrorMsg(null, WITHDRAW_AMOUNT_ZERO);
        }
        try {
            vo = insertTransactionData(request, vo, administrativeFee, deductFavorable);
        } catch (Exception e) {
            LOG.error(e, "取款报错{0}", userName);
            vo.setSuccess(false);
        }
        if (vo.isSuccess()) {
            return getSuccessMsg(vo.getResult().getTransactionNo());
        }
        return getErrorMsg(null, null);
    }

    /**
     * 获取手续费
     *
     * @param withdrawAmount 取款金额
     * @return 手续费
     */
    public double getPoundage(Double withdrawAmount, PlayerRank rank) {
        Integer hasCount = 0;     // 设定时间内已取款次数
        Integer freeCount = rank.getWithdrawFreeCount();
        Double poundage = 0.0d; // 手续费
        // 有设置取款次数限制
        if (rank.getWithdrawTimeLimit() != null && freeCount != null) {
            hasCount = getHasCount(rank);
            // 超过免手续费次数(n+1次:即已取款次数+当前取款次数)时需要扣除手续费
            if (hasCount >= freeCount) {
                poundage = calPoundage(withdrawAmount, rank);
            }
        } else {
            poundage = calPoundage(withdrawAmount, rank);
        }

        // 手续费上限
        Double maxFee = rank.getWithdrawMaxFee();
        if (maxFee != null && poundage > maxFee) {
            poundage = maxFee;
        }

        return poundage;
    }

    /**
     * 计算平台设定的手续费
     *
     * @param withdrawAmount 取款金额
     * @param rank           玩家层级信息
     * @return 手续费
     */
    public Double calPoundage(Double withdrawAmount, PlayerRank rank) {
        Double ratioOrFee = rank.getWithdrawFeeNum();  // 比例或固定费用
        ratioOrFee = ratioOrFee == null ? 0d : ratioOrFee;

        Double poundage = 0d;
        if (RankFeeType.PROPORTION.getCode().equals(rank.getWithdrawFeeType())) {   // 比例收费
            poundage = ratioOrFee / 100 * withdrawAmount;
        } else if (RankFeeType.FIXED.getCode().equals(rank.getWithdrawFeeType())) { // 固定收费
            poundage = ratioOrFee;
        }
        return poundage;
    }

    /**
     * 设定时间内已取款次数
     */
    public Integer getHasCount(PlayerRank rank) {
        //返回多次取款次数，收取手续费
        if (SessionManagerCommon.getUserId() == null) {
            throw new RuntimeException("玩家ID不存在");
        }
        Date date = new Date();
        Date lastTime = DateTool.addHours(date, -rank.getWithdrawTimeLimit());
        PlayerWithdrawVo withdrawVo = new PlayerWithdrawVo();
        withdrawVo.setResult(new PlayerWithdraw());
        withdrawVo.getSearch().setPlayerId(SessionManagerCommon.getUserId());
        withdrawVo.setStartTime(lastTime);
        withdrawVo.setEndTime(date);
        Long count = ServiceSiteTool.playerWithdrawService().searchTwoHoursPlayerWithdrawCount(withdrawVo);
        return count.intValue();
    }

    private Map getSuccessMsg(String transactionNo) {
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("state", true);
        map.put("msg", LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));
        map.put("transactionNo", transactionNo);
        return map;
    }

    private PlayerTransactionVo insertTransactionData(HttpServletRequest request, PlayerTransactionVo vo, double administrativeFee, double deductFavorable) {
        SysUser user = SessionManagerCommon.getUser();
        if (user == null) {
            throw new RuntimeException("玩家不在线");
        }
        //添加交易表和取款表数据
        vo.setSuccess(false);
        vo.setSysUser(user);
        PlayerTransaction transaction = new PlayerTransaction();
        transaction.setAdministrativeFee(administrativeFee);
        transaction.setDeductFavorable(deductFavorable);
        transaction.setPlayerId(SessionManagerCommon.getUserId());
        transaction.setTransactionMoney(vo.getWithdrawAmount());
        transaction.setOrigin(SessionManagerCommon.withdrawTerminalOrigin(request));
        vo.setResult(transaction);
        vo.setIpWithdraw(SessionManagerBase.getIpDb().getIp());
        vo.setIpDictCode(SessionManagerBase.getIpDictCode());
        vo = ServiceSiteTool.playerTransactionService().insertData(vo);
        return vo;
    }


    /**
     * 余额是否充足
     *
     * @param player
     * @param withdrawAmount
     * @return
     */
    public boolean balanceEnough(UserPlayer player, double withdrawAmount) {
        if (ParamTool.isLotterySite()) {
            double apiBalance = queryLotteryApiBalance();
            Double walletBalance = player.getWalletBalance();
            if (walletBalance != null) {
                apiBalance += walletBalance.doubleValue();
            }
            return !(apiBalance < withdrawAmount);

        } else {
            return !(player.getWalletBalance() == null || player.getWalletBalance() < withdrawAmount);
        }
    }

    /**
     * 取款消息发送给站长中心
     */
    public void tellerReminder(String transactionNo) {
        if (StringTool.isBlank(transactionNo)) {
            return;
        }
        PlayerWithdrawVo withdrawVo = new PlayerWithdrawVo();
        withdrawVo.setResult(new PlayerWithdraw());
        withdrawVo.getSearch().setTransactionNo(transactionNo);
        withdrawVo = ServiceSiteTool.playerWithdrawService().search(withdrawVo);
        PlayerWithdraw withdraw = withdrawVo.getResult();
        if (withdraw == null || WithdrawStatusEnum.CANCELLATION_OF_ORDERS.getCode().equals(withdraw.getWithdrawStatus())) {
            return;
        }
        //推送消息给前端
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_WITHDRAW_REMINDER.getCode());
        Map<String, Object> map = new HashMap<>(6, 1f);
        map.put("date", withdraw.getCreateTime());
        map.put("currency", StringTool.isBlank(SessionManagerCommon.getUser().getDefaultCurrency()) ? "" : SessionManagerCommon.getUser().getDefaultCurrency());
        map.put("type", withdraw.getWithdrawTypeParent());
        map.put("status", withdraw.getWithdrawStatus());
        map.put("amount", CurrencyTool.formatCurrency(withdraw.getWithdrawAmount()));
        map.put("id", withdraw.getId());
        message.setMsgBody(JsonTool.toJson(map));
        message.setSendToUser(true);
        message.setCcenterId(SessionManagerCommon.getSiteParentId());
        message.setSiteId(SessionManagerCommon.getSiteId());
        message.setMasterId(SessionManagerCommon.getSiteUserId());

        SysUserDataRightListVo sysUserDataRightListVo = new SysUserDataRightListVo();
        sysUserDataRightListVo.getSearch().setUserId(SessionManagerCommon.getUserId());
        sysUserDataRightListVo.getSearch().setModuleType(DataRightModuleType.PLAYERWITHDRAW.getCode());
        List<Integer> list = ServiceSiteTool.sysUserDataRightService().searchPlayerDataRightEntityId(sysUserDataRightListVo);
        list.add(Const.MASTER_BUILT_IN_ID);

        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(withdrawVo, list);
        message.addUserIds(list);
        ServiceTool.messageService().sendToMcenterMsg(message);
        createSchedule(list);
    }

    /**
     * 创建任务提醒
     */
    public void createSchedule(List<Integer> subUserIds) {
        UserTaskReminderVo reminderVo = new UserTaskReminderVo();
        reminderVo.setUserIds(subUserIds);
        reminderVo.setTaskEnum(UserTaskEnum.PLAYERWITHDRAW);
        ServiceSiteTool.userTaskReminderService().addTaskReminder(reminderVo);
    }

    public void filterUnavailableSubAccount(PlayerWithdrawVo withdrawVo, List<Integer> list) {
        SysUserDataRightVo sysUserDataRightVo = new SysUserDataRightVo();
        sysUserDataRightVo.getSearch().setModuleType(DataRightModuleType.PLAYERWITHDRAW.getCode());
        Map<Integer, List<SysUserDataRight>> udrMap = ServiceSiteTool.sysUserDataRightService().searchDataRightsByModuleType(sysUserDataRightVo);
        Integer rankId = withdrawVo.getResult().getRankId();
        if (rankId == null) {
            UserPlayerVo userPlayerVo = new UserPlayerVo();
            userPlayerVo.getSearch().setId(SessionManagerCommon.getUserId());
            userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
            rankId = userPlayerVo.getResult().getRankId();
        }
        for (Iterator<Integer> iterator = list.iterator(); iterator.hasNext(); ) {
            Integer userId = iterator.next();
            List<SysUserDataRight> dataRights = udrMap.get(userId);
            if (dataRights == null || dataRights.size() == 0) {
                continue;
            }
            if (rankId != null) {
                boolean flag = true;
                for (SysUserDataRight sysUserDataRight : dataRights) {
                    if (rankId.equals(sysUserDataRight.getEntityId())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    iterator.remove();
                }
            }
        }
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
     * @return
     */
    protected boolean hasFreeze() {
        UserPlayer player = getPlayer();
        if (player == null) {
            return true;
        }
        boolean hasFreeze = player.getBalanceFreezeEndTime() != null
                && player.getBalanceFreezeEndTime().getTime() > SessionManagerCommon.getDate().getNow().getTime();
        LOG.info("取款玩家{0}是否冻结{1}", SessionManagerCommon.getUserName(), hasFreeze);
        return hasFreeze;
    }

    /**
     * 验证是否今日取款是否达到上限
     *
     * @return
     */
    protected boolean isFull() {
        PlayerRank rank = getRank();
        if (rank == null) {
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
     * 余额是否充足
     *
     * @return
     */
    protected boolean isBalanceAdequate(Map map) {
        PlayerRank rank = getRank();
        UserPlayer player = getPlayer();
        if (rank == null || player == null) {
            return true;
        }
        //取款时同步彩票余额
        double totalBalance = 0;
        if (ParamTool.isLotterySite()) {
            double apiBalance = queryLotteryApiBalance();
            totalBalance = apiBalance + totalBalance;
        }
        if (rank.getWithdrawMinNum() > player.getWalletBalance() + totalBalance) {
            map.put("withdrawMinNum", formatCurrency(rank.getWithdrawMinNum()));
            return true;
        }
        return false;
    }

    /**
     * 获取玩家层级
     *
     * @return 层级信息
     */
    protected PlayerRank getRank() {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.getSearch().setId(SessionManagerCommon.getUserId());
        return ServiceSiteTool.playerRankService().searchRankByPlayerId(sysUserVo);
    }
}
