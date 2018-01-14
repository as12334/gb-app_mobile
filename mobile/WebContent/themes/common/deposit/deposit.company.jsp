<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page no-backdrop">
<div id="offCanvasWrapper" class="mui-draggable"><%-- mui-off-canvas-wrap --%>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.deposit_auto['银行卡转账']}</h1>
        </header>
        <form id="confirmCompanyForm">
            <div class="mui-content mui-scroll-wrapper deposit-scroll-wrapper mui-scroll2" ${os eq 'android'?'style="padding-top:0!important"':''}>
                <c:choose>
                    <c:when test="${not empty payAccount}">
                        <div class="mui-scroll">
                            <gb:token/>
                            <%--<c:set var="onlinePayMin" value="${empty rank.onlinePayMin?1:rank.onlinePayMin}"/>
                            <c:set var="onlinePayMax" value="${empty rank.onlinePayMax?99999999:rank.onlinePayMax}"/>
                            <input type="hidden" name="onlinePayMin" value="${onlinePayMin}"/>
                            <input type="hidden" name="onlinePayMax" value="${onlinePayMax}"/>
                            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>--%>
                            <div id="validateRule" style="display: none">${validateRule}</div>
                            <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                            <input type="hidden" id="rechargeTypeJson" value='${rechargeTypeJson}' />
                            <div class="mui-row">
                                <div class="gb-panel">
                                    <div class="gb-bankcard">
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
                                                        ${views.deposit_auto['账号代码']}:
                                                        <span class="text-blue">${account.code}&nbsp;&nbsp;</span>

                                                        <a open-href="${root}/index/gotoCustomer.html">${hideContent.value}</a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${soulFn:formatBankCard(payAccount.account)}
                                                        <a href="#" class="copy" data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                            <div class="ct">
                                                <p><span>${views.deposit_auto['银行开户名']}:</span>
                                                        ${payAccount.fullName}
                                                    <a href="#" class="copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
                                                </p>
                                                <p><span>${views.deposit_auto['开户行']}:</span>
                                                        ${payAccount.openAcountName}
                                                    <a href="#" id="copyAcountName" class="copy" data-clipboard-text="${payAccount.openAcountName}">${views.themes_auto['复制']}</a>
                                                    <input type="hidden" value="${payAccount.openAcountName}" data-clipboard-text="${payAccount.openAcountName}" id="hidenAccountName">
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${!empty payAccount.remark}">
                                    <div class="front-end">
                                        <pre>${payAccount.remark}</pre>
                                    </div>
                                </c:if>
                            </div>

                            <div class="mui-row">
                                <div class="mui-input-group mine-form m-t-sm">

                                    <%--<div class="mui-input-row">
                                        <label>${views.deposit_auto['金额']}</label>
                                        <div class="ct">
                                            <input type="text" placeholder="${siteCurrencySign}${soulFn:formatCurrency(onlinePayMin)}~${siteCurrencySign}${soulFn:formatCurrency(onlinePayMax)}"
                                                   name="result.rechargeAmount" id="result.rechargeAmount" autocomplete="off"/>
                                        </div>
                                    </div>

                                    <%@include file="./ChooseAmount.jsp"%>--%>
                                    <%--存款金额--%>
                                    <input type="hidden" name="result.rechargeAmount" id="result.rechargeAmount" value="${rechargeAmount}"/>

                                    <div class="mui-input-row"><label>${views.deposit_auto['存款类型']}</label>
                                        <div class="ct" id="rechargeType">
                                            <div class="gb-select">
                                                <a class="mui-btn mui-btn-link gb-input-link" id="rechargeTypeText" style="margin-right: 0">
                                                        ${rechargeType.text}
                                                </a>
                                                <input type="hidden" name="result.rechargeType" id="result.rechargeType" value="${rechargeType.value}"/>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="mui-input-row" style="display:${rechargeType.value eq 'atm_money'?'block':'none'}" id="address">
                                        <label>${views.deposit_auto['交易地点']}</label>
                                        <div class="ct">
                                            <input type="text" name="result.rechargeAddress" placeholder="${views.deposit_auto['请输入地点']}" onchange="this.value=this.value.trim()">
                                        </div>
                                    </div>

                                    <div class="mui-input-row" style="display:${rechargeType.value ne 'atm_money'?'block':'none'}" id="payerName">
                                        <label>${views.deposit_auto['存款人']}</label>
                                        <div class="ct">
                                            <input type="text" name="result.payerName" placeholder="${views.deposit_auto['您转账时使用的银行卡姓名']}" onchange="this.value=this.value.trim()">
                                        </div>
                                    </div>

                                    <%--<div class="mui-input-row" id="captcha">
                                            <div class="form-row">
                                                <div class="cont">
                                                    <input type="text" class="mui-input ico6" maxlength="4" placeholder="${views.passport_auto['请输入验证码']}" name="captcha" id="captcha">
                                                    <div class="gb-vcode">
                                                        <img class="_captcha_img" src="${root}/captcha/code.html" data-src="${root}/captcha/code.html" alt="">
                                                    </div>
                                                </div>
                                            </div>
                                    </div>--%>

                                </div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-form-foot bank-pay-btn">
                                <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount">${views.deposit_auto['提交']}</button>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="mui-scroll2">
                            <div class="mui-row">${views.deposit_auto['请确认是否有收款账号']}</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </form>
    </div>
</div>
<script type="text/javascript" src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script>
    curl(['site/deposit/Company'], function (Page) {
        page = new Page();
    });

</script>
</body>

