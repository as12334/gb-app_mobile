<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
</head>

<body onload="initPage()">
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-icon mui-icon-arrowleft mui-pull-left mui-action-back"></a>
    <h1 class="mui-title">APP下载</h1>
</header>
<div class="download-content-ios">
    <img src="${resRoot}/images/apple-download-ios-bg.png" class="h-bg"/>
    <img src="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" class="app-logo" />
    <a data-rel='{"target":"download","opType":"function","url":"${iosUrl}"}' class="btn-install">${views.themes_auto['点击安装']}</a>
    <img src="${resRoot}/images/apple-down.png" class="down-step"/>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/download/DownLoad.js?v=${rcVersion}"></script>
</html>

<%@ include file="/include/include.footer.jsp" %>