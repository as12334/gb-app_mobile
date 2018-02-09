<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<body class="gb-theme mine-page no-backdrop" >
<div id="offCanvasWrapper" class="mui-draggable mui-off-canvas-wrap">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <c:if test="${os ne 'app_ios'}">
                    <%@ include file="/include/include.toolbar.jsp" %>
                </c:if>
                <h1 class="mui-title">${views.deposit_auto['存款']}</h1>
                <c:if test="${!isLotterySite}">
                    <%@ include file="/themes/default/include/include.asset.jsp" %>
                </c:if>
            </header>
        </c:if>
        <div class="mui-content mui-scroll-wrapper deposit-scroll-wrapper main-contents" style="${os eq 'android'?'padding-top:0!important;':''}">
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="mui-input-group mine-form">
                        <div class="bank-selector modes-payment">
                            <p>${views.deposit_auto['请选择支付方式']}</p>
                            <c:choose>
                                <c:when test="${fn:length(payAccountMap) > 0 || !empty digiccyAccountInfo}">
                                    <div class="ct">
                                        <ul id="depositWay">
                                            <c:forEach items="${payAccountMap}" var="p">
                                                <c:if test="${p.key eq 'online_deposit'}">
                                                    <li key="${p.key}"><a data-online="/wallet/deposit/online/index.html" class="${fn:length(views.deposit[p.key])>5?'long':''}"><i class="pay ${payAccountMap.get(p.key)}"></i><span><div class="text-two-line">${views.deposit[p.key]}</div></span></a></li>
                                                </c:if>
                                                <c:if test="${p.key eq 'scanPay'}">
                                                    <c:forEach items="${p.value}" var="s">
                                                        <c:set var="bankCode" value="${s.key}"/>
                                                        <c:set var="bankName" value="${views.deposit[bankCode]}"/>
                                                        <li key="${bankCode}"><a data-scan="/wallet/deposit/online/scan/scanCode/${bankCode}.html" class="${fn:length(bankName)>5?'long':''}"><i class="pay ${s.key}"></i><span><div class="text-two-line">${bankName}</div></span></a></li>
                                                    </c:forEach>
                                                </c:if>

                                                <c:if test="${p.key eq 'company_deposit'}">
                                                    <c:forEach items="${p.value}" var="c">
                                                        <li key="${command.getSearchId(c.value)}"><a data-company="/wallet/deposit/company/index.html?searchId=${command.getSearchId(c.value)}" class="${fn:length(c.text)>5?'long':''}"><i class="pay ${c.bankCode}"></i><span><div class="text-two-line">${c.text}</div></span></a></li>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${p.key eq 'electronicPay'}">
                                                    <c:forEach items="${p.value}" var="i">
                                                        <li key="${command.getSearchId(i.id)}">
                                                            <c:choose>
                                                                <c:when test="${isMultipleAccount}">
                                                                    <a data-fast="/wallet/deposit/company/electronic/index.html?searchId=${command.getSearchId(i.id)}" class="${fn:length(i.aliasName)>5?'long':''}"><i class="pay ${i.bankCode=='onecodepay'?'ymf':i.bankCode}"></i><span><div class="text-two-line">
                                                                            ${i.aliasName}
                                                                    </div></span></a>
                                                                </c:when>
                                                                <c:when test="${i.rechargeType eq 'other_fast'}">
                                                                    <a data-fast="/wallet/deposit/company/electronic/index.html?searchId=${command.getSearchId(i.id)}" class="${fn:length(i.customBankName)>5?'long':''}"><i class="pay ${i.bankCode=='onecodepay'?'ymf':i.bankCode}"></i><span><div class="text-two-line">
                                                                            ${i.customBankName}
                                                                    </div></span></a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <a data-fast="/wallet/deposit/company/electronic/index.html?searchId=${command.getSearchId(i.id)}" class="${fn:length(views.deposit[i.rechargeType])>5?'long':''}"><i class="pay ${i.bankCode=='onecodepay'?'ymf':i.bankCode}"></i><span><div class="text-two-line">
                                                                            ${views.deposit[i.rechargeType]}
                                                                    </div></span></a>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </li>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${p.key eq 'isFastRecharge'}">
                                                    <li key="${p.key}"><a data-fastRecharge="${p.value}" class="${fn:length(views.deposit_auto['快速充值中心'])>5?'long':''}"><i class="pay ks"></i><span><div class="text-two-line">${views.deposit_auto['快速充值中心']}</div></span></a></li>
                                                </c:if>
                                                <c:if test="${p.key eq 'bitcoin_fast'}">
                                                    <li key="${p.key}"><a data-bitcoin="/wallet/deposit/company/bitcoin/index.html?searchId=${command.getSearchId(p.value.id)}" class="${fn:length(views.deposit_auto['比特币支付'])>5?'long':''}"><i class="pay bitcoin"></i><span><div class="text-two-line">${views.deposit_auto['比特币支付']}</div></span></a></li>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${not empty digiccyAccountInfo}">
                                                <li key="digiccyAccountInfo"><a data-bitcoin="/wallet/deposit/digiccy/index.html" class="${fn:length(views.themes_auto['数字货币支付'])>5?'long':''}"><i class="pay digitalc"></i><span><div class="text-two-line">${views.themes_auto['数字货币支付']}</div></span></a></li>
                                            </c:if>
                                        </ul>
                                        <div class="clearfix"></div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-gray-light">${views.deposit_auto['支付系统升级中']}</p>
                                </c:otherwise>
                            </c:choose>

                            <c:if test="${isFastRecharge}">
                                <div class="deposit-tab01">
                                    <c:set var="url" value="${rechargeUrlParam.paramValue}"/>
                                    <c:if test="${!fn:startsWith(url, 'http')}">
                                        <c:set var="url" value="http://${rechargeUrlParam.paramValue}"/>
                                    </c:if>
                                    <a data-href="<c:out value='${url}'/>" target="_blank" id="fastDeposit">
                                        <img src="${resRoot}/images/deposit-tab-img7.png">
                                        <span class="pay-title">
                                            <em>${views.deposit_auto['充值中心']}</em>
                                             <small>${views.deposit_auto['请优先使用其他存款方式']}</small>
                                            </span>
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="mui-row" id="deposit"></div>
            </div>
            <div class="mui-row">
                <div class="gb-form-foot bank-pay-btn">
                    <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount" >${views.deposit_auto['下一步']}</button>
                </div>
            </div>
        </div>
    </div>
</div>
<%--微信反扫教程：--%>
<div id="depositHelpBox10" class="depositHelpBox" style="display: none">
    <div class="swiper-container swiper1">
        <div class="swiper-wrapper wechat-fansao">
            <div class="swiper-slide step-1"><img src="${resRoot}/images/wechat-step-1.png"/><div class="circle"></div></div>
            <div class="swiper-slide step-2"><img src="${resRoot}/images/wechat-step-2.png"/><div class="circle"></div></div>
            <div class="swiper-slide step-3"><img src="${resRoot}/images/wechat-step-3.png" /><div class="circle"></div></div>
        </div>
        <div class="swiper-pagination swiper-pagination-bullets"></div>
        <div class="closeHelpBox">+</div>
    </div>
</div>
<!--qq反扫教程：-->
<div id="depositHelpBox12"  class="depositHelpBox" style="display:none;">
    <div class="swiper-container">
        <div class="swiper-wrapper qq-fansao" style="">
            <div class="swiper-slide step-1"><img src="${resRoot}/images/qq-step-1.png" alt="" /><div class="circle"></div></div>
            <div class="swiper-slide step-2"><img src="${resRoot}/images/qq-step-2.png" alt="" /><div class="circle"></div></div>
            <div class="swiper-slide step-3"><img src="${resRoot}/images/qq-step-3.png" alt="" /><div class="circle"></div></div>
        </div>
        <div class="swiper-pagination"></div>
        <div class="closeHelpBox">+</div>
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
        <div class="closeHelpBox">+</div>
    </div>
</div>

<script type="text/javascript" src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script>
    curl(['site/deposit/Deposit','site/deposit/Online','site/deposit/ScanCode', 'site/common/Assets'],
        function (Page,Online,ScanCode, Assets) {
            page = new Page();
            page.Online = new Online();
            page.ScanCode = new ScanCode();
            page.asset = new Assets();
        });
</script>
</body>