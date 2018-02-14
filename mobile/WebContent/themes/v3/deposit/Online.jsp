<%--@elvariable id="command" type="so.wwb.gamebox.model.master.content.vo.PayAccountListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(payAccountMap)>0}">
        <form id="onlineForm" onsubmit="return false">
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
            <input type="hidden" name="depositChannel" value="online"/>
            <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
            <input type="hidden" name="account" value="${command.getSearchId(account.id)}"/>
            <gb:token/>
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <label>${views.deposit_auto['选择您所使用的银行']}</label>
                    <div class="ct" id="selectBank">
                        <p class="text-gray-light gb-select">
                            <a id="selectText" data-rel='{"opType":"function","target":"showBankList"}' style="margin-right: 0px">
                                    ${bankList[0]["text"]}
                            </a>
                        </p>
                        <input type="hidden" name="result.payerBank" id="result.payerBank" value="${bank}"/>
                    </div>
                </div>
                <div class="mui-input-row"><label for="result.rechargeAmount" style="width:20%">${views.deposit_auto['存款金额']}</label>
                    <p class="text-gray-light">
                        <c:if test="${account.randomAmount eq true}">
                            <% int randomCash = (int)(Math.random()*88+11);%>
                            <input type="hidden" name="result.randomCash" value="<%=randomCash%>">
                            <span style=" display: inline-block; height: 39px;float:right;margin:1px 10px 0 0;  background: #eee;  padding: 0 4px;  vertical-align: -1px;  color: #333;">.<%=randomCash%></span>
                        </c:if>
                        <input type="text" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:12px;">
                    </p>
                </div>
                <%@include file="./ChooseAmount.jsp"%>
                <!--随机额度提示-->
                <c:if test="${account.randomAmount eq true}">
                    <p  class="info" name="randomAmountMsg">${views.deposit_auto['随机额度提示']}</p>
                </c:if>
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
