<%@ page import="so.wwb.gamebox.mobile.init.ConfigManager" %>
<c:set var="ftlRoot" value='<%= MessageFormat.format(ConfigManager.get().getFreemakerTemplateRootPath(),request.getServerName()) %>' />
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1,user-scalable=no">
<meta name="apple-touch-fullscreen" content="yes">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="format-detection" content="telephone=no">
<meta name="x5-fullscreen" content="true">
<meta http-equiv="Content-Type" content="text/html; content=no-cache" charset=utf-8" />

<%-- UC强制竖屏 --%>
<meta name="screen-orientation" content="portrait">
<meta name="browsermode" content="application">
<%-- QQ强制竖屏 --%>
<meta name="x5-orientation" content="portrait">
<meta name="x5-page-mode" content="app">
<%-- Chrome地址栏颜色 --%>
<meta name="theme-color" content="#09102c">
<%-- 添加到主屏 --%>
<meta name="mobile-web-app-capable" content="yes">
<link rel="apple-touch-icon" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" />
<link rel="icon" sizes="144x144" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" />
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.min.css?v=${rcVersion}"/>
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui-icon-extra.css?v=${rcVersion}"/>
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/common.css?v=${rcVersion}" />
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/loading.css?v=${rcVersion}" />
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/layer.css?v=${rcVersion}" />
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/${curTheme}/lang/${language}.css?v=${rcVersion}"/>
<%
    String userAgent = request.getHeader("User-Agent");
    String myos;
    if (userAgent.contains("app_android")) {
        myos= "android";
    } else if (userAgent.contains("app_ios")){
        myos= "app_ios";
    }else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod") || userAgent.contains("iOS")) {
        myos= "ios";
    } else {
        myos= "pc";
    }
%>
<c:set var="os" value="<%=myos%>"/>
<script>
    var os = '${os}';
</script>
<input type="hidden" id="isLogin" value="${isLogin}">

