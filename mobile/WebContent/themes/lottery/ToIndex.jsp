<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html><!-- manifest="${resRoot}/appcache/lottery.appcache" -->
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
    <style>
        .middle-content {bottom: 0;}
    </style>
    <script>
        if(os==='android'||os==='app_ios'){window.location.replace('/mainIndex.html')}
    </script>
</head>

<body class="bg-color">
<header class="mui-bar mui-bar-top mui-bar-nav" style="display:${os eq 'android'?'none':'block'}">
    <span class="home-logo logo-center"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"></span>
</header>
<div class="mui-scroll-wrapper middle-content bg-color">
    <div class="mui-scroll">
        <div class="p-1r">
            <h4 class="tit-center">${views.include_auto['下载APP随时随地想赚就赚']}</h4>

            <button type="button" class="${myos != ''} mui-btn mui-btn-ios mui-btn-block mui-btn-outlined down-app" data-href="${iosUrl}">
                <i class="mobile-ico ico-app"></i><span>${views.include_auto['IOS下载']}</span>
            </button>
            <button type="button" class="mui-btn mui-btn-android mui-btn-block mui-btn-outlined down-app" data-href="${androidUrl}">
                <i class="mobile-ico ico-and"></i><span>${views.include_auto['Android下载']}</span>
            </button>
            <div class="p1"><img src="${resRoot}/lottery/themes/images/p1.png" width="60%"></div>
            <button type="button" class="mui-btn mui-btn-danger mui-btn-block goon" data-href="/mainIndex.html">${views.include_auto['继续访问']}${views.include_auto['连接成功']}</button>
            <a class="mui-btn mui-btn-block" data-terminal="pc">返回电脑版</a>
            <div class="tit-tips">${views.include_auto['温馨提示']}</div>
        </div>
    </div>
</div>
</body>

</html>

<script>
    curl(['${resRoot}/lottery/js/ToIndex'],function (Page) {
            page = new Page();
        });
</script>
