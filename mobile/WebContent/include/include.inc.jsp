<%@ include file="include.base.inc.jsp" %>
<%@ include file="include.base.inc.i18n.jsp" %>
<%@ include file="include.base.inc.ext.jsp" %>

<%@ page import="so.wwb.gamebox.mobile.session.SessionManager" %>

<c:set var="isLogin" value="<%=(SessionManager.getUser() != null)%>" />
<c:set var="isAutoPay" value="<%=SessionManagerCommon.isAutoPay()%>" />
<c:set var="u" value="<%=SessionManager.getUser() %>" />
<c:set var="api" value="<%=Cache.getApi() %>" />
<c:set var="siteApi" value="<%=Cache.getSiteApi() %>" />
<c:set var="centerId" value="<%=CommonContext.get().getSiteParentId() %>"/>
<%--
<input type="hidden" id="isLogin" value="${isLogin}" />
<input type="hidden" id="siteId" value="${siteId}" />
--%>