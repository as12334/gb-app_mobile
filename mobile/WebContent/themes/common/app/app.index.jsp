<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<%
    if (userAgent.contains("MicroMessenger")){
        myos = "wechat";
    } else if (userAgent.contains("Android")) {
        myos = "h5_android";
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

<link rel="stylesheet" href="${resRoot}/js/slider/ma5slider.min.css?v=${rcVersion}" />
<style type="text/css">
    body {background: #fff;}
    .header {width: 100%; height: 44px; color: #fff; line-height: 44px; font-weight: 500; position: fixed; z-index: 1000}
    .header i {
        position: absolute; z-index: 101;
        display: inline-block; height: 44px; vertical-align: middle;
        width: 44px; background: url("${resRoot}/themes/images/icon_close.png") no-repeat center; background-size: 20px;}
    .header span {position: absolute;display: inline-block;vertical-align: middle;text-align: center;width: 100%;left: 0;z-index: 100}
    .container {
        position: absolute;
        width: 100%;
        top: 44px;
        overflow: hidden;
        z-index: 98;
    }
    .container content {
        background:#06815d;
        overflow-y: scroll;
    }
    .gb-qr {width: 100%; text-align: center;margin: 0;}
    .qrcode-img img {width: 240px; height: 240px; margin: 10px;}
    .ioss a {white-space: nowrap;width: 90px;padding: 10px 0;}
    .icon {position: absolute; z-index: 99; width: 100%; text-align: center; top: 100px;}
    .icon img {width: 60px; height: 60px; border: 3px solid #fff; border-radius: 6px;}
    .ios_guide {margin-top: 15px; height: 130px; background: #000; padding: 20px;}
    .ios_guide a {padding:16px 0; width: 130px; display: inline-block; background: #262729; border-radius: 3px; color: #fff;}
    .ma5slider {margin-bottom: 0;   }
    .ma5slider img{width: 100%;}
    .inside-dots .dots {margin-bottom: 5px;}
    .inside-dots .dots>.dot.active {
        background-color: #007aff;
    }
</style>

<body class="gb-theme mine-page login-page">

<div class="header gb-header">
    <i class="close"></i>
    <span>${views.app_auto['下载最新客户端']}</span>
</div>

<div class="container">
    <div class="content">
        <div class="icon">
            <img src="${root}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" />
        </div>
        <c:choose>
            <c:when test="${os eq 'wechat'}">
                <img src="${resRoot}/images/wechat_open.png" />
            </c:when>
            <c:otherwise>
                <div class="mui-content-padded gb-qr">
                    <c:if test="${os eq 'h5_android' || os eq 'pc'}">
                        <div class="qrcode-img"><img src="data:image/png;base64, ${androidQrcode}"/></div>
                        <div>
                            <a data-href="${androidUrl}" class="mui-btn mui-btn-primary btn-download">${views.app_auto['下载Android版']}</a>
                        </div>
                    </c:if>
                    <c:if test="${os eq 'ios' || os eq 'pc'}">
                        <div class="qrcode-img"><img src="data:image/png;base64, ${iosQrcode}"/></div>
                        <a data-href="${iosUrl}" class="mui-btn mui-btn-primary  btn-download">${views.app_auto['下载IOS版']}</a>
                        <div class="par-1" style="display: none">
                            <img src="${resRoot}/images/ios/img01.png"/>
                            <div class="logo-big">
                                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
                            </div>
                            <div class="logo-small">
                                <img src="${root}/ftl/commonPage/images/app_logo/app_logo_${siteId}.png"/>
                            </div>
                        </div>

                        <div class="ios_guide">
                            <div class="column">
                                <p class="small-12 columns center_align" style="font-size: 16px; margin-bottom: 0;">${views.app_auto['安装APP']}</p>
                            </div>
                            <div class="column center_align margin-right">
                                <a href="javascript:;" class="button ios8" style="margin:15px 15px 0 0;">iOS 8</a>
                                <a href="javascript:;" class="button ios9">iOS 9${views.app_auto['或以上']}</a>
                            </div>
                        </div>

                        <div id="ios8" class="ma5slider inside-navs inside-dots loop-mode">
                            <div class="slides">
                                <a href="#slide-1"><img src="${resRoot}/images/ios/ios8_1.png"></a>
                            </div>
                        </div>
                        <div id="ios9" class="ma5slider inside-navs inside-dots loop-mode">
                            <div class="slides">
                                <a href="#slide-1"><img src="${resRoot}/images/ios/ios9_1.png"></a>
                                <a href="#slide-2"><img src="${resRoot}/images/ios/ios9_2.png"></a>
                                <a href="#slide-3"><img src="${resRoot}/images/ios/ios9_3.png"></a>
                                <a href="#slide-4"><img src="${resRoot}/images/ios/ios9_4.png"></a>
                                <a href="#slide-5"><img src="${resRoot}/images/ios/ios9_5.png"></a>
                                <a href="#slide-6"><img src="${resRoot}/images/ios/ios9_6.png"></a>
                            </div>
                        </div>

                    </c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>

<script src="${resRoot}/js/slider/jquery-ui.min.js"></script>
<script src="${resRoot}/js/slider/ma5slider.min.js"></script>
<script>
    $(function() {
        $('div#ios8').ma5slider();
        $('div#ios9').ma5slider();
        $('div#ios9').hide();
        $('i.close').click(function () {
            window.location.href="about:blank";
            window.close();
        });

        $('a.ios8').click(function () {
            $('div#ios8').show();
            $('div#ios9').hide();
        })

        $('a.ios9').click(function () {
            $('div#ios8').hide();
            $('div#ios9').show();
        })
    })
</script>