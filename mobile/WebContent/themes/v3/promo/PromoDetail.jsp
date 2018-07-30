<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="isDemo" value="<%=SessionManagerCommon.isDemoModel() %>"/>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<%--此处把js相关引用挪到前面是因为优惠详细里图片地址带onload方法里涉及window.top.imgRoot--%>
<%@ include file="../include/include.js.jsp" %>
<!-- 主页面容器 -->
<div class="mui-inner-wrap">
    <c:set var="activity" value="${command}"/>
    <!-- 主页面标题 -->
    <c:if test="${siteId!=18&&siteId<7000}">
        <header class="mui-bar mui-bar-nav">
            <a style="color: #fff;" class="mui-icon mui-icon mui-icon-left-nav mui-pull-left"
               data-rel='{"target":"goToLastPage","opType":"function"}'></a>
            <%--<%@include file="../common/Assert.jsp"%>--%>
            <a class="mui-icon mui-icon mui-pull-right icon-gift"
               data-rel='{"target":"goPromoDetail","opType":"function","src":"${root}/promo/myPromo.html"}'
               data-href="/promo/myPromo.html"><i></i></a>
            <h1 class="mui-title"><%--${activity.activityName}--%> 优惠详情</h1>
        </header>
    </c:if>
    <div class="mui-content mui-scroll-wrapper promo-detail-content content-without-notice">
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
                                                <c:set var="imgSrc"
                                                       value="${soulFn:getImagePath(domain, activity.activityAffiliated)}"/>
                                            </c:when>
                                            <c:when test="${!empty activity.activityCover}">
                                                <c:set var="imgSrc"
                                                       value="${soulFn:getImagePath(domain, activity.activityCover)}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="imgSrc" value="${resRoot}/images/img-sale1.jpg"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <img src="${imgSrc}" style="width: 100%;">
                                    </a>
                                    <div class="ct" style="padding: 0;">
                                        <h5><i></i><span>${activity.activityName}</span></h5>
                                        <div>${activity.activityDescription}</div>
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
                    <c:set var="activityCode" value="${activity.code}"/>
                    <div class="gb-form-foot" style="margin-top: -10px;">
                        <c:choose>
                            <c:when test="${(not empty activity.allRank) && activity.allRank}">
                                <c:set var="rankId" value="all"/>
                            </c:when>
                            <c:when test="${activityCode eq 'back_water'}">
                                <c:set var="rankId" value="backwater"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="rankId" value="${activity.rankid}"/>
                            </c:otherwise>
                        </c:choose>
                        <c:choose>
                            <c:when test="${activityCode eq 'money'}">
                                <c:set var="btnText" value="${views.promo_auto['抢红包']}"/>
                            </c:when>
                            <c:when test="${activityCode eq 'back_water'}">
                                <c:set var="btnText" value="${views.promo_auto['参与中']}"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="btnText" value="${views.promo_auto['立即加入']}"/>
                            </c:otherwise>
                        </c:choose>
                        <div class="gb-form-foot" style="margin-top: -10px;">
                            <c:if test="${activity.code ne 'content'}">
                                <a class="mui-btn mui-btn-primary submit" ${activityCode eq 'back_water'?'disabled':''}
                                   data-rel='{"target":"submitPromo","opType":"function","dataCode":"${activity.code}",
                                "dataStates":"${activity.states}","dataType":"processing","dataSearchId":"${activity.searchId}","dataRankId":"${rankId}"}'>${btnText}</a>
                            </c:if>
                        </div>
                    </div>
                </c:if>
                <c:if test="${activity.states != 'processing'}">
                    <div class="gb-form-foot" style="margin-top: -10px;">
                        <div class="gb-form-foot">
                            <a class="mui-btn mui-btn-primary submit mui-disabled"
                               disabled>${dicts.operation.activity_state[activity.states]}</a>
                        </div>
                    </div>
                </c:if>
                <%--<div class="mui-off-canvas-backdrop"></div>--%>

            </div>
        </div> <!--mui-scroll 闭合标签-->
    </div>  <!--mui-content 闭合标签-->
    <!--浮窗广告轮播-->
    <%@ include file="../index.include/Envelope.jsp" %>
</div>
</body>
<script src="${resRoot}/js/envelope/Envelope.js?v=${rcVersion}"></script>
<%--即使没有头部菜单，也要调用该js 往sessionStorage设置一些相关登录信息--%>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/discounts/PromoDetail.js?v=${rcVersion}"></script>

</html>
