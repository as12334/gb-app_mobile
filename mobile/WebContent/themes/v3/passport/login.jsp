<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../include/include.head.jsp" %>
    <title>${views.passport_auto['登录']}</title>
</head>
<body class="login">
<!-- 主页面标题 -->
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
    <h1 class="mui-title">${views.passport_auto['登录']}</h1>
    <a class="mui-icon mui-icon-home mui-pull-right " data-rel='{"target":"goToHomePageOnly","opType":"function"}'></a>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="user-img"></div>
        <!-- 主界面具体展示内容 -->
        <div class="login-form">
            <form id="mui-input-group">
                <div class="username">
                    <label for="">${views.passport_auto['请输入账号']}</label>
                    <input type="text" name="username" id="username" class="mui-input-clear"
                           placeholder="${views.passport_auto['请输入账号']}">
                    <p id="username-error-msg" class="warn">
                    </p>
                </div>
                <div class="password">
                    <label>${views.passport_auto['请输入密码']}</label>
                    <input for="" type="password" name="password" id="password" class="mui-input-password"
                           placeholder="${views.passport_auto['请输入密码']}">
                    <p id="password-error-msg" class="warn">
                    </p>
                </div>
                <div class="code _captcha ${isOpenCaptcha ? 'final' : ''}"
                     id="captcha_div" ${isOpenCaptcha ? "" :'style="display: none"'}>
                    <label>${views.passport_auto['请输入验证码']}</label>
                    <p id="captcha-error-msg" class="warn">
                    </p>
                    <div class="div_flex">
                        <input type="text" class="mui-input-clear" name="captcha" id="captcha" maxlength="4"
                               placeholder="${views.passport_auto['请输入验证码']}">
                        <img class="captcha_img" data-rel='{"target":"captchaChange","opType":"function"}' data-src="${root}/captcha/code.html" alt="">
                    </div>
                </div>
                <c:if test="${isRecover}">
                <p class='forget-pas'><a data-rel='{"target":"${root}/help/forgetPassword.html","opType":"href"}'>忘记密码？</a></p></c:if>
                <a data-rel='{"target":"loginOk","opType":"function"}' type="button"
                   class="btn btn-login btn-ok">立即登录</a>
                <a data-rel='{"target":"${root}/signUp/index.html","opType":"href"}' type="button"
                   class="btn btn-regist">免费开户</a>
            </form>
        </div>
    </div> <!--mui-scroll 闭合标签-->
</div>  <!--mui-content 闭合标签-->
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/passport/Login.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>