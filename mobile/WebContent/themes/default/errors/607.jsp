<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<script>
    var root = '${root}';
    var resRoot = '${resRoot}';
</script>
<!DOCTYPE html>
<html>
<head>
    <title>607 | 站点维护</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<%@ include file="/themes/common/errors/error.607.jsp" %>

</html>
