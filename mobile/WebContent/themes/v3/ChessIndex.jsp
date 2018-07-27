<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<%@page import="so.wwb.gamebox.web.SessionManagerCommon" %>
<!DOCTYPE html>
<html>
<head>
    <c:set var="siteName" value="<%=SessionManagerCommon.getSiteName(request) %>"/>
    <c:set var="siteId" value="<%=SessionManagerCommon.getSiteId() %>"/>
    <title>${siteName}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <!--禁止百度转码-->
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <!-- 优先使用 IE 最新版本和 Chrome -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

    <link rel="bookmark" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png">
    <link rel="shortcut icon" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png">

    <link rel="stylesheet" href="${resRoot}/themes/mui.min.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/common.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/index.css?v=${rcVersion}"/>
    <c:set var="background_type" value="blue"/>
    <link rel="stylesheet" href="${resRoot}/themes/${background_type}/style.css?v=${rcVersion}"/>
    <c:set var="appLogo" value="${cdnUrl}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
</head>

<body>
<!-- 主页面标题 -->
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<header class="mui-bar mui-bar-nav">
    <img src="${appLogo}" alt="logo" class="logo">
</header>
<!-- 主界面具体展示内容 -->
<div class="mui-content home-content">
    <!--易记域名-->
    <section class="yjym">${views.themes_auto['易记域名']}：${empty sysDomain ? domain : sysDomain}</section>
    <!--轮播和公告-->
    <section class="mui-slider banner-slide">
        <div class="banner-ads" style="height:50px">
            ${views.themes_auto['更多精彩游戏']}
            <a data-rel='{"target":"downLoadApp","opType":"function"}' class="btn-download">${views.themes_auto['立即下载']}</a>
            <div data-rel='{"target":"closeDownLoad","opType":"function"}' class="close-slide"></div>
        </div>
        <div class="mui-slider-group " style="height: 44px;">
        </div>
    </section>
</div>

</body>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery-eventlock/jquery-eventlock-1.0.0.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Common.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
<script>
    var root = '${root}';
    var resComRoot = '${resComRoot}';
    var resRoot = '${resRoot}';
    var imgRoot = '${imgRoot}';
    var rcVersion ='${rcVersion}';
    var random ='${random}';

    $(function () {
        var options = {
        };
        muiInit(options);
    });

    /**
     * 下载客户端
     * @param obj
     * @param options
     */
    function downLoadApp(obj, options) {
        //是否要先登录再跳转登录页面
        var ajaxOption = {
            url: root + '/downLoad/downLoadShowQrcode.html',
            success: function (data) {
                var userAgent = whatOs();
                var targetUrl = root + "/downLoad/downLoad.html?userAgent=" + userAgent;
                if (data.showQrCode == true && data.isLogin != true) {
                    toast("请登入下载");
                    window.setTimeout(function () {
                        login(targetUrl);
                    }, 1500);
                } else {
                    goToUrl(targetUrl, null, targetUrl);
                }
            }
        };
        muiAjax(ajaxOption);
    }

    /**
     * 关闭下载提示
     */
    function closeDownLoad() {
        $(".banner-ads").hide();
    }

</script>