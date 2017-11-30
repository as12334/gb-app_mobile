<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<section class="mui-slider banner-slide">
    <div class="gb-banner">
        <div class="close-slide"><a href="" class="mui-icon mui-icon-closeempty"></a></div>
        <div class="ct">
            <div id="slider" class="mui-slider mui-banner">
                <div class="mui-slider-group">
                    <c:choose>
                        <c:when test="${not empty carousels && carousels.size() > 0}">
                            <c:if test="${not empty carousels && carousels.size() > 1}">
                                <div class="mui-slider-item mui-slider-item-duplicate">
                                    <a>
                                        <img class="c_banner"
                                             src="${soulFn:getImagePath(domain,carousels.get(carousels.size()-1)["cover"])}"/>
                                    </a>
                                </div>
                            </c:if>
                            <c:forEach items="${carousels}" var="carousel" varStatus="vs">
                                <div class="mui-slider-item">
                                    <a href="#" ${not empty carousel["link"] ? '_url=\"'.concat(carousel["link"]).concat('\"') : ''}>
                                        <img class="c_banner" src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                                    </a>
                                </div>
                            </c:forEach>
                            <c:if test="${not empty carousels && carousels.size() > 1}">
                                <div class="mui-slider-item mui-slider-item-duplicate">
                                    <a>
                                        <img class="c_banner"
                                             src="${soulFn:getImagePath(domain, carousels.get(0)["cover"])}"/>
                                    </a>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <div class="mui-slider-item">
                                <a><img class="c_banner" src="${resRoot}/images/ban-01.jpg"/></a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${not empty carousels && carousels.size() > 1}">
                    <div class="mui-slider-indicator">
                        <c:forEach items="${carousels}" var="carousel" begin="0" end="${carousels.size()}"
                                   varStatus="status">
                            <div class="mui-indicator ${status.index == 0 ? 'mui-active':''}"></div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</section>

<!--公告-->
<section class="notice">
    <button type="button" class="mui-btn mui-btn-primary btn-title">${views.game_auto['公告']}</button>
    <div class="notice-list">
        <marquee behavior="scroll" scrollamount="0" direction="left">
            <p>
                <c:forEach items="${announcement}" var="a" varStatus="vs">
                    <soul:button target="showNotice" opType="function" text="" data-idx="${vs.index}">${a.content}</soul:button>
                </c:forEach>
            </p>
        </marquee>
    </div>
</section>
