<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<head>
    <%@include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/otherpage.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css">
    <link rel="stylesheet" href="${resRoot}/themes/mui.poppicker.css">
</head>

<body class="deposit_2">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.deposit_auto['比特币支付']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper deposit-2-content">
    <div class="mui-scroll">
        <form id="rechargeForm" onsubmit="return false">
            <c:choose>
            <c:when test="${not empty payAccount}">
                <div id="validateRule" style="display: none">${validateRule}</div>
                <gb:token/>
                <input type="hidden" name="account" id="account" value="${command.getSearchId(payAccount.id)}"/>
                <input type="hidden" name="result.rechargeType" value="bitcoin_fast"/>
                <input type="hidden" name="activityId" id="activityId"/>
                <input type="hidden" name="depositChannel" value="bitcoin"/>
                <input type="hidden" name="channel" value="${channel}"/>
                <div class="pay_mone">
                    <div class="tit">账号信息</div>
                    <div class="bank_car_item bit_coin_card">
                        <div class="top">
                            <span class="pay-third BTC"></span>
                        </div>
                        <div class="bank_car_txt_info">
                            <div class="b_c_t_i_row">
                            <c:choose>
                                <c:when test="${isHide}">
                                    <c:set var="copyText" value="${payAccount.code}"/>
                                    <a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>${payAccount.code} ${views.deposit_auto['请联系客服']}</a>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="copyText" value="${payAccount.account}"/>
                                    ${payAccount.account}
                                    <a href="#" class="btn_copy copy" data-clipboard-text="${payAccount.account}">${views.themes_auto['复制']}</a>
                                </c:otherwise>
                            </c:choose>
                            </div>
                            <div class="b_c_t_i_row">
                                    ${views.deposit_auto['姓名']}： ${payAccount.fullName}
                                    <a href="#" class="btn_copy copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>
                            </div>
                        </div>
                    </div>
                    <div class="wechat-code bit_coin_c">
                        <div class="cod-img-wrap">
                                <img src="${soulFn:getThumbPath(domain,payAccount.qrCodeUrl,135,135)}" alt="">
                        </div>
                        <div class="cod-btn-wrap">
                            <a data-rel='{"url":"${soulFn:getImagePath(domain, payAccount.qrCodeUrl)}","opType":"function","target":"savePicture"}' class="mui-btn">${views.deposit_auto['保存到手机']}</a>
                        </div>
                    </div>
                    <div class="depo_row">
                        <div class="label w_120"><label for="result.payerBankcard">${views.deposit_auto['您的比特币地址']}</label></div>
                        <div class="input">
                            <input type="text" id="result.payerBankcard"  class="text-right"name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${views.deposit_auto['请输入比特币地址']}" autocomplete="off">
                        </div>
                    </div>
                    <div class="depo_row">
                        <div class="label w_120"><label for="result.bankOrder">txid</label></div>
                        <div class="input">
                            <input type="text" name="result.bankOrder" class="text-right" id="result.bankOrder" placeholder="${views.deposit_auto['请输入交易时产生的txid']}" autocomplete="off">
                        </div>
                    </div>
                    <div class="depo_row">
                        <div class="label w_120"><label for="result.bitAmount">比特币</label></div>
                        <div class="input">
                            <input type="text" name="result.bitAmount" class="text-right" id="result.bitAmount"  placeholder="${views.deposit_auto['请输入比特币数量']}" autocomplete="off">
                        </div>
                    </div>
                    <div class="depo_row">
                        <div class="label w_120"><label for="result.returnTime">交易时间</label></div>
                        <div class="input">
                            <a data-rel='{"opType":"function","target":"depositBitCoin.selectionDate"}'>
                                <input type="text" readonly="readonly" id="result.returnTime" name="result.returnTime" class="time text-right" placeholder="请选择交易时间" value="${soulFn:formatDateTz(date, DateFormat.DAY_MINUTE,timeZone)}" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY_MINUTE,timeZone)}">
                            </a>
                        </div>
                    </div>
                </div>
                <div class="btn_wrap">
                    <a href="javascript:" data-rel='{"opType":"function","target":"baseDeposit.activity"}' class="mui-btn btn_submit mui-btn-block">提交</a>
                </div>
                <div class="deposit_help">
                    <p>温馨提示</p>
                    <p>• ${views.deposit_auto['正确的txid']}</p>
                    <p>• 建议您使用Internet Explorer 9以上、360浏览器、Firefox或Google Chrome等浏览器浏览。</p>
                    <p>• ${views.deposit_auto['客服帮助']}
                        <a href="javascript:" data-rel='{"target":"loadCustomer","opType":"function" }'>${views.deposit_auto['点击联系在线客服']}</a>
                    </p>
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
            <a data-rel='{"opType":"function","target":"goToDepositPage"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["再存一次"]}</a>
            <a data-rel='{"target":"goToHome","opType":"function"}' class="btn mui-btn mui-btn-outlined">${views.deposit_auto["返回首页"]}</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/dist/clipboard.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositCenter.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/BaseDeposit.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/deposit/DepositBitcoin.js?v=${rcVersion}"></script>