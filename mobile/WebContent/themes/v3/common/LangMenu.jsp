<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!--语言弹窗-->
<ul class="lang-menu">
    <c:set var="siteLang" value="<%=Cache.getAvailableSiteLanguage()%>"/>
    <c:forEach var="i" items="${siteLang}">
        <li class="${fn:replace(i.value.language, '_', '-')} ${language eq i.value.language?' current':''}">
            <%--<a href="">${dicts.common.language[i.value.language]}</a>--%>
            <soul:button text="" lang="${fn:replace(i.value.language, '_', '-')}" target="changeLanguage" opType="function">${dicts.common.language[i.value.language]}</soul:button>
        </li>
    </c:forEach>
</ul>
