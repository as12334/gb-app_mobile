<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="nav">
    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
        <div class="mui-scroll">
            <c:forEach var="apiType" items="${SiteApiRelationI18n}" varStatus="status">
                <c:set var="type" value=""/>
                <c:choose>
                    <c:when test="${apiType.key == 1}"><c:set var="type" value="live"/></c:when>
                    <c:when test="${apiType.key == 2}"><c:set var="type" value="casino"/></c:when>
                    <c:when test="${apiType.key == 3}"><c:set var="type" value="sports"/></c:when>
                    <c:when test="${apiType.key == 4}"><c:set var="type" value="lottery"/></c:when>
                </c:choose>
                <a class="mui-control-item ${empty path && status.index == 0 ? 'mui-active':''} item-${type}" data-item="${type}" data-id="${apiType.key}" >
                    <c:choose>
                        <c:when test="${type == 'live'}">${views.game_auto['真人']}</c:when>
                        <c:when test="${type == 'casino'}">${views.game_auto['电子']}</c:when>
                        <c:when test="${type == 'sports'}">${views.game_auto['体育']}</c:when>
                        <c:when test="${type == 'lottery'}">${views.game_auto['彩票']}</c:when>
                    </c:choose>
                </a>
            </c:forEach>
            <a class="mui-control-item item-fish" data-item="fish" data-id="5">
                捕鱼游戏
            </a>
        </div>
    </div>
</section>