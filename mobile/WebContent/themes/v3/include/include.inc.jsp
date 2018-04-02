<%--@elvariable id="DateFormat" type="org.soul.commons.locale.DateFormat"--%>
<%--@elvariable id="dateQPicker" type="org.soul.commons.locale.DateQuickPicker"--%>
<%@page language="java" pageEncoding="UTF-8"%>
<%@page import="org.soul.commons.init.context.CommonContext" %>
<%@page import="org.soul.commons.locale.DateQuickPicker" %>
<%@page import="org.soul.commons.spring.utils.SpringTool" %>
<%@page import="org.soul.commons.support.CommonConf" %>
<%@page import="org.soul.web.init.BaseConfigManager" %>
<%@page import="so.wwb.gamebox.model.ParamTool" %>
<%@page import="so.wwb.gamebox.web.SessionManagerCommon" %>
<%@page import="so.wwb.gamebox.web.SupportLocale" %>
<%@page import="so.wwb.gamebox.web.cache.Cache" %>
<%@page import="java.text.MessageFormat" %>
<%@ page import="org.soul.web.tag.ImageTag" %>
<%@page trimDirectiveWhitespaces="true" %>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@taglib uri="http://soul/tags" prefix="soul" %>
<%@taglib uri="http://soul/fnTag" prefix="soulFn"  %>
<%@taglib uri="http://gb/fnTag" prefix="gbFn"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="gb" %>

<c:set var="random" value="<%= org.soul.commons.lang.string.RandomStringTool.randomNumeric(6)%>"/>
<c:set var="locale" value="<%=SessionManagerCommon.getLocale() %>"/>
<c:set var="language" value="<%= SessionManagerCommon.getLocale().toString() %>"/>
<c:set var="DateFormat" value="<%= CommonContext.getDateFormat() %>"/>
<c:set var="timeZone" value="<%= SessionManagerCommon.getTimeZone() %>"/>
<c:set var="dateQPicker" value="<%=DateQuickPicker.getInstance() %>"/>
<c:set var="curTheme" value="<%= SessionManagerCommon.getThemeFileName(request) %>"/>
<c:set var="siteCurrency" value="<%=SupportLocale.querySiteCurrencyBySiteId() %>" />
<c:set var="siteCurrencySign" value="<%=SupportLocale.querySiteCurrencySignBySiteId()  %>" />

<c:set var="rcVersion" value="<%= Cache.getRcVersion() %>"/>
<c:set var="domain" value="<%= request.getServerName() %>"/>

<c:set var="cdnUrl" value="<%= ImageTag.getCndUrl(ImageTag.getCdnConf()) %>"/>
<c:set var="root" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getRoot(),request.getServerName()) %>' />
<c:set var="ftlRoot" value='${cdnUrl}${root}'/>
<c:set var="resComRoot" value='<%=  MessageFormat.format(BaseConfigManager.getConfigration().getResComRoot(),request.getServerName()) %>' />
<c:set var="resComRoot" value='${cdnUrl}${resComRoot}' />
<c:set var="resRoot" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(),request.getServerName()) %>' />
<c:set var="resRoot" value='${cdnUrl}${resRoot}' />
<c:set var="resRoot" value='${resRoot}/../mobile-v3'/>
<c:set var="fileRoot" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getFileRoot(),request.getServerName()) %>' />
<c:set var="fileRoot" value='${cdnUrl}${fileRoot}' />
<c:set var="imgRoot" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getImgRoot(),request.getServerName()) %>' />
<c:set var="imgRoot" value='${cdnUrl}${imgRoot}' />
<c:set var="mdRoot" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getMdRoot(),request.getServerName()) %>' />

<c:set var="imgRoot_origin" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getImgRoot(),request.getServerName()) %>' />
<c:set var="resComRoot_origin" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getResComRoot(),request.getServerName()) %>' />
<c:set var="resRoot_origin" value='<%= MessageFormat.format(BaseConfigManager.getConfigration().getResRoot(),request.getServerName()) %>' />
<c:set var="resRoot_origin" value='${resRoot_origin}/../mobile-v3'/>
<c:set var="fileRoot_origin" value='<%=MessageFormat.format(BaseConfigManager.getConfigration().getFileRoot(),request.getServerName()) %>' />


<%@ include file="../../../include/include.base.inc.i18n.jsp" %>
<%@ include file="../../../include/include.base.inc.ext.jsp" %>
