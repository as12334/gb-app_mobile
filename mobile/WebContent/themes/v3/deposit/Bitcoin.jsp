<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<head>
    <%@include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/otherpage.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.poppicker.css">
</head>
<body class="exchange-bitcoin exchange-bank">
<header class="mui-bar mui-bar-nav">
    <a class="mui-icon mui-icon mui-icon-left-nav mui-pull-left mui-action-back"></a>
    <h1 class="mui-title">${views.deposit_auto['比特币支付']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <form id="bitcoinForm">
            <c:choose>
            <c:when test="${not empty payAccount}">
                <div id="validateRule" style="display: none">${validateRule}</div>
                <gb:token/>
                <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
                <input type="hidden" name="depositChannel" value="bitcoin"/>
                <div class="mui-control-content mui-active ">
                    <div class="mui-row">
                        <div class="gb-panel" style="border-top-color: #fff;">
                            <p class="tit">${views.deposit_auto['账号信息']}</p>
                            <div class="gb-bankcard">
                                <div class="hd">
                                    <p>
                                        <span class="pay-third BTC"></span>
                                    </p>
                                </div>
                                <div class="ct">
                                    <p class="text-green">
                                        <span style="word-break: break-all;">
                                            <c:choose>
                                                <c:when test="${isHide}">
                                                    <c:set var="copyText" value="${payAccount.code}"/>
                                                    ${payAccount.code} ${views.deposit_auto['请联系客服']}
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="copyText" value="${payAccount.account}"/>
                                                    ${payAccount.account}
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <a href="#" class="copy" data-clipboard-text="${copyText}" style="margin-right:10px;">${views.themes_auto['复制']}</a>
                                    </p>
                                    <div class="ct">
                                        <p>
                                            <span>${views.deposit_auto['姓名']}:</span>
                                            ${payAccount.fullName}
                                            <a href="#" class="copy" data-clipboard-text="${payAccount.fullName}" style="margin-right:10px;">${views.themes_auto['复制']}</a>
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <c:if test="${not empty payAccount.qrCodeUrl}">
                                <div class="wechat-code">
                                    <img src="${soulFn:getThumbPath(domain,payAccount.qrCodeUrl,135,135)}" alt="">
                                    <a data-rel='{"url":"${soulFn:getImagePath(domain, payAccount.qrCodeUrl)}","opType":"function","target":"savePicture"}'><p class="tit">${views.deposit_auto['保存到手机']}</p></a>
                                </div>
                            </c:if>
                        </div>
                    </div>
                    <div class="mui-row">
                        <div class="mui-input-group mine-form m-t-sm">
                            <div class="mui-input-row"><label for="result.payerBankcard">${views.deposit_auto['您的比特币地址']}</label>
                                <div class="ct">
                                    <input type="text" id="result.payerBankcard" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${views.deposit_auto['请输入比特币地址']}" autocomplete="off">
                                </div>
                            </div>
                            <div class="mui-input-row"><label for="result.bankOrder">txid</label>
                                <div class="ct">
                                    <input type="text" name="result.bankOrder" id="result.bankOrder" placeholder="${views.deposit_auto['请输入交易时产生的txid']}" autocomplete="off">
                                </div>
                            </div>
                            <div class="mui-input-row"><label for="result.bitAmount">${views.deposit_auto['比特币']}</label>
                                <div class="ct">
                                    <input type="text" name="result.bitAmount" id="result.bitAmount"  placeholder="${views.deposit_auto['请输入比特币数量']}" autocomplete="off">
                                </div>
                            </div>

                            <div class="mui-input-row">
                                <label for="result.returnTime">${views.deposit_auto['交易时间']}</label>
                                <div class="ct">
                                    <a data-rel='{"opType":"function","target":"selectionDate"}'>
                                        <input type="text" readonly="readonly" id="result.returnTime" name="result.returnTime" class="time"  value="${soulFn:formatDateTz(date, DateFormat.DAY_MINUTE,timeZone)}" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY_MINUTE,timeZone)}">
                                    </a>
                                </div>
                            </div>

                            <p class="info">* 为了方便系统快速完成转账，请输入正确的txid、交易时间等.</p>
                        </div>
                    </div>
                </div>
            </c:when>
                <c:otherwise>
                    <div class="mui-scroll">
                        <div class="mui-row">${views.deposit_auto['请确认是否有收款账号']}</div>
                    </div>
                </c:otherwise>
            </c:choose>
        </form>
    </div>
</div>
<div class="mui-row">
    <div class="gb-form-foot bank-pay-btn">
        <a data-rel='{"opType":"function","target":"depositDiscount"}' class="mui-btn mui-btn-primary submit">${views.deposit_auto['提交']}</a>
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
            <a data-rel='{"target":"${root}/wallet/deposit/index.html","opType":"href"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["再存一次"]}</a>
            <a data-rel='{"target":"goToHome","opType":"function"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.js"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js"></script>
<script src="${resRoot}/js/deposit/CompanyDeposit.js"></script>
