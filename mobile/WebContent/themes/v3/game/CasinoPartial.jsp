<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<%-- 电子游艺 --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/layer.css"/>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <%--<%@include file="../common/Head.jsp" %>--%>
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav casino-list-bar">
            <soul:button text="" opType="function" target="goToLastPage" cssClass="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></soul:button>
            <%@include file="../common/Assert.jsp"%>
            <h1 class="mui-title">${empty siteApi.name?(gbFn:getApiName(apiId)):siteApi.name}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-casino-list" id="pullfresh">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="casino-wrap">
                    <div class="mui-input-row">
                        <div class="electronic-search">
                            <soul:button target="searchGame" text="${views.themes_auto['搜索']}" opType="function" cssClass="btn-search"/>
                            <input type="text" id="game-name" class="mui-input-clear" placeholder="${views.themes_auto['输入游戏名称']}">
                        </div>
                    </div>
                    <c:if test="${command.paging.firstPage}">
                        <input type="hidden" apiId="${apiId}" id="api" pageNumber="1"
                               value="${command.paging.lastPageNumber}" title="${command.result.size()}"/>
                    </c:if>
                    <div class="casino-list">
                        <div class="mui-row">
                            <c:choose>
                                <c:when test="${command.result.size() > 0}">
                                    <c:forEach var="g" items="${command.result}" varStatus="vs">
                                        <c:forEach var="gi" items="${gameI18n}">
                                            <c:if test="${g.gameId == gi.key}">
                                                <%--BB电子不支持直接进入电子游戏，需进入大厅--%>
                                                <div class="mui-col-xs-3">
                                                    <soul:button dataApiTypeId="2"
                                                                 dataApiId="${g.apiId}"
                                                                 dataStatus="${g.status}"
                                                                 dataGameCode="${g.apiId!=10? g.code : ''}"
                                                                 dataGameId="${g.apiId!=10? g.gameId : ''}"
                                                                 dataApiName="${gbFn:getGameName(g.gameId)}"
                                                                 target="goGame" text="" opType="function" cssClass="">
                                                        <div class="img-wrap">
                                                            <img data-src="${soulFn:getThumbPath(domain, gi.value.cover,60,60)}"
                                                                 src="${soulFn:getThumbPath(domain, gi.value.cover,60,60)}"/>
                                                        </div>
                                                        <p>${gbFn:getGameName(g.gameId)}</p>
                                                    </soul:button>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@include file="../common/Footer.jsp" %>
        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
        <!--浮窗广告轮播-->
        <div class="ads-slider">
            <a href="javascript:" class="close-ads"></a>
            <div class="mui-slider">
                <div class="mui-slider-group">
                    <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/ads-banner-01.png"/></a>
                    </div>
                    <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/ads-banner-01.png"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--语言弹窗-->
<%@include file="../common/LangMenu.jsp"%>
</body>
<%@include file="../include/include.js.jsp" %>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/Game.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>