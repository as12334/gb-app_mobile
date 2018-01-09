<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<body class="gb-theme mine-page no-backdrop gb-theme mine-page ex-wechat ex-bitcoin">
<div id="offCanvasWrapper" class="mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav blue">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.deposit_auto['比特币支付']}</h1>
        </header>
        <form id="bitcoinForm">
            <div class="mui-content ${os eq 'app_ios' ? 'mui-scroll-wrapper':''}" ${os eq 'android'?'style="padding-top:0!important"':''}>
                <c:choose>
                <c:when test="${not empty payAccount}">
                <div class="mui-scroll2 main-coutent">
                    <div id="validateRule" style="display: none">${validateRule}</div>
                    <gb:token/>
                    <input type="hidden" name="result.payAccountId" value="${payAccount.id}"/>
                    <input type="hidden" name="result.rechargeType" value="${rechargeType}"/>
                    <div id="wechat1" class="mui-control-content mui-active ">
                        <div class="mui-row">
                            <div class="gb-panel" style="border-top-color: #fff;">
                                <p  class="tit">${views.deposit_auto['账号信息']}</p>
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
                                            <div class="bit-box">
                                                <c:choose>
                                                    <c:when test="${isHide}">
                                                        ${views.deposit_auto['账号代码']}：${payAccount.code},${views.deposit_auto['请联系客服']}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <textarea readonly="readonly">${payAccount.account}</textarea>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div class="ct">
                                            <p><span>${views.deposit_auto['姓名']}:</span>
                                                    ${payAccount.fullName}
                                                    <%--<a href="#" class="copy" data-clipboard-text="${payAccount.fullName}">${views.themes_auto['复制']}</a>--%>
                                            </p>
                                        </div>
                                    </div>
                                </div>
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
                                    <label for="result.payerBankcard">${views.deposit_auto['您的比特币地址']}</label>
                                    <div class="ct">
                                        <input type="text" id="result.payerBankcard" name="result.payerBankcard" value="${lastTimeAccount}" placeholder="${views.deposit_auto['请输入比特币地址']}" autocomplete="off">
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="result.bankOrder">txId</label>
                                    <div class="ct">
                                        <input type="text" name="result.bankOrder" id="result.bankOrder" placeholder="${views.deposit_auto['请输入交易时产生的txid']}" autocomplete="off">
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="result.bitAmount">${views.deposit_auto['比特币']}</label>
                                    <div class="ct">
                                        <input type="text" name="result.bitAmount" id="result.bitAmount"  placeholder="${views.deposit_auto['请输入比特币数量']}" autocomplete="off">
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
                                <div class="mui-input-row">
                                    <label for="result.returnTime">${views.deposit_auto['交易时间']}</label>
                                    <div class="ct">
                                        <input type="text" id="result.returnTime" name="result.returnTime" class="time"  value="${soulFn:formatDateTz(date, DateFormat.DAY_MINUTE,timeZone)}" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY_MINUTE,timeZone)}">
                                    </div>
                                </div>
                            </div>
                        </div>
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
    <div class="mui-off-canvas-backdrop"></div>
    </form>
</div>
</div>
<script>
    curl(['site/deposit/BitcoinDeposit'], function (Page) {
        page = new Page();
    });
</script>
</body>
