<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<form id="transferForm" class="m-b-0" onsubmit="return false">
    <div id="validateRule" style="display: none">${validateRule}</div>
    <gb:token/>
    <div class="mui-row">
        <c:set var="transferPendingAmount" value="${empty transferPendingAmount?0:transferPendingAmount}"/>
        <c:if test="${transferPendingAmount > 0}">
            <div class="mui-input-group mine-form">
                <div class="bankitem">
                    <p class="mui-text-left" style="padding: 12px;">
                        ${views.transfer_auto['转账处理中']}：<span class="text-green">${player.currencySign}${soulFn:formatCurrency(transferPendingAmount)}</span>
                    </p>
                </div>
            </div>
        </c:if>

        <div class="mui-input-group mine-form m-t-sm">
            <div class="mui-input-row"><label>${views.transfer_auto['转出账户']}</label>
                <div class="ct">
                    <div class="gb-select width-all">
                        <a data-href="#" class="mui-btn mui-btn-link gb-input-link turn" default="${views.transfer_auto['我的钱包']}" defaultValue="wallet" data-value="transferInto" id="transferOut">
                            <span>${views.transfer_auto['我的钱包']}</span>
                            <input name="transferOut" value="wallet" type="hidden"/>
                        </a>
                    </div>
                    <i class="arrow"></i>
                </div>
            </div>
            <div class="mui-input-row"><label>${views.transfer_auto['转入账户']}</label>
                <div class="ct">
                    <div class="gb-select width-all">
                        <a data-href="#" class="mui-btn mui-btn-link gb-input-link turn" default="${views.transfer_auto['请选择']}" data-value="transferOut" id="transferInto">
                            <span>${views.transfer_auto['请选择']}</span>
                            <input name="transferInto" value="" type="hidden"/>
                        </a>
                    </div>
                    <i class="arrow"></i>
                </div>
            </div>
            <div class="mui-input-row"><label>${views.transfer_auto['金额']}</label>
                <div class="ct">
                    <input type="number" class="gb-money" placeholder="${views.transfer_auto['请输入']}" name="result.transferAmount" autocomplete="off">
                </div>
            </div>
        </div>
    </div>
    <div class="mui-row">
        <div class="gb-form-foot" id="mui-content-padded">
            <button class="mui-btn mui-btn-primary submit" id="transfersMoney">${views.transfer_auto['确认提交']}</button>
        </div>
    </div>
</form>
