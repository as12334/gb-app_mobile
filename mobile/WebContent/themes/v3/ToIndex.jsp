<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<!DOCTYPE html>
<html manifest="${resRoot}/appcache/v3.appcache">
<head>
    <%@include file="include/include.head.jsp" %>
    <title>${siteName}</title>
</head>
<body class="gb-theme">
    <div class="index-canvas mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <%-- 是否未开站 --%>
            <c:set var="isOpen" value="${siteId == 176 ? true : false}" />
            <input type="hidden" value="${isOpen}" name="isOpen"/>
            <!--加载层-->
            <div class="load-bg" style="display: none;">
                <div class="load-bar">
                    <p>${views.app_auto['请耐心等待']}</p>
                    <div id="load-bar-1" class="mui-progressbar mui-progressbar-infinite"><span></span></div>
                </div>
                <div class="load-bar-middle">
                    <span id="welcome" class="btn-back">${views.app_auto['访问主页']}</span>
                </div>
                <div class="load-bar-bottom">
                    <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" width="110"/>
                    <div class="copy-right">
                        Copyright © &nbsp;${siteName}&nbsp; Reserved.
                    </div>
                    <div class="tech-support">
                        ${views.app_auto['技术支持']}：<span class="icon-tech-support"></span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
<%@include file="include/include.js.jsp"%>
<script src="${resRoot}/js/ToIndex.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>