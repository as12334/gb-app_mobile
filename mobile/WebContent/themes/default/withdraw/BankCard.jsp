<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.withdraw_auto['管理银行卡']}</title>
    <%@ include file="/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css" />
    <style type="text/css">
        div.ct {
            padding-right: 10px;
        }
    </style>
</head>

<body class="gb-theme">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-icon mui-icon-left-nav mui-pull-left" data-href="${root}/my/index.html"></a>
            <h1 class="mui-title">${views.withdraw_auto['我的银行卡']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <div class="p-a-sm">
                    <div id="segmentedControl" class="mui-segmented-control">
                        <a class="mui-control-item" data-href="${root}/bankCard/page/addCard.html">${views.withdraw_auto['添加银行卡']}</a>
                        <a class="mui-control-item mui-active" data-href="${root}/bankCard/page/addCard.html">${views.withdraw_auto['管理银行卡']}</a>
                    </div>
                </div>

                    <div class="gb-fund-step">
                        <c:choose>
                            <c:when test="${not empty userBankCard}">
                                <div class="mui-card m-t-0">
                                    <div class="mui-card-header">
                                        ${dicts.common.bankname[userBankCard.bankName]}
                                    </div>
                                    <div class="mui-card-content">
                                        <div class="mui-card-content-inner p-a-sm">
                                            <p>${views.withdraw_auto['收款人']}：${userBankCard.bankcardMasterName}</p>
                                            <h4 class="text-blue">${soulFn:overlayString(soulFn:formatBankCard(userBankCard.bankcardNumber))}</h4>
                                        </div>
                                    </div>
                                    <div class="mui-card-footer">
                                        <a class="mui-card-link text-gray2">${views.withdraw_auto['开户行']}：${userBankCard.bankDeposit}</a>
                                            <%--<a class="mui-card-link"><span class="mui-icon mui-icon-compose"></span></a>
                                            <a class="mui-card-link"><span class="mui-icon mui-icon-trash"></span></a>--%>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                ${views.withdraw_auto['没有绑定银行卡']}
                            </c:otherwise>
                        </c:choose>
                    </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/my/bankCard/BankCard.js?v=${rcVersion}"></script>
</body>
</html>
