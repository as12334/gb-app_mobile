<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<script>
    var resRoot = '${resRoot}';
</script>
<!DOCTYPE html>
<html>
<head>
    <title>404 | NOT FIND</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<%@ include file="/themes/common/errors/error.404.jsp" %>

</html>
