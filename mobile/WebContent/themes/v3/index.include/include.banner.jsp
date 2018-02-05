<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<section class="mui-slider banner-slide">
    <div class="banner-ads">更多精彩游戏，请下载客户端！<a href="#" class="btn-download">立即下载</a>
        <div class="close-slide"></div>
    </div>
    <div class="mui-slider-group">
        <c:choose>
            <c:when test="${fn:length(carousels) > 0}">
                <c:forEach items="${carousels}" var="carousel" varStatus="vs">
                    <c:set var="link" value="${not empty carousel['link'] ? carousel['link']:''}"/>
                    <c:choose>
                        <c:when test="${not empty link}">
                            <c:set var="link"
                                   value="${fn:startsWith(link, 'http://')||fn:startsWith(link, 'https://')?link:'http://'.concat(link)}"/>
                        </c:when>
                    </c:choose>
                    <c:if test="${fn:length(carousels)-1 == vs.index}">
                        <div class="mui-slider-item mui-slider-item-duplicate">
                            <a data-rel='{"target":"${link}","opType":"href"}'>
                                <img src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                            </a>
                        </div>
                    </c:if>
                    <div class="mui-slider-item">
                        <a data-rel='{"target":"${link}","opType":"href"}'>
                            <img src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                        </a>
                    </div>
                    <c:if test="${vs.index==0}">
                        <div class="mui-slider-item mui-slider-item-duplicate">
                            <a data-rel='{"target":"${link}","opType":"href"}'>
                                <img src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                            </a>
                        </div>
                    </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="mui-slider-item">
                    <a><img src="${resRoot}/../mobile-v3/images/banner-01.jpg" /></a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <!--轮播图加载时的样式-->
    <%--<div class="banner-loading">
        <div class="loader api-loader">
            <div class="loader-inner ball-pulse api-div">
                <div></div>
                <div></div>
                <div></div>
            </div>
        </div>
    </div>--%>
</section>
