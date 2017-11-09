<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<meta charset="utf-8" />
<!-- 宽度设置为设备实际宽度，初始化倍数为1，最小倍数为1，最大倍数为1，用户缩放为否 -->
<meta name="viewport" content="width=device-width,inital-scale=1.0,minimum-scale=1.0,maximum-scake=1.0,user-scalable=no" />
<!-- 删除默认的苹果工具栏和菜单栏 -->
<meta name="apple-mobile-web-app-capable" content="yes" />
<!-- 苹果手机顶部为黑色 -->
<meta name="apple-mobile-web-status-bar-style" content="block" />
<!-- 屏蔽浏览器自动识别数字为电话号码 -->
<meta name="fromat-detecition" content="telephone=no" />
<!--禁止百度转码-->
<meta http-equiv="Cache-Control" content="no-siteapp" />
<!-- 优先使用 IE 最新版本和 Chrome -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

<%--<link rel="bookmark" href="${resRoot}/mobile-v3/favicon.ico">
<link rel="shortcut icon" href="../../mobile-v3/favicon.ico">--%>
<c:set var="v3JsRoot" value="${fn:split(resRoot, '/')}"/>

<script>
    v3JsRoot = '${v3JsRoot}';
</script>
<link rel="stylesheet" href="${v3JsRoot}/mobile-v3/themes/mui.min.css" />
<link rel="stylesheet" href="${v3JsRoot}/mobile-v3/themes/common.css" />
<link rel="stylesheet" href="${v3JsRoot}/mobile-v3/themes/index.css" />
<link rel="stylesheet" href="${v3JsRoot}/mobile-v3/themes/default/style.css" />
<link rel="stylesheet" href="${v3JsRoot}/mobile-v3/themes/hongbao.css" />

