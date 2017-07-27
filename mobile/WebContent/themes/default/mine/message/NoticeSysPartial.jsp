<%--@elvariable id="command" type="so.wwb.gamebox.model.company.operator.vo.SystemAnnouncementListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${fn:length(command.result)>0}">
        <c:forEach items="${command.result}" var="s">
            <li data-url="${root}/message/systemNoticeDetail.html?searchId=${command.getSearchId(s.id)}">
                <div class="i">
                    <div class="ct">
                        <p>${s.shortContentText50}</p>
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
