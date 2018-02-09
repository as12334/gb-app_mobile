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
            <input type="hidden" name="statusNum" value="1"/>
            <input type="hidden" name="depositChannel" value="electronic"/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row"><label for="result.rechargeAmount" style="width:20%">${views.deposit_auto['存款金额']}</label>
                    <p class="text-gray-light">
                        <input type="text" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:12px;">
                    </p>
                </div>
                <%@include file="./ChooseAmount.jsp"%>
            </div>
        </form>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>
