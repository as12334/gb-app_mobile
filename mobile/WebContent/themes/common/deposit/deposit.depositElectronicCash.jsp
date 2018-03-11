<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${electronicPayAccount != null}">
        <form id="electronicCashForm" onsubmit="return false">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <gb:token/>
            <c:set var="onlinePayMin" value="${empty rank.onlinePayMin || rank.onlinePayMin <= 0 ?0.01:rank.onlinePayMin}"/>
            <c:set var="onlinePayMax" value="${empty rank.onlinePayMax?99999999:rank.onlinePayMax}"/>
            <input type="hidden" name="onlinePayMin" value="${onlinePayMin}"/>
            <input type="hidden" name="onlinePayMax" value="${onlinePayMax}"/>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label>${views.deposit_auto['金额']}</label>
                    <%--<div class="ct">--%>
                        <input type="text" value="" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:8px;"/>
                    <%--</div>--%>
                </div>
                <%@include file="./ChooseAmount.jsp"%>
                <div class="gb-form-notice">
                    <p>* ${views.deposit_auto['请先加好友']}</p>
                    <p>* ${views.deposit_auto['存款金额请加以小数']}
                    <p>* ${views.deposit_auto['提示']}<span style="color: red">${views.deposit_auto['支付成功']}</span>${views.deposit_auto['关闭支付窗口']}
                    <p>* ${views.deposit_auto['客服帮助']}
                        <a class="customer" id="loadCustomerId" href="#">${views.deposit_auto['点击联系在线客服']}</a>
                    </p>
                </div>
            </div>
        </form>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>
