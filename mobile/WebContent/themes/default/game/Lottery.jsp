<%--@elvariable id="command" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<!-- 彩票游戏 -->
<input type="hidden" id="api-type" value="lottery" />
<div class="mui-row">
    <div class="lottery-type">
        <ul id="mui-lottery" class="mui-bar-tab">
            <c:forEach var="a" items="${command}" varStatus="vs">
                <li class="mui-tab-item api-logo">
                    <c:if test="${vs.index == 0}">
                        <input type="hidden" id="lottery-id" value="${a.apiId}" />
                    </c:if>
                    <a data-lottery-id="${a.apiId}" data-lotto-idx="${vs.index}" class="${vs.index == 0 ? 'active' : ''}">
                        <c:choose>
                            <c:when test="${centerId == -3   && a.apiId == 22}">
                                <c:set var="path" value="${resRoot}/images/api/api-lottery-22-2.png"></c:set>
                            </c:when>
                            <c:otherwise>
                                <c:set var="path" value="${resRoot}/images/api/api-lottery-${a.apiId}.png"></c:set>
                            </c:otherwise>
                        </c:choose>
                        <img src="${path}"/>
                        <span class="mui-tab-label api-name">
                              ${a.name}
                        </span>
                    </a>
                </li>
            </c:forEach>
        </ul>
        <div class="clearfix"></div>
    </div>
</div>
<div class="mui-row" id="lottery-container">
    <c:forEach var="a" items="${command}" varStatus="vs">
        <div class="lottery-list ${vs.index == 0 ? 'mui-show' : ''}">
            <%--loading-x: 是否重新加载--%>
            <input type="hidden" id="loading-${a.apiId}" value="${vs.index == 0 ? 'false' : 'true'}">
            <%--paging-x: 当前API对应的当前页--%>
            <input type="hidden" id="paging-${a.apiId}" value="1">
            <div class="mui-row lottery-content-${a.apiId}">
                <%@include file="/include/include.loading.jsp"%>
            </div>
        </div>
    </c:forEach>
</div>
