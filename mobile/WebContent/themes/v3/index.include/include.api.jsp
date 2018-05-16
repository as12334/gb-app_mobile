<%--@elvariable id="siteApiTypes" type="java.util.List<so.wwb.gamebox.model.company.site.vo.ApiTypeCacheEntity>"--%>
<%--@elvariable id="fishGames" type="java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="swiper-container nav-slide-content api-grid">
    <div class="swiper-wrapper">
        <c:forEach var="apiType" items="${siteApiTypes}" varStatus="status">
            <c:set var="apiTypeId" value="${apiType.apiTypeId}"/>
            <c:set var="type" value=""/>
            <c:choose>
                <c:when test="${apiTypeId == 1}">
                    <c:set var="type" value="live"/>
                </c:when>
                <c:when test="${apiTypeId == 2}">
                    <c:set var="type" value="casino"/>
                </c:when>
                <c:when test="${apiTypeId == 3}">
                    <c:set var="type" value="sports"/>
                </c:when>
                <c:when test="${apiTypeId == 4}">
                    <c:set var="type" value="lottery"/>
                </c:when>
                <c:when test="${apiTypeId == 5}">
                    <c:set var="type" value="chess-and-card"/>
                </c:when>
            </c:choose>
            <c:set var="apis" value="${apiType.apis}"/>
            <c:if test="${fn:length(apis)>0}">
                <div class="swiper-slide slide-${type}">
                    <ul class="mui-table-view mui-grid-view mui-grid-9 active" data-list="${type}">
                        <c:choose>
                            <%--彩票棋牌类处理--%>
                            <c:when test="${apiTypeId==4 || apiTypeId == 5}">
                                <%@include file="../game/NavGame.jsp"%>
                            </c:when>
                            <%--电子类处理--%>
                            <c:when test="${apiTypeId==2}">
                                <c:forEach var="i" items="${apis}">
                                    <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                        <a data-rel='{"target":"${root}/game/getCasinoByApiId.html?search.apiId=${i.apiId}&search.apiTypeId=${i.apiTypeId}","opType":"href"}'>
                                            <span class="api-item api-icon-${apiTypeId}-${i.apiId}"></span>
                                            <div class="mui-media-body">${i.relationName}</div>
                                        </a>
                                    </li>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="i" items="${apis}">
                                    <c:if test="${apiTypeId!=3 || i.apiId!=10}">
                                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                            <a data-rel='{"dataApiId":"${i.apiId}","dataApiTypeId":"${i.apiTypeId}","dataApiName":"${i.apiName}","dataStatus":"${i.apiStatus}","target":"goApiGame","opType":"function"}'>
                                                <span class="api-item api-icon-${apiTypeId}-${i.apiId} site${siteId}"></span>
                                                <div class="mui-media-body">${i.relationName}</div>
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </c:if>
        </c:forEach>
        <%--捕鱼游戏--%>
        <c:if test="${fn:length(fishGames)>0}">
            <div class="swiper-slide slide-fish" >
                <ul class="mui-table-view mui-grid-view mui-grid-9 active">
                    <c:forEach var="g" items="${fishGames}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}","dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10?'':g.code}",
                                            "dataStatus":"${g.status}","target":"fishGameLogin","opType":"function"}'>
                                <img data-lazyload="${root}/${g.cover}" class="fish-img"/>
                                <div class="mui-media-body">${g.name}</div>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </div>
</div>