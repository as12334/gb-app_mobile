<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${payAccountForScan != null}">
        <form id="scanForm" onsubmit="return false">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <gb:token/>
            <input type="hidden" name="result.rechargeType" value="${scanPay}"/>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <c:set var="onlinePayMax" value="${empty payAccountForScan.singleDepositMax?99999999:payAccountForScan.singleDepositMax}"/>
            <c:set var="onlinePayMin" value="${empty payAccountForScan.singleDepositMin || payAccountForScan.singleDepositMin <= 0 ?0.01:payAccountForScan.singleDepositMin}"/>
            <input type="hidden" class="onlinePayMax" value="${onlinePayMax}" name="onlinePayMax"/>
            <input type="hidden" class="onlinePayMin" value="${onlinePayMin}" name="onlinePayMin"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <input type="hidden" name="account" value="${command.getSearchId(payAccountForScan.id)}"/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label for="result.rechargeAmount">${views.deposit_auto['金额']}</label>
                    <%--<div class="ct">--%>
                        <p class="text-gray-light">
                            <c:if test="${payAccountForScan.randomAmount eq true}">
                                <% int randomCash = (int)(Math.random()*88+11);%>
                                <input type="hidden" name="result.randomCash" value="<%=randomCash%>">
                                <span style=" display: inline-block; height: 39px;float:right;margin:1px 10px 0 0;  background: #eee;  padding: 0 4px;  vertical-align: -1px;  color: #333;">.<%=randomCash%></span>
                            </c:if>
                            <input type="text" value="" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                                   name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:8px;"/>
                        </p>
                   <%-- </div>--%>
                </div>
                <%@include file="./ChooseAmount.jsp"%>
                <c:set var="accountType" value="${payAccountForScan.accountType}"/>
                <c:if test="${accountType eq '10' || accountType eq '11' || accountType eq '12'}">
                    <input type="hidden" value="true" name="isAuthCode"/>
                    <div class="mui-input-row">
                        <label for="result.payerBankcard">授权码</label>
                        <input type="text" placeholder="" name="result.payerBankcard" id="result.payerBankcard" autocomplete="off"/>
                    </div>
                    <p class="depositHelp" accountType="${accountType}">获取授权码教程<span>?</span></p>
                </c:if>
                <!--随机额度提示-->
                <c:if test="${payAccountForScan.randomAmount eq true}">
                    <div class="gb-form-notice" id="randomAmountMsg">
                        <p name="randomAmountMsg">${views.deposit_auto['随机额度提示']}</p>
                    </div>
                </c:if>
                <div class="gb-form-notice">
                    <p>* ${views.deposit_auto['提示']}<span style="color: red">${views.deposit_auto['支付成功']}</span>${views.deposit_auto['关闭支付窗口']}
                    <p>* ${views.deposit_auto['客服帮助']}
                        <a class="customer" id="loadCustomerId" href="#">${views.deposit_auto['点击联系在线客服']}</a>
                    </p>
                </div>
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