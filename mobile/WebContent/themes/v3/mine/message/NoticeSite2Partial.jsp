<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <li class="${s.isRead==false?'no-read':''}">
                <div class="i"><a class="${s.isRead==false?'gb-unread-radio':'gb-radio'}" data-rel='{"target":"siteCheck","opType":"function"}'
                                  value="${s.id}" name="site2_check"></a>
                    <div class="ct"
                         data-rel='{"target":"${root}/message/playerAdvisoryDetail.html?id=${s.id}","opType":"href"}'>
                        <p>提问标题:&nbsp;<c:if
                                test="${not empty s.advisoryTitle}">${fn:substring(s.advisoryTitle,0,10)}</c:if><c:if
                                test="${fn:length(s.advisoryTitle)>10}">...</c:if></p>
                        <c:if test="${not empty s.replyTitle}">
                            <p>回复标题:&nbsp;${fn:substring(s.replyTitle,0,20).replace('【回复】','')}
                                <c:if test="${fn:length(s.replyTitle)>20}">...</c:if>
                            </p></c:if>
                    </div>
                    <p data-rel='{"target":"${root}/message/playerAdvisoryDetail.html?id=${s.id}","opType":"href"}'>
                        &nbsp;${soulFn:formatDateTz(s.advisoryTime, DateFormat.DAY_SECOND,timeZone)}</p>
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
<input value="${command.paging.lastPageNumber}" id="site2LastPageNumber" hidden>
