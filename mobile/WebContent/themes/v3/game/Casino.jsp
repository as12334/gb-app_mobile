<%--@elvariable id="tagGameMap" type="java.util.Map<java.lang.String, java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>>"--%>
<%--@elvariable id="tagName" type="java.util.Map<java.lang.String, java.lang.String>"--%>
<%--@elvariable id="allGames" type="java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>"--%>
<%--@elvariable id="casinoApis" type="java.util.Map<java.lang.String,so.wwb.gamebox.model.company.site.vo.ApiCacheEntity>"--%>
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
        <header class="mui-bar mui-bar-blue mui-bar-nav casino-page-bar">
            <a class="mui-icon mui-icon-back mui-pull-left"></a>
            <a data-rel='{"target":"${root}/mainIndex.html","opType":"href"}' class="mui-icon mui-icon-home mui-pull-left"></a>
            <%---列表展示--%>
            <a class="icon-list2 mui-pull-right" data-rel='{"target":"listDisplay","opType":"function"}'></a>
            <%---图标展示--%>
            <a class="icon-list1 mui-pull-right" data-rel='{"target":"iconDisplay","opType":"function"}'></a>
            <a data-rel='{"target":"toggleSearch","opType":"function"}' class="mui-icon mui-icon-search mui-pull-right"></a>
        </header>
        <div class="mui-content mui-content-casino-page">
            <div class="electronic-search" name="searchDiv" style="display: none">
                <a class="btn-search" data-rel='{"target":"searchGame","opType":"function"}'>搜索</a>
                <input type="text" name="gameName" class="mui-input-clear" placeholder="输入游戏名称" data-input-clear="3"><span class="mui-icon mui-icon-clear mui-hidden"></span>
            </div>
            <div class="search-shadow" data-rel='{"target":"hideShadow","opType":"function"}' style="display: none;"></div>
            <!-- 主界面具体展示内容 -->
            <div id="pull_apiScroll">
                <div class="api-scroll swiper-container">
                    <div class="swiper-wrapper">
                        <c:forEach items="${casinoApis}" var="i">
                            <div data-rel='{"target":"/game/getCasinoByApiId.html?search.apiId=${i.key}&search.apiTypeId=2","opType":"href"}' class="swiper-slide ${i.key eq apiId?'api-index':''}">
                                <div class="img-wrap">
                                    <i class='api-item api-icon-2-${i.key}'></i>
                                </div>
                                <span class='title'>${i.value.relationName}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <!-- Swiper -->
            <div class="casino-game-type" id='apiScroll-wrap'>
                <div id="apiScroll-cont">
                    <div class="swiper-container g-t-slide-indicators">
                        <div class="swiper-wrapper">
                            <div class="swiper-slide">所有游戏</div>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGameMap[t.key])>0}">
                                    <div class="swiper-slide">${t.value}</div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="swiper-container g-t-slide-content">
                        <div class="swiper-wrapper">
                            <%--所有游戏--%>
                            <div class="swiper-slide mui-scroll">
                                <div class="casino-wrap">
                                    <div class="casino-list">
                                        <div class="mui-row">
                                            <c:forEach items="${allGames}" var="game">
                                                <div class="mui-col-xs-4" name="game" gameName="${game.name}">
                                                    <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.apiId}","dataStatus":"${game.status}",
                                                            "dataGameCode":"${game.code}","dataGameId":"${game.gameId}",
                                                            "dataApiName":"${game.name}","target":"goGame","opType":"function"}'>
                                                        <div class="img-wrap"><img data-lazyload="${soulFn:getImagePath(domain, game.cover)}"/></div>
                                                        <p>${game.name}</p>
                                                        <p class='hot'>所有游戏</p>
                                                        <span class="name">${apiName}</span>
                                                        <i class='icon-play'></i>
                                                    </a>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%--其他标签游戏--%>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGameMap[t.key])>0}">
                                    <div class="swiper-slide mui-scroll">
                                        <div class="casino-wrap">
                                            <div class="casino-list">
                                                <div class="mui-row">
                                                    <c:forEach items="${tagGameMap[t.key]}" var="game">
                                                        <div class="mui-col-xs-4" name="game" gameName="${game.name}">
                                                            <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.apiId}","dataStatus":"${game.status}",
                                                            "dataGameCode":"${game.code}","dataGameId":"${game.gameId}",
                                                            "dataApiName":"${game.name}","target":"goGame","opType":"function"}'>
                                                                <div class="img-wrap"><img data-lazyload="${soulFn:getImagePath(domain, game.cover)}"/></div>
                                                                <p>${game.name}</p>
                                                                <p class='hot'>${t.value}</p>
                                                                <span class="name">${apiName}</span>
                                                                <i class='icon-play'></i>
                                                            </a>
                                                        </div>
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

            <!--mui-scroll 闭合标签-->
        </div>
        <!--mui-content 闭合标签-->
    </div>
</div>

<%@include file="../include/include.js.jsp" %>
<%--<script src="${resComRoot}/js/mobile/layer.js"></script>--%>
<script src="${resRoot}/js/mui/mui.lazyload.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.lazyload.img.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/casino/alloy_touch.css.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/casino/transform.js?v=${rcVersion}"></script>
<c:if test="${apiId == 6}">
    <script type="text/javascript" src="${resRoot}/js/game/PtGame.js?v=${rcVersion}"></script>
</c:if>
<script type="text/javascript" src="${resRoot}/js/game/Game.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/casino/Casino.js?v=${rcVersion}"></script>
</body>
<%@ include file="/include/include.footer.jsp" %>
</html>
