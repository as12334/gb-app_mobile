<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.withdraw_auto['取款']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/plugin/map.js?v=${rcVersion}"></script>
    <%@ include file="/include/include.js.jsp" %>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.withdraw_auto['取款']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <form onsubmit="return false">
                    <input type="hidden" id="hasBankcard" value="${hasBankcard}" />
                    <c:set var="currencySign" value="${dicts.common.currency_symbol[player.defaultCurrency]}" />
                    <c:choose>
                        <%-- 有未审核的取款订单 --%>
                        <c:when test="${hasOrder}">
                            <div class="withdraw-out">
                                <div class="withdraw-not">
                                    <h1><i class="tipbig fail"></i></h1>
                                    <div class="tiptext">
                                        <p>${views.withdraw_auto['当前已有取款订单正在审核']}</p>
                                        <p>${views.withdraw_auto['请在该订单结束后再继续取款']}</p>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:when test="${rank.withdrawMinNum > player.walletBalance}">
                            <div class="withdraw-out">
                                <div class="withdraw-not">
                                    <h1><i class="tipbig fail"></i></h1>
                                    <div class="tiptext">
                                        <p>${views.withdraw_auto['取款金额最少为'].replace('{0}', soulFn:formatCurrency(rank.withdrawMinNum))}元</p>
                                        <p>${views.withdraw_auto['您当前钱包余额不足']}</p>
                                    </div>
                                    <a class="mui-btn mui-btn-blue btn-deposit" data-href="${root}/wallet/deposit/index.html">${views.withdraw_auto['快速存款']}</a>
                                </div>
                            </div>
                        </c:when>
                        <c:when test="${isFull}">
                            <div class="withdraw-out">
                                <div class="withdraw-not">
                                    <h1><i class="tipbig fail"></i></h1>
                                    <div class="tiptext">
                                        <p>${views.withdraw_auto['今日已达取款次数上限']}</p>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="mui-row">
                                <div id="validateRule" style="display: none">${validateRule}</div>
                                <input type="hidden" id="sign" value="${currencySign}" />
                                <div class="mui-input-group mine-form">
                                    <div class="bankitem">
                                        <span>
                                            <c:choose>
                                                <c:when test="${hasBankcard eq 'yes'}">
                                                    <span class="pay-bank ${bankcard.bankName}">
                                                    </span>${soulFn:overlayName(bankcard.bankcardMasterName)}&nbsp;&nbsp;${gbFn:overlayBankcard(bankcard.bankcardNumber)}
                                                </c:when>
                                                <c:otherwise>
                                                    <a>${views.withdraw_auto['请先绑定银行卡']}</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                        <div class="clearfix"></div>
                                    </div>
                                    <div class="arrow"></div>
                                </div>
                                <div class="mui-input-group mine-form m-t-sm">
                                    <div class="mui-input-row"><label>${views.withdraw_auto['取款金额']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right text-gray">
                                                <c:set var="minAmount" value="${rank.withdrawMinNum}" />
                                                <c:set var="maxAmount" value="${rank.withdrawMaxNum}" />
                                                <input type="hidden" name="walletBalance" value="${player.walletBalance}"/>
                                                <input type="text" class="gb-money"
                                                       placeholder="${currencySign}${minAmount}-${currencySign}${maxAmount}"
                                                       ${hasBankcard == 'yes' ? '' : 'disabled'}
                                                       step="1"
                                                       autocomplete="off"
                                                       name="withdrawAmount"
                                                       min="${minAmount}"
                                                       max="${maxAmount}"/>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="mui-input-group mine-form m-t-sm">
                                    <div class="mui-input-row">
                                        <label>${views.withdraw_auto['手续费']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right text-gray">
                                                <c:set var="poundage" value="${audit.counterFee}" />
                                                <c:set var="pouTip" value="${poundage == '0' ? '免手续费' : poundage}" />
                                                <span class="t-gray poundage mt-8">${poundage == '0'? '' : currencySign}${pouTip}</span>
                                                <input type="hidden" name="poundageHide" value="${poundage == '0'? '' : currencySign}${pouTip}" />
                                                <input type="hidden" name="withdrawFee" value="${poundage}" readonly>
                                            </p>
                                        </div>
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.withdraw_auto['行政费']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right text-gray">
                                                <c:set var="poundage" value="${audit.administrativeFee}" />
                                                <span class="t-gray">${currencySign}${poundage>0?'-':''}
                                                        ${soulFn:formatInteger(poundage)}${soulFn:formatDecimals(poundage)}</span>
                                            </p>
                                        </div>
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.withdraw_auto['扣除优惠']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right text-gray">
                                                <c:set var="deductFav" value="${audit.deductFavorable}" />
                                                <span class="t-gray deductFav">${currencySign}${deductFav > 0 ? '-' : ''}
                                                        ${soulFn:formatInteger(deductFav)}${soulFn:formatDecimals(deductFav)}</span>
                                            </p>
                                        </div>
                                    </div>
                                    <div class="mui-input-row final">
                                        <label>${views.withdraw_auto['最终可取']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right text-green">
                                                <span class="gb-money co-tomato actual mt-8">${currencySign}--</span>
                                                <input type="hidden" name="actualHide" value="${currencySign}--" />
                                                <input type="hidden" name="actualWithdraw" readonly>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${audit.recordList}">
                                    <div class="mui-input-row last-child f-link">
                                        <div class="form-row">
                                            <div class="cont">
                                                <p>
                                                    <span class="mui-pull-right">
                                                    <a href="javascript:void(0)" data-href="${root}/wallet/withdraw/showAuditLog.html">${views.withdraw_auto['查看稽核']}</a>
                                                    </span>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                                <div class="mui-row">
                                    <div class="gb-form-foot">
                                        <gb:token/>
                                        <button type="button" class="mui-btn mui-btn-primary submit" disabled>${views.withdraw_auto['确认提交']}</button>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </form>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<script>
    curl(['site/withdraw/Withdraw', 'site/passport/password/PopSecurityPassword', 'site/common/Assets'],
        function(Page, Security, Assets) {
            page = new Page();
            page.security = new Security();
            page.asset = new Assets();
        }
    );
</script>
</html>

