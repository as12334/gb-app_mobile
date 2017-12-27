<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<section class="mui-slider banner-slide">
    <div class="gb-banner">
        <div class="close-slide">
            <soul:button target="closeBanner" text="" opType="function" cssClass="mui-icon mui-icon-closeempty"/>
        </div>
        <div class="ct">
            <div id="slider" class="mui-slider mui-banner">
                <div class="mui-slider-group mui-slider-loop">
                    <c:choose>
                        <c:when test="${fn:length(carousels) > 0}">
                            <c:forEach items="${carousels}" var="carousel" varStatus="vs">
                                <c:set var="link" value="${not empty carousel['link'] ? carousel['link']:''}"/>
                                <c:choose>
                                    <c:when test="${not empty link}">
                                        <c:set var="link" value="${fn:startsWith(link, 'http://')||fn:startsWith(link, 'https://')?link:'http://'.concat(link)}"/>
                                    </c:when>
                                </c:choose>
                                <c:if test="${fn:length(carousels)-1 == vs.index}">
                                    <div class="mui-slider-item mui-slider-item-duplicate">
                                        <soul:button text="" opType="href" target="${link}">
                                            <img class="c_banner" src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                                        </soul:button>
                                    </div>
                                </c:if>
                                <div class="mui-slider-item">
                                    <soul:button text="" opType="href" target="${link}">
                                        <img class="c_banner" src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                                    </soul:button>
                                </div>
                                <c:if test="${vs.index==0}">
                                    <div class="mui-slider-item mui-slider-item-duplicate">
                                        <soul:button text="" opType="href" target="${link}">
                                            <img class="c_banner" src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                                        </soul:button>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="mui-slider-item">
                                <a><img class="c_banner" src="${resRoot}/images/ban-01.jpg"/></a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="mui-slider-indicator">
                    <c:forEach items="${carousels}" var="carousel" varStatus="status">
                        <div class="mui-indicator ${status.index == 0 ? 'mui-active':''}"></div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
</section>

<!--公告-->
<section class="notice">
    <button type="button" class="mui-btn mui-btn-primary btn-title">${views.game_auto['公告']}</button>
    <div class="notice-list">
        <c:forEach items="${announcement}" var="a" varStatus="vs">
            <p>
                <soul:button cssClass="mui-ellipsis" target="showNotice" opType="function" text="" idx="${vs.index}">${a.content}</soul:button>
            </p>
        </c:forEach>
    </div>
</section>
