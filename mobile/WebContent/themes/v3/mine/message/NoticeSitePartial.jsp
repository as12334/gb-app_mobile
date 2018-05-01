<%--@elvariable id="command" type="org.soul.model.msg.notice.vo.VNoticeReceivedTextListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <li class="${s.receiveStatus=='12'?'':'no-read'}" >
                <div class="i"><a  href="" class="gb-radio" value="${s.id}" name="site1_check"></a>
                    <div class="ct" data-rel='{"target":"${root}/message/announcementDetail.html?searchId=${command.getSearchId(s.id)}","opType":"href"}'>
                        <p>${s.shortTitle50}</p>
                    </div>
                    <p data-rel='{"target":"${root}/message/announcementDetail.html?searchId=${command.getSearchId(s.id)}","opType":"href"}'>
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
