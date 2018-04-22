<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>修改安全密码</title>
    <%@ include file="../../include/include.head.jsp" %>
</head>

<body class="change-loginpassword">
<input type="hidden" name="hasName" value="${hasName}"/>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">修改安全密码</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <form id="updateSecurityPwd">
        <div class="mui-scroll">
            <div class="mui-row">
                <div class="mui-input-group mine-form">
                    <div class="mui-input-row"><label for="">真实姓名</label>
                        <div class="ct">
                            <input type="hidden" name="needCaptcha" value="${isOpenCaptcha}" />
                            <input type="text" placeholder="请验证真实姓名" maxlength="30" name="realName" maxlength="30">
                        </div>
                    </div>
                    <div class="mui-input-row"><label for="">当前密码</label>
                        <div class="ct">
                            <input type="password" placeholder="请输入当前密码" maxlength="6" name="originPwd">
                        </div>
                    </div>
                    <div class="mui-input-row"><label for="">新密码</label>
                        <div class="ct">
                            <input type="password" placeholder="请输入新密码" maxlength="6" name="pwd1">
                        </div>
                    </div>
                    <div class="mui-input-row _pass ${isOpenCaptcha ? '' : 'final'}"><label for="">确认密码</label>
                        <div class="ct">
                            <input type="password" placeholder="请再次输入新密码" maxlength="6" name="pwd2">
                        </div>
                    </div>
                    <div class="mui-input-row _captcha final" ${isOpenCaptcha ? '' : 'style="display:none"'}>
                        <div class="form-row">
                            <div class="text">
                                            <span class="vcode">
                                                <img src="${root}/captcha/securityPwd.html"
                                                     data-src="${root}/captcha/securityPwd.html" class="captcha_img">
                                            </span>
                            </div>
                            <div class="cont">
                                <input type="text" class="ico6" name="code"
                                       placeholder="${views.passport_auto['请输入验证码']}" maxlength="4"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="mui-row _captcha" ${isOpenCaptcha ? '' : 'style="display:none"'}>
                <div class="warning-info"
                     style="text-align: center;color:red; font-size:12px;line-height:18px;margin-top: 5px;">
            <span><i
                    style="background:url(${resRoot}/images/ico-notice.png) no-repeat center center;display: inline-block;width: 15px;height: 15px;background-size: cover;margin-right: 5px;vertical-align: middle;"></i>
                ${views.passport_auto['密码错误'].replace("{0}", remindTimes)}
            </span>
                </div>
            </div>
            <div class="mui-row">
                <div class="gb-form-foot">
                    <a data-rel={"opType":"function","target":"submitSafePassword"}
                       class="mui-btn mui-btn-primary submit">修改</a>
                </div>
            </div>
        </div>
    </form>
</div>
</body>


<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js"></script>
<script type="text/javascript" src="${resRoot}/js/password/SafePasswordCommon.js"></script>
<script type="text/javascript" src="${resRoot}/js/password/UpdateSecurityPassword.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
