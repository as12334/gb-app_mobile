<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${isLotterySite}">
        <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left back_icon"><i class="back">${views.app_auto['返回']}</i></a>
    </c:when>
    <c:otherwise>
        <%--<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left "></a>--%>
        <a class="mui-icon mui-icon-left-nav mui-pull-left" data-href="/mainIndex.html"></a>
    </c:otherwise>
</c:choose>
