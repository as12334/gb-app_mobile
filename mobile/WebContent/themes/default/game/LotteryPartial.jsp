<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%-- 彩票游戏 --%>
<c:if test="${command.paging.firstPage}">
    <input type="hidden" id="total-page-${apiId}" value="${command.paging.lastPageNumber}" title="${command.paging.firstPage}" />
</c:if>
<c:choose>
    <c:when test="${command.result.size() > 0}">
        <c:forEach var="g" items="${command.result}" varStatus="vs">
            <c:forEach var="gi" items="${gameI18n}">
                <c:if test="${g.gameId == gi.key}">
                    <div class="mui-col-xs-4">
                        <%--彩票KG和BB只支持跳转彩票大厅，并不支持跳转子类--%>
                        <a class="item _api" data-api-type-id="4" data-api-id="${g.apiId}" <%--data-game-id="${g.gameId}" data-game-code="${g.code}"--%> data-status="${g.status}">
                            <p>
                                <img data-src="${soulFn:getImagePath(domain, gi.value.cover)}"
                                     src="${soulFn:getImagePath(domain, gi.value.cover)}"/>
                            </p>
                            <p>${gbFn:getGameName(g.gameId)}</p>
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
