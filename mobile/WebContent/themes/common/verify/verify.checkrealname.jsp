<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<c:set var="p" value="${command.result}" />
<body class="gb-theme mine-page login-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">
                    <c:choose>
                        <c:when test="${!empty p.realName}">
                            ${views.verify_auto['验证真实姓名']}
                        </c:when>
                        <c:otherwise>
                            ${views.verify_auto['设置登录密码']}
                        </c:otherwise>
                    </c:choose>
                </h1>
                <a class="mui-icon mui-icon-home mui-pull-right" data-href="/mainIndex.html"></a>
            </header>
            <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
                <div class="mui-scroll">
                    <form id="nameForm">
                        <gb:token/>
                        <div class="mui-row">
                            <div class="mui-input-group v-tip">
                                ${views.verify_auto['本次升级']}<c:if test="${!empty p.realName}">${views.verify_auto['验证真实姓名并']}</c:if>${views.verify_auto['重新设置登录密码']}
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="mui-input-group mine-form m-t-sm">
                                <div class="mui-input-row">
                                    <div class="form-row">
                                        <div class="cont">
                                            <c:choose>
                                                <c:when test="${!empty command.result.realName}">
                                                    <input name="result.realName" class="ico1" type="text" placeholder="${views.verify_auto['请输入您的真实姓名']}" autocomplete="off" maxlength="15" />
                                                    <input type="hidden" name="needRealName" value="yes" />
                                                </c:when>
                                                <c:otherwise>
                                                    <input type="hidden" name="needRealName" value="no" />
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                                <div class="mui-input-row _pass">
                                    <div class="form-row">
                                        <div class="cont">
                                            <input name="tempPass" class="ico2" id="ppw" type="password" placeholder="${views.verify_auto['密码长度']}" autocomplete="off" maxlength="20" />
                                        </div>
                                    </div>
                                </div>
                                <div class="mui-input-row _pass ${isOpenCaptcha ? '' :'final'}">
                                    <div class="form-row">
                                        <div class="cont">
                                            <input name="newPassword" class="ico2" type="password" placeholder="${views.verify_auto['请再次输入密码']}" autocomplete="off" maxlength="20" />
                                        </div>
                                    </div>
                                </div>
                                <div class="mui-input-row securityCode ${isOpenCaptcha ? 'final' :''}" ${isOpenCaptcha ? "" :'style="display: none"'}>
                                    <div class="form-row">
                                        <div class="cont">
                                            <input type="hidden" name="needCaptcha" value="${isOpenCaptcha ? 'yes' : 'no'}" />
                                            <input type="text" name="tempCapt" class="mui-input ico6" maxlength="4" placeholder="${views.verify_auto['请输入验证码']}" />
                                            <div class="gb-vcode">
                                                <img class="v-captcha" src="${root}/captcha/code.html" data-src="${root}/captcha/code.html">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mui-input-row">
                            <div class="gb-form-foot" style="height: 65px;">
                                <input type="hidden" name="search.id" value="${p.id}" />
                                <input name="passLevel" type="hidden" value="0" />
                                <input name="result.playerAccount" type="hidden" value="${p.playerAccount}" autocomplete="off" />
                                <button class="mui-btn mui-btn-primary submit _ok" style="float: left; width: 68%" type="button">${views.verify_auto['确认']}</button>
                                <button class="mui-btn mui-btn-primary btn-reg" data-href="/mainIndex.html"
                                        style="float: right; background: #fff; margin-top: 0; width: 30%" type="button">${views.verify_auto['取消']}</button>
                            </div>
                        </div>
                    </form>
                    <form style="display: none" id="loginForm">
                        <input type="text" name="username" />
                        <input type="password" name="password" />
                        <input type="text" name="captcha" />
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>

<%@ include file="/include/include.js.jsp" %>
<script>
    curl(['site/verify/CheckRealName'], function(Page) {
        page = new Page();
    });
</script>
