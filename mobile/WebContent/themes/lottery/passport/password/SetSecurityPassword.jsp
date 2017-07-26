<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/include/include.head.jsp" %>
    <title>${views.passport_auto['设置安全密码']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
</head>

<body class="gb-theme mine-page">
    <input type="hidden" name="hasName" value="${hasName}" />
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">${views.passport_auto['设置安全密码']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper ${os eq 'android' ? 'p-t-0': ''}">
                <div class="mui-scroll">
                    <form id="setSecurityPwdForm">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form m-t-sm">
                                <div class="mui-input-row">
                                    <label>${views.passport_auto['密码']}</label>
                                    <div class="ct">
                                        <input type="password" name="pwd1" autocomplete="off" maxlength="6" placeholder="${views.passport_auto['请输入安全密码']}">
                                    </div>
                                </div>
                                <div class="mui-input-row final">
                                    <label>${views.passport_auto['确认密码']}</label>
                                    <div class="ct">
                                        <input type="password" name="pwd2" autocomplete="off" maxlength="6" placeholder="${views.passport_auto['请再次输入安全密码']}">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-form-foot">
                                <button type="button" class="mui-btn mui-btn-primary submit">${views.passport_auto['确认']}</button>
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
    curl(['site/passport/password/SetSecurityPassword',
        'site/passport/password/PopSecurityPassword'], function (Page, Security) {
        page = new Page();
        page.security = new Security();
    })
</script>
</html>
