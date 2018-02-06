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
                <a data-rel='{"target":"changeApiTypeTab","opType":"function","item":"${type}"}'
                   class="swiper-slide ${empty path && status.index == 0 ? 'mui-active':''} item-${type}">${apiType.name}</a>
            </c:forEach>
            <a data-rel='{"target":"changeApiTypeTab","opType":"function","item":"fish"}'
               class="swiper-slide item-fish">${views.themes_auto['捕鱼游戏']}</a>
        </div>
    </div>
</section>
