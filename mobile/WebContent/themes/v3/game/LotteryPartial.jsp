<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%-- 彩票游戏 --%>
<c:if test="${command.paging.firstPage}">
    <input type="hidden" id="total-page-${apiId}" value="${command.paging.lastPageNumber}"
           title="${command.paging.firstPage}" pageNumber="1"/>
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
                                     dataGameCode="${g.apiId == 10||g.apiId==2?'':g.code}" dataStatus="${g.status}"
                                     target="goGame" text="" opType="function" cssClass="item _api">
                            <img src="${soulFn:getImagePath(domain, gi.value.cover)}" alt="" class="lottery-img"/>
                            <div class="mui-media-body">${gbFn:getGameName(g.gameId)}</div>
                        </soul:button>
                    </li>
                </c:if>
            </c:forEach>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
            <a href="#" class="item _api" data-api-type-id="4">
                <div class="mui-no-data">${views.game_auto['此地无银']}</div>
            </a>
        </li>
    </c:otherwise>
</c:choose>