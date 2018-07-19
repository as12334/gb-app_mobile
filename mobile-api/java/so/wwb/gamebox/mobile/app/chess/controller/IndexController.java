package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.model.sys.po.SysParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.mobile.app.enums.AppErrorCodeEnum;
import so.wwb.gamebox.mobile.app.model.AppModelVo;
import so.wwb.gamebox.mobile.app.model.AppRequestModelVo;
import so.wwb.gamebox.mobile.app.model.SiteApiRelationApp;
import so.wwb.gamebox.mobile.controller.BaseOriginController;
import so.wwb.gamebox.mobile.session.SessionManager;
import so.wwb.gamebox.model.ParamTool;
import so.wwb.gamebox.model.SiteParamEnum;
import so.wwb.gamebox.model.company.site.vo.ApiTypeCacheEntity;
import so.wwb.gamebox.model.company.site.vo.GameCacheEntity;
import so.wwb.gamebox.model.gameapi.enums.ApiProviderEnum;
import so.wwb.gamebox.model.gameapi.enums.ApiTypeEnum;
import so.wwb.gamebox.model.master.enums.CttCarouselTypeEnum;
import so.wwb.gamebox.web.cache.Cache;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static so.wwb.gamebox.mobile.app.constant.AppConstant.*;


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
        map.remove("phoneDialog");
        map.put("language", SessionManager.getLocale().toString());

        String chessApiTypeStr = String.valueOf(ApiTypeEnum.CHESS.getCode());
        List<String> excludeApis = new ArrayList<>();
        excludeApis.add(StringTool.join(JOIN_CHAR, String.valueOf(ApiTypeEnum.SPORTS_BOOK.getCode()), ApiProviderEnum.BBIN.getCode()));
        //获取apiType不包含CHESS，包含FISH
        Collection<ApiTypeCacheEntity> apiTypes = getApiType(new Integer[]{ApiTypeEnum.CHESS.getCode()}, true);
        List<SiteApiRelationApp> gamesByApiTypes = getGamesByApiTypes(request, model, apiTypes, excludeApis);

        Map<String, GameCacheEntity> mobileGameByApiType = getNotEmptyMap(Cache.getMobileGameByApiType(chessApiTypeStr), new LinkedHashMap());
        gamesByApiTypes.addAll(rechangeGameEntity(mobileGameByApiType.values(), excludeApis,
                String.format(CHESS_GAME_IMG_PATH, model.getResolution(), SessionManager.getLocale().toString(), STR_PLACEHOLDER)));

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
        SysParam sysParam = ParamTool.getSysParam(SiteParamEnum.CHESS_SHARE_PICTURE);
        String qrCodeUrl = "";
        if(sysParam != null) {
            qrCodeUrl = StringTool.isNotBlank(sysParam.getParamValue()) ? sysParam.getParamValue() : sysParam.getDefaultValue();
        }
        map.put("qrCodeUrl", qrCodeUrl);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

}