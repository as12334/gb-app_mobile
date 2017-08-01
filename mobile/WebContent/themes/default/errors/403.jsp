<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>404 | NOT FIND</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<body class="gb-theme">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav gb-header">
                <h1 class="mui-title" style="color: #ffffff;">${views.errors_auto['访问受限']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper gb-navbar">
                <div class="mui-scroll">
                    <div class="mui-content-padded gb-error">
                        <div class="mui-row m-y-lg">
                            <div class="mui-col-xs-3"></div>
                            <div class="mui-col-xs-6 mui-text-center gb-error-img">
                                <img src="${resRoot}/images/error/ico-605.png">
                            </div>
                            <div class="mui-col-xs-3"></div>
                        </div>
                        <p class="mui-text-center">${views.errors_auto['您没有权限访问该页面']}</p>
                        <p class="mui-text-center">Sorry! You do not have permission to access the page.</p>
                        <div class="mui-text-center"><a class="mui-action-back mui-btn mui-btn-primary p-x-lg">${views.errors_auto['返回上一页']}</a></div>
                    </div>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
</html>

<script>
    mui.init({});
    mui('.mui-scroll-wrapper').scroll({indicators: false});
</script>
