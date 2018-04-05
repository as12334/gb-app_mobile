<%--@elvariable id="command" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
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
                       data-status="${a.apiStatus}">${views.game_auto['进入游戏']}</a>
                    <div class="game-logo-wrap">
                        <%---BC图片个性化--%>
                        <c:if test="${(siteId == 119 || siteId == 270) && a.apiId eq 37}">
                            <c:set var="special" value="-${siteId}"/>
                        </c:if>
                        <img src="${resRoot}/images/game-${a.apiId}-logo${special}.png" width="${a.apiId eq 12 || a.apiId eq 36 ||a.apiId eq 37 ?'36':'58'}" alt="">
                        <div class="txt">${a.name}</div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>
