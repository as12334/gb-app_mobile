<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@include file="include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/layer.css"/>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <%@include file="common/Head.jsp"%>
        <div class="mui-content mui-scroll-wrapper" id="pullfresh">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--轮播和公告-->
                <div class="_banner">
                    <%@include file="index.include/include.banner.jsp"%>
                </div>
                <!--导航-->
                <%@include file="index.include/include.nav.jsp"%>
                <!--api九宫格-->
                <%@include file="index.include/include.api.jsp"%>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@include file="common/Footer.jsp"%>
    </div>
</div>
<!--浮窗广告轮播-->
<%@include file="index.include/Envelope.jsp"%>
<%@include file="include/include.js.jsp"%>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script type="text/javascript" src="${resRoot}/js/jquery/jquery.nicescroll.min.js"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.material.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/Index.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
<script type="text/javascript" src="${resRoot}/js/envelope/Envelope.js"></script>
</body>
</html>
