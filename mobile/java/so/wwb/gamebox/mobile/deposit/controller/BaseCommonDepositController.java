package so.wwb.gamebox.mobile.deposit.controller;

import org.soul.commons.lang.string.StringTool;
import org.soul.model.sys.po.SysParam;
import so.wwb.gamebox.mobile.V3.helper.DepositControllerHelper;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

/**
 * Created by bruce on 17-4-29.
 */
public abstract class BaseCommonDepositController {

    /**
     * 获取层级
     */
    protected PlayerRank getRank() {
        return DepositControllerHelper.getInstance().getRank();
    }

    /**
     * 获取快速充值地址
     *
     * @return
     */
    protected String getFastRechargeUrl() {
        SysParam rechargeUrlParam = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL);
        if (rechargeUrlParam == null || StringTool.isBlank(rechargeUrlParam.getParamValue())) {
            return "";
        }
        //是否包含全部层级
        SysParam allRank = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL_ALL_RANK);
        if (allRank != null && "true".equals(allRank.getParamValue())) {
            return rechargeUrlParam.getParamValue();
        }
        SysParam ranksParam = ParamTool.getSysParam(SiteParamEnum.SETTING_RECHARGE_URL_RANKS);
        boolean isFastRecharge = false;
        if (ranksParam != null && StringTool.isNotBlank(ranksParam.getParamValue())) {
            PlayerRank rank = getRank();
            String[] ranks = ranksParam.getParamValue().split(",");
            for (String rankId : ranks) {
                if (String.valueOf(rank.getId()).equals(rankId)) {
                    isFastRecharge = true;
                    break;
                }
            }
        }
        if (isFastRecharge) {
            return rechargeUrlParam.getParamValue();
        } else {
            return "";
        }
    }

}
