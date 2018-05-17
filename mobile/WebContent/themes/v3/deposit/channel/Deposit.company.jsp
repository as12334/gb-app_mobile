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
            <input type="hidden" name="result.rechargeType" value="online_bank"/>
            <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
            <input type="hidden" name="onlinePayMin" id="onlinePayMin" value="0.01"/>
            <input type="hidden" name="onlinePayMax" id="onlinePayMax" value="9999999"/>
            <input type="hidden" name="account" id="account" value=""/>
            <input type="hidden" name="result.payerBank" id="result.payerBank" value=""/>
            <input type="hidden" name="bankCode" value="" id="bankCode"/>
            <input type="hidden" name="channel" id="channel" value="${channel}"/>
            <div class="pay_mone">
                <div class="tit">选择银行</div>
                <div class="bank_list" id="company_bank_list">
                    <c:forEach items="${accounts}" var="i" varStatus="vs">
                        <div class="bank_list_i_w"
                             data-rel='{"bankCode":"${i.bankCode}","accountId":"${command.getSearchId(i.id)}","opType":"function","target":"depositCompany.checkAccount"}'>
                            <div class="bank_list_i">
                                <i class="bank_n ${i.bankCode}"></i>
                                <div class="bank_n_txt">
                                     ${empty i.aliasName ? dicts.common.bankname[i.bankCode]:i.aliasName}
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
                <%@include file="../ChooseAmount.jsp" %>
                <div class="depo_row">
                    <div class="label">存款金额</div>
                    <div class="input"><input id="result.rechargeAmount" name="result.rechargeAmount" type="text"
                                              placeholder="${siteCurrencySign}${soulFn:formatCurrency(0.01)}~${siteCurrencySign}${soulFn:formatCurrency(9999999)}"/>
                    </div>
                    <div class="ext" style="display: none">
                        <input type="hidden" name="result.randomCash" value="${rechargeDecimals}"/>
                        <div class="cha">.${rechargeDecimals}</div>
                    </div>
                </div>
            </div>
            <div class="btn_wrap">
                <a class="mui-btn btn_submit mui-btn-block"
                   data-rel='{"opType":"function","target":"baseDeposit.nextStep","payType":"online"}'>提交</a>
            </div>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light"
               style="width:100%; text-align:center; padding-top: 20px;">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>
