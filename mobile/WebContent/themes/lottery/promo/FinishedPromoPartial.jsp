<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <li name="promoLi" code="${s.activityClassifyKey}" >
                <a class="activity-a" data-id="${s.id}">
                    <img src="${soulFn:getImagePathWithDefault(domain, s.activityAffiliated, resRoot.concat('/images/img-sale1.jpg'))}">
                </a>
                <%--<div name="activityDetail" class="activity-detail" style="display: none">
                    ${s.activityDescription}
                </div>--%>
            </li>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <li id="noActivity2">
            <div class="mui-no-data">${views.promo_auto['暂无优惠信息']}</div>
        </li>
    </c:otherwise>
</c:choose>
<input value="${command.paging.lastPageNumber}" id="lastPageNumber2" hidden/>
