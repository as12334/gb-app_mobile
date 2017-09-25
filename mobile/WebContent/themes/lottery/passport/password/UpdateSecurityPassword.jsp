<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.passport_auto['修改安全密码']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
</head>

<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
            <c:if test="${os ne 'android'}">
                <header class="mui-bar mui-bar-nav">
                    <a class="mui-action-backs mui-icon mui-icon-left-nav mui-pull-left"></a>
                    <h1 class="mui-title">${views.passport_auto['修改安全密码']}</h1>
                </header>
            </c:if>
            <center>
                <img src="${resRoot}/lottery/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
            </center>
        </body>
    </c:when>
    <c:otherwise>
        <%@ include file="/themes/common/passport/password/password.updatesecurity.jsp" %>
    </c:otherwise>
</c:choose>

</html>
