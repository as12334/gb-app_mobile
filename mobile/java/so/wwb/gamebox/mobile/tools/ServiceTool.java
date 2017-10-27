package so.wwb.gamebox.mobile.tools;

import org.soul.iservice.pay.IOnlinePayService;
import so.wwb.gamebox.iservice.company.setting.IGameService;
import so.wwb.gamebox.iservice.company.site.ISiteApiTypeRelationI18nService;
import so.wwb.gamebox.iservice.company.site.IVSiteApiService;
import so.wwb.gamebox.iservice.master.autoTranfer.IAutoTranferServcice;
import so.wwb.gamebox.iservice.master.fund.IPlayerTransferService;
import so.wwb.gamebox.iservice.master.fund.IVPlayerWithdrawService;
import so.wwb.gamebox.iservice.master.operation.IActivityPlayerApplyService;
import so.wwb.gamebox.iservice.master.operation.IVActivityMessageService;
import so.wwb.gamebox.iservice.master.operation.IVPlayerActivityMessageService;
import so.wwb.gamebox.iservice.master.player.*;
import so.wwb.gamebox.iservice.master.report.IVPlayerTransactionService;
import so.wwb.gamebox.iservice.master.tasknotify.IUserTaskReminderService;
import so.wwb.gamebox.web.ServiceToolBase;

import static org.soul.commons.dubbo.DubboTool.getService;

/**
 * 远程服务实例获取工具类
 *
 * @author fly
 * @time 2015-12-10 13:57:24
 */
public class ServiceTool extends ServiceToolBase {

    //region your codes 1

    /**
     * 返回玩家远程服务实例
     *
     * @return 玩家远程服务实例
     */
    public static IUserPlayerService userPlayerService() {
        return getService(IUserPlayerService.class);
    }

    public static IPlayerTransferService playerTransferService(){
        return getService(IPlayerTransferService.class);
    }


    public static IVUserPlayerService VUserPlayerService(){
        return getService(IVUserPlayerService.class);
    }
    /**
     * 提现交易表
     *
     * @return
     */
    public static IPlayerTransactionService getPlayerTransactionService() {
        return getService(IPlayerTransactionService.class);
    }

    public static IVPlayerWithdrawService vPlayerWithdrawService() {
        return getService(IVPlayerWithdrawService.class);
    }

    /**
     * 返回玩家游戏下单表远程服务实例
     *
     * @return 玩家游戏下单表远程服务实例
     */
    public static IPlayerGameOrderService playerGameOrderService() {
        return getService(IPlayerGameOrderService.class);
    }
    /**
     * 返回系统公告视图远程服务实例
     *
     * @return 系统公告视图远程服务实例
     */
    public static so.wwb.gamebox.iservice.company.operator.IVSystemAnnouncementService vSystemAnnouncementService() {
        return getService(so.wwb.gamebox.iservice.company.operator.IVSystemAnnouncementService.class);
    }


    public static IVPlayerAdvisoryService vPlayerAdvisoryService() {
        return getService(IVPlayerAdvisoryService.class);
    }

    public static IVUserPlayerService vUserPlayerService() {
        return getService(IVUserPlayerService.class);
    }


    /**
     * 返回联系方式表远程服务实例
     *
     * @return 联系方式表远程服务实例
     */
    public static org.soul.iservice.msg.notice.INoticeContactWayService noticeContactWayService() {
        return getService(org.soul.iservice.msg.notice.INoticeContactWayService.class);
    }

    public static IPlayerApiAccountService playerApiAccountService() {
        return getService(IPlayerApiAccountService.class);
    }

    public static IPlayerApiService playerApiService() {
        return getService(IPlayerApiService.class);
    }


    /**
     * 返回玩家层级表远程服务实例
     *
     * @return 玩家层级表远程服务实例
     */
    public static IPlayerRankService playerRankService() {
        return getService(IPlayerRankService.class);
    }

    /**
     * 玩家提现记录表
     *
     * @return
     */
    public static so.wwb.gamebox.iservice.master.fund.IPlayerWithdrawService playerWithdrawService() {
        return getService(so.wwb.gamebox.iservice.master.fund.IPlayerWithdrawService.class);
    }


    public static IUserTaskReminderService userTaskReminderService() {
        return getService(IUserTaskReminderService.class);
    }

    /**
     * 玩家充值记录表
     *
     * @return
     */
    public static so.wwb.gamebox.iservice.master.fund.IPlayerRechargeService playerRechargeService() {
        return getService(so.wwb.gamebox.iservice.master.fund.IPlayerRechargeService.class);
    }

    /**
     * 返回收款账户表远程服务实例
     *
     * @return 收款账户表远程服务实例
     */
    public static so.wwb.gamebox.iservice.master.content.IPayAccountService payAccountService() {
        return getService(so.wwb.gamebox.iservice.master.content.IPayAccountService.class);
    }

    public static IOnlinePayService onlinePayService() {
        return getService(IOnlinePayService.class);
    }

    public static IGameService gameService() {
        return getService(IGameService.class);
    }

    public static IVPlayerTransactionService vPlayerTransactionService() {
        return getService(IVPlayerTransactionService.class);
    }

    public static so.wwb.gamebox.iservice.company.sys.ISysDomainService sysDomainService() {
        return getService(so.wwb.gamebox.iservice.company.sys.ISysDomainService.class);
    }
    public static IVSiteApiService vSiteApiService() {
        return getService(IVSiteApiService.class);
    }

    /**
     * 返回远程服务实例
     *
     * @return 远程服务实例
     */
    public static so.wwb.gamebox.iservice.master.operation.IVPreferentialRecodeService vPreferentialRecodeService() {
        return getService(so.wwb.gamebox.iservice.master.operation.IVPreferentialRecodeService.class);
    }

    /**
     * 返回内容管理-公告表远程服务实例
     *
     * @return 内容管理-公告表远程服务实例
     */
    public static so.wwb.gamebox.iservice.master.content.ICttAnnouncementService cttAnnouncementService() {
        return getService(so.wwb.gamebox.iservice.master.content.ICttAnnouncementService.class);
    }

    public static so.wwb.gamebox.iservice.master.content.ICttDocumentI18nService cttDocumentI18nService() {
        return getService(so.wwb.gamebox.iservice.master.content.ICttDocumentI18nService.class);
    }
    public static IPlayerAdvisoryReplyService playerAdvisoryReplyService() {
        return getService(IPlayerAdvisoryReplyService.class);
    }
    /**
     * 返回player_advisory_read表远程服务实例
     *
     * @return player_advisory_read表远程服务实例
     */
    public static so.wwb.gamebox.iservice.master.operation.IPlayerAdvisoryReadService playerAdvisoryReadService() {
        return getService(so.wwb.gamebox.iservice.master.operation.IPlayerAdvisoryReadService.class);
    }
    public static IPlayerAdvisoryService playerAdvisoryService() {
        return getService(IPlayerAdvisoryService.class);
    }

    /**
     * 返回远程服务实例
     *
     * @return 远程服务实例
     */
    public static IVActivityMessageService vActivityMessageService() {
        return getService(IVActivityMessageService.class);
    }
    public static IVPlayerActivityMessageService vPlayerActivityMessageService() {
        return getService(IVPlayerActivityMessageService.class);
    }
    public static IActivityPlayerApplyService activityPlayerApplyService() {
        return getService(IActivityPlayerApplyService.class);
    }
    public static IVPlayerAdvisoryReplyService vPlayerAdvisoryReplyService() {
        return getService(IVPlayerAdvisoryReplyService.class);
    }
    public static ISiteApiTypeRelationI18nService siteApiTypeRelationI18nService() {
        return getService(ISiteApiTypeRelationI18nService.class);
    }


    public static IAutoTranferServcice getAutoTranferServcice() {
        return getService(IAutoTranferServcice.class);
    }
    /**
     * 返回内容管理-浮动图片表远程服务实例
     *
     * @return 内容管理-浮动图片表远程服务实例
     */
    public static so.wwb.gamebox.iservice.master.content.ICttFloatPicService cttFloatPicService() {
        return getService(so.wwb.gamebox.iservice.master.content.ICttFloatPicService.class);
    }

    //endregion your codes 1
}
