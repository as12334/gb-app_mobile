<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<script>
    var resRoot = '${resRoot}';
</script>
<!DOCTYPE html>
<html>
<head>
    <title>606 | 强制踢出</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<body class="gb-theme">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav gb-header">
                <h1 class="mui-title" style="color: #ffffff;">${views.errors_auto['网站维护中']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper gb-navbar">
                <div class="mui-scroll">
                    <div class="mui-content-padded gb-error">
                        <div class="mui-row m-y-lg">
                            <div class="mui-col-xs-3"></div>
                            <div class="mui-col-xs-6 mui-text-center gb-error-img">
                                <img src="${resRoot}/images/error/ico-606.png">
                            </div>
                            <div class="mui-col-xs-3"></div>
                        </div>
                        <p class="mui-text-center">${views.errors_auto['您已被強制踢出']}</p>
                        <p class="mui-text-center">${views.errors_auto['联系我们']}</p>
                    </div>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
</html>

<script>
    mui('.mui-scroll-wrapper').scroll({indicators: false});
    mui.init({});
</script>
