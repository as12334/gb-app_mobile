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
                            <%--<button class="mui-btn mui-btn-primary btn-reg btn-demo" type="button">${views.passport_auto['免费试玩']}</button>--%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<div id="middlePopover" class="mui-popover origin-player-dialog notice-popover-wrap">
    <div class="mui-popup mui-popup-in">
        <div class="mui-popup-inner">
            <div class="mui-popup-title">
                <span class="pop-title">${views.passport_auto['提示消息']}</span>
                <a href="#bottomPopover" class="mui-btn mui-btn-link mui-pull-right pop-close"><span class="mui-icon mui-icon-closeempty verify-cancel"></span></a>
            </div>
            <div class="cont-text">
                <div class="popup-scroll">
                    <div class="bs-component">
                        <div class="alert alert-dismissible alert-danger">${views.passport_auto['本次升级，加强了账户的安全防护体系，请验证真实姓名，验证通过后即可成功登陆。']}</div>
                    </div>
                    <form class="form-horizontal">
                        <div class="form-group">
                            <gb:token />
                            <label class="col-12-3 control-label">${views.passport_auto['您的真实姓名：']}</label>
                            <div class="col-12-8">
                                <input type="text" class="form-control" name="result.realName" id="result_realName" placeholder="请输入您的真实姓名">
                                <input type="hidden" name="needRealName" value="yes">
                                <input type="hidden" name="result.playerAccount" id="result_playerAccount">
                                <input type="hidden" name="search.playerAccount" id="search_playerAccount">
                                <input type="hidden" name="tempPass" id="tempPass">
                                <input type="hidden" name="newPassword" id="newPassword">
                                <input type="hidden" name="passLevel" value="20">
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <button type="button" class="mui-btn confirm-btn verify-name">${views.passport_auto['确定']}</button>
            <button type="button" class="mui-btn verify-cancel">${views.passport_auto['取消']}</button>
        </div>
    </div>

</div>
<%@ include file="/include/include.base.js.common.jsp" %>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
</script>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/global.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/plugin/layer.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/passport/Login.js?v=${rcVersion}"></script>