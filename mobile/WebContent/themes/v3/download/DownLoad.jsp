<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
    <link rel="stylesheet" href="${resRoot}/themes/otherpage.css" />
    <link rel="bookmark" href="${resRoot}/favicon.ico">
    <link rel="shortcut icon" href="${resRoot}/favicon.ico">
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
        <div class="mui-content mui-scroll-wrapper mui-content-without-footer-address download-content">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" class="app-logo" />
                <div class="tit">${siteName}</div>
                <div class="des">下载APP 再也无需输入网址</div>
                <div class="btn-wrap">
                    <a href="javascript:void(0)" class="btn-download ios">点击下载iOS版</a>
                    <a href="javascript:void(0)" data-value="${androidUrl}" class="btn-download android">点击下载安卓版</a>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/head/Head.js"></script>
</html>

