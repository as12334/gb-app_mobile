<%@ page import="so.wwb.gamebox.model.SiteParamEnum" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.head.jsp" %>
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/hb/style.css?v=${rcVersion}" />
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/hongbao.css?v=${rcVersion}" />
<script>var siteType='default';</script>
<c:set var="backgroundParam" value="<%=ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_BACKGROUND_COLOR)%>"/>
<c:set var="background_type" value="${backgroundParam.paramValue}"/>
<link rel="stylesheet" type="text/css" href="${resRoot}/themes/${background_type}/style.css?v=${rcVersion}" />
