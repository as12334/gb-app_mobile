<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<section class="mui-slider banner-slide">
    <div class="banner-ads">
        ${views.themes_auto['更多精彩游戏']}
        <a data-rel='{"target":"downLoadApp","opType":"function"}' class="btn-download">${views.themes_auto['立即下载']}</a>
        <div data-rel='{"target":"closeDownLoad","opType":"function"}' class="close-slide"></div>
    </div>
    <div class="mui-slider-group ${fn:length(carousels)>0?'mui-slider-loop':''}">
        <c:choose>
            <c:when test="${fn:length(carousels) > 0}">
                <c:forEach items="${carousels}" var="carousel" varStatus="vs">
                    <c:set var="link" value="${not empty carousel['link'] ? carousel['link']:''}"/>
                    <c:choose>
                        <c:when test="${not empty link}">
                            <c:set var="link" value="${fn:startsWith(link, 'http://')||fn:startsWith(link, 'https://')?link:'http://'.concat(link)}"/>
                        </c:when>
                    </c:choose>
                    <c:if test="${vs.index==0}">
                        <div class="mui-slider-item mui-slider-item-duplicate">
                            <a data-rel='{"target":"${link}","opType":"href"}'>
                                <img src="${soulFn:getImagePath(domain, carousels.get(carousels.size()-1)["cover"])}"/>
                            </a>
                        </div>
                    </c:if>
                    <div class="mui-slider-item">
                        <a data-rel='{"target":"${link}","opType":"href"}'>
                            <c:if test="${vs.index == 0}">
                                <img src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                            </c:if>
                            <c:if test="${vs.index != 0}">
                                <img data-lazyload="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                            </c:if>
                        </a>
                    </div>
                    <c:if test="${fn:length(carousels)-1 == vs.index}">
                        <div class="mui-slider-item mui-slider-item-duplicate">
                            <a data-rel='{"target":"${link}","opType":"href"}'>
                                <img data-lazyload="${soulFn:getImagePath(domain, carousels.get(0)["cover"])}"/>
                            </a>
                        </div>
                    </c:if>
                </c:forEach>
            </c:when>
            <c:otherwise >
                <div class="mui-slider-item">
                    <a>
                        <img src="${resRoot}/images/banner-01.jpg" />
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>
<section class="notice">
    <button type="button" class="mui-btn mui-btn-primary btn-title">${views.game_auto['公告']}</button>
    <div class="notice-list">
        <div class="marquee">
            <c:forEach items="${announcement}" var="a" varStatus="vs">
                <a data-rel='{"target":"showNotice","opType":"function","idx":"${vs.index}"}'>${a.content}</a>
            </c:forEach>
        </div>
    </div>
</section>
