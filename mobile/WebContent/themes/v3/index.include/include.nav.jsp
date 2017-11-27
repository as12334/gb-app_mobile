<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="nav">
    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
        <div class="mui-scroll">
            <c:forEach var="at" items="${apiTypes}" varStatus="vs">
                <c:set var="type" value=""/>
                <c:choose>
                    <c:when test="${at.apiTypeId == 1}"><c:set var="type" value="live"/></c:when>
                    <c:when test="${at.apiTypeId == 2}"><c:set var="type" value="casino"/></c:when>
                    <c:when test="${at.apiTypeId == 3}"><c:set var="type" value="sports"/></c:when>
                    <c:when test="${at.apiTypeId == 4}"><c:set var="type" value="lottery"/></c:when>
                </c:choose>

                <a class="mui-control-item ${empty path && at.apiTypeId == 1 ? 'mui-active':''} item-${type}" data-item="${type}" data-id="${at.apiTypeId}" >
                    <c:choose>
                        <c:when test="${at.apiTypeId == 1}">${views.game_auto['真人']}</c:when>
                        <c:when test="${at.apiTypeId == 2}">${views.game_auto['电子']}</c:when>
                        <c:when test="${at.apiTypeId == 3}">${views.game_auto['体育']}</c:when>
                        <c:when test="${at.apiTypeId == 4}">${views.game_auto['彩票']}</c:when>
                    </c:choose>
                </a>
            </c:forEach>
            <a class="mui-control-item item-fish" data-item="fish" data-id="5">
                捕鱼游戏
            </a>
            <a class="mui-control-item item-about" data-item="about" data-id="6" >关于我们</a>
            <a class="mui-control-item item-terms" data-item="terms" data-id="7" hidden>注册条款</a>
        </div>
    </div>
</section>