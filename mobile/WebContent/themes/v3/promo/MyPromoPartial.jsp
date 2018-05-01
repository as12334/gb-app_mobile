<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result) gt 0}">
        <c:forEach items="${command.result}" var="s">
            <c:choose>
                <c:when test="${s.checkState eq 'success'||s.checkState eq '2' || s.checkState eq '4'}">
                    <div class="promo-records-warp" name="awarded">
                </c:when>
                <c:when test="${s.checkState eq '1'}">
                    <div class="promo-records-warp" name="unaudited">
                </c:when>
                <c:when test="${s.checkState eq '0'}">
                    <div class="promo-records-warp">
                </c:when>
                <c:otherwise>
                    <div class="promo-records-warp" name="didNotPass">
                </c:otherwise>
            </c:choose>
            <div class="mui-row">
                <div class="promo-box-left">
                    <div>
                        <span class="tit">申请时间：</span>${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}
                    </div>
                        <%--<span class="time">${soulFn:formatDateTz(s.applyTime,DateFormat.DAY_SECOND ,timeZone )}</span>--%>
                    <div>
                        <span class="tit">申请单号：</span>${s.transactionNo}
                    </div>
                    <div>
                        <span class="tit">参与活动：</span>${s.activityName}
                    </div>
                    <div>
                        <span class="tit">优惠稽核：</span><em
                            class="org">${soulFn:formatCurrency(s.preferentialAudit)}</em>
                    </div>
                        <%--</div>--%>
                    <i class="pro-bg-left"></i>
                </div>
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
                        <%--<c:set value="25" var="mt" />--%>
                    </c:when>
                    <c:otherwise>
                        <c:set value="gray" var="color"/>
                        <c:set value="${views.promo_auto['未通过']}" var="text"/>
                    </c:otherwise>
                </c:choose>
                <div class="promo-box-right promo-${color}">
                        <span class="num"<%-- style="margin-top:${mt}px"--%>>
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
