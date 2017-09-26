<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${views.promo_auto['我的优惠记录']}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme mine-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${views.promo_auto['我的优惠记录']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0"':''}>
                <div class="mui-scroll">
                    <div class="mui-row" id="content">

                    </div>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>

<soul:import res="site/promo/MyPromo"/>
</html>
