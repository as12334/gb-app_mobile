<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['银行卡']}</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
</head>

<body class="bankcard">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['银行卡']}</h1>
</header>

<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <c:choose>
            <c:when test="${empty userBankCard}">
                <form name="bankcardForm">
                    <div id="validateRule" style="display: none">${validate}</div>
                    <input type="hidden" name="action" value="${action}"/>
                    <gb:token/>
                    <div class="mui-row">
                        <div class="mui-input-group mine-form">
                            <div class="mui-input-row">
                                <label for="">${views.withdraw_auto['真实姓名']}<br>
                                    <small>${views.withdraw_auto['银行卡户名与真实姓名一致才能取款成功']}</small>
                                </label>
                                <div class="ct">
                                    <c:choose>
                                        <c:when test="${empty realName}"><%--如果真实姓名为空，则显示输入框--%>
                                            <input type="text" placeholder="${views.withdraw_auto['请输入真实姓名']}" name="result.bankcardMasterName" id="result.bankcardMasterName" autocomplete="off">
                                        </c:when>
                                        <c:otherwise>
                                            <p class="mui-text-right text-gray">${soulFn:overlayName(realName)}</p> <%--如果已设置真实姓名，则显示出遮挡姓名--%>
                                            <input type="hidden" value="${realName}" name="result.bankcardMasterName">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="mui-input-row" id="selectBank"><label for="">${views.withdraw_auto['银行']}</label>
                                <div class="ct">
                                    <div class="gb-select">
                                        <a <%--href="#popover"--%> id="bankLabel">${views.withdraw_auto['请选择']}<i class="mui-icon mui-icon-arrowdown"></i></a>
                                        <input name="result.bankName" type="hidden" /> <%--银行名称--%>
                                    </div>
                                </div>
                            </div>
                            <div class="mui-input-row"><label for="result.bankcardNumber">${views.withdraw_auto['卡号']}</label>
                                <div class="ct">
                                    <input type="number" placeholder="${views.withdraw_auto['请输入卡号']}" name="result.bankcardNumber" id="result.bankcardNumber" />
                                </div>
                            </div>
                            <div class="mui-input-row">
                                <label for="result.bankDeposit">${views.withdraw_auto['开户银行']}<br>
                                    <small>选择“其他”银行时必填</small>
                                </label>
                                <div class="ct">
                                    <input type="text" placeholder="${views.withdraw_auto['例如']}" name="result.bankDeposit" id="result.bankDeposit" />
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mui-row">
                        <div class="gb-form-foot">
                            <a href="" class="mui-btn mui-btn-primary submit" data-rel='{"target":"submitBankCard","opType":"function"}' id="submitBankCard">${views.withdraw_auto['绑定']}</a>
                        </div>
                    </div>
                </form>
            </c:when>

            <c:otherwise>
                <div class="mui-row">
                    <div class="mui-input-group mine-form">
                        <div class="mui-input-row">
                            <label>${views.withdraw_auto['真实姓名']}</label>
                            <div class="ct">
                                <p>${soulFn:overlayName(realName)}</p>
                            </div>
                        </div>
                        <div class="mui-input-row"><label>${views.withdraw_auto['银行']}</label>
                            <div class="ct">
                                <p>${dicts.common.bankname[userBankCard.bankName]}</p>
                            </div>
                        </div>
                        <div class="mui-input-row"><label>${views.withdraw_auto['卡号']}</label>
                            <div class="ct">
                                <p>${gbFn:overlayBankcard(userBankCard.bankcardNumber)}</p>
                            </div>
                        </div>
                        <div class="mui-input-row">
                            <label>${views.withdraw_auto['开户银行']}</label>
                            <div class="ct">
                                <p>${userBankCard.bankDeposit}</p>
                            </div>
                        </div>

                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>


</body>
<%@ include file="../../include/include.js.jsp" %>

<script type="text/javascript" src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/withdraw/bankcard/Bankcard.js?v=${rcVersion}"></script>


</html>
<%--<%@ include file="/include/include.footer.jsp" %>--%>

