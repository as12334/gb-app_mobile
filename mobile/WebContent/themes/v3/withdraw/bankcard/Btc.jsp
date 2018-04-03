<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['比特币地址']}</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
</head>

<body class="bit-wallet">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['比特币地址']}</h1>
</header>

<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <c:choose>
            <c:when test="${empty userBankCard}">
                <form name="btcForm">
                    <div id="validateRule" style="display: none">${validate}</div>
                    <input type="hidden" name="action" value="${action}"/>
                    <gb:token/>
                    <div class="bit_bind_wrap">
                        <div class="tit">${views.themes_auto['绑定比特币钱包']}</div>
                        <div class="sub_tit">取款前请先绑定比特币钱包地址，成功取款后我们会将款项转入您填写的钱包帐户</div>
                        <div class="tit">${views.themes_auto['比特币钱包地址']}：</div>
                        <div><input type="text" class="bit_input" id="result.bankcardNumber" name="result.bankcardNumber"/></div>
                        <div class="btn_wrap">
                            <div class="mui-col-xs-6 mui-pull-left"><a class="mui-btn btn_reset">${views.themes_auto['重置']}</a></div>
                            <div class="mui-col-xs-6 mui-pull-left">
                                    <a href="" class="mui-btn btn_bind" id="bindBtc" data-rel='{"target":"submitBtc","opType":"function"}'>${views.themes_auto['绑定']}</a>
                            </div>
                        </div>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <div class="cont m-t-sm">
                    <div class="mui-row">
                        <div class="mui-input-group mine-form">
                            <div class="mui-input-row"><label>${views.themes_auto['比特币地址']}</label>
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

<!--提交成功弹窗-->
<div class="masker darker" style="display: none;"></div>
<div class="gb-bindcard-box bit_addr" style="display: none;"><span data-rel='{"target":"closeConfirm","opType":"function"}' class="close mui-icon mui-icon-closeempty"></span>
    <div class="cont">
        <div class="tit">确认提交吗?</div>
        <div class="bit_addr_txt">比特币钱包地址:</div>
        <div class="bit_addr_code" id="confirmBtc"></div>
        <div class="btn_wrap">
            <div class="mui-col-xs-6 mui-pull-left"><a href="" class="mui-btn btn_cancel">取消</a></div>
            <div class="mui-col-xs-6 mui-pull-left"><a href="" class="mui-btn btn_submit" id="bindBtn" data-rel='{"target":"submitConfirm","opType":"function"}'>提交</a></div>
        </div>
    </div>
</div>


</body>
<%@ include file="../../include/include.js.jsp" %>

<script type="text/javascript" src="${resRoot}/js/mui/mui.poppicker.js"></script>
<script type="text/javascript" src="${resRoot}/js/mui/mui.picker.js"></script>
<script type="text/javascript" src="${resRoot}/js/withdraw/bankcard/Btc.js"></script>

</html>
<%--<%@ include file="/include/include.footer.jsp" %>--%>

