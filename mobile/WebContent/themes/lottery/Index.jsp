<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set var="tid" value="${apiTypeId}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${siteName}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>

<body class="gb-theme index">
<!-- 侧滑导航根容器 -->
<div class="index-canvas mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="/include/include.menu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!--头部-->
        <header class="mui-bar mui-bar-nav _siteHeader" style="display: ${os == 'android' ? 'none' : ''}">
            <div class="mui-pull-left">
                <span class="index-action-menu mui-action-menu"></span>
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"></div>
            </div>
            <!-- 资产 -->
            <div class="mui-hide-bar"></div>
            <div class="mui-pull-right">
                <div class="user mui-hide _rightUnLogin">
                    <a class="btn mui-btn mui-btn-outlined btn-login">${views.include_auto['登录']}</a>
                    <a class="btn mui-btn mui-btn-outlined btn-register" data-href="/signUp/index.html">${views.include_auto['注册']}</a>
                    <a class="btn mui-btn mui-btn-outlined btn-try">${views.include_auto['试玩']}</a>
                </div>
            </div>
        </header>

        <!--底部-->
        <%@include file="/include/include.footer.jsp" %>
        <!-- 内容 -->
        <div class="mui-scroll-wrapper middle-content _cacheContent" id="mui-refresh">
            <div class="mui-scroll">
                <%@include file="include/include.banner.jsp" %>

                <ul class="mui-table-view mui-grid-view diy-grid-9 customer">
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="customer">
                            <span class="home-f-ico ico-service"></span>
                            <span class="lottery-title">${views.themes_auto['在线客服']}</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a data-href="/mine/index.html">
                            <span class="home-f-ico ico-service-2"></span>
                            <span class="lottery-title">${views.themes_auto['会员中心']}</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a data-href="/lottery/lotteryResultHistory/index.html">
                            <span class="home-f-ico ico-service-3"></span>
                            <span class="lottery-title">${views.themes_auto['开奖结果']}</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none" style="border-right: none">
                        <a href="javascript:">
                            <span class="home-f-ico ico-service-4"></span>
                            <span class="lottery-title">${views.themes_auto['走势图']}</span>
                        </a>
                    </li>
                </ul>

                <%@ include file="include/include.lottery.jsp" %>

                <div class="content-title home-title">
                    <h4>${views.themes_auto['热门开奖']}</h4>
                    <a data-href="/lottery/lotteryResultHistory/index.html" class="mui-pull-right home-more-btn">${views.themes_auto['更多']}<span class="mui-icon mui-icon-more-filled"></span></a>
                </div>
                <ul class="mui-table-view mui-table-view-chevron _result">
                    <!-- 热门开奖 -->
                </ul>
                <div class="margin"></div>
            </div>
        </div>

        <div class="desk">
            <div class="mui-col-xs-2 logo"><img src="${root}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" /></div>
            <div class="mui-col-xs-8 tip">
                <span class="desk-text">${views.game_auto['点击下方']}<em></em>${views.game_auto['添加到主屏幕']}</span>
            </div>
            <div class="mui-col-xs-1 close"><i></i></div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>

<script type="text/html" id="template_myLotteryTemplate">
        {{each list as value index}}
        <li class="mui-table-view-cell">
            <a class="mui-navigate-right draw-list-a"
               data-href="${root}/lottery/lotteryResultHistory/queryLotteryResultByCode.html?search.code={{value.code}}">
                <img class="mui-media-object draw-list-img" src="${resRoot}/themes/lottery/images/lottery_ico/{{value.code}}.png">
                <div class="mui-media-body draw-list-right">
                    <span class="title">{{value.name}}</span>
                    <span class="expect mui-pull-right"><font class="col-blue">{{value.expect}}期</font>&nbsp;
                                {{value.openTime}}
                                </span>
                    <p class="mui-ellipsis">
                        {{if value.type=="pk10"}}
                        <span class="inline-list-2">
                            {{each value.ball as ball index}}
                                    <i class="lottery-ball pks-num" num="{{ball}}">{{ball}}</i>
                            {{/each}}
                        </span>
                        {{else if value.type=="lhc"}}
                        <span class="inline-list-2">
                            {{each value.ball as ball index}}
                                {{if index==6}}
                                    <i class="draw-list-i-add">+</i>
                                {{/if}}
                                    <i class="lottery-ball lhc-num" num="{{ball}}">{{ball}}</i>
                            {{/each}}
                               </span>
                        <span class="inline-list-2">
                            {{each value.sx as sx index}}
                                {{if index==6}}
                                    <i style="margin-left: 12px"></i>
                                {{/if}}
                                    <i class="lottery-block">{{sx}}</i>
                            {{/each}}
                        </span>
                        {{else}}
                        <span class="inline-list-2">
                            {{each value.ball as ball index}}
                                    <i class="lottery-ball">{{ball}}</i>
                            {{/each}}
                        </span>
                        {{/if}}
                    </p>
                </div>
            </a>
        </li>
        {{/each}}
</script>

<script src="${resRoot}/js/mui/mui.pullToRefresh.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.pullToRefresh.material.js?v=${rcVersion}"></script>
<script>
    curl(['site/lottery/Index', 'site/game/ApiLogin', 'site/common/Menu', 'site/common/Footer', 'site/common/DynamicSeparation'],
        function (Page, ApiLogin, Menu, Footer, Dynamic) {
            page = new Page();
            page.apiLogin = new ApiLogin();
            page.menu = new Menu();
            page.footer = new Footer();
            page.dynamic = new Dynamic();
        }
    );
</script>
</html>