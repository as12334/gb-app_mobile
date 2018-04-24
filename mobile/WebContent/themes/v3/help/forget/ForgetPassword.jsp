<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <title>忘记密码</title>
</head>
<body class="forget">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">忘记密码</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="mui-row">
            <div class="mui-input-group mine-form m-t-sm">
                <div class="mui-input-row">
                    <div class="form-row">
                        <div class="cont">
                            <input type="text" placeholder="请输入用户名">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-row">
            <div class="gb-form-foot">
                <a data-rel='{"target":"getUserInfo","opType":"function"}' class="mui-btn mui-btn-primary submit">下一步</a>
            </div>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
</html>
<%@ include file="/include/include.footer.jsp" %>