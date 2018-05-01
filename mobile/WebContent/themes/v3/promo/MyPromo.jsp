<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<html>

<head>
    <title>${views.promo_auto['我的优惠记录']}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav promo-record mui-bar-blue">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.promo_auto['我的优惠记录']}</h1>
            <soul:button text="" opType="href" target="${root}/discounts/index.html?skip=1"
                         cssClass="mui-icon mui-pull-right icon-gift"/>
        </header>
        <input value="1" id="pageNumber" type="hidden"/>
        <div class="mui-content mui-scroll-wrapper content-without-notice content-without-footer promo-record-content"  <%--${os eq 'android'?'style="padding-top:0!important"':''}--%>>
            <!-- 主界面具体展示内容 -->
            <div class="promo-nav">
                <div id="segmentedControl" class="mui-segmented-control">
                    <a class="mui-control-item mui-active"
                       data-rel='{"target":"promotionCategory","opType":"function","code":""}'>
                        全部优惠
                    </a>
                    <a class="mui-control-item"
                       data-rel='{"target":"promotionCategory","opType":"function","code":"awarded"}'>
                        已派奖
                    </a>
                    <a class="mui-control-item"
                       data-rel='{"target":"promotionCategory","opType":"function","code":"didNotPass"}'>
                        未通过
                    </a>
                    <a class="mui-control-item"
                       data-rel='{"target":"promotionCategory","opType":"function","code":"unaudited"}'>
                        未审核
                    </a>
                </div>
            </div>
            <div class="promo-cont mui-scroll-wrapper" id="refreshContainer">
                <div class="mui-scroll">
                    <div class="promo-record-content" id="content">
                        <%--  <%@include file="./MyPromoPartial.jsp"%>--%>
                    </div>
                </div>
            </div>
        </div>
        <%--<div class="mui-off-canvas-backdrop"></div>--%>
    </div>
</div>
</body>

<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/discounts/MyPromo.js?v=${rcVersion}"></script>
</html>
