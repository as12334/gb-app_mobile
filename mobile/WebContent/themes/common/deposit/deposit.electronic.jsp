<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<body class="gb-theme mine-page ex-wechat">
<div id="offCanvasWrapper" class="mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
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
        <form id="electronicForm">
            <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0!important"':''}>
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
                                <div class="gb-panel" style="border-top-color: #fff;">
                                    <p  class="tit">${views.deposit_auto['账号信息']}</p>
                                    <div class="mui-control-content mui-active">
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
                                            <c:if test="${not empty payAccount.remark}">
                                                <div class="front-end">
                                                    <pre stype="white-space: normal;">${payAccount.remark}</pre>                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                        <%--<div class="wechat-code">
                                            <p class="m-b-0"><img id="qrCodeUrl" src="${soulFn:getThumbPath(domain,payAccount.qrCodeUrl,135,135)}" style="width: 135px;height: 135px;"></p>
                                            <p>
                                                <button type="button" id="saveImage" url="${soulFn:getImagePath(domain, payAccount.qrCodeUrl)}" class="btn mui-btn mui-btn-primary">${views.deposit_auto['保存到手机']}</button>
                                            </p>
                                        </div>--%>
                                    <input type="hidden" id="imgQrCodeUrl" value="${payAccount.qrCodeUrl}">
                                    <c:if test="${not empty payAccount.qrCodeUrl}">
                                        <div class="wechat-code">
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
                                            <%--<c:if test="${payAccount.bankCode eq 'onecodepay'}">
                                                <c:set value="${views.deposit_auto['您的一码付账号']}" var="n"></c:set>
                                                <c:set value="${views.deposit_auto['请输入一码付账号']}" var="m"></c:set>
                                            </c:if>--%>
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
                                    <c:if test="${payAccount.bankCode eq 'alipay'}">
                                        <div class="mui-row m-l-sm">
                                            <div class="gb-form-notice">
                                                <p><img src="${resRoot}/images/ico-notice.png" height="12px;" alt="">
                                                    “支付宝”转账到“支付宝”，请填写昵称；
                                                    <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“支付宝”转账到“银行卡”，请填写真实姓名；
                                                </p>
                                            </div>
                                        </div>
                                    </c:if>
                                    <div class="mui-input-row">
                                        <label>${views.deposit_auto['订单号后5位']}
                                            <span class="small">${views.deposit_auto['非必填']}</span>
                                        </label>
                                        <div class="ct">
                                            <input type="text" name="result.bankOrder" placeholder="${views.deposit_auto['非商户订单号']}">
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
                                        <div class="gb-form-notice">
                                            <p>
                                                    ${payAccount.remark}
                                            </p>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                            <div class="mui-row">
                                <div class="gb-form-foot bank-pay-btn">
                                    <button class="mui-btn mui-btn-primary submit" type="button" id="submitAmount">${views.deposit_auto['提交']}</button>
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

