<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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