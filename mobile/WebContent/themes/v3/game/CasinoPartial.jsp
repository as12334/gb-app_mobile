<%--@elvariable id="tagGameMap" type="java.util.Map<java.lang.String, java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>>"--%>
<%--@elvariable id="tagName" type="java.util.Map<java.lang.String, java.lang.String>"--%>
<%--@elvariable id="allGames" type="java.util.List<so.wwb.gamebox.model.company.site.vo.GameCacheEntity>"--%>
<%--@elvariable id="casinoApis" type="java.util.Map<java.lang.String,so.wwb.gamebox.model.company.site.vo.ApiCacheEntity>"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/swiper.min.css" />
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->

    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-blue mui-bar-nav casino-page-bar">
            <a class="mui-icon  mui-icon-back mui-pull-left"></a>
            <a data-rel='{"target":"${root}/mainIndex.html","opType":"href"}' class="mui-icon mui-icon-home mui-pull-left"></a>
            <%---列表展示--%>
            <a class="icon-list2 mui-pull-right" data-rel='{"target":"listDisplay","opType":"function"}'></a>
            <%---图标展示--%>
            <a class="icon-list1 mui-pull-right" data-rel='{"target":"iconDisplay","opType":"function"}'></a>
            <a data-rel='{"target":"showSearch","opType":"function"}' class="mui-icon mui-icon-search mui-pull-right"></a>
        </header>
        <div class="mui-content  mui-content-casino-page">
            <!-- 主界面具体展示内容 -->
            <div id="pull_apiScroll">
                <div class="api-scroll swiper-container">
                    <div class="electronic-search" name="searchDiv">
                        <a class="btn-search">搜索</a>
                        <input type="text" class="mui-input-clear" placeholder="输入游戏名称" data-input-clear="3"><span class="mui-icon mui-icon-clear mui-hidden"></span>
                    </div>
                    <div class="swiper-wrapper">
                        <c:forEach items="${casinoApis}" var="i">
                            <div class="swiper-slide">
                                <div class="img-wrap">
                                    <i class='api-icon-2-${i.key}'></i>
                                </div>
                                <span class='title'>${i.value.relationName}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <!-- Swiper -->
            <div class="casino-game-type" id='apiScroll-wrap'>
                <div id="apiScroll-cont">
                    <div class="swiper-container g-t-slide-indicators">
                        <div class="swiper-wrapper">
                            <div class="swiper-slide">所有游戏</div>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGameMap[t.key])>0}">
                                    <div class="swiper-slide">${t.value}</div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="swiper-container g-t-slide-content">
                        <div class="swiper-wrapper">
                            <%--所有游戏--%>
                            <div class="swiper-slide mui-scroll">
                                <div class="casino-wrap">
                                    <div class="casino-list">
                                        <div class="mui-row">
                                            <c:forEach items="${allGames}" var="game">
                                                <div class="mui-col-xs-4">
                                                    <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.apiId}","dataStatus":"${game.status}",
                                                            "dataGameCode":"${game.code}","dataGameId":"${game.gameId}",
                                                            "dataApiName":"${game.name}","target":"goGame","opType":"function"}'>
                                                        <div class="img-wrap"><img data-lazyload="${soulFn:getImagePath(domain, game.cover)}"/></div>
                                                        <p>${game.name}</p>
                                                    </a>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%--其他标签游戏--%>
                            <c:forEach items="${tagName}" var="t">
                                <c:if test="${fn:length(tagGameMap[t.key])>0}">
                                    <div class="swiper-slide mui-scroll">
                                        <div class="casino-wrap">
                                            <div class="casino-list">
                                                <div class="mui-row">
                                                    <c:forEach items="${tagGameMap[t.key]}" var="game">
                                                        <div class="mui-col-xs-4">
                                                            <a href="#" data-rel='{"dataApiTypeId":"2","dataApiId":"${game.apiId}","dataStatus":"${game.status}",
                                                            "dataGameCode":"${game.code}","dataGameId":"${game.gameId}",
                                                            "dataApiName":"${game.name}","target":"goGame","opType":"function"}'>
                                                                <div class="img-wrap"><img data-lazyload="${soulFn:getImagePath(domain, game.cover)}"/></div>
                                                                <p>${game.name}</p>
                                                            </a>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <!--mui-scroll 闭合标签-->
        </div>
        <!--mui-content 闭合标签-->
    </div>
</div>
<script src="../../mobile-v3/js/mui/mui.min.js"></script>
<script src="../../mobile-v3/js/jquery/jquery-2.1.1.js"></script>
<script src="../../mobile-v3/js/swiper.min.js"></script>
<script src="../../mobile-v3/js/common.js"></script>
<script src="../../mobile-v3/js/casino/alloy_touch.css.js"></script>
<script src="../../mobile-v3/js/casino/transform.js"></script>
<script type="text/javascript">
    $(function() {
        function resetMin(){
            $('.g-t-slide-content').height($('.g-t-slide-content .swiper-slide.swiper-slide-active').height());// 左右滑动内容区域时，动态设定swiper的高度
            var min_h = $('#apiScroll-cont').height() > $('.casino-game-type').height() ? $('#apiScroll-cont').height() : $('.casino-game-type').height(); // 滑动内容区域的高度
            alloyT.min = window.innerHeight - 44 - min_h;
        }
//      顶部切换电子排列:
        $('.casino-page-bar .icon-list1').click(function(){
            $(' .casino-wrap .mui-row .mui-col-xs-4').addClass('mui-col-xs-12 item').removeClass('mui-col-xs-4');
            resetMin();
        });
        $('.casino-page-bar .icon-list2').click(function(){
            $(' .casino-wrap .mui-row .mui-col-xs-12').addClass('mui-col-xs-4').removeClass('mui-col-xs-12 item');
            resetMin();
        });
        $('.casino-page-bar .mui-icon-search').click(function(){
            $('.electronic-search').toggle();
        });

        var alloyT = null;
        //下拉显示顶部api滚动区域.
        function pull_apiScroll() {
            var scroller = document.querySelector("#apiScroll-cont"),
                    wrapper = document.querySelector("#apiScroll-wrap"),
                    pull_refresh = document.querySelector("#pull_apiScroll"),
                    index = 0;

            Transform(pull_refresh, true);
            Transform(scroller, true);
            var min_h = $('#apiScroll-cont').height() > $('.casino-game-type').height() ? $('#apiScroll-cont').height() : $('.casino-game-type').height();
            alloyT = new AlloyTouch({
                touch: "#apiScroll-wrap", //反馈触摸的dom
                vertical: true, //不必需，默认是true代表监听竖直方向touch
                target: scroller, //运动的对象
                property: "translateY", //被滚动的属性
                sensitivity: 1, //不必需,触摸区域的灵敏度，默认值为1，可以为负数
                factor: 1, //不必需,默认值是1代表touch区域的1px的对应target.y的1
                min: window.innerHeight - 44 - min_h, //不必需,滚动属性的最小值
                max: 0, //不必需,滚动属性的最大值
                change: function(value) {
                    if(value < 105) {
                        pull_refresh.translateY = value;
                        // scroller.translateY = value;
                    } else {
                        pull_refresh.translateY = 105;
                        // scroller.translateY = 70;
                    }

                },
                touchMove: function(evt, value) {
                    $('.electronic-search').hide();
                },
                touchEnd: function(evt, value) {
                    if(value >= 105) {
                        this.to(105);
                        return false;
                    }
                }
            })
        };
        pull_apiScroll();

        //	  api滚动区域:
        var swiper = new Swiper('.api-scroll', {
            slidesPerView: 5,
            spaceBetween: 0,
            loop: true,
            //    slideToClickedSlide: true,
            watchSlidesProgress: true,
            on: {
                slideChangeTransitionEnd: function() {
                    $('.swiper-slide-next').next('.swiper-slide').addClass('api-index').siblings().removeClass('api-index');
                },
            },
        });

        // api滑动
        var slideContent = new Swiper('.g-t-slide-content', {
            loop: true,
            loopedSlides: 5,
            autoHeight: true,
            on: {
                slideChangeTransitionEnd: function() {
                    $('.g-t-slide-content').height($('.g-t-slide-content .swiper-slide.swiper-slide-active').height());
                    resetMin();
                }
            }
        });
        var slideIndicators = new Swiper('.g-t-slide-indicators', {
            loop: true,
            loopedSlides: 5,
            slidesPerView: 'auto',
            touchRatio: 0.2,
            slideToClickedSlide: true,
        });
        slideContent.controller.control = slideIndicators;
        slideIndicators.controller.control = slideContent;
    });
</script>


<body>

<%@include file="../include/include.js.jsp" %>
<%--<script src="${resComRoot}/js/mobile/layer.js"></script>--%>
<script src="${resRoot}/js/mui/mui.lazyload.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.lazyload.img.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/swiper.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/casino/alloy_touch.css.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/casino/transform.js?v=${rcVersion}"></script>
<c:if test="${apiId == 6}">
    <script type="text/javascript" src="${resRoot}/js/game/PtGame.js?v=${rcVersion}"></script>
</c:if>
<script type="text/javascript" src="${resRoot}/js/game/Game.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/game/GoGame.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/casino/Casino.js?v=${rcVersion}"></script>
<%@ include file="/include/include.footer.jsp" %>
</body>
</html>
