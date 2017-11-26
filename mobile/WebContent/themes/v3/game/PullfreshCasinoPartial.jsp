<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.SiteGameListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%-- 电子游艺 --%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${command.result.size() > 0}">
        <c:forEach var="g" items="${command.result}" varStatus="vs">
            <c:forEach var="gi" items="${gameI18n}">
                <c:if test="${g.gameId == gi.key}">
                    <div class="mui-col-xs-3">
                        <%--<a href="#" dataApiType-id="2" dataApiId="${g.apiId}"
                           <c:if test="${g.apiId!=10}">data-game-id="${g.gameId}" data-game-code="${g.code}"</c:if>
                           data-status="${g.status}">
                            <div class="img-wrap">
                                <img data-src="${soulFn:getImagePath(domain, gi.value.cover)}"
                                     src="${soulFn:getImagePath(domain, gi.value.cover)}"/>
                            </div>
                            <p>${gbFn:getGameName(g.gameId)}</p>
                        </a>--%>
                        <soul:button dataApiTypeId="2"
                                     dataApiId="${g.apiId}"
                                     dataStatus="${g.status}"
                                     dataGameCode="${g.apiId!=10? g.code : ''}"
                                     dataGameId="${g.apiId!=10? g.gameId : ''}"
                                     dataApiName="${gbFn:getGameName(g.gameId)}"
                                     target="goGame" text="" opType="function" cssClass="">
                            <div class="img-wrap">
                                <img data-src="${soulFn:getImagePath(domain, gi.value.cover)}"
                                     src="${soulFn:getImagePath(domain, gi.value.cover)}"/>
                            </div>
                            <p>${gbFn:getGameName(g.gameId)}</p>
                        </soul:button>
                    </div>
                </c:if>
            </c:forEach>
        </c:forEach>
    </c:when>
</c:choose>