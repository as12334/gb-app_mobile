<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
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
                        <img src="${resRoot}/images/api/api-lottery-${a.apiId}.png">
                        <span class="mui-tab-label api-name">
                                ${gbFn:getApiName(a.apiId.toString())}
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
                <%@include file="../include/include.loading.jsp"%>
            </div>
        </div>
    </c:forEach>
</div>