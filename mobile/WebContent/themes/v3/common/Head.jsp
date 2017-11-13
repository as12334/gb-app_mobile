<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<header class="mui-bar mui-bar-nav">
    <a class="mui-icon mui-action-menu mui-icon-bars mui-pull-left"></a>
    <soul:button target="${root}/downLoad/downLoad.html" text="" opType="href" cssClass="btn-download"></soul:button>
    <a type="button" class="mui-btn mui-btn-success mui-pull-right btn-login" href="login.html">登录/注册</a>
    <a type="button" class="mui-btn mui-btn-success mui-pull-right btn-demo">试玩</a>
    <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt="logo" class="logo">
    <!--易记域名-->
    <section class="yjym">易记域名：${domain}</section>
</header>
