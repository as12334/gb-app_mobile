<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<%--popover滚动需在页面自定义样式--%>
<style>
    /*跨webview需要手动指定位置*/
    #popover #popover2 {position: fixed;top: 16px;right: 6px;}
    #popover #popover2 .mui-popover-arrow {left: auto;right: 6px;}
    .mui-popover {height: 40%;}
</style>
<div class="mui-row default-background" >
    <div class="hd mui-pull-left" style="padding: 10px;">
        <div class="gb-select">
            <a href="#activityType"><span id="displayType1">${views.promo_auto['活动类别']}</span>
                <i class="mui-icon mui-icon-arrowdown"></i>
            </a>
        </div>
    </div>
    <div class="gb-headtabs mui-pull-right" style="margin-right: 10px">
        <div id="segmentedControl-promo" class="mui-segmented-control c-gray">
            <a class="mui-control-item gray mui-active" slideNumber="0" href="promo1"><i></i>${views.promo_auto['进行中活动']}</a>
            <a class="mui-control-item gray" slideNumber="1" href="promo2"><i></i>${views.promo_auto['活动历史']}</a>
        </div>
    </div>
</div>
<input type="hidden" class="_now_time" value="${nowTime}">
<div class="mui-row  background-none gb-promo">
    <div class="gb-panel gray">
        <div class="gb-select" style="width: 100%;">
            <%--第一个内容区容器--%>
            <div class="promo1" id="promo1">
                <div class="ct">
                    <ul id="content1" class="mui-promo">
                        <%@include file="PromoRecordsPartial.jsp" %>
                    </ul>
                </div>
            </div>
            <%--第二个内容区--%>
            <div class="promo2 mui-hide" id="promo2">
                <div class="ct finished-gray">
                    <ul id="content2" class="mui-promo">
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<div class="mui-off-canvas-backdrop" style="z-index:1"></div>
<script>
    curl(['site/promo/PromoRecords'], function (Promo) {
        window.top.game.promo = new Promo();
    });
</script>

