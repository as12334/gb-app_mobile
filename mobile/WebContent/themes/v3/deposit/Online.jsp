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
    <c:when test="${fn:length(payAccountMap)>0}">
        <form id="onlineForm" onsubmit="return false">
        <gb:token/>
        <div id="validateRule" style="display: none">${validateRule}</div>
        <c:set var="bank" value="${bankList[0]['value']}"/>
        <c:set var="account" value="${payAccountMap[bank]}"/>
        <c:set var="onlinePayMin" value="${empty account.singleDepositMin || account.singleDepositMin <= 0 ? 0.01 : account.singleDepositMin}"/>
        <c:set var="onlinePayMax" value="${empty account.singleDepositMax?99999999:account.singleDepositMax}"/>
        <input type="hidden" name="onlinePayMin" id="onlinePayMin" value="${onlinePayMin}"/>
        <input type="hidden" name="onlinePayMax" id="onlinePayMax" value="${onlinePayMax}"/>
        <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
        <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
        <input type="hidden" name="activityId" id="activityId"/>
        <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
        <input type="hidden" name="account" value="${command.getSearchId(account.id)}"/>
        <input type="hidden" name="result.payerBank" id="result.payerBank" value="${bank}"/>
        <input type="hidden" name="depositChannel" value="online"/>
        <div class="pay_mone">
            <%@include file="./ChooseAmount.jsp"%>
            <div class="depo_row">
                <div class="label">存款金额</div>
                <div class="input"><input id="result.rechargeAmount" name="result.rechargeAmount" type="text" placeholder="${onlinePayMin}.00~${onlinePayMax}.00"/></div>
                <div class="ext">
                    <input type="hidden" name="result.randomCash" value="${rechargeDecimals}"/>
                    <div class="cha">.${rechargeDecimals}</div>
                </div>
            </div>
            <div class="depo_row" data-rel='{"opType":"function","target":"showBankList"}'>
                <div class="label">支付银行</div>
                <div class="input">
                    <input id="selectBank"  type="text" value="${bankList[0]["text"]}" readonly>
                </div>
                <div class="ext">
                    <i class="mui-icon mui-icon-arrowdown"></i>
                </div>
                <input type="hidden" id="bankJson" value='${bankJson}' />
            </div>
        </div>
        <div class="btn_wrap">
            <a class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"confirmDeposit","payType":"online"}'>提交</a>
        </div>
        </form>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light" style="width:100%; text-align:center; padding-top: 20px;">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>