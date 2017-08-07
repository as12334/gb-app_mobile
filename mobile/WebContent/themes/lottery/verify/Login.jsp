<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.verify_auto['登录验证']}</title>
</head>

<body class="gb-theme mine-page login-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <c:if test="${os ne 'android' && os ne 'app_ios'}">
            <header class="mui-bar mui-bar-nav ">
                <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">${views.verify_auto['登录验证']}</h1>
                <a class="mui-icon mui-icon-home mui-pull-right" data-href="/mainIndex.html"></a>
            </header>
            </c:if>
            <div class="mui-content mui-scroll-wrapper" ${(os eq 'android' or os eq 'app_ios')?'style="padding-top:0"':''}>
                <div class="mui-scroll">
                    <input type="hidden" name="customer" value="${customerService}" />
                    <c:choose>
                        <c:when test="${enable}">
                            <form id="vForm">
                                <div class="mui-row">
                                    <div class="mui-input-group v-tip">
                                        ${views.verify_auto['站点近期已完成全方位升级']}
                                    </div>
                                </div>
                                <div class="mui-row">
                                    <div class="mui-input-group mine-form m-t-sm">
                                        <div class="mui-input-row">
                                            <div class="form-row">
                                                <div class="cont">
                                                    <input name="search.playerAccount" class="ico1" type="text" placeholder="${views.verify_auto['请输入账号']}" autocomplete="off" maxlength="15" />
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mui-input-row _pass ${isOpenCaptcha ? '' :'final'}">
                                            <div class="form-row">
                                                <div class="cont">
                                                    <input name="tempPass" class="ico2" type="password" placeholder="${views.verify_auto['请输入密码']}" autocomplete="off" maxlength="20">
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
                                        <button class="mui-btn mui-btn-primary submit _login" type="button">${views.verify_auto['立即登录']}</button>
                                        <p class="no-account">${views.verify_auto['没有账号']}？</p>
                                        <div class="login-line"></div>
                                        <button class="mui-btn mui-btn-primary btn-reg" data-href="/signUp/index.html" type="button">${views.verify_auto['免费开户']}</button>
                                    </div>
                                </div>
                            </form>
                            <form style="display: none" id="loginForm">
                                <input type="text" name="username" />
                                <input type="password" name="password" />
                                <input type="text" name="captcha" />
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="withdraw-out">
                                <div class="withdraw-not" style="padding-bottom: 10px;">
                                    <h1><i class="tipbig fail"></i></h1>
                                    <div class="tiptext">
                                        <p>${views.verify_auto['该站点未开启导入玩家功能']}</p>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</body>

</html>

<%@ include file="/include/include.js.jsp" %>
<script>
    curl(['site/verify/Login'], function(Page) {
        page = new Page();
    });
</script>
