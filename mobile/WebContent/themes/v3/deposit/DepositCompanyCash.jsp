<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${(fn:length(accounts)>0)}">
        <form id="companyCashForm" onsubmit="return false">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <gb:token/>
            <c:set var="onlinePayMin" value="${empty rank.onlinePayMin || rank.onlinePayMin <= 0 ?0.01:rank.onlinePayMin}"/>
            <c:set var="onlinePayMax" value="${empty rank.onlinePayMax?99999999:rank.onlinePayMax}"/>
            <input type="hidden" name="onlinePayMin" id="onlinePayMin" value="${onlinePayMin}"/>
            <input type="hidden" name="onlinePayMax" id="onlinePayMax" value="${onlinePayMax}"/>
            <%--<input type="hidden" name="result.payerBank" value="${bankCode}"/>--%>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <input type="hidden" name="depositChannel" value="company"/>
            <input type="hidden" name="statusNum" value="1"/>
            <input type="hidden" name="bankCode" value="" id="bankCode"/>
            <input type="hidden" name="accountId" value="" id="accountId"/>
            <div class="pay_mone">
                <div class="tit">选择银行</div>
                <div class="bank_list" id="company_bank_list">
                    <c:forEach items="${accounts}" var="i" varStatus="vs">
                    <div class="bank_list_i_w" data-rel='{"bankCode":"${i.bankCode}","accountId":"${i.id}","opType":"function","target":"selectCompanyBank"}'>
                        <div class="bank_list_i">
                            <i class="bank_n ${i.bankCode}"></i>
                            <div class="bank_n_txt">
                                ${dicts.common.bankname[i.bankCode]}
                            </div>
                        </div>
                    </div>
                    </c:forEach>
                </div>
                <div class="tit">选择金额</div>
                <div class="conv_mone">
                    <div class="conv_mone_i">
                        <i class="icon_mone mone_100" data-rel='{"mone":100,"opType":"function","target":"quickCheckMoney"}'></i>
                    </div>
                    <div class="conv_mone_i">
                        <i class="icon_mone mone_200" data-rel='{"mone":200,"opType":"function","target":"quickCheckMoney"}'></i>
                    </div>
                    <div class="conv_mone_i">
                        <i class="icon_mone mone_500" data-rel='{"mone":500,"opType":"function","target":"quickCheckMoney"}'></i>
                    </div>
                    <div class="conv_mone_i">
                        <i class="icon_mone mone_1000" data-rel='{"mone":1000,"opType":"function","target":"quickCheckMoney"}'></i>
                    </div>
                    <div class="conv_mone_i">
                        <i class="icon_mone mone_5000" data-rel='{"mone":5000,"opType":"function","target":"quickCheckMoney"}'></i>
                    </div>
                </div>
                <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row"><label for="result.rechargeAmount" style="width:20%">${views.deposit_auto['存款金额']}</label>
                    <p class="text-gray-light">
                        <input type="text" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                               name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:12px;">
                        <input type="hidden" id="result.randomCash" name="result.randomCash" value="0"/>
                    </p>
                </div>
                </div>
            </div>
            <div class="btn_wrap">
                <a class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"nextStep","payType":"company"}'>提交</a>
            </div>
        </form>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>


