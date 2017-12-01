<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="isDemo" value="<%=SessionManagerCommon.isDemoModel() %>" />
<%--<html>

<head>
    <c:set var="activity" value="${command.result}" />
    <title>${activity.activityName}</title>
    &lt;%&ndash;<%@ include file="/themes/default/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/hb/style.css?v=${rcVersion}" />
    <%@ include file="/include/include.js.jsp" %>&ndash;%&gt;
    <%@ include file="../include/include.head.jsp"%>
</head>

<body class="gb-theme mine-page no-backdrop">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${activity.activityName}</h1>
                <a class="mui-icon mui-icon mui-pull-right icon-gift" data-href="/promo/myPromo.html"><i></i></a>
            </header>
            <div class="activity-content-bg mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0!important;padding-bottom:60px"':'style="padding-bottom:60px"'}>
                <div class="mui-scroll">
                    <div class="mui-row" style="padding: 10px;">
                        <img src="${soulFn:getImagePathWithDefault(domain, activity.activityAffiliated, resRoot.concat('/images/img-sale1.jpg'))}" style="width: 100%;">
                        <div class="gb-select" style="width: 100%">
                            <div class="gb-imgs">
                                <div class="ct" style="padding: 0;">
                                    <p>${activity.activityDescription}</p>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <nav class="mui-bar mui-bar-tab promo-tab">
                <c:if test="${activity.states eq 'processing'}">
                    <span class="_vr_promo_ostart" value="${activity.startTime}" hidden></span>
                    <span class="_vr_promo_oend" value="${activity.endTime}" hidden></span>
                    <span class="_now_time" value="${nowTime}" hidden></span>
                    <div class="gb-form-foot" style="margin-top: -10px;">
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
                        <button class="mui-pull-right mui-btn mui-btn-primary submit" data-code="${activity.code}" data-states="${activity.states}"
                                data-type="processing" data-searchid="${activity.searchId}" data-rankid="${rankId}">${btnText}</button>
                    </div>
                </c:if>
            </nav>

            <%@ include file="../index.include/Envelope.jsp" %>

            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
<%@ include file="../include/include.js.jsp"%>
</html>--%>

<!DOCTYPE html>
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
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
            <c:set var="activity" value="${command.result}" />
            <h1 class="mui-title">${activity.activityName}</h1>
            <%--<a class="mui-icon mui-icon mui-pull-right icon-gift" data-href="/promo/myPromo.html"><i></i></a>--%>
            <soul:button target="${root}/promo/myPromo.html" text="" opType="href" cssClass="mui-icon mui-icon mui-pull-right icon-gift"><i></i></soul:button>
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
                                            <img src="${soulFn:getImagePathWithDefault(domain, activity.activityAffiliated, resRoot.concat('/images/img-sale1.jpg'))}">
                                        </a>
                                        <div class="ct">
                                            <p>${activity.activityDescription}</p>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>


                    <%--<div class="gb-form-foot" style="margin-top: -10px;">
                        <a href="" class="mui-btn mui-btn-primary submit">申请优惠</a>
                    </div>--%>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <nav class="mui-bar mui-bar-tab promo-tab">
            <c:if test="${activity.states eq 'processing'}">
                <span class="_vr_promo_ostart" value="${activity.startTime}" hidden></span>
                <span class="_vr_promo_oend" value="${activity.endTime}" hidden></span>
                <span class="_now_time" value="${nowTime}" hidden></span>
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
                    <%--<a class="mui-pull-right mui-btn mui-btn-primary submit" data-code="${activity.code}" data-states="${activity.states}"
                       data-type="processing" data-searchid="${activity.searchId}" data-rankid="${rankId}">${btnText}</a>--%>
                    <soul:button target="submit" opType="function" dataCode="${activity.code}"
                                 dataStates="${activity.states}" dataType="processing" dataSearchId="${activity.searchId}"
                                 dataRankId="${rankId}" isDemo="${isDemo}" cssClass="mui-pull-right mui-btn mui-btn-primary submit" text="">
                        ${btnText}
                    </soul:button>
                </div>
            </c:if>
        </nav>
        <%--<div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${activity.activityName}</h1>
                <a class="mui-icon mui-icon mui-pull-right icon-gift" data-href="/promo/myPromo.html"><i></i></a>
            </header>
            <div class="activity-content-bg mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0!important;padding-bottom:60px"':'style="padding-bottom:60px"'}>
                <div class="mui-scroll">
                    <div class="mui-row" style="padding: 10px;">
                        <img src="${soulFn:getImagePathWithDefault(domain, activity.activityAffiliated, resRoot.concat('/images/img-sale1.jpg'))}" style="width: 100%;">
                        <div class="gb-select" style="width: 100%">
                            <div class="gb-imgs">
                                <div class="ct" style="padding: 0;">
                                    <p>${activity.activityDescription}</p>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>
            </div>

            <nav class="mui-bar mui-bar-tab promo-tab">
                <c:if test="${activity.states eq 'processing'}">
                    <span class="_vr_promo_ostart" value="${activity.startTime}" hidden></span>
                    <span class="_vr_promo_oend" value="${activity.endTime}" hidden></span>
                    <span class="_now_time" value="${nowTime}" hidden></span>
                    <div class="gb-form-foot" style="margin-top: -10px;">
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
                        <button class="mui-pull-right mui-btn mui-btn-primary submit" data-code="${activity.code}" data-states="${activity.states}"
                                data-type="processing" data-searchid="${activity.searchId}" data-rankid="${rankId}">${btnText}</button>
                    </div>
                </c:if>
            </nav>

            <%@ include file="../index.include/Envelope.jsp" %>

            <div class="mui-off-canvas-backdrop"></div>
        </div>--%>
        <!--浮窗广告轮播-->
        <%@ include file="../index.include/Envelope.jsp" %>
        <%--<div class="ads-slider">
            <a href="javascript:" class="close-ads"></a>
            <div class="mui-slider">
                <div class="mui-slider-group">
                    <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/ads-banner-01.png" /></a></div>
                    <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/ads-banner-01.png" /></a></div>
                </div>
            </div>
        </div>--%>
    </div>
</div>

<%@ include file="../include/include.js.jsp"%>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script src="${resRoot}/js/envelope/Envelope.js"></script>
<script src="${resRoot}/js/discounts/PromoDetail.js"></script>
</html>