<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<section class="api-grid">
    <c:forEach var="type" items="${SiteApiRelationI18n}" varStatus="status">
        <c:set var="show" value="${empty path && status.index == 0 ? 'active':''}"/>
        <c:choose>
            <c:when test="${type.key == 1}">
                <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="live">
                    <c:forEach var="i18n" items="${type.value}">
                        <c:set var="apiStatus"
                               value="${api.get(i18n.apiId.toString()).systemStatus eq 'maintain' ?'maintain' : siteApi.get(i18n.apiId.toString()).systemStatus}"></c:set>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <soul:button dataApiId="${i18n.apiId}"
                                         dataApiTypeId="${i18n.apiTypeId}"
                                         dataApiName="${i18n.name}"
                                         dataStatus="${apiStatus}"
                                         target="goApiGame" text="" opType="function" cssClass="_api">
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.name}</div>
                            </soul:button>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${type.key == 2}">
                <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="casino">
                    <c:forEach var="i18n" items="${type.value}">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <soul:button
                                    target="${root}/game/getGameByApiId.html?search.apiId=${i18n.apiId}&search.apiTypeId=${i18n.apiTypeId}"
                                    text="" opType="href" cssClass="">
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span> <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.name}</div>
                            </soul:button>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${type.key == 3}">
                <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="sports">
                    <c:forEach var="i18n" items="${type.value}">
                        <c:set var="apiStatus"
                               value="${api.get(i18n.apiId.toString()).systemStatus eq 'maintain' ?'maintain' : siteApi.get(i18n.apiId.toString()).systemStatus}"></c:set>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <soul:button dataApiId="${i18n.apiId}"
                                         dataApiType-id="${i18n.apiTypeId}"
                                         dataApiName="${i18n.name}"
                                         dataStatus="${apiStatus}"
                                         target="goApiGame" text="" opType="function" cssClass="_api">
                                <span class="api-item api-icon-${i18n.apiTypeId}-${i18n.apiId}"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">${i18n.name}</div>
                            </soul:button>
                        </li>
                    </c:forEach>
                </ul>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${type.key == 4}">
                <ul class="mui-table-view mui-grid-view mui-grid-9 ${show}" data-list="lottery">
                    <!--彩票导航-->
                    <%@include file="../game/Lottery.jsp" %>
                </ul>
            </c:when>
        </c:choose>
    </c:forEach>
    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="fish">
        <c:if test="${AG}">
            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                <soul:button
                    target="${root}/game/getGameByApiId.html?search.apiId=9&search.apiTypeId=2&search.gameType=Fish"
                    text="" opType="href" cssClass="">
                    <span class="api-item api-icon-2-9"></span> <!--根据class的不同来显示api图标-->
                    <div class="mui-media-body">AG</div>
                </soul:button>
            </li>
        </c:if>
        <c:if test="${GG}">
            <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                <soul:button
                    target="${root}/game/getGameByApiId.html?search.apiId=28&search.apiTypeId=2"
                    text="" opType="href" cssClass="">
                    <span class="api-item api-icon-2-28"></span> <!--根据class的不同来显示api图标-->
                    <div class="mui-media-body">GG</div>
                </soul:button>
            </li>
        </c:if>
        <c:if test="${GG == false && AG == false}">
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