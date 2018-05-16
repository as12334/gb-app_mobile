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
        <a class="a-block" data-rel='{"target":"${root}/help/judgeUserExist.html","opType":"href"}'>
            <div class="diy-list">
                <div class="label"><i class="mui-icon mui-icon-phone"></i></div>
                <div class="input">
                    <div class="txt">手机找回</div>
                </div>
                <div class="ext">
                    <i class="mui-icon mui-icon-arrowright"></i>
                </div>
            </div>
        </a>
        <a class="a-block" data-rel='{"target":"loadCustomer","opType":"function"}'>
            <div class="diy-list">
                <div class="label"><i class="mui-icon mui-icon-chat"></i></div>
                <div class="input">
                    <div class="txt">联系客服</div>
                </div>
                <div class="ext">
                    <i class="mui-icon mui-icon-arrowright"></i>
                </div>
            </div>
        </a>
        <%--<div class="btn-wrap">
            <a href="javascript:" class="mui-btn mui-btn-block btn-blo">立即绑定</a>
        </div>--%>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/help/forget/ForgetPassword.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>