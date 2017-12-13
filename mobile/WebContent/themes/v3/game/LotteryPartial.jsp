<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<%-- 彩票游戏 --%>
<c:if test="${command.paging.firstPage}">
    <input type="hidden" id="total-page-${apiId}" value="${command.paging.lastPageNumber}" title="${command.paging.firstPage}" pageNumber="1"/>
</c:if>
<c:choose>
    <c:when test="${command.result.size() > 0}">
        <c:forEach var="g" items="${command.result}" varStatus="vs">
            <c:forEach var="gi" items="${gameI18n}">
                <c:if test="${g.gameId == gi.key}">
                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                        <soul:button dataApiTypeId="4"
                                     dataApiId="${g.apiId}"
                                     dataApiName="${gbFn:getGameName(g.gameId)}"
                                     dataGameId="${g.gameId}"
                                     dataGameCode="${g.apiId == 10||g.apiId==2?'':g.code}" dataStatus="${g.status}"
                                     target="goApiGame" text="" opType="function" cssClass="item _api">
                            <img src="${soulFn:getThumbPath(domain, gi.value.cover,60,60)}" alt="" class="lottery-img"/>
                            <div class="mui-media-body">${gbFn:getGameName(g.gameId)}</div>
                        </soul:button>
                    </li>
                </c:if>
            </c:forEach>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
    </c:otherwise>
</c:choose>