<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div id="slider" class="mui-slider mui-banner">
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

<div class="gb-notice gb-anno">
    <a type="button" class="mui-pull-left notice-icon"><i class="iconfont icon-gonggao"></i></a>
    <ul class="mui-list-unstyled gb-anno-list">
        <li>
            <marquee behavior="scroll" scrollamount="5" direction="left">
                <p>
                    <c:forEach items="${announcement}" var="a" varStatus="vs">
                        <a data-idx="${vs.index}">${a.content}</a>
                    </c:forEach>
                </p>
            </marquee>
        </li>
    </ul>
</div>

<div class="desk">
    <div class="mui-col-xs-2 logo"><img src="${root}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" /></div>
    <div class="mui-col-xs-8 tip">
        <span class="desk-text">${views.game_auto['点击下方']}<em></em>${views.game_auto['添加到主屏幕']}</span>
    </div>
    <div class="mui-col-xs-1 close"><i></i></div>
</div>

<div class="masker masker-notice-box"></div>
<div class="gb-notice-box">
    <div class="hd"><p>${views.game_auto['公告']}</p></div>
    <div class="ct">
        <div class="" id="box-notice">
            <div class="mui-slider-group" style="margin-bottom: 10px">
                <c:choose>
                    <c:when test="${not empty announcement && announcement.size() > 0}">
                        <c:forEach items="${announcement}" var="a" varStatus="vs">
                            <div class="mui-slider-item">
                                <p>${a.content}</p>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="mui-slider-item">
                            <a><img src="${resRoot}/images/ban-01.jpg"/></a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${not empty announcement && announcement.size() > 1}">
                <div class="mui-slider-indicator">
                    <c:forEach items="${announcement}" var="carousel" begin="0" end="${announcement.size()}" varStatus="vs">
                        <div class="mui-indicator gray"></div>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </div>
</div>
