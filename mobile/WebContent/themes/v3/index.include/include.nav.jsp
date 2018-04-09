<%--@elvariable id="siteApiTypes" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiType>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="nav">
    <div class="swiper-container nav-slide-indicators">
        <div class="swiper-wrapper">
            <c:forEach var="apiType" items="${siteApiTypes}" varStatus="status">
                <c:set var="type" value=""/>
                <c:choose>
                    <c:when test="${apiType.apiTypeId == 1}">
                        <c:set var="type" value="live"/>
                    </c:when>
                    <c:when test="${apiType.apiTypeId == 2}">
                        <c:set var="type" value="casino"/>
                    </c:when>
                    <c:when test="${apiType.apiTypeId == 3}">
                        <c:set var="type" value="sports"/>
                    </c:when>
                    <c:when test="${apiType.apiTypeId == 4}">
                        <c:set var="type" value="lottery"/>
                    </c:when>
                    <c:when test="${apiType.apiTypeId == 5}">
                        <c:set var="type" value="chess-and-card"/>
                    </c:when>
                </c:choose>
                <a data-rel='{"target":"slideHeight","opType":"function"}' class="swiper-slide item-${type} ${status.index == 0 ? ' mui-active':''}"><span>${apiType.name}</span></a>
            </c:forEach>
            <c:if test="${fn:length(fish)>0}">
                <a data-rel='{"target":"slideHeight","opType":"function"}' class="swiper-slide item-fish"><span>${dicts.game.game_type['Fish']}</span></a>
            </c:if>
        </div>
    </div>
</section>
