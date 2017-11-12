<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
    <link rel="bookmark" href="${resRoot}/favicon.ico">
    <link rel="shortcut icon" href="${resRoot}/favicon.ico">
    <link rel="stylesheet" href="${resRoot}/themes/otherpage.css" />
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->
    <aside class="mui-off-canvas-left">
    </aside>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav mui-bar-blue">
            <a class="mui-icon mui-icon-arrowleft mui-pull-left mui-action-back"></a>
            <h1 class="mui-title">APP下载</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-without-footer-address download-content-ios">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <img src="${resRoot}/images/apple-download-ios-bg.png" class="h-bg"/>
                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" class="app-logo" />
                <a href="javascript:void(0)" data-value="${iosUrl}" class="btn-install">点击安装</a>
                <img src="${resRoot}/images/apple-down.png" class="down-step"/>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/head/Head.js"></script>
</html>

