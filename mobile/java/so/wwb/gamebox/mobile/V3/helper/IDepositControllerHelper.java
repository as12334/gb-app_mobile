package so.wwb.gamebox.mobile.V3.helper;

import org.soul.web.support.IForm;
import so.wwb.gamebox.model.master.content.po.PayAccount;
import so.wwb.gamebox.model.master.player.po.PlayerRank;

import java.util.List;

/**
 * 所有充值通道入口的controller辅助类
 *
 * @param <P>
 */
public interface IDepositControllerHelper<P extends PayAccount> {
    /**
     * 获取账号列表
     *
     * @param rank    用户层级
     * @param channel 所选通道
     * @return
     */
    List<P> getPayAccounts(PlayerRank rank, String channel);

    /**
     * 将账号信息转成json:页面点击账号触发
     *
     * @param accouts
     * @return
     */
    String getAccountJson(List<P> accouts);

    /**
     * 获取js验证公式
     *
     * @return
     */
    Class<? extends IForm> getIndexValidateFormClazz();

    /**
     * 获取公司存款的验证器
     *
     * @return
     */
    Class<? extends IForm> getSecondValidateFormClazz();


    /**
     * 返回IndexUrl
     *
     * @return
     */
    String getIndexUrl();

    /**
     * 返回第二步骤URL
     *
     * @return
     */
    String getNextStepUrl();

    /**
     * 获取支付类型
     *
     * @return
     */
    String getRechargeType(String channel);


}
