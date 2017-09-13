<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme mine-page mui-fullscreen">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable" data-offcanvas="2">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">优惠</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <div id="_promo" class="mui-control-content mui-nav mui-active">
                    <div class="mui-row default-background promo-caidan">
                        <div class="hd mui-pull-left">
                            <div class="gb-select">
                                <a href="#activityType">
                                    <span id="displayType1">全部</span>
                                    <i class="mui-icon mui-icon-arrowdown"></i>
                                </a>
                            </div>
                        </div>
                        <div class="gb-headtabs mui-pull-right" style="">
                            <div id="segmentedControl-promo" class="mui-segmented-control c-gray">
                                <a class="mui-control-item gray mui-active" href="promo1">进行中活动</a>
                                <a class="mui-control-item gray" href="promo2">活动历史</a>
                            </div>
                        </div>
                    </div>
                    <div class="mui-row  background-none gb-promo">
                        <div class="gb-panel gray">
                            <div class="gb-select" style="width: 100%;">
                                <div class="promo1" id="promo1">
                                    <div class="ct">
                                        <ul id="content1" class="mui-promo">
                                            <%@include file="./PromoRecordsPartial.jsp" %>
                                        </ul>
                                    </div>
                                </div>
                                <div class="promo2 mui-hide" id="promo2">
                                    <div class="ct finished-gray">
                                        <ul id="content2" class="mui-promo">
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="activityType" class="mui-popover lottery-promo-sz">
    <div class="mui-scroll-wrapper popover-scroll lottery-promo-sz-l">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <li class="mui-table-view-cell">
                    <a href="#" value="all">全部</a>
                </li>
            </ul>
        </div>
    </div>
</div>
<script src="${resRoot}/js/mui/mui.pullToRefresh.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.material.js?v=${rcVersion}"></script>
<script>
    curl(['${resRoot}/lottery/js/promo/PromoRecords'], function (Page) {
        page = new Page();
    });
</script>
</body>