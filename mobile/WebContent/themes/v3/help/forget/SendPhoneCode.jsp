<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <c:set var="code" value="忘记密码"/>
    <c:if test="${!empty forgetType}"><c:set var="code" value="忘记安全码"/></c:if>
    <title>${code}</title>
</head>

<body class="forget-password">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${code}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
        <div class="code-txt">
            验证码已发送至您的手机
            <span>${phone}</span>
            <input type="hidden" value="${encryptedId}" id="encryptedId"/>
        </div>
        <div class="diy-list">
            <div class="input">
                <input type="text" id="phoneCode" placeholder="请输入验证码">
            </div>
            <div class="ext">
                <a class="txt" id="sendPhone" data-rel='{"target":"sendPhoneCode","opType":"function"}'></a>
            </div>
        </div>
        <div class="btn-wrap">
            <a class="mui-btn mui-btn-block btn-blo" data-rel='{"target":"checkPhoneCode","opType":"function","forgetType":"${forgetType}"}'>下一步</a>
        </div>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/help/forget/SendPhoneCode.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>