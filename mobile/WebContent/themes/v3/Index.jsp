<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@include file="include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css?v=${rcVersion}" />
</head>

<body>
<!-- 主页面标题 -->
<%@include file="common/Head.jsp"%>
<!-- 主界面具体展示内容 -->
<div class="mui-content home-content">
    <%--顶部游戏类型--%>
    <%@include file="index.include/include.nav.jsp"%>
    <!--易记域名-->
    <section class="yjym">${views.themes_auto['易记域名']}：${domain}</section>
    <!--轮播和公告-->
    <%@include file="index.include/include.banner.jsp"%>
    <%--游戏类型--%>
    <%@include file="index.include/include.api.jsp"%>
</div>  <!--mui-content 闭合标签-->
<%--底部菜单--%>
<%@include file="common/Footer.jsp"%>
<%-- 侧滑导航根容器 --%>
<%@include file="common/LeftMenu.jsp"%>
<%--添加到桌面图标--%>
<div id="deskTip" class="desk">
    <div class="mui-col-xs-2 logo"><img src="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png"></div>
    <div class="mui-col-xs-8 tip">
        <span class="desk-text">${views.game_auto['点击下方']}<em></em>${views.game_auto['添加到主屏幕']}</span>
    </div>
    <div data-rel='{"target":"closeDesk","opType":"function"}' class="mui-col-xs-1 close"><i></i></div>
</div>
<!--消息弹窗-->
<%@include file="index.include/include.dialog.jsp"%>
<%--红包--%>
<%@include file="index.include/Envelope.jsp"%>
</body>
<%@include file="include/include.js.jsp"%>
<script src="${resRoot}/js/mui/mui.lazyload.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.lazyload.img.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/jquery.marquee.min.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/Index.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/envelope/Envelope.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>