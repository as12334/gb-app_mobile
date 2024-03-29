<%--@elvariable id="apiList" type="java.util.List<so.wwb.gamebox.model.company.site.po.SiteApiRelation>"--%>
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
            <a style="color: #fff;" class="mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
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
                        <img src="${resRoot}/images/api/banner/${apiI18n.apiId}-${apiI18n.apiId eq 10?'1':apiDetail.get('apiTypeId')}.jpg"
                             width="100%" alt="">
                    </div>
                    <!-- 游戏项 -->
                    <div class="game-item api-grid " style="min-height: 0">
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
                                        <c:if test="${at.apiId == apiI18n.apiId && apiDetail.get('apiTypeId') == at.apiTypeId}">
                                            <c:choose >
                                                <c:when test="${at.apiId eq 10}">${at.siteApiName}</c:when>
                                                <c:otherwise>${at.name}</c:otherwise>
                                            </c:choose>
                                            <c:set var="flag" value="0"/>
                                        </c:if>
                                    </c:forEach>
                                </h1>
                                <div class="g-money">
                                    <div class="login-status">
                                        <span class="rmb-c">${apiDetail.get('currSign')}</span>${apiDetail.get('apiMoney')}<%--200.25--%>
                                        <%--<span class="icon-refresh"></span>--%>
                                    </div>
                                    <div class="unlogin-status mui-hide">
                                        <a href="login">${views.themes_auto['登陆查看余额']}</a>
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
<div id="apiType" class="mui-popover allgame-popover">
    <div class="mui-content mui-scroll-wrapper allgame-scroll popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view" id="activityTypeLi">
                <%--BBIN只显示一个--%>
                <c:set var="hasBBIN" value="false"/>
                <c:forEach var="a" items="${apiList}" varStatus="status">
                    <c:if test="${a.apiId==10&&hasBBIN eq 'false' || a.apiId!=10}">
                        <c:set var="apiStatus" value="${a.apiStatus}"></c:set>
                        <li class="mui-table-view-cell">
                            <soul:button text=""
                                         target="${root}/api/detail.html?apiId=${a.apiId}&apiTypeId=${a.apiTypeId}"
                                         opType="href"
                                         cssClass="_api">
                                <c:choose>
                                    <c:when test="${a.apiId eq 10}">${a.siteApiName}</c:when>
                                    <c:otherwise>${a.name}</c:otherwise>
                                </c:choose>
                            </soul:button>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
</body>
<%@include file="../include/include.js.jsp" %>
<script src="${resComRoot}/js/mobile/layer.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/Api.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js?v=${rcVersion}"></script>
</html>