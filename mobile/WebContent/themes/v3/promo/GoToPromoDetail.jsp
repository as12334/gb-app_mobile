<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="isLogin" value="<%=SessionManagerCommon.getUser() != null ? true :false %>"/>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<%--此处把js相关引用挪到前面是因为优惠详细里图片地址带onload方法里涉及window.top.imgRoot--%>
<%@ include file="../include/include.js.jsp" %>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:set var="activity" value="${command}"/>
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav mui-bar-blue">
            <a class="mui-icon mui-icon mui-icon-left-nav mui-pull-left"
               data-rel='{"target":"goToLastPage","opType":"function"}'></a>
            <h1 class="mui-title"><%--${activity.activityName}--%> 优惠详情</h1>
            <a class="mui-icon mui-pull-right icon-gift"
               data-rel='{"target":"goPromoDetail","opType":"function","src":"${root}/promo/myPromo.html"}'><i></i></a>
        </header>
        <div class="mui-content mui-scroll-wrapper promo-detail-content content-without-notice">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="mui-row">
                    <div class="gb-select">
                        <div class="gb-imgs">
                            <div class="ct">
                                <ul>
                                    <li>
                                        <a>
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
                                            <img src="${imgSrc}">
                                        </a>
                                        <div class="ct">
                                            <h5><i></i><span>${activity.activityName}</span></h5>
                                            <div>${activity.activityDescription}</div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div> <!--mui-scroll 闭合标签-->

            <c:if test="${activity.states eq 'processing'}">
                <span class="_vr_promo_ostart" value="${activity.startTime}" type="hidden"></span>
                <span class="_vr_promo_oend" value="${activity.endTime}" type="hidden"></span>
                <span class="_now_time" value="${nowTime}" type="hidden"></span>
                <c:set var="activityCode" value="${activity.code}"/>
                <div class="gb-form-foot new_foo">
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
                        <c:when test="${activityCode eq 'profit_loss' || activityCode eq 'effective_transaction'}">
                            <c:set var="btnText" value="${views.promo_auto['报名参与']}"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="btnText" value="${views.promo_auto['立即加入']}"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${activity.code ne 'content'}">
                        <c:choose>
                            <c:when test="${isLogin}">
                                <a class="mui-btn mui-btn-primary btn-deposit"
                                   data-rel='{"target":"${root}/wallet/deposit/index.html","opType":"href"}'>${views.promo_auto['前往存款']}</a>
                                <a class="mui-btn mui-btn-primary btn-apply submit mui-disabled notfit mui-hidden">${views.promo_auto['未满足条件']}</a>
                                <a class="mui-btn mui-btn-primary btn-apply submit fit" ${activityCode eq 'back_water'?'disabled':''}
                                   data-rel='{"target":"submitPromo","opType":"function","dataCode":"${activity.code}",
                                    "dataStates":"${activity.states}","dataType":"processing",
                                    "dataSearchId":"${activity.searchId}","dataRankId":"${rankId}",
                                    "activityName":"${activity.activityName}"}'>${btnText}</a>
                            </c:when>
                            <c:otherwise>
                                <a class="mui-btn mui-btn-primary btn-deposit"
                                   data-rel='{"target":"goRegister","opType":"function","src":"${root}/signUp/index.html"}'>${views.promo_auto['注册新会员']}</a>
                                <a class="mui-btn mui-btn-primary btn-apply"
                                   data-rel='{"target":"login","opType":"function"}'>${views.promo_auto['登录账户']}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </div>
            </c:if>
        </div>  <!--mui-content 闭合标签-->
        <!--浮窗广告轮播-->
        <%@ include file="../index.include/Envelope.jsp" %>
    </div>
</div>
</body>

<script src="${resRoot}/js/envelope/Envelope.js?v=${rcVersion}"></script>
<%--即使没有头部菜单，也要调用该js 往sessionStorage设置一些相关登录信息--%>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/discounts/GoToPromoDetail.js?v=${rcVersion}"></script>
</html>
