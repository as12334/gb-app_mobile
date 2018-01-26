<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<input type="hidden" id="api-type-id" value="${apiTypeId}" />
<div class="mui-row">
    <div class="a gb-homemenu mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
        <span class="shadow-left"></span>
        <span class="shadow-right"></span>
        <div class="mui-scroll" id="mui-nav">
            <c:forEach var="at" items="${apiTypes}" varStatus="vs">
                <a class="mui-control-item ${at.apiTypeId == tid ? 'mui-active' : ''} ico${at.apiTypeId} jp-btn-distance"
                   data-id="${at.apiTypeId}" href="#container${at.apiTypeId}" data-of="${at.apiTypeId == tid ? 'off' : ''}">
                    <c:choose>
                        <c:when test="${at.apiTypeId == 1}">${views.game_auto['真人']}</c:when>
                        <c:when test="${at.apiTypeId == 2}">${views.game_auto['电子']}</c:when>
                        <c:when test="${at.apiTypeId == 3}">${views.game_auto['体育']}</c:when>
                        <c:when test="${at.apiTypeId == 4}">${views.game_auto['彩票']}</c:when>
                        <c:when test="${at.apiTypeId == 5}">棋牌</c:when>
                    </c:choose>
                </a>
            </c:forEach>
            <a class="mui-control-item ico-promo" data-id="-1" href="#_promo" data-of="">${views.game_auto['优惠']}</a>
            <a class="mui-control-item ico-agent" data-id="-2" href="#_agent">${views.game_auto['代理']}</a>
            <a class="mui-control-item ico-about jp-btn-distance-90" data-id="-3" href="#_about">${views.game_auto['关于']}</a>
            <a class="mui-control-item ico-terms" data-id="-4" href="#_terms">${views.game_auto['条款']}</a>
            <c:if test="${os ne 'android' && os ne 'app_ios'}">
                <a class="mui-control-item ico-pc jp-btn-distance-80" data-terminal="pc">${views.game_auto['电脑版']}</a>
            </c:if>
        </div>
    </div>
</div>
