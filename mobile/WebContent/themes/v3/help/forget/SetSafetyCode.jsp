<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <title>忘记安全码</title>
</head>

<body class="forget-password">
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">忘记安全码</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="diy-list">
            <div class="label label_txt">新安全码</div>
            <div class="input">
                <input type="password" class="text-right" name="pwd1" maxlength="6" placeholder="请输入安全码">
            </div>
        </div>
        <div class="diy-list">
            <div class="label label_txt">确认安全码</div>
            <div class="input">
                <input type="password" class="text-right" name="pwd2" maxlength="6" placeholder="再次输入安全码">
            </div>
        </div>
        <div class="btn-wrap">
            <a class="mui-btn mui-btn-block btn-blo" data-rel='{"target":"setSafetyCode","opType":"function"}'>修改</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/help/forget/SetSafetyCode.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>