<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row">
    <div class="index-block hb-block">
        <div class="index-block-header mui-hide">
            <div class="mui-pull-left title">${views.game_auto['喜好定制']}</div>
            <div class="mui-pull-right more">
                <a href="#">
                    <span class="mui-icon mui-icon-plus"></span>${views.game_auto['自定义']}
                </a>
            </div>
        </div>
        <div class="index-block-content">
            <c:set var="apiListSize" value="${fn:length(apiList)}"></c:set>
            <c:forEach var="apiType" items="${apiList}" varStatus="status"><%--BBIN只显示一个--%>
                <c:if test="${apiListSize <= 3 || status.index % 3 == 0}">
                    <div class="mui-row">
                </c:if>
                <c:set var="apiStatus" value="${api.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus eq 'maintain' ?'maintain' : siteApi.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus}"></c:set>
                <div class="mui-col-xs-4">
                    <a href="#" class="_api"
                       data-api-id="${apiType.get("apiTypeRelation").apiId}"
                       data-api-type-id="${apiType.get("apiTypeRelation").apiTypeId}"
                       data-status="${apiStatus}">
                        <span class="api-icon api-icon-${apiType.get("apiTypeRelation").apiTypeId}-${apiType.get("apiTypeRelation").apiId}"></span>
                        <span class="icon-text">
                            <c:choose>
                            <c:when test="${apiType.get('apiTypeRelation').apiId eq 10}">${gbFn:getApiName(10)}</c:when>
                            <c:otherwise>${apiType.get("apiTypeRelation").name}</c:otherwise>
                            </c:choose></span>
                    </a>
                </div>
                <c:if test="${apiListSize<=3||status.index%3==2}">
                    </div>
                </c:if>
            </c:forEach>
            <%--<!-- 更多的喜好 -->
            <div class="mui-row">
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-nyx"></span><span class="icon-text">NYX</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-hb"></span><span class="icon-text">HB</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-hg"></span><span class="icon-text">HG</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-im"></span><span class="icon-text">IM</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-sb"></span><span class="icon-text">SB</span></a></div>
                <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-kg"></span><span class="icon-text">KG</span></a></div>
                <!-- <div class="mui-col-xs-4"><a href=""><span class="icon-hobby-chq"></span><span class="icon-text">CHQ</span></a></div> -->
            </div>--%>
        </div>
    </div>
</div>
