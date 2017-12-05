<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <div class="promo-records-warp">
                <div class="mui-row">
                    <div class="promo-box-left">
                        <h5>${empty s.activityName?views.promo_auto['系统优惠']:s.activityName}</h5>
                        <div class="text">
                            <span class="sum">
                                <c:choose>
                                <c:when test="${(not empty s.preferentialAudit) && s.preferentialAudit ne 0}">
                                <em class="org">${soulFn:formatCurrency(s.preferentialAudit)}</em>${views.promo_auto['倍稽核']}
                                </c:when>
                                    <c:otherwise>
                                        ${views.promo_auto['免稽核']}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                            <span class="time">${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}</span>
                        </div>
                        <i class="pro-bg-left"></i>
                    </div>
                    <c:set value="" var="mt" />
                    <c:choose>
                        <c:when test="${s.checkState eq 'success'||s.checkState eq '2' || s.checkState eq '4'}">
                            <c:set value="green" var="color"/>
                            <c:set value="${views.promo_auto['已发放']}" var="text"/>
                        </c:when>
                        <c:when test="${s.checkState eq '1'}">
                            <c:set value="orange" var="color"/>
                            <c:set value="${views.promo_auto['待审核']}" var="text"/>
                        </c:when>
                        <c:when test="${s.checkState eq '0'}">
                            <c:set value="blue" var="color"/>
                            <c:set value="${views.promo_auto['进行中']}" var="text"/>
                            <c:set value="25" var="mt" />
                        </c:when>
                        <c:otherwise>
                            <c:set value="gray" var="color"/>
                            <c:set value="${views.promo_auto['未通过']}" var="text"/>
                        </c:otherwise>
                    </c:choose>
                    <div class="promo-box-right promo-${color}">
                        <span class="num" style="margin-top:${mt}px">
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
<input value="${command.paging.lastPageNumber}" id="lastPageNumber" type="hidden"/>
