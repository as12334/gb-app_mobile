<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<head>
    <%@include file="../../include/include.head.jsp" %>
</head>
<body class="deposit_2">
<header class="mui-bar mui-bar-nav">
    <a class="mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"opType":"function","target":"goToLastPage"}'></a>
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
<div class="mui-content mui-scroll-wrapper deposit-2-content">
    <div class="mui-scroll">
        <c:if test="${activityHall==true}">
            <div class="deposit_tips">温馨提示：完成存款后，请前往活动大厅申请活动优惠。</div>
        </c:if>
        <form id="rechargeForm" onsubmit="return false">
            <gb:token/>
            <div id="validateRule" style="display: none">${validateRule}</div>
            <input type="hidden" name="account" id="account" value="${command.getSearchId(payAccount.id)}"/>
            <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
            <input type="hidden" name="activityId" id="activityId"/>
            <input type="hidden" name="displayFee" value="${!(empty rank.isFee && empty rank.isReturnFee)}"/>
            <input type="hidden" name="depositChannel" value="electronic"/>
            <input type="hidden" name="channel" id="channel" value="${channel}"/>
            <div class="pay_mone">
                <div class="gb-panel" style="border-top-color: #fff;">
                    <div class="tit">${views.deposit_auto['账号信息']}</div>
                    <div class="bank_car_item">
                        <div class="top">
                            <span class="${payAccount.bankCode eq 'other'?'':'pay-third sm '} ${payAccount.bankCode eq 'other'?'':payAccount.bankCode}"></span>
                            <span class='pay-txt'>${payAccount.customBankName}</span>
                        </div>
                        <div class="bank_car_txt_info">
                            <div class="b_c_t_i_row">
                                <c:choose>
                                    <c:when test="${isHide}">
                                        <a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>${payAccount.code},${views.deposit_auto['请联系客服']}</a>
                                    </c:when>
                                    <c:otherwise>
                                        ${payAccount.account}
                                    </c:otherwise>
                                </c:choose>
                                <a href="#" class="copy btn_copy" data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                            </div>
                            <div class="b_c_t_i_row">
                                ${views.deposit_auto['姓名']}:
                                ${payAccount.fullName}
                                <a href="#" class=" btn_copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
                            </div>
                        </div>
                    </div>
                    <c:if test="${not empty payAccount.qrCodeUrl && !isHide}">
                        <div class="wechat-code">
                            <div class="cod-img-wrap">
                                <img src="${soulFn:getThumbPath(domain,payAccount.qrCodeUrl,135,135)}" alt="">
                            </div>
                            <div class="cod-btn-wrap">
                                <a data-rel='{"url":"${soulFn:getImagePath(domain, payAccount.qrCodeUrl)}","opType":"function","target":"savePicture"}' class="mui-btn">${views.deposit_auto['保存到手机']}</a>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${not empty payAccount.remark}">
                        <p><c:out value="${payAccount.remark}"></c:out></p>
                    </c:if>
                </div>
            </div>
            <div class="mui-row">
                <div class="mui-input-group mine-form m-t-sm">
                    <c:if test="${payAccount.bankCode eq 'alipay'}">
                        <div class="mui-input-row">
                            <label for="result.payerName">您的支付户名</label>
                            <div class="ct">
                                <input type="text" id="result.payerName" name="result.payerName" placeholder="请填写存款时使用的真实姓名">
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${payAccount.bankCode != 'onecodepay'}">
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
                            <c:if test="${not empty payAccount.accountInformation}">
                                <c:set value="${payAccount.accountInformation}" var="n"></c:set>
                            </c:if>
                            <c:if test="${not empty payAccount.accountPrompt}">
                                <c:set value="${payAccount.accountPrompt}" var="m"></c:set>
                            </c:if>
                            <label for="result.payerBankcard">${n}</label>
                            <div class="ct">
                                <input type="text" id="result.payerBankcard" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${m}" autocomplete="off">
                            </div>
                        </div>
                    </c:if>
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
                    <div class="btn_wrap">
                        <a data-rel='{"opType":"function","target":"baseDeposit.activity"}' class="mui-btn btn_submit mui-btn-block">${views.deposit_auto['提交']}</a>
                    </div>
                </div>
            </div>
            <div class="deposit_help">
                <p>温馨提示</p>
                <c:if test="${channel!='onecodepay'}">
                    <p>• 请先查看入款账号信息或扫描二维码。</p>
                    <p>• ${views.deposit_auto['提示']}${views.deposit_auto['支付成功']}${views.deposit_auto['关闭支付窗口']}</p>
                    <p>• ${views.deposit_auto['请输入订单号后5位']}</p>
                    <p>• 如出现充值失败或充值后未到账等情况，请联系在线客服获取帮助。<a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>点击联系在线客服</a></p>
                </c:if>
                <c:if test="${channel=='onecodepay'}">
                    <p>• 五码合一，使用网银，支付宝，微信，QQ钱包，京东钱包均可扫描二维码进行存款.</p>
                    <p>• ${views.deposit_auto['提示']}${views.deposit_auto['支付成功']}${views.deposit_auto['关闭支付窗口']}</p>
                    <p>• ${views.deposit_auto['请输入订单号后5位']}</p>
                    <p>• 如出现充值失败或充值后未到账等情况，请联系在线客服获取帮助。<a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>点击联系在线客服</a></p>
                </c:if>
            </div>
        </form>
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
            <a class="btn mui-btn mui-btn-outlined" data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto["再存一次"]}</a>
            <a data-rel='{"target":"goToHome","opType":"function"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
<input type="hidden" value="true" id="depositPage"/>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/BaseDeposit.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositScancode.js?v=${rcVersion}"></script>
</body>