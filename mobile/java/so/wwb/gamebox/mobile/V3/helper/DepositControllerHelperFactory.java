package so.wwb.gamebox.mobile.V3.helper;

import org.soul.commons.spring.utils.SpringTool;
import so.wwb.gamebox.mobile.V3.enums.DepositChannelEnum;

/**
 * 根据不同通道找到不同的类实现
 *
 * @author hanson 18-05-15
 */
public class DepositControllerHelperFactory {

    public static IDepositControllerHelper getHelper(String channel) {
        DepositChannelEnum channelEnum = DepositChannelEnum.enumOf(channel);
        return SpringTool.getBean(channelEnum.getHelperClazz());
    }
}
