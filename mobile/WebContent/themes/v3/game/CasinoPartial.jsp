<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css" />
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->

    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav casino-list-bar">
            <a class="mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
            <%@include file="../common/Assert.jsp"%>
            <h1 class="mui-title">${empty siteApi.name?(gbFn:getApiName(apiId)):siteApi.name}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-casino-list">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="electronic-search">
                    <a id="search-game" data-rel='{"target":"searchGame","opType":"function"}' class="btn-search">${views.themes_auto['搜索']}</a>
                    <input type="text" id="game-name" class="mui-input-clear" placeholder="${views.themes_auto['输入游戏名称']}" data-input-clear="3"><span class="mui-icon mui-icon-clear mui-hidden"></span>
                </div>
                <!-- Swiper -->
                <div class="casino-game-type">
                    <div class="swiper-container g-t-slide-indicators">
                        <div class="swiper-wrapper">
                            <div class="swiper-slide all" data-rel='{"opType":"function","target":"changeTag"}'>所有游戏</div>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGames[t.key])>0}">
                                    <div class="swiper-slide partial" data-rel='{"opType":"function","target":"changeTag"}'>${t.value}</div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>

                    <input type="hidden" value="${apiId}" apiId="${apiId}" id="api">

                    <div class="swiper-container g-t-slide-content">
                        <div class="swiper-wrapper">

                            <div class="swiper-slide">
                                <div class="casino-wrap">
                                    <div class="casino-list">
                                        <div class="mui-row"><%--所有游戏--%>
                                            <c:forEach var="game" items="${allGames}" varStatus="tagMap">
                                                    <div class="mui-col-xs-3" apiName="${game.value.name}">
                                                        <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.value.apiId}","dataStatus":"${game.value.status}",
                                                            "dataGameCode":"${game.value.code}","dataGameId":"${game.value.gameId}",
                                                            "dataApiName":"${game.value.name}","target":"goGame","opType":"function"}'>

                                                            <div class="img-wrap">
                                                                <img data-lazyload="${soulFn:getImagePath(domain, game.value.cover)}">
                                                            </div>
                                                            <p>${game.value.name}</p>
                                                        </a>
                                                    </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGames[t.key])>0}">
                                    <div class="swiper-slide">
                                        <div class="casino-wrap">
                                            <div class="casino-list">
                                                <div class="mui-row">
                                                   <c:forEach items="${tagGames[t.key]}" var="i">
                                                       <c:set var="game" value="${allGames[i.toString()]}"/>
                                                       <c:if test="${!empty game}">
                                                           <div class="mui-col-xs-3" apiName="${game.name}">
                                                               <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.apiId}","dataStatus":"${game.status}",
                                                                            "dataGameCode":"${game.code}","dataGameId":"${game.gameId}",
                                                                            "dataApiName":"${game.name}","target":"goGame","opType":"function"}' class="" >

                                                                   <div class="img-wrap">
                                                                           <%--<img data-src="${soulFn:getImagePath(domain, allGames[g].cover)}" src="${soulFn:getImagePath(domain, allGames[g].cover)}">--%>
                                                                       <img data-lazyload="${soulFn:getImagePath(domain, game.cover)}">
                                                                   </div>
                                                                   <p>${game.name}</p>
                                                               </a>
                                                           </div>
                                                       </c:if>
                                                   </c:forEach>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <%--底部菜单--%>
        <%@include file="../common/Footer.jsp"%>


    </div>
</div>

<%@include file="../include/include.js.jsp" %>
<%--<script src="${resComRoot}/js/mobile/layer.js"></script>--%>
<script src="${resRoot}/js/mui/mui.lazyload.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.lazyload.img.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<c:if test="${apiId == 6}">
    <script type="text/javascript" src="${resRoot}/js/game/PtGame.js?v=${rcVersion}"></script>
</c:if>
<script type="text/javascript" src="${resRoot}/js/game/Game.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/casino/CasinoTemp.js?v=${rcVersion}"></script>
<%@ include file="/include/include.footer.jsp" %>
</body>
</html>
