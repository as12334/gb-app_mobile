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