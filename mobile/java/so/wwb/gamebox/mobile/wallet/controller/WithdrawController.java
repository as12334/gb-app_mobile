package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.math.NumberTool;
import org.soul.commons.validation.form.PasswordRule;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.validation.form.annotation.FormModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.security.AuthTool;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.Module;
import so.wwb.gamebox.model.master.enums.PlayerStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.FundTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.PlayerTransaction;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.web.ServiceToolBase;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.fund.controller.BaseWithdrawController;
import so.wwb.gamebox.web.fund.form.WithdrawForm;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 取款
 *
 * @author Fei
 * @date 2016-07-28
 */
@Controller
@RequestMapping("/wallet/withdraw")
public class WithdrawController extends BaseWithdrawController {
    @Override
    public String getWithdrawUri() {
        return "/withdraw/Index";
    }

    @Override
    public String getBinkBindUri() {
        return getWithdrawUri();
    }

    @RequestMapping("/index")
    @Token(generate = true)
    protected String index(Model model) {
        return withdraw(model);
    }


    /**
     * 提交取款
     */
    @RequestMapping("/submitWithdraw")
    @ResponseBody
    @Token(valid = true)
    public Map submitWithdraw(HttpServletRequest request, PlayerTransactionVo vo, Model model, @FormModel @Valid WithdrawForm form, BindingResult result) {
        return submitWithdraw(request, vo, model, result);
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
     * 远程验证取款金额
     */
    @RequestMapping("/checkWithdrawAmount")
    @ResponseBody
    public boolean checkWithdrawAmount(@RequestParam("withdrawAmount") String withdrawAmount) {
        if (!NumberTool.isNumber(withdrawAmount)) return false;

        UserPlayer player = getUser();
        if (player == null) return false;
        PlayerRank rank = getRank();//查询层级
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

        PlayerRank playerRank = getRank();
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
        Double poundage = getPoundage(amount, playerRank);
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
    public String showAuditLog(PlayerTransactionListVo listVo, Model model) {
        //查询列表前先进行集合一下
        getAuditMap();
        List<PlayerTransaction> list = getAuditLogList(listVo);
        initAuditData(SessionManager.getUserId(), list);
        model.addAttribute("list", list);
        model.addAttribute("user", SessionManager.getUser());
        return "/withdraw/WithdrawAudit";
    }

    private List<PlayerTransaction> getAuditLogList(PlayerTransactionListVo listVo) {
        listVo.getSearch().setPlayerId(SessionManager.getUser().getId());
        listVo.getSearch().setCreateTime(new Date());
        return ServiceTool.getPlayerTransactionService().searchAuditLog(listVo);
    }

    private void initAuditData(Integer playerId, List<PlayerTransaction> playerTransactions) {
        if (playerId == null || playerTransactions == null || playerTransactions.size() == 0) {
            return;
        }
        UserPlayer player = getPlayer();
        if (player == null || player.getRankId() == null) {
            return;
        }
        PlayerRankVo playerRankVo = new PlayerRankVo();
        playerRankVo.getSearch().setId(player.getRankId());
        playerRankVo = ServiceTool.playerRankService().get(playerRankVo);
        if (playerRankVo.getResult() == null || playerRankVo.getResult().getWithdrawNormalAudit() == null) {
            return;
        }
        for (PlayerTransaction transaction : playerTransactions) {
            if (transaction.getRechargeAuditPoints() == null && TransactionTypeEnum.DEPOSIT.getCode().equals(transaction.getTransactionType())
                    && !FundTypeEnum.ARTIFICIAL_DEPOSIT.getCode().equals(transaction.getFundType())) {
                Double rap = transaction.getTransactionMoney() * playerRankVo.getResult().getWithdrawNormalAudit();
                transaction.setRechargeAuditPoints(rap);
            }
        }
    }

}
