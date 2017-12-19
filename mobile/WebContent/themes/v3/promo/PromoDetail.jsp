<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="isDemo" value="<%=SessionManagerCommon.isDemoModel() %>" />
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
        <c:set var="activity" value="${command.result}" />
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav">
            <soul:button text="" opType="function" target="goToLastPage" cssClass="mui-icon mui-icon mui-icon-left-nav mui-pull-left"></soul:button>
            <%@include file="../common/Assert.jsp"%>
            <h1 class="mui-title">${activity.activityName}</h1>
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
                                <soul:button target="submit" opType="function" dataCode="${activity.code}"
                                             dataStates="${activity.states}" dataType="processing" dataSearchId="${activity.searchId}"
                                             dataRankId="${rankId}" isDemo="${isDemo}" cssClass="mui-pull-right mui-btn mui-btn-primary submit" text="">
                                    ${btnText}
                                </soul:button>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--浮窗广告轮播-->
        <%@ include file="../index.include/Envelope.jsp" %>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp"%>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script src="${resRoot}/js/common/Common.js"></script>
<script src="${resRoot}/js/common/Head.js"></script>
<script src="${resRoot}/js/envelope/Envelope.js"></script>
<script src="${resRoot}/js/discounts/PromoDetail.js"></script>
</html>
