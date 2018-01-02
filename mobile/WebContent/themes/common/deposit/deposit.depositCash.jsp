<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${rank != null}">
        <form id="depositCashForm" onsubmit="return false">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <gb:token/>
            <c:set var="onlinePayMin" value="${empty rank.onlinePayMin?0.01:rank.onlinePayMin}"/>
            <c:set var="onlinePayMax" value="${empty rank.onlinePayMax?99999999:rank.onlinePayMax}"/>
            <input type="hidden" name="onlinePayMin" value="${onlinePayMin}"/>
            <input type="hidden" name="onlinePayMax" value="${onlinePayMax}"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label>${views.deposit_auto['金额']}</label>
                    <div class="ct">
                        <input type="text" value="" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:8px;"/>
                    </div>
                </div>
                <%@include file="./ChooseAmount.jsp"%>
            </div>
            <div class="mui-row">
                <div class="gb-form-foot bank-pay-btn" style="padding-bottom:10px;">
                    <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount" disabled="disabled">${views.deposit_auto['下一步']}</button>
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
