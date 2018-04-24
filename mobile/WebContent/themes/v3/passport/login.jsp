<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../include/include.head.jsp" %>
    <title>${views.passport_auto['登录']}</title>
</head>
<body>
<!-- 主页面标题 -->
<header class="mui-bar mui-bar-nav mui-bar-blue mui-action-back">
    <a class="mui-icon mui-icon-arrowleft mui-pull-left"></a>
    <h1 class="mui-title">${views.passport_auto['登录']}</h1>
    <a class="mui-icon mui-icon-home mui-pull-right " data-rel='{"target":"${root}/mainIndex.html","opType":"href"}'></a>
</header>
<div class="mui-content mui-scroll-wrapper mui-content-without-footer-address login-content">
    <div class="mui-scroll">
        <!-- 主界面具体展示内容 -->
        <section class="type-content">
            <div class="login-icon"></div>
            <div id="login_content"	 class="mui-control-content mui-active login-part"><!--登录部分的内容-->
                <form class="mui-input-group">

                    <div class="mui-input-row">
                        <label class="mui-ellipsis">${views.passport_auto['请输入账号']}</label>
                        <span id="username-error-msg" class="error-info">
				        	<%--<i class="mui-icon mui-icon-info"></i>${SK_Passport_Rs.propMessages["username"]}--%>
				        </span>
                        <input type="text" name="username" id="username" class="mui-input-clear" placeholder="${views.passport_auto['请输入账号']}">
                    </div>
                    <div class="mui-input-row password-input-row">
                        <label>${views.passport_auto['请输入密码']}</label>
                        <span id="password-error-msg" class="error-info">
				        	<%--<i class="mui-icon mui-icon-info"></i>输入密码错误--%>
				        </span>
                        <input type="password" name="password" id="password" class="mui-input-password" placeholder="${views.passport_auto['请输入密码']}">
                    </div>
                    <div class="mui-input-row _captcha ${isOpenCaptcha ? 'final' : ''}" id="captcha_div" ${isOpenCaptcha ? "" :'style="display: none"'}>
                        <label>${views.passport_auto['请输入验证码']}</label>
                        <span id="captcha-error-msg" class="error-info">
                            <%--<i class="mui-icon mui-icon-info"></i>验证码错误--%>
                        </span>
                        <div class="mui-flex gb-vcode">
                            <input type="text" class="mui-input-clear" name="captcha" id="captcha" maxlength="4" placeholder="${views.passport_auto['请输入验证码']}">
                            <img class="captcha_img" data-rel='{"target":"captchaChange","opType":"function"}' src="${root}/captcha/code.html" data-src="${root}/captcha/code.html" alt="">
                        </div>
                    </div>
                    <div class="mui-button-row">
                        <button data-rel='{"target":"loginOk","opType":"function"}' type="button" class="mui-btn mui-btn-block btn-ok" >立即登录</button>
                        <button data-rel='{"target":"${root}/signUp/index.html","opType":"href"}' type="button" class="mui-btn mui-btn-block btn-cancel" >免费开户</button>
                    </div>
                </form>
            </div>
        </section>
    </div> <!--mui-scroll 闭合标签-->
</div>  <!--mui-content 闭合标签-->
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/passport/Login.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>