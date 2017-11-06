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
            <c:set var="onlinePayMin" value="${empty payAccountForScan.singleDepositMin?0.01:payAccountForScan.singleDepositMin}"/>
            <input type="hidden" class="onlinePayMax" value="${onlinePayMax}" name="onlinePayMax"/>
            <input type="hidden" class="onlinePayMin" value="${onlinePayMin}" name="onlinePayMin"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label>${views.deposit_auto['存款账号']}</label>
                    <div class="ct">
                        <p class="text-gray-light">${username}</p>
                    </div>
                </div>
                <div class="mui-input-row">
                    <label>${views.deposit_auto['金额']}</label>
                    <div class="ct">
                        <input type="number" value="" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off"/>
                    </div>
                </div>
                <!--随机额度提示-->
                <c:if test="${payAccountForScan.randomAmount eq true}">
                <div class="mui-input-row" id="randomAmountMsg">
                    <marquee scrollamount="5" direction="left" >
                        <input style="width: 550px" type="randomAmountMsg"  name="randomAmountMsg" value="为了提高对账速度及成功率,当前支付方式已开启随机额度，整数存款金额将随机增加0.11~0.99元！" disabled/>
                    </marquee>
                </div>
                </c:if>
                <%@include file="./ChooseAmount.jsp"%>
            </div>
            <div class="mui-row">
                <div class="gb-form-foot" style="padding-bottom:10px;">
                    <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount" disabled="disabled">${views.deposit_auto['提交']}</button>
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