<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}比特币</title>
    <%@ include file="../../include/include.head.jsp" %>
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
            <h1 class="mui-title">${empty userBankCard ? views.withdraw_auto['添加min'] : views.withdraw_auto['我的']}比特币地址</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <c:choose>
                    <c:when test="${empty userBankCard}">
                        <form name="btcForm">
                        <div id="validateRule" style="display: none">${validate}</div>
                        <input type="hidden" name="action" value="${action}"/>
                        <gb:token/>
                        <div class="bit_bind_wrap">
                            <div class="tit">绑定比特币钱包</div>
                            <div class="sub_tit">取款前请先绑定比特币钱包地址，成功取款后我们会将款项转入您填写的钱包账户</div>
                            <div class="tit">比特币钱包地址：</div>
                            <div><input type="text" class="bit_input" id="result.bankcardNumber" name="result.bankcardNumber"/></div>
                            <div class="btn_wrap">
                                <div class="mui-col-xs-6 mui-pull-left"><a class="mui-btn btn_reset">重置</a></div>
                                <div class="mui-col-xs-6 mui-pull-left"><a class="mui-btn btn_bind" id="bindBtc">绑定</a></div>
                            </div>
                        </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="cont m-t-sm">
                            <div class="mui-row">
                                <div class="mui-input-group mine-form">
                                    <div class="mui-input-row"><label>比特币地址</label>
                                        <div class="ct">
                                            <span>${gbFn:overlayBankcard(userBankCard.bankcardNumber)}</span>
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
<script>
    curl(['site/withdraw/bankcard/Btc.js', 'site/passport/password/PopSecurityPassword', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
        function (Page, Security, Menu, Footer, Dynamic) {
            page = new Page();
            page.security = new Security();
            page.menu = new Menu();
            page.menu = new Footer();
            page.dynamic = new Dynamic();
        });
</script>