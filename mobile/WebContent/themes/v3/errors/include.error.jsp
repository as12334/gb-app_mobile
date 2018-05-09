<%@ page import="so.wwb.gamebox.model.SiteParamEnum" %>
<%@ page import="so.wwb.gamebox.model.ParamTool" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<c:set var="backgroundParam" value="<%=ParamTool.getSysParam(SiteParamEnum.SETTING_SYSTEM_SETTINGS_BACKGROUND_COLOR)%>"/>
<c:set var="background_type" value="${backgroundParam.paramValue}"/>
<c:choose>
    <c:when test="${background_type eq 'golden'}">
        <c:set var="appLogo" value="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}_c.png"/>
    </c:when>
    <c:otherwise>
        <c:set var="appLogo" value="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${background_type eq 'blue'}">
        <style>
            .diy{background:#1b75d9!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'green'}">
        <style>
            .diy{background:#14805e!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'pink'}">
        <style>
            .diy{background:#f2205f!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'rainbow'}">
        <style>
            .diy{background:#a1a8d7!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'blue_black'}">
        <style>
            .diy{background:#1766bb!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'orange_black'}">
        <style>
            .diy{background:#ff7700!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'red_black'}">
        <style>
            .diy{background:#C82721!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'coffee_black'}">
        <style>
            .diy{background:#7b340d!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'coffee_white'}">
        <style>
            .diy{background:#7b341d!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'green_white'}">
        <style>
            .diy{background:#14805e!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'white'}">
        <style>
            .diy{background:#151515!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'golden'}">
        <style>
            .diy{background:#e6bd55!important;}
        </style>
    </c:when>
    <c:when test="${background_type eq 'orange_white'}">
        <style>
            .diy{background:#ff7701!important;}
        </style>
    </c:when>
    <c:otherwise>
        <style>
            .diy{background:#151515!important;}
        </style>
    </c:otherwise>
</c:choose>

<link rel="stylesheet" href="${resRoot}/themes/error/common.css?v=${rcVersion}"/>