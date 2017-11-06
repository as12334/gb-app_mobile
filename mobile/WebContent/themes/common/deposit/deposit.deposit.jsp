<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page no-backdrop" >
<div id="offCanvasWrapper" class="mui-draggable">
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
        <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0!improtant"':''} >
            <div class="${os eq 'app_ios' ? 'mui-scroll':''} mui-scroll2">
                <div class="gb-notice page-notice bg-white mui-hide" id="bankNotice" ${bankNotices.result.size() == 0 ? 'style="display:none"' : ''}>
                    <%@include file="./BankNotice.jsp"%>
                </div>
                <div class="mui-row">
                    <div class="mui-input-group mine-form">
                        <div class="bank-selector">
                            <p>${views.deposit_auto['请选择支付方式']}</p>
                            <c:choose>
                                <c:when test="${fn:length(payAccountMap) > 0 || !empty digiccyAccountInfo}">
                                    <div class="ct">
                                        <ul id="depositWay">
                                            <c:forEach items="${payAccountMap}" var="p">
                                                <c:if test="${p.key eq 'online_deposit'}">
                                                    <li key="${p.key}"><a data-online="/wallet/deposit/online/index.html">${views.deposit[p.key]}</a></li>
                                                </c:if>
                                                <c:if test="${p.key eq 'wechatpay_scan' || p.key eq 'alipay_scan'||p.key eq 'qqwallet_scan'|| p.key eq 'jdpay_scan' || p.key eq 'bdwallet_san' || p.key eq 'union_pay_scan'}">
                                                    <li key="${p.key}"><a data-scan="/wallet/deposit/online/scan/scanCode/${p.value}.html">${views.deposit[p.key]}</a></li>
                                                </c:if>

                                                <c:if test="${p.key eq 'company_deposit'}">
                                                    <c:forEach items="${p.value}" var="c">
                                                        <li><a data-company="/wallet/deposit/company/index.html?searchId=${command.getSearchId(c.value)}">${c.text}</a></li>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${p.key eq 'other_fast'|| p.key eq 'wechatpay_fast' || p.key eq 'alipay_fast'||p.key eq 'qqwallet_fast'||p.key eq 'jdwallet_fast'||p.key eq 'bdwallet_fast'||p.key eq 'onecodepay_fast'}">
                                                    <c:forEach items="${p.value}" var="i">
                                                        <li>
                                                            <a data-fast="/wallet/deposit/company/electronic/index.html?searchId=${command.getSearchId(i.id)}">
                                                                <c:choose>
                                                                    <c:when test="${isMultipleAccount}">
                                                                        ${i.aliasName}
                                                                    </c:when>
                                                                    <c:when test="${p.key eq 'other_fast'}">
                                                                        ${i.customBankName}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${views.deposit[p.key]}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </a>
                                                        </li>
                                                    </c:forEach>
                                                </c:if>
                                                <c:if test="${p.key eq 'isFastRecharge'}">
                                                    <li><a data-fastRecharge="${p.value}">${views.deposit_auto['快速充值中心']}</a></li>
                                                </c:if>
                                                <c:if test="${p.key eq 'bitcoin_fast'}">
                                                    <li><a data-bitcoin="/wallet/deposit/company/bitcoin/index.html?searchId=${command.getSearchId(p.value.id)}">${views.deposit_auto['比特币支付']}</a></li>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${!empty digiccyAccountInfo}">
                                                <li><a data-bitcoin="/wallet/deposit/digiccy/index.html">${views.themes_auto['数字货币支付']}</a></li>
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
        </div>
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