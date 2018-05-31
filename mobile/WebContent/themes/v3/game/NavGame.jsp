<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %><%--@elvariable id="apis" type="java.util.List<so.wwb.gamebox.model.company.site.vo.ApiCacheEntity>"--%>
<%--@elvariable id="games" type="java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:if test="${fn:length(apis)>1}">
    <%int apiCount = 0;Object fgame =null;%>
    <c:forEach var="i" items="${apis}" varStatus="vs">
        <c:set var="gameCount" value="${i.games}"/>
        <c:if test="${fn:length(gameCount)>0}"><%if(fgame==null){fgame=pageContext.getAttribute("i");pageContext.setAttribute("fgame",fgame); }apiCount = ++apiCount;%></c:if>
    </c:forEach>
    <c:set var="apiCount" value="<%=apiCount%>"/>
    <c:if test="${apiCount > 1}">
        <div class="lottery-nav" name="nav-${apiTypeId}">
            <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted"
                 data-scroll="7">
                <div class="mui-scroll">
                    <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                        <c:forEach var="i" items="${apis}" varStatus="vs">
                            <c:set var="centerId" value="<%=CommonContext.get().getSiteParentId() %>"/>
                            <c:choose>
                                <c:when test="${centerId == -3 && i.apiId == 22}">
                                    <c:set var="api_icon" value="4-22-2"/>
                                </c:when>
                                <c:when test="${i.apiId == 22}">
                                    <c:set var="api_icon" value="4-22-1"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="api_icon" value="${i.apiTypeId}-${i.apiId}"/>
                                </c:otherwise>
                            </c:choose>
                            <c:set var="gameCount" value="${i.games}"/>
                            <c:if test="${fn:length(gameCount)>0}">
                                <li>
                                    <a data-rel='{"target":"changeNavGame","opType":"function","apiTypeId":"${i.apiTypeId}","apiId":"${i.apiId}"}'
                                       class="mui-tab-item ${vs.index==0?'mui-active':''} api-icon-${api_icon}">${i.relationName}</a>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>
    </c:if>
</c:if>
<div class="lottery-content" name="nav-content-${apiTypeId}">
        <c:forEach var="i" items="${apis}" varStatus="vs">
            <c:set var="games" value="${i.games}"/>
            <c:if test="${fn:length(games)>0}">

                <div id="nav-${apiTypeId}-${i.apiId}" class="mui-control-content ${i==fgame?' mui-active':''}">
                    <c:forEach var="g" items="${games}">
                        <c:choose>
                            <c:when test="${g.apiTypeId == 5}">
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                                    "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                                    "dataStatus":"${g.status}","target":"fishGameLogin","opType":"function"}'
                                       class="_api">
                                        <img data-lazyload="${g.cover}" class="lottery-img">
                                        <div class="mui-media-body">${g.name}</div>
                                    </a>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a data-rel='{"dataApiTypeId":"${g.apiTypeId}","dataApiId":"${g.apiId}","dataApiName":"${g.name}",
                                                    "dataGameId":"${g.gameId}","dataGameCode":"${g.apiId == 10||g.apiId==2?'':g.code}",
                                                    "dataStatus":"${g.status}","target":"goApiGame","opType":"function"}'
                                       class="_api">
                                        <img data-lazyload="${g.cover}" class="lottery-img">
                                        <div class="mui-media-body">${g.name}</div>
                                    </a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:if>
        </c:forEach>
</div>