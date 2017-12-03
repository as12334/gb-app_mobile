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
        <div class="mui-content mui-scroll-wrapper mui-content-without-footer-address download-content-ios">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <img src="${resRoot}/images/apple-download-ios-bg.png" class="h-bg"/>
                <img src="${root}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" class="app-logo" />
                <soul:button target="${iosUrl}" text="" opType="href" cssClass="btn-install">点击安装</soul:button>
                <img src="${resRoot}/images/apple-down.png" class="down-step"/>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/download/DownLoad.js"></script>
</html>

