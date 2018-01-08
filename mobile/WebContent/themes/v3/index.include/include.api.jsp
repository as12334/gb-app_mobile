<%--@elvariable id="siteApiTypes" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiType>"--%>
<%--@elvariable id="fish" type="java.util.Map<java.lang.Integer,java.lang.String>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<section class="api-grid">
    <c:forEach var="apiType" items="${siteApiTypes}" varStatus="status">
        <c:set var="show" value="${empty path && status.index == 0 ? 'active':''}"/>
        <c:set var="type" value=""/>
        <c:choose>
            <c:when test="${apiType.apiTypeId == 1}">
                <c:set var="type" value="live"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 2}">
                <c:set var="type" value="casino"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 3}">
                <c:set var="type" value="sports"/>
            </c:when>
            <c:when test="${apiType.apiTypeId == 4}">
                <c:set var="type" value="lottery"/>
            </c:when>
        </c:choose>
        <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="${type}">
            <c:choose>
                <c:when test="${apiType.apiTypeId==2}">
                    <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"target":"${root}/game/getGameByApiId.html?search.apiId=${i18n.apiId}&search.apiTypeId=${i18n.apiTypeId}","opType":"href"}'>
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span> <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.apiName}</div>
                            </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:when test="${apiType.apiTypeId==4}">
                    <!--彩票导航-->
                    <%@include file="../game/Lottery.jsp" %>
                </c:when>
                <c:otherwise>
                    <c:forEach var="i18n" items="${apiType.apiTypeRelations}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a data-rel='{"dataApiId":"${i18n.apiId}","dataApiTypeId":"${i18n.apiTypeId}","dataApiName":"${i18n.apiName}","dataStatus":"${i18n.apiStatus}","target":"goApiGame","opType":"function"}' class="_api">
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.apiName}</div>
                            </a>
                        </li>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </ul>
    </c:forEach>
    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="fish">
        <c:forEach var="i" items="${fish}">
            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                <a data-rel='{"target":"${root}/game/getGameByApiId.html?search.apiId=${i.key}&search.apiTypeId=2&search.gameType=Fish","opType":"href"}'>
                    <span class="api-item api-icon-2-${i.key}"></span>
                    <div class="mui-media-body">${i.value}</div>
                </a>
            </li>
        </c:forEach>
        <c:if test="${fn:length(fish)<=0}">
            <div class="deficiency-nots">${views.themes_auto['没有找到符合的游戏']}</div>
        </c:if>
    </ul>
    <!--关于我们-->
    <div class="mui-table-view mui-grid-view mui-grid-9 ${not empty path && path == 'about' ? 'active':''}" data-list="about">
        <c:if test="${not empty about}">
            <c:set var="c" value="${about.content == null ? '' : about.content}" />
            ${c.replace("${company}", siteName)}
        </c:if>
    </div>
    <!--注册条款-->
    <div class="mui-table-view mui-grid-view mui-grid-9 ${not empty path && path == 'terms' ? 'active':''}" data-list="terms">
        <c:if test="${not empty terms}">
            ${not empty terms.value ? terms.value : terms.defaultValue}
        </c:if>
    </div>
</section>