<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@include file="include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 菜单容器 -->
    <%@include file="common/LeftMenu.jsp" %>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <%@include file="common/Head.jsp"%>
        <div class="mui-content mui-scroll-wrapper">
            <div class="mui-scroll">
                <!-- 主界面具体展示内容 -->
                <!--轮播-->
                <section class="mui-slider banner-slide">
                    <div class="close-slide"></div>
                    <div class="mui-slider-group">
                        <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/banner-01.jpg"/></a>
                        </div>
                        <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/banner-01.jpg"/></a>
                        </div>
                        <div class="mui-slider-item"><a href="#"><img src="../../mobile-v3/images/banner-01.jpg"/></a>
                        </div>
                    </div>
                </section>
                <!--公告-->
                <section class="notice">
                    <button type="button" class="mui-btn mui-btn-primary btn-title">公告</button>
                    <div class="notice-list">
                        <marquee behavior="scroll" scrollamount="2" direction="left">
                            <p>
                                <a href="">111尊敬的用户您好！本站于20161206进行升级！</a>
                                <a href="">222尊敬的用户您好！本站于20161206进行升级！</a>
                                <a href="">333尊敬的用户您好！本站于20161206进行升级！</a>
                                <a href="">444尊敬的用户您好！本站于20161206进行升级！</a>
                            </p>
                        </marquee>
                    </div>
                </section>
                <!--导航-->
                <section class="nav">
                    <div class="mui-scroll-wrapper mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
                        <div class="mui-scroll">
                            <a class="mui-control-item mui-active item-live" data-item="live">
                                真人视讯
                            </a>
                            <a class="mui-control-item item-casino" data-item="casino">
                                电子游艺
                            </a>
                            <a class="mui-control-item item-lottery" data-item="lottery">
                                彩票投注
                            </a>
                            <a class="mui-control-item item-sports" data-item="sports">
                                体育赛事
                            </a>
                            <a class="mui-control-item item-fish" data-item="fish">
                                捕鱼游戏
                            </a>
                        </div>
                    </div>
                </section>
                <!--api九宫格-->
                <section class="api-grid">
                    <ul class="mui-table-view mui-grid-view mui-grid-9 active" data-list="live">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item bb"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item opus"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">OPUS</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item ag"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">AG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item ebet"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">EBET</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item og"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">OG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item sa"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">SA</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item gd"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">GD</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item ds"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">DS</div>
                            </a>
                        </li>
                    </ul>
                    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="casino">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item png"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">PNG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item bsg"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">BSG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item pt"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">PT</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item sg"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">SG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item ag"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">AG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item bb"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">BBIN</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item hb"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">HB</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item mg"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">MG</div>
                            </a>
                        </li>
                    </ul>
                    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="lottery">
                        <!--彩票导航-->
                        <div class="lottery-nav">
                            <div class="mui-scroll-wrapper mui-slider-indicatorcode mui-segmented-control mui-segmented-control-inverted">
                                <div class="mui-scroll">
                                    <ul class="mui-list-unstyled mui-clearfix mui-bar-tab">
                                        <li><a href="#lottery1" class="mui-tab-item mui-active yzt">一指通彩票</a></li>
                                        <li><a href="#lottery2" class="mui-tab-item kg">KG彩票</a></li>
                                        <li><a href="#lottery3" class="mui-tab-item bb">BBIN彩票</a></li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div class="lottery-content"><!--彩票内容切换-->
                            <div id="lottery1" class="mui-control-content mui-active">
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">YZT</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                            </div>
                            <div id="lottery2" class="mui-control-content">
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">KG</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                            </div>
                            <div id="lottery3" class="mui-control-content">
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">BBIN</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                                <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                                    <a href="#">
                                        <img src="../../mobile-v3/images/lottery/lottery-demo.png" alt=""
                                             class="lottery-img"/>
                                        <div class="mui-media-body">安徽快3</div>
                                    </a>
                                </li>
                            </div>
                        </div><!--彩票内容切换结束-->
                    </ul>
                    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="sports">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item hg"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">HG</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item sb"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">SHABA</div>
                            </a>
                        </li>
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item im"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">IM</div>
                            </a>
                        </li>
                    </ul>
                    <ul class="mui-table-view mui-grid-view mui-grid-9" data-list="fish">
                        <li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-3">
                            <a href="#">
                                <span class="api-item gg"></span>  <!--根据class的不同来显示api图标-->
                                <div class="mui-media-body">GG</div>
                            </a>
                        </li>
                    </ul>
                </section>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
        <!--footer-->
        <%@include file="common/Footer.jsp"%>
    </div>
</div>
<%@include file="include/include.js.jsp"%>
<script type="text/javascript" src="${resRoot}/js/Index.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Head.js"></script>
<script type="text/javascript" src="${resRoot}/js/common/Menu.js"></script>
</body>
</html>
