package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.support.CdnConf;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.AppRequestModelVo;
import so.wwb.gamebox.mobile.app.model.SiteApiRelationApp;
import so.wwb.gamebox.mobile.controller.BaseOriginController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.company.site.vo.ApiTypeCacheEntity;
import so.wwb.gamebox.model.company.site.vo.GameCacheEntity;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.enums.CttCarouselTypeEnum;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.APP_VERSION;

/**
 * 棋牌包网首页控制器
 */
@Controller
@RequestMapping("/chess")
public class IndexController extends BaseOriginController {
    private Log LOG = LogFactory.getLog(IndexController.class);

    /**
     * 请求首页，查询轮播图，公告，游戏类，红包活动
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/mainIndex")
    @ResponseBody
    public String mainIndex(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = new HashMap<>(5, 1f);
        getBannerAndPhoneDialog(map, request, CttCarouselTypeEnum.CAROUSEL_TYPE_PHONE_DIALOG);//获取轮播图和手机弹窗广告
        map.put("language", SessionManager.getLocale().toString());

        //获取CDN url
        String cdnUrl = new CdnConf().getCndUrl();
        String gameCover = cdnUrl + String.format(AppConstant.GAME_COVER_URL, model.getTerminal(), model.getResolution(), SessionManager.getLocale().toString());

        String chessApiTypeStr = String.valueOf(ApiTypeEnum.CHESS.getCode());
        List<ApiProviderEnum> ridApiProviderEnums = new ArrayList<>();
        //获取apiType不包含CHESS，包含FISH
        Collection<ApiTypeCacheEntity> apiTypes = getApiType(new Integer[]{ApiTypeEnum.CHESS.getCode()}, true);
        List<SiteApiRelationApp> gamesByApiTypes = getGamesByApiTypes(request, model, apiTypes, ridApiProviderEnums);

        Map<String, GameCacheEntity> mobileGameByApiType = getNotEmptyMap(Cache.getMobileGameByApiType(chessApiTypeStr), new LinkedHashMap());
        gamesByApiTypes.addAll(rechangeGameEntity(mobileGameByApiType.values(), ridApiProviderEnums));
        Collections.sort(gamesByApiTypes);
        map.put("siteApiRelation", gamesByApiTypes);

        return AppModelVo.getAppModeVoJsonUseFastjson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 获取分享二维码图片
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/getShareQRCode")
    @ResponseBody
    public String getShareQRCode(HttpServletRequest request, AppRequestModelVo model) {
        Map<String, Object> map = new HashMap<>();
        map.put("qrCodeUrl", "/fserver/mobile-api/Share/321235652.png");
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

}