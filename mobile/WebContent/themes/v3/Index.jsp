<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@include file="include/include.head.jsp" %>
</head>

<body>
<!-- 主页面标题 -->
<%@include file="common/Head.jsp"%>
<!-- 主界面具体展示内容 -->
<div class="mui-content home-content">
    <!--易记域名-->
    <section class="yjym">${views.themes_auto['易记域名']}：${domain}</section>
    <!--轮播和公告-->
    <%@include file="index.include/include.banner.jsp"%>
    <%--游戏类型--%>
    <%@include file="index.include/include.nav.jsp"%>
    <%@include file="index.include/include.api.jsp"%>
</div>  <!--mui-content 闭合标签-->
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <div class="mui-content mui-scroll-wrapper" id="pullfresh">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--轮播和公告-->
                <%--<div class="_banner">
                    <%@include file="index.include/include.banner.jsp"%>
                </div>--%>
                <!--导航-->
                <%--<%@include file="index.include/include.nav.jsp"%>--%>
                <!--api九宫格-->
                <%--<%@include file="index.include/include.api.jsp"%>--%>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@include file="common/Footer.jsp"%>
        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<!--浮窗广告轮播-->
<%--<%@include file="index.include/Envelope.jsp"%>--%>
<!--消息弹窗-->
<%@include file="index.include/include.dialog.jsp"%>
</body>
<%@include file="include/include.js.jsp"%>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script type="text/javascript" src="${resRoot}/js/Index.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
<script type="text/javascript" src="${resRoot}/js/envelope/Envelope.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>