<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.password_auto['修改登录密码']}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body class="change-loginpassword">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.password_auto['修改登录密码']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <form id="updatePwdForm" name="updateForm">
            <input type="hidden" name="result.id" value="${command.result.id}">
            <input id="remainTimes" type="hidden" value="${remainTimes}"/>
            <div id="validateRule" style="display: none">${validateRule}</div>
            <div class="mui-row">
                <div class="mui-input-group mine-form">

                    <div class="mui-input-row"><label for="">${views.password_auto['当前密码']}</label>
                        <div class="ct">
                            <input type="password" id="password" name="password" placeholder="${views.password_auto['请输入当前密码']}">
                        </div>
                    </div>

                    <div class="mui-input-row"><label for="">${views.password_auto['新密码']}</label>
                        <div class="ct">
                            <input type="password" id="newPassword" onkeyup="checkIntensity(this.value)" name="newPassword" placeholder="${views.password_auto['请输入新密码']}">
                        </div>
                    </div>
                </div>

                <div class="gb-stressed">
                    <p id="PStrength">密码强度
                        <i id="firstELement" <%--class="active"--%>></i>
                        <i></i>
                        <i></i>
                    </p>
                </div>

                <div class="mui-input-group mine-form">
                    <div class="mui-input-row"><label for="">${views.password_auto['确认密码']}</label>
                        <div class="ct">
                            <input type="password" id="confirmPassword" name="newRePassword" placeholder="${views.password_auto['请再次输入新密码']}">
                        </div>
                    </div>
                    <input type="hidden" name="flag" value="false">
                    <div class="mui-input-row" style="display: none" id="privilegeTipDiv">
                            <img src="${root}/captcha/code.html" data-src="${root}/captcha/code.html" alt="" class="captcha_img"/>
                        <div class="ct">
                            <input type="text" class="ico6" name="code" placeholder="*${views.password_auto['请输入验证码']}" maxlength="4" id="code"/>
                        </div>
                    </div>

                </div>

            </div>


        <div class="mui-row" id="privilegeMsg" style="display: none">
            <div class="warning-info" style="text-align: center;color:red; font-size:12px;
                                                     line-height:18px;margin-top: 5px;">
                <span><i style="background:url(${resRoot}/images/ico-notice.png) no-repeat center center;
                        display: inline-block;width: 15px;height: 15px;background-size: cover;
                        margin-right: 5px;vertical-align: middle;"></i>
                    ${fn:replace(messages.passport['privilege.password.prefix'],"{0}","<span class=\"co-red3\">0</span>")}
                </span>
            </div>
        </div>

        <div class="mui-row">
            <div class="gb-form-foot">
                <a href="" id="updatePwd" class="mui-btn mui-btn-primary submit" data-rel='{"target":"updatePassword","opType":"function"}'>${views.password_auto['修改']}</a>
            </div>
        </div>

        </form>
    </div>
</div>

</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/password/UpdatePassword.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>

