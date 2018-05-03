<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>手机绑定</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css"/>
    <link rel="stylesheet" href="../../mobile-v3/themes/default/style.css" />
</head>

<body class="bind-mobile">
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">手机绑定</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <div class="mui-scroll">
      <div class="bind-success">
                         您已绑定手机号码<br/>
         <a href="javascript:;">186****745</a>
      </div>
      <div class="btn-wrap">
        <a href="bind-mobile-3.html" class="mui-btn mui-btn-block btn-blo">立即更换</a>
      </div>
      <ul class="cue">
      	<li>温馨提示</li>
      	<li>1.忘记密码时可以通过手机找回；</li>
      	<li>2.运营商发送短信可能有延迟，请耐心等待几分钟；</li>
      	<li>3.若几分钟后仍未收到验证码，请您重新获取或联系<a href="javascript:;">点击联系在线客服.</a></li>
      </ul>
    </div>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/help/bind/BindMobile.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>