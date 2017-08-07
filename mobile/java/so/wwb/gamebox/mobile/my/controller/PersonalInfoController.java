package so.wwb.gamebox.mobile.my.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.locale.LocaleTool;
import org.soul.model.msg.notice.po.NoticeContactWay;
import org.soul.model.msg.notice.vo.NoticeContactWayListVo;
import org.soul.model.security.privilege.vo.SysUserProtectionVo;
import org.soul.model.security.privilege.vo.SysUserVo;
import org.soul.web.validation.form.annotation.FormModel;
import org.soul.web.validation.form.js.JsRuleCreator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.my.form.PersonInfoMobileForm;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.ServiceTool;
import so.wwb.gamebox.model.common.PrivilegeStatusEnum;
import so.wwb.gamebox.model.master.player.po.UserPlayer;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人资料
 * Created by bruce on 16-7-19.
 */
@Controller
@RequestMapping("/personalInfo")
public class PersonalInfoController {

    @RequestMapping("/index")
    public String index(Model model) {
        Map<String, Object> map = initSecurityPwdMap();
        String state = String.valueOf(map.get("state"));
        if (PrivilegeStatusEnum.CODE_100.getCode().equals(state)) {
            model.addAttribute("sysUser", SessionManager.getUser());
            //获取联系方式
            getNoticeContactWay(model);

            //安全问题
            SysUserProtectionVo sysUserProtectionVo = getSysUserProtectionVo(SessionManager.getUserId());
            model.addAttribute("sysUserProtectionVo", sysUserProtectionVo);

            model.addAttribute("validateRule", JsRuleCreator.create(PersonInfoMobileForm.class));
            return "/mine/MineUser";
        }
        return "/errors/403";
    }

    /**
     * 初始化安全密码验证MAP
     */
    private Map<String, Object> initSecurityPwdMap() {
        Map<String, Object> map = SessionManager.getPrivilegeStatus();
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     * 手机-更新个人信息
     *
     * @param sysUserVo
     * @return
     */
    @RequestMapping("/updatePersonInfo")
    @ResponseBody
    public Map updatePersonInfo(SysUserVo sysUserVo, UserPlayerVo userPlayerVo, HttpServletRequest request,
                                @FormModel @Valid PersonInfoMobileForm form, BindingResult result) {
        Map map = new HashMap();

        //前置验证
        if (result.hasErrors()) {
            map.put("status", false);
            map.put("msg", LocaleTool.tranMessage("my_auto", "更新失败"));
            return map;
        }
        sysUserVo.getResult().setId(SessionManager.getUserId());
        if (userPlayerVo.getPhone() != null && StringTool.isNotBlank(userPlayerVo.getPhone().getContactValue())) {
                UserPlayer userPlayer = new UserPlayer();
                userPlayer.setId(SessionManager.getUserId());
                userPlayer.setPhoneCode("+86");
                userPlayerVo.setResult(userPlayer);
                userPlayerVo.setPhone(userPlayerVo.getPhone());
        }
        sysUserVo = ServiceTool.userPlayerService().mobileUpdatePersonInfo(sysUserVo, userPlayerVo);
        map.put("status", sysUserVo.isSuccess());
        if (sysUserVo.isSuccess()) {
            SessionManager.refreshUser();
            map.put("msg", LocaleTool.tranMessage("my_auto", "更新成功"));
        } else {
            map.put("msg", LocaleTool.tranMessage("my_auto", "更新失败"));
        }
        return map;
    }

    /**
     * 获取联系方式
     *
     * @param model
     */
    private void getNoticeContactWay(Model model) {
        NoticeContactWayListVo noticeContactWayListVo = new NoticeContactWayListVo();
        noticeContactWayListVo.getSearch().setUserId(SessionManager.getUserId());
        List<NoticeContactWay> noticeContactWayList = ServiceTool.noticeContactWayService().fetchByUserIdAndTypesAndStatuses(noticeContactWayListVo);
        if (noticeContactWayList != null && noticeContactWayList.size() > 0) {
            Map map = CollectionTool.toEntityMap(noticeContactWayList, NoticeContactWay.PROP_CONTACT_TYPE, Integer.class);
            model.addAttribute("noticeContactWayMap", map);
            model.addAttribute("noticeContactWays", noticeContactWayList.size());
        } else {
            model.addAttribute("noticeContactWays", 0);
        }

    }

    /**
     * 获取用户设置的密保
     *
     * @param userId
     * @return
     */
    private SysUserProtectionVo getSysUserProtectionVo(Integer userId) {
        SysUserProtectionVo sysUserProtectionVo = new SysUserProtectionVo();
        sysUserProtectionVo.getSearch().setId(userId);
        sysUserProtectionVo = ServiceTool.sysUserProtectionService().get(sysUserProtectionVo);
        return sysUserProtectionVo;
    }

}


