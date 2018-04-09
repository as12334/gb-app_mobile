<%--@elvariable id="command" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row">
    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted electronic-type">
        <span class="shadow-left"></span><span class="shadow-right"></span>
        <div id="type-slider" class="mui-scroll mui-chess">
            <c:forEach var="a" items="${command}" varStatus="vs">
                <c:if test="${vs.index == 0}">
                    <input type="hidden" id="chess-id" value="${a.apiId}"/>
                </c:if>
                <a href="#chess${vs.count}" class="mui-control-item ${vs.index == 0 ? 'mui-active':''}"
                   data-chess-id="${a.apiId}" data-status="${a.apiStatus}">
                    <c:if test="${a.apiStatus eq 'maintain'}">
                        <div class="mui-maintain chess"></div>
                    </c:if>
                    <c:set var="path" value="${resRoot}/images/api/api-chess-${a.apiId}.png"></c:set>
                    <img src="${path}"/>
                    ${a.name}
                </a>
            </c:forEach>
        </div>
    </div>
</div>
<!-- 棋牌 -->
<input type="hidden" id="api-type" value="chess"/>
<div class="mui-row">
    <div class="electronic-srch">
        <i class="mui-search-clear"></i>
        <a id="search-game"></a>
        <input type="text" id="game-name" class="mui-input-clear" placeholder="${views.game_auto['输入游戏名']}">
    </div>
</div>
<div id="chess-container">
    <c:forEach var="a" items="${command}" varStatus="vs">
        <%--<c:if test="${a.apiId == 6}">
            <script>
                window.top.loadJsFile('${resRoot}/js/game/PtGame.js?v=${rcVersion}');
            </script>
        </c:if>--%>
        <div id="chess${vs.count}" class="mui-control-content ${vs.index == 0 ? 'mui-active' : ''}">
                <%--loading-x: 是否重新加载--%>
            <input type="hidden" id="loading-${a.apiId}" value="${vs.index == 0 ? 'false' : 'true'}">
                <%--paging-x: 当前API对应的当前页--%>
            <input type="hidden" id="paging-${a.apiId}" value="1">
            <div class="mui-row">
                <div class="electronic-list" style="margin-bottom: 0px;">
                    <%@include file="/include/include.loading.jsp" %>
                    <div class="mui-row chess-content-${a.apiId}">
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

