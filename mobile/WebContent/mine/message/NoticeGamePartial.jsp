<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:if test="${empty unReadType}">
    <c:choose>
        <c:when test="${fn:length(command.result)>0}">
            <c:forEach items="${command.result}" var="s" varStatus="dex">
                <c:forEach items="${apiMap}" var="apis">
                    <c:if test="${apis.value.apiId==s.apiId}">
                        <li data-url="${root}/message/gameNoticeDetail.html?searchId=${command.getSearchId(s.id)}">
                            <div class="i">
                                <div class="ct">
                                    <p><c:if test="${not empty s.title}"> 【${fn:substring(s.title,0,50)}<c:if
                                            test="${fn:length(s.title)>50}">...</c:if>】</c:if>
                                            ${fn:substring(s.content,0,80)}<c:if
                                                test="${fn:length(s.content)>80}">...</c:if></p>
                                </div>
                                <p>${gbFn:getApiName((s.apiId).toString())}
                                    <c:if test="${s.gameId!=null}">——${gbFn:getGameName((s.gameId).toString())}</c:if>
                                    &nbsp;${soulFn:formatDateTz(s.publishTime, DateFormat.DAY_SECOND,timeZone)}</p>
                            </div>
                        </li>
                    </c:if>
                </c:forEach>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <li>
                <div class="i">
                    <div class="ct">
                        <p class="no-data">${views.mine_auto['暂无内容']}</p>
                    </div>
                </div>
            </li>
        </c:otherwise>
    </c:choose>
    <input value="${command.paging.lastPageNumber}" id="gameLastPageNumber" hidden>
</c:if>

