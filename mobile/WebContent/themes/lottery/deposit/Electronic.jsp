<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <title><c:if test="${payAccount.bankCode eq 'wechatpay'}">
        ${views.deposit_auto['转账到微信']}
    </c:if>
        <c:if test="${payAccount.bankCode eq 'alipay'}">
            ${views.deposit_auto['转账到支付宝']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'other'}">
            ${views.deposit_auto['其它支付']}
        </c:if></title>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">
                <c:if test="${payAccount.bankCode eq 'wechatpay'}">
                    ${views.deposit_auto['转账到微信']}
                </c:if>
                <c:if test="${payAccount.bankCode eq 'alipay'}">
                    ${views.deposit_auto['转账到支付宝']}
                </c:if>
                <c:if test="${payAccount.bankCode eq 'other'}">
                    ${views.deposit_auto['其它支付']}
                </c:if>
            </h1>
        </header>
        <form id="electronicForm">
        <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0"':''}>
            <c:choose>
                <c:when test="${not empty payAccount}">
                    <div class="${os eq 'app_ios' ? 'mui-scroll':''} mui-scroll2">
                        <div id="validateRule" style="display: none">${validateRule}</div>
                        <gb:token/>
                        <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                        <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
                        <input type="hidden" name="onlinePayMin" value="${rank.onlinePayMin}"/>
                        <input type="hidden" name="onlinePayMax" value="${rank.onlinePayMax}"/>
                        <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
                        <div class="mui-row">
                            <div class="gb-panel" style="border-bottom-color: #fff;">
                                <div class="gb-headtabs">
                                    <div id="segmentedControl" class="mui-segmented-control">
                                        <a href="#wechat1" class="mui-control-item mui-active">${views.deposit_auto['账号信息']}</a>
                                        <a href="#wechat2" class="mui-control-item">
                                            <c:if test="${payAccount.bankCode eq 'wechatpay'}">
                                                ${views.deposit_auto['微信二维码']}
                                            </c:if>
                                            <c:if test="${payAccount.bankCode eq 'alipay'}">
                                                ${views.deposit_auto['支付宝二维码']}
                                            </c:if>
                                            <c:if test="${payAccount.bankCode eq 'other'}">
                                                ${views.deposit_auto['其它二维码']}
                                            </c:if>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div id="wechat1" class="mui-control-content mui-active">
                            <div class="mui-row">
                                <div class="gb-panel" style="border-top-color: #fff;">
                                    <div class="gb-bankcard">
                                        <div class="hd">
                                            <c:set var="flag" value="${empty payAccount.customBankName || dicts.common.bankname[payAccount.bankCode]==payAccount.customBankName}"/>
                                            <p class="${!flag ? '':'pay-third '} ${!flag ? '':payAccount.bankCode}">
                                                <c:if test="${!flag}">
                                                    ${payAccount.customBankName}
                                                </c:if>
                                            </p>
                                        </div>
                                        <div class="ct">
                                            <p>
                                            <span class="text-green">
                                                <c:choose>
                                                    <c:when test="${isHide}">
                                                        ${views.deposit_auto['账号代码']}：${payAccount.code},${views.deposit_auto['请联系客服']}
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${payAccount.account}
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            </p>
                                            <div class="ct">
                                                <p><span>${views.deposit_auto['姓名']}:</span>
                                                        ${payAccount.fullName}
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <c:if test="${not empty payAccount.remark}">
                                    <div class="front-end">
                                        <pre>${payAccount.remark}</pre>
                                    </div>
                                </c:if>

                            </div>


                        </div>
                        <div id="wechat2" class="mui-control-content">
                            <div class="mui-row">
                                <div class="gb-panel" style="border-top-color: #fff;">
                                    <div class="pays-wxcode" style="padding: 0">
                                        <div class="ct" style="padding: 0">
                                            <p class="m-b-0"><img id="qrCodeUrl" src="${soulFn:getThumbPath(domain,payAccount.qrCodeUrl,135,135)}" style="width: 135px;height: 135px;"></p>
                                            <p>
                                                <button type="button" id="saveImage" url="${soulFn:getImagePath(domain, payAccount.qrCodeUrl)}" class="btn mui-btn mui-btn-primary">${views.deposit_auto['保存到手机']}</button>
                                            </p>
                                        </div>
                                    </div>
                                </div>
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
                                    <c:if test="${payAccount.bankCode eq 'other'}">
                                        <c:set value="${views.deposit_auto['您的其他方式账号']}" var="n"></c:set>
                                        <c:set value="${views.deposit_auto['请输入其他方式账号']}" var="m"></c:set>
                                    </c:if>
                                    <label>${n}</label>
                                    <div class="ct">
                                        <input type="text" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${m}" autocomplete="off">
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label>${views.deposit_auto['金额']}</label>
                                    <div class="ct">
                                        <c:set value="${siteCurrencySogn}${empty rank.onlinePayMin?'1.00':soulFn:formatCurrency(rank.onlinePayMin)}" var="pmi"></c:set>
                                        <c:set value="${siteCurrencySogn}${empty rank.onlinePayMax?'99,999,999.00':soulFn:formatCurrency(rank.onlinePayMax)}" var="pma"></c:set>
                                        <input type="number" name="result.rechargeAmount" id="result.rechargeAmount" placeholder="${pmi}-${pma}" autocomplete="off">
                                    </div>
                                </div>

                                <%@include file="ChooseAmount.jsp"%>

                                <div class="mui-input-row">
                                    <label>${views.deposit_auto['订单号后5位']}
                                        <span class="small">${views.deposit_auto['非商户订单号']}</span>
                                    </label>
                                    <div class="ct">
                                        <input type="text" name="result.bankOrder" placeholder="${views.deposit_auto['非必填']}">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mui-row">
                            <div class="gb-form-foot" style="padding-bottom:10px;">
                                <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount" disabled="disabled">${views.deposit_auto['提交']}</button>
                            </div>
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
<script>
    mui('.mui-scroll-wrapper').scroll({
        deceleration: 0.0005 //flick 减速系数，系数越大，滚动速度越慢，滚动距离越小，默认值0.0006
    });
    curl(['site/deposit/ElectronicDeposit'], function (Page) {
        page = new Page();
    });
</script>
</body>
</html>