<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <title>忘记密码</title>
</head>

<body class="forget-password">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">输入验证码</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <input hidden="hidden" value="${encryptedId}" id="encryptedId"/>
    <div class="mui-scroll">
        <div class="diy-list">
            <div class="label label_txt">新密码</div>
            <div class="input">
                <input type="password" class="text-right" id="password" placeholder="请输入6-20个字母，数字或字符">
            </div>
        </div>
        <div class="diy-list">
            <div class="label label_txt">确认密码</div>
            <div class="input">
                <input type="password" class="text-right" id="repeatPassword" placeholder="再次输入密码">
            </div>
        </div>
        <div class="btn-wrap">
            <a class="mui-btn mui-btn-block btn-blo" data-rel='{"target":"setLoginPassword","opType":"function"}'>修改</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/help/forget/SetLoginPassword.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>