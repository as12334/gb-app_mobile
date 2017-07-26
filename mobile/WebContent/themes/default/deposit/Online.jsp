<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(payAccountMap)>0}">
        <form id="onlineForm" onsubmit="return false">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <c:set var="bank" value="${bankList[0]['value']}"/>
            <c:set var="account" value="${payAccountMap[bank]}"/>
            <c:set var="onlinePayMin" value="${empty account.singleDepositMin?1:account.singleDepositMin}"/>
            <c:set var="onlinePayMax" value="${empty account.singleDepositMax?99999999:account.singleDepositMax}"/>
            <input type="hidden" name="onlinePayMin" id="onlinePayMin" value="${onlinePayMin}"/>
            <input type="hidden" name="onlinePayMax" id="onlinePayMax" value="${onlinePayMax}"/>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <gb:token/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label>${views.deposit_auto['选择您所使用的银行']}</label>
                    <div class="ct" id="selectBank">
                        <p class="text-gray-light gb-select">
                            <a id="selectText" style="margin-right: 0px">
                                ${bankList[0]["text"]}
                            </a>
                        </p>
                        <input type="hidden" name="result.payerBank" id="result.payerBank" value="${bank}"/>
                    </div>
                </div>

                <div class="mui-input-row">
                    <label>${views.deposit_auto['金额']}</label>
                    <div class="ct">
                        <input type="number" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off"/>
                        <p class="fee text-gray-light" id="fee" style="display: none"></p>
                    </div>
                </div>

                <%@include file="./ChooseAmount.jsp"%>

            </div>
            <div class="mui-row">
                <div class="gb-form-foot" style="padding-bottom:10px;">
                    <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount" disabled="disabled">${views.deposit_auto['提交']}</button>
                </div>
            </div>
            <input type="hidden" id="bankJson" value='${bankJson}' />
        </form>

    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light" style="width:100%; text-align:center; padding-top: 20px;">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>
