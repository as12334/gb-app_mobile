<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<%@ include file="../include/include.head.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Dawoo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css" />
    <link rel="stylesheet" href="${resRoot}/themes/default/style.css" />
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
            <h1 class="mui-title">${empty siteApi.name?(gbFn:getApiName(apiId)):siteApi.name}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-casino-list">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="electronic-search">
                    <%--<a id="search-game" class="btn-search">搜索</a>--%>
                    <%--<button data-rel='{"target":"searchGame","opType":"function"}' class="btn-search">${views.themes_auto['搜索']}</button>--%>
                    <a id="search-game" data-rel='{"target":"searchGame","opType":"function"}' class="btn-search">${views.themes_auto['搜索']}</a>
                    <%--<soul:button target="searchGame" text="${views.themes_auto['搜索']}" opType="function" cssClass="btn-search"/>--%>
                    <input type="text" id="game-name" class="mui-input-clear" placeholder="${views.themes_auto['输入游戏名称']}">
                </div>
                <!-- Swiper -->
                <div class="casino-game-type">
                    <div class="swiper-container g-t-slide-indicators">
                        <div class="swiper-wrapper">
                            <div class="swiper-slide">所有游戏</div>
                            <c:forEach items="${siteGameTagByTagId}" var="t">
                                <div class="swiper-slide">${t.key}</div>
                            </c:forEach>
                        </div>
                    </div>

                    <input type="hidden" value="${apiId}" apiId="${apiId}" id="api">

                    <div class="swiper-container g-t-slide-content">
                        <div class="swiper-wrapper">

                            <div name="tag" class="swiper-slide">
                                <div class="casino-wrap">
                                    <div class="casino-list">
                                        <div class="mui-row"><%--所有游戏--%>
                                            <c:forEach var="game" items="${allGame}" varStatus="tagMap">
                                                    <div class="mui-col-xs-3">
                                                        <button data-rel='{"dataApiTypeId":"2","dataApiId":"${game.value.apiId}","dataStatus":"${game.value.status}",
                                                            "dataGameCode":"${game.value.apiId!=10? game.value.code : ''}","dataGameId":"${game.value.apiId!=10? game.value.gameId : ''}",
                                                            "dataApiName":"${gbFn:getGameName(game.value.gameId)}","target":"goGame","opType":"function"}' class="" >

                                                            <div class="img-wrap">
                                                                <img data-src="${soulFn:getImagePath(domain, game.value.cover)}" src="${soulFn:getImagePath(domain, game.value.cover)}">
                                                            </div>
                                                            <p>${gbFn:getGameName(game.value.gameId)}</p>
                                                        </button>
                                                    </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <c:forEach items="${siteGameTagByTagId}" var="t">
                                <div class="swiper-slide">
                                    <div class="casino-wrap">
                                        <div class="casino-list">
                                            <div class="mui-row">
                                                <c:forEach items="${t.value}" var="g">
                                                    <c:if test="${not empty allGame[g]}">
                                                        <div class="mui-col-xs-3">
                                                            <button data-rel='{"dataApiTypeId":"2","dataApiId":"${allGame[g].apiId}","dataStatus":"${allGame[g].status}",
                                                            "dataGameCode":"${allGame[g].apiId!=10? allGame[g].code : ''}","dataGameId":"${allGame[g].apiId!=10? allGame[g].gameId : ''}",
                                                            "dataApiName":"${gbFn:getGameName(allGame[g].gameId)}","target":"goGame","opType":"function"}' class="" >

                                                                <div class="img-wrap">
                                                                    <img data-src="${soulFn:getImagePath(domain, allGame[g].cover)}" src="${soulFn:getImagePath(domain, allGame[g].cover)}">
                                                                </div>
                                                                <p>${gbFn:getGameName(allGame[g].gameId)}</p>
                                                            </button>
                                                        </div>
                                                    </c:if>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </div>
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
</body>
<%@include file="../include/include.js.jsp" %>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/Game.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
<script type="text/javascript" src="${resRoot}/js/casino/CasinoTemp.js"></script>
<%@ include file="/include/include.footer.jsp" %>
