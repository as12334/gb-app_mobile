<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <c:set var="activity" value="${command.result}" />
    <title>${activity.activityName}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/hb/style.css?v=${rcVersion}" />
    <%@ include file="/include/include.js.jsp" %>
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



            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
<soul:import res="site/promo/PromoDetail"/>
</html>
