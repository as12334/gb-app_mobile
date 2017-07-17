<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${fn:length(command.result)>0}">
        <c:forEach items="${command.result}" var="s">

            <c:set var="noHtmlContent" value="${soulFn.replaceHtml(s.content)}"/>
            <li data-url="${root}/message/systemNoticeDetail.html?searchId=${command.getSearchId(s.id)}">
                <div class="i">
                    <div class="ct">
                        <p>${fn:substring(noHtmlContent,0,50)}<c:if test="${fn:length(noHtmlContent)>50}">...</c:if></p>
                    </div>
                    <p>${soulFn:formatDateTz(s.publishTime, DateFormat.DAY_SECOND,timeZone)}</p>
                </div>
            </li>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <li >
            <div class="i">
                <div class="ct">
                    <p class="no-data">${views.mine_auto['暂无内容']}</p>
                </div>
            </div>
        </li>
    </c:otherwise>
</c:choose>
<input value="${command.paging.lastPageNumber}" id="sysLastPageNumber" hidden>
