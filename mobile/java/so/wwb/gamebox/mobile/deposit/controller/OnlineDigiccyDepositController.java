package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.web.session.SessionManagerBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.master.digiccy.po.UserDigiccy;
import so.wwb.gamebox.model.master.digiccy.vo.UserDigiccyListVo;
import so.wwb.gamebox.model.master.digiccy.vo.UserDigiccyVo;
import so.wwb.gamebox.model.master.enums.TransactionOriginEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.RechargeTypeEnum;
import so.wwb.gamebox.model.master.fund.po.PlayerRecharge;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;
import so.wwb.gamebox.model.master.operation.po.VActivityMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数字货币存款
 * Created by cherry on 17-10-18.
 */
@RequestMapping("/wallet/deposit/digiccy")
@Controller
public class OnlineDigiccyDepositController extends BaseDepositController {
    private static final Log LOG = LogFactory.getLog(OnlineDigiccyDepositController.class);
    private static final String DIGICCY_URI = "/deposit/Digiccy";
    private static final String DIGICCY_SALE_URI = "/deposit/DigiccySale";

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String index(Model model) {
        UserDigiccyListVo userDigiccyListVo = new UserDigiccyListVo();
        userDigiccyListVo.getSearch().setUserId(SessionManagerBase.getUserId());
        List<UserDigiccy> userDigiccyList = ServiceSiteTool.userDigiccyService().getUserDigiccis(userDigiccyListVo);
        model.addAttribute("userDigiccyList", userDigiccyList);
        return DIGICCY_URI;
    }

    /**
     * 生成数字货币地址
     *
     * @param currency
     * @return
     */
    @RequestMapping("/newAddress")
    @ResponseBody
    public Map<String, Object> newAddress(String currency) {
        UserDigiccyVo userDigiccyVo = new UserDigiccyVo();
        userDigiccyVo.setSysUser(SessionManager.getUser());
        userDigiccyVo.getSearch().setCurrency(currency);
        userDigiccyVo.getSearch().setUserId(SessionManager.getUserId());
        userDigiccyVo = ServiceSiteTool.userDigiccyService().getDepositAddress(userDigiccyVo);
        Map<String, Object> map = new HashMap<>(3, 1f);
        if (userDigiccyVo.isSuccess() && userDigiccyVo.getResult() != null) {
            UserDigiccy userDigiccy = userDigiccyVo.getResult();
            map.put("address", userDigiccy.getAddress());
            map.put("addressQrcodeUrl", userDigiccy.getAddressQrcodeUrl());
            map.put("state", true);
        } else {
            map.put("state", "false");
            map.put("msg", userDigiccyVo.getErrMsg());
        }
        return map;
    }

    /**
     * 兑换金额
     *
     * @param currency
     * @return
     */
    @RequestMapping("/exchange")
    @ResponseBody
    public Map<String, Object> exchange(String currency) {
        UserDigiccyVo userDigiccyVo = new UserDigiccyVo();
        userDigiccyVo.setSysUser(SessionManager.getUser());
        userDigiccyVo.getSearch().setCurrency(currency);
        userDigiccyVo.getSearch().setUserId(SessionManager.getUserId());
        PlayerRechargeVo playerRechargeVo = new PlayerRechargeVo();
        try {
            playerRechargeVo.setOrigin(TransactionOriginEnum.MOBILE.getCode());
            playerRechargeVo = ServiceSiteTool.playerRechargeService().digiccyExchange(playerRechargeVo, userDigiccyVo);
        } catch (Exception e) {
            LOG.error(e);
            playerRechargeVo.setSuccess(false);
        }
        Map<String, Object> map = new HashMap<>(3, 1f);
        map.put("state", playerRechargeVo.isSuccess());
        map.put("msg", playerRechargeVo.getErrMsg());
        if (playerRechargeVo.isSuccess()) {
            map.put("transactionNo", playerRechargeVo.getResult().getTransactionNo());
            map.put("isOpenActivityHall", ParamTool.isOpenActivityHall());
        }
        return map;
    }

    /**
     * 刷新货币余额
     *
     * @param currency
     * @return
     */
    @RequestMapping("/refresh")
    @ResponseBody
    public Map<String, Object> refresh(String currency) {
        UserDigiccyVo userDigiccyVo = new UserDigiccyVo();
        userDigiccyVo.getSearch().setCurrency(currency);
        userDigiccyVo.getSearch().setUserId(SessionManager.getUserId());
        userDigiccyVo = ServiceSiteTool.userDigiccyService().fetchBalance(userDigiccyVo);
        UserDigiccy userDigiccy = userDigiccyVo.getResult();
        Map<String, Object> map = new HashMap<>(1, 1f);
        if (userDigiccy != null) {
            map.put("amount", userDigiccy.getAmount());
        } else {
            map.put("amount", 0);
        }
        return map;
    }

    /**
     * 优惠
     *
     * @param playerRechargeVo
     * @param model
     * @return
     */
    @RequestMapping("/sale")
    @Upgrade(upgrade = true)
    public String sale(PlayerRechargeVo playerRechargeVo, Model model) {
        playerRechargeVo = ServiceSiteTool.playerRechargeService().searchPlayerRecharge(playerRechargeVo);
        PlayerRecharge playerRecharge = playerRechargeVo.getResult();
        List<VActivityMessage> sales;
        if (playerRecharge != null && RechargeStatusEnum.ONLINE_SUCCESS.getCode().equals(playerRecharge.getRechargeStatus())) {
            sales = searchSaleByAmount(playerRecharge.getRechargeAmount(), RechargeTypeEnum.DIGICCY_SCAN.getCode());
        } else {
            sales = searchSales(RechargeTypeEnum.DIGICCY_SCAN.getCode());
        }
        model.addAttribute("sales", sales);
        model.addAttribute("playerRecharge", playerRecharge);
        model.addAttribute("currencySign", getCurrencySign());
        return DIGICCY_SALE_URI;
    }

    @RequestMapping("/saveSale")
    @ResponseBody
    public Map<String, Object> saveSale(PlayerRechargeVo playerRechargeVo) {
        String transactionNo = playerRechargeVo.getSearch().getTransactionNo();
        Integer activityId = playerRechargeVo.getActivityId();
        Map<String, Object> map = new HashMap<>(1, 1f);
        if (StringTool.isBlank(transactionNo)) {
            LOG.info("保存数字货币优惠参数不全:没有指定交易号");
            map.put("state", false);
            return map;
        }
        if (activityId == null) {
            map.put("state", true);
            LOG.info("保存数字货币优惠:玩家未选择优惠,交易号{0}", transactionNo);
            return map;
        }
        playerRechargeVo.setSysUser(SessionManager.getUser());
        try {
            playerRechargeVo = ServiceSiteTool.playerRechargeService().saveDigiccyFavorable(playerRechargeVo);
        } catch (Exception e) {
            playerRechargeVo.setSuccess(false);
            LOG.error(e);
        }
        map.put("state", playerRechargeVo.isSuccess());
        return map;
    }

}
