<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>更换手机</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css?v=${rcVersion}"/>
</head>

<body class="bind-mobile">
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">更换手机</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <form class="mui-input-group" id="regForm">
    <div id="validateRule" style="display: none">${rule}</div>
    <div class="mui-scroll">
      <div class="input">
          <input type="number" name="oldPhone" maxlength="12" placeholder="旧手机号码"/>
          <input type="number" name="search.contactValue" maxlength="12" placeholder="新手机号码"/>
        <div class="yzm">
          <input  id="phoneCode" name="phoneCode" type="number" maxlength="6" placeholder="验证码"/>
          <a id="sendPhoneCode" data-rel='{"target":"sendPhoneCode","opType":"function"}'>获取验证码</a>
        </div>
      </div>
      <div class="btn-wrap">
        <a data-rel='{"target":"bindMobile","opType":"function"}' class="mui-btn mui-btn-block btn-blo">立即绑定</a>
      </div>
      <ul class="cue">
      	<li>温馨提示</li>
      	<%--<li>1.忘记密码时可以通过手机找回；</li>--%>
      	<li>2.运营商发送短信可能有延迟，请耐心等待几分钟；</li>
      	<li>3.若几分钟后仍未收到验证码，请您重新获取或联系<a data-rel='{"target":"${customer}","opType":"href"}'>点击联系在线客服.</a></li>
      </ul>
    </div>
    </form>
</div>
</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/help/bind/BindMobile.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>