package so.wwb.gamebox.mobile.wallet.controller;

import org.soul.commons.bean.Pair;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.ListTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criteria;
import org.soul.commons.query.enums.Operator;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.model.sys.po.SysParam;
import org.soul.web.controller.NoMappingCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.iservice.master.report.IVPlayerTransactionService;
import so.wwb.gamebox.mobile.form.PlayerTransactionForm;
import so.wwb.gamebox.mobile.form.PlayerTransactionSearchForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.master.enums.CommonStatusEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionTypeEnum;
import so.wwb.gamebox.model.master.fund.enums.TransactionWayEnum;
import so.wwb.gamebox.model.master.fund.vo.PlayerTransferVo;
import so.wwb.gamebox.model.master.fund.vo.PlayerWithdrawVo;
import so.wwb.gamebox.model.master.fund.vo.VPlayerWithdrawVo;
import so.wwb.gamebox.model.master.report.po.VPlayerTransaction;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionListVo;
import so.wwb.gamebox.model.master.report.vo.VPlayerTransactionVo;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 资金记录
 * Created by bill on 16-11-27.
 */
@Controller
@RequestMapping("/fund/record")
public class FundRecordController extends NoMappingCrudController<IVPlayerTransactionService, VPlayerTransactionListVo, VPlayerTransactionVo, PlayerTransactionSearchForm, PlayerTransactionForm, VPlayerTransaction, Integer> {

    private IPlayerTransferService playerTransferService;
    private static final int DEFAULT_TIME = -6;

    //region your codes 3
    private IPlayerTransferService playerTransferService() {
        if (playerTransferService == null)
            playerTransferService = ServiceTool.playerTransferService();
        return playerTransferService;
    }

    @RequestMapping("/index")
    public String fundRecord(VPlayerTransactionListVo listVo, Model model, HttpServletRequest request) {
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        initQueryDate(listVo);
        if (listVo.getSearch().getEndCreateTime() != null)
            listVo.getSearch().setEndCreateTime(DateTool.addDays(listVo.getSearch().getEndCreateTime(), 1));
        getFund(model);

        //玩家中心不展示派彩相关资金记录
        listVo.getSearch().setNoDisplay(TransactionWayEnum.MANUAL_PAYOUT.getCode());
        listVo.getSearch().setLotterySite(isLotterySite());
        listVo = getService().search(listVo);
        listVo = preList(listVo);
        model.addAttribute("command", listVo);
        model.addAttribute("maxDate", SessionManager.getDate().getNow());
        model.addAttribute("minDate", SessionManager.getDate().addDays(DEFAULT_TIME));
        model.addAttribute("currency", getCurrencySign(SessionManager.getUser().getDefaultCurrency()));

        return ServletTool.isAjaxSoulRequest(request) ? getViewBasePath() + "IndexPartial" : getViewBasePath() + "Index";
    }

    private String getCurrencySign(String defaultCurrency) {
        if (StringTool.isNotBlank(defaultCurrency)) {
            SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
            if (sysCurrency != null) {
                return sysCurrency.getCurrencySign();
            }
        }
        return "";
    }

    @RequestMapping("/handleRefresh")
    @ResponseBody
    public Map handleRefresh() {
        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(SessionManager.getUserId());
        Map<String, String> result = new HashMap<>();
        result.put("withdrawSum", CurrencyTool.formatCurrency(ServiceTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo)));
        result.put("currency", getCurrencySign());
        if (!isLotterySite()) {
            //正在转账中金额
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
            result.put("transferSum", CurrencyTool.formatCurrency(playerTransferService().queryProcessAmount(playerTransferVo)));
        }
        return result;
    }

    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }

    /**
     * 取款处理中/转账处理中的金额
     */
    private void getFund(Model model) {
        //正在处理中取款金额
        PlayerWithdrawVo playerWithdrawVo = new PlayerWithdrawVo();
        playerWithdrawVo.getSearch().setPlayerId(SessionManager.getUserId());
        model.addAttribute("withdrawSum", ServiceTool.playerWithdrawService().getDealWithdraw(playerWithdrawVo));

        if (!isLotterySite()) {
            //正在转账中金额
            PlayerTransferVo playerTransferVo = new PlayerTransferVo();
            playerTransferVo.getSearch().setUserId(SessionManager.getUserId());
            model.addAttribute("transferSum", playerTransferService().queryProcessAmount(playerTransferVo));
        }
    }

    /**
     * 资金记录详情
     *
     * @param vo
     * @param model
     * @return
     */
    @RequestMapping("/details")
    public String details(VPlayerTransactionVo vo, Model model) {
        if (vo.getSearch().getId() == null) {
            model.addAttribute("command", vo);
            return getViewBasePath() + "Details";
        }
        vo = super.doView(vo, model);
        //如果playerid不是当前玩家id,不展示
        if (!SessionManager.getUserId().equals(vo.getResult().getPlayerId())) {
            vo.setResult(null);
            model.addAttribute("command", vo);
            return getViewBasePath() + "Details";
        }
        if (vo.getResult() != null && TransactionTypeEnum.WITHDRAWALS.getCode().equals(vo.getResult().getTransactionType())) {
            if (StringTool.isNotBlank(vo.getResult().getTransactionNo())) {
                VPlayerWithdrawVo withdrawVo = new VPlayerWithdrawVo();
                withdrawVo.getSearch().setId(vo.getResult().getSourceId());
                withdrawVo = ServiceTool.vPlayerWithdrawService().get(withdrawVo);
                model.addAttribute("withdrawVo", withdrawVo);
            }
            if (vo.getResult().getPlayerId() != null) {
                SysUserVo sysUserVo = new SysUserVo();
                sysUserVo.setResult(SessionManager.getUser());
                model.addAttribute("sysUserVo", sysUserVo);
            }
        }
        model.addAttribute("command", vo);
        model.addAttribute("siteCurrencySign", getCurrencySign());
        return getViewBasePath() + "Details";
    }

    private VPlayerTransactionListVo preList(VPlayerTransactionListVo playerTransactionListVo) {
        Map<String, Serializable> transactionMap = DictTool.get(DictEnum.COMMON_TRANSACTION_TYPE);
        if (transactionMap != null) {   // 过滤转账类型
            transactionMap.remove(TransactionTypeEnum.TRANSFERS.getCode());
        }
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

    private void initQueryDate(VPlayerTransactionListVo listVo) {
        listVo.setMinDate(SessionManager.getDate().addDays(DEFAULT_TIME));
        if (listVo.getSearch().getBeginCreateTime() == null) {
            listVo.getSearch().setBeginCreateTime(SessionManager.getDate().addDays(DEFAULT_TIME));
        } else if (listVo.getSearch().getBeginCreateTime().before(listVo.getMinDate())) {
            listVo.getSearch().setBeginCreateTime(listVo.getMinDate());
        }
        if (listVo.getSearch().getEndCreateTime() == null) {
            listVo.getSearch().setEndCreateTime(SessionManager.getDate().getNow());
        }
    }

    private boolean isLotterySite() {
        SysParam param= ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_IS_LOTTERY_SITE);
        return param != null ? Boolean.valueOf(param.getParamValue()) : false;
    }

    @Override
    protected String getViewBasePath() {
        return "/fund/record/";
    }
}
