<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${views.password_auto['修改登录密码']}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
</head>

<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
            <c:if test="${os ne 'android'}">
                <header class="mui-bar mui-bar-nav">
                    <a class="mui-action-backs mui-icon mui-icon-left-nav mui-pull-left"></a>
                    <h1 class="mui-title">${views.password_auto['修改登录密码']}</h1>
                </header>
            </c:if>
            <center>
                <img src="${resRoot}/lottery/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
            </center>
        </body>
    </c:when>
    <c:otherwise>
        <%@ include file="/themes/common/password/password.updatepassword.jsp" %>
    </c:otherwise>
</c:choose>

</html>
