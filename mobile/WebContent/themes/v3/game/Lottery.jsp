<%--@elvariable id="lottery" type="java.util.Map<java.lang.Integer,java.util.List<so.wwb.gamebox.model.company.site.po.SiteGame>>"--%>
<%--@elvariable id="lotteryGames" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteGame>"--%>
<%--@elvariable id="apiType" type="so.wwb.gamebox.model.company.site.po.SiteApiType"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="lottery-nav">
    <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted">
        <div class="mui-scroll">
            <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                <c:forEach var="a" items="${apiType.apiTypeRelations}" varStatus="vs">
                    <li>
                        <c:set var="tempType" value=""/>
                        <c:choose>
                            <c:when test="${centerId == -3 && a.apiId == 22}">
                                <c:set var="tempType" value="api-icon-4-22-1"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="tempType" value="api-icon-4-${a.apiId}"/>
                            </c:otherwise>
                        </c:choose>
                        <a data-rel='{"target":"changeLottery","opType":"function","apiId":"${a.apiId}"}' class="${tempType} mui-tab-item ${vs.index == 0 ? 'mui-active':''}">${a.apiName}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<div class="lottery-content"><!--彩票内容切换-->
    <c:forEach var="a" items="${apiType.apiTypeRelations}" varStatus="vs">
        <div id="lottery-${a.apiId}" class="mui-control-content ${vs.index == 0 ? 'mui-active':''}">
            <c:set var="lotteryGames" value="${lottery.get(a.apiId)}"/>
            <c:forEach var="g" items="${lotteryGames}">
                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                            "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                            "dataStatus":"${g.status}","target":"goApiGame","opType":"function"}' class="item _api">
                        <img src="${soulFn:getImagePath(domain, g.cover)}" class="lottery-img"/>
                        <div class="mui-media-body">${g.name}</div>
                    </a>
                </li>
            </c:forEach>
            <c:if test="${fn:length(lotteryGames)<=0}">
                <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
            </c:if>
        </div>
    </c:forEach>
</div>
