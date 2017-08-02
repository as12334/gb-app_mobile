<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css" />
    <title>${views.deposit_auto['比特币支付']}</title>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.deposit_auto['比特币支付']}</h1>
        </header>
        <form id="bitcoinForm">
        <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0"':''}>
            <c:choose>
                <c:when test="${not empty payAccount}">
                    <div class="${os eq 'app_ios' ? 'mui-scroll':''} mui-scroll2">
                        <div id="validateRule" style="display: none">${validateRule}</div>
                        <gb:token/>
                        <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                        <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
                        <div class="mui-row">
                            <div class="gb-panel" style="border-bottom-color: #fff;">
                                <div class="gb-headtabs">
                                    <div id="segmentedControl" class="mui-segmented-control">
                                        <a href="#wechat1" class="mui-control-item mui-active">${views.deposit_auto['账号信息']}</a>
                                        <a href="#wechat2" class="mui-control-item">${views.deposit_auto['比特币二维码']}</a>
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
                                            <div>
                                            <span class="text-green" style="word-break: break-all;">
                                                <c:choose>
                                                    <c:when test="${isHide}">
                                                        ${views.deposit_auto['账号代码']}：${payAccount.code},${views.deposit_auto['请联系客服']}
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${payAccount.account}
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            </div>
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
                                        <c:set value="${views.deposit_auto['您的比特币地址']}" var="n"></c:set>
                                        <c:set value="${views.deposit_auto['请输入比特币地址']}" var="m"></c:set>
                                    <label>${n}</label>
                                    <div class="ct">
                                        <input type="text" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${m}" autocomplete="off">
                                    </div>
                                </div>

                                <div class="mui-input-row">
                                    <label for="result.bankOrder">txId</label>
                                    <div class="ct">
                                        <c:set value="txID" var="d"></c:set>
                                        <input type="number" name="result.bankOrder" id="result.bankOrder" placeholder="${d}" autocomplete="off">
                                    </div>
                                </div>

                                <div class="mui-input-row">
                                    <label for="result.bitAmount">${views.deposit_auto['比特币']}</label>
                                    <div class="ct">
                                       <input type="number" name="result.bitAmount" id="result.bitAmount" autocomplete="off">
                                    </div>
                                </div>

                                <div class="mui-input-row">
                                    <div class="bank-cash">
                                        <ul id="chooseAmount">
                                            <li><a money="1">1</a></li>
                                            <li><a money="5">5</a></li>
                                            <li><a money="10">10</a></li>
                                            <li><a money="20">20</a></li>
                                        </ul>
                                        <div class="clearfix"></div>
                                    </div>
                                </div>

                                <%--<div class="mui-input-row">--%>
                                    <div class="gb-datafilter">
                                        <label for="result.returnTime">${views.deposit_auto['交易时间']}</label>
                                        <span class="input-date">
                                            <input type="text" id="result.returnTime" name="result.returnTime" class="date" value="${soulFn:formatDateTz(date, DateFormat.DAY_MINUTE,timeZone)}" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY_MINUTE,timeZone)}">
                                        </span>
                                        <div class="clearfix"></div>
                                    </div>
                                <%--</div>--%>
                            </div>
                        </div>

                        <div class="mui-row">
                            <div class="gb-form-foot" style="padding-bottom:10px;">
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
    curl(['site/deposit/BitcoinDeposit'], function (Page) {
        page = new Page();
    });
</script>
</body>
</html>