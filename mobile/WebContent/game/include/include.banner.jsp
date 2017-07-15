<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row">
    <div class="gb-banner"><a href="" class="mui-icon mui-icon-closeempty"></a>
        <div class="ct">
            <div id="slider" class="mui-slider mui-banner" >
                <div class="mui-slider-group ${not empty carousels && carousels.size() > 1 ? 'mui-slider-loop':'' }">
                    <input type="hidden" id="bannerSize" value="${carousels.size()}" />
                    <c:choose>
                        <c:when test="${not empty carousels && carousels.size() > 0}">
                            <c:if test="${not empty carousels && carousels.size() > 1}">
                                <div class="mui-slider-item mui-slider-item-duplicate">
                                    <a>
                                        <img class="c_banner" src="${soulFn:getImagePath(domain,carousels.get(carousels.size()-1)["cover"])}"/>
                                    </a>
                                </div>
                            </c:if>
                            <c:forEach items="${carousels}" var="carousel" varStatus="vs">
                                <div class="mui-slider-item">
                                    <a id="_banner_${vs.index}" ${not empty carousel["link"] ? '_url=\"'.concat(carousel["link"]).concat('\"') : ''}>
                                        <img class="c_banner" src="${soulFn:getImagePath(domain, carousel["cover"])}"/>
                                    </a>
                                </div>
                            </c:forEach>
                            <c:if test="${not empty carousels && carousels.size() > 1}">
                                <div class="mui-slider-item mui-slider-item-duplicate">
                                    <a>
                                        <img class="c_banner" src="${soulFn:getImagePath(domain, carousels.get(0)["cover"])}"/>
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
                        <c:forEach items="${carousels}" var="carousel" begin="0" end="${carousels.size()}" varStatus="status">
                            <div class="mui-indicator ${status.index == 0 ? 'mui-active':''}"></div>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<div class="mui-row">
    <div class="gb-notice">
        <button type="button" class="mui-pull-left mui-btn mui-btn-danger mui-btn-outlined">${views.game_auto['公告']}</button>
        <ul class="mui-list-unstyled gb-notice-list">
            <li>
                <marquee behavior="scroll" scrollamount="2" direction="left">
                    <p><c:forEach items="${announcement}" var="a" varStatus="vs">
                        <a data-idx="${vs.index}">${a.content}</a></c:forEach>
                    </p>
                </marquee>
            </li>
        </ul>
        <%--<div class="mui-list-unstyled gb-notice-list">--%>
            <%--<div class="m-outer">--%>
                <%--<div id="marquee-box" class="m-box">--%>
                    <%--<c:forEach items="${announcement}" var="a" varStatus="vs">--%>
                        <%--<a data-idx="${vs.index}">${a.content}</a>--%>
                    <%--</c:forEach>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    </div>
</div>