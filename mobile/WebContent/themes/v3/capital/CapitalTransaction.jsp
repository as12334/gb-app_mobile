<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../include/include.head.jsp" %>
    <link href="${resRoot}/themes/mui.picker.css?v=${rcVersion}" type="text/css" rel="stylesheet" />
    <link href="${resRoot}/themes/swiper.min.css?v=${rcVersion}" type="text/css" rel="stylesheet" />
    <title>${views.deposit_auto['存款']}</title>
</head>
<body class="deposit_2">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">存款</h1>
    <%@ include file="../common/Assert.jsp" %>
    <!--导航tab-->
    <div class="deposit-2-header">
        <div class="swiper-container  deposit-sorts">
            <div class="swiper-wrapper">
                <a class="swiper-slide mui-btn btn-promo-sort"><span>存款</span></a>
                <a class="swiper-slide mui-btn btn-promo-sort"><span>资金</span></a>
                <a class="swiper-slide mui-btn btn-promo-sort"><span>提现</span></a>
            </div>
        </div>
    </div>
</header>
<div class="mui-content mui-scroll-wrapper deposit-2-content">
    <!--主界面tab内容展示-->
    <div class="tab_content">
        <div class="swiper-container slide-content promo-list">
            <div class="swiper-wrapper">
                <!--存款-->
                <div class="swiper-slide">
                    <div class="mui-scroll-wrapper">
                        <%@ include file="../deposit/index/Deposit.jsp" %>
                    </div>
                </div>
                <!--资金-->
                <div class="swiper-slide">
                    <%@ include file="../transfer/Index.jsp" %>
                </div>
                <!--提现-->
                <div class="swiper-slide">
                    <%@ include file="../withdraw/Index.jsp" %>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/capital/CapitalTransaction.js?v=${rcVersion}"></script>
