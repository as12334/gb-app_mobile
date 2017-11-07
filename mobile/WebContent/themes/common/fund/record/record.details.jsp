<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.fund_auto['资金记录明细']}</h1>
            <c:if test="${os ne 'app_ios' && !isLotterySite}">
            <%@ include file="/themes/default/include/include.asset.jsp" %>
            </c:if>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0!important"':''}>
            <c:choose>
                <c:when test="${command.result.transactionType eq 'favorable' or command.result.transactionType eq 'backwater' or command.result.transactionType eq 'recommend'}">
                    <div class="mui-scroll">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form">
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.transactionNo']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                                ${command.result.transactionNo}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.createTime']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            ${soulFn:formatDateTz(command.result.createTime, DateFormat.DAY_SECOND, timeZone )}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.desc']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            <c:choose>
                                                <c:when test="${command.result.transactionType eq 'favorable'}">
                                                    <%--为什么会有三种写法呢 这是因为它们是互补的 。优惠描述的来源不单一 --%>
                                                    ${command.result._describe['activityName']}${command.result._describe[language]}${dicts.common.transaction_way[command.result._describe['transaction_way']]}${dicts.common.fund_type[command.result._describe['transaction_way']]}
                                                </c:when>
                                                <c:when test="${command.result.transactionType eq 'backwater'}">
                                                    <c:set value="${soulFn:formatDateTz(command.result._describe['date'],DateFormat.YEARMONTH,timeZone)}"
                                                           var="date"></c:set>
                                                    ${date.replace("-", views.fund_auto['年'])}${views.fund_auto['月']}&nbsp;${command.result._describe['period']}${views.fund['FundRecord.record.period']}
                                                </c:when>
                                                <c:when test="${command.result.transactionType eq 'recommend'}">
                                                    <%-- 红利-单次奖励 --%>
                                                    <c:if test="${command.result.transactionWay eq 'single_reward'}">
                                                        <c:if test="${command.result._describe['rewardType'] eq 2}">
                                                            ${views.fund['FundRecord.record.friend']}&nbsp; ${command.result._describe['username']}
                                                        </c:if>
                                                        <c:if test="${command.result._describe['rewardType'] eq 3}">
                                                            ${views.fund['FundRecord.record.recmTip1']}${views.fund['FundRecord.record.recmTip2']}${command.result._describe['username']}${views.fund['FundRecord.record.friend']}
                                                        </c:if>
                                                    </c:if>
                                                    <%-- 天天返 --%>
                                                    <c:if test="${command.result.transactionWay eq 'bonus_awards'}">
                                                        ${views.fund['FundRecord.record.singleReward']}
                                                    </c:if>
                                                </c:when>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.transactionMoney']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result.transactionMoney)}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for=""class="text-gray">${views.fund['FundRecord.record.transactionStatus']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right ${command.result.status eq 'failure' ? 'gray':'green'}">
                                                ${dicts.common.status[command.result.status]}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:when test="${command.result.transactionType eq 'deposit' || command.result.transactionType eq 'withdrawals'}">
                    <div class="mui-scroll">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form">
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.transactionNo']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            ${command.result.transactionNo}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.createTime']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            ${soulFn:formatDateTz(command.result.createTime, DateFormat.DAY_SECOND, timeZone )}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for="" class="text-gray">${views.fund['FundRecord.record.desc']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                            <c:choose>
                                                <c:when test="${command.result.transactionType eq 'deposit'}">
                                                    <%--存款--%>
                                                    <c:if test="${command.result.fundType=='atm_counter'}">
                                                        ${dicts.common.transaction_way[command.result.transactionWay]}
                                                    </c:if>
                                                    <c:if test="${command.result.fundType=='artificial_deposit'}">
                                                        ${views.fund['FundRecord.view.tips']}
                                                    </c:if>
                                                    <c:if test="${command.result.fundType!='atm_counter'&&command.result.fundType!='artificial_deposit'}">
                                                        ${dicts.common.fund_type[command.result.fundType]}
                                                    </c:if>
                                                </c:when>
                                                <c:otherwise>
                                                    <%--取款--%>
                                                    <c:if test="${command.result.fundType=='artificial_withdraw'}">
                                                        ${views.fund['FundRecord.view.manualWithdraw']}
                                                    </c:if>
                                                    <c:if test="${command.result.fundType!='artificial_withdraw'}">
                                                        ${dicts.common.bankname[command.result._describe['bankCode']]}&nbsp;${views.fund['FundRecord.record.bankNoAfter']}&nbsp;${fn:substring(command.result._describe['bankNo'],command.result._describe['bankNo'].length()-4,command.result._describe['bankNo'].length())}
                                                    </c:if>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                                <c:if test="${not empty command.result._describe['auditStatus']}">
                                    <div class="mui-input-row">
                                        <label for="" class="text-gray">${views.fund['FundRecord.view.audit']}${views.fund['FundRecord.record.transactionStatus']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right">
                                                ${dicts.fund.withdraw_status[command.result._describe['auditStatus']]}
                                            </p>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${not empty command.result.failureReason && command.result.status eq 'failure'}">
                                    <div class="mui-input-row"><label for=""class="text-gray">${views.fund['FundRecord.view.failReason']}</label>
                                        <div class="ct">
                                            <p class="mui-text-right">${command.result.failureReason}</p>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${command.result.fundType eq 'bitcoin_fast'}">
                                    <div class="mui-input-row"><label for="" class="text-gray">txId:</label>
                                        <div class="ct">
                                            <p class="mui-text-right">${command.result._describe['bankOrder']}</p>
                                        </div>
                                    </div>
                                    <div class="mui-input-row"><label for="" class="text-gray">${views.themes_auto['交易时间']}:</label>
                                        <div class="ct">
                                            <p class="mui-text-right">
                                                    ${soulFn:formatDateTz(command.result._describe['returnTime'], DateFormat.DAY_SECOND, timeZone )}
                                            </p>
                                        </div>
                                    </div>
                                    <div class="mui-input-row"><label for="" class="text-gray">${views.withdraw_auto['比特币地址']}:</label>
                                        <div class="ct">
                                            <p class="mui-text-right">${command.result._describe['payerBankcard']}</p>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${command.result.fundType != 'bitcoin_fast'}">
                                    <c:if test="${not empty command.result._describe['bankOrder']}">
                                        <div class="mui-input-row"><label for="" class="text-gray">${views.fund['FundRecord.view.orderNo']}</label>
                                            <div class="ct">
                                                <p class="mui-text-right">
                                                        ${command.result._describe['bankOrder']}（${views.fund_auto['后5位']}）
                                                </p>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty command.result._describe['returnTime']}">
                                        <div class="mui-input-row"><label for="" class="text-gray">${views.fund['FundRecord.view.depositTime']}</label>
                                            <div class="ct">
                                                <p class="mui-text-right">
                                                        ${soulFn:formatDateTz(command.result._describe['returnTime'], DateFormat.DAY_SECOND, timeZone )}
                                                </p>
                                            </div>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty command.result.rechargeAddress}">
                                        <div class="mui-input-row"><label for="" class="text-gray">${views.fund_auto['交易地点']}：</label>
                                            <div class="ct">
                                                <p class="mui-text-right">
                                                        ${command.result.rechargeAddress}
                                                </p>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:if>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-bankcard">
                                <c:set var="bankCode" value="${command.result._describe['bankCode']}"/>
                                <c:choose>
                                    <%--人工存提--%>
                                    <c:when test="${command.result.fundType eq 'artificial_deposit'}">
                                        <div class="hd">
                                            <p>${views.fund['FundRecord.view.tips']}</p>
                                        </div>
                                    </c:when>
                                    <c:when test="${command.result.fundType eq 'qqwallet_scan'||command.result.fundType eq 'alipay_scan'||command.result.fundType eq 'other_fast' || command.result.fundType eq 'wechatpay_scan'||command.result.fundType eq 'wechatpay_fast' || command.result.fundType eq 'alipay_fast'||command.result.fundType eq 'bitcoin_fast'
                                                    ||command.result.fundType eq 'onecodepay_fast'||command.result.fundType eq 'jdwallet_fast'||command.result.fundType eq 'bdwallet_fast'||command.result.fundType eq 'union_pay_scan'||command.result.fundType eq 'bdwallet_scan'
                                                    ||command.result.fundType eq 'jdpay_scan'}">

                                        <div class="hd">
                                            <p>
                                                <c:set var="bankCss" value="${bankCode}"/>
                                             <%--   <c:if test="${command.result.fundType eq 'qqwallet_scan'}">
                                                    <c:set var="bankCss" value="qq_pay"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'alipay_scan'||command.result.fundType eq 'alipay_scan'}">
                                                    <c:set var="bankCss" value="alipay"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'wechatpay_scan'||command.result.fundType eq 'wechatpay_fast'}">
                                                    <c:set var="bankCss" value="wechatpay"/>
                                                </c:if>--%>
                                                <c:set var="isOther" value="${bankCode eq 'other' && !empty command.result._describe['customBankName']}"/>

                                                <c:set var="scanCss" value="${bankCode}"/>
                                                <c:if test="${command.result.fundType eq 'alipay_scan'||command.result.fundType eq 'alipay_fast'}">
                                                    <c:set var="scanCss" value="alipay"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'wechatpay_scan'||command.result.fundType eq 'wechatpay_fast'}">
                                                    <c:set var="scanCss" value="wechatpay"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'qqwallet_scan'|| command.result.fundType eq 'qqwallet_fast'}">
                                                    <c:set var="scanCss" value="qqwallet"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'jdpay_scan'|| command.result.fundType eq 'jdwallet_fast'}">
                                                    <c:set var="scanCss" value="jdwallet"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'bdwallet_san'|| command.result.fundType eq 'bdwallet_fast'}">
                                                    <c:set var="scanCss" value="bdwallet"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'union_pay_scan'}">
                                                    <c:set var="scanCss" value="unionpay"/>
                                                </c:if>
                                                <c:if test="${command.result.fundType eq 'onecodepay_fast'}">
                                                    <c:set var="scanCss" value="onecodepay"/>
                                                </c:if>

                                                <span class="${isOther?'':'pay-third '}${bankCss}"/>
                                                <i>${isOther?command.result._describe['customBankName']:''}</i>
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:when test="${command.result.fundType eq 'digiccy_scan'}">
                                        <div class="hd">
                                            <p>
                                                <span class="pay-third ${bankCode}"/>
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="hd">
                                            <p>
                                                <c:set var="isOther" value="${bankCode eq 'other_bank' && !empty command.result._describe['customBankName']}"/>
                                                <span class="${isOther?'':'pay-bank '}${bankCode}"/>
                                                <i>${isOther?'':command.result._describe['customBankName']}</i>
                                            </p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <div class="ct">
                                    <c:if test="${bankCode eq 'alipay' || bankCode eq 'wechatpay'}">
                                            <p><span>${bankCode eq 'alipay'?'您的支付宝账号':'您的微信账号'}:</span>
                                                    ${soulFn:overlayString(command.result.payerBankcard)}
                                            </p>
                                    </c:if>
                                    <c:if test="${(bankCode ne 'alipay' && bankCode ne 'wechatpay') && not empty command.result._describe['realName']}">
                                            <p><span><%--姓名--%>${views.fund['FundRecord.view.name']}:</span>
                                                    ${soulFn:overlayName(command.result._describe['realName'])}
                                            </p>
                                    </c:if>
                                    <c:if test="${command.result.transactionType eq 'withdrawals'}">
                                        <p>
                                            <span><%--真实姓名--%>${views.fund['FundRecord.view.name']}:</span>
                                                ${soulFn:overlayName(sysUserVo.result.realName)}
                                        </p>
                                        <p>
                                            <span>${views.fund_auto['取款金额']}:</span>
                                            <span class="orange">
                                            ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result.transactionMoney)}
                                            </span>
                                        </p>
                                        <c:if test="${not empty withdrawVo.result.deductFavorable&&withdrawVo.result.deductFavorable>0}">
                                            <p>
                                                <span><%--扣除优惠--%>${views.fund['withdraw.check.playerWithdraw.deductedFavourable']}:</span>
                                                    ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(withdrawVo.result.deductFavorable)}
                                            </p>
                                        </c:if>
                                        <p>
                                            <c:if test="${not empty withdrawVo.result.counterFee && withdrawVo.result.counterFee>0}">
                                                <span><%--手续费--%>${views.fund['FundRecord.view.poundage']}:</span>
                                                ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(withdrawVo.result.counterFee)}
                                            </c:if>
                                            <c:if test="${empty withdrawVo.result.counterFee ||withdrawVo.result.counterFee==0}"><span>${views.fund['FundRecord.view.poundage']}:</span>${views.fund_auto['免手续费']}</c:if>
                                        </p>
                                        <c:if test="${not empty withdrawVo.result.administrativeFee && withdrawVo.result.administrativeFee>0}">
                                            <p>
                                                <span><%--行政费--%>${views.fund['withdraw.check.playerWithdraw.forAdministrationCost']}:</span>
                                                    ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(withdrawVo.result.administrativeFee)}
                                            </p>
                                        </c:if>
                                        <p><span>${views.fund_auto['实际到账']}:</span>
                                        <span class="orange">
                                        ${siteCurrencySign}&nbsp;${withdrawVo.result.withdrawActualAmount>0?'-':''}${soulFn:formatCurrency(withdrawVo.result.withdrawActualAmount)}
                                        </span>
                                        </p>
                                    </c:if>
                                    <c:if test="${command.result.transactionType ne 'withdrawals'}">
                                        <c:if test="${command.result.fundType eq 'bitcoin_fast'}">
                                            <p>
                                                <span>${views.themes_auto['比特币']}:</span>
                                                Ƀ<fmt:formatNumber value="${command.result._describe['bitAmount']}" pattern="#.########"/>
                                            </p>
                                            <c:if test="${command.result.rechargeAmount!=0}">
                                                <p>
                                                    <span>${views.fund['Deposit.deposit.rechargeAmount']}</span>
                                                    ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result.rechargeAmount)}
                                                </p>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${command.result.fundType != 'bitcoin_fast'}">
                                            <p>
                                                <span><%--存款金额--%>${views.fund['Deposit.deposit.rechargeAmount']}</span>
                                                    ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result.rechargeAmount)}
                                            </p>
                                            <c:if test="${not empty command.result._describe['poundage']}">
                                                <p>
                                                    <span><%--手续费--%>${command.result._describe['poundage']-0 > 0 ?views.fund['FundRecord.view.rebackPoundage']:views.fund['FundRecord.view.poundage']}:</span>
                                                        ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result._describe['poundage'])}
                                                </p>
                                            </c:if>
                                            <p>
                                                <span>${views.fund_auto['实际到账']}:</span>
                                            <span class="orange">
                                                ${siteCurrencySign}&nbsp;${soulFn:formatCurrency(command.result.rechargeTotalAmount)}
                                            </span>
                                            </p>
                                        </c:if>
                                    </c:if>
                                    <p><span><%--状态--%>${views.fund['FundRecord.record.transactionStatus']}:</span>
                                        <a class="${command.result.status eq 'success' ? 'green':'text-black'}">${dicts.common.status[command.result.status]}</a>
                                    </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:when test="${command.result.transactionType eq 'transfers'}">
                    <div class="mui-scroll">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form">
                                <div class="mui-input-row">
                                    <label for=""
                                           class="text-gray"><%--交易号--%>${views.fund['FundRecord.record.transactionNo']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                                ${command.result.transactionNo}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for=""
                                           class="text-gray"><%--创建时间--%>${views.fund['FundRecord.record.createTime']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">

                                                ${soulFn:formatDateTz(command.result.createTime, DateFormat.DAY_SECOND, timeZone )}
                                        </p>
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label for=""
                                           class="text-gray"><%--金额--%>${fn:replace(views.fund['Transfer.transfer.transferAmount'],'：','')}</label>
                                    <div class="ct">
                                        <p class="mui-text-right">
                                                ${command.result.transactionMoney}
                                        </p>
                                    </div>
                                </div>
                                <c:choose>
                                    <c:when test="${command.result.fundType eq 'transfer_into'}">
                                        <div class="mui-input-row">
                                            <label for=""
                                                   class="text-gray"><%--转出--%>${fn:replace(views.fund['Transfer.transfer.transferOut'],'：','')}</label>
                                            <div class="ct">
                                                <p class="mui-text-right">${gbFn:getApiName(command.result._describe['API'].toString())}</p>
                                            </div>
                                        </div>
                                        <div class="mui-input-row">
                                            <label for=""
                                                   class="text-gray"><%--转入--%>${fn:replace(views.fund['Transfer.transfer.transferInto'],'：','')}</label>
                                            <div class="ct">
                                                <p class="mui-text-right"><%--我的钱包--%>${views.fund['FundRecord.record.playerWallet']}</p>
                                            </div>
                                        </div>
                                    </c:when>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${command.result.fundType eq 'transfer_out'}">
                                        <div class="mui-input-row">
                                            <label for=""
                                                   class="text-gray"><%--转出--%>${fn:replace(views.fund['Transfer.transfer.transferOut'],'：','')}</label>
                                            <div class="ct">
                                                <p class="mui-text-right"><%--我的钱包--%>${views.fund['FundRecord.record.playerWallet']}</p>
                                            </div>
                                        </div>
                                        <div class="mui-input-row">
                                            <label for=""
                                                   class="text-gray"><%--转入--%>${fn:replace(views.fund['Transfer.transfer.transferInto'],'：','')}</label>
                                            <div class="ct">
                                                <p class="mui-text-right">${gbFn:getApiName(command.result._describe['API'].toString())}</p>
                                            </div>
                                        </div>
                                    </c:when>
                                </c:choose>
                                <div class="mui-input-row">
                                    <label for=""
                                           class="text-gray"><%--状态--%>${views.fund['FundRecord.record.transactionStatus']}</label>
                                    <div class="ct">
                                        <p class="mui-text-right"><span
                                                class="${command.result.status eq 'success'?'green':'red'}">${dicts.common.status[command.result.status]}
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:when>
            </c:choose>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</div>
<script>
    $('._userAsset').removeClass('mui-hide');
    curl(['common/MobileBasePage', 'site/common/Assets'], function(Page, Assets) {
        page = new Page();
        asset = new Assets();
    });
</script>
</body>
