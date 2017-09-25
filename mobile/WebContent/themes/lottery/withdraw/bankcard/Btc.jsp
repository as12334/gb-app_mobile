<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}比特币</title>
    <%@ include file="../../include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
</head>

<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
            <c:if test="${os ne 'android'}">
                <header class="mui-bar mui-bar-nav">
                    <%@ include file="/include/include.toolbar.jsp" %>
                    <h1 class="mui-title">比特币</h1>
                </header>
            </c:if>
            <center>
                <img src="${resRoot}/lottery/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
            </center>
        </body>
    </c:when>
    <c:otherwise>
        <%@ include file="/themes/common/withdraw/bankcard/bankcard.btc.jsp" %>
    </c:otherwise>
</c:choose>

</html>
