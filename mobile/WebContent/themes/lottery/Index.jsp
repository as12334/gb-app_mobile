<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set var="tid" value="${apiTypeId}"/>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/lottery/style.css?v=${rcVersion}" />
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
        <header class="mui-bar mui-bar-nav mui-hide _siteHeader">
            <div class="mui-pull-left">
                <span class="index-action-menu mui-action-menu"></span>
                <div class="logo"><img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"></div>
            </div>
            <!-- 资产 -->
            <%@include file="/include/include.asset.jsp" %>
        </header>

        <!--底部-->
        <%@include file="/include/include.footer.jsp" %>
        <!-- 内容 -->
        <div class="mui-scroll-wrapper middle-content" id="mui-refresh-index">
            <div class="mui-scroll">
                <div class="gb-notice">
                    <a type="button" class="mui-pull-left notice-icon"><i class="iconfont icon-gonggao"></i></a>
                    <ul class="mui-list-unstyled gb-notice-list">
                        <li>
                            <marquee behavior="scroll" scrollamount="7" direction="left">
                                <p>
                                    <a href="">1. 尊敬的用户您好！本站于20161206进行升级！</a><a href="">2. 尊敬的用户您好！本站于20161206进行升级！</a>
                                </p>
                            </marquee>
                        </li>
                    </ul>
                </div>
                <ul class="mui-table-view mui-grid-view diy-grid-9 customer">
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="customer">
                            <span class="home-f-ico ico-service"></span>
                            <span class="lottery-title">在线客服</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a data-href="/mine/index.html">
                            <span class="home-f-ico ico-service"></span>
                            <span class="lottery-title">会员中心</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a href="bjpks.html">
                            <span class="home-f-ico ico-service"></span>
                            <span class="lottery-title">开奖结果</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none" style="border-right: none">
                        <a href="bjpks.html">
                            <span class="home-f-ico ico-service"></span>
                            <span class="lottery-title">走势图</span>
                        </a>
                    </li>
                </ul>
                <div class="content-title home-title">
                    <h4>热门彩种</h4>
                    <a data-href="/lottery/mainIndex.html" class="mui-pull-right home-more-btn">更多<span class="mui-icon mui-icon-more-filled"></span></a>
                </div>
                <ul class="mui-table-view mui-grid-view diy-grid-9">
                    <li class="diy-table-view-cell mui-col-xs-3">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-cqssc"></span>
                            <span class="lottery-title">重庆时时彩</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-hklhc"></span>
                            <span class="lottery-title">香港六合彩</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-bjpk10"></span>
                            <span class="lottery-title">北京PK拾</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-sfssc"></span>
                            <span class="lottery-title">三分时时彩</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-tjssc"></span>
                            <span class="lottery-title">天津时时彩</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-xjssc"></span>
                            <span class="lottery-title">新疆时时彩</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-jsk3"></span>
                            <span class="lottery-title">江苏快3</span>
                        </a>
                    </li>
                    <li class="diy-table-view-cell mui-col-xs-3 none">
                        <a class="item _api" data-api-type-id="4" data-api-id="22" data-status="normal">
                            <span class="lottery-ico lottery-ico-gxk3"></span>
                            <span class="lottery-title">广西快3</span>
                        </a>
                    </li>
                </ul>
                <div class="content-title home-title">
                    <h4>热门开奖</h4>
                    <a data-href="/lottery/lotteryResultHistory/index.html" class="mui-pull-right home-more-btn">更多<span class="mui-icon mui-icon-more-filled"></span></a>
                </div>
                <ul class="mui-table-view mui-table-view-chevron">
                    <li class="mui-table-view-cell">
                        <a class="mui-navigate-right draw-list-a">
                            <img class="mui-media-object draw-list-img" src="${resRoot}/themes/lottery/images/lottery_ico/cqssc.png">
                            <div class="mui-media-body draw-list-right">
                                <span class="title">重庆时时彩</span>
                                <span class="mui-pull-right"><font class="col-blue">20170502077期</font>&nbsp;17-05-02 18:50</span>
                                <p class="mui-ellipsis">
                                    <span class="inline-list-2">
                                        <i class="lottery-ball">0</i>
                                        <i class="lottery-ball">1</i>
                                        <i class="lottery-ball">0</i>
                                        <i class="lottery-ball">2</i>
                                        <i class="lottery-ball">6</i>
                                    </span>
                                    <span class="inline-list-2">
                                        <i class="lottery-block">9</i>
                                        <i class="lottery-block">单</i>
                                        <i class="lottery-block">小</i>
                                        <i class="lottery-block">虎</i>
                                    </span>
                                </p>

                            </div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell">
                        <a class="mui-navigate-right draw-list-a">
                            <img class="mui-media-object draw-list-img" src="${resRoot}/themes/lottery/images/lottery_ico/bjpk10.png">
                            <div class="mui-media-body draw-list-right">
                                <span class="title">北京PK拾</span>
                                <span class="mui-pull-right"><font class="col-blue">20170502077期</font>&nbsp;17-05-02 18:50</span>
                                <p class="mui-ellipsis">
                                    <span class="inline-list-2">
                                        <i class="lottery-ball pks-num" num="1">1</i>
                                        <i class="lottery-ball pks-num" num="3">3</i>
                                        <i class="lottery-ball pks-num" num="4">4</i>
                                        <i class="lottery-ball pks-num" num="5">5</i>
                                        <i class="lottery-ball pks-num" num="6">6</i>
                                        <i class="lottery-ball pks-num" num="9">9</i>
                                        <i class="lottery-ball pks-num" num="2">2</i>
                                        <i class="lottery-ball pks-num" num="8">8</i>
                                        <i class="lottery-ball pks-num" num="10">10</i>
                                        <i class="lottery-ball pks-num" num="7">7</i>
                                    </span>
                                </p>

                            </div>
                        </a>
                    </li>
                    <li class="mui-table-view-cell">
                        <a class="mui-navigate-right draw-list-a">
                            <img class="mui-media-object draw-list-img" src="${resRoot}/themes/lottery/images/lottery_ico/sfssc.png">
                            <div class="mui-media-body draw-list-right">
                                <span class="title">三分时时彩</span>
                                <span class="mui-pull-right"><font class="col-blue">20170502077期</font>&nbsp;17-05-02 18:50</span>
                                <p class="mui-ellipsis">
                                    <span class="inline-list-2">
                                        <i class="lottery-ball">0</i>
                                        <i class="lottery-ball">1</i>
                                        <i class="lottery-ball">0</i>
                                        <i class="lottery-ball">2</i>
                                        <i class="lottery-ball">6</i>
                                    </span>
                                    <span class="inline-list-2">
                                        <i class="lottery-block">9</i>
                                        <i class="lottery-block">单</i>
                                        <i class="lottery-block">小</i>
                                        <i class="lottery-block">虎</i>
                                    </span>
                                </p>

                            </div>
                        </a>
                    </li>
                </ul>
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