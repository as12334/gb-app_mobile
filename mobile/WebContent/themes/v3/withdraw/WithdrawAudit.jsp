<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.withdraw_auto['查看稽核']}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>
<body class="audit">
<header class="mui-bar mui-bar-nav">
    <a style="color: #fff;" class="mui-icon mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
    <h1 class="mui-title">${views.withdraw_auto['查看稽核']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll" style="width: 600px;">
        <div class="mui-row">
            <div class="mine-table-wrapper">
                <div class="mine-table">
                    <table>
                        <tr class="head">
                            <td>${views.withdraw_auto['存款时间']}</td>
                            <td>${views.withdraw_auto['存款金额']}</td>
                            <td>${views.withdraw_auto['存款稽核点']}</td>
                            <td>${views.withdraw_auto['行政费用']}</td>
                            <td>${views.withdraw_auto['优惠金额']}</td>
                            <td>${views.withdraw_auto['优惠稽核点']}</td>
                            <td>${views.withdraw_auto['优惠扣除']}</td>
                        </tr>
                        <c:forEach items="${list}" var="s" varStatus="vs">
                            <tr>
                                    <%--存款时间--%>
                                <td>
                                    ${soulFn:formatDateTz(s.completionTime, DateFormat.DAY_SECOND,timeZone)}
                                    <input type="hidden" name="feeList[${vs.index}].id" value="${s.id}" >
                                </td>
                                    <%--<td>${empty s.effectiveTransaction?0:s.effectiveTransaction}</td>--%>
                                    <%--存款金额--%>
                                <td>
                                    <c:if test="${s.transactionType!='deposit'}">--</c:if>
                                    <c:if test="${s.transactionType=='deposit'}">
                                        <c:if test="${s.transactionMoney eq null}">--</c:if>
                                        <c:if test="${s.transactionMoney != null}">${dicts.common.currency_symbol[user.defaultCurrency]}${soulFn:formatInteger(s.transactionMoney)}${soulFn:formatDecimals(s.transactionMoney)}</c:if>
                                    </c:if>
                                </td>
                                    <%--<td>${soulFn:formatInteger(s.relaxingQuota)}${soulFn:formatDecimals(s.relaxingQuota)}</td>--%>
                                    <%--存款稽核点--%>
                                <td>
                                    <c:if test="${s.rechargeAuditPoints eq null}">
                                        --
                                    </c:if>
                                    <c:if test="${s.rechargeAuditPoints != null}">
                                        <c:if test="${empty s.effectiveTransaction}">
                                            <c:set var="sub" value="${s.remainderEffectiveTransaction+s.relaxingQuota}"></c:set>
                                        </c:if>
                                        <c:if test="${not empty s.effectiveTransaction}">
                                            <c:set var="sub" value="${s.effectiveTransaction+s.remainderEffectiveTransaction+s.relaxingQuota}"></c:set>
                                        </c:if>

                                        <c:set var="parent" value="${s.rechargeAuditPoints<0?0:s.rechargeAuditPoints}"></c:set>
                                        ${soulFn:formatInteger(sub)}${soulFn:formatDecimals(sub)}
                                        /
                                        ${soulFn:formatInteger(parent)}${soulFn:formatDecimals(parent)}
                                    </c:if>
                                </td>
                                        <%--行政费用--%>
                                <td>
                                    <c:if test="${s.administrativeFee eq null}">
                                        --
                                    </c:if>
                                    <c:if test="${s.administrativeFee==0}">
                                        <li class="text-green">${views.fund['withdraw.edit.playerWithdraw.passThrough']}</li>
                                    </c:if>
                                    <c:if test="${s.administrativeFee>0}">
                                        <li class="text-red">
                                                ${dicts.common.currency_symbol[user.defaultCurrency]}
                                            -${soulFn:formatInteger(s.administrativeFee)}${soulFn:formatDecimals(s.administrativeFee)}
                                        </li>
                                    </c:if>
                                </td>
                                        <%--优惠金额--%>
                                <td>
                                    <c:if test="${s.transactionType!='deposit'}">
                                        <c:if test="${s.transactionMoney eq null}">--</c:if>
                                        <c:if test="${s.transactionMoney != null}">${dicts.common.currency_symbol[user.defaultCurrency]}${soulFn:formatInteger(s.transactionMoney)}${soulFn:formatDecimals(s.transactionMoney)}</c:if>
                                    </c:if>
                                    <c:if test="${s.transactionType=='deposit'}">
                                        --
                                    </c:if>

                                </td>
                                        <%--优惠稽核点--%>
                                <td>
                                    <c:if test="${s.favorableAuditPoints eq null}">--</c:if>
                                    <c:if test="${s.favorableAuditPoints != null}">

                                        <c:if test="${empty s.effectiveTransaction}">
                                            <c:set var="sub" value="${s.favorableRemainderEffectiveTransaction}"></c:set>
                                        </c:if>
                                        <c:if test="${not empty s.effectiveTransaction}">
                                            <c:set var="sub" value="${s.effectiveTransaction+s.favorableRemainderEffectiveTransaction}"></c:set>
                                        </c:if>

                                        <c:set var="parent" value="${s.favorableAuditPoints-s.relaxingQuota<0?0:s.favorableAuditPoints}"></c:set>
                                        ${soulFn:formatInteger(sub)}${soulFn:formatDecimals(sub)}
                                        /
                                        ${soulFn:formatInteger(parent)}${soulFn:formatDecimals(parent)}


                                        <%--${soulFn:formatInteger(s.favorableAuditPoints)}${soulFn:formatDecimals(s.favorableAuditPoints)}--%>
                                    </c:if>
                                </td>
                                        <%--优惠扣除--%>
                                <td>
                                    <c:if test="${s.deductFavorable eq null}">
                                        --
                                    </c:if>
                                    <c:if test="${s.deductFavorable==0}">
                                        <li class="green">${views.fund['withdraw.edit.playerWithdraw.passThrough']}</li>
                                    </c:if>
                                    <c:if test="${s.deductFavorable>0}">
                                        <li class="co-red3">
                                                ${dicts.common.currency_symbol[user.defaultCurrency]}
                                            -${soulFn:formatInteger(s.deductFavorable)}${soulFn:formatDecimals(s.deductFavorable)}
                                        </li>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/withdraw/WithdrawAudit.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
