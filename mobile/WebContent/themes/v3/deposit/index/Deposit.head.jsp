<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="../../include/include.head.jsp" %>
    <link href="${resRoot}/themes/mui.picker.css?v=${rcVersion}" type="text/css" rel="stylesheet" />
    <link href="${resRoot}/themes/mui.poppicker.css?v=${rcVersion}" type="text/css" rel="stylesheet" />
    <link href="${resRoot}/themes/swiper.min.css?v=${rcVersion}" type="text/css" rel="stylesheet" />
    <title>${views.deposit_auto['存款']}</title>
</head>
<body class="deposit_2">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">存款</h1>
    <%@ include file="../../common/Assert.jsp" %>
</header>