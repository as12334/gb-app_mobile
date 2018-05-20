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
                                <input type="hidden" depositChannel="scan"  rechargeType="${i.rechargeType}" accountType="${i.accountType}" randomAmount="${i.randomAmount}" onlinePayMax="${i.singleDepositMax==null?99999999:i.singleDepositMax}" onlinePayMin="${i.singleDepositMin==null?0.01:i.singleDepositMin}" account="${command.getSearchId(i.id)}" payerBank="${i.bankCode}" />
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
                                            ${empty i.aliasName ? (empty i.customBankName ? views.deposit[i.rechargeType]:i.customBankName):i.aliasName}
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
                        <label for="result.payerBankcard" style="display: none;">${views.deposit_auto['授权码']}</label>
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
            <div class="deposit_help">
                <p>温馨提示</p>
                <c:if test="${channel!='easypay'}">
                    <p class="scan_code_random" style="display: none">* 为了提高对账速度及成功率，当前支付方式已开随机额度，请输入整数存款金额，将随机增加0.11~0.99元！</p>
                    <p class="scan_code" style="display: none">* 支付成功后,请等待几秒钟,提示[支付成功]按确认后再关闭支付窗口。</p>
                    <p class="scan_code" style="display: none">* 如出现充值失败或充值后未到账等情况，请联系在线客服获取 帮助。<a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>点击联系在线客服</a></p>
                    <p class="electronic">* 存款金额请加小数点或尾数,以便区分.如充值200元,请输入201或200.1之类小数.。</p>
                </c:if>
                <c:if test="${channel=='easypay'}">
                    <p>* 当前支付额度必须精确到小数点，请严格核对您的转账金额精确到分，如：100.51，否则无法提高对账速度及成功率，谢谢您的配合。</p>
                </c:if>
            </div>
    </c:when>
    <c:otherwise>
        <div class="ct">
            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
        </div>
    </c:otherwise>
</c:choose>