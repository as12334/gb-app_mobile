<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>

<body class="orange-skin">
<div class="mui-content">
    <div class="no-data-img kick-out"></div>
    <div class="no-data-txt">${views.themes_auto['您已被强制踢出']}</div>
    <div class="no-data-txt-2">${views.themes_auto['您的账号']}<a href="#">${views.themes_auto['在线客服']}</a>${views.themes_auto['我们将竭诚为您服务']}</div>
</div>
</body>
<script>
    mui('.mui-scroll-wrapper').scroll({indicators: false});
    mui.init({});
</script>

