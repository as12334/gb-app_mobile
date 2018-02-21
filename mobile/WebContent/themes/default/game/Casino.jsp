<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row">
    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted electronic-type">
        <span class="shadow-left"></span><span class="shadow-right"></span>
        <div id="type-slider" class="mui-scroll mui-casino">
            <c:forEach var="a" items="${command}" varStatus="vs">
                <c:if test="${vs.index == 0}">
                    <input type="hidden" id="casino-id" value="${a.apiId}"/>
                </c:if>
                <a href="#casino${vs.count}" class="mui-control-item ${vs.index == 0 ? 'mui-active':''}"
                   data-casino-id="${a.apiId}" data-status="${api.get(a.apiId.toString()).systemStatus eq 'maintain' ? 'maintain' : siteApi.get(a.apiId.toString()).systemStatus}">
                    <c:if test="${api.get(a.apiId.toString()).systemStatus eq 'maintain' or siteApi.get(a.apiId.toString()).systemStatus eq 'maintain'}">
                        <div class="mui-maintain casino"></div>
                    </c:if>
                    <c:set var="path" value="${resRoot}/images/api/api-casino-${a.apiId}.png"></c:set>
                    <img src="${soulFn:getImagePath(domain, path)}"/>
                    ${gbFn:getApiName(a.apiId.toString())}
                </a>
            </c:forEach>
        </div>
    </div>
</div>
<!-- 电子游艺 -->
<input type="hidden" id="api-type" value="casino"/>
<div class="mui-row">
    <div class="electronic-srch">
        <i class="mui-search-clear"></i>
        <a id="search-game"></a>
        <input type="text" id="game-name" class="mui-input-clear" placeholder="${views.game_auto['输入游戏名']}">
    </div>
</div>
<div id="casino-container">
    <c:forEach var="a" items="${command}" varStatus="vs">
        <c:if test="${a.apiId == 6}">
            <script>
                window.top.loadJsFile('${resRoot}/js/game/PtGame.js?v=${rcVersion}');
            </script>
        </c:if>
        <div id="casino${vs.count}" class="mui-control-content ${vs.index == 0 ? 'mui-active' : ''}">
                <%--loading-x: 是否重新加载--%>
            <input type="hidden" id="loading-${a.apiId}" value="${vs.index == 0 ? 'false' : 'true'}">
                <%--paging-x: 当前API对应的当前页--%>
            <input type="hidden" id="paging-${a.apiId}" value="1">
            <div class="mui-row">
                <div class="electronic-list" style="margin-bottom: 0px;">
                    <%@include file="/include/include.loading.jsp" %>
                    <div class="mui-row casino-content-${a.apiId}">
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

