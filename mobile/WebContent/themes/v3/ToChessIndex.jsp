<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<%@page import="so.wwb.gamebox.web.SessionManagerCommon" %>
<!DOCTYPE html>
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
<body class="load-body">
<div class="load-bg loading-page">
    <div class="load-bar">
        <p>${views.app_auto['请耐心等待']}</p>
        <div id="load-bar-1" class="mui-progressbar mui-progressbar-infinite"><span></span></div>
    </div>
    <div class="load-bar-middle">
        <span id="welcome" class="btn-back" data-rel='{"target":"clickWelcome","opType":"function"}'>${views.app_auto['访问主页']}</span>
    </div>
    <div class="load-bar-bottom">
        <img src="${appLogo}" width="110">
        <div class="copy-right">
            Copyright © &nbsp;${siteName}&nbsp; Reserved.
        </div>
    </div>
</div>
</body>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery-eventlock/jquery-eventlock-1.0.0.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Common.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/ToIndex.js?v=${rcVersion}"></script>
<script>
    var root = '${root}';
    var resComRoot = '${resComRoot}';
    var resRoot = '${resRoot}';
    var imgRoot = '${imgRoot}';
    var rcVersion='${rcVersion}';
</script>
</html>
<%@ include file="/include/include.footer.jsp" %>

