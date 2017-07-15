<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <li class="${s.receiveStatus=='12'?'':'no-read'}" >
                <div class="i"><a  href="" class="gb-radio" value="${s.id}" name="site1_check"></a>
                    <div class="ct" data-url="${root}/message/announcementDetail.html?searchId=${command.getSearchId(s.id)}">
                        <p><c:if test="${not empty s.title}">${fn:substring(s.title,0,50)}</c:if><c:if
                            test="${fn:length(s.title)>50}">...</c:if>
                            <%--<pre>${s.content} </pre>--%></p>
                    </div>
                    <p data-url="${root}/message/announcementDetail.html?searchId=${command.getSearchId(s.id)}">
                        &nbsp;${soulFn:formatDateTz(s.receiveTime, DateFormat.DAY_SECOND,timeZone)}</p>
                </div>
            </li>
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
<input value="${command.paging.lastPageNumber}" id="site1LastPageNumber" hidden>