<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html> <%--manifest="${resRoot}/appcache/lottery.appcache"--%>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
    <style>
        .middle-content {bottom: 0;}
    </style>
    <script>
        if (os === 'app_android' || os === 'app_ios') {
            window.location.replace('/mainIndex.html');
        }
    </script>
    <%
        if (userAgent.contains("MicroMessenger")){
            myos = "wechat";
        } else if (userAgent.contains("Android")) {
            myos = "browser_android";
        } else if (userAgent.contains("app_android")) {
            myos = "android";
        } else if (userAgent.contains("app_ios")) {
            myos = "app_ios";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod") || userAgent.contains("iOS")) {
            myos = "ios";
        }  else {
            myos = "pc";
        }
    %>
    <c:set value="<%=myos %>" var="os"/>
</head>

<body class="bg-color">
<header class="mui-bar mui-bar-top mui-bar-nav" style="display:${os eq 'android'?'none':'block'}">
    <span class="home-logo logo-center"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"></span>
</header>
<div class="mui-scroll-wrapper middle-content bg-color">
    <div class="mui-scroll">
        <div class="p-1r">
            <h4 class="tit-center">${views.include_auto['下载APP随时随地想赚就赚']}</h4>
            <c:choose>
                <c:when test="${os eq 'browser_android'}">
                    <button type="button" class="mui-btn mui-btn-android mui-btn-block mui-btn-outlined down-app" data-href="${androidUrl}">
                        <i class="mobile-ico ico-and"></i><span>${views.include_auto['立即下载']}</span>
                    </button>
                </c:when>
                <c:when test="${os eq 'ios'}">
                    <button type="button" class="mui-btn mui-btn-ios mui-btn-block mui-btn-outlined down-app" data-href="${iosUrl}">
                        <i class="mobile-ico ico-app"></i><span>${views.include_auto['立即下载']}</span>
                    </button>
                </c:when>
                <c:otherwise>
                    <button type="button" class="${myos != ''} mui-btn mui-btn-ios mui-btn-block mui-btn-outlined down-app" data-href="${iosUrl}">
                        <i class="mobile-ico ico-app"></i><span>${views.include_auto['IOS下载']}</span>
                    </button>
                    <button type="button" class="mui-btn mui-btn-android mui-btn-block mui-btn-outlined down-app" data-href="${androidUrl}">
                        <i class="mobile-ico ico-and"></i><span>${views.include_auto['Android下载']}</span>
                    </button>
                </c:otherwise>
            </c:choose>
            <div class="p1"><img src="${resRoot}/themes/lottery/images/p1.png" width="60%"></div>
            <button type="button" class="mui-btn mui-btn-danger mui-btn-block goon" data-href="/mainIndex.html">${views.include_auto['继续访问']}${views.include_auto['连接成功']}</button>
            <div class="tit-tips">${views.include_auto['温馨提示']}</div>
        </div>
    </div>
</div>
</body>

</html>

<script type="text/javascript" charset="utf-8">
    mui.init();
    mui('body').on('tap', 'button', function () {
        var href = $(this).data('href');
        mui.openWindow({
            url: href,
            id: href,
            extras: {},
            createNew: false,
            show: {autoShow: true},
            waiting: {
                autoShow: true,
                title: ''
            }
        })
    })
</script>