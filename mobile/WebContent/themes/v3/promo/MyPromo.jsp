<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<html>

<head>
    <title>${views.promo_auto['我的优惠记录']}</title>
    <%@ include file="../include/include.head.jsp"%>
</head>

<body class="gb-theme mine-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav">
                <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">${views.promo_auto['我的优惠记录']}</h1>
                <soul:button text="" opType="href" target="${root}/discounts/index.html?skip=1" cssClass="mui-icon mui-pull-right icon-gift"/>
            </header>
            <input value="1" id="pageNumber" type="hidden"/>
            <div class="mui-content mui-scroll-wrapper content-without-notice content-without-footer" id="refreshContainer" ${os eq 'android'?'style="padding-top:0!important"':''}>
                <div class="mui-scroll">
                    <div class="promo-record-content" id="content">
                       <%--  <%@include file="./MyPromoPartial.jsp"%>--%>
                    </div>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>

<%@ include file="../include/include.js.jsp"%>
<script src="${resRoot}/js/discounts/MyPromo.js"></script>
</html>
