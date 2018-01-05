<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>
<body class="gb-theme index">
<c:set var="tid" value="${apiTypeId}"/>
<input type="hidden" id="tid" value="${apiTypeId}">
<!-- 侧滑导航根容器 -->
<div class="index-canvas mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="/themes/default/include/include.menu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!--头部-->
        <header class="mui-bar mui-bar-nav mui-hide _siteHeader">
            <div class="mui-pull-left">
                <span class="index-action-menu mui-action-menu"></span>
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt=""></div>
            </div>
            <!-- 资产 -->
            <%@include file="/themes/default/include/include.asset.jsp" %>
        </header>
        <section class="site-address mui-hide _indexDomain">
            ${views.app_auto['主页域名']}：${empty sysDomain?domain:sysDomain}
        </section>
        <!--底部-->
        <%@include file="/themes/default/include/include.footer.jsp" %>
        <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
            <!-- 主页面容器 -->
            <div class="mui-inner-wrap">
                <!-- 内容 -->
                <div class="index-content mui-content mui-scroll-wrapper _cacheContent" id="mui-refresh">
                    <div class="mui-scroll">
                        <div>
                            <!-- Banner和公告 -->
                            <%@ include file="./include/include.banner.jsp" %>
                            <!-- 导航 -->
                            <%@ include file="./include/include.nav.jsp" %>
                            <div id="_games" class="mui-control-content ${apiTypeId<5?'mui-active':''}">
                                <!-- API游戏 -->
                                <input type="hidden" id="nav-type" value="${tid}"/>
                                <div class="mui-container">
                                    <c:forEach var="at" items="${apiTypes}" varStatus="vs">
                                        <div id="container${at.apiTypeId}"
                                             class="mui-control-content ${at.apiTypeId == tid ? 'mui-show' : 'mui-hide'}">
                                            <%@include file="/include/include.loading.jsp" %>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                            <div id="_promo" class="mui-control-content mui-nav ${apiTypeId==5?'mui-active':''}">
                                <%@include file="/include/include.loading.jsp" %>
                            </div>
                            <div id="_agent" class="mui-control-content mui-nav ${apiTypeId==6?'mui-active':''}"></div>
                            <div id="_about" class="mui-control-content mui-nav ${apiTypeId==7?'mui-active':''}"></div>
                            <div id="_terms" class="mui-control-content mui-nav ${apiTypeId==8?'mui-active':''}"></div>
                            <div class="footer-info">
                                <span class="_user_time"></span>
                                <span class="site-info">Copyright © &nbsp;${siteName}&nbsp; Reserved</span>
                            </div>
                            <div style="height: 100px"></div><%--作用是北京时间和Copyright ©新海天垫上去显示出来--%>
                        </div>
                    </div>
                </div>
                <div class="mui-off-canvas-backdrop"></div>
            </div>
        </div>
        <%@ include file="./include/include.notice.jsp" %>
    </div>
</div>
</body>
<div id="activityType" class="mui-popover">
    <div class="mui-scroll-wrapper popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <li class="mui-table-view-cell">
                    <a href="#" value="all">${views.game_auto['全部']}</a>
                </li>
            </ul>
        </div>
    </div>
</div>
<script src="${resRoot}/js/mui/mui.pullToRefresh.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.material.js?v=${rcVersion}"></script>
<script>
    curl(['site/game/Game', 'site/game/Live', 'site/game/Casino', 'site/game/Sport', 'site/game/Lottery',
        'site/common/Assets', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
            function (Game, Live, Casino, Sport, Lottery, Assets, Menu, Footer, Dynamic) {
                game = new Game();
                game.live = new Live();
                game.casino = new Casino();
                game.sport = new Sport();
                game.lottery = new Lottery();
                game.asset = new Assets();
                game.menu = new Menu();
                game.footer = new Footer();
                game.dynamic = new Dynamic();
            });
</script>
</html>
<%@ include file="/include/include.footer.jsp" %>