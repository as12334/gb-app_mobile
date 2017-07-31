<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row">
    <div class="about-text">
        <c:set var="c" value="${about.content == null ? '' : about.content}" />
        ${c.replace("${company}", siteName)}
    </div>
</div>

<script>
    $('.about-text').height(function () {
        $(this).css({'min-height': $(window).height() - 176});
    });
</script>
