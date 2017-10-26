<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <c:if test="${os ne 'app_ios'}">
                    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                </c:if>
                <h1 class="mui-title">${views.mine_auto['额度转换']}</h1>
            </header>
        </c:if>
        <center>
            <img src="${resRoot}/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
        </center>
        </body>
    </c:when>
    <c:otherwise>
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

    </c:otherwise>
</c:choose>

