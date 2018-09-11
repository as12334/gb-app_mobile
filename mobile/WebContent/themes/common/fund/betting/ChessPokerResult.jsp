<%--@elvariable id="resultVo" type="so.wwb.gamebox.model.api.vo.ApiChessPokerResultVo"--%>
<%--@elvariable id="p" type="so.wwb.gamebox.model.site.report.po.VPlayerGameOrder"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<%--棋牌游戏结果--%>
<c:set var="betType" value="${resultVo.betType}"/>
<c:choose>

    <%--开元--%>
    <c:when test="${p.apiId==34}">
        <%--德州扑克--%>
        <c:if test="${betType eq '620'}">
            <c:set var="betType" value="TEXAS_HOLDEM"/>
        </c:if>
        <%--二八杠--%>
        <c:if test="${betType eq '720'}">
            <c:set var="betType" value="MAHJONG_TILES"/>
        </c:if>
        <%--抢庄牛牛--%>
        <c:if test="${betType eq '830'}">
            <c:set var="betType" value="BULL_BULL"/>
        </c:if>
        <%--炸金花--%>
        <c:if test="${betType eq '220'}">
            <c:set var="betType" value="GOLDEN_FRIED"/>
        </c:if>
        <%--三公--%>
        <c:if test="${betType eq '860'}">
            <c:set var="betType" value="THREE_FACE"/>
        </c:if>
        <%--押庄龙虎--%>
        <c:if test="${betType eq '900'}">
            <c:set var="betType" value="DRAGON_TIGER"/>
        </c:if>
        <%--21 点--%>
        <c:if test="${betType eq '600'}">
            <c:set var="betType" value="BLACKJACK"/>
        </c:if>
        <%--通比牛牛--%>
        <c:if test="${betType eq '870'}">
            <c:set var="betType" value="BULL_BULL"/>
        </c:if>
        <%--欢乐红包--%>
        <c:if test="${betType eq '880'}">
            <c:set var="betType" value="RED_ENVELOPE"/>
        </c:if>
    </c:when>

    <%--乐游棋牌--%>
    <c:when test="${p.apiId==46}">
        <%--德州扑克--%>
        <c:if test="${betType eq '620'}">
            <c:set var="betType" value="TEXAS_HOLDEM"/>
        </c:if>
        <%--二八杠--%>
        <c:if test="${betType eq '720'}">
            <c:set var="betType" value="MAHJONG_TILES"/>
        </c:if>
        <%--抢庄牛牛--%>
        <c:if test="${betType eq '830'}">
            <c:set var="betType" value="BULL_BULL"/>
        </c:if>
        <%--炸金花--%>
        <c:if test="${betType eq '220'}">
            <c:set var="betType" value="GOLDEN_FRIED"/>
        </c:if>
        <%--三公--%>
        <c:if test="${betType eq '860'}">
            <c:set var="betType" value="LEG_THREE_FACE"/>
        </c:if>
        <%--押庄龙虎--%>
        <c:if test="${betType eq '900'}">
            <c:set var="betType" value="DRAGON_TIGER"/>
        </c:if>
        <%--21 点--%>
        <c:if test="${betType eq '600'}">
            <c:set var="betType" value="LEG_BLACKJACK"/>
        </c:if>
        <%--通比牛牛--%>
        <c:if test="${betType eq '870'}">
            <c:set var="betType" value="LEG_TB_BULL"/>
        </c:if>
        <%--欢乐红包--%>
        <c:if test="${betType eq '880'}">
            <c:set var="betType" value="RED_ENVELOPE"/>
        </c:if>
        <%--急速扎金花--%>
        <c:if test="${betType eq '230'}">
            <c:set var="betType" value="GOLDEN_FRIED"/>
        </c:if>
        <%--抢庄牌九--%>
        <c:if test="${betType eq '730'}">
            <c:set var="betType" value="LEG_PAIJIU"/>
        </c:if>
        <%--斗地主--%>
        <c:if test="${betType eq '610'}">
            <c:set var="betType" value="DDZ"/>
        </c:if>
    </c:when>
    <%--VG--%>
    <c:when test="${p.apiId==42}">
        <c:if test="${betType eq '3'||betType eq '4' || betType eq '7'}">
            <c:set var="betType" value="BULL_BULL"/>
        </c:if>
    </c:when>

    <%-- 棋乐游 --%>
    <c:when test="${p.apiId==43}">
        <%-- 德州扑克 --%>
        <c:if test="${betType eq '43001'}">
            <c:set var="betType" value="TEXAS_HOLDEM"/>
        </c:if>
        <%-- 无庄牛牛,为棋乐游特殊添加 --%>
        <c:if test="${betType eq '43004' or betType eq '43007'}">
            <c:set var="betType" value="NO_DEALER_BULL_BULL"/>
        </c:if>
        <%-- 抢庄牛牛 --%>
        <c:if test="${betType eq '43002' or betType eq '43003'}">
            <c:set var="betType" value="BULL_BULL"/>
        </c:if>
        <%--炸金花--%>
        <c:if test="${betType eq '43006'}">
            <c:set var="betType" value="GOLDEN_FRIED"/>
        </c:if>
        <%--押庄龙虎--%>
        <c:if test="${betType eq '43005'}">
            <c:set var="betType" value="DRAGON_TIGER"/>
        </c:if>
        <%-- 欢乐30秒 --%>
        <c:if test="${betType eq '43008'}">
            <c:set var="betType" value="BLACKJACK"/>
        </c:if>
    </c:when>

</c:choose>
<gb:chessPokerCardResult betType="${betType}" commonPorker="${resultVo.commonPorker}" porkerListSet="${resultVo.porkerListSet}" porkerList="${resultVo.porkerList}"/>
