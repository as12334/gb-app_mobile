<%--@elvariable id="command" type="so.wwb.gamebox.model.company.site.vo.VSiteApiListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<!--体育赛事 -->
<input type="hidden" id="api-type" value="sport" />
<div class="mui-row">
    <div class="game-list">
        <c:forEach var="a" items="${command}">
        <c:if test="${a.apiId != 10}">
        <div class="game" style="background: url(${resRoot}/themes/default/images/game-list-${a.apiId}.jpg) 0 0/100% no-repeat;">
            <a class="enter-btn _api" data-api-type-id="3" data-api-id="${a.apiId}" data-game-id="0"
               data-status="${api.get(a.apiId.toString()).systemStatus eq 'maintain' ? 'maintain' : siteApi.get(a.apiId.toString()).systemStatus}">${views.game_auto['进入游戏']}</a>
            <div class="game-logo-wrap">
                <img src="${resRoot}/images/game-${a.apiId}-logo.png" width="${a.apiId eq 12 || a.apiId eq 36 ?'36':'58'}" alt="">
                <div class="txt">${gbFn:getApiName(a.apiId)}</div>
            </div>
        </div>
        </c:if>
        </c:forEach>
    </div>
</div>
