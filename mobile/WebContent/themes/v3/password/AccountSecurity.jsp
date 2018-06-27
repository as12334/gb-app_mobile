<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.themes_auto['账号安全']}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body class="change-loginpassword">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.themes_auto['账号安全']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll mui-row">

        <ul class="mui-input-group mine-form"><!--第三行-->
            <c:if test="${isCash}">
                <li class="mui-input-row">
                    <soul:button target="${root}/bankCard/page/addCard.html" text="" opType="href"
                                 cssClass="">
                        <p style="margin-left: 20px">${views.themes_auto['银行卡']}</p>
                    </soul:button>
                </li>
            </c:if>
            <c:if test="${isBit}">
                <li class="mui-input-row">
                    <soul:button target="${root}/bankCard/page/addBtc.html" text="" opType="href"
                                 cssClass="">
                        <p style="margin-left: 20px">${views.themes_auto['比特币钱包']}</p>
                    </soul:button>
                </li>
            </c:if>
            <li class="mui-input-row">
                <soul:button target="${root}/my/password/editPassword.html" text="" opType="href"
                             cssClass="">
                    <p style="margin-left: 20px">${views.themes_auto['修改登录密码']}</p>
                </soul:button>
            </li>

            <li class="mui-input-row">
                <soul:button target="${root}/passport/securityPassword/edit.html" text="" opType="href"
                             cssClass="">
                    <p style="margin-left: 20px">${views.themes_auto['修改安全密码']}</p>
                </soul:button>
            </li>
        </ul>
    </div>
</div>
</body>

<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/password/SafePasswordCommon.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/password/SetSecurityPassword.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
