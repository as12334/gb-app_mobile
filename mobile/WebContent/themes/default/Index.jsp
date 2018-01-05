<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set var="tid" value="${apiTypeId}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
    <script src="${resRoot}/js/promo/redEnvelope/Envelope.js?v=${rcVersion}"></script>
</head>

<body class="gb-theme index">
<input type="hidden" id="activityId" value="${activityId}">
<%@ include file="promo/redEnvelope/Envelope.jsp" %>

<!-- 侧滑导航根容器 -->
<div class="index-canvas mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="/themes/default/include/include.menu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!--头部-->
        <header class="mui-bar mui-bar-nav mui-hide _siteHeader" style="display:${myos eq 'app_android'?'none':'block'}">
            <div class="mui-pull-left">
                <span class="index-action-menu mui-action-menu"></span>
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt=""></div>
            </div>
            <!-- 资产 -->
            <%@include file="/themes/default/include/include.asset.jsp" %>
        </header>
        <section class="site-address mui-hide _indexDomain">${views.app_auto['主页域名']}：${empty sysDomain?domain:sysDomain}</section>

        <!--底部-->
        <%@include file="/themes/default/include/include.footer.jsp" %>
        <!--浮窗广告轮播-->
        <c:if test="${not empty floatList}">
            <div class="ads-slider hongbao-slide-wrap hongbao-wrap" id="hongbao">
                <div class="icon-close"></div>
                <div class="mui-slider hongbao-slider">
                    <div class="mui-slider-group">
                        <c:forEach var="item" items="${floatList}">
                            <c:if test="${item.type=='moneyActivity'}">
                                <c:choose>
                                    <c:when test="${item.cttFloatPic.singleMode}">
                                        <div class="mui-slider-item hb_type_<c:choose><c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-first')}">1</c:when><c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-second')}">2</c:when><c:otherwise>3</c:otherwise></c:choose>">
                                            <div class="img"></div>
                                            <div class="extra float_idx" objectId="${item.activityId}"></div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="mui-slider-item">
                                            <img class="float_idx" objectId="${item.activityId}" data-src="${soulFn:getImagePath(domain,item.floatItem.normalEffect)}"
                                                 src="${soulFn:getThumbPath(domain,item.floatItem.normalEffect,0,0)}">
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <%--<div class="mui-slider-item"><a href="#"><img src="${resRoot}/images/ads-banner-01.png" /></a></div>--%>
                        </c:forEach>

                    </div>
                    <%--<div class="mui-slider-indicator">
                        <c:forEach var="item" items="${floatList}">
                            <c:if test="${item.type=='moneyActivity'}">
                                <div class="mui-indicator mui-active"></div>
                            </c:if>
                        </c:forEach>
                        &lt;%&ndash;<div class="mui-indicator"></div>&ndash;%&gt;
                    </div>--%>
                </div>
            </div>
        </c:if>
        <!-- 内容 -->
        <div class="index-content mui-content mui-scroll-wrapper _cacheContent" id="mui-refresh">
            <div class="mui-scroll">
                <div class="gb-fullpage">
                    <!-- Banner和公告 -->
                    <%@include file="/include/include.loading.jsp" %>
                    <div class="_banner">
                        <%-- include.banner.jsp" --%>
                    </div>

                    <!-- 导航 -->
                    <%@ include file="./game/include/include.nav.jsp" %>

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
                            <div class="copy-right">
                                <span class="_user_time"></span>
                                <span class="site-info">Copyright © &nbsp;${siteName}&nbsp; Reserved</span>
                            </div>
                    <div style="height: 20px"></div> <%--作用是让时间和Copyright ©这句话能显示出来--%>
                    </div>
                </div>
                <%--<div class="footer-info">
                    <span class="_user_time"></span>
                    <span class="site-info">Copyright © &nbsp;${siteName}&nbsp; Reserved</span>
                </div>--%>
            </div>
        </div>

        <%@ include file="./game/include/include.notice.jsp" %>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<%@ include file="./include/include.indexDialog.jsp"%>
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
    Index
</script>
<c:if test="${isLogin}">
    <script type="text/javascript">
        curl(['site/index/Comet'], function (Comet) {
            comet = new Comet();
        });
    </script>
</c:if>
</html>
<%@ include file="/include/include.footer.jsp" %>