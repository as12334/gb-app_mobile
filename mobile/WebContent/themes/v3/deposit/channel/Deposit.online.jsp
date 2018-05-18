<%--
  Created by IntelliJ IDEA.
  User: hanson
  Date: 18-5-11
  Time: 下午1:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(accounts)>0}">
        <gb:token/>
        <div id="validateRule" style="display: none">${validateRule}</div>
        <c:set var="account" value="${accounts[0]}"/>
        <c:set var="onlinePayMin" value="${empty account.singleDepositMin || account.singleDepositMin <= 0 ? 0.01 : account.singleDepositMin}"/>
        <c:set var="onlinePayMax" value="${empty account.singleDepositMax?99999999:account.singleDepositMax}"/>
        <input type="hidden" name="onlinePayMin" id="onlinePayMin" value="${onlinePayMin}"/>
        <input type="hidden" name="onlinePayMax" id="onlinePayMax" value="${onlinePayMax}"/>
        <input type="hidden" name="result.rechargeType" value="online_deposit"/>
        <input type="hidden" name="activityId" id="activityId"/>
        <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
        <input type="hidden" name="account" id="account" value="${command.getSearchId(account.id)}"/>
        <input type="hidden" name="result.payerBank" id="result.payerBank" value="${account.bank}"/>
        <input type="hidden" name="depositChannel" value="online"/>
        <input type="hidden" name="channel" value="${channel}"/>
        <div class="pay_mone">
            <%@include file="../ChooseAmount.jsp"%>
            <div class="depo_row">
                <div class="label">存款金额</div>
                <div class="input"><input id="result.rechargeAmount" name="result.rechargeAmount" type="text" placeholder="${soulFn:formatCurrency(onlinePayMin)}~${onlinePayMax}"/></div>
                <div class="ext" id="random_area" style="display: ${account.randomAmount?"":"none"}">
                    <input type="hidden" name="result.randomCash" value="${rechargeDecimals}"/>
                    <div class="cha">.${rechargeDecimals}</div>
                </div>
            </div>
            <div class="depo_row" data-rel='{"opType":"function","target":"depositOnline.showBankList"}'>
                <div class="label">支付银行</div>
                <div class="input">
                    <input id="selectBank"  type="text" value="${account.bankName}" readonly>
                </div>
                <div class="ext">
                    <i class="mui-icon mui-icon-arrowdown"></i>
                </div>
                <input type="hidden" id="bankJson" value='${accountJson}' />
            </div>
        </div>
        <div class="btn_wrap">
            <a class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"depositOnline.nextStep","payType":"online"}'>提交</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light" style="width:100%; text-align:center; padding-top: 20px;">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>
