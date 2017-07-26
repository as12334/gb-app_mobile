<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<script>
    var resRoot = '${resRoot}';
</script>
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
            <header class="mui-bar mui-bar-nav gb-header ${os eq 'android'?'mui-hide':''}">
                <h1 class="mui-title" style="color: #ffffff;">${views.errors_auto['页面不存在']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper gb-navbar">
                <div class="mui-scroll">
                    <div class="mui-content-padded gb-error">
                        <div class="mui-row m-y-lg">
                            <div class="mui-col-xs-3"></div>
                            <div class="mui-col-xs-6 mui-text-center gb-error-img">
                                <img src="${resRoot}/images/error/ico-404.png">
                            </div>
                            <div class="mui-col-xs-3"></div>
                        </div>
                        <p class="mui-text-center">${views.errors_auto['你访问的页面不存在']}</p>
                        <p class="mui-text-center">Sorry! the page you are looking for doesn't exist.</p>
                        <div class="mui-text-center"><a data-href="${root}/index.html" class="mui-action-back mui-btn mui-btn-primary p-x-lg">${views.errors_auto['返回首页']}</a></div>
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
    mui("body").on("tap", "[data-href]", function() {
        var _href = $(this).data('href');
        mui.openWindow({
            url: _href,
            id: _href,
            extras: {},
            createNew: false,
            show: {
                autoShow: true
            },
            waiting: {
                autoShow: true,
                title: '正在加载...'
            }
        })
    })
</script>
