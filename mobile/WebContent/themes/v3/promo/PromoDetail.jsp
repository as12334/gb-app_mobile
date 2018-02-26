<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="isDemo" value="<%=SessionManagerCommon.isDemoModel() %>" />
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
    <link rel="stylesheet" href="${resRoot}/themes/layer.css"/>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:set var="activity" value="${command.result}" />
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav">
            <a class="mui-icon mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
            <%@include file="../common/Assert.jsp"%>
            <h1 class="mui-title"><%--${activity.activityName}--%> 优惠详情</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper promo-detail-content content-without-notice content-without-footer">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="mui-row">
                    <div class="gb-select">
                        <div class="gb-imgs">
                            <div class="ct">
                                <ul>
                                    <li>
                                        <a href="">
                                            <c:choose>
                                                <c:when test="${!empty activity.activityAffiliated}">
                                                    <c:set var="imgSrc" value="${soulFn:getImagePath(domain, activity.activityAffiliated)}"/>
                                                </c:when>
                                                <c:when test="${!empty activity.activityCover}">
                                                    <c:set var="imgSrc" value="${soulFn:getImagePath(domain, activity.activityCover)}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="imgSrc" value="${resRoot}/images/img-sale1.jpg 3"/>
                                                </c:otherwise>
                                            </c:choose>
                                            <img src="${imgSrc}" style="width: 100%;">
                                        </a>
                                        <div class="ct" style="padding: 0;">
                                            <h5><i></i><span>${activity.activityName}</span></h5>
                                            <p>${activity.activityDescription}</p>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <c:if test="${activity.states eq 'processing'}">
                        <span class="_vr_promo_ostart" value="${activity.startTime}" type="hidden"></span>
                        <span class="_vr_promo_oend" value="${activity.endTime}" type="hidden"></span>
                        <span class="_now_time" value="${nowTime}" type="hidden"></span>
                        <div class="gb-form-foot">
                            <c:choose>
                                <c:when test="${(not empty activity.isAllRank) && activity.isAllRank}">
                                    <c:set var="rankId" value="all" />
                                </c:when>
                                <c:when test="${activity.code eq 'back_water'}">
                                    <c:set var="rankId" value="backwater" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="rankId" value="${activity.rankid}" />
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${activity.code eq 'money'}">
                                    <c:set var="btnText" value="${views.promo_auto['抢红包']}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="btnText" value="${views.promo_auto['立即加入']}" />
                                </c:otherwise>
                            </c:choose>
                            <div class="gb-form-foot" style="margin-top: -10px;">
                                <button class="mui-pull-right mui-btn mui-btn-primary submit" data-rel='{"target":"joinPromo","opType":"function","dataCode":"${activity.code}",
                                "dataStates":"${activity.states}","dataType":"processing","dataSearchId":"${activity.searchId}","dataRankId":"${rankId}","isDemo":"${isDemo}"}' value="${btnText}"></button>
                            </div>
                        </div>
                    </c:if>

                    <%@ include file="./redEnvelope/Envelope.jsp" %>
                    <%--<div class="mui-off-canvas-backdrop"></div>--%>

                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--浮窗广告轮播-->
        <%@ include file="../index.include/Envelope.jsp" %>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp"%>
<script src="${resComRoot}/js/mobile/layer.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/envelope/Envelope.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/discounts/PromoDetail.js?v=${rcVersion}"></script>
</html>
