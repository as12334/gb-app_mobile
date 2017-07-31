<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['银行卡']}</title>
    <%@ include file="/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
</head>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['银行卡']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <c:choose>
                    <c:when test="${empty userBankCard}">
                        <div class="gb-bindcard-box" style="display: block;">
                            <form name="bankcardForm">
                                <div id="validateRule" style="display: none">${validate}</div>
                                <input type="hidden" name="action" value="${action}"/>
                                <gb:token/>
                                <div class="cont m-t-sm">
                                    <div class="mui-row">
                                        <div class="mui-input-group mine-form">
                                            <div class="mui-input-row">
                                                <label>${views.withdraw_auto['真实姓名']}<br>
                                                    <small>${views.withdraw_auto['银行卡户名与真实姓名一致才能取款成功']}</small>
                                                </label>
                                                <div class="ct">
                                                    <c:choose>
                                                        <c:when test="${empty realName}">
                                                            <input type="text" placeholder="${views.withdraw_auto['请输入真实姓名']}" name="result.bankcardMasterName" autocomplete="off">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="mui-text-right" style="font-size: 12px; color: #999999">${soulFn:overlayName(realName)}</span>
                                                            <input type="hidden" value="${realName}" name="result.bankcardMasterName">
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div class="mui-input-row" id="selectBank"><label>${views.withdraw_auto['银行']}</label>
                                                <div class="ct">
                                                    <div class="gb-select">
                                                        <a id="bankLabel">${views.withdraw_auto['请选择']}</a>
                                                        <input name="result.bankName" type="hidden"/>
                                                    </div>
                                                    <i class="arrow"></i>
                                                </div>
                                            </div>
                                            <div class="mui-input-row"><label>${views.withdraw_auto['卡号']}</label>
                                                <div class="ct">
                                                    <input type="number" placeholder="${views.withdraw_auto['请输入卡号']}" name="result.bankcardNumber"/>
                                                </div>
                                            </div>
                                            <div class="mui-input-row">
                                                <label>${views.withdraw_auto['开户银行']}<br>
                                                    <small>${views.withdraw_auto['选择其他银行时必填']}</small>
                                                </label>
                                                <div class="ct">
                                                    <input type="text" placeholder="${views.withdraw_auto['例如']}" name="result.bankDeposit"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="mui-row">
                                        <div class="gb-form-foot">
                                            <button class="mui-btn mui-btn-primary submit" id="submitBankCard">${views.withdraw_auto['绑定']}</button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="cont m-t-sm">
                            <div class="mui-row">
                                <div class="mui-input-group mine-form">
                                    <div class="mui-input-row">
                                        <label>${views.withdraw_auto['真实姓名']}</label>
                                        <div class="ct">
                                            <span class="mui-text-right">${soulFn:overlayName(realName)}</span>
                                        </div>
                                    </div>
                                    <div class="mui-input-row"><label>${views.withdraw_auto['银行']}</label>
                                        <div class="ct">
                                            <span>${dicts.common.bankname[userBankCard.bankName]}</span>
                                        </div>
                                    </div>
                                    <div class="mui-input-row"><label>${views.withdraw_auto['卡号']}</label>
                                        <div class="ct">
                                            <span>${gbFn:overlayBankcard(userBankCard.bankcardNumber)}</span>
                                        </div>
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.withdraw_auto['开户银行']}</label>
                                        <div class="ct">
                                            <span class="mui-text-right">${userBankCard.bankDeposit}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
</html>
<script type="text/javascript" src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script>
    curl(['site/withdraw/bankcard/Bankcard', 'site/passport/password/PopSecurityPassword', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
            function (Page, Security, Menu, Footer, Dynamic) {
                page = new Page();
                page.security = new Security();
                page.menu = new Menu();
                page.menu = new Footer();
                page.dynamic = new Dynamic();
            });
</script>
