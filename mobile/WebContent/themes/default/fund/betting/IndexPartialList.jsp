<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<tr hidden>
    <td><span id="hiddenTotalCount" value="${command.paging.totalCount}"></span></td>
</tr>
<c:choose>
    <c:when test="${fn:length(command.result)>0}">
        <c:forEach items="${command.result}" var="p" varStatus="status">
            <%--<c:set var="resultVo" value="${p.resultVo}"/>--%>
            <%--<c:set var="len" value="${fn:length(resultVo.apiSportResultVoList)}"/>--%>
            <tr data-href="${root}/fund/betting/gameRecordDetail.html?searchId=${command.getSearchId(p.id)}">
                <td>${gbFn:getApiName(p.apiId.toString())}<br/>${gbFn:getGameName(p.gameId.toString())}</td>
                <td><c:if test="${p.terminal==2}"><i
                        class="mb_icon"></i></c:if>${soulFn:formatDateTz(p.betTime, DateFormat.DAY,timeZone)}
                    <br>${soulFn:formatDateTz(p.betTime, DateFormat.SECOND,timeZone)}
                </td>
                <td>${soulFn:formatCurrency(p.singleAmount)}</td>

                <c:choose>
                    <c:when test="${p.orderState eq 'settle'&&p.profitAmount > 0}">
                        <td><span class="co-green">+${soulFn:formatCurrency(p.profitAmount)}</span></td>
                    </c:when>
                    <c:when test="${p.orderState eq 'settle'&&p.profitAmount<0}">
                        <td><span class="co-red3">${soulFn:formatCurrency(p.profitAmount)}</span></td>
                    </c:when>
                    <c:when test="${p.orderState eq 'settle'&&!empty p.profitAmount&&p.profitAmount==0}">
                        <td>0</td>
                    </c:when>
                    <c:otherwise>
                        <td>--</td>
                    </c:otherwise>
                </c:choose>
                <td>
            <span class="label
            <c:choose>
                <c:when test="${p.orderState=='settle'}">
                    label-success
                </c:when>
                <c:when test="${p.orderState=='pending_settle'}">
                    label-orange
                </c:when>
                <c:otherwise>
                    label-danger
                </c:otherwise>
            </c:choose>">${dicts.player.order[p.orderState]}</span>
                </td>
            </tr>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <tr><td style="height: 40px" colspan="5"><p>${views.fund_auto['暂无内容']}</p></td></tr>
    </c:otherwise>
</c:choose>
<tr hidden>
    <td><input value="${command.paging.lastPageNumber}" id="lastPageNumber"></td>
</tr>
