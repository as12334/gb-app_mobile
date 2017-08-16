<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.password_auto['修改登录密码']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper"  ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <form id="updatePwdForm">
                    <input type="hidden" name="result.id" value="${command.result.id}">
                    <input id="remainTimes" type="hidden" value="${remainTimes}"/>
                    <div id="validateRule" style="display: none">${validateRule}</div>
                    <div class="mui-row m-t-sm">
                        <div class="mui-input-group mine-form">
                            <div class="mui-input-row"><label>${views.password_auto['当前密码']}</label>
                                <div class="ct">
                                    <input id='password' type="password" name="password" placeholder="${views.password_auto['请输入当前密码']}">
                                </div>
                            </div>
                            <div class="mui-input-row"><label>${views.password_auto['新密码']}</label>
                                <div class="ct">
                                    <input id='newPassword' type="password" name="newPassword" placeholder="${views.password_auto['请输入新密码']}">
                                </div>
                            </div>

                            <div class="mui-input-row"><label>${views.password_auto['确认密码']}</label>
                                <div class="ct">
                                    <input id='confirmPassword' type="password" name="newRePassword"
                                           placeholder="${views.password_auto['请再次输入新密码']}">
                                </div>
                            </div>

                            <div class="mui-input-row" style="display: none" id="privilegeTipDiv">
                                <input type="hidden" name="flag" value="false">
                                <div class="form-row">
                                    <div class="text" style="background: none">
                                        <span class="vcode">
                                            <img src="${root}/captcha/code.html"
                                                         data-src="${root}/captcha/code.html" alt=""
                                                         class="captcha_img"/>
                                        </span>
                                    </div>
                                    <div class="cont">
                                        <input type="text" class="ico6" name="code" placeholder="*${views.password_auto['请输入验证码']}" maxlength="4" id="code"/>
                                    </div>
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
                            <a href="" id='updatePwd' class="mui-btn mui-btn-primary submit">${views.password_auto['修改']}</a>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<soul:import res="site/password/UpdatePassword"/>
