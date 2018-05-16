package so.wwb.gamebox.mobile.V3.support;

import org.springframework.validation.BindingResult;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 充值最终提交者
 */
public interface IDepositSubmitter {
    /**
     * 保存充值信息
     *
     * @param playerRechargeVo
     * @param result
     * @param request
     * @return
     */
    AbstractRechargeSubmitter.DespositResult saveRecharge(PlayerRechargeVo playerRechargeVo, BindingResult result, HttpServletRequest request);
}

