<%--@elvariable id="bank" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<%--@elvariable id="btc" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<%@ page import="so.wwb.gamebox.model.master.player.enums.UserBankcardTypeEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.withdraw_auto['取款']}</h1>
            <%@ include file="/themes/default/include/include.asset.jsp" %>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0!important"':''}>
            <form name="withdrawform">
            <c:set var="totalBalance" value="${not empty player.walletBalance?player.walletBalance:0}"></c:set>
            <c:if test="${not empty apiBalance}">
                <c:set var="totalBalance" value="${totalBalance + apiBalance}"></c:set>
            </c:if>
            <c:choose>
                <%--已经有取款订单---%>
                <c:when test="${hasOrder}">
                    <div class="mui-scroll">
                        <div class="withdraw-out">
                            <div class="withdraw-not">
                                <h1><i class="tipbig fail"></i></h1>
                                <div class="tiptext">
                                    <p>${views.withdraw_auto['当前已有取款订单正在审核']}</p>
                                    <p>${views.withdraw_auto['请在该订单结束后再继续取款']}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <%--玩家是否余额冻结--%>
                <c:when test="${hasFreeze}">
                    <div class="mui-scroll">
                        <div class="withdraw-out">
                            <div class="withdraw-not">
                                <h1><i class="tipbig fail"></i></h1>
                                <div class="tiptext">
                                    <p>您的账号余额已被冻结，请联系客服</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <%--是否达到上限--%>
                <c:when test="${isFull}">
                    <div class="mui-scroll">
                        <div class="withdraw-out">
                            <div class="withdraw-not">
                                <h1><i class="tipbig fail"></i></h1>
                                <div class="tiptext">
                                    <p>${views.withdraw_auto['今日已达取款次数上限']}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <%--余额是否充足--%>
                <c:when test="${rank.withdrawMinNum > totalBalance}">
                    <div class="mui-scroll">
                        <div class="withdraw-out">
                            <div class="withdraw-not">
                                <h1><i class="tipbig fail"></i></h1>
                                <div class="tiptext">
                                    <p>${views.withdraw_auto['取款金额最少为'].replace('{0}', soulFn:formatCurrency(rank.withdrawMinNum))}</p>
                                    <c:if test="${!isLotterySite}">
                                        <p>${views.withdraw_auto['您当前钱包余额不足']}</p>
                                    </c:if>
                                </div>
                                <%--是否是mobile-v3 v3的存款target为0，v2target为1--%>
                                <c:set var="isMobileUpgrade" value="<%=ParamTool.isMobileUpgrade()%>"/>
                                <a class="mui-btn mui-btn-blue btn-deposit" data-target="${isMobileUpgrade?0:1}" data-os="${os}" data-skip="${root}/wallet/deposit/index.html">${views.withdraw_auto['快速存款']}</a>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:if test="${isBit&&isCash}">
                        <div class="account_tab">
                            <div class="mui-segmented-control">
                                <a class="mui-control-item mui-active" data="bank_account">${views.themes_auto['银行卡账户']}</a>
                                <a class="mui-control-item" data="bit_account">${views.themes_auto['比特币账户']}</a>
                            </div>
                        </div>
                    </c:if>
                    <div class="mui-clearfix">
                        <div class="mui-control-content mui-active">
                            <div id="validateRule" style="display: none">${validate}</div>
                            <div class="mui-scroll">
                                <div class="mui-row">
                                    <c:set var="bankType" value="<%=UserBankcardTypeEnum.TYPE_BANK%>"/>
                                    <c:set var="btcType" value="<%=UserBankcardTypeEnum.TYPE_BTC%>"/>
                                    <c:set var="bank" value="${bankcardMap.get(bankType)}"/>
                                    <c:set var="btc" value="${bankcardMap.get(btcType)}"/>
                                    <input type="hidden" name="noBank" value="${isCash && empty bank}"/>
                                    <input type="hidden" name="noBtc" value="${isBit && empty btc}"/>
                                    <input type="hidden" name="remittanceWay" value="${isCash?'1':'2'}">
                                    <input id="sign" type="hidden" value="${currencySign}"/>
                                    <c:if test="${isCash}">
                                        <div class="mui-input-group mine-form bankcard" id="bank_account">
                                            <div class="bankitem" data-url="${root}/bankCard/page/addCard.html?action=withdraw">
                                                <span>
                                                <c:if test="${!empty bank}">
                                                    <span class="pay-bank s ${bank.bankName}"></span>
                                                    ${soulFn:overlayName(bank.bankcardMasterName)} &nbsp;${gbFn:overlayBankcard(bank.bankcardNumber)}
                                                </c:if>
                                                <c:if test="${empty bank}">
                                                    <a>${views.withdraw_auto['请先绑定银行卡']}</a>
                                                </c:if>
                                                 </span>
                                                <div class="clearfix"></div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${isBit}">
                                        <div class="mui-input-group mine-form bankcard" id="bit_account" style="${isCash?'display: none':''}">
                                            <div class="bankitem" data-url="${root}/bankCard/page/addBtc.html?action=withdraw">
                                                <span>
                                                    <c:if test="${!empty btc}">
                                                        <span class="pay-bank bitcoin"></span>
                                                        比特币&nbsp;${gbFn:overlayBankcard(btc.bankcardNumber)}
                                                    </c:if>
                                                     <c:if test="${empty btc}">
                                                         <a>${views.themes_auto['请先绑定比特币地址']}</a>
                                                     </c:if>
                                                </span>
                                                <div class="clearfix"></div>
                                            </div>
                                        </div>
                                    </c:if>
                                    <div class="mui-input-group mine-form m-t-sm">
                                        <div class="mui-input-row"><label>${views.withdraw_auto['取款金额']}</label>
                                            <div class="ct">
                                                <p class="mui-text-right text-gray">
                                                    <c:set var="minAmount" value="${rank.withdrawMinNum}" />
                                                    <c:set var="maxAmount" value="${rank.withdrawMaxNum}" />
                                                    <input type="hidden" name="walletBalance" value="${totalBalance}"/>
                                                    <input type="text" class="gb-money" placeholder="${currencySign}${minAmount}-${currencySign}${maxAmount}"
                                                        ${hasBank? '' : 'disabled'}
                                                           step="1"
                                                           autocomplete="off"
                                                           name="withdrawAmount"
                                                           min="${minAmount}"
                                                           max="${maxAmount}"/>
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                   <%-- <div class="mui-input-group mine-form m-t-sm">
                                        <div class="mui-input-row">
                                            <label>${views.withdraw_auto['手续费']}</label>
                                            <div class="ct">
                                                <p class="mui-text-right text-gray">
                                                    <c:set var="poundage" value="${auditMap.counterFee}" />
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
                                                    <c:set var="poundage" value="${auditMap.administrativeFee}" />
                                                <span class="t-gray">${currencySign}${poundage>0?'-':''}
                                                        ${soulFn:formatInteger(poundage)}${soulFn:formatDecimals(poundage)}</span>
                                                </p>
                                            </div>
                                        </div>
                                        <div class="mui-input-row">
                                            <label>${views.withdraw_auto['扣除优惠']}</label>
                                            <div class="ct">
                                                <p class="mui-text-right text-gray">
                                                    <c:set var="deductFav" value="${auditMap.deductFavorable}" />
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
                                    </div>--%>
                                </div>
                                <c:if test="${auditMap.recordList}">
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
                                        <button type="button" id="confirmWithdraw" class="mui-btn mui-btn-primary submit">${views.withdraw_auto['确认提交']}</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
                <div class="masker" style="display:none;"></div>
                <div class="gb-withdraw-box pro-window" id="confirmWithdrawDialog" style="display: none;">
                    <div class="cont">
                        <h3>${views.withdraw_auto['取款核算']}</h3>
                        <div class="cont-text">
                            <p>${views.withdraw_auto['取款金额']}：<span class="org" id="confirmWithdrawAmount"></span></p>
                            <p>${views.withdraw_auto['手续费']}：<span class="org" id="confirmWithdrawFee"></span></p>
                            <p>${views.withdraw_auto['行政费']}：<span class="org" id="confirmAdministrativeFee">${auditMap.administrativeFee>0?'-':''}${soulFn:formatCurrency(auditMap.administrativeFee)}</span></p>
                            <p>${views.withdraw_auto['扣除优惠']}：<span class="org">${auditMap.deductFavorable>0?'-':''}${soulFn:formatCurrency(auditMap.deductFavorable)}</span></p>
                            <p>${views.withdraw_auto['最终可取']}：<span class="org" id="confirmWithdrawActualAmount"></span></p>
                        </div>
                        <div class="pro-btn">
                            <a class="next-btn" id="submitWithdraw">${views.withdraw_auto['确认提交']}</a>
                            <a class="agin-btn" name="closeConfirmDialog">${views.deposit_auto['重新填写金额']}</a>
                        </div>
                        <div class="close" name="closeConfirmDialog">
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<script>
    curl(['site/withdraw/Index', 'site/passport/password/PopSecurityPassword', 'site/common/Assets'],
            function(Page, Security, Assets) {
                page = new Page();
                page.security = new Security();
                page.asset = new Assets();
            }
    );
</script>
