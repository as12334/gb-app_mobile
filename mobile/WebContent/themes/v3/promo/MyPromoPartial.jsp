<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">

            <c:choose>
                <c:when test="${s.checkState eq '2' || s.checkState eq 'success'}">
                    <c:set value="green" var="color"/>
                    <c:set value="${views.promo_auto['已发放']}" var="text"/>
                    <c:set value="awarded" var="recordName"/>
                </c:when>
                <c:when test="${s.checkState eq '4'}">
                    <c:set value="gray" var="color"/>
                    <c:set value="${views.promo_auto['未通过']}" var="text"/>
                    <c:set value="didNotPass" var="recordName"/>
                </c:when>
                <c:when test="${s.checkState eq '1'}">
                    <c:set value="orange" var="color"/>
                    <c:set value="${views.promo_auto['待审核']}" var="text"/>
                    <c:set value="unaudited" var="recordName"/>
                </c:when>
            </c:choose>
            <c:if test="${fn:length(recordName) > 0}">
                <div class="promo-records-warp" name="${recordName}">
                    <div class="mui-row">
                        <div class="promo-box-left">
                            <div>
                                <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                            </div>
                            <div>
                                <span class="tit">申请单号：</span>${s.transactionNo}
                            </div>
                            <div>
                                <span class="tit">参与活动：</span>${s.activityName}
                            </div>
                            <div>
                                <span class="tit">优惠稽核：</span><em
                                    class="org">${soulFn:formatCurrency(s.preferentialAudit)}倍</em>
                            </div>
                            <i class="pro-bg-left"></i>
                        </div>
                        <div class="promo-box-right promo-${color}">
                        <span class="num">
                            <c:if test="${s.checkState eq 'success' || s.checkState ne '0'}">
                                ${siteCurrencySign}${soulFn:formatCurrency(s.preferentialValue)}
                            </c:if>
                        </span>
                            <span class="annotation">${text}</span>
                            <i class="pro-bg-right"></i>
                            <div class="round radio-up"></div>
                            <div class="round radio-dwon"></div>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </c:when>
    <c:otherwise>
        <div class="promo-records-warp">
            <div class="mui-row no-promo">
                    ${views.promo_auto['暂无优惠信息']}
            </div>
        </div>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${s.checkState eq '2' || s.checkState eq 'success'}">

    </c:when>
    <c:when test="${s.checkState eq '4'}">
        <c:set value="gray" var="color"/>
        <c:set value="${views.promo_auto['未通过']}" var="text"/>
        <c:set value="didNotPass" var="recordName"/>
    </c:when>
    <c:when test="${s.checkState eq '1'}">
        <c:set value="orange" var="color"/>
        <c:set value="${views.promo_auto['待审核']}" var="text"/>
        <c:set value="unaudited" var="recordName"/>
    </c:when>
</c:choose>
<input value="${command.paging.lastPageNumber}" id="AllPageNumber" type="hidden"/>
<input value="${command.paging.lastPageNumber}" id="awardedPageNumber" type="hidden"/>
<input value="${command.paging.lastPageNumber}" id="didNotPassPageNumber" type="hidden"/>
<input value="${command.paging.lastPageNumber}" id="unauditedPageNumber" type="hidden"/>