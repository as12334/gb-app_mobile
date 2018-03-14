<%--@elvariable id="apiTypeRelations" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiTypeRelation>"--%>
<%--@elvariable id="navApiGameMap" type="java.util.Map<java.lang.Integer, java.util.Map<java.lang.Integer, java.util.List<so.wwb.gamebox.model.company.site.po.SiteGame>>>"--%>
<%--@elvariable id="g" type="so.wwb.gamebox.model.company.site.po.SiteGame"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:if test="${fn:length(apiTypeRelations)>1}">
    <div class="lottery-nav" name="nav-${apiTypeId}">
        <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted" data-scroll="7">
            <div class="mui-scroll">
                <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                    <c:forEach var="i" items="${apiTypeRelations}" varStatus="vs">
                        <li><a data-rel='{"target":"changeNavGame","opType":"function","apiTypeId":"${i.apiTypeId}","apiId":"${i.apiId}"}' class="mui-tab-item ${vs.index==0?'mui-active':''} api-icon-${i.apiTypeId}-${i.apiId}">${i.apiName}</a></li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</c:if>
<div class="lottery-content" name="nav-content-${apiTypeId}">
    <c:set var="navGameMap" value="${navApiGameMap.get(apiTypeId)}"/>
    <c:forEach var="i" items="${apiTypeRelations}" varStatus="vs">
        <div id="nav-${apiTypeId}-${i.apiId}" class="mui-control-content ${vs.index==0?' mui-active':''}">
            <c:set var="navGames" value="${navGameMap.get(i.apiId)}"/>
            <c:if test="${fn:length(navGames)<=0}">
                <div class="deficiency-nots">没有找到符合的游戏</div>
            </c:if>
            <c:forEach var="g" items="${navGames}">
                <%--<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                    <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                            "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                            "dataStatus":"${g.status}","target":"goApiGame","opType":"function"}' class="_api">
                        <img data-lazyload="${root}/${g.cover}" class="lottery-img">
                        <div class="mui-media-body">${g.name}</div>
                    </a>
                </li>--%>
                <c:choose>
                    <c:when test="${g.apiTypeId == 5}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                            "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                            "dataStatus":"${g.status}","target":"fishGameLogin","opType":"function"}' class="_api">
                                <img data-lazyload="${root}/${g.cover}" class="lottery-img">
                                <div class="mui-media-body">${g.name}</div>
                            </a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                            "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                            "dataStatus":"${g.status}","target":"goApiGame","opType":"function"}' class="_api">
                                <img data-lazyload="${root}/${g.cover}" class="lottery-img">
                                <div class="mui-media-body">${g.name}</div>
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </div>
    </c:forEach>
</div>