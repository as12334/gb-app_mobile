<%--<c:set var="siteId" value="<%=SessionManager.getSiteId() %>" />--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page login-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.passport_auto['登录']}</h1>
            <c:if test="${os ne 'app_ios' }">
            <a class="mui-icon mui-icon-home mui-pull-right" data-href="/mainIndex.html"></a>
            </c:if>
        </header>
        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <div class="mui-row">
                    <div class="mui-input-group mine-form m-t-sm">

                        <div class="mui-input-row">
                            <div class="form-row">
                                <div class="cont">
                                    <input type="text" class="ico1" placeholder="${views.passport_auto['请输入账号']}" name="username"
                                           id="username" autocomplete="off">
                                    <div id="username-error-msg" style="font-size:10px; padding-left: 25px;">
                                        ${SK_Passport_Rs.propMessages["username"]}
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mui-input-row _pass ${isOpenCaptcha ? '' : 'final'}">
                            <div class="form-row">
                                <div class="cont">
                                    <input type="password" class="ico2" placeholder="${views.passport_auto['请输入密码']}" name="password" id="password">
                                </div>
                                <div  id="password-error-msg" style="font-size: 10px; padding-left: 25px;">
                                    ${sessionScope.SK_Passport_Rs.propMessages["password"]}
                                </div>
                            </div>
                        </div>

                        <div class="mui-input-row _captcha ${isOpenCaptcha ? 'final' : ''}" id="captcha_div" ${isOpenCaptcha ? "" :'style="display: none"'}>
                            <div class="form-row">
                                <div class="cont">
                                    <input type="text" class="mui-input ico6" maxlength="4" placeholder="${views.passport_auto['请输入验证码']}" name="captcha" id="captcha">
                                    <div class="gb-vcode">
                                        <img class="_captcha_img" src="${root}/captcha/code.html" data-src="${root}/captcha/code.html" alt="">
                                    </div>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div class="mui-input-row">
                        <div class="gb-form-foot" style="height: 65px;">
                            <button  class="mui-btn mui-btn-primary submit _login" type="button">${views.passport_auto['立即登录']}</button>
                            <p class="no-account">${views.passport_auto['没有账号']}？</p>
                            <div class="login-line"></div>
                            <button class="mui-btn mui-btn-primary btn-reg" data-href="/signUp/index.html" type="button">${views.passport_auto['免费开户']}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<%@ include file="/include/include.base.js.common.jsp" %>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
</script>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/global.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/passport/Login.js?v=${rcVersion}"></script>