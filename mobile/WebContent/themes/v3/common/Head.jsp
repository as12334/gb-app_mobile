<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<header class="mui-bar mui-bar-nav">
    <a data-rel='{"target":"leftMenu","opType":"function"}' class="mui-icon mui-action-menu mui-icon-bars mui-pull-left"></a>
    <div id="notLogin">
        <a data-rel='{"target":"${root}/signUp/index.html","opType":"href"}' class="mui-btn mui-btn-success mui-pull-right btn-register">${views.themes_auto['注册']}</a>
        <a data-rel='{"target":"${root}/login/commonLogin.html","opType":"href"}' class="mui-btn mui-btn-success mui-pull-right btn-login">${views.themes_auto['登录']}</a>
    </div>
    <%@include file="Assert.jsp"%>
    <img src="${appLogo}" alt="logo" class="logo">
</header>