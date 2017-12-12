<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="nav">
    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
        <div class="mui-scroll">
            <c:forEach var="apiType" items="${SiteApiRelationI18n}" varStatus="status">
                <c:set var="type" value=""/>
                <c:set var="desc" value=""/>
                <c:choose>
                    <c:when test="${apiType.key == 1}">
                        <c:set var="type" value="live"/>
                        <c:set var="desc" value="${views.game_auto['真人']}"/>
                    </c:when>
                    <c:when test="${apiType.key == 2}">
                        <c:set var="type" value="casino"/>
                        <c:set var="desc" value="${views.game_auto['电子']}"/>
                    </c:when>
                    <c:when test="${apiType.key == 3}">
                        <c:set var="type" value="sports"/>
                        <c:set var="desc" value="${views.game_auto['体育']}"/>
                    </c:when>
                    <c:when test="${apiType.key == 4}">
                        <c:set var="type" value="lottery"/>
                        <c:set var="desc" value="${views.game_auto['彩票']}"/>
                    </c:when>
                </c:choose>
                <soul:button target="changeApiTypeTab" text="${desc}" opType="function" cssClass="mui-control-item ${empty path && status.index == 0 ? 'mui-active':''} item-${type}" item="${type}" data-id="${apiType.key}"/>
            </c:forEach>
            <soul:button target="changeApiTypeTab" text="${views.themes_auto['捕鱼游戏']}" opType="function" cssClass="mui-control-item item-fish" item="fish" data-id="5"/>
        </div>
    </div>
</section>