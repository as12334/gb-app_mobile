<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="lottery-nav">
    <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted">
        <div class="mui-scroll">
            <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                <c:forEach var="a" items="${type.value}" varStatus="vs">
                    <c:if test="${vs.index == 0}">
                        <input type="hidden" id="lottery-id" value="${a.apiId}"/>
                    </c:if>
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
                        <soul:button target="changeLottery" text="${a.name}" opType="function" apiId="${a.apiId}"
                                     cssClass="${tempType} mui-tab-item ${vs.index == 0 ? 'mui-active':''}"/>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<div class="lottery-content"><!--彩票内容切换-->
    <c:forEach var="a" items="${type.value}" varStatus="vs">
        <div id="lottery-${a.apiId}" class="mui-control-content ${vs.index == 0 ? 'mui-active':''}">
            <c:forEach var="i" items="${lotteryGame}">
                <c:if test="${a.apiId == i.key}">
                    <c:choose>
                        <c:when test="${fn:length(i.value)>0}">
                            <c:forEach var="g" items="${i.value}">
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <soul:button dataApiTypeId="4"
                                                 dataApiId="${g.apiId}"
                                                 dataApiName="${gbFn:getGameName(g.gameId)}"
                                                 dataGameId="${g.gameId}"
                                                 dataGameCode="${g.apiId == 10||g.apiId==2?'':g.code}"
                                                 dataStatus="${g.status}"
                                                 target="goApiGame" text="" opType="function" cssClass="item _api">
                                        <img src="${soulFn:getImagePath(domain, g.cover)}" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">${gbFn:getGameName(g.gameId)}</div>
                                    </soul:button>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </c:forEach>
        </div>
    </c:forEach>
</div>
