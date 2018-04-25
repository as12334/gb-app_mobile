<%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css"/>
    <link rel="stylesheet" href="${resRoot}/themes/otherpage.css"/>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@ include file="../common/LeftMenu.jsp" %>
    <c:set var="defaultSaleImg" value="${resRoot}/images/img-sale1.jpg"/>
    <input type="hidden" value="${defaultSaleImg}" name="defaultSaleImg"/>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <%@include file="../discounts/PromoHead.jsp" %>
        <div class="mui-content promo-content">
            <div class="promo">
                <div class="swiper-container p-t-slide-content promo-list">
                    <div class="swiper-wrapper">
                        <div class="swiper-slide">
                            <div class="mui-scroll-wrapper">
                                <div class="mui-scroll">
                                    <c:forEach var="map" items="${messageVo.typeMessageMap}">
                                        <c:forEach var="message" items="${map.value}">
                                            <div class="promo-item">
                                                    <%--<a href="${root}/promo/promoDetail.html?search.id=${message.id}"
                                                           data-rel='{"target":"${root}/promo/promoDetail.html?search.id=${message.id}","opType":"href"}'>--%>
                                                <c:choose>
                                                    <c:when test="${!empty message.activityAffiliated}">
                                                        <c:set var="imgSrc"
                                                               value="${soulFn:getImagePath(domain, message.activityAffiliated)}"/>
                                                    </c:when>
                                                    <c:when test="${!empty message.activityCover}">
                                                        <c:set var="imgSrc"
                                                               value="${soulFn:getImagePath(domain, message.activityCover)}"/>
                                                    </c:when>
                                                </c:choose>
                                                <div class="img-wrap">
                                                    <img src="${empty imgSrc?defaultSaleImg:''}"
                                                         data-lazyload="${imgSrc}"/>
                                                </div>
                                                <div class="pro-txt">
                                                    <div class="mui-pull-left">${message.activityName}</div>
                                                    <a data-rel='{"target":"${root}/promo/promoDetail.html?search.id=${message.id}","opType":"href"}'
                                                       class="mui-pull-right">查看详情</a>
                                                </div>
                                                    <%--</a>--%>
                                            </div>
                                        </c:forEach>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <c:forEach var="type" items="${messageVo.typeList}">
                            <c:set var="mapValue" value="${messageVo.typeMessageMap.get(type.key)}"></c:set>
                            <c:if test="${fn:length(mapValue) > 0}">
                                <div class="swiper-slide" name="${type.key}">
                                    <div class="mui-scroll-wrapper">
                                        <div class="mui-scroll">
                                            <c:forEach items="${mapValue}" var="message">
                                                <div class="promo-item">
                                                    <c:choose>
                                                        <c:when test="${!empty message.activityAffiliated}">
                                                            <c:set var="imgSrc"
                                                                   value="${soulFn:getImagePath(domain, message.activityAffiliated)}"/>
                                                        </c:when>
                                                        <c:when test="${!empty message.activityCover}">
                                                            <c:set var="imgSrc"
                                                                   value="${soulFn:getImagePath(domain, message.activityCover)}"/>
                                                        </c:when>
                                                    </c:choose>
                                                        <%--<a data-rel='{"target":"${root}/promo/promoDetail.html?search.id=${message.id}","opType":"href"}'>--%>
                                                    <div class="img-wrap">
                                                        <img src="${empty imgSrc?defaultSaleImg:''}"
                                                             data-lazyload="${imgSrc}"/>
                                                    </div>
                                                    <div class="pro-txt">
                                                        <div class="mui-pull-left">${message.activityName}</div>
                                                        <a data-rel='{"target":"${root}/promo/promoDetail.html?search.id=${message.id}","opType":"href"}'
                                                           class="mui-pull-right">查看详情</a>
                                                    </div>
                                                        <%--</a>--%>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>

        <!--footer-->
        <%@ include file="../common/Footer.jsp" %>
        <!-- off-canvas backdrop -->
        <%--<div class="mui-off-canvas-backdrop"></div>--%>
    </div>
</div>
<input value="1" id="pageNumber" type="hidden">
<input value="1" id="lastPageNumber" type="hidden">
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.lazyload.js"></script>
<script src="${resRoot}/js/mui/mui.lazyload.img.js"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/discounts/Promo.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>

</html>
<%@ include file="/include/include.footer.jsp" %>