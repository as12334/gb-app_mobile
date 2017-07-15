<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <title>${views.index_auto['玩家注册服务条款']}</title>
</head>
<body>
<style type="text/css">
    .about-text h2 {
        line-height: normal !important;
        color: #ffffff;
    }
</style>

<div class="mui-row">
    <div class="about-text">
        ${not empty terms.value ? terms.value : terms.defaultValue}
    </div>
</div>
</body>
<script>
    $('.about-text').height(function () {
        $(this).css({'min-height': $(window).height() - 176});
    });
</script>
</html>