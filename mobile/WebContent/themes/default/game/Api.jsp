<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set value="${apiDetail.get('apiI18n')}" var="apiI18n"></c:set>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${apiI18n.name}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme index e-transfer" onresize="window.activeElement.scrollIntoView(true);">
<%--popover滚动需在页面自定义样式--%>
<style>
    /*跨webview需要手动指定位置*/
    #apiType {
        position: fixed;
        top: 16px;
        right: 6px;
    }

    #apiType .mui-popover-arrow {
        left: auto;
        right: 6px;
    }

    .mui-popover {
        height: 50%;
        width: 40%;
    }
</style>
<!--头部-->
<header class="mui-bar mui-bar-nav" ${os eq 'android' ? 'style="margin-top:24px"' : ''}>
    <div class="mui-pull-left">
        <c:if test="${os ne 'android'}">
            <a href="" class="mui-action-back"><span class="mui-icon mui-icon-back"></span></a>
        </c:if>
    </div>
    <div class="mui-pull-right" style="padding-right: 5px">
        <a href="#apiType" class="btn-allgame">${views.game_auto['全部游戏']}</a>
    </div>
</header>
<gb:token></gb:token>
<input type="hidden" id="apiId" value="${apiI18n.apiId}"/>
<input type="hidden" id="apiTypeId" value="${apiDetail.get('apiTypeId')}"/>
<!--滚动区域-->
<div class="mui-content mui-scroll-wrapper mui-fullscreen api-content">
    <div class="mui-scroll">
        <div class="gb-fullpage">
            <!-- banner -->
            <div class="e-banner">
                <img src="${resRoot}/images/api/banner/${apiI18n.apiId}-${apiI18n.apiId eq 10?'1':apiDetail.get('apiTypeId')}.jpg"
                     width="100%" alt="">
            </div>
            <!-- 游戏项 -->
            <div class="game-item">
                <div class="mui-row">
                    <div class="mui-pull-left">
                        <span class="icon-hobby">
                            <c:choose>
                                <c:when test="${centerId == -3 && apiI18n.apiId == 22}">
                                    <c:set var="api_icon" value="4-22-2" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="api_icon" value="${apiDetail.get('apiTypeId')}-${apiI18n.apiId}" />
                                </c:otherwise>
                            </c:choose>
                            <span class="api-icon api-icon-${api_icon}"></span>
                        </span>
                    </div>
                    <div class="mui-pull-left game-detail">
                        <h1 id="apiName">
                            <c:set var="flag" value="${apiList.size()}"/>
                            <c:forEach var="at" items="${apiList}" end="${flag}">
                                <c:if test="${at.get('apiTypeRelation').apiId == apiI18n.apiId && apiDetail.get('apiTypeId') == at.get('apiTypeRelation').apiTypeId}">
                                    <c:choose >
                                        <c:when test="${at.get('apiTypeRelation').apiId eq 10}">${gbFn:getApiName(10)}</c:when>
                                        <c:otherwise>${at.get("apiTypeRelation").name}</c:otherwise>
                                    </c:choose>
                                    <c:set var="flag" value="0" />
                                </c:if>
                            </c:forEach>
                        </h1>
                        <div class="g-money">
                            <div class="login-status">
                                <span class="rmb-c">${siteCurrency}</span><span id="apiBalance"></span> <span class="icon-refresh"
                                                                                                  id="refreshApi"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 操作按钮组 -->
            <div class="e-btn-group">
                <div class="mui-row">
                    <div class="mui-col-xs-4">
                        <div class="e-btn btn-in"></div>
                        <div class="txt">${views.game_auto['额度转入']}</div>
                    </div>
                    <div class="mui-col-xs-4">
                        <a href="" id="startGame" >
                            <div class="e-btn btn-start-game"></div>
                            <div class="txt">${views.game_auto['开始游戏']}</div>
                        </a>

                    </div>
                    <div class="mui-col-xs-4">
                        <div class="e-btn btn-out"></div>
                        <div class="txt">${views.game_auto['额度转出']}</div>
                    </div>
                </div>
            </div>
            <!-- 游戏描述 -->
            <div class="game-description">
                <p id="apiContent">${apiI18n.introduceContent}</p>
            </div>
        </div>
    </div>
</div>
<div id="apiType" class="mui-popover">
    <div class="mui-scroll-wrapper popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <c:forEach var="apiType" items="${apiList}" varStatus="status">
                    <c:set var="apiStatus" value="${api.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus eq 'maintain' ?'maintain' : siteApi.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus}"></c:set>
                    <li class="mui-table-view-cell">
                        <a class="_api" data-api-id="${apiType.get("apiTypeRelation").apiId}" data-api-type-id="${apiType.get("apiTypeRelation").apiTypeId}"
                           data-status="${apiStatus}"><c:choose >
                            <c:when test="${apiType.get('apiTypeRelation').apiId eq 10}">${gbFn:getApiName(10)}</c:when>
                            <c:otherwise>${apiType.get("apiTypeRelation").name}</c:otherwise>
                        </c:choose></a>
                    </li>
                </c:forEach>

            </ul>
        </div>
    </div>
</div>
</body>
<script>
    curl(['site/game/Api'],
            function (Api) {
                page = new Api();
            });
</script>
</html>
