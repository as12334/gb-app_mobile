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
                        <soul:button target="changeLottery" text="${a.name}" opType="function" apiId="${a.apiId}" cssClass="${tempType} mui-tab-item ${vs.index == 0 ? 'mui-active':''}"/>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>

<div class="lottery-content"><!--彩票内容切换-->
    <c:forEach var="a" items="${type.value}" varStatus="vs">
        <div id="lottery-${a.apiId}" class="mui-control-content ${vs.index == 0 ? 'mui-active':''}">
            <div class="api-loading">
                <div class="loader api-loader">
                    <div class="loader-inner ball-pulse api-div">
                        <div></div>
                        <div></div>
                        <div></div>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
