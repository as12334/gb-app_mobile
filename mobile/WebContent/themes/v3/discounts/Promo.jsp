<%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <link rel="bookmark" href="${resRoot}/favicon.ico">
    <link rel="shortcut icon" href="${resRoot}/favicon.ico">
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css" />
    <%--<meta name="apple-mobile-web-app-status-bar-style" content="black">--%>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>

    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">

        <%@include file="../discounts/PromoHead.jsp" %>

        <div class="promo-header">
            <div class="swiper-container p-t-slide-indicators promo-sorts">
                <div class="swiper-wrapper">
                    <a class="swiper-slide mui-btn btn-promo-sort" data-rel='{"target":"activityType","opType":"function","activityType",""}'><span>${views.themes_auto['全部']}</span></a>
                    <c:forEach var="type" items="${messageVo.typeList}" varStatus="vs">
                        <a class="swiper-slide mui-btn btn-promo-sort" data-rel='{"target":"activityType","opType","function","activityType","${type.key}"}'><span>${type.value}</span></a>
                    </c:forEach>

                </div>
            </div>

        </div>


        <div class="mui-content promo-content">
            <div class="promo">
                <div class="swiper-container p-t-slide-content promo-list">
                    <div class="swiper-wrapper">

                        <div class="swiper-slide">
                            <div class="mui-scroll-wrapper">
                                <div class="mui-scroll">
                                    <c:forEach var="map" items="${messageVo.typeMessageMap}">
                                        <c:forEach var="message" items="${map.value}">
                                            <a href="${root}/promo/promoDetail.html?search.id=${message.id}"><img src="${message.activityAffiliated}" /></a>
                                        </c:forEach>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>


                        <c:forEach var="message" items="${typeMessageMap}">
                            <c:if test="${message.key ne 'default'}">
                                <div class="swiper-slide">
                                    <div class="mui-scroll-wrapper">
                                        <div class="mui-scroll">
                                            <c:forEach var="mapValue" items="${message.value}">
                                                <a href="${root}/promo/promoDetail.html?search.id=${mapValue.id}"><img src="${mapValue.activityAffiliated}"></a>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>

                        <div class="swiper-slide">
                            <div class="mui-scroll-wrapper">
                                <div class="mui-scroll">
                                    <c:forEach var="message" items="${typeMessageMap}">
                                        <c:if test="${message.key eq 'default'}">
                                            <c:forEach items="${message.value}" var="mapValue">
                                                <a href="${root}/promo/promoDetail.html?search.id=${mapValue.id}"><img src="${mapValue.activityAffiliated}"></a>
                                            </c:forEach>
                                        </c:if>
                                    </c:forEach>

                                </div>
                            </div>
                        </div>


                    </div>
                </div>
            </div>
        </div>

        <!--footer-->
        <%@ include file="../common/Footer.jsp" %>
        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<input value="1" id="pageNumber" hidden>
<input value="1" id="lastPageNumber" hidden>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/discounts/Promo.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>