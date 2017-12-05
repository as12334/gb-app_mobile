<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <title>${views.deposit_auto['存款']}</title>
</head>

<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <c:if test="${os ne 'app_ios'}">
                    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                </c:if>
                <h1 class="mui-title">${views.deposit_auto['存款']}</h1>
            </header>
        </c:if>
        <center>
            <img src="${resRoot}/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
        </center>
        </body>
    </c:when>
    <c:otherwise>
        <%@ include file="/themes/common/deposit/deposit.deposit.jsp" %>
    </c:otherwise>
</c:choose>



</html>
<%@ include file="/include/include.footer.jsp" %>
