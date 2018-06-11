<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<head>
    <%@include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.poppicker.css">
</head>

<body class="deposit_2">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.deposit_auto['银行卡转账']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper deposit-2-content">
    <div class="mui-scroll">
        <c:if test="${activityHall==true}">
            <div class="deposit_tips">温馨提示：完成存款后，请前往活动大厅申请活动优惠。</div>
        </c:if>
        <form id="rechargeForm" onsubmit="return false">
            <c:choose>
                <c:when test="${not empty payAccount}">
                    <div id="validateRule" style="display: none">${validateRule}</div>
                    <input type="hidden" name="account" id="account" value="${command.getSearchId(payAccount.id)}"/>
                    <input type="hidden" id="rechargeTypeJson" value='${rechargeTypeJson}'/>
                    <input type="hidden" name="depositChannel" value="company"/>
                    <input type="hidden" name="result.rechargeAmount" id="result.rechargeAmount"
                           value="${rechargeAmount}"/>
                    <input type="hidden" name="activityId" id="activityId"/>
                    <input type="hidden" name="channel" value="${channel}"/>
                    <gb:token/>
                    <div class="pay_mone">
                        <div class="tit">账号信息</div>
                        <c:set var="isOther" value="${payAccount.bankCode=='other_bank'}"/>
                        <div class="bank_car_item">
                            <div class="top">
                                <span class="${isOther?'':'pay-bank '} ${isOther?'':payAccount.bankCode}"></span>
                                <span class="pay-txt" style="margin-right: -30px;">${payAccount.customBankName}</span>
                                <span class="car_type">${views.deposit_auto['储蓄卡']}</span>
                            </div>
                            <div class="bank_car_txt_info">
                                <div class="b_c_t_i_row">
                                    <c:choose>
                                        <c:when test="${isHide}">
                                            <span class="text-green">${account.code}&nbsp;&nbsp;</span>
                                            <a href="javascript:"
                                               data-rel='{"target":"loadCustomer","opType":"function" }'>${hideContent.value}</a>
                                        </c:when>
                                        <c:otherwise>
                                            ${soulFn:formatBankCard(payAccount.account)}
                                            <a href="#" class="btn_copy copy"
                                               data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="b_c_t_i_row"> ${views.deposit_auto['银行开户名']}： ${payAccount.fullName}<a
                                        href="#" class="btn_copy copy"
                                        data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
                                </div>
                                <div class="b_c_t_i_row"> ${views.deposit_auto['开户行']}： ${payAccount.openAcountName}<a
                                        href="#" class="btn_copy copy"
                                        data-clipboard-text="${payAccount.openAcountName}">${views.themes_auto['复制']}</a>
                                </div>
                            </div>

                        </div>
                        <c:choose>
                            <c:when test="${channel=='company'}">
                                <div class="depo_row">
                                    <div class="label">存款类型</div>
                                    <div class="input">
                                        <input class="text-right" type="text" placeholder="网银存款" readonly/>
                                        <input type="hidden" name="result.rechargeType" id="result.rechargeType"
                                               value="online_bank"/>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="depo_row"
                                     data-rel='{"opType":"function","target":"depositCompany.checkRechargeType"}'>
                                    <div class="label">存款类型</div>
                                    <div class="input">
                                        <input class="text-right" id="selectRecharge" type="text" value="柜员机现金存款"
                                               readonly>
                                        <input type="hidden" name="result.rechargeType" id="result.rechargeType"
                                               value="atm_money"/>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="depo_row">
                            <div class="label">存款人</div>
                            <div class="input"><input class="text-right" id="result.payerName" name="result.payerName"
                                                      type="text" placeholder="转账账号对应的姓名"></div>
                        </div>
                        <c:if test="${channel!='company'}">
                            <div class="depo_row">
                                <div class="label">${views.deposit_auto['交易地点']}</div>
                                <div class="input"><input class="text-right" type="text" name="result.rechargeAddress"
                                                          placeholder="${views.deposit_auto['请输入地点']}" maxlength="30"
                                                          onchange="this.value=this.value.trim()"></div>
                            </div>
                        </c:if>
                    </div>
                    <div class="btn_wrap">
                        <a data-rel='{"opType":"function","target":"baseDeposit.activity"}'
                           class="mui-btn btn_submit mui-btn-block">${views.deposit_auto['提交']}</a>
                    </div>
                    <div class="deposit_help">
                        <p>温馨提示</p>
                        <c:if test="${channel=='company'}">
                            <p>• 先查看要入款的银行账号信息,然后通过网上银行或者手机银行进行转账,转账成功后再如实提交转账信息,财务专员查收到信息后会及时添加您的款项.</p>
                        </c:if>
                        <c:if test="${channel=='counter'}">
                            <p>• 先查看要入款的银行账号信息,然后通过ATM、柜台转账,转账成功后再如实提交转账信息,财务专员查收到信息后会及时添加您的款项.</p>
                        </c:if>
                        <p>• 请尽可能选择同行办理转账,可快速到账。</p>
                        <p>• 存款完成后保留单据以利核对并确保您的权益。</p>
                        <p>• 如出现充值失败或充值后未到账等情况，请联系在线客服获取帮助。<a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>点击联系在线客服</a></p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="mui-scroll main-contents">
                        <div class="mui-row">${views.deposit_auto['请确认是否有收款账号']}</div>
                    </div>
                </c:otherwise>
            </c:choose>
        </form>
    </div>
</div>
<!--弹窗-->
<div class="masker" id="successMasker" style="display: none;"></div> <!--遮罩-->
<div class="gb-withdraw-box window-ok" style="display:none">
    <div class="cont">
        <div class="ok-box">
            <i class="ok-icon"></i>
            <span>${views.deposit_auto["提交成功"]}</span>
        </div>
        <div class="ct">
            <p>${views.deposit_auto["等待处理"]}
            </p>
        </div>
        <div class="ft">
            <a class="btn mui-btn mui-btn-outlined"
               data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto["再存一次"]}</a>
            <a data-rel='{"target":"goToHomePageOnly","opType":"function"}'
               class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/BaseDeposit.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCompany.js?v=${rcVersion}"></script>


