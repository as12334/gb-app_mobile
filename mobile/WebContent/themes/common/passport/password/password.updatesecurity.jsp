<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page">
    <input type="hidden" name="hasName" value="${hasName}" />
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${views.passport_auto['修改安全密码']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper ${os eq 'android' ? 'p-t-0': ''}">
                <div class="mui-scroll">
                    <form id="updateSecurityPwd">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form m-t-sm">
                                <div class="mui-input-row"><label>${views.passport_auto['真实姓名']}</label>
                                    <div class="ct">
                                        <input type="hidden" name="needCaptcha" value="${isOpenCaptcha}" />
                                        <input type="text" name="realName" maxlength="30" placeholder="${views.passport_auto['请验证真实姓名']}">
                                    </div>
                                </div>
                                <div class="mui-input-row"><label>${views.passport_auto['当前密码']}</label>
                                    <div class="ct">
                                        <input type="password" placeholder="${views.passport_auto['请输入当前密码']}" name="originPwd" autocomplete="off" maxlength="6">
                                    </div>
                                </div>
                                <div class="mui-input-row">
                                    <label>${views.passport_auto['新密码']}</label>
                                    <div class="ct">
                                        <input type="password" placeholder="${views.passport_auto['请输入新密码']}" name="pwd1" id='newPassword' autocomplete="off" maxlength="6">
                                    </div>
                                </div>
                                <div class="mui-input-row _pass ${isOpenCaptcha ? '' : 'final'}">
                                    <label>${views.passport_auto['确认密码']}</label>
                                    <div class="ct">
                                        <input type="password" placeholder="${views.passport_auto['请再次输入新密码']}" name="pwd2" id='confirmPassword' maxlength="6">
                                    </div>
                                </div>
                                <div class="mui-input-row _captcha final ${isOpenCaptcha ? '' : 'mui-hide'}">
                                    <div class="form-row">
                                        <div class="text">
                                            <span class="vcode">
                                                <img src="${root}/captcha/securityPwd.html" data-src="${root}/captcha/securityPwd.html" class="captcha_img">
                                            </span>
                                        </div>
                                        <div class="cont">
                                            <input type="text" class="ico6" name="code" placeholder="${views.passport_auto['请输入验证码']}" maxlength="4"/>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mui-row _captcha ${isOpenCaptcha ? '' : 'mui-hide'}">
                            <div class="error-tip">
                                <span class="pwd-tip">
                                    <span class="tip-content">${views.passport_auto['密码错误'].replace('{0}', remindTimes)}</span>
                                </span>
                            </div>
                        </div>

                        <div class="mui-row">
                            <div class="gb-form-foot">
                                <button type="button" class="mui-btn mui-btn-primary submit" id="updatePwd">${views.passport_auto['确认']}</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
<%@ include file="/include/include.js.jsp" %>

<script>
    curl(['site/passport/password/UpdateSecurityPassword',
        'site/passport/password/PopSecurityPassword'], function (Page, Security) {
        page = new Page();
        page.security = new Security();
    })
</script>

