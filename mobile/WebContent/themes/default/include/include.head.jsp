<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.head.jsp" %>
<script>var siteType='default';</script>
<c:choose>
    <c:when test="${siteId == 22 || siteId == 119 || siteId == 171}"> <!-- blue -->
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/blue/style.css?v=${rcVersion}" />
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/blue/loading.css?v=${rcVersion}" />
    </c:when>
    <c:when test="${siteId == 141 || siteId == 161 || siteId == 207 || siteId == 238}"> <!-- green -->
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/green/style.css?v=${rcVersion}" />
    </c:when>
    <c:when test="${siteId == 35 || siteId == 185}"> <!-- phoenix -->
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/phoenix/style.css?v=${rcVersion}" />
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/phoenix/loading.css?v=${rcVersion}" />
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" type="text/css" href="${resRoot}/themes/default/style.css?v=${rcVersion}" />
    </c:otherwise>
</c:choose>
<input type="hidden" id="activityId" value="${activityId}">
<%@ include file="../promo/redEnvelope/Envelope.jsp" %>
<script src="${resRoot}/js/promo/redEnvelope/Envelope.js?v=${rcVersion}"></script>