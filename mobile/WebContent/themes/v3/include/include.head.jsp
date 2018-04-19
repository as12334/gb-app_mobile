<%@ page import="so.wwb.gamebox.model.SiteParamEnum" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<meta charset="utf-8"/>
<!-- 宽度设置为设备实际宽度，初始化倍数为1，最小倍数为1，最大倍数为1，用户缩放为否 -->
<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
<!-- 删除默认的苹果工具栏和菜单栏 -->
<meta name="apple-mobile-web-app-capable" content="yes"/>
<!-- 苹果手机顶部为黑色 -->
<meta name="apple-mobile-web-status-bar-style" content="black"/>
<!-- 屏蔽浏览器自动识别数字为电话号码 -->
<meta name="fromat-detecition" content="telephone=no"/>
<!--禁止百度转码-->
<meta http-equiv="Cache-Control" content="no-siteapp"/>
<!-- 优先使用 IE 最新版本和 Chrome -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

<link rel="bookmark" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.ico">
<link rel="shortcut icon" href="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.ico">

<link rel="stylesheet" href="${resRoot}/themes/mui.min.css?v=${rcVersion}"/>
<link rel="stylesheet" href="${resRoot}/themes/common.css?v=${rcVersion}"/>
<link rel="stylesheet" href="${resRoot}/themes/index.css?v=${rcVersion}"/>
<link rel="stylesheet" href="${resRoot}/themes/otherpage.css?v=${rcVersion}"/>
<c:set var="backgroundParam" value="<%=ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_BACKGROUND_COLOR)%>"/>
<c:set var="background_type" value="${backgroundParam.paramValue}"/>
<c:choose>
    <c:when test="${background_type eq 'blue'
                  ||background_type eq 'green'
                  ||background_type eq 'pink'
                  ||background_type eq 'rainbow'
                  ||background_type eq 'blue_black'
                  ||background_type eq 'orange_black'
                  ||background_type eq 'red_black'
                  ||background_type eq 'coffee_black'
                  ||background_type eq 'coffee_white'
                  ||background_type eq 'green_white'
                  ||background_type eq 'orange_white'}">
        <link rel="stylesheet" href="${resRoot}/themes/${background_type}/style.css?v=${rcVersion}"/>
        <c:set var="appLogo" value="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
    </c:when>
    <c:when test="${background_type eq 'white' || background_type eq 'golden'}">
        <link rel="stylesheet" href="${resRoot}/themes/${background_type}/style.css?v=${rcVersion}"/>
        <c:set var="appLogo" value="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}_c.png"/>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" href="${resRoot}/themes/default/style.css?v=${rcVersion}"/>
        <c:set var="appLogo" value="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
    </c:otherwise>
</c:choose>
<link rel="stylesheet" href="${resRoot}/themes/hongbao.css?v=${rcVersion}"/>
<link rel="stylesheet" href="${resRoot}/themes/loading.css?v=${rcVersion}"/>



