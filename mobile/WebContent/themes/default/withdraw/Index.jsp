<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<%--@elvariable id="bank" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<%--@elvariable id="btc" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<!DOCTYPE html>
<html>

<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${views.withdraw_auto['取款']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/plugin/map.js?v=${rcVersion}"></script>
    <%@ include file="/include/include.js.jsp" %>
</head>


<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${views.withdraw_auto['取款']}</h1>
            </header>
        </c:if>
        <center>
            <img src="${resRoot}/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
        </center>
        </body>
    </c:when>
    <c:otherwise>
        <%@ include file="/themes/common/withdraw/withdraw.index.jsp" %>
    </c:otherwise>
</c:choose>



</html>
