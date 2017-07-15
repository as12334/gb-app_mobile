package so.wwb.gamebox.mobile.annou.controller;

import org.soul.commons.lang.DateTool;
import org.soul.commons.net.ServletTool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.company.operator.vo.VSystemAnnouncementListVo;
import so.wwb.gamebox.model.master.enums.AnnouncementTypeEnum;

import javax.servlet.http.HttpServletRequest;


/**
 * 玩家中心-系统公告视图控制器
 *
 * @author orange
 * @time 2015-10-26 16:29:08
 */
@Controller
@RequestMapping("/annou")
public class AnnouController {
    // 系统公告
    private static final String SYSTEM_ANNOUNCEMENT_URL = "/annou/SystemAnnouncement";
    // 游戏公告
    private static final String GAME_ANNOUNCEMENT_URL = "/annou/GameAnnouncement";
    // 公告详情
    private static final String DETAIL_URL = "/annou/Detail";

    /**
     * 系统公告
     */
    /*@RequestMapping("/systemAnnouncement")
    public String systemAnnouncement(VSystemAnnouncementListVo listVo, HttpServletRequest request, Model model) {
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.SYSTEM.getCode());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        listVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
        model.addAttribute("command", listVo);
        return ServletTool.isAjaxSoulRequest(request) ? SYSTEM_ANNOUNCEMENT_URL + "Partial" : SYSTEM_ANNOUNCEMENT_URL;
    }

    *//**
     * 游戏公告
     *//*
    @RequestMapping("/gameAnnouncement")
    public String gameAnnouncement(VSystemAnnouncementListVo listVo, Model model, HttpServletRequest request) {
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        listVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
        model.addAttribute("command", listVo);
        return ServletTool.isAjaxSoulRequest(request) ? GAME_ANNOUNCEMENT_URL + "Partial" : GAME_ANNOUNCEMENT_URL;
    }

    *//**
     * 公告详细
     *//*
    @RequestMapping("/detail")
    public String detail(Model model, VSystemAnnouncementVo vo) {
        vo.getSearch().setLocal(SessionManager.getLocale().toString());
        vo = ServiceTool.vSystemAnnouncementService().search(vo);
        model.addAttribute("command", vo);
        return DETAIL_URL;
    }*/

    /**
     * 公告
     * @param listVo
     * @param model
     * @return
     */
    @RequestMapping("/{type}")
    public String announcement(@PathVariable String type, VSystemAnnouncementListVo listVo, Model model,
                               HttpServletRequest request) {
        String page ="";
        if ("system".equals(type)) {
            page = "System";
            model.addAttribute("command", systemListVo(listVo));
        } else if ("game".equals(type)) {
            page = "Game";
            model.addAttribute("game", gameListVo(listVo));
        }
        model.addAttribute("type",page);
        return ServletTool.isAjaxSoulRequest(request) ? "/annou/"+page:"/annou/Announcement";
    }

    @RequestMapping("/game2")
    public String game(VSystemAnnouncementListVo listVo, Model model) {
        model.addAttribute("game", gameListVo(listVo));
        return "/annou/Game2";
    }

    @RequestMapping("/system2")
    public String system(VSystemAnnouncementListVo listVo, Model model) {
        model.addAttribute("system", systemListVo(listVo));
        return "/annou/System2";
    }

    private VSystemAnnouncementListVo systemListVo(VSystemAnnouncementListVo listVo) {
        if(listVo.getSearch().getStartTime()==null && listVo.getSearch().getEndTime()==null){
            listVo.getSearch().setStartTime(DateTool.addMonths(SessionManager.getDate().getNow(), -1));
            listVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        }
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.SYSTEM.getCode());
        listVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
        return listVo;
    }

    private VSystemAnnouncementListVo gameListVo(VSystemAnnouncementListVo listVo) {
        if(listVo.getSearch().getStartTime()==null && listVo.getSearch().getEndTime()==null){
            listVo.getSearch().setStartTime(DateTool.addMonths(SessionManager.getDate().getNow(), -1));
            listVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        }
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        listVo.getSearch().setPublishTime(SessionManager.getUser().getCreateTime());
        listVo.getSearch().setAnnouncementType(AnnouncementTypeEnum.GAME.getCode());
        listVo = ServiceTool.vSystemAnnouncementService().searchMasterSystemNotice(listVo);
        return listVo;
    }
}