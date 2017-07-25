<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <title>${views.withdraw_auto['查看稽核']}</title>
    <%@ include file="/include/include.head.jsp" %>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.withdraw_auto['查看稽核']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="mine-table-wrapper">
                        <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
                            <div class="mui-scroll">
                                <div class="mine-table m-t-sm" style="width: 700px">
                                    <table class="bg-white">
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
                                                <td>
                                                        ${soulFn:formatDateTz(s.completionTime, DateFormat.DAY_SECOND,timeZone)}
                                                    <input type="hidden" name="feeList[${vs.index}].id" value="${s.id}" >
                                                </td>
                                                    <%--<td>${empty s.effectiveTransaction?0:s.effectiveTransaction}</td>--%>
                                                <td>
                                                    <c:if test="${s.transactionType!='deposit'}">--</c:if>
                                                    <c:if test="${s.transactionType=='deposit'}">
                                                        <c:if test="${s.transactionMoney eq null}">--</c:if>
                                                        <c:if test="${s.transactionMoney != null}">${dicts.common.currency_symbol[user.defaultCurrency]}${soulFn:formatInteger(s.transactionMoney)}${soulFn:formatDecimals(s.transactionMoney)}</c:if>
                                                    </c:if>
                                                </td>
                                                    <%--<td>${soulFn:formatInteger(s.relaxingQuota)}${soulFn:formatDecimals(s.relaxingQuota)}</td>--%>

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
                                                <td>
                                                    <c:if test="${s.administrativeFee eq null}">
                                                        --
                                                    </c:if>
                                                    <c:if test="${s.administrativeFee==0}">
                                                        <li class="green">${views.fund['withdraw.edit.playerWithdraw.passThrough']}</li>
                                                    </c:if>
                                                    <c:if test="${s.administrativeFee>0}">
                                                        <li class="co-red3">
                                                                ${dicts.common.currency_symbol[user.defaultCurrency]}
                                                            -${soulFn:formatInteger(s.administrativeFee)}${soulFn:formatDecimals(s.administrativeFee)}
                                                        </li>
                                                    </c:if>
                                                </td>

                                                <td>
                                                    <c:if test="${s.transactionType!='deposit'}">
                                                        <c:if test="${s.transactionMoney eq null}">--</c:if>
                                                        <c:if test="${s.transactionMoney != null}">${dicts.common.currency_symbol[user.defaultCurrency]}${soulFn:formatInteger(s.transactionMoney)}${soulFn:formatDecimals(s.transactionMoney)}</c:if>
                                                    </c:if>
                                                    <c:if test="${s.transactionType=='deposit'}">
                                                        --
                                                    </c:if>

                                                </td>
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
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script >
    if(os == 'app_ios'){
        mui('body').on('tap', '.mui-action-back', function () {
            goBack();
        });
    }
</script>
</html>

