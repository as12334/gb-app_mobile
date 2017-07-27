package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.comet.vo.MessageVo;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysResourceListVo;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.common.Const;
import so.wwb.gamebox.model.common.notice.enums.CometSubscribeType;
import so.wwb.gamebox.model.master.dataRight.DataRightModuleType;
import so.wwb.gamebox.model.master.dataRight.po.SysUserDataRight;
import so.wwb.gamebox.model.master.dataRight.vo.SysUserDataRightVo;
import so.wwb.gamebox.model.master.enums.PlayerStatusEnum;
import so.wwb.gamebox.model.master.enums.RankFeeType;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.enums.UserTaskEnum;
import so.wwb.gamebox.model.master.fund.enums.FundTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.WithdrawStatusEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerWithdraw;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.player.po.*;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.tasknotify.vo.UserTaskReminderVo;
import so.wwb.gamebox.web.ServiceToolBase;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;

import java.util.*;

/**
 * 取款
 *
 * @author Fei
 * @date 2016-07-28
 */
@Controller
@RequestMapping("/wallet/withdraw")
public class WithdrawController extends WalletBaseController {
    private static final String WITHDRAW_URL = "/withdraw/Withdraw";
    //站长中心-玩家取款审核url
    private static final String MCENTER_PLAYER_WITHDRAW_URL = "fund/withdraw/withdrawList.html";

    @Override
    @Token(generate = true)
    protected String index(Model model) {
        model.addAttribute("channel", "withdraw");
        VUserPlayer player = getPlayer();
        model.addAttribute("player", getPlayer());

        boolean hasOrder = hasOrder();
        model.addAttribute("hasOrder", hasOrder);

        if (!hasOrder && player != null) {
            UserBankcard bankcard = BankHelper.getUserBankcard();
            if (bankcard != null) {
                // 24H内已取款次数
                Integer count = get24HHasCount();
                PlayerRank rank = getRank(getUser());

                if (rank != null) {
                    //有每日取款限制次数
                    if (rank.getIsWithdrawLimit() != null && rank.getIsWithdrawLimit() && rank.getWithdrawCount() != null && count < rank.getWithdrawCount()) {
                        setWithdrawPageData(rank, bankcard, player, model);
                        // 还剩取款次数
                        model.addAttribute("reminder", rank.getWithdrawCount() - count);
                    } else if(rank.getIsWithdrawLimit() != null && rank.getIsWithdrawLimit() && rank.getWithdrawCount() != null && count >= rank.getWithdrawCount()){
                        // 已达取款次数上限
                        model.addAttribute("isFull", true);
                    } else {
                        setWithdrawPageData(rank, bankcard, player, model);
                    }
                }
            } else {
                model.addAttribute("hasBankcard", "no");
            }
        }

        return WITHDRAW_URL;
    }

    private void setWithdrawPageData(PlayerRank rank, UserBankcard bankcard, VUserPlayer player, Model model) {
        // 层级上下限金额
        model.addAttribute("rank", rank);
        // 银行卡信息
        model.addAttribute("bankcard", bankcard);
        model.addAttribute("hasBankcard", "yes");
        // 取款稽核
        model.addAttribute("audit", getAuditMap());
    }

    /**
     * 查询是否已存在取款订单
     */
    private boolean hasOrder() {
        if (SessionManager.getUserId() == null) {
            return false;
        }
        PlayerWithdrawVo vo = new PlayerWithdrawVo();
        vo.setResult(new PlayerWithdraw());
        vo.getSearch().setPlayerId(SessionManager.getUserId());
        Long result = ServiceTool.playerWithdrawService().existPlayerWithdrawCount(vo);
        return result > 0;
    }

    /**
     * 取款稽核
     */
    private Map getAuditMap() {
        if (SessionManager.getUserId() == null) {
            throw new RuntimeException("玩家ID不存在");
        }
        PlayerTransactionVo vo = new PlayerTransactionVo();
        vo.setResult(new PlayerTransaction());
        vo.setPlayerId(SessionManager.getUserId());
        vo.setAuditDate(new Date());
        Map map = ServiceTool.getPlayerTransactionService().getTransactionMap(vo);
        return toAuditObjectMap(vo, map);
    }

    /**
     * 获取收款账号信息
     *
     * @return 账号信息
     */
    private UserBankcard getBankcard() {
        if (SessionManager.getUserId() == null) {
            return null;
        }
        UserBankcardVo bankcardVo = new UserBankcardVo();
        bankcardVo.setResult(new UserBankcard());
        bankcardVo.getSearch().setUserId(SessionManager.getUserId());
        bankcardVo.getSearch().setIsDefault(true);
        bankcardVo = ServiceTool.userBankcardService().search(bankcardVo);
        return bankcardVo.getResult();
    }

    /**
     * 获取玩家层级
     *
     * @param player 玩家
     * @return 层级信息
     */
    private PlayerRank getRank(UserPlayer player) {
        if (player == null || player.getRankId() == null) {
            return null;
        }
        PlayerRankVo rankVo = new PlayerRankVo();
        rankVo.setResult(new PlayerRank());
        rankVo.getSearch().setId(player.getRankId());
        rankVo = ServiceTool.playerRankService().get(rankVo);
        return rankVo.getResult();
    }

    /**
     * 提交取款
     */
    @RequestMapping("/submitWithdraw")
    @ResponseBody
    @Token(valid = true)
    public Map submitWithdraw(PlayerTransactionVo vo, Model model) {
        Map<String, Object> map = new HashMap<>();
        try {
            // 检测玩家状态
            if (checkPlayerStatus(map)) return map;

            boolean hasOrder = hasOrder();
            if (!hasOrder) {
                map = toWithdraw(vo);
            } else {
                map.put("state", false);
                map.put("msg", LocaleTool.tranMessage(Module.FUND, "withdraw.order.has"));
            }
        } catch (Exception ex) {
            LogFactory.getLog(this.getClass()).error(ex, "申请取款出错");
            map.put("state", false);
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        }
        if (map.get("state") != null && MapTool.getBoolean(map, "state")) {
            // 生成任务提醒
            taskRemider(map);
        }

        return map;
    }

    /**
     * 获取玩家状态
     */
    private String getPlayerStatus() {
        VUserPlayerVo playerVo = new VUserPlayerVo();
        playerVo.getSearch().setId(SessionManager.getUserId());
        playerVo = ServiceTool.vUserPlayerService().get(playerVo);
        VUserPlayer player = playerVo.getResult();
        return player.getPlayerStatus();
    }

    /**
     * 检测玩家状态
     */
    private boolean checkPlayerStatus(Map<String, Object> map) {
        String status = getPlayerStatus();
        if (!PlayerStatusEnum.ENABLE.getCode().equals(status)) {
            map.put("state", false);
            if (PlayerStatusEnum.DISABLE.getCode().equals(status)) {
                map.put("msg", LocaleTool.tranMessage(Module.FUND, "withdraw.apply.account.stop"));
            } else if (PlayerStatusEnum.ACCOUNTFREEZE.getCode().equals(status)) {
                map.put("msg", LocaleTool.tranMessage(Module.FUND, "withdraw.apply.account.freeze"));
            } else if (PlayerStatusEnum.BALANCEFREEZE.getCode().equals(status)) {
                map.put("msg", LocaleTool.tranMessage(Module.FUND, "withdraw.apply.balance.freeze"));
            }
            return true;
        }
        return false;
    }

    /**
     * 生成任务提醒
     */
    private void taskRemider(Map<String, Object> result) {
        PlayerWithdrawVo vo = new PlayerWithdrawVo();
        vo.setResult(new PlayerWithdraw());
        vo.getResult().setTransactionNo(MapTool.getString(result, "transactionNo"));
        tellerReminder(vo);//发送提醒消息给站长中心
    }

    private PlayerTransactionVo insertTransactionData(PlayerTransactionVo transactionVo) {
        SysUser user = SessionManager.getUser();
        if (user == null) {
            throw new RuntimeException("玩家不在线");
        }
        //添加交易表和取款表数据
        transactionVo.setSuccess(false);
        transactionVo.setSysUser(user);
        transactionVo.getResult().setOrigin(TransactionOriginEnum.MOBILE.getCode());
        transactionVo.setIpWithdraw(SessionManagerBase.getIpDb().getIp());
        transactionVo.setIpDictCode(SessionManagerBase.getIpDictCode());
        transactionVo = ServiceTool.getPlayerTransactionService().insertData(transactionVo);
        return transactionVo;
    }

    /**
     * 进入取款页面
     */
    private Map<String, Object> toWithdraw(PlayerTransactionVo vo) {
        Map<String, Object> result = new HashMap<>();

        UserPlayer player = getUser();
        PlayerRank rank = getRank(player);
        UserBankcard bankcard = getBankcard();
        // 取款金额
        Double amount = vo.getWithdrawAmount();
        if (player == null || rank == null || bankcard == null || amount == null) {
            result.put("state", false);
            return result;
        }
        //取款金额判断
        if (!checkWithdrawAmount(amount.toString())){
            result.put("state", false);
            return result;
        }
        vo.setUserBankcard(bankcard);
        // 手续费
        Double poundage = getPoundage(amount, rank);
        // 稽核
        Map auditMap = getAuditMap();
        Double adminFee = MapTool.getDouble(auditMap, "administrativeFee");
        adminFee = adminFee == null ? 0d : adminFee;
        Double deductFa = MapTool.getDouble(auditMap, "deductFavorable");
        deductFa = deductFa == null ? 0d : deductFa;
        // 实际取款金额
        Double actualAmount = amount - poundage - adminFee - deductFa;

        if (actualAmount <= 0) {
            result = setErrorMsg(result, "withdrawForm.withdrawAmountZero");
            return result;
        }

        // 玩家账户余额 >= 取款金额 + 手续费
        if (player.getWalletBalance() >= amount && (amount > poundage)) {
            setTransactionMoney(vo, amount, poundage, adminFee, deductFa, actualAmount);
            // 判断是否启用取款限制
            if (rank.getIsWithdrawLimit()) {
                // 24H内已取款次数
                Integer count = get24HHasCount();
                if (count < rank.getWithdrawCount()) {
                    vo = insertTransactionData(vo);
                    result.put("state", vo.isSuccess());
                    result.put("transactionNo", vo.getResult().getTransactionNo());
                } else {    // 已达取款次数上限
                    result = setErrorMsg(result, "withdrawForm.maxCount");
                    return result;
                }
            } else {
                vo = insertTransactionData(vo);
                result.put("state", vo.isSuccess());
                result.put("transactionNo", vo.getResult().getTransactionNo());
            }
        } else {
            result = setErrorMsg(result, "withdrawForm.balanceNotEnough");
            return result;
        }
        return result;
    }

    private Map setErrorMsg(Map<String, Object> result, String type) {
        result.put("state", false);
        result.put("type", type);
        return result;
    }

    /**
     * 设置取款金额手续费等
     */
    private void setTransactionMoney(PlayerTransactionVo vo, double amount, double poundage, double adminFee, double deductFa, double actualAmount) {
        vo.setPoundage(poundage);
        vo.setActualWithdrawAmount(actualAmount);
        vo.setResult(new PlayerTransaction());
        vo.getResult().setAdministrativeFee(adminFee);
        vo.getResult().setDeductFavorable(deductFa);
        vo.getResult().setPlayerId(SessionManager.getUser().getId());
        vo.getResult().setTransactionMoney(amount);
    }

    /**
     * 取得24H内已取款次数
     */
    private Integer get24HHasCount() {
        Date nowTime = SessionManager.getDate().getToday(); // 今天零时时间
        PlayerWithdrawVo vo = new PlayerWithdrawVo();
        vo.getSearch().setPlayerId(SessionManager.getUserId());
        vo.getSearch().setCreateTime(nowTime);
        Long count = ServiceTool.playerWithdrawService().searchPlayerWithdrawNum(vo);
        count = count == null ? 0L : count;
        return count.intValue();
    }

    /**
     * 创建任务提醒
     */
    private void createSchedule() {
        UserTaskReminderVo reminderVo = new UserTaskReminderVo();
        reminderVo.setTaskEnum(UserTaskEnum.PLAYERWITHDRAW);
        ServiceTool.userTaskReminderService().addTaskReminder(reminderVo);
    }

    private Map<String, Object> toAuditObjectMap(PlayerTransactionVo transactionVo, Map auditMap) {
        double favorableSum = MapTool.getDouble(auditMap, "favorableSum");
        double depositSum = MapTool.getDouble(auditMap, "depositSum");
        double withdrawAmount = 0;
        if (auditMap.get("withdrawAmount") != null) {
            withdrawAmount = MapTool.getDouble(auditMap, "withdrawAmount");
        }
        double poundage = getPoundage(transactionVo, withdrawAmount);
        double actualWithdraw = withdrawAmount - depositSum - favorableSum - poundage;
        //用于显示用的手续用，不能用来计算
        String counterFee = ServiceTool.playerWithdrawService().getDisplayCounterFee(transactionVo);

        Map<String, Object> result = new HashMap<>(8,1f);
        result.put("actualWithdraw", actualWithdraw);
        result.put("deductFavorable", auditMap.get("favorableSum"));
        result.put("transactionNo", auditMap.get("transactionNo"));
        result.put("administrativeFee", depositSum);
        result.put("withdrawAmount", withdrawAmount);
        result.put("withdrawFeeMoney", poundage);
        result.put("counterFee", "-.00".equals(counterFee) ? "0" : counterFee);
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
    private double getPoundage(PlayerTransactionVo transactionVo, double withdrawAmount) {
        PlayerWithdrawVo withdrawVo = new PlayerWithdrawVo();
        withdrawVo.setResult(new PlayerWithdraw());
        withdrawVo.getSearch().setTransactionNo(transactionVo.getResult().getTransactionNo());
        withdrawVo.getResult().setWithdrawAmount(withdrawAmount);
        Double poundage = ServiceTool.playerWithdrawService().getWithdrawFeeNum(transactionVo, withdrawVo, transactionVo.getPlayerId());
        return poundage == null ? 0 : poundage;
    }

    /**
     * 取款消息发送给站长中心
     */
    private void tellerReminder(PlayerWithdrawVo withdrawVo) {
        if (withdrawVo.getResult() == null || StringTool.isBlank(withdrawVo.getResult().getTransactionNo())) {
            return;
        }
        PlayerWithdrawVo newWithdrawVo = new PlayerWithdrawVo();
        newWithdrawVo.getSearch().setTransactionNo(withdrawVo.getResult().getTransactionNo());
        newWithdrawVo = ServiceTool.playerWithdrawService().search(newWithdrawVo);
        if (newWithdrawVo.getResult() == null || WithdrawStatusEnum.CANCELLATION_OF_ORDERS.getCode().equals(newWithdrawVo.getResult().getWithdrawStatus())) {
            return;
        }
        //推送消息给前端
        MessageVo message = new MessageVo();
        message.setSubscribeType(CometSubscribeType.MCENTER_WITHDRAW_REMINDER.getCode());
        PlayerWithdraw withdraw = newWithdrawVo.getResult();
        Map<String, Object> map = new HashMap<>(3);
        map.put("date", withdraw.getCreateTime());
        map.put("currency", StringTool.isBlank(SessionManager.getUser().getDefaultCurrency()) ? "" : SessionManager.getUser().getDefaultCurrency());
        map.put("type", withdraw.getWithdrawTypeParent());
        map.put("status", withdraw.getWithdrawStatus());
        map.put("amount", CurrencyTool.formatCurrency(withdraw.getWithdrawAmount()));
        map.put("id", withdraw.getId());
        message.setMsgBody(JsonTool.toJson(map));
        message.setSendToUser(true);
        message.setCcenterId(SessionManager.getSiteParentId());
        message.setSiteId(SessionManager.getSiteId());
        message.setMasterId(SessionManager.getSiteUserId());

        SysResourceListVo sysResourceListVo = new SysResourceListVo();
        sysResourceListVo.getSearch().setUrl(MCENTER_PLAYER_WITHDRAW_URL);
        List<Integer> list = ServiceTool.playerRechargeService().findUserIdByUrl(sysResourceListVo);
        list.add(Const.MASTER_BUILT_IN_ID);
        //判断账号是否可以查看该层级的记录 add by Bruce.QQ
        filterUnavailableSubAccount(withdrawVo, list);
        message.addUserIds(list);
        ServiceTool.messageService().sendToMcenterMsg(message);
        createSchedule();
    }

    private void filterUnavailableSubAccount(PlayerWithdrawVo withdrawVo, List<Integer> list) {
        SysUserDataRightVo sysUserDataRightVo = new SysUserDataRightVo();
        sysUserDataRightVo.getSearch().setModuleType(DataRightModuleType.PLAYERWITHDRAW.getCode());
        Map<Integer,List<SysUserDataRight>> udrMap = ServiceTool.sysUserDataRightService().searchDataRightsByModuleType(sysUserDataRightVo);
        Integer rankId = withdrawVo.getResult().getRankId();
        if(rankId == null) {
            UserPlayerVo userPlayerVo = new UserPlayerVo();
            userPlayerVo.getSearch().setId(SessionManager.getUserId());
            userPlayerVo = ServiceTool.userPlayerService().get(userPlayerVo);
            rankId = userPlayerVo.getResult().getRankId();
        }
        for (Iterator<Integer> iterator = list.iterator(); iterator.hasNext(); ) {
            Integer userId =  iterator.next();
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
     * 远程验证取款金额
     */
    @RequestMapping("/checkWithdrawAmount")
    @ResponseBody
    public boolean checkWithdrawAmount(@RequestParam("withdrawAmount") String withdrawAmount) {
        if (!NumberTool.isNumber(withdrawAmount)) return false;

        UserPlayer player = getUser();
        if (player == null) return false;
        PlayerRank rank = getRank(player);//查询层级
        if (rank == null) return false;

        double money = Double.valueOf(withdrawAmount);
        double balance = player.getWalletBalance() == null ? 0d : player.getWalletBalance();
        double maxNum = rank.getWithdrawMaxNum() == null ? 0d : rank.getWithdrawMaxNum();
        double minNum = rank.getWithdrawMinNum() == null ? 0d : rank.getWithdrawMinNum();
        return !(balance < money || maxNum < money || minNum > money);
    }

    /**
     * 计算取款各种费用
     *
     * @param withdrawAmount 存款金额
     * @return map
     */
    @RequestMapping("/withdrawFee")
    @ResponseBody
    public Map withdrawFee(@RequestParam("withdrawAmount") String withdrawAmount) {
        if (!NumberTool.isNumber(withdrawAmount)) {
            return null;
        }

        PlayerRank playerRank = getRank(getUser());
        if (playerRank == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>();

        double amount = Double.valueOf(withdrawAmount);
        Integer withdrawMinNum = playerRank.getWithdrawMinNum();
        Integer withdrawMaxNum = playerRank.getWithdrawMaxNum();
        if (withdrawMinNum != null && withdrawMaxNum != null) {
           if (!(playerRank.getWithdrawMinNum() <= amount && playerRank.getWithdrawMaxNum() >= amount)) {
               map.put("legalNum", LocaleTool.tranMessage(Module.FUND, "withdraw.apply.amont.renge", withdrawMinNum, withdrawMaxNum));
               return map;
           }
        }
        // 手续费
        Double poundage = getPoundage(amount,playerRank);
        Map auditMap = getAuditMap();
        Double administrativeFee = MapTool.getDouble(auditMap, "administrativeFee");
        Double deductFavorable = MapTool.getDouble(auditMap, "deductFavorable");
        double result = amount - poundage - administrativeFee - deductFavorable;
        if (amount <= poundage) {
            map.put("amountTooSmall", "true");
        } else {
            map.put("amountTooSmall", "false");
        }

        // 实际取款金额
        map.put("actualWithdraw", CurrencyTool.formatCurrency(result));
        map.put("actualLess0", result <= 0);
        map.put("poundage", CurrencyTool.formatCurrency(poundage));
        return map;
    }

    /**
     * 获取玩家信息
     *
     * @return 玩家信息
     */
    private UserPlayer getUser() {
        if (SessionManager.getUserId() == null) {
            return null;
        }
        UserPlayerVo playerVo = new UserPlayerVo();
        playerVo.setResult(new UserPlayer());
        playerVo.getSearch().setId(SessionManager.getUserId());
        playerVo = ServiceTool.userPlayerService().get(playerVo);
        return playerVo.getResult();
    }

    /**
     * 获取手续费
     *
     * @param withdrawAmount 取款金额
     * @return 手续费
     */
    private double getPoundage(Double withdrawAmount, PlayerRank rank) {
        Integer hasCount = 0;     // 设定时间内已取款次数
        Integer freeCount = rank.getWithdrawFreeCount();
        // 有设置取款次数限制
        if (rank.getWithdrawTimeLimit() != null && freeCount != null) {
            hasCount = getHasCount(rank);
        }

        Double poundage = 0.0d; // 手续费
        // 超过免手续费次数(n+1次：即已取款次数+当前取款次数)时需要扣除手续费
        if (hasCount >= (freeCount == null ? 0 : freeCount)) {
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
     * 设定时间内已取款次数
     */
    private Integer getHasCount(PlayerRank rank) {
        //返回多次取款次数，收取手续费
        if (SessionManager.getUserId() == null) {
            throw new RuntimeException("玩家ID不存在");
        }
        Date date = new Date();
        Date lastTime = DateTool.addHours(date, -rank.getWithdrawTimeLimit());
        PlayerWithdrawVo withdrawVo = new PlayerWithdrawVo();
        withdrawVo.setResult(new PlayerWithdraw());
        withdrawVo.getSearch().setPlayerId(SessionManager.getUserId());
        withdrawVo.setStartTime(lastTime);
        withdrawVo.setEndTime(date);
        Long count = ServiceTool.playerWithdrawService().searchTwoHoursPlayerWithdrawCount(withdrawVo);
        return count.intValue();
    }

    /**
     * 计算平台设定的手续费
     *
     * @param withdrawAmount 取款金额
     * @param rank           玩家层级信息
     * @return 手续费
     */
    private Double calPoundage(Double withdrawAmount, PlayerRank rank) {
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
     * 检测安全密码
     */
    @RequestMapping(value = "/checkSecurePwd")
    @ResponseBody
    public boolean checkSecurePwd(@RequestParam("code") String password) {
        String privilegeCode = SessionManager.getPrivilegeCode();
        SysUser user = SessionManager.getUser();
        String inputPassword = AuthTool.md5SysUserPermission(password, user.getUsername());
        return privilegeCode.equals(inputPassword);
    }

    /**
     * 检测密码强度
     */
    @RequestMapping(value = "/checkPwdStrength")
    @ResponseBody
    public boolean checkPwdStrength(@RequestParam("pwd") String password) {
        return !PasswordRule.isWeak(password);
    }

    @RequestMapping("/submitPwd")
    @ResponseBody
    public boolean submitPwd(@RequestParam("pwd") String password) {
        SysUserVo vo = new SysUserVo();
        SysUser user = SessionManagerCommon.getUser();
        vo.setResult(user);
        vo.setProperties(SysUser.PROP_PERMISSION_PWD, SysUser.PROP_SECPWD_ERROR_TIMES, SysUser.PROP_SECPWD_FREEZE_END_TIME);
        vo.getResult().setPermissionPwd(AuthTool.md5SysUserPermission(password, user.getUsername()));
        vo.getResult().setSecpwdErrorTimes(0);
        vo.getResult().setSecpwdFreezeEndTime(new Date());
        vo = ServiceToolBase.sysUserService().updateOnly(vo);

        if (SessionManagerCommon.isCurrentSiteMaster()) {
            vo._setDataSourceId(SessionManagerCommon.getSiteParentId());
            vo.getResult().setId(SessionManagerCommon.getSiteUserId());
            vo = ServiceToolBase.sysUserService().updateOnly(vo);
        }

        // 改变session中user的权限密码
        if (vo.isSuccess()) {
            SysUser sysUser = SessionManagerBase.getUser();
            sysUser.setPermissionPwd(vo.getResult().getPermissionPwd());
            sysUser.setSecpwdErrorTimes(0);
            SessionManagerBase.setUser(sysUser);
        }
        return vo.isSuccess();
    }

    @RequestMapping("/showAuditLog")
    public String showAuditLog(PlayerTransactionListVo listVo,Model model){
        //查询列表前先进行集合一下
        getAuditMap();
        List<PlayerTransaction> list = getAuditLogList(listVo);
        initAuditData(SessionManager.getUserId(),list);
        model.addAttribute("list",list);
        model.addAttribute("user",getUser(model));
        return "/withdraw/WithdrawAudit";
    }

    private List<PlayerTransaction> getAuditLogList(PlayerTransactionListVo listVo) {
        listVo.getSearch().setPlayerId(SessionManager.getUser().getId());
        listVo.getSearch().setCreateTime(new Date());
        return ServiceTool.getPlayerTransactionService().searchAuditLog(listVo);
    }

    private void initAuditData(Integer playerId,List<PlayerTransaction> playerTransactions){
        if(playerId==null||playerTransactions==null||playerTransactions.size()==0){
            return;
        }
        UserPlayer player = getPlayer2();
        if(player==null||player.getRankId()==null){
            return;
        }
        PlayerRankVo playerRankVo = new PlayerRankVo();
        playerRankVo.getSearch().setId(player.getRankId());
        playerRankVo = ServiceTool.playerRankService().get(playerRankVo);
        if(playerRankVo.getResult()==null||playerRankVo.getResult().getWithdrawNormalAudit()==null){
            return;
        }
        for(PlayerTransaction transaction : playerTransactions){
            if(transaction.getRechargeAuditPoints()==null&& TransactionTypeEnum.DEPOSIT.getCode().equals(transaction.getTransactionType())
                    &&!FundTypeEnum.ARTIFICIAL_DEPOSIT.getCode().equals(transaction.getFundType())){
                Double rap = transaction.getTransactionMoney() * playerRankVo.getResult().getWithdrawNormalAudit();
                transaction.setRechargeAuditPoints(rap);
            }
        }
    }

    private UserPlayer getPlayer2() {
        if(SessionManager.getUserId()==null){
            return null;
        }
        UserPlayerVo playerVo = new UserPlayerVo();
        playerVo.setResult(new UserPlayer());
        playerVo.getSearch().setId(SessionManager.getUserId());
        playerVo = ServiceTool.userPlayerService().get(playerVo);
        return playerVo.getResult();
    }

    private SysUser getUser(Model model) {
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setResult(new SysUser());
        sysUserVo.getSearch().setId(SessionManager.getUserId());
        sysUserVo = ServiceTool.sysUserService().get(sysUserVo);
        if (model != null) {
            model.addAttribute("sysUserVo", sysUserVo);
        }
        return sysUserVo.getResult();
    }

}
