package so.wwb.gamebox.mobile.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.soul.commons.collections.CollectionTool;
import org.soul.commons.collections.MapTool;
import org.soul.commons.currency.CurrencyTool;
import org.soul.commons.data.json.JsonTool;
import org.soul.commons.init.context.CommonContext;
import org.soul.commons.lang.string.EncodeTool;
import org.soul.commons.lang.string.I18nTool;
import org.soul.commons.lang.string.StringEscapeTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.math.NumberTool;
import org.soul.commons.net.ServletTool;
import org.soul.commons.qrcode.QrcodeDisTool;
import org.soul.commons.security.Base36;
import org.soul.commons.security.CryptoTool;
import org.soul.model.security.privilege.po.SysUser;
import org.soul.model.security.privilege.po.SysUserStatus;
import org.soul.model.sys.po.SysParam;
import org.soul.web.session.SessionManagerBase;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.dubbo.ServiceBossTool;
import so.wwb.gamebox.common.dubbo.ServiceSiteTool;
import so.wwb.gamebox.iservice.boss.IAppUpdateService;
import so.wwb.gamebox.iservice.company.site.ISiteAppUpdateService;
import so.wwb.gamebox.mobile.init.annotataion.Upgrade;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.mobile.tools.OsTool;
import so.wwb.gamebox.model.DictEnum;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteI18nEnum;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.boss.po.AppUpdate;
import so.wwb.gamebox.model.boss.vo.AppUpdateVo;
import so.wwb.gamebox.model.company.enums.DomainPageUrlEnum;
import so.wwb.gamebox.model.company.lottery.po.SiteLottery;
import so.wwb.gamebox.model.company.setting.po.SysCurrency;
import so.wwb.gamebox.model.company.site.po.SiteApiType;
import so.wwb.gamebox.model.company.site.po.SiteAppUpdate;
import so.wwb.gamebox.model.company.site.po.SiteI18n;
import so.wwb.gamebox.model.company.site.vo.SiteAppUpdateVo;
import so.wwb.gamebox.model.company.sys.po.VSysSiteDomain;
import so.wwb.gamebox.model.enums.OSTypeEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttAnnouncementTypeEnum;
import so.wwb.gamebox.model.master.content.enums.CttDocumentEnum;
import so.wwb.gamebox.model.master.content.enums.CttPicTypeEnum;
import so.wwb.gamebox.model.master.content.po.*;
import so.wwb.gamebox.model.master.content.vo.CttDocumentI18nListVo;
import so.wwb.gamebox.model.master.enums.ActivityTypeEnum;
import so.wwb.gamebox.model.master.enums.AppTypeEnum;
import so.wwb.gamebox.model.master.enums.CarouselTypeEnum;
import so.wwb.gamebox.model.master.enums.CttCarouselTypeEnum;
import so.wwb.gamebox.model.master.operation.vo.PlayerActivityMessage;
import so.wwb.gamebox.model.master.player.vo.PlayerApiListVo;
import so.wwb.gamebox.model.master.player.vo.UserPlayerVo;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardListVo;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardRecord;
import so.wwb.gamebox.model.master.report.vo.PlayerRecommendAwardVo;
import so.wwb.gamebox.model.master.setting.po.GradientTemp;
import so.wwb.gamebox.web.SessionManagerCommon;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.web.common.SiteCustomerServiceHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * 首页 Controller
 * Created by jeffrey on 16-7-15.
 */
@Controller
public class IndexController extends BaseApiController {
    private Log LOG = LogFactory.getLog(IndexController.class);

    private static final int DEFAULT_MIN_TIME = -6;
    private static final int CHESS_TEST_SITE = 18;
    private static final int CHESS_PRODUCE_SITE_MIN = 7000;

    @RequestMapping("/game")
    public String game(Integer typeId, Model model, HttpServletRequest request) {
        List<SiteApiType> apiTypes = getApiTypes();
        if (typeId == null || !NumberTool.isNumber(String.valueOf(typeId))) {
            if (CollectionTool.isNotEmpty(apiTypes)) {
                // 取出第一个API-TYPE
                typeId = apiTypes.get(0).getApiTypeId();
            } else {
                // 没有，默认取真人
                typeId = ApiTypeEnum.LIVE_DEALER.getCode();
            }
        }
        if (typeId == -1) {
            model.addAttribute("channel", "activity");
        } else {
            model.addAttribute("channel", "game");
        }
        model.addAttribute("apiTypeId", typeId);
        model.addAttribute("apiTypes", apiTypes);
        model.addAttribute("carousels", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        model.addAttribute("announcement", getAnnouncement());
        model.addAttribute("sysUser", SessionManager.getUser());
        model.addAttribute("sysDomain", getSiteDomain(request));
        return "/game/Game";
    }

    @RequestMapping("/index")
    @Upgrade(upgrade = true)
    public String toIndex(Model model, HttpServletRequest request,HttpServletResponse response) {
        String c = request.getParameter("c");
        if (StringTool.isNotBlank(c)) {
            try {
                response.sendRedirect("/signUp/index.html?c="+c);
            }catch (IOException e) {
                LOG.info("推广链接跳转注册失败，推广码:["+c+"],错误信息:"+e.getMessage());
            }
            SessionManager.setRecommendUserCode(c);
        }
        if (ParamTool.isLotterySite()) {
            getAppPath(model, request);
        }
        //棋牌官网站点
        Integer siteId = SessionManager.getSiteId();
        if(siteId != null && (siteId == CHESS_TEST_SITE || siteId >= CHESS_PRODUCE_SITE_MIN)){
            return  "/ToChessIndex";
        }
        return "/ToIndex";
    }

    @RequestMapping("/mainIndex")
    @Upgrade(upgrade = true)
    public String index(Model model, HttpServletRequest request, HttpServletResponse response, Integer skip, String path) {
        model.addAttribute("skip", skip);
        model.addAttribute("channel", "index");
        model.addAttribute("apiTypes", getApiTypes());
        model.addAttribute("announcement", getAnnouncement());
        model.addAttribute("sysDomain", getSiteDomain(request));
        String code = CommonContext.get().getSiteCode();
        model.addAttribute("code", code);
        if (ParamTool.isLotterySite()) {
            model.addAttribute("lotteries", getLottery(request, 19));
        }
        if (ParamTool.isMobileUpgrade()) {
            //查询游戏类型对应的分类
            getApiTypeGame(model);
            //关于我们/注册条款
            model.addAttribute("path", path);
            if (StringTool.isNotBlank(path)) {
                getAboutAndTerms(path, model, request);
            }
        } else {
            initFloatPic(model);
        }
        //手机弹窗广告、查询Banner和公告
        getBannerAndAd(model, request);
        model.addAttribute("isShowQrCode", ParamTool.isLoginShowQrCode()); //这是二维码开启开关
        //棋牌官网站点
        Integer siteId = SessionManager.getSiteId();
        if(siteId != null && (siteId == CHESS_TEST_SITE || siteId >= CHESS_PRODUCE_SITE_MIN)){
            String userAgent = OsTool.getOsInfo(request);
            String url = null;
            //android自定义下载地址 androidUrl
            if (AppTypeEnum.ANDROID.getCode().contains(userAgent)) {
                url = getAndroidDownloadUrl();
            } else if (AppTypeEnum.IOS.getCode().contains(userAgent)) { //ios下载页面
                url = getIosDownloadUrl();
            }
            if (StringTool.isBlank(url)) {
                //ios
                IAppUpdateService appUpdateService = ServiceBossTool.appUpdateService();
                ISiteAppUpdateService siteAppUpdateService = ServiceBossTool.siteAppUpdateService();
                getIosInfo(model, code, appUpdateService, siteAppUpdateService);
                //android
                getAndroidInfo(model, request, code, appUpdateService, siteAppUpdateService);
            } else {
                try {
                    response.sendRedirect(url);
                } catch (IOException e) {
                    LOG.error(e, "app请求外接地址错误,地址:{0}", url);
                }
            }

            return  "/ChessIndex";
        }
        return "/Index";
    }

    /**
     * 获取IOS信息
     */
    private void getIosInfo(Model model, String code, IAppUpdateService appUpdateService, ISiteAppUpdateService siteAppUpdateService) {
        boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
        if (isMobileUpgrade) {
            Integer siteId = CommonContext.get().getSiteId();
            SiteAppUpdateVo iosVo = new SiteAppUpdateVo();
            iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
            iosVo.getSearch().setSiteId(siteId);
            SiteAppUpdate iosApp = siteAppUpdateService.queryNewApp(iosVo);
            if (iosApp != null) {
                String versionName = iosApp.getVersionName();
                String appUrl = iosApp.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        } else {
            AppUpdateVo iosVo = new AppUpdateVo();
            iosVo.getSearch().setAppType(AppTypeEnum.IOS.getCode());
            AppUpdate iosApp = appUpdateService.queryNewApp(iosVo);
            if (iosApp != null) {
                String versionName = iosApp.getVersionName();
                String appUrl = iosApp.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        }

    }

    /**
     * 获取android APP信息
     */
    private void getAndroidInfo(Model model, HttpServletRequest request, String code, IAppUpdateService appUpdateService, ISiteAppUpdateService siteAppUpdateService) {
        boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
        String appDomain = fetchAppDownloadDomain(request);
        if (StringTool.isBlank(appDomain)) {
            appDomain = ParamTool.appDmain(request.getServerName());
        }
        if (isMobileUpgrade) {
            Integer siteId = CommonContext.get().getSiteId();
            SiteAppUpdateVo androidVo = new SiteAppUpdateVo();
            androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
            androidVo.getSearch().setSiteId(siteId);
            SiteAppUpdate androidApp = siteAppUpdateService.queryNewApp(androidVo);
            if (androidApp != null) {
                String versionName = androidApp.getVersionName();
                String appUrl = androidApp.getAppUrl();
                fillAndroidInfo(model, code, appDomain, versionName, appUrl);
            }
        } else {
            AppUpdateVo androidVo = new AppUpdateVo();
            androidVo.getSearch().setAppType(AppTypeEnum.ANDROID.getCode());
            AppUpdate androidApp = appUpdateService.queryNewApp(androidVo);
            if (androidApp != null) {
                String versionName = androidApp.getVersionName();
                fillAndroidInfo(model, code, appDomain, versionName, androidApp.getAppUrl());
            }
        }

    }


    @RequestMapping("/index/floatPic")
    @Upgrade(upgrade = true)
    public String floatPic(Model model) {
        initFloatPic(model);
        return "/index.include/Envelope";
    }

    @RequestMapping("/index/dialog")
    @Upgrade(upgrade = true)
    public String dialog(HttpServletRequest request, Model model) {
        getBannerAndAd(model, request);
        return "/index.include/include.dialog";
    }

    /**
     * 注册条款
     *
     * @return
     */
    @RequestMapping("/getRegisterRules")
    @Upgrade(upgrade = true)
    public String getRegisterRules(Model model, String path) {
        if ("terms".equals(path) || "protocol".equals(path)) {
            SiteI18n terms = Cache.getSiteI18n(SiteI18nEnum.MASTER_SERVICE_TERMS).get(SessionManager.getLocale().toString());
            model.addAttribute("terms", terms);
        }
        return "/registe/RegisteRules";
    }

    /**
     * 关于我们
     *
     * @return
     */
    @RequestMapping("/about")
    @Upgrade(upgrade = true)
    public String getAbout(HttpServletRequest request, String path, Model model) {

        if ("about".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("aboutUs");
            CttDocumentI18n cttDocumentI18n = ServiceSiteTool.cttDocumentI18nService().queryAboutDocument(listVo);
            if (cttDocumentI18n != null) {
                cttDocumentI18n.setContent(cttDocumentI18n.getContent()
                        .replace("${weburl}", request.getServerName())
                        .replace("${customerservice}", "")
                        .replace("${company}", SessionManagerCommon.getSiteName(request)));
            }
            model.addAttribute("about", cttDocumentI18n);
        }
        return "/about/About";
    }

    /**
     * 分享好友
     *
     * @return
     */
    @RequestMapping("/recommend")
    @Upgrade(upgrade = true)
    public String recommend(Model model, PlayerRecommendAwardListVo listVo, HttpServletRequest request) {
        UserPlayerVo userPlayerVo = new UserPlayerVo();
        userPlayerVo.getSearch().setId(SessionManager.getUserId());
        userPlayerVo = ServiceSiteTool.userPlayerService().get(userPlayerVo);
        if (userPlayerVo.getResult() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("/register.html?c=");
            String invitationCode = userPlayerVo.getResult().getRegistCode() + SessionManager.getUserId().toString();
            sb.append(Base36.encryptIgnoreCase(invitationCode));
            //分享码
            model.addAttribute("code", sb.toString());
            model.addAttribute("recommendCode", Base36.encryptIgnoreCase(invitationCode));
            LOG.info("玩家邀请码:[" + userPlayerVo.getResult().getRegistCode() + "][" + SessionManager.getUserId().toString() + "]" + Base36.encryptIgnoreCase(invitationCode));
        }
        //如果这个参数有值，表示有单次推荐奖励
        model.addAttribute("theWay", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_THEWAY).getParamValue());
        //满足存款条件后谁获利：1 表示双方获取奖励 2表示你将会得到 其他表示你推荐的好友会获取到
        model.addAttribute("reward", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD).getParamValue());
        //将会获取到的金额值
        model.addAttribute("money", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_MONEY).getParamValue());
        //有红利奖励显示分享红利标志，没有不显示
        model.addAttribute("bonus", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_BONUS).getActive());
        // 存款金额满多少钱
        model.addAttribute("witchWithdraw", ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_REWARD_THEWAY).getParamValue());
        //有效分享人数 和所对应的红利
        SysParam bonusJson = ParamTool.getSysParam(SiteParamEnum.SETTING_RECOMMENDED_BONUS_JSON);
        ArrayList<GradientTemp> gradientTempArrayList = JsonTool.fromJson(bonusJson.getParamValue(), new TypeReference<ArrayList<GradientTemp>>() {
        });
        model.addAttribute("gradientTempArrayList", gradientTempArrayList);

        //查询被该玩家推荐的好友记录奖励表
        listVo.getSearch().setUserId(SessionManager.getUserId());
        initSearchDate(listVo);
        listVo = ServiceSiteTool.playerRecommendAwardService().searchRewardRecodeToApp(listVo);
        List<PlayerRecommendAwardRecord> list = changeToApp(listVo.getRecommendAwardRecords());
        model.addAttribute("command", list);
        //查询推荐记录 默认时间区间
        model.addAttribute("defaultMinDate", SessionManager.getDate().addDays(DEFAULT_MIN_TIME));
        model.addAttribute("defaultMaxDate", SessionManager.getDate().getNow());

        //查询推荐人数 获取奖励 红利
        PlayerRecommendAwardVo playerVo = new PlayerRecommendAwardVo();
        playerVo.getSearch().setUserId(SessionManager.getUserId());
        model.addAttribute("sign", getCurrencySign());
        model.addAttribute("recommend", ServiceSiteTool.playerRecommendAwardService().searchRewardUserAndBonus(playerVo));
        //活动规则
        Map siteI18nMap = Cache.getSiteI18n(SiteI18nEnum.MASTER_RECOMMEND_RULE);
        if (MapTool.isNotEmpty(siteI18nMap)) {
            model.addAttribute("activityRules", siteI18nMap.get(SessionManager.getLocale().toString()));
        }

        //是否是ajax请求
        if (ServletTool.isAjaxSoulRequest(request)) {
            return "/recommend/RecommendList";
        }

        return "/recommend/Recommend";
    }

    /**
     * 推荐记录
     *
     * @param records
     * @return
     */
    private List<PlayerRecommendAwardRecord> changeToApp(List<PlayerRecommendAwardRecord> records) {
        for (PlayerRecommendAwardRecord record : records) {
            record.setRecommendUserName(StringTool.overlayString(record.getRecommendUserName()));
            record.setStatus(SysUserStatus.enumOf(record.getStatus()).getTrans());
            record.setRewardAmount(record.getRewardAmount() == null ? BigDecimal.ZERO : record.getRewardAmount());
        }
        return records;
    }

    /**
     * 初始化 推荐记录 时间区间
     *
     * @param listVo
     */
    private void initSearchDate(PlayerRecommendAwardListVo listVo) {
        if (listVo.getSearch().getStartTime() == null) {
            listVo.getSearch().setStartTime(SessionManager.getDate().addDays(DEFAULT_MIN_TIME));
        }
        if (listVo.getSearch().getEndTime() == null) {
            listVo.getSearch().setEndTime(SessionManager.getDate().getNow());
        }

    }

    /**
     * 获得钱币类型
     *
     * @return
     */
    private String getCurrencySign() {
        SysCurrency sysCurrency = Cache.getSysCurrency().get(Cache.getSysSite().get(SessionManager.getSiteIdString()).getMainCurrency());
        if (sysCurrency != null) {
            return sysCurrency.getCurrencySign();
        }
        return "";
    }


    private void initFloatPic(Model model) {
        List<Map> floatList = new ArrayList();
        showMoneyActivityFloat(floatList);
        model.addAttribute("floatList", floatList);
    }


    // 彩票站-彩票
    private List<Map<String, String>> getLottery(HttpServletRequest request, Integer pageSize) {
        String terminal = SessionManagerCommon.fetchTerminalType(request);
        List<Map<String, String>> lotteryList = new ArrayList<>();
        List<SiteLottery> lotteries = Cache.getNormalSiteLottery(terminal, SessionManagerCommon.getSiteId());
        if (!CollectionTool.isEmpty(lotteries)) {
            if (pageSize == null || lotteries.size() < pageSize) {
                pageSize = lotteries.size();
            }

            Map<String, String> dictMap = I18nTool.getDictMapByEnum(SessionManagerCommon.getLocale(), DictEnum.LOTTERY);
            Map<String, String> map;
            for (int i = 0; i < pageSize; i++) {
                SiteLottery lottery = lotteries.get(i);
                map = new HashMap<>(3, 1f);
                map.put("type", lottery.getType());
                map.put("name", dictMap.get(lottery.getCode()));
                map.put("code", lottery.getCode());
                lotteryList.add(map);
            }
        }
        return lotteryList;
    }

    /**
     * 查询公告
     */
    protected List<CttAnnouncement> getAnnouncement() {
        Map<String, CttAnnouncement> announcement = Cache.getSiteAnnouncement();
        List<CttAnnouncement> resultList = new ArrayList<>();
        if (announcement != null) {
            for (CttAnnouncement an : announcement.values()) {
                if (StringTool.equals(an.getAnnouncementType(), CttAnnouncementTypeEnum.SITE_ANNOUNCEMENT.getCode())
                        && StringTool.equals(an.getLanguage(), SessionManager.getLocale().toString())) {
                    resultList.add(an);
                }
            }
        }
        return resultList;
    }

    /**
     * 查询Banner
     *
     * @deprecated since v1057
     */
    protected List<Map> getCarousel(HttpServletRequest request, String type) {
        Map<String, Map> carousels = (Map) Cache.getSiteCarousel();
        List<Map> resultList = new ArrayList<>();
        String webSite = ServletTool.getDomainFullAddress(request);
        if (carousels != null) {
            for (Map m : carousels.values()) {
                if ((StringTool.equalsIgnoreCase(type, m.get("type").toString()))
                        && (StringTool.equals(m.get(CttCarouselI18n.PROP_LANGUAGE).toString(), SessionManager.getLocale().toString()))
                        && (((Date) m.get("start_time")).before(new Date()) && ((Date) m.get("end_time")).after(new Date()))
                        && (MapTool.getBoolean(m, "status") == null || MapTool.getBoolean(m, "status") == true)) {
                    String link = String.valueOf(m.get("link"));
                    if (StringTool.isNotBlank(link)) {
                        if (link.contains("${website}")) {
                            link = link.replace("${website}", webSite);
                        }
                    }
                    m.put("link", link);
                    resultList.add(m);
                }
            }
        }
        return resultList;
    }

    private void getBannerAndAd(Model model, HttpServletRequest request) {
        Map<String, Map> carouselMap = (Map) Cache.getSiteCarousel();
        if (MapTool.isEmpty(carouselMap)) {
            return;
        }
        String webSite = ServletTool.getDomainFullAddress(request);
        List<Map> phoneDialog = new ArrayList<>();
        List<Map> carousels = new ArrayList<>();
        String phoneDialogType = CttCarouselTypeEnum.CAROUSEL_TYPE_PHONE_DIALOG.getCode();
        String bannerType = CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode();
        Date date = new Date();
        String local = SessionManager.getLocale().toString();
        for (Map m : carouselMap.values()) {
            if ((StringTool.equals(m.get(CttCarouselI18n.PROP_LANGUAGE).toString(), local)) && (((Date) m.get("start_time")).before(date) && ((Date) m.get("end_time")).after(date))
                    && (MapTool.getBoolean(m, "status") == null || MapTool.getBoolean(m, "status") == true)) {
                String link = MapTool.getString(m, "link");
                if (StringTool.isNotBlank(link)) {
                    if("${website}".equals(link) || "${website}/mainIndex.html".equals(link)){ //若广告链接为当前域名则置为空串，防止循环弹窗
                        link = "";
                    }else if(link.contains("${website}")) {
                        link = link.replace("${website}", webSite);
                    }
                }
                m.put("link", link);
                if (phoneDialogType.equals(m.get("type"))) {
                    phoneDialog.add(m);
                } else if (bannerType.equals(m.get("type"))) {
                    carousels.add(m);
                }
            }
        }
        //手机弹窗广告
        model.addAttribute("phoneDialog", phoneDialog);
        //查询Banner和公告
        model.addAttribute("carousels", carousels);
    }


    /**
     * 查询红包浮动图
     */
    private CttFloatPic queryMoneyActivityFloat() {
        Map<String, CttFloatPic> floatPicMap = Cache.getFloatPic();
        Iterator<String> iter = floatPicMap.keySet().iterator();
        CttFloatPic tempFloatPic = null;
        while (iter.hasNext()) {
            String key = iter.next();
            CttFloatPic cttFloatPic = floatPicMap.get(key);
            if (CttPicTypeEnum.PROMO.getCode().equals(cttFloatPic.getPicType()) && cttFloatPic.getStatus()) {
                tempFloatPic = cttFloatPic;
                break;
            }
        }
        return tempFloatPic;
    }

    /**
     * 查找红包活动
     *
     * @return
     */
    private PlayerActivityMessage findMoneyActivity() {
        Map<String, PlayerActivityMessage> activityMessages = Cache.getMobileActivityMessages();
        String lang = SessionManagerBase.getLocale().toString();
        Iterator<String> iter = activityMessages.keySet().iterator();
        Date justNow = new Date();
        PlayerActivityMessage playerActivityMessage = null;
        while (iter.hasNext()) {
            String key = iter.next();
            if (key.endsWith(lang)) {
                playerActivityMessage = activityMessages.get(key);
                Date startTime = playerActivityMessage.getStartTime();
                Date endTime = playerActivityMessage.getEndTime();
                if (!ActivityTypeEnum.MONEY.getCode().equals(playerActivityMessage.getCode())) {
                    //不是红包活动继续
                    continue;
                }
                if (playerActivityMessage.getIsDeleted()) {
                    continue;
                }
                if (!playerActivityMessage.getIsDisplay()) {
                    continue;
                }
                if (startTime.before(justNow) && justNow.before(endTime)) {
                    return playerActivityMessage;
                }

            }
        }
        return null;
    }

    private CttFloatPicItem queryMoneyFloatPic(CttFloatPic cttFloatPic) {
        CttFloatPicItem item = null;
        Map<String, CttFloatPicItem> floatPicItemMap = Cache.getFloatPicItem();
        Iterator<String> iter = floatPicItemMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            CttFloatPicItem cttFloatPicItem = floatPicItemMap.get(key);
            if (cttFloatPicItem.getFloatPicId().equals(cttFloatPic.getId())) {
                item = cttFloatPicItem;
                break;
            }
        }
        return item;
    }

    /**
     * 显示红包浮动图
     *
     * @param floatList
     */
    protected void showMoneyActivityFloat(List<Map> floatList) {
        CttFloatPic cttFloatPic = queryMoneyActivityFloat();
        if (cttFloatPic != null) {
            PlayerActivityMessage moneyActivity = findMoneyActivity();
            CttFloatPicItem cttFloatPicItem = queryMoneyFloatPic(cttFloatPic);
            if (moneyActivity != null) {
                String activityId = CryptoTool.aesEncrypt(String.valueOf(moneyActivity.getId()), "PlayerActivityMessageListVo");
                Map floatMap = new HashMap();
                floatMap.put("type", "moneyActivity");
                floatMap.put("activityId", activityId);
                floatMap.put("floatItem", cttFloatPicItem);
                floatMap.put("cttFloatPic", cttFloatPic);
                floatMap.put("description", moneyActivity.getActivityDescription());
                floatList.add(floatMap);
            }
        }
    }

    /**
     * 关于/条款
     */
    private void getAboutAndTerms(String path, Model model, HttpServletRequest request) {
        if ("about".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("aboutUs");
            CttDocumentI18n cttDocumentI18n = ServiceSiteTool.cttDocumentI18nService().queryAboutDocument(listVo);
            if (cttDocumentI18n != null) {
                cttDocumentI18n.setContent(cttDocumentI18n.getContent().replaceAll("\\$\\{weburl\\}", request.getServerName()));
            }
            model.addAttribute("about", cttDocumentI18n);
        } else if ("terms".equals(path) || "protocol".equals(path)) {
            SiteI18n terms = Cache.getSiteI18n(SiteI18nEnum.MASTER_SERVICE_TERMS).get(SessionManager.getLocale().toString());
            model.addAttribute("terms", terms);
        }
    }

    /**
     * 关于/条款等
     */
    @RequestMapping("/index/{path}")
    public String staticPage(@PathVariable("path") String path, Model model, HttpServletRequest request) {
        if ("agent".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("agent&cooperation");
            model.addAttribute("agents", ServiceSiteTool.cttDocumentI18nService().queryAgentDocument(listVo));
        } else if ("about".equals(path)) {
            CttDocumentI18nListVo listVo = initDocument("aboutUs");
            CttDocumentI18n cttDocumentI18n = ServiceSiteTool.cttDocumentI18nService().queryAboutDocument(listVo);
            if (cttDocumentI18n != null) {
                cttDocumentI18n.setContent(cttDocumentI18n.getContent().replaceAll("\\$\\{weburl\\}", request.getServerName()));
            }
            model.addAttribute("about", cttDocumentI18n);
        } else if ("terms".equals(path) || "protocol".equals(path)) {
            SiteI18n terms = Cache.getSiteI18n(SiteI18nEnum.MASTER_SERVICE_TERMS).get(SessionManager.getLocale().toString());
            model.addAttribute("terms", terms);
        }
        return "/index/" + path;
    }


    private CttDocumentI18nListVo initDocument(String code) {
        CttDocumentI18nListVo listVo = new CttDocumentI18nListVo();
        listVo.getSearch().setStatus(CttDocumentEnum.ON.getCode());
        listVo.getSearch().setCode(code);
        listVo.getSearch().setLocal(SessionManager.getLocale().toString());
        return listVo;
    }

    @RequestMapping("/index/getCustomerService")
    @ResponseBody
    public String getCustomerService() {
        String csUrl = SiteCustomerServiceHelper.getMobileCustomerServiceUrl();
        return StringTool.isBlank(csUrl) ? "/commonPage/e404.html" : csUrl;
    }

    @RequestMapping("/index/gotoCustomerService")
    public void gotoCustomerService(HttpServletResponse response) {
        try {
            response.sendRedirect(SiteCustomerServiceHelper.getMobileCustomerServiceUrl());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * 用户所在时区时间
     */
    @RequestMapping(value = "/index/getUserTimeZoneDate")
    @ResponseBody
    public String getUserTimeZoneDate() {
        Map<String, String> map = new HashMap<>(2, 1f);
        map.put("dateTimeFromat", CommonContext.getDateFormat().getDAY_SECOND());
        map.put("dateTime", SessionManager.getUserDate(CommonContext.getDateFormat().getDAY_SECOND()));
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("displayName", SessionManager.getTimeZone().getID());
        return JsonTool.toJson(map);
    }

    static String getSiteDomain(HttpServletRequest request) {
        if (SessionManager.getAttribute("SESSION_DEFAULTSITE") != null) {
            return SessionManager.getAttribute("SESSION_DEFAULTSITE").toString();
        }
        String defaultSite = "";
        List<VSysSiteDomain> domainList = Cache.getSiteDomain(CommonContext.get().getSiteId(), DomainPageUrlEnum.INDEX.getCode());
        if (CollectionTool.isNotEmpty(domainList)) {
            for (VSysSiteDomain o : domainList) {
                if (o.getIsDefault()) {
                    defaultSite = o.getDomain();
                    break;
                }
            }
        }
        if (StringTool.isBlank(defaultSite)) {
            //未设置的取当前域名
            defaultSite = request.getServerName();
        }
        SessionManager.setAttribute("SESSION_DEFAULTSITE", defaultSite);
        return defaultSite;
    }

    @RequestMapping("/index/getLogoUrl")
    @ResponseBody
    public String getLogoUrl() {
        Map<String, String> urlMap = new HashMap<>(2, 1f);
        urlMap.put("logoUrl", "/ftl/commonPage/images/app_logo/app_logo_" + SessionManager.getSiteId() + ".png");
        urlMap.put("iconUrl", "/ftl/commonPage/images/app_icon/app_icon_" + SessionManager.getSiteId() + ".png");
        return JsonTool.toJson(urlMap);
    }

    @RequestMapping("/index/getBanner")
    public String getBanner(Model model, HttpServletRequest request) {
        model.addAttribute("carousels", getCarousel(request, CarouselTypeEnum.CAROUSEL_TYPE_PHONE.getCode()));
        model.addAttribute("announcement", getAnnouncement());
        return "/game/include/include.banner";
    }

    @RequestMapping("/index/getApiType")
    public String getApiType(Model model) {
        model.addAttribute("apiList", getSiteApiRelation(null));
        return "/game/include/include.api";
    }

    /**
     * app下载页
     * 获取下载地址/二维码
     *
     * @param model
     * @param request
     * @return
     */
    @RequestMapping("/app/download")
    @Upgrade(upgrade = true)
    public String downloadApp(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (ParamTool.isLoginShowQrCode() && SessionManager.getUser() == null) {//是否登录才显示二维码
            String url = "/login/commonLogin.html";
            response.setStatus(302);
            response.setHeader("Location", SessionManagerCommon.getRedirectUrl(request, url));
            return "/passport/login";
        }
        String userAgent = OsTool.getOsInfo(request);
        String url = null;
        //android自定义下载地址
        if (AppTypeEnum.ANDROID.getCode().contains(userAgent)) {
            url = getAndroidDownloadUrl();
        } else if (AppTypeEnum.IOS.getCode().contains(userAgent)) { //ios下载页面
            url = getIosDownloadUrl();
        }
        if (StringTool.isBlank(url)) {
            getAppPath(model, request);
        } else {
            try {
                response.sendRedirect(url);
            } catch (IOException e) {
                LOG.error(e, "ios请求外接地址错误,地址:{0}", url);
            }
        }
        if (ParamTool.isMobileUpgrade()) {
            return "/download/DownLoad";
        }
        return "/app/Index";
    }

    /**
     * 获取参数表中android下载地址
     *
     * @return
     */
    private String getAndroidDownloadUrl() {
        String addressUrl = "";
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.SETTING_ANDROID_DOWNLOAD_ADDRESS);
        if (sysParam != null && StringTool.isNotBlank(sysParam.getParamValue())) {
            addressUrl = sysParam.getParamValue();
        }
        return addressUrl;
    }

    /**
     * 获取参数表中ios下载地址
     *
     * @return
     */
    private String getIosDownloadUrl() {
        String addressUrl = "";
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.SETTING_IOS_DOWNLOAD_ADDRESS);
        if (sysParam != null && StringTool.isNotBlank(sysParam.getParamValue())) {
            addressUrl = sysParam.getParamValue();
        }
        return addressUrl;
    }

    /**
     * 获取APP下载地址
     */
    private void getAppPath(Model model, HttpServletRequest request) {
        //获取站点信息
        String code = CommonContext.get().getSiteCode();
        String os = OsTool.getOsInfo(request);
        boolean isMobileUpgrade = ParamTool.isMobileUpgrade();
        if (OSTypeEnum.IOS.getCode().equals(os)) {
            // 获取IOS信息
            getIosInfo(model, code, isMobileUpgrade);
        } else if (OSTypeEnum.ANDROID.getCode().equals(os)) {
            // 获取android APP信息
            getAndroidInfo(model, request, code, isMobileUpgrade);
        } else {
            getIosInfo(model, code, isMobileUpgrade);
            getAndroidInfo(model, request, code, isMobileUpgrade);
        }
    }

    /**
     * 获取android APP信息
     */
    private void getAndroidInfo(Model model, HttpServletRequest request, String code, boolean isMobileUpgrade) {
        Integer siteId = CommonContext.get().getSiteId();
        String appDomain = fetchAppDownloadDomain(request);
        if (StringTool.isBlank(appDomain)) {
            appDomain = ParamTool.appDmain(request.getServerName());
        }
        if (isMobileUpgrade) {
            SiteAppUpdate siteAppUpdate = Cache.getSiteAppUpdate(String.valueOf(siteId), AppTypeEnum.ANDROID.getCode());
            if (siteAppUpdate != null) {
                String versionName = siteAppUpdate.getVersionName();
                String appUrl = siteAppUpdate.getAppUrl();
                fillAndroidInfo(model, code, appDomain, versionName, appUrl);
            }
        } else {
            AppUpdate androidApp = Cache.getAppUpdate(AppTypeEnum.ANDROID.getCode());
            if (androidApp != null) {
                String versionName = androidApp.getVersionName();
                String appUrl = androidApp.getAppUrl();
                fillAndroidInfo(model, code, appDomain, versionName, appUrl);
            }
        }
    }

    /**
     * 获取IOS信息
     */
    private void getIosInfo(Model model, String code, boolean isMobileUpgrade) {
        Integer siteId = CommonContext.get().getSiteId();
        if (isMobileUpgrade) {
            SiteAppUpdate siteAppUpdate = Cache.getSiteAppUpdate(String.valueOf(siteId), AppTypeEnum.IOS.getCode());
            if (siteAppUpdate != null) {
                String versionName = siteAppUpdate.getVersionName();
                String appUrl = siteAppUpdate.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        } else {
            AppUpdate iosApp = Cache.getAppUpdate(AppTypeEnum.IOS.getCode());
            if (iosApp != null) {
                String versionName = iosApp.getVersionName();
                String appUrl = iosApp.getAppUrl();
                fillIosInfo(model, code, versionName, appUrl);
            }
        }
    }

    /**
     * 填充android信息
     *
     * @param model
     * @param code
     * @param appDomain
     * @param versionName
     * @param appUrl
     */
    private void fillAndroidInfo(Model model, String code, String appDomain, String versionName, String appUrl) {
        String url = String.format("https://%s%s%s/app_%s_%s.apk", appDomain, appUrl,
                versionName, code, versionName);

        model.addAttribute("androidQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
        model.addAttribute("androidUrl", url);
    }

    /**
     * 填充ios信息
     *
     * @param model
     * @param code
     * @param versionName
     * @param appUrl
     */
    private void fillIosInfo(Model model, String code, String versionName, String appUrl) {
        String url = String.format("itms-services://?action=download-manifest&url=https://%s%s/%s/app_%s_%s.plist", appUrl,
                versionName, code, code, versionName);

        model.addAttribute("iosQrcode", EncodeTool.encodeBase64(QrcodeDisTool.createQRCode(url, 6)));
        model.addAttribute("iosUrl", url);
    }

    /**
     * 查询用户信息,判断是否登录
     */
    @RequestMapping("/sysUser")
    @ResponseBody
    public String getSysUser(HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        if (sysUser != null) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("username", StringTool.overlayName(sysUser.getUsername()));
            map.put("avatarUrl", ImageTag.getImagePath(request.getServerName(), StringEscapeTool.unescapeHtml4(sysUser.getAvatarUrl())));
            return JsonTool.toJson(map);
        } else {
            return "unLogin";
        }
    }

    /**
     * 查询用户信息,判断是否登录
     */
    @RequestMapping("/getHeadInfo")
    @ResponseBody
    public String getHeadInfo(HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        Map map = new HashMap<>(2, 1f);
        if (sysUser == null) {
            map.put("isLogin", false);
        } else {
            String defaultCurrency = sysUser.getDefaultCurrency();
            if (StringTool.isNotBlank(defaultCurrency)) {
                SysCurrency sysCurrency = Cache.getSysCurrency().get(defaultCurrency);
                if (sysCurrency != null) {
                    map.put("currencySign", sysCurrency.getCurrencySign());
                }
            }
            map.put("isLogin", true);
            map.put("name", StringTool.overlayName(sysUser.getUsername()));
            if (StringTool.isNotBlank(sysUser.getAvatarUrl())) {
                map.put("avatar", StringEscapeTool.unescapeHtml4(ImageTag.getThumbPathWithDefault(request.getServerName(), sysUser.getAvatarUrl(), 46, 46, null)));
            }
            map.put("isAutoPay", SessionManager.isAutoPay());//是否免转标识
            //查询总资产
            PlayerApiListVo playerApiListVo = new PlayerApiListVo();
            playerApiListVo.getSearch().setPlayerId(SessionManager.getUserId());
            map.put("totalAssert", CurrencyTool.formatCurrency(ServiceSiteTool.playerApiService().queryPlayerAssets(playerApiListVo)));
            map.put("isDemo", SessionManager.isLotteryDemo());//彩票试玩模式
        }
        return JsonTool.toJson(map);
    }

    @RequestMapping("/lotteryDemo/{terminal}")
    public String lotteryDemo(@PathVariable String terminal, HttpServletRequest request) {
        createDemoAccount(request);
        return terminal.equals("h5") ? "redirect:/mainIndex.html" : "/ToIndex";
    }

    @RequestMapping("/demo/lottery")
    @ResponseBody
    public boolean lotteryDemo(HttpServletRequest request) {
        createDemoAccount(request);
        return true;
    }

    @Override
    protected String getDemoIndex() {
        return "/mainIndex";
    }

    /**
     * 查询用户信息,判断是否登录
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    @Upgrade(upgrade = true)
    public String userInfo(HttpServletRequest request) {
        SysUser sysUser = SessionManager.getUser();
        Map map = new HashMap<>(3, 1f);
        if (sysUser == null) {
            map.put("isLogin", false);
        } else {
            map.put("isLogin", true);
            map.put("name", StringTool.overlayName(sysUser.getUsername()));
            PlayerApiListVo playerApiListVo = new PlayerApiListVo();
            playerApiListVo.getSearch().setPlayerId(SessionManager.getUserId());
            map.put("asset", CurrencyTool.formatCurrency(ServiceSiteTool.playerApiService().queryPlayerAssets(playerApiListVo)));
        }
        return JsonTool.toJson(map);
    }
}
