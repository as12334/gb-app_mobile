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
<!--语言弹窗-->
<ul class="lang-menu">
    <li class="current zh-CN"><a href="">中文</a></li><!--当前语言版本加current-->
    <li class="en-US"><a href="">English</a></li>
    <li class="ja-JP"><a href="">日文</a></li>
</ul>
<!--红包html开始  通过改变hongbao-wrap的class来改变红包样式，一共三种（hb_type_1,hb_type_2,hb_type_3）-->
<div class="hongbao-slide-wrap hongbao-wrap" id="hongbao">
    <div class="mui-slider hongbao-slider">
        <div class="mui-slider-group">
            <div class="mui-slider-item hb_type_1">
                <div class="img"></div>
                <div class="extra"></div>
            </div>
            <div class="mui-slider-item hb_type_2">
                <div class="img"></div>
                <div class="extra"></div>
            </div>
            <div class="mui-slider-item hb_type_3">
                <div class="img"></div>
                <div class="extra"></div>
            </div>
        </div>
        <div class="mui-slider-indicator">
            <div class="mui-indicator mui-active"></div>
            <div class="mui-indicator"></div>
            <div class="mui-indicator"></div>
        </div>
    </div>
</div>
<div id="hongbao_detail" class="hongbao_detail">
    <div class="hongbao_inner">
        <div class="icon-close"></div>
        <div class="hongbao"><!--未能拆时加disabled类名-->
            <div class="icon-open"></div>
            <div class="hongbao-time-txt">下次拆红包开始时间为</div>
            <div class="hongbao-time">2017-11-11 11:11:11</div>
            <a href="javascript:" class="btn-rule" id="btn-rule"></a>
            <!--红包规则元素-->
            <div class="hongbao-rule">
                <div class="txt">
                    <div class="nice-wrapper">
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                        游戏规则游戏规则
                    </div>
                </div>
                <a href="javascript:" class="icon-close-rule"></a>
            </div>
            <!--中奖时的提示-->
            <div class="win-hongbao tips">
                <div class="ttxt-1">恭喜您</div>
                <div class="ttxt-2">获得20元</div>
            </div>
            <!--未中奖时的提示-->
            <div class="lose-hongbao tips">
                <div class="ttxt-1">很遗憾</div>
                <div class="ttxt-2">还差一点就中奖了呦！</div>
            </div>
        </div>
        <div class="hongbao_extra"></div>
        <!--拆开红包时的彩带和光环-->
        <div class="caidai"></div>
        <div class="hongbao-light"></div>
        <!--关闭红包继续抽奖按钮-->
        <a href="javascript:" id="btn-ok" class="btn-ok"></a>
    </div>
</div>
<%@include file="include/include.js.jsp"%>
</body>
</html>
