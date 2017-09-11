package so.wwb.gamebox.mobile.my.controller;

import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dubbo.DubboTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.math.NumberTool;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.security.privilege.po.SysUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.security.HttpTool;
import so.wwb.gamebox.iservice.master.report.IPlayerRecommendAwardService;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.master.enums.ActivityApplyCheckStatusEnum;
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
import so.wwb.gamebox.web.bank.BankHelper;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的
 *
 * @author Fei
 * @date 2016-07-28
 */
@Controller
@RequestMapping("/mine")
public class MyController {
    private static final String MY_INDEX = "/mine/Mine";
    private static final String GAME_PAGE = "/my/GamePage";
    private static final int PROMO_RECORD_DAYS = -7;
    private static final int RECOMMEND_DAYS = -1;

    @RequestMapping("/index")
    public String index(Model model) {
        model.addAttribute("channel", "mine");
        //玩家信息
        model.addAttribute("sysUser", SessionManager.getUser());
        //现金取款方式
        model.addAttribute("isBit", ParamTool.isBit());
        model.addAttribute("isCash", ParamTool.isCash());
        return MY_INDEX;
    }

    @RequestMapping("/gamePage")
    public String gamePage(Model model, HttpServletRequest request) {
        String url = request.getParameter("url");
        if (StringTool.isNotBlank(url)) {
            model.addAttribute("url", HttpTool.formatUrl(url));
        }
        return GAME_PAGE;
    }

    /**
     * 获取我的个人数据(异步加载)
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    public String getFund() {
        SysUser sysUser = SessionManager.getUser();
        Integer userId = SessionManager.getUserId();
        Map<String, Object> userInfo = new HashMap<>();
        try {
            //总资产
            PlayerApiListVo playerApiListVo = new PlayerApiListVo();
            playerApiListVo.getSearch().setPlayerId(userId);
            playerApiListVo.setApis(Cache.getApi());
            playerApiListVo.setSiteApis(Cache.getSiteApi());
            double totalAssets = ServiceTool.playerApiService().queryPlayerAssets(playerApiListVo);
            userInfo.put("totalAssets", totalAssets);
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        //钱包余额
        userInfo.put("walletBalance", getWalletBalance(userId));

        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(userId);
        userInfo.put("withdrawAmount", ServiceTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));

        //正在处理中转账金额(额度转换)
        PlayerTransferVo playerTransferVo = new PlayerTransferVo();
        playerTransferVo.getSearch().setUserId(userId);
        userInfo.put("transferAmount", ServiceTool.playerTransferService().queryProcessAmount(playerTransferVo));

        //计算近7日收益（优惠金额）
        VPreferentialRecodeListVo vPreferentialRecodeListVo = new VPreferentialRecodeListVo();
        vPreferentialRecodeListVo.getSearch().setUserId(userId);
        vPreferentialRecodeListVo.getSearch().setActivityVersion(SessionManager.getLocale().toString());
        vPreferentialRecodeListVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        vPreferentialRecodeListVo.getSearch().setCheckState(ActivityApplyCheckStatusEnum.SUCCESS.getCode());
        vPreferentialRecodeListVo.getSearch().setStartTime(DateTool.addDays(SessionManager.getDate().getToday(), PROMO_RECORD_DAYS));
        vPreferentialRecodeListVo.setPropertyName(VPreferentialRecode.PROP_PREFERENTIAL_VALUE);
        userInfo.put("preferentialAmount", ServiceTool.vPreferentialRecodeService().sum(vPreferentialRecodeListVo));

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
        userInfo.put("recomdAmount", DubboTool.getService(IPlayerRecommendAwardService.class).searchRecomdAmount(playerRecommendAwardListVo, PlayerRecommendAward.PROP_REWARD_AMOUNT));

        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long number = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        VPlayerAdvisoryListVo listVo = new VPlayerAdvisoryListVo();
        listVo.setSearch(null);
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceTool.vPlayerAdvisoryService().search(listVo);
        Integer advisoryUnReadCount = 0;
        String tag = "";
        //所有咨询数据
        for (VPlayerAdvisory obj : listVo.getResult()) {
            //查询回复表每一条在已读表是否存在
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);
            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
                readVo.setResult(new PlayerAdvisoryRead());
                readVo.getSearch().setUserId(SessionManager.getUserId());
                readVo.getSearch().setPlayerAdvisoryReplyId(replay.getId());
                readVo = ServiceTool.playerAdvisoryReadService().search(readVo);
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
                    VPlayerAdvisoryVo vpaVo = ServiceTool.vPlayerAdvisoryService().get(pa);
                    if (vo.getId().equals(vpaVo.getResult().getContinueQuizId()) || vo.getId().equals(vpaVo.getResult().getId())) {
                        vo.setIsRead(false);
                    }
                }
            }
        }
        userInfo.put("unReadCount", number + advisoryUnReadCount);
        //用户个人信息
        userInfo.put("username", sysUser.getUsername());
        userInfo.put("avatarUrl", sysUser.getAvatarUrl());
        userInfo.put("loginTime", sysUser.getLoginTime());
        userInfo.put("currency", getCurrencySign());

        return JsonTool.toJson(userInfo);
    }

    private Double getWalletBalance(Integer userId) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(userId);
        userPlayerVo = ServiceTool.userPlayerService().get(userPlayerVo);
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

    @RequestMapping("/getBalance")
    @ResponseBody
    public String getBalance() {
        Integer userId = SessionManager.getUserId();
        //纯彩票站点查询api余额
        if (ParamTool.isLotterySite()) {
            PlayerApiVo playerApiVo = new PlayerApiVo();
            playerApiVo.getSearch().setPlayerId(userId);
            playerApiVo.getSearch().setApiId(NumberTool.toInt(ApiProviderEnum.PL.getCode()));
            playerApiVo = ServiceTool.playerApiService().search(playerApiVo);
            PlayerApi playerApi = playerApiVo.getResult();
            return CurrencyTool.formatCurrency(playerApi == null || playerApi.getMoney() == null ? 0d : playerApi.getMoney());
        }
        return CurrencyTool.formatCurrency(getWalletBalance(userId));
    }
}
