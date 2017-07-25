<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%-- 电子游艺 --%>
<c:if test="${command.paging.firstPage}">
    <input type="hidden" id="total-page-${apiId}" value="${command.paging.lastPageNumber}" title="${command.result.size()}" />
</c:if>
<c:choose>
    <c:when test="${command.result.size() > 0}">
        <c:forEach var="g" items="${command.result}" varStatus="vs">
            <c:forEach var="gi" items="${gameI18n}">
                <c:if test="${g.gameId == gi.key}">
                    <div class="mui-col-xs-3">
                        <%--BB电子不支持直接进入电子游戏，需进入大厅--%>
                        <a class="item ${g.apiId eq 10?'_api':'_game'}" data-api-type-id="2" data-api-id="${g.apiId}" <c:if test="${g.apiId!=10}">data-game-id="${g.gameId}" data-game-code="${g.code}"</c:if>data-status="${g.status}">
                            <p class="m-b-0">
                                <img data-src="${soulFn:getImagePath(domain, gi.value.cover)}"
                                     src="${soulFn:getImagePath(domain, gi.value.cover)}"/>
                            </p>
                            <p class="">${gbFn:getGameName(g.gameId)}</p>
                        </a>
                    </div>
                </c:if>
            </c:forEach>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="mui-no-data">${views.game_auto['此地无银']}</div>
    </c:otherwise>
</c:choose>
