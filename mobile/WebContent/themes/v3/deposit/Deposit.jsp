<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<html>
<head>
    <%@include file="../include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/swiper.min.css"/>
</head>
<body class="deposit">
<header class="mui-bar mui-bar-nav">
<%@ include file="../common/Assert.jsp" %>
</header>
<div class="mui-content mui-scroll-wrapper deposit-content">
    <div class="mui-scroll">
        <div class="mui-row">
            <div class="mui-input-group mine-form">
                <div class="bank-selector modes-payment">
                    <p>${views.deposit_auto['请选择支付方式']}</p>
                    <c:choose>
                        <c:when test="${fn:length(payAccountMap) > 0 || !empty digiccyAccountInfo}">
                            <div class="ct" id="payList">
                                <ul>
                                    <c:forEach items="${payAccountMap}" var="p">
                                        <c:if test="${p.key eq 'online_deposit'}">
                                            <li key="${p.key}"><a id="first" data-rel='{"payType":"online","url":"/wallet/deposit/online/index.html","opType":"function","target":"amountInput"}'
                                                    class="${fn:length(views.deposit[p.key])>5?'long':''}"><i class="pay ${payAccountMap.get(p.key)}"></i><span><div class="text-two-line">${views.deposit[p.key]}</div></span></a>
                                            </li>
                                        </c:if>

                                        <c:if test="${p.key eq 'scanPay'}">
                                            <c:forEach items="${p.value}" var="s">
                                                <c:set var="bankCode" value="${s.key}"/>
                                                <c:set var="bankName" value="${views.deposit[bankCode]}"/>
                                                <li key="${bankCode}"><a data-rel='{"payType":"scan","url":"/wallet/deposit/online/scan/scanCode/${bankCode}.html","opType":"function","target":"amountInput"}'
                                                                         class="${fn:length(bankName)>5?'long':''}"><i class="pay ${s.key}"></i><span><div class="text-two-line">${bankName}</div></span></a>
                                                </li>
                                            </c:forEach>
                                        </c:if>

                                        <c:if test="${p.key eq 'company_deposit'}">
                                            <c:forEach items="${p.value}" var="c">
                                                <li key="${command.getSearchId(c.value)}">
                                                    <a data-rel='{"payType":"company","url":"/wallet/deposit/company/depositCash.html?searchId=${command.getSearchId(c.value)}","opType":"function","target":"amountInput"}'
                                                     class="${fn:length(c.text)>5?'long':''}"><i class="pay ${c.bankCode}"></i><span><div class="text-two-line">${c.text}</div></span></a>
                                                </li>
                                            </c:forEach>
                                        </c:if>
                                        <c:if test="${p.key eq 'electronicPay'}">
                                            <c:forEach items="${p.value}" var="i">
                                                <li key="${command.getSearchId(i.id)}"><a data-rel='{"payType":"electronicPay","url":"/wallet/deposit/company/electronic/depositCash.html?searchId=${command.getSearchId(i.id)}","opType":"function","target":"amountInput"}'
                                                        class="${fn:length(isMultipleAccount ? i.aliasName : (i.rechargeType eq 'other_fast' ? i.customBankName : views.deposit[i.rechargeType]))>5 && isMultipleAccount ?'long':''}">
                                                    <i class="pay ${i.bankCode=='onecodepay'?'ymf':i.bankCode}"></i><span><div class="text-two-line">
                                                        ${isMultipleAccount ? i.aliasName : (i.rechargeType eq 'other_fast' ? i.customBankName : views.deposit[i.rechargeType])}</div></span></a>
                                                </li>
                                            </c:forEach>
                                        </c:if>
                                        <c:if test="${p.key eq 'isFastRecharge'}">
                                            <li key="${p.key}"><a data-rel='{"target":"${p.value}","opType":"href"}'
                                                                  class="long"><i class="pay ks"></i><span><div class="text-two-line">${views.deposit_auto['快速充值中心']}</div></span></a>
                                            </li>
                                        </c:if>
                                        <c:if test="${p.key eq 'bitcoin_fast'}">
                                            <li key="${p.key}"><a data-rel='{"target":"/wallet/deposit/company/bitcoin/index.html?searchId=${command.getSearchId(p.value.id)}","opType":"href"}'
                                                    class="long"><i class="pay bitcoin"></i><span><div class="text-two-line">${views.deposit_auto['比特币支付']}</div></span></a>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                    <c:if test="${not empty digiccyAccountInfo}">
                                        <li key="digiccyAccountInfo"><a data-rel='{"target":"/wallet/deposit/digiccy/index.html","opType":"href"}'
                                                class="long"><i class="pay digitalc"></i><span><div class="text-two-line">${views.themes_auto['数字货币支付']}</div></span></a>
                                        </li>
                                    </c:if>
                                </ul>
                                <div class="clearfix"></div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <div class="mui-row" id="depositInput"></div>
    </div>
    <div class="mui-row">
        <div class="gb-form-foot bank-pay-btn">
            <soul:button tag="a" precall="" callback="" target="nextStep" opType="function" text="${views.deposit_auto['下一步']}"
                         cssClass="mui-btn mui-btn-primary submit">
            </soul:button>
        </div>
    </div>
</div>

<%--线上支付选择银行--%>
<%--<div id="choose_bank" class="mui-popover mui-popover-action mui-popover-bottom gb-popover">
    <ul id="bankUl" class="mui-table-view">
    </ul>
</div>--%>

<!--存款帮助：-->
<%--微信反扫教程：--%>
<div id="depositHelpBox10" class="depositHelpBox" style="display: none">
    <div class="swiper-container">
        <div class="swiper-wrapper wechat-fansao">
            <div class="swiper-slide step-1"><img src="${resRoot}/images/wechat-step-1.png"/><div class="circle"></div></div>
            <div class="swiper-slide step-2"><img src="${resRoot}/images/wechat-step-2.png"/><div class="circle"></div></div>
            <div class="swiper-slide step-3"><img src="${resRoot}/images/wechat-step-3.png" /><div class="circle"></div></div>
        </div>
        <div class="swiper-pagination swiper-pagination-bullets"></div>
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
    </div>
</div>
<!--qq反扫教程：-->
<div id="depositHelpBox12" class="depositHelpBox" style="display:none;">
    <div class="swiper-container">
        <div class="swiper-wrapper qq-fansao" style="">
            <div class="swiper-slide step-1"><img src="${resRoot}/images/qq-step-1.png" alt="" /><div class="circle"></div></div>
            <div class="swiper-slide step-2"><img src="${resRoot}/images/qq-step-2.png" alt="" /><div class="circle"></div></div>
            <div class="swiper-slide step-3"><img src="${resRoot}/images/qq-step-3.png" alt="" /><div class="circle"></div></div>
        </div>
        <div class="swiper-pagination"></div>
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
    </div>
</div>
<%--支付宝反扫教程：--%>
<div id="depositHelpBox11" class="depositHelpBox" style="display:none;">
    <div class="swiper-container">
        <div class="swiper-wrapper alipay-fansao" style="">
            <div class="swiper-slide step-1"><img src="${resRoot}/images/alipay-step-1.png" alt="" /><div class="circle"></div></div>
            <div class="swiper-slide step-2"><img src="${resRoot}/images/alipay-step-2.png" alt="" /><div class="circle"></div></div>
        </div>
        <div class="swiper-pagination"></div>
        <a data-rel='{"opType":"function","target":"closeHelpBox"}' class="closeHelpBox">+</a>
    </div>
</div>

</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/swiper.min.js"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js"></script>
<script src="${resRoot}/js/deposit/OnlinePay.js"></script>
<script src="${resRoot}/js/deposit/CompanyDeposit.js"></script>
</html>
