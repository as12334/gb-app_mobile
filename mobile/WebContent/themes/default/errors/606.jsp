<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>606 | ${views.themes_auto['强制踢出']}</title>
    <script>
        var resRoot = '${resRoot}';
    </script>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>
<body class="orange-skin">
<div class="mui-content">
    <div class="no-data-img kick-out"></div>
    <div class="no-data-txt">${views.themes_auto['您已被强制踢出']}</div>
    <div class="no-data-txt-2">${views.themes_auto['您的账号']}<a href="#">${views.themes_auto['在线客服']}</a>${views.themes_auto['我们将竭诚为您服务']}</div>
</div>
</body>
</html>
<%@ include file="/include/include.footer.jsp" %>