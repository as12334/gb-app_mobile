<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set value="${apiDetail.get('apiI18n')}" var="apiI18n"></c:set>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/layer.css"/>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->
    <aside class="mui-off-canvas-left">
        <div class="mui-scroll-wrapper side-menu-scroll-wrapper">
            <div class="mui-scroll">
            </div>
        </div>
    </aside>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav header-casino-transfer">
            <a class="mui-action-back mui-icon mui-icon mui-icon-left-nav mui-pull-left mui-action-back"></a>
            <a href="#apiType" class="mui-pull-right mui-icon link-all-game">${views.game_auto['全部游戏']}</a>
        </header>
        <gb:token></gb:token>
        <input type="hidden" id="apiId" value="${apiI18n.apiId}"/>
        <input type="hidden" id="apiTypeId" value="${apiDetail.get('apiTypeId')}"/>
        <div class="mui-content mui-scroll-wrapper content-without-notice content-without-footer content-casino-transfer">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <div class="casino-transfer-content">
                    <!-- banner -->
                    <div class="e-banner">
                        <%--<img src="../../mobile-v3/images/electronic-transfer-banner.png" width="100%" alt="">--%>
                        <img src="${resRoot}/images/electronic-transfer-banner.png" width="100%" alt="">
                        <%--<img src="${resRoot}/images/api/banner/${apiI18n.apiId}-${apiI18n.apiId eq 10?'1':apiDetail.get('apiTypeId')}.jpg"
                                 width="100%" alt="">--%>
                    </div>
                    <!-- 游戏项 -->
                    <div class="game-item api-grid ">
                        <div class="mui-row">
                            <div class="mui-pull-left">
                                <c:choose>
                                    <c:when test="${centerId == -3 && apiI18n.apiId == 22}">
                                        <c:set var="api_icon" value="4-22-2"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="api_icon" value="${apiDetail.get('apiTypeId')}-${apiI18n.apiId}"/>
                                    </c:otherwise>
                                </c:choose>
                                <span class="api-item api-icon-${api_icon}"></span>
                            </div>
                            <div class="mui-pull-left game-detail">
                                <h1>
                                    <c:set var="flag" value="${apiList.size()}"/>
                                    <c:forEach var="at" items="${apiList}" end="${flag}">
                                        <c:if test="${at.get('apiTypeRelation').apiId == apiI18n.apiId && apiDetail.get('apiTypeId') == at.get('apiTypeRelation').apiTypeId}">
                                            <c:choose>
                                                <c:when test="${at.get('apiTypeRelation').apiId eq 10}">${gbFn:getApiName(10)}</c:when>
                                                <c:otherwise>${at.get("apiTypeRelation").name}</c:otherwise>
                                            </c:choose>
                                            <c:set var="flag" value="0"/>
                                        </c:if>
                                    </c:forEach>
                                </h1>
                                <div class="g-money">
                                    <div class="login-status">
                                        <span class="rmb-c">${apiDetail.get('currSign')}</span>${apiDetail.get('apiMoney')}<%--200.25--%>
                                        <span class="icon-refresh"></span>
                                    </div>
                                    <div class="unlogin-status mui-hide">
                                        <a href="login">登陆查看余额</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 操作按钮组 -->
                    <div class="e-btn-group">
                        <div class="mui-row">
                            <soul:button text="" tag="div" target="moneyIn" opType="function" cssClass="mui-col-xs-4">
                                <div class="e-btn btn-in"></div>
                                <div class="txt">${views.game_auto['额度转入']}</div>
                            </soul:button>
                            <div class="mui-col-xs-4">
                                <soul:button text="" target="startGame" opType="function">
                                    <div class="e-btn btn-start-game"></div>
                                    <div class="txt">${views.game_auto['开始游戏']}</div>
                                </soul:button>
                            </div>
                            <soul:button text="" tag="div" target="moneyOut" opType="function" cssClass="mui-col-xs-4">
                                <div class="e-btn btn-out"></div>
                                <div class="txt">${views.game_auto['额度转出']}</div>
                            </soul:button>
                        </div>
                    </div>
                    <!-- 游戏描述 -->
                    <div class="game-description">
                        <p>${apiI18n.introduceContent}</p>
                    </div>
                </div> <!--casino-transfer-contetn 闭合标签-->
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
<div id="apiType" class="mui-popover">
    <div class="mui-content mui-scroll-wrapper popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <c:forEach var="apiType" items="${apiList}" varStatus="status">
                    <c:set var="apiStatus"
                           value="${api.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus eq 'maintain' ?'maintain' : siteApi.get(apiType.get('apiTypeRelation').apiId.toString()).systemStatus}"></c:set>
                    <li class="mui-table-view-cell">
                        <a class="_api" data-api-id="${apiType.get("apiTypeRelation").apiId}"
                           data-api-type-id="${apiType.get("apiTypeRelation").apiTypeId}"
                           data-status="${apiStatus}"><c:choose>
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
<%@include file="../include/include.js.jsp" %>
<script src="${resComRoot}/js/mobile/layer.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/Api.js"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js"></script>
</html>