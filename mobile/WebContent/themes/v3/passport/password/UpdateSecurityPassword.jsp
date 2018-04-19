<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${views.passport_auto['修改安全密码']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
</head>

<%@ include file="/themes/common/passport/password/password.updatesecurity.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>