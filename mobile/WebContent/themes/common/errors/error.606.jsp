<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>

<body class="orange-skin">
<div class="mui-content">
    <div class="no-data-img kick-out"></div>
    <div class="no-data-txt">您已被强制踢出</div>
    <div class="no-data-txt-2">您的账号有可能在另外一个地点登录或账号长时间未进行任何操作，您被强制踢出。如有疑问请联系 <a href="#">在线客服</a> 我们将竭诚为您服务。</div>
</div>
</body>
<script>
    mui('.mui-scroll-wrapper').scroll({indicators: false});
    mui.init({});
</script>

