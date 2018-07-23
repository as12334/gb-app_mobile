package so.wwb.gamebox.mobile.app.chess.controller;

import org.soul.commons.collections.CollectionTool;
import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.net.ServletTool;
import org.soul.model.sys.po.SysParam;
import org.soul.web.tag.ImageTag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import so.wwb.gamebox.common.cache.Cache;
import so.wwb.gamebox.mobile.app.constant.AppConstant;
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

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
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
        //以下获取游戏.先获取棋牌游戏,再获取其他入口.入口里面包含API
        String chessApiTypeStr = String.valueOf(ApiTypeEnum.CHESS.getCode());
        List<String> excludeApis = new ArrayList<>();
        excludeApis.add(StringTool.join(JOIN_CHAR, String.valueOf(ApiTypeEnum.SPORTS_BOOK.getCode()), ApiProviderEnum.BBIN.getCode()));
        //获取apiType不包含CHESS，包含FISH
        Collection<ApiTypeCacheEntity> apiTypes = getApiType(new Integer[]{ApiTypeEnum.CHESS.getCode()}, true);
        List<SiteApiRelationApp> gamesByApiTypes = getGamesByApiTypes(request, model, apiTypes, excludeApis);

        Map<String, GameCacheEntity> mobileGameByApiType = getNotEmptyMap(Cache.getMobileGameByApiType(chessApiTypeStr), new LinkedHashMap());

        gamesByApiTypes.addAll(rechangeGameEntity(mobileGameByApiType.values(), excludeApis,
                String.format(CHESS_GAME_IMG_PATH, model.getResolution(), SessionManager.getLocale().toString(), STR_PLACEHOLDER)));

        convertLiveImg(gamesByApiTypes, request);
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
        if (sysParam != null) {
            qrCodeUrl = StringTool.isNotBlank(sysParam.getParamValue()) ? sysParam.getParamValue() : sysParam.getDefaultValue();
        }
        map.put("qrCodeUrl", qrCodeUrl);
        return AppModelVo.getAppModeVoJson(true, AppErrorCodeEnum.SUCCESS.getCode(),
                AppErrorCodeEnum.SUCCESS.getMsg(),
                map,
                APP_VERSION);
    }

    /**
     * 棋牌包网,真人图片特殊处理
     *
     * @param siteApiRelationApps
     */
    private void convertLiveImg(List<SiteApiRelationApp> siteApiRelationApps, HttpServletRequest request) {
        {
            for (SiteApiRelationApp game : siteApiRelationApps) {
                if (ApiTypeEnum.LIVE_DEALER.getCode() == game.getApiTypeId()) {
                    String cover = game.getCover();
                    cover = cover.replaceAll("livedealer", "live");//棋牌包网真人图片名称更改
                    game.setCover(cover);
                    //如果是真人api,则强制设置为游戏类型
                    if ("api".equals(game.getType())) {
                        game.setType("game");
                        game.setRelation(new ArrayList<>());
                        game.setGameLink(getCasinoGameRequestUrl(game.getApiTypeId(), game.getApiId(), game.getGameId(), game.getCode()));
                    }
                }
                //递归执行替换
                if (CollectionTool.isNotEmpty(game.getRelation())) {
                    convertLiveImg(game.getRelation(), request);
                } else {
                    //如果没有下个层级,则直接为game:比如申博API是直接进入游戏大厅的.
                    //电子和真人,第三层游戏图标更换
                    if ("game".equals(game.getType())) {
                        if (ApiTypeEnum.CASINO.getCode() == game.getApiTypeId()) {
                            game.setCover(ImageTag.getImagePath(ServletTool.getDomainFullAddress(request), game.getGameConver()));
                        }
                    }
                }
            }
        }
    }


}
