<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.base.inc.jsp" %>
<%@ include file="/include/include.base.inc.i18n.jsp" %>

<body class="gb-theme">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav gb-header">
            <h1 class="mui-title" style="color: #ffffff;">${views.errors_auto['禁止訪問']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper gb-navbar">
            <div class="mui-scroll">
                <div class="mui-content-padded gb-error">
                    <div class="mui-row m-y-lg">
                        <div class="mui-col-xs-3"></div>
                        <div class="mui-col-xs-6 mui-text-center gb-error-img">
                            <img src="${resRoot}images/error/ico-605.png">
                        </div>
                        <div class="mui-col-xs-3"></div>
                    </div>
                    <p>${views.errors_auto['IP6051']}</p>
                    <p>${views.errors_auto['IP6052']}</p>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>

<script>
    mui('.mui-scroll-wrapper').scroll({indicators: false});
    mui.init({});
</script>

