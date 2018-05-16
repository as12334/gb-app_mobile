<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(accounts)>0}">
        <div id="validateRule" style="display: none">${validateRule}</div>
        <gb:token/>
        <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
        <input type="hidden" name="randomValue" id="randomValue" value="${rechargeDecimals}">
        <input type="hidden" name="account" value="" id="account"/>
        <input type="hidden" name="channel" id="channel" value="${channel}"/>
        <div class="pay_mone">
            <div class="tit">选择账号</div>
            <div class="bank_list" id="scan_Bank_List">
                    <%--迭代页面返回的账号信息--%>
                <c:forEach items="${accounts}" var="i">
                    <div class="bank_list_i_w" href="#" data-rel='{"opType":"function","accountId":"${i.id}","target":"depositBitCoin.checkAccount"}'>
                        <div class="bank_list_i">
                            <i class="bank_n ${i.bankCode}"></i>
                            <div class="bank_n_txt">
                                    ${empty i.aliasName ?i.customBankName:i.aliasName}
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div class="btn_wrap">
            <a id="btn_electronicPay"  class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"depositBitCoin.nextStep"}'>提交</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>