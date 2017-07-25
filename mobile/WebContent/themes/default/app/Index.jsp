<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/include/include.head.jsp" %>
    <title>${views.app_auto['下载最新客户端']}</title>
    <link rel="stylesheet" href="${resRoot}/themes/default/ios/foundation.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/default/ios/layout.css?v=${rcVersion}"/>
    <script src="${resRoot}/js/plugin/modernizr.js?v=${rcVersion}"></script>
    <script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/plugin/foundation.min.js?v=${rcVersion}"></script>
    <style type="text/css">
        body {background: #fff;}
        .gb-qr {width: 100%; text-align: center}
        .qrcode-img img {width: 60%;}
        .btn-download {margin-top: 10px;}
        .ioss a {white-space: nowrap;width: 90px;padding: 10px 0;}
    </style>
    <%
        if (userAgent.contains("MicroMessenger")){
            myos = "wechat";
        } else if (userAgent.contains("Android")) {
            myos = "browser_android";
        } else if (userAgent.contains("app_android")) {
            myos = "android";
        } else if (userAgent.contains("app_ios")) {
            myos = "app_ios";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod") || userAgent.contains("iOS")) {
            myos = "ios";
        }  else {
            myos = "pc";
        }
    %>
    <c:set value="<%=myos %>" var="os"/>
</head>

<body class="gb-theme mine-page login-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.app_auto['下载最新客户端']}</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <div class="mui-content-padded gb-qr">
                    <c:if test="${os eq 'browser_android' || os eq 'pc'}">
                        <div class="qrcode-img"><img src="data:image/png;base64, ${androidQrcode}"/></div>
                        <div>
                            <a data-href="${androidUrl}" class="mui-btn mui-btn-primary btn-download">${views.app_auto['下载Android版']}</a>
                        </div>
                    </c:if>
                    <c:if test="${os eq 'ios' || os eq 'pc'}">
                        <div class="qrcode-img"><img src="data:image/png;base64, ${iosQrcode}"/></div>
                        <a data-href="${iosUrl}" class="mui-btn mui-btn-primary  btn-download">${views.app_auto['下载IOS版']}</a>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${os eq 'wechat'}">
                        <h2>${views.app_auto['请在浏览器中打开']}</h2>
                    </c:when>
                    <c:when test="${os eq 'ios' || os eq 'pc'}">
                        <div class="par-1" style="display: none">
                            <img src="${resRoot}/images/ios/img01.png"/>
                            <div class="logo-big">
                                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
                            </div>
                            <div class="logo-small">
                                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
                            </div>
                        </div>

                        <div class="iosinstruction_box column">
                            <div class="column margintop1">
                                <p class="small-12 columns center_align" style="font-size: 16px;">${views.app_auto['安装APP']}</p>
                            </div>
                            <div class="column margintop1 center_align margin-right" style="margin-right:10px;">
                                <div class="small-6" style="float:left">
                                    <a href="#ios8" class="button">iOS 8</a>
                                </div>
                                <div class="small-6" style="float:right">
                                    <a href="#ios9" class="button">iOS 9${views.app_auto['或以上']}</a>
                                </div>
                            </div>
                        </div>
                        <div class="inst2 margintop1 sli1" id="ios8">
                            <ul class="example-orbit" data-orbit>
                                <li>
                                    <img src="${resRoot}/images/ios/img04.png" alt="slide 1"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">01</div>
                                                ${views.app_auto['点击']} ${siteName} APP
                                        </div>
                                    </div>
                                </li>
                                <li class="active sli2">
                                    <img src="${resRoot}/images/ios/img02.png" alt="slide 2"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">02</div>
                                                ${views.app_auto['在对话']}
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <div class="inst3 margintop1 marginbottom1" id="ios9">
                            <ul class="example-orbit" data-orbit>
                                <li>
                                    <img src="${resRoot}/images/ios/img03.png" alt="slide 1"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">01</div>
                                                ${views.app_auto['安装了App以后']}
                                        </div>
                                    </div>
                                </li>
                                <li class="active">
                                    <img src="${resRoot}/images/ios/img03_2.png" alt="slide 2"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">02</div>
                                                ${views.app_auto['前往通用']}
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <img src="${resRoot}/images/ios/img03_3.png" alt="slide 3"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">03</div>
                                                ${views.app_auto['设备管理']}
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <img src="${resRoot}/images/ios/img03_4.png" alt="slide 4"/>
                                    <div class="com-name small">Fujian ShiTong Photoelectric NetWork Co.,Ltd.</div>
                                    <div class="com-name">Fujian ShiTong Photoelectric NetWork Co.,Ltd.</div>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">04</div>
                                                ${views.app_auto['在列表中']}”Fujian ShiTong Photoelectric NetWork Co.,Ltd.”
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <img src="${resRoot}/images/ios/img03_5.png" alt="slide 5"/>
                                    <div class="com-name2 small">${views.app_auto['信任']}“Fujian ShiTong Photoelectric NetWork Co.,Ltd.”</div>
                                    <div class="com-name2">${views.app_auto['信任']}“Fujian ShiTong Photoelectric NetWork Co.,Ltd.”</div>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">05</div>
                                                ${views.app_auto['点击']} “Fujian ShiTong Photoelectric NetWork Co.,Ltd.”
                                        </div>
                                    </div>
                                </li>
                                <li>
                                    <img src="${resRoot}/images/ios/img03_6.png" alt="slide 6"/>
                                    <div class="orbit-caption center_align">
                                        <div class="orbit-text">
                                            <div class="orbit-number">06</div>
                                                ${views.app_auto['在对话']}
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
</div>
</body>

</html>

<script>
    $(document).foundation({
        orbit: {
            animation: 'slide',
            timer_speed: 10000,
            pause_on_hover: true,
            animation_speed: 500,
            navigation_arrows: false,
            bullets: true,
            circular: false
        }
    });

    mui('.mui-scroll-wrapper').scroll();

    $(function () {
        $('a[href*="#"]:not([href="#"])').click(function () {
            if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
                var target = $(this.hash);
                target = target.length ? target : $('[name=' + this.hash.slice(1) + ']');
                if (target.length) {
                    $('html, body').animate({
                        scrollTop: target.offset().top
                    }, 1000);
                    return false;
                }
            }
        });
    });
    $(function () {
        // 自适应代码
        if (document.documentElement.clientWidth > 1242) {
            document.querySelector('html').style.fontSize = '12.42px';
        } else {
            document.querySelector('html').style.fontSize = document.documentElement.clientWidth / 10 + 'px';
        }
        $(window).on('resize', function () {
            if (document.documentElement.clientWidth > 1242) {
                document.querySelector('html').style.fontSize = '12.42px';
            } else {
                document.querySelector('html').style.fontSize = document.documentElement.clientWidth / 10 + 'px';
            }
        });
    });
    mui('body').on('tap', '[data-href]', function () {
        open($(this).data("href"));
    });
    function open(_href) {
        mui.openWindow({
            url: _href,
            id: _href,
            extras: {},
            createNew: false,
            show: {
                autoShow: true
            },
            waiting: {
                autoShow: true,
                title: '${views.app_auto['正在加载']}'

            }
        })
    }
</script>
