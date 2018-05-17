package so.wwb.gamebox.mobile.V3.support.submitter;

import org.springframework.validation.BindingResult;
import so.wwb.gamebox.mobile.V3.support.AbstractRechargeSubmitter;
import so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo;

import javax.servlet.http.HttpServletRequest;

/**
 * 充值最终的执行器
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

