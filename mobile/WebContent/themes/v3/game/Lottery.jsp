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
                            <c:when test="${centerId == -3   && a.apiId == 22}">
                                <c:set var="tempType" value="api-icon-4-22-1"/>
                            </c:when>
                            <c:when test="${a.apiId == 10}">
                                <c:set var="tempType" value="api-icon-4-${a.apiId}"/>
                            </c:when>
                            <c:when test="${a.apiId == 2}">
                                <c:set var="tempType" value="api-icon-4-${a.apiId}"/>
                            </c:when>
                        </c:choose>
                        <a href="#lottery-${a.apiId}" data-lottery-id="${a.apiId}"
                           class="mui-tab-item ${vs.index == 0 ? 'mui-active':''} a ${tempType}">${a.name}</a>
                    </li>
                </c:forEach>
                <%--<li><a href="#lottery4" class="mui-tab-item mui-active yzt">一指通彩票</a></li>
                <li><a href="#lottery5" class="mui-tab-item kg">KG彩票</a></li>
                <li><a href="#lottery6" class="mui-tab-item bb">BBIN彩票</a></li>--%>
            </ul>
        </div>
    </div>
</div>

<div class="lottery-content"><!--彩票内容切换-->
    <c:forEach var="a" items="${type.value}" varStatus="vs">
        <div id="lottery-${a.apiId}" class="mui-control-content ${vs.index == 0 ? 'mui-active':''}">
            <%--<%@include file="LotteryPartial.jsp"%>--%>
        </div>
    </c:forEach>
</div>
