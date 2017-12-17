<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>606 | 强制踢出</title>
    <script>
        var resRoot = '${resRoot}';
    </script>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>
<body class="orange-skin">
<div class="mui-content">
    <div class="no-data-img kick-out"></div>
    <div class="no-data-txt">${views.themes_auto['您已被强制踢出']}</div>
    <div class="no-data-txt-2">您的账号有可能在另外一个地点登录或账号长时间未进行任何操作，您被强制踢出。如有疑问请联系 <a href="#">${views.themes_auto['在线客服']}</a> 我们将竭诚为您服务。</div>
</div>
</body>
</html>
<%@ include file="/include/include.footer.jsp" %>