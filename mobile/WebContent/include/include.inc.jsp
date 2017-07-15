<%@ include file="include.base.inc.jsp" %>
<%@ include file="include.base.inc.i18n.jsp" %>
<%@ include file="include.base.inc.ext.jsp" %>

<%@ page import="so.wwb.gamebox.mobile.session.SessionManager" %>
<%@ page import="so.wwb.gamebox.model.ParamTool" %>
<%@ page import="so.wwb.gamebox.model.SiteParamEnum" %>

<c:set var="isLogin" value="<%=(SessionManager.getUser() != null)%>" />
<c:set var="isAutoPay" value="<%=SessionManagerCommon.isAutoPay()%>" />
<c:set var="u" value="<%=SessionManager.getUser() %>" />
<c:set var="api" value="<%=Cache.getApi() %>" />
<c:set var="siteApi" value="<%=Cache.getSiteApi() %>" />
<%--
<input type="hidden" id="isLogin" value="${isLogin}" />
<input type="hidden" id="siteId" value="${siteId}" />
--%>