<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>605 | IP限制</title>
    <script>
        var resRoot = '${resRoot}';
    </script>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<%@ include file="/themes/common/errors/error.605.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>