<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<div id="headMenu">
<header class="mui-bar mui-bar-nav">
    <soul:button target="leftMenu" text="" opType="function" cssClass="mui-icon mui-action-menu mui-icon-bars mui-pull-left"/>
    <%--<soul:button target="${root}/downLoad/downLoad.html" text="" opType="href" cssClass="btn-download"/>--%>
    <div id="notLogin">
        <%--<soul:button target="" text="试玩" opType="href" cssClass="mui-btn mui-btn-success mui-pull-right btn-demo"/>--%>
        <soul:button target="${root}/signUp/index.html" text="注册" opType="href" cssClass="mui-btn mui-btn-success mui-pull-right btn-login"/>
        <soul:button target="${root}/login/commonLogin.html" text="登录" opType="href" cssClass="mui-btn mui-btn-success mui-pull-right btn-login"/>
    </div>
    <%@include file="Assert.jsp"%>
    <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt="logo" class="logo">
    <!--易记域名-->
    <section class="yjym">易记域名：${domain}</section>
</header>
</div>