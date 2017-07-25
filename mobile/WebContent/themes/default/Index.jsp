<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set var="tid" value="${apiTypeId}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme index">
<!-- 侧滑导航根容器 -->
<div class="index-canvas mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="/include/include.menu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!--头部-->
        <header class="mui-bar mui-bar-nav mui-hide _siteHeader">
            <div class="mui-pull-left">
                <span class="index-action-menu mui-action-menu"></span>
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt=""></div>
            </div>
            <!-- 资产 -->
            <%@include file="/include/include.asset.jsp" %>
        </header>
        <section class="site-address mui-hide _indexDomain">${views.app_auto['主页域名']}：${empty sysDomain?domain:sysDomain}</section>

        <!--底部-->
        <%@include file="/include/include.footer.jsp" %>
        <!-- 内容 -->
        <div class="index-content mui-content mui-scroll-wrapper _cacheContent" id="mui-refresh-index">
            <div class="mui-scroll">
                <div class="gb-fullpage">
                    <!-- Banner和公告 -->
                    <%@include file="/include/include.loading.jsp" %>
                    <div class="_banner">
                        <%-- include.banner.jsp" --%>
                    </div>

                    <!-- 导航 -->
                    <%@ include file="game/include/include.nav.jsp" %>

                    <!--喜好定制(API)-->
                    <div class="mui-row _apiType">
                        <%-- include.api.jsp --%>
                    </div>

                    <!--优惠速递-->
                    <div class="mui-row">
                        <div class="index-block promo-block">
                            <div class="index-block-header">
                                <div class="mui-pull-left title">${views.app_auto['优惠速递']}</div>
                                <div class="mui-pull-right more">
                                    <a class="_more" _href="/game.html?typeId=5">
                                        ${views.app_auto['更多优惠']}<span class="mui-icon mui-icon-double-arrow"></span>
                                    </a>
                                </div>
                            </div>
                            <div class="index-block-content">
                                <ul id="activity-content" class="mui-promo">
                                </ul>
                            </div>
                        </div>
                    </div>

                    <!--游戏推荐-->
                    <div class="mui-row">
                        <div class="index-block game-block">
                            <div class="index-block-header">
                                <div class="mui-pull-left title">${views.app_auto['游戏推荐']}</div>
                                <div class="mui-pull-right more">
                                    <a class="_more" _href="/game.html?typeId=2">
                                        ${views.app_auto['更多游戏']}<span class="mui-icon mui-icon-double-arrow"></span>
                                    </a>
                                </div>
                            </div>
                            <div class="index-block-content game-block-content _hot_games">
                                <!-- 热门游戏 -->
                            </div>
                        </div>
                    </div>

                    <!--底部导航-->
                    <div class="mui-row bottom-nav">
                        <ul class="mui-list-inline">
                            <li><a data-href="${root}/game.html?typeId=6">${views.app_auto['代理加盟']}</a></li>
                            <li><a data-href="${root}/game.html?typeId=7">${views.app_auto['关于我们']}</a></li>
                            <li><a data-href="${root}/game.html?typeId=8">${views.app_auto['注册条款']}</a></li>

                            <li class="_download mui-hide _downloadApp" data-download="/app/download.html">
                                <a>${views.app_auto['客户端']}</a></li>
                            <li class="mui-hide" id="goPc"><a data-terminal="pc">${views.app_auto['电脑版']}</a></li>
                            <c:if test="${siteId eq 173}">
                                <li><a data-href="mailto:hlh666.net@gmail.com">hlh666.net@gmail.com</a></li>
                            </c:if>
                        </ul>
                    </div>
                </div>
                <div class="footer-info">
                    <span class="_user_time"></span>
                    <span class="site-info">Copyright © &nbsp;${siteName}&nbsp; Reserved</span>
                </div>
            </div>
        </div>

        <%@ include file="/themes/default/game/include/include.notice.jsp" %>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<div id="activityType" class="mui-popover">
    <div class="mui-scroll-wrapper popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <li class="mui-table-view-cell">
                    <a href="#" value="all">${views.app_auto['全部']}</a>
                </li>
            </ul>
        </div>
    </div>
</div>
<script src="${resRoot}/js/mui/mui.pullToRefresh.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.material.js?v=${rcVersion}"></script>
<script>
    curl(['site/Index', 'site/game/ApiLogin', 'site/common/Assets', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
            function (Page, ApiLogin, Assets, Menu, Footer, Dynamic) {
                page = new Page();
                page.apiLogin = new ApiLogin();
                page.asset = new Assets();
                page.menu = new Menu();
                page.footer = new Footer();
                page.dynamic = new Dynamic();
            });
</script>
</html>