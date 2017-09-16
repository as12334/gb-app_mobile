<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>
<body class="gb-theme mine-page mui-android mui-android-6 mui-android-6-0">

<style>
    .mui-control-content {
        background-color: white;
        min-height: 215px;
    }

    .mui-control-content .mui-loading {
        margin-top: 50px;
    }
</style>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">优惠活动</h1>
</header>
<div class="mui-content">
    <div id="slider" class="mui-slider mui-fullscreen lottery-promo-wrap">
        <div class="p-selector-button">
            <a class="mui-control-item no-bg" href="#activityType">
                <span id="displayType1">全部</span>
                <i class="mui-icon mui-icon-arrowdown"></i>
            </a>
        </div>
        <div id="segmentedControl-promo" class="mui-slider-indicator mui-segmented-control mui-segmented-control-inverted promo-menu-btn">
            <a class="mui-control-item mui-active" href="#promo1">进行中的活动</a>
            <a class="mui-control-item" href="#promo2">活动历史</a>
        </div>
        <div class="mui-slider-group">
            <div id="promo1" class="mui-slider-item mui-control-content mui-active">
                <div id="scroll1" class="mui-scroll-wrapper">
                    <div class="mui-scroll">
                        <ul class="mui-table-view" id="promo1Ul">
                            <%@include file="PromoRecordsPartial.jsp"%>
                        </ul>
                    </div>
                </div>
            </div>
            <div id="promo2" class="mui-slider-item mui-control-content">
                <div id="scroll2" class="mui-scroll-wrapper">
                    <div class="mui-scroll">
                        <ul class="mui-table-view" id="promo2Ul">
                        </ul>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
<div id="activityType" class="mui-popover lottery-promo-sz">
    <div class="mui-scroll-wrapper popover-scroll lottery-promo-sz-l">
        <div class="mui-scroll">
            <ul class="mui-table-view">
                <li class="mui-table-view-cell" id="activityTypeLi">
                    <a href="#" value="all" class="">全部</a>
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