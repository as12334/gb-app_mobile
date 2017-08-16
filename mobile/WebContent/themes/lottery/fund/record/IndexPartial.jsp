<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result)>0}">
        <c:forEach items="${command.result}" var="s">
            <tr data-href="${root}/fund/record/details.html?searchId=${command.getSearchId(s.id)}">
                <td><span class="text-gray">${soulFn:formatDateTz(s.createTime, DateFormat.DAY,timeZone)}</span></td>
                <td><span class="text-green2">${soulFn:formatCurrency(s.transactionMoney)}</span></td>
                <td>${dicts.common.transaction_type[s.transactionType]}</td>
                <td>
                    <c:choose>
                        <c:when test="${s.status=='success'}"><span class="green">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='failure'}"><span class="red">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='pending_pay'}"><span class="orange">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='pending'}"><span class="blue">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='exchange'}"><span class="blue">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='over_time'}"><span class="red">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='process'}"><span class="blue">${dicts.common.status[s.status]}</span></c:when>
                        <c:when test="${s.status=='reject'}"><span class="red">${dicts.common.status[s.status]}</span></c:when>
                        <c:otherwise><span class="gray">${views.fund_auto['其他']}</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <tr >
            <td style="height: 40px"><p>${views.fund_auto['暂无内容']}</p></td>
        </tr>
    </c:otherwise>
</c:choose>
<tr hidden>
    <td><input value="${command.paging.lastPageNumber}" id="lastPageNumber"></td>
</tr>
