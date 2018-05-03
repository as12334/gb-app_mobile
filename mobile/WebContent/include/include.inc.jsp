<%@ include file="include.base.inc.jsp" %>
<%@ include file="include.base.inc.i18n.jsp" %>
<%@ include file="include.base.inc.ext.jsp" %>

<%@ page import="so.wwb.gamebox.mobile.session.SessionManager" %>

<c:set var="isLogin" value="<%=(SessionManager.getUser() != null)%>" />
<c:set var="isAutoPay" value="<%=SessionManagerCommon.isAutoPay()%>" />
<c:set var="u" value="<%=SessionManager.getUser() %>" />
<c:set var="centerId" value="<%=CommonContext.get().getSiteParentId() %>"/>
<%--是否是mobile-v3 v3的存款target为0，v2target为1--%>
<c:set var="isMobileUpgrade" value="<%=ParamTool.isMobileUpgrade()%>"/>
