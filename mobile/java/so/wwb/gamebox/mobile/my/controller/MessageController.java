package so.wwb.gamebox.mobile.my.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.dict.DictTool;
import org.soul.commons.lang.DateTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.query.Criterion;
import org.soul.commons.query.enums.Operator;
import org.soul.commons.support._Module;
import org.soul.model.msg.notice.po.VNoticeReceivedText;
import org.soul.model.msg.notice.vo.NoticeReceiveVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo;
import org.soul.model.msg.notice.vo.VNoticeReceivedTextVo;
import org.soul.model.session.SessionKey;
import org.soul.web.locale.DateQuickPicker;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.my.form.PlayerAdvisoryForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.company.operator.po.VSystemAnnouncement;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.master.enums.AnnouncementTypeEnum;
import so.wwb.gamebox.model.master.enums.UserTaskEnum;
import so.wwb.gamebox.model.master.operation.po.PlayerAdvisoryRead;
import so.wwb.gamebox.model.master.operation.vo.PlayerAdvisoryReadVo;
import so.wwb.gamebox.model.master.player.enums.PlayerAdvisoryEnum;
import so.wwb.gamebox.model.master.player.po.PlayerAdvisory;
import so.wwb.gamebox.model.master.player.po.PlayerAdvisoryReply;
import so.wwb.gamebox.model.master.player.po.VPlayerAdvisory;
import so.wwb.gamebox.model.master.player.vo.*;
import so.wwb.gamebox.model.master.tasknotify.vo.UserTaskReminderVo;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.web.cache.Cache;
import so.wwb.gamebox.web.common.token.Token;
import so.wwb.gamebox.web.common.token.TokenHandler;
import so.wwb.gamebox.web.passport.captcha.CaptchaUrlEnum;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**
 * Created by bill on 16-12-10.
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    private static final String GAME_NOTICE_URL = "mine/message/NoticeGame";
    private static final String SYSTEM_NOTICE_URL = "mine/message/NoticeSysPartial";
    private static final String MESSAGE_SITE_URL = "mine/message/NoticeSitePartial";
    private static final String MESSAGE_SITE2_URL = "mine/message/NoticeSite2Partial";
    private static final String SEND_MESSAGE = "mine/message/SendMessage";
    private static final String MESSAGE_DETAIL_URL = "mine/message/MessageDetail";
    private Long sysMessageUnReadCount = null ;
    private Integer advisoryUnReadCount = 0;
    private static final int TIME_INTERVAL = -29;
    /**
     * 玩家中心-游戏公告
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/gameNotice")
    public String gameNotice(VSystemAnnouncementListVo listVo, VPlayerAdvisoryListVo aListVo,Model model, HttpServletRequest request,boolean isSendMessage) {
        String returnStr=GAME_NOTICE_URL;
        model.addAttribute("maxDate", SessionManager.getDate().getNow());
        model.addAttribute("minDate", SessionManager.getDate().addDays(TIME_INTERVAL));
        //获取SiteApi
        Map apiMap = Cache.getSiteApiI18n();
        model.addAttribute("apiMap", apiMap);
        Map<String, Serializable> advisoryType = DictTool.get(DictEnum.ADVISORY_TYPE);
        advisoryType.toString();
        model.addAttribute("advisoryType", advisoryType);


        if(ServletTool.isAjaxSoulRequest(request)){
            returnStr = returnStr + "Partial";
        }else {
            this.unReadCount(aListVo, model);
            //如果有未读消息，则跳到未读消息页
            if(isSendMessage){
                model.addAttribute("unReadType","sendMessage");
                return returnStr;
            }
            if (sysMessageUnReadCount != null && sysMessageUnReadCount != 0) {
                model.addAttribute("unReadType","sysMessage");  //传一个标识，判断未读消息
                return returnStr;
            } else if (advisoryUnReadCount != 0) {
                model.addAttribute("unReadType", "advisoryMessage");
                return returnStr;
            }

        }
        if(listVo.getSearch().getStartTime()==null && listVo.getSearch().getEndTime()==null){
            listVo.getSearch().setStartTime(DateTool.addMonths(SessionManager.getDate().getNow(), -1));
            listVo.getSearch().setEndTime(DateQuickPicker.getInstance().getNow());
        }
        if(listVo.getSearch().getEndTime()!=null)
            listVo.getSearch().setEndTime(DateTool.addDays(listVo.getSearch().getEndTime(),1));
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        listVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
        model.addAttribute("command", listVo);

        //未读数量
//        this.unReadCount(aListVo, model);
        return returnStr;
    }
    /**
     * 系统公告
     *
     * @param vListVo
     * @param model
     * @return
     */
    @RequestMapping("/systemNotice")
    public String systemNoticeHistory( VPlayerAdvisoryListVo aListVo, VSystemAnnouncementListVo vListVo, Model model) {
        if(vListVo.getSearch().getStartTime()==null && vListVo.getSearch().getEndTime()==null){
            vListVo.getSearch().setStartTime(DateTool.addMonths(SessionManager.getDate().getNow(), -1));
            vListVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        }
        vListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        vListVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.SYSTEM.getCode());
        vListVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        vListVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(vListVo);
        if (CollectionTool.isNotEmpty(vListVo.getResult())) {
            for (VSystemAnnouncement vSystemAnnouncement : vListVo.getResult()) {
                vSystemAnnouncement.setContent(StringTool.replaceHtml(vSystemAnnouncement.getContent()));
            }
        }
        model.addAttribute("command", vListVo);
        model.addAttribute("minDate",SessionManager.getDate().addDays(TIME_INTERVAL));
        model.addAttribute("maxDate", new Date());
        //未读数量
//        this.unReadCount(aListVo, model);
        return  SYSTEM_NOTICE_URL ;
    }

    /**
     * 玩家中心-系统消息-显示
     *
     * @param listVo
     * @param model
     * @return
     */
    @RequestMapping("/messageList")
    public String messageList(VNoticeReceivedTextListVo listVo,  Model model) {
        listVo.getSearch().setReceiverId(SessionManager.getUserId());
        listVo = ServiceTool.noticeService().fetchReceivedSiteMsg(listVo);
        List<VNoticeReceivedText> texts = new ArrayList<>();
        for(VNoticeReceivedText text : listVo.getResult()){
            text.setContent(text.getContent().replaceAll("\\$\\{user\\}",SessionManager.getUserName()));
            text.setTitle(text.getTitle().replaceAll("\\$\\{user\\}",SessionManager.getUserName()));
            texts.add(text);
        }
        listVo.setResult(texts);
        model.addAttribute("command", listVo);
        //未读数量
//        this.unReadCount(aListVo, model);
        return MESSAGE_SITE_URL;
    }
    /**
     * 玩家中心-咨询消息显示
     *
     * @param listVo
     * @param model
     * @return
     */
    @RequestMapping("/advisoryMessage")
    public String advisoryMessage(VPlayerAdvisoryListVo listVo, VPlayerAdvisoryListVo aListVo, Model model ) {
        //提问内容+未读数量
        listVo = this.unReadCount(listVo, model);
        model.addAttribute("command", listVo);
        return  MESSAGE_SITE2_URL;
    }
    /**
     * 发送消息
     *
     * @param model
     * @return
     */
    @RequestMapping("/beforeSendMessage")
    @Token(generate = true)
    public String beforeSendMessage(VPlayerAdvisoryListVo aListVo, Model model) {
        //表单校验
        model.addAttribute("validate", JsRuleCreator.create(PlayerAdvisoryForm.class, "result"));
        if(SessionManager.getSendMessageCount() !=null && SessionManager.getSendMessageCount() >=3){
            model.addAttribute("isOpenCaptcha",true);
        }
        //未读数量
//        this.unReadCount(aListVo, model);
        return SEND_MESSAGE;
    }
    /**
     * 保存消息
     *
     * @param playerAdvisoryVo
     * @return
     */
    @RequestMapping("/sendMessage")
    @ResponseBody
    @Token(valid = true)
    public Map sendMessage(PlayerAdvisoryVo playerAdvisoryVo, @RequestParam("captcha") String code) {
        playerAdvisoryVo.setSuccess(false);
        playerAdvisoryVo.getResult().setAdvisoryTime(SessionManager.getDate().getNow());
        playerAdvisoryVo.getResult().setPlayerId(SessionManager.getUserId());
        playerAdvisoryVo.getResult().setReplyCount(0);
        playerAdvisoryVo.getResult().setQuestionType(PlayerAdvisoryEnum.QUESTION.getCode());
        playerAdvisoryVo = ServiceTool.playerAdvisoryService().insert(playerAdvisoryVo);
        HashMap map = new HashMap(4);
        map.put("isOpenCaptcha",false);
        if(SessionManager.getSendMessageCount() !=null && SessionManager.getSendMessageCount() >=3){
            String sessionCode = SessionManagerCommon.getCaptcha(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_FEEDBACK.getSuffix());
            if (StringTool.isNotBlank(sessionCode) && sessionCode.equalsIgnoreCase(code)) {

            }else{
                map.put("isOpenCaptcha",true);
                map.put("msg", "captchaError");
                map.put("state", false);
                map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
                return map;
            }
        }
        if (playerAdvisoryVo.isSuccess()) {
            playerAdvisoryVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "save.success"));
            //发送消息数量
            Integer sendMessageCount = SessionManager.getSendMessageCount()==null?0:SessionManager.getSendMessageCount();
            SessionManager.setsSendMessageCount(sendMessageCount+1);
            if(SessionManager.getSendMessageCount() >=3){
                map.put("isOpenCaptcha",true);
            }
            //生成任务提醒
            UserTaskReminderVo userTaskReminderVo = new UserTaskReminderVo();
            userTaskReminderVo.setTaskEnum(UserTaskEnum.PLAYERCONSULTATION);
            ServiceTool.userTaskReminderService().addTaskReminder(userTaskReminderVo);
        } else {
            playerAdvisoryVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, "save.failed"));
        }

        map.put("msg", StringTool.isNotBlank(playerAdvisoryVo.getOkMsg()) ? playerAdvisoryVo.getOkMsg() : playerAdvisoryVo.getErrMsg());
        map.put("state", Boolean.valueOf(playerAdvisoryVo.isSuccess()));
        map.put(TokenHandler.TOKEN_VALUE,TokenHandler.generateGUID());
        return map;
    }

    /**
     * 玩家中心-咨询消息-删除
     *
     * @param vo
     * @param ids
     * @param model
     * @return
     */
    @RequestMapping("/deleteAdvisoryMessage")
    @ResponseBody
    public Map deleteAdvisoryMessage(PlayerAdvisoryVo vo, String ids, Model model) {
        String[] id = ids.split(",");
        for (String messageId : id) {
            PlayerAdvisoryListVo listVo = new PlayerAdvisoryListVo();
            listVo.getSearch().setContinueQuizId(Integer.valueOf(messageId));
            listVo = ServiceTool.playerAdvisoryService().search(listVo);
            for(PlayerAdvisory obj:listVo.getResult()){
                vo.setSuccess(false);
                vo.setResult(new PlayerAdvisory());
                vo.getResult().setId(obj.getId());
                vo.getResult().setPlayerDelete(true);
                vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
                vo = ServiceTool.playerAdvisoryService().updateOnly(vo);
            }
            vo.setSuccess(false);
            vo.setResult(new PlayerAdvisory());
            vo.getResult().setId(Integer.valueOf(messageId));
            vo.getResult().setPlayerDelete(true);
            vo.setProperties(PlayerAdvisory.PROP_PLAYER_DELETE);
            vo = ServiceTool.playerAdvisoryService().updateOnly(vo);
        }
        if (vo.isSuccess()) {
            vo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "delete.success"));
        } else {
            vo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, "delete.failed"));
        }
        HashMap map = new HashMap(2);
        map.put("msg", StringTool.isNotBlank(vo.getOkMsg()) ? vo.getOkMsg() : vo.getErrMsg());
        map.put("state", Boolean.valueOf(vo.isSuccess()));
        return map;
    }
    /**
     * 玩家中心-删除系统消息
     *
     * @param vo
     * @param ids
     * @return
     */
    @RequestMapping("/deleteNoticeReceived")
    @ResponseBody
    public Map deleteNoticeReceived(NoticeReceiveVo vo, String ids) {
        String[] idArray = ids.split(",");
        List<Integer> list = new ArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        vo.setIds(list);
        boolean bool = ServiceTool.noticeService().deleteSiteMsg(vo);

        if (bool) {
            vo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "delete.success"));
        } else {
            vo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, "delete.failed"));
        }
        HashMap map = new HashMap(2);
        map.put("msg", StringTool.isNotBlank(vo.getOkMsg()) ? vo.getOkMsg() : vo.getErrMsg());
        map.put("state", Boolean.valueOf(vo.isSuccess()));
        return map;
    }


    /**
     * 玩家中心-系统消息-标记已读
     *
     * @param ids
     * @return
     */
    @RequestMapping("/systemMessageEditStatus")
    @ResponseBody
    public Map messageEditStatus(NoticeReceiveVo noticeReceiveVo, String ids) {
        String[] idArray = ids.split(",");
        List<Integer> list = new ArrayList();
        for (String id : idArray) {
            list.add(Integer.valueOf(id));
        }
        noticeReceiveVo.setIds(list);
        boolean b = ServiceTool.noticeService().markSiteMsg(noticeReceiveVo);

        if (b) {
            noticeReceiveVo.setOkMsg(LocaleTool.tranMessage(_Module.COMMON, "update.success"));
        } else {
            noticeReceiveVo.setErrMsg(LocaleTool.tranMessage(_Module.COMMON, "update.failed"));
        }
        HashMap map = new HashMap(2);
        map.put("msg", StringTool.isNotBlank(noticeReceiveVo.getOkMsg()) ? noticeReceiveVo.getOkMsg() : noticeReceiveVo.getErrMsg());
        map.put("state", Boolean.valueOf(b));
        return map;
    }

    /**
     * 玩家中心-我的消息-标记已读
     *
     * @param ids
     * @return
     */
    @RequestMapping("/getSelectAdvisoryMessageIds")
    @ResponseBody
    public Map getSelectAdvisoryMessageIds(String ids) {
        String[] id = ids.split(",");
        for (String messageId : id) {
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            parListVo = ServiceTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            //判断是否已读
            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(Integer.valueOf(messageId));
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});
            ServiceTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(Integer.valueOf(messageId));
                ServiceTool.playerAdvisoryReadService().insert(parVo);
            }
        }
        HashMap map = new HashMap(1);
        map.put("state", true);
        return map;
    }
    /**
     * 玩家中心-系统消息详细
     *
     * @param vo
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/announcementDetail")
    public String announcementDetail(VNoticeReceivedTextVo vo, VPlayerAdvisoryListVo aListVo, NoticeReceiveVo noticeReceiveVo, Model model, HttpServletRequest request) {
        List list = new ArrayList();
        list.add(noticeReceiveVo.getSearch().getId());
        noticeReceiveVo.setIds(list);
        ServiceTool.noticeService().markSiteMsg(noticeReceiveVo);

        vo = ServiceTool.noticeService().fetchReceivedSiteMsgDetail(vo);
        vo.getResult().setContent(vo.getResult().getContent().replaceAll("\\$\\{user\\}",SessionManager.getUserName()));
        vo.getResult().setContent(vo.getResult().getContent().replaceAll("\\$\\{sitename\\}",SessionManager.getSiteName(request)));
        vo.getResult().setTitle(vo.getResult().getTitle().replaceAll("\\$\\{user\\}",SessionManager.getUserName()));
        model.addAttribute("command", vo);
        model.addAttribute("messageType","sysMessage");

        return MESSAGE_DETAIL_URL;
    }
    /**
     * 系统公告详细
     *
     * @param model
     * @param vSystemAnnouncementListVo
     * @return
     */
    @RequestMapping("/systemNoticeDetail")
    public String systemNoticeDetail(Model model, VPlayerAdvisoryListVo aListVo, VSystemAnnouncementListVo vSystemAnnouncementListVo) {
        vSystemAnnouncementListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        vSystemAnnouncementListVo = ServiceTool.vSystemAnnouncementService().search(vSystemAnnouncementListVo);
        model.addAttribute("vSystemAnnouncementListVo", vSystemAnnouncementListVo);
        model.addAttribute("messageType","sysNotice");
        return MESSAGE_DETAIL_URL;
    }
    /**
     * 我的消息-详细
     *
     * @param vPlayerAdvisoryListVo
     * @param id
     * @param continueQuizId
     * @param listVo
     * @param model
     * @return
     */
    @RequestMapping({"/playerAdvisoryDetail"})
    public String playerAdvisory(VPlayerAdvisoryListVo aListVo, VPlayerAdvisoryListVo vPlayerAdvisoryListVo, Integer id, Integer continueQuizId, VPlayerAdvisoryReplyListVo listVo, Model model) {
        //表单校验
//        model.addAttribute("validate", JsRuleCreator.create(AdvisoryMessageForm.class, "result"));

        //当前咨询信息
        listVo.getPaging().setPageSize(60);
        if (continueQuizId != null) {
            vPlayerAdvisoryListVo.getSearch().setId(continueQuizId);
        } else {
            vPlayerAdvisoryListVo.getSearch().setId(id);
        }
        List<VPlayerAdvisory> vPlayerAdvisoryList = ServiceTool.vPlayerAdvisoryService().searchVPlayerAdvisoryReply(vPlayerAdvisoryListVo);
        Map map = new TreeMap(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Integer) o1) - ((Integer) o2);
            }
        });
        for (VPlayerAdvisory obj : vPlayerAdvisoryList) {
            //回复标题和内容
            listVo.getSearch().setPlayerAdvisoryId(obj.getId());
            listVo = ServiceTool.vPlayerAdvisoryReplyService().search(listVo);
            map.put(obj.getId(), listVo);

            //判断是否已读
            //当前回复表Id
            PlayerAdvisoryReplyListVo parListVo = new PlayerAdvisoryReplyListVo();
            parListVo.getSearch().setPlayerAdvisoryId(obj.getId());
            parListVo = ServiceTool.playerAdvisoryReplyService().searchByIdPlayerAdvisoryReply(parListVo);

            PlayerAdvisoryReadVo readVo = new PlayerAdvisoryReadVo();
            readVo.getSearch().setUserId(SessionManager.getUserId());
            readVo.getSearch().setPlayerAdvisoryId(obj.getId());
            readVo.getQuery().setCriterions(new Criterion[]{new Criterion(PlayerAdvisoryRead.PROP_USER_ID, Operator.EQ, readVo.getSearch().getUserId())
                    , new Criterion(PlayerAdvisoryRead.PROP_PLAYER_ADVISORY_ID, Operator.EQ, readVo.getSearch().getPlayerAdvisoryId())});
            ServiceTool.playerAdvisoryReadService().batchDeleteCriteria(readVo);

            for (PlayerAdvisoryReply replay : parListVo.getResult()) {
                PlayerAdvisoryReadVo parVo = new PlayerAdvisoryReadVo();
                parVo.setResult(new PlayerAdvisoryRead());
                parVo.getResult().setUserId(SessionManager.getUserId());
                parVo.getResult().setPlayerAdvisoryReplyId(replay.getId());
                parVo.getResult().setPlayerAdvisoryId(obj.getId());
                ServiceTool.playerAdvisoryReadService().insert(parVo);
            }
        }

        model.addAttribute("command", vPlayerAdvisoryList);
        model.addAttribute("messageType","myMessage");
        model.addAttribute("map", map);

        return MESSAGE_DETAIL_URL;
    }
    /**
     * 玩家中心-游戏消息详细显示
     *
     * @param model
     * @return
     */
    @RequestMapping("/gameNoticeDetail")
    public String gameNoticeDetail(VSystemAnnouncementListVo vSystemAnnouncementListVo, Model model) {
        vSystemAnnouncementListVo.getSearch().setLocal(SessionManager.getLocale().toString());
        vSystemAnnouncementListVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        vSystemAnnouncementListVo = ServiceTool.vSystemAnnouncementService().search(vSystemAnnouncementListVo);
        model.addAttribute("vSystemAnnouncementListVo", vSystemAnnouncementListVo);
        model.addAttribute("messageType","gameNotice");
        return MESSAGE_DETAIL_URL;
    }
    /**
     * 校验验证码
     *
     * @param code
     * @param request
     * @return
     */
    @RequestMapping("/checkCaptcha")
    @ResponseBody
    public boolean checkCaptcha(@RequestParam("code") String code, HttpServletRequest request) {
        if (StringTool.isEmpty(code)) {
            return false;
        }
        return code.equalsIgnoreCase((String) SessionManager.getAttribute(SessionKey.S_CAPTCHA_PREFIX + CaptchaUrlEnum.CODE_CONTINUE_QUESTION.getSuffix()));
    }

    private VPlayerAdvisoryListVo unReadCount(VPlayerAdvisoryListVo listVo, Model model) {
        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long length = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        model.addAttribute("length", length);
        //玩家咨询-未读数量
        listVo.setSearch(null);
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceTool.vPlayerAdvisoryService().search(listVo);
        Integer advisoryUnReadCount = 0;
        String tag  = "";
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
                if(readVo.getResult()==null && !tag.contains(replay.getPlayerAdvisoryId().toString())){
                    advisoryUnReadCount++;
                    tag+=replay.getPlayerAdvisoryId().toString()+",";
                }
            }
        }
        //判断已标记的咨询Id除外的未读咨询id,添加未读标记isRead=false;
        String [] tags = tag.split(",");
        for(VPlayerAdvisory vo:listVo.getResult()){
            for(int i=0;i<tags.length;i++){
                if(tags[i]!=""){
                    VPlayerAdvisoryVo pa = new VPlayerAdvisoryVo();
                    pa.getSearch().setId(Integer.valueOf(tags[i]));
                    VPlayerAdvisoryVo vpaVo = ServiceTool.vPlayerAdvisoryService().get(pa);
                    if(vo.getId().equals(vpaVo.getResult().getContinueQuizId()) || vo.getId().equals(vpaVo.getResult().getId())){
                        vo.setIsRead(false);
                    }
                }
            }
        }
        sysMessageUnReadCount = length;
        this.advisoryUnReadCount = advisoryUnReadCount;
        model.addAttribute("sysMessageUnReadCount",sysMessageUnReadCount);
        model.addAttribute("advisoryUnReadCount", advisoryUnReadCount);
        return listVo;
    }
    @RequestMapping("/unReadCount")
    @ResponseBody
    private String getUnReadCount(VPlayerAdvisoryListVo listVo) {
        //系统消息-未读数量
        VNoticeReceivedTextVo vNoticeReceivedTextVo = new VNoticeReceivedTextVo();
        Long length = ServiceTool.noticeService().fetchUnclaimedMsgCount(vNoticeReceivedTextVo);
        //玩家咨询-未读数量
        listVo.setSearch(null);
        listVo.getSearch().setSearchType("player");
        listVo.getSearch().setPlayerId(SessionManager.getUserId());
        listVo.getSearch().setAdvisoryTime(DateTool.addDays(new Date(), -30));
        listVo.getSearch().setPlayerDelete(false);
        listVo = ServiceTool.vPlayerAdvisoryService().search(listVo);
        Integer advisoryUnReadCount = 0;
        String tag  = "";
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
                if(readVo.getResult()==null && !tag.contains(replay.getPlayerAdvisoryId().toString())){
                    advisoryUnReadCount++;
                    tag+=replay.getPlayerAdvisoryId().toString()+",";
                }
            }
        }
        Map map = new HashMap();
        map.put("sysMessageUnReadCount",length);
        map.put("advisoryUnReadCount",advisoryUnReadCount);
        return JsonTool.toJson(map);
    }
}
