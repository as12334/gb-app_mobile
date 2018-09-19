package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.collections.CollectionQueryTool;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.support._Module;
import org.soul.model.session.SessionKey;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceActivityTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.iservice.master.fund.IPlayerRechargeService;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.TerminalEnum;
import so.wwb.gamebox.model.common.MessageI18nConst;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.content.vo.PayAccountVo;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.RankFeeType;
import so.wwb.gamebox.model.master.fee.po.RechargeFeeSchema;
import so.wwb.gamebox.model.master.fee.vo.RechargeFeeSchemaVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeListVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageListVo;
import so.wwb.gamebox.model.master.operation.vo.VActivityMessageVo;
import so.wwb.gamebox.model.master.player.po.PlayerRank;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.po.VUserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.master.player.vo.VUserPlayerVo;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import java.util.*;


/**
 * Created by bruce on 16-12-10.
 */
public class BaseDepositController extends BaseCommonDepositController {

    @RequestMapping("/getPlayer")
    @ResponseBody
    public VUserPlayer getPlayer() {
        Integer userId = SessionManager.getUserId();
        if (userId != null) {
            VUserPlayerVo vo = new VUserPlayerVo();
            vo.getSearch().setId(userId);
            VUserPlayer player = ServiceSiteTool.vUserPlayerService().queryPlayer4App(vo);
            if (player != null) {
                player.setCurrencySign(getCurrencySign(player.getDefaultCurrency()));
            }
            return player;
        }
        return null;
    }

    /**
     * 获取货币标志
     */
    private String getCurrencySign(String defaultCurrency) {
        if (StringTool.isNotBlank(defaultCurrency)) {
            SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
            if (sysCurrency != null) {
                return sysCurrency.getCurrencySign();
            }
        }
        return "";
    }

    /**
     * 获取主货币符号
     *
     * @return
     */
    public String getCurrencySign() {
        String defaultCurrency = SessionManager.getUser().getDefaultCurrency();
        return getCurrencySign(defaultCurrency);
    }

    /**
     * 设置分类
     */
    private List<VActivityMessage> setClassifyKeyName(List<VActivityMessage> vActivityMessages) {
        Map<String, SiteI18n> siteI18nMap = Cache.getOperateActivityClassify();
        for (VActivityMessage message : vActivityMessages) {
            String str = message.getActivityClassifyKey() + ":" + SessionManager.getLocale();
            message.setClassifyKeyName(siteI18nMap.get(str).getValue());
        }
        return vActivityMessages;
    }

    /**
     * 根据存款金额、存款方式获取优惠
     *
     * @param rechargeAmount
     * @param type
     * @return
     */
    List<VActivityMessage> searchSaleByAmount(Double rechargeAmount, String type) {
        UserPlayer userPlayer = getUserPlayer();
        VActivityMessageVo vActivityMessageVo = new VActivityMessageVo();
        vActivityMessageVo.getSearch().setDepositWay(type);
        vActivityMessageVo.setDepositAmount(rechargeAmount);
        vActivityMessageVo.setRankId(userPlayer.getRankId());
        vActivityMessageVo.setLocal(SessionManager.getLocale().toString());
        vActivityMessageVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        vActivityMessageVo = ServiceActivityTool.vActivityMessageService().searchDepositPromotions(vActivityMessageVo);
        LinkedHashSet<VActivityMessage> vActivityMessages = vActivityMessageVo.getvActivityMessageList();
        if (CollectionTool.isEmpty(vActivityMessages)) {
            return new ArrayList<>();
        }
        //如果玩家首存可同时显示首存送和存就送
        if (userPlayer.getIsFirstRecharge() != null && userPlayer.getIsFirstRecharge()) {
            return setClassifyKeyName(new ArrayList(vActivityMessages));
        }
        //玩家非首存，查询存就送优惠
        List<VActivityMessage> activityList = CollectionQueryTool.query(vActivityMessages, Criteria.add(VActivityMessage.PROP_CODE, Operator.EQ, ActivityTypeEnum.DEPOSIT_SEND.getCode()));
        return setClassifyKeyName(activityList);
    }

    /**
     * 获取优惠
     *
     * @param type
     * @return
     */
    public List<VActivityMessage> searchSales(String type) {
        if (StringTool.isBlank(type)) {
            return null;
        }
        VActivityMessageListVo listVo = new VActivityMessageListVo();
        listVo.getSearch().setDepositWay(type);
        listVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        listVo.getSearch().setActivityTerminalType(TerminalEnum.MOBILE.getCode());
        listVo = ServiceSiteTool.playerRechargeService().searchSale(listVo, SessionManager.getUserId());
        return setClassifyKeyName(listVo.getResult());
    }

    /**
     * 获取玩家信息
     */
    private UserPlayer getUserPlayer() {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        return userPlayerVo.getResult();
    }

    /**
     * 根据id获取收款账号
     *
     * @param payAccountId
     * @return
     */
    PayAccount getPayAccountById(Integer payAccountId) {
        PayAccountVo payAccountVo = new PayAccountVo();
        payAccountVo.getSearch().setId(payAccountId);
        payAccountVo.getSearch().setPlayerId(SessionManager.getUserId());
        payAccountVo = ServiceSiteTool.payAccountService().queryAccountByAccountIdAndPlayerId(payAccountVo);//查询可用的账户
        return payAccountVo.getResult();
    }

    public IPlayerRechargeService playerRechargeService() {
        return ServiceSiteTool.playerRechargeService();
    }

    /**
     * 计算在RechargeFeeSchema设置的符合免手续费的存款次数
     *
     * @param schema        手续费方案
     * @param isFee       手续费标志
     * @param isReturnFee 返手续费标志
     * @return
     */
    private long getDepositFeeSchemaCountInTime(RechargeFeeSchema schema, boolean isFee, boolean isReturnFee) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        Date now = SessionManager.getDate().getNow();
        listVo.getSearch().setEndTime(now);
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        if (isFee && schema.getFreeCount() != null && schema.getFreeCount() > 0 && schema.getFeeTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -schema.getFeeTime()));
        } else if (isReturnFee && schema.getReturnFeeCount() != null && schema.getReturnFeeCount() > 0 && schema.getReturnTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -schema.getReturnTime()));
        }
        listVo.setRechargeFeeSchema(schema);
        return playerRechargeService().searchPlayerRechargeFeeSchemaCount(listVo);
    }

    /**
     * 计算手续费方案收取的手续费金额
     * @param rechargeAmount
     * @param account
     * @return
     */
    public RechargeFeeSchemaVo calculateFeeSchema(double rechargeAmount, String account) {
        //查询手续费方案
        PayAccountVo accountVo = new PayAccountVo();
        accountVo.setSearchId(account);
        RechargeFeeSchemaVo schemaVo = ServiceSiteTool.rechargeFeeSchemaService().searchFeeSchemaUseAccountId(accountVo);
        RechargeFeeSchema schema = schemaVo.getResult();
        //是否收取或者返还:手续费为空,
        if (schema == null) {
            schemaVo.setISSchema(false);
            return schemaVo;
        }

        //其余情况都是要收取的
        schemaVo.setISSchema(true);

        //手续费标志
        boolean isFee = !(schema.getIsFee() == null || !schema.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(schema.getIsReturnFee() == null || !schema.getIsReturnFee());
        double fee = 0d;
        schemaVo.setFeeAmount(fee);


        //有手续费方案关联,但不收不返
        if (!isFee && !isReturnFee) {
            return schemaVo;
        }
        //返还达不到金额要求
        if (isReturnFee && rechargeAmount < schema.getReachMoney()) {
            return schemaVo;
        }
        // 规定时间内存款次数
        long count = getDepositFeeSchemaCountInTime(schema, isFee, isReturnFee);
        if (isFee && schema.getFreeCount() != null && count < schema.getFreeCount()) {
            return schemaVo;
        }
        if (isReturnFee && schema.getReturnFeeCount() != null && count >= schema.getReturnFeeCount()) {
            return schemaVo;
        }

        //费用计算
        if (isFee && schema.getFeeMoney() != null) {
            fee = computeFee(schema.getFeeType(), schema.getFeeMoney(), rechargeAmount, schema.getMaxFee());
        } else if (isReturnFee && schema.getReturnMoney() != null) {
            fee = computeFee(schema.getReturnType(), schema.getReturnMoney(), rechargeAmount, schema.getMaxReturnFee());
        }
        if (isFee) {
            fee = -Math.abs(fee);
        } else {
            fee = Math.abs(fee);
        }
        schemaVo.setFeeAmount(fee);
        return schemaVo;

    }

    /**
     * 计算手续费:存款手续费方案或者层级手续费
     *
     * @param rechargeAmount 存款金额
     * @return
     */
    public double calculateFeeSchemaAndRank(PlayerRank rank, double rechargeAmount, String account) {

        //计算手续费方案收取的手续费金额
        RechargeFeeSchemaVo schemaVo = calculateFeeSchema(rechargeAmount, account);
        if (schemaVo.getISSchema()){
            return schemaVo.getFeeAmount();
        }
        //不用手续费方案,就计算层级的费用设置
        if (rank == null) {
            return 0d;
        }
        //手续费标志
        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
        if (!isFee && !isReturnFee) {
            return 0d;
        }
        if (isReturnFee && rechargeAmount < rank.getReachMoney()) {
            return 0d;
        }
        // 规定时间内存款次数
        long count = getDepositCountInTime(rank, isFee, isReturnFee);
        if (isFee && rank.getFreeCount() != null && count < rank.getFreeCount()) {
            return 0d;
        }
        if (isReturnFee && rank.getReturnFeeCount() != null && count >= rank.getReturnFeeCount()) {
            return 0d;
        }
        double fee = 0d;
        if (isFee && rank.getFeeMoney() != null) {
            fee = computeFee(rank.getFeeType(), rank.getFeeMoney(), rechargeAmount, rank.getMaxFee());
        } else if (isReturnFee && rank.getReturnMoney() != null) {
            fee = computeFee(rank.getReturnType(), rank.getReturnMoney(), rechargeAmount, rank.getMaxReturnFee());
        }
        if (isFee) {
            fee = -Math.abs(fee);
        } else {
            fee = Math.abs(fee);
        }
        return fee;
    }
    /**
     * 计算手续费
     */
    Double calculateFee(PlayerRank rank, Double rechargeAmount) {
        if (rank == null || rechargeAmount == null) {
            return 0d;
        }
        //手续费标志
        boolean isFee = !(rank.getIsFee() == null || !rank.getIsFee());
        //返手续费标志
        boolean isReturnFee = !(rank.getIsReturnFee() == null || !rank.getIsReturnFee());
        if (!isFee && !isReturnFee) {
            return 0d;
        }
        if (isReturnFee && rechargeAmount < rank.getReachMoney()) {
            return 0d;
        }
        // 规定时间内存款次数
        long count = getDepositCountInTime(rank, isFee, isReturnFee);
        if (isFee && rank.getFreeCount() != null && count < rank.getFreeCount()) {
            return 0d;
        }
        if (isReturnFee && rank.getReturnFeeCount() != null && count >= rank.getReturnFeeCount()) {
            return 0d;
        }
        Double fee = 0d;
        if (isFee && rank.getFeeMoney() != null) {
            fee = computeFee(rank.getFeeType(), rank.getFeeMoney(), rechargeAmount, rank.getMaxFee());
        } else if (isReturnFee && rank.getReturnMoney() != null) {
            fee = computeFee(rank.getReturnType(), rank.getReturnMoney(), rechargeAmount, rank.getMaxReturnFee());
        }
        if (isFee) {
            fee = -Math.abs(fee);
        } else {
            fee = Math.abs(fee);
        }
        return fee == null ? 0d : fee;
    }

    /**
     * 计算在层级设置的符合免手续费的存款次数
     *
     * @param rank        玩家层级
     * @param isFee       手续费标志
     * @param isReturnFee 返手续费标志
     */
    private long getDepositCountInTime(PlayerRank rank, boolean isFee, boolean isReturnFee) {
        PlayerRechargeListVo listVo = new PlayerRechargeListVo();
        Date now = SessionManager.getDate().getNow();
        listVo.getSearch().setEndTime(now);
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.setRank(rank);
        if (isFee && rank.getFreeCount() != null && rank.getFreeCount() > 0 && rank.getFeeTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getFeeTime()));
        } else if (isReturnFee && rank.getReturnFeeCount() != null && rank.getReturnFeeCount() > 0 && rank.getReturnTime() != null) {
            listVo.getSearch().setStartTime(DateTool.addHours(now, -rank.getReturnTime()));
        }
        return ServiceSiteTool.playerRechargeService().searchPlayerRechargeCount(listVo);
    }

    /**
     * 按计算手续费方式计算手续费
     *
     * @param feeType        计算手续费方式：按比例、固定
     * @param feeMoney       收取手续费固定值
     * @param rechargeAmount 存款金额
     * @param maxFee         手续费最大值
     */
    private double computeFee(String feeType, Double feeMoney, Double rechargeAmount, Double maxFee) {
        double fee = 0d;
        if (feeMoney != null || rechargeAmount != null) {
            if (RankFeeType.PROPORTION.getCode().equals(feeType)) {
                fee = rechargeAmount * feeMoney / 100.0;
            } else if (RankFeeType.FIXED.getCode().equals(feeType)) {
                fee = feeMoney;
            }
        }
        if (maxFee != null && fee > maxFee) {
            fee = maxFee;
        }
        return fee;
    }

    /**
     * 返回信息
     *
     * @param map
     * @param playerRechargeVo
     * @return
     */
    Map<String, Object> getVoMessage(Map<String, Object> map, PlayerRechargeVo playerRechargeVo) {
        if (!playerRechargeVo.isSuccess()) {
            map.put(TokenHandler.TOKEN_VALUE, TokenHandler.generateGUID());
        } else {
            map.put("orderNo", playerRechargeVo.getResult().getTransactionNo());
        }
        if (playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getOkMsg())) {
            playerRechargeVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_SUCCESS));

        } else if (!playerRechargeVo.isSuccess() && StringTool.isBlank(playerRechargeVo.getErrMsg())) {
            playerRechargeVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, MessageI18nConst.SAVE_FAILED));
        }
        map.put("state", playerRechargeVo.isSuccess());
        map.put("msg", StringTool.isNotBlank(playerRechargeVo.getOkMsg()) ? playerRechargeVo.getOkMsg() : playerRechargeVo.getErrMsg());
        return map;
    }


    /**
     * 验证码验证
     *
     * @param code
     * @return
     */
    @RequestMapping("/checkCaptcha")
    @ResponseBody
    public boolean checkCaptcha(@RequestParam("code") String code) {
        return !StringTool.isEmpty(code) && code.equalsIgnoreCase((String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_RECHARGE.getSuffix()));
    }

}
