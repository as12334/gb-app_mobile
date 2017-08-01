<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
    <style>
        .middle-content {bottom: 0;}
    </style>
</head>
<body>
<header class="mui-bar mui-bar-top mui-bar-nav">
    <span class="home-logo logo-center"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"></span>
</header>
<div class="mui-scroll-wrapper middle-content bg-color">
    <div class="mui-scroll">
        <div class="p-1r">
            <h4 class="tit-center">${views.include_auto['下载APP随时随地想赚就赚']}</h4>
            <button type="button" class="mui-btn mui-btn-ios mui-btn-block mui-btn-outlined"><i class="mobile-ico ico-app"></i>${views.include_auto['IOS下载']}</button>
            <button type="button" class="mui-btn mui-btn-android mui-btn-block mui-btn-outlined"><i class="mobile-ico ico-and"></i>${views.include_auto['Android下载']}</button>
            <div class="p1"><img src="${resRoot}/themes/lottery/images/p1.png" width="60%"></div>
            <button type="button" class="mui-btn mui-btn-danger mui-btn-block">${views.include_auto['继续访问']}${views.include_auto['连接成功']}</button>
            <div class="tit-tips">${views.include_auto['温馨提示']}</div>
        </div>
    </div>
</div>
<script type="text/javascript" charset="utf-8">
    mui.init();
</script>
</body>

</html>