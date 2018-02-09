<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<head>
    <%@include file="../include/include.head.jsp" %>
</head>
<body class="exchange-wechat exchange-bank">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">
        <c:if test="${payAccount.bankCode eq 'wechatpay'}">
            ${views.deposit_auto['转账到微信']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'alipay'}">
            ${views.deposit_auto['转账到支付宝']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'qqwallet'}">
            ${views.deposit_auto['转账到QQ钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'jdwallet'}">
            ${views.deposit_auto['转账到京东钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'bdwallet'}">
            ${views.deposit_auto['转账到百度钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'onecodepay'}">
            ${views.deposit_auto['转账到一码付']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'other'}">
            ${payAccount.customBankName}
        </c:if>
    </h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <form id="electronicForm">
            <gb:token/>
            <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
            <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <input type="hidden" name="depositChannel" value="electronic"/>
            <div class="mui-row">
                <div class="gb-panel" style="border-top-color: #fff;">
                    <p class="tit">${views.deposit_auto['账号信息']}</p>
                    <div class="gb-bankcard">
                        <div class="hd">
                            <c:set var="flag" value="${empty payAccount.customBankName || dicts.common.bankname[payAccount.bankCode]==payAccount.customBankName}"/>
                            <p class="${!flag ? '':'pay-third '} ${!flag ? '':payAccount.bankCode}">
                                <c:if test="${!flag}">
                                    ${payAccount.customBankName}
                                </c:if>
                            </p>
                        </div>
                        <div class="ct" style="padding-left:20px;">
                            <p>
                                <span class="text-green" style="color:#f49e2e;line-height:40px;">
                                <c:choose>
                                    <c:when test="${isHide}">
                                        ${payAccount.code},${views.deposit_auto['请联系客服']}
                                    </c:when>
                                    <c:otherwise>
                                        ${payAccount.account}
                                    </c:otherwise>
                                </c:choose>
                                </span>
                                <a href="#" class="copy" data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                            </p>
                            <div class="ct">
                                <p><span style="float:left;">${views.deposit_auto['姓名']}:</span>
                                    ${payAccount.fullName}
                                    <a href="#" class="copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
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
                    <div class="mui-input-row">
                        <c:set value="${views.deposit_auto['您的支付账号']}" var="n"></c:set>
                        <c:set value="${views.deposit_auto['请输入账号']}" var="m"></c:set>
                        <c:if test="${payAccount.bankCode eq 'wechatpay'}">
                            <c:set value="${views.deposit_auto['您的微信昵称']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入微信昵称']}" var="m"></c:set>
                        </c:if>
                        <c:if test="${payAccount.bankCode eq 'alipay'}">
                            <c:set value="${views.deposit_auto['您的支付宝账号']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入支付宝账号']}" var="m"></c:set>
                        </c:if>
                        <c:if test="${payAccount.bankCode eq 'qqwallet'}">
                            <c:set value="${views.deposit_auto['您的QQ钱包账号']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入QQ钱包账号']}" var="m"></c:set>
                        </c:if>
                        <c:if test="${payAccount.bankCode eq 'jdwallet'}">
                            <c:set value="${views.deposit_auto['您的京东钱包账号']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入京东钱包账号']}" var="m"></c:set>
                        </c:if>
                        <c:if test="${payAccount.bankCode eq 'bdwallet'}">
                            <c:set value="${views.deposit_auto['您的百度钱包账号']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入百度钱包账号']}" var="m"></c:set>
                        </c:if>
                        <c:if test="${payAccount.bankCode eq 'other'}">
                            <c:set value="${views.deposit_auto['您的其他方式账号']}" var="n"></c:set>
                            <c:set value="${views.deposit_auto['请输入其他方式账号']}" var="m"></c:set>
                        </c:if>

                        <c:choose>
                            <c:when test="${payAccount.bankCode eq 'onecodepay'}"></c:when>
                            <c:when test="${payAccount.bankCode eq 'alipay'}">
                                <label>${n}</label>
                                <div class="ct">
                                    <input type="text" id="payerBankcard1" name="result.payerBankcard" value="${lastTimeAccount}" autocomplete="off">
                                </div>
                            </c:when>
                            <c:otherwise>
                                <label>${n}</label>
                                <div class="ct">
                                    <input type="text" id="payerBankcard2" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${m}" autocomplete="off">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <%--存款金额--%>
                    <input type="hidden" name="result.rechargeAmount" id="result.rechargeAmount" value="${rechargeAmount}"/>
                    <div class="mui-input-row">
                        <label for="result.bankOrder">${views.deposit_auto['订单号后5位']}
                            <span class="small">${views.deposit_auto['非必填']}</span>
                        </label>
                        <div class="ct">
                            <input type="text" id="result.bankOrder" name="result.bankOrder" placeholder="${views.deposit_auto['非商户订单号']}">
                        </div>
                    </div>
                </div>
            </div>
            <c:if test="${empty payAccount.remark}">
                <ul class="info">
                    <li>* 请先搜索微信号或扫描二维码添加好友。</li>
                    <li>* 为了系统快速完成转账，请输入订单号后5位，以加快系统入款速度。</li>
                    <li>* 支付成功后，请等待几秒钟，提示[支付成功] 按确认</li>
                </ul>
            </c:if>
            <c:if test="${not empty payAccount.remark}">
                <div class="info">
                    <ul>
                        <li>${payAccount.remark}</li>
                    </ul>
                </div>
            </c:if>
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
<div class="gb-withdraw-box window-ok" style="display: none;">
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
            <a data-rel='{"target":"${root}/mainIndex.html","opType":"href"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/deposit/DepositCenter.js"></script>
<script src="${resRoot}/js/deposit/CompanyDeposit.js"></script>
<script src="${resRoot}../../common/js/jquery/plugins/jquery.validate/jquery.validate.js"></script>
</body>

