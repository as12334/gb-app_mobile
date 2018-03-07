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
                            <div class="swiper-slide">所有游戏</div>
                            <c:forEach items="${siteGameTagByTagId}" var="t">
                                <div class="swiper-slide">${t.key}</div>
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
                                            <c:forEach var="game" items="${allGame}" varStatus="tagMap">
                                                    <div class="mui-col-xs-3">
                                                        <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.value.apiId}","dataStatus":"${game.value.status}",
                                                            "dataGameCode":"${game.value.apiId!=10? game.value.code : ''}","dataGameId":"${game.value.apiId!=10? game.value.gameId : ''}",
                                                            "dataApiName":"${gbFn:getGameName(game.value.gameId)}","target":"goGame","opType":"function"}'>

                                                            <div class="img-wrap">
                                                                <img data-src="${soulFn:getImagePath(domain, game.value.cover)}" src="${soulFn:getImagePath(domain, game.value.cover)}">
                                                            </div>
                                                            <p>${gbFn:getGameName(game.value.gameId)}</p>
                                                        </a>
                                                    </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>

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
<script type="text/javascript" src="${resRoot}/js/swiper.min.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/Game.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/casino/CasinoTemp.js"></script>
<%@ include file="/include/include.footer.jsp" %>
</body>
</html>
