<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <title>忘记密码</title>
</head>

<body class="forget-password">
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">忘记密码</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="account_input">
            <input type="text" id="userName" placeholder="请输入账号">
        </div>
        <div class="btn-wrap">
            <a class="mui-btn mui-btn-block btn-blo" data-rel='{"target":"judgeUserExist","opType":"function"}'>下一步</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/help/forget/ForgetPassword.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>