<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<%--<c:choose>--%>

    <%--<c:when test="${command ne null}">--%>
        <c:forEach items="${command}" var="p">
            <tr>
                <td>${p.recommendUserName}</td><%--被推荐人账号--%>
                <td>${soulFn:formatDateTz(p.createTime, DateFormat.DAY, timeZone)}</td> <%--注册时间--%>
                <td>${p.status}</td>         <%--状态--%>
                <td>${p.rewardAmount}</td>    <%--推荐奖励--%>
            </tr>
        </c:forEach>
    <%--</c:when>--%>

    <%--<c:otherwise>
        <table>
            <tbody>
            <tr >
                <td style="height: 40px"><p>${views.fund_auto['暂无内容']}</p></td>
            </tr>
            </tbody>
        </table>

    </c:otherwise>--%>
<%--</c:choose>--%>
