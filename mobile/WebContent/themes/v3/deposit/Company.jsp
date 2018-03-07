<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<head>
    <%@include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.poppicker.css">
</head>

<body class="exchange-bank">
<header class="mui-bar mui-bar-nav">
    <a class="mui-icon mui-icon mui-icon-left-nav mui-pull-left mui-action-back"></a>
    <h1 class="mui-title">${views.deposit_auto['银行卡转账']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <form id="confirmCompanyForm">
            <c:choose>
                <c:when test="${not empty payAccount}">
                    <div id="validateRule" style="display: none">${validateRule}</div>
                    <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                    <input type="hidden" id="rechargeTypeJson" value='${rechargeTypeJson}' />
                    <input type="hidden" name="depositChannel" value="company"/>
                    <gb:token/>
                    <div id="wechat1" class="mui-control-content mui-active">
                        <div class="mui-row">
                            <div class="gb-panel" style="border-top-color: #fff;">
                                <div class="gb-bankcard ">
                                    <div class="hd">
                                        <c:set var="isOther" value="${payAccount.bankCode=='other_bank'}"/>
                                        <p><span class="mui-pull-right">${views.deposit_auto['储蓄卡']}</span>
                                            <c:choose>
                                                <c:when test="${isOther}">
                                                    <span style="margin-right: -30px;">${payAccount.customBankName}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="pay-bank ${payAccount.bankCode}" style="margin-right: -30px;"></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                    <div class="ct">
                                        <p>
                                        <c:choose>
                                            <c:when test="${isHide}">
                                                <span class="text-green">${account.code}&nbsp;&nbsp;</span>
                                                <a open-href="${root}/index/gotoCustomer.html">${hideContent.value}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-green">${soulFn:formatBankCard(payAccount.account)}</span><%--data-rel='{"opType":"function","target":"copy"}'--%>
                                                <a href="#" class="copy" data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                                            </c:otherwise>
                                            </c:choose>
                                        </p>
                                        <div class="ct">
                                            <p><span style="float:left;">${views.deposit_auto['银行开户名']}: </span>
                                                    ${payAccount.fullName}
                                                <a href="#" class="copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
                                            </p>
                                            <p style="margin-top:3px;"><span style="float:left;">${views.deposit_auto['开户行']}: </span>
                                                    ${payAccount.openAcountName}
                                                <a href="#" class="copy" data-clipboard-text="${payAccount.openAcountName}">${views.themes_auto['复制']}</a>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="mui-input-group mine-form m-t-sm">
                                    <div class="mui-input-row"><label>${views.deposit_auto['存款类型']}</label>
                                        <div class="ct" id="rechargeType">
                                            <div class="gb-select">
                                                <input type="text" data-rel='{"opType":"function","target":"showPayTypeList","rechargeTypeValue":"${rechargeType.value}"}' readOnly="readonly" id="rechargeTypeText" id="payTypeText" placeholder=" ${rechargeType.text}"/>
                                                <input type="hidden" name="result.rechargeType" id="result.rechargeType" value="${rechargeType.value}"/>
                                            </div>
                                        </div>
                                    </div>
                                <div class="mui-input-row" style="display:${rechargeType.value ne 'atm_money'?'block':'none'}" id="payerName">
                                    <label for="result.payerName">${views.deposit_auto['存款人']}</label>
                                    <div class="ct">
                                        <input type="text" id="result.payerName" name="result.payerName" placeholder="${views.deposit_auto['您转账时使用的银行卡姓名']}" onchange="this.value=this.value.trim()"/>
                                    </div>
                                </div>
                                 <%--存款金额--%>
                                <input type="hidden" name="result.rechargeAmount" id="result.rechargeAmount" value="${rechargeAmount}"/>
                                <div class="mui-input-row" style="display:${rechargeType.value eq 'atm_money'?'block':'none'}" id="address">
                                    <label for="result.rechargeAddress">${views.deposit_auto['交易地点']}</label>
                                    <div class="ct">
                                        <input type="text" id="result.rechargeAddress" name="result.rechargeAddress" placeholder="${views.deposit_auto['请输入地点']}" onchange="this.value=this.value.trim()"/>
                                    </div>
                                </div>
                                <input type="hidden" name="activityId" id="activityId"/>
                            </div>
                        </div>
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
<div class="mui-row">
    <div class="gb-form-foot bank-pay-btn">
        <a data-rel='{"opType":"function","target":"seachDiscount"}' class="mui-btn mui-btn-primary submit">${views.deposit_auto['提交']}</a>
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
            <a class="btn mui-btn mui-btn-outlined" data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto["再存一次"]}</a>
            <a data-rel='{"target":"goToHome","opType":"function"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.js"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js"></script>
<script src="${resComRoot}/js/dist/clipboard.js"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js"></script>
<script src="${resRoot}/js/deposit/CompanyDeposit.js"></script>


