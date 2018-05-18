<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(accounts)>0}">
            <div id="validateRule" style="display: none">${validateRule}</div>
            <gb:token/>

            <input type="hidden" id="onlinePayMax" value="" name="onlinePayMax"/>
            <input type="hidden" id="onlinePayMin" value="" name="onlinePayMin"/>
            <input type="hidden" id="siteCurrencySign" value="${siteCurrencySign}"/>
            <input type="hidden" name="randomValue" id="randomValue" value="${rechargeDecimals}">
            <input type="hidden" name="isAuthCode" id="isAuthCode" value=""/>
            <input type="hidden" name="accountType" id="accountType" value=""/>

            <input type="hidden" name="activityId" id="activityId"/>
            <input type="hidden" name="account" value="" id="account"/>
            <input type="hidden" name="result.payerBank" value="" id="result.payerBank"/>
            <input type="hidden" name="depositChannel" id="depositChannel" value=""/>
            <input type="hidden" name="result.rechargeType" id="rechargeType" value=""/>
            <input type="hidden" name="channel" id="channel" value="${channel}"/>

            <div class="pay_mone">
                <div class="tit">存款方式</div>
                <div class="bank_list" id="scan_Bank_List">
                        <%--迭代页面返回的账号信息--%>
                    <c:forEach items="${accounts}" var="i">
                        <c:if test="${i.scanType=='sacn'}">
                            <div class="bank_list_i_w" href="#" data-rel='{"opType":"function","target":"depositScanCode.checkAccount"}'>
                                <input type="hidden" depositChannel="scan"  rechargeType="${i.rechargeType}" accountType="${i.accountType}" randomAmount="${i.randomAmount}" onlinePayMax="${i.singleDepositMax}" onlinePayMin="${i.singleDepositMin}" account="${command.getSearchId(i.id)}" payerBank="${i.bankCode}" />
                                <div class="bank_list_i">
                                    <i class="bank_n ${channel}"></i>
                                    <div class="bank_n_txt">${views.deposit[i.bankCode]}支付</div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${i.scanType=='electroin'}">
                            <div class="bank_list_i_w" href="#" data-rel='{"opType":"function","target":"depositScanCode.checkAccount"}'>
                                <input type="hidden" depositChannel="electronic" rechargeType="${i.rechargeType}"  accountType="0" randomAmount="false" onlinePayMax="${rank.onlinePayMax}" onlinePayMin="${rank.onlinePayMin}" account="${command.getSearchId(i.id)}" payerBank="${i.bankCode}" />
                                <div class="bank_list_i">
                                    <i class="bank_n ${channel}"></i>
                                    <div class="bank_n_txt">
                                            ${empty i.aliasName ? dicts.common.bankname[i.bankCode]:i.aliasName}
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
                <%@include file="../ChooseAmount.jsp"%>
                <div class="depo_row">
                    <div class="label">存款金额</div>
                    <div class="input"><input id="result.rechargeAmount" name="result.rechargeAmount" type="text" placeholder="${siteCurrencySign}0.01-${siteCurrencySign}9999999.00"/></div>
                    <div class="ext" id="random_amount" style="display: none">
                        <input type="hidden" name="result.randomCash" value="${rechargeDecimals}"/>
                        <div class="cha">.${rechargeDecimals}</div>
                    </div>
                </div>
                <div style="display: none;" id="reverseSacn_div">
                    <div class="depo_row">
                        <div class="label">${views.deposit_auto['授权码']}</div>
                        <div class="input">
                            <input type="text" placeholder="${views.deposit_auto['请输入12~20位授权码']}" name="result.payerBankcard" id="result.payerBankcard" autocomplete="off" style="display:inline-block;width:50%;text-align: right;float:right;height:40px;padding-right:12px;"/>
                        </div>
                    </div>
                    <div class="depo_row">
                        <a href="#" data-rel='{"opType":"function","target":"depositScanCode.showReScanCourse"}'>${views.deposit_auto['获取授权码教程']}?</a>
                    </div>
                </div>
            </div>
            <div class="btn_wrap">
                <a id="btn_default" class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"depositScanCode.defBtnTap","payType":"scan"}'>提交</a>
                <a id="btn_scan" style="display: none"  class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"baseDeposit.activity","payType":"scan"}'>提交</a>
                <a id="btn_electronicPay" style="display: none" class="mui-btn btn_submit mui-btn-block" data-rel='{"opType":"function","target":"baseDeposit.nextStep","payType":"electronicPay"}'>提交</a>
            </div>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>