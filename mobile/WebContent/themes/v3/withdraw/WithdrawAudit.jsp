<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>${views.withdraw_auto['查看稽核']}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <%@ include file="../include/include.head.jsp" %>
    <%--<link rel="bookmark" href="../../mobile-v3/favicon.ico">
    <link rel="shortcut icon" href="../../mobile-v3/favicon.ico">
    <link rel="stylesheet" href="../../mobile-v3/themes/mui.min.css" />
    <link rel="stylesheet" href="../../mobile-v3/themes/common.css" />
    <link rel="stylesheet" href="../../mobile-v3/themes/otherpage.css" />
    <link rel="stylesheet" href="../../mobile-v3/themes/default/style.css" />--%>
</head>
<body class="audit">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.withdraw_auto['查看稽核']}</h1>
</header>
<%--<div class="mui-content mui-scroll-wrapper">--%>
    <%--<div class="mui-scroll">--%>
        <div class="mui-row">
            <div class="mine-table-wrapper">
                <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
                    <div class="mui-scroll">
                        <div class="mine-table">
                            <table style="width: 600px;">
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
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>免稽核</td>
                                        <td><div class="text-red">-50</div></td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td>200</td>
                                    </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td>200</td>
                                    </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>免稽核</td>
                                        <td>200</td>
                                    </tr><tr>
                                    <td>2016-11-02 17:20:34</td>
                                    <td>200</td>
                                    <td>免稽核</td>
                                    <td><div class="text-red">-50</div></td>
                                    <td>200</td>
                                    <td>200/400</td>
                                    <td>200</td>
                                </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td>200</td>
                                    </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>免稽核</td>
                                        <td>200</td>
                                    </tr><tr>
                                    <td>2016-11-02 17:20:34</td>
                                    <td>200</td>
                                    <td>免稽核</td>
                                    <td><div class="text-red">-50</div></td>
                                    <td>200</td>
                                    <td>200/400</td>
                                    <td>200</td>
                                </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td>200</td>
                                    </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>免稽核</td>
                                        <td>200</td>
                                    </tr><tr>
                                    <td>2016-11-02 17:20:34</td>
                                    <td>200</td>
                                    <td>免稽核</td>
                                    <td><div class="text-red">-50</div></td>
                                    <td>200</td>
                                    <td>200/400</td>
                                    <td>200</td>
                                </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td>200</td>
                                    </tr>
                                    <tr>
                                        <td>2016-11-02 17:20:34</td>
                                        <td>200</td>
                                        <td>200/400</td>
                                        <td><div class="text-green">通过</div></td>
                                        <td>200</td>
                                        <td>免稽核</td>
                                        <td>200</td>
                                    </tr>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    <%--</div>--%>
<%--</div>--%>

</body>

<%--<%@ include file="withdrawV3/withdraw.audit.jsp" %>--%>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/withdraw/Withdraw.js"></script>


</html>
<%--
<%@ include file="/include/include.footer.jsp" %>--%>
