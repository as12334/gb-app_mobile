<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<c:set var="siteId" value="<%=SessionManagerCommon.getSiteId() %>"/>
<!DOCTYPE html>
<html manifest="${resRoot}/appcache/v3.appcache">
<head>
    <%@include file="include/include.head.jsp" %>
    <title>${siteName}</title>
</head>
<body class="load-body">
<div class="load-bg loading-page">
    <div class="load-bar">
        <p>${views.app_auto['请耐心等待']}</p>
        <div id="load-bar-1" class="mui-progressbar mui-progressbar-infinite"><span></span></div>
    </div>
    <div class="load-bar-middle">
        <span id="welcome" class="btn-back" data-rel='{"target":"clickWelcome","opType":"function"}'>${views.app_auto['访问主页']}</span>
    </div>
    <div class="load-bar-bottom">
        <img src="${appLogo}" width="110">
        <div class="copy-right">
            Copyright © &nbsp;${siteName}&nbsp; Reserved.
        </div>
        <div class="tech-support">
            ${views.app_auto['技术支持']}：<span class="icon-tech-support ${centerId==-3?'dbo':''}"></span>
        </div>
    </div>
</div>


</body>
<%@include file="include/include.js.jsp"%>
<script src="${resRoot}/js/ToIndex.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>