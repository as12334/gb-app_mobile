<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
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
                <img src="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" class="app-logo" />
                <div class="tit">${siteName}</div>
                <div class="des">下载APP 再也无需输入网址</div>
                <div class="btn-wrap">
                    <soul:button target="${root}/downLoad/downLoadIOS.html" text="" opType="href" cssClass="btn-download ios">点击下载iOS版</soul:button>
                    <soul:button target="${androidUrl}" text="" opType="href" cssClass="btn-download android">${views.themes_auto['点击下载安卓版']}</soul:button>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/download/DownLoad.js"></script>
</html>

<%@ include file="/include/include.footer.jsp" %>