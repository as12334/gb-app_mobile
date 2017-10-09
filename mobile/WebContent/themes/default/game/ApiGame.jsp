<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <title>${empty siteApi.name?(gbFn:getApiName(apiId)):siteApi.name}</title>
    <%@ include file="/include/include.js.jsp" %>
    <style>
        .mui-bar-tab .mui-tab-item .mui-icon {top: 0;}
    </style>
</head>
<input type="hidden" id="apiId" value="${apiId}">
<input type="hidden" id="apiTypeId" value="${apiTypeId}">
<body class="gb-theme index game-page game-page-detail">
<!-- 侧滑导航根容器 -->

        <!--头部-->
        <c:if test="${os ne 'android' && os ne 'app_ios'}">
        <header class="mui-bar mui-bar-nav" style="padding-left: 10px">
            <div class="mui-pull-left">
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png" alt=""></div>
            </div>
            <!-- 资产 -->
            <%@include file="/themes/default/include/include.asset.jsp" %>
        </header>
        </c:if>
        <c:if test="${os ne 'android' && os ne 'app_ios'}">
            <section class="site-address">
                ${views.game_auto['主页域名']}：${empty sysDomain?domain:sysDomain}
            </section>api
        </c:if>
        <!--底部-->
        <c:if test="${os ne 'android' && os ne 'app_ios'}">
            <%@include file="/themes/default/include/include.footer.jsp"%>
        </c:if>
        <!--滚动区域-->
        <div class="index-content mui-content mui-scroll-wrapper mui-fullscreen" id="game-scroll" style="${os eq 'android' or os eq 'app_ios'?'padding-top:0!important;':''}">
            <div class="mui-scroll">
                <div class="gb-fullpage">
                    <!-- 游戏类型 -->
                    <div class="game-type" style="width: 100%;">
                        <div class="mui-row">
                            <div class="mui-pull-left">
                                <div class="game-name">
                                    <span class="icon-hobby" style="text-align: center;margin: 0;">
                                        <img src="${resRoot}/images/api/round/${apiTypeId}-${apiId}.png" width="100%">
                                    </span>
                                    <span class="name">${empty siteApi.name?(gbFn:getApiName(apiId)):siteApi.name}</span>
                                </div>
                            </div>
                            <div class="mui-pull-right">
                                <a _href="/game.html?typeId=2" class="mui-btn mui-btn-block btn-back-index">${views.game_auto['回到电子首页']}</a>
                            </div>
                        </div>
                    </div>
                    <!-- 类型下拉折叠面板 -->
                    <div class="mui-table-view search-collapse">
                        <div class="mui-table-view-cell mui-collapse">
                            <a href="#" class="toggle-btn" style="right: 120px;">
                                <span class="mui-icon mui-icon-search"></span>
                            </a>
                            <div class="mui-collapse-content">
                                <div class="mui-row select-search-wrap">
                                    <!-- 搜索框 -->
                                    <div class="search-wrap">
                                        <form>
                                            <input type="text" placeholder="${views.game_auto['输入游戏名']}" id="game-name">
                                            <a class="mui-icon mui-icon-search" id="search-game"></a>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- 游戏列表 -->
                    <div class="mui-control-content mui-active">
                        <div class="mui-row">
                            <div class="electronic-list" style="margin-bottom: 0;">
                                <div class="mui-row" id="gameContent">
                                    <%@include file="/include/include.loading.jsp" %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>

</body>
<c:if test="${apiId == 6}">
    <script>
        window.top.loadJsFile('${resRoot}/js/game/PtGame.js?v=${rcVersion}');
    </script>
</c:if>
<script type="text/javascript">
    curl(['site/game/ApiGame', 'site/common/Assets', 'site/common/Menu','site/common/Footer', 'site/common/DynamicSeparation'],
            function (ApiGame, Assets, Menu, Footer, Dynamic) {
                page = new ApiGame();
                page.asset = new Assets();
                page.menu = new Menu();
                page.menu = new Footer();
                page.dynamic = new Dynamic();
            });
</script>
</html>
