<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
       <%@include file="../common/Head.jsp"%>
        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--优惠列表-->
                <section class="promo">
                    <div class="promo-sorts">
                        <%--<a type="button" data-value="" class="mui-btn btn-promo-sort active">全部</a>--%>
                        <soul:button activityType=""  callback="" target="activityType" text="" opType="function" cssClass="mui-btn btn-promo-sort active">全部</soul:button>
                    </div>
                    <div  id="pullrefresh">
                        <ul class="promo-list mui-list-unstyled">
                            <%--<li><a href=""><img src="${resRoot}/images/promo/promo01.png"/></a></li>
                            <li><a href=""><img src="${resRoot}/images/promo/promo02.png"/></a></li>
                            <li><a href=""><img src="${resRoot}/images/promo/promo03.png"/></a></li>--%>
                        </ul>
                    </div>

                </section>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@ include file="../common/Footer.jsp" %>
        <!--浮窗广告轮播-->
        <div class="ads-slider">
            <a href="javascript:" class="close-ads"></a>
            <div class="mui-slider">
                <div class="mui-slider-group">
                    <div class="mui-slider-item"><a href="#"><img src="${resRoot}/images/ads-banner-01.png" /></a></div>
                    <div class="mui-slider-item"><a href="#"><img src="${resRoot}/images/ads-banner-01.png" /></a></div>
                </div>
            </div>
        </div>
    </div>
</div>
<input value="1" id="pageNumber" hidden>
<input value="1" id="lastPageNumber" hidden>
<!--语言弹窗-->
<ul class="lang-menu">
    <li class="current zh-CN"><a href="">中文</a></li><!--当前语言版本加current-->
    <li class="en-US"><a href="">English</a></li>
    <li class="ja-JP"><a href="">日文</a></li>
</ul>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/discounts/Promo.js"></script>
</html>
