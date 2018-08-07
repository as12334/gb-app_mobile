<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="include/include.inc.jsp" %>
<%@page import="so.wwb.gamebox.web.SessionManagerCommon" %>
<!DOCTYPE html>
<html>
<head>
    <c:set var="siteName" value="<%=SessionManagerCommon.getSiteName(request) %>"/>
    <c:set var="siteId" value="<%=SessionManagerCommon.getSiteId() %>"/>
    <title>${siteName}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <!--禁止百度转码-->
    <meta http-equiv="Cache-Control" content="no-siteapp"/>
    <!-- 优先使用 IE 最新版本和 Chrome -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>

    <link rel="stylesheet" href="${resRoot}/themes/mui.min.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/chessIndex.css?v=${rcVersion}"/>
    <c:set var="background_type" value="blue"/>
    <link rel="stylesheet" href="${resRoot}/themes/${background_type}/style.css?v=${rcVersion}"/>

</head>

<body>
<div class="index">
    <div class="home-header"><i class="iconfont icon-fanhui mui-action-back"></i> <span class="top-name">iOS APP下载</span></div>
    <div class="home1">
         <img src="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" class="log" />
        <div class="showindex">
            <div onclick="downIOS()" class="down-ios click-scare"><i class="log-ios"></i>&nbsp;&nbsp;&nbsp;点击下载iOS版</div>
            <div class="down-adr click-scare"  data-rel='{"target":"download","opType":"function","url":"${androidUrl}"}'><i class="log-adr"></i>&nbsp;&nbsp;&nbsp;点击下载安卓版</div>
        </div>
        <div class="showdown">

            <div onclick="downANZ()" class="down-anz click-scare">
                <span class="spcname" data-rel='{"target":"download","opType":"function","url":"${iosUrl}"}'>点击安装</span>
                <div class="loadEffect" id="loading">
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
            <div class="wleco">
                <p>欢迎下载本APP</p>
                <p class="spcp">请依步骤依序操作</p>
            </div>
            <div class="bzbg">
                <div class="hands"></div>
            </div>
        </div>
    </div>
    <div class="demonstration">
        <div class="lb">
            <div class="lb_img">
                <img src="${resRoot}/themes/images/step05_bg.jpg">
                <img src="${resRoot}/themes/images/step02_bg.jpg">
                <img src="${resRoot}/themes/images/step03_bg.jpg">
                <img src="${resRoot}/themes/images/step04_bg.jpg">
                <img src="${resRoot}/themes/images/step05_bg.jpg">
                <img src="${resRoot}/themes/images/step02_bg.jpg">
            </div>
            <ul>
            <ul>
                <li class="active"></li>
                <li></li>
                <li></li>
                <li></li>
            </ul>
        </div>
    </div>
</div>
</body>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery-eventlock/jquery-eventlock-1.0.0.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Common.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
<script>
    var root = '${root}';
    var resComRoot = '${resComRoot}';
    var resRoot = '${resRoot}';
    var imgRoot = '${imgRoot}';
    var rcVersion ='${rcVersion}';
    var random ='${random}';

    $(function () {
        var options = {
        };
        muiInit(options);
    });

    function download(obj, options) {
        var url = options.url;
        if (!url) {
            toast("暂无设置下载地址，请联系客服！");
            return;
        }
        complete(obj);
        //谷歌浏览器不支持新开跳转打开下载 、safari在设置为弹窗阻止时也是无法新开下载
        var ua = navigator.userAgent.toLowerCase();
        if (ua.indexOf('chrome') >= 0 || ua.indexOf("safari") >= 0) {
            goToUrl(url);
            return;
        }
        var win = window.open(url);
        if (!win) {
            window.location.href = url;
        }
    }
    function complete(obj){
        $(obj).addClass("loading");
        $(obj).html('<i class="spinner"></i>请到桌面查看进度');
    }

    /**
     * 下载客户端
     * @param obj
     * @param options
     */
    function downLoadApp(obj, options) {
        //是否要先登录再跳转登录页面
        var ajaxOption = {
            url: root + '/downLoad/downLoadShowQrcode.html',
            success: function (data) {
                var userAgent = whatOs();
                var targetUrl = root + "/downLoad/downLoad.html?userAgent=" + userAgent;
                if (data.showQrCode == true && data.isLogin != true) {
                    toast("请登入下载");
                    window.setTimeout(function () {
                        login(targetUrl);
                    }, 1500);
                } else {
                    goToUrl(targetUrl, null, targetUrl);
                }
            }
        };
        muiAjax(ajaxOption);
    }

    /**
     * 关闭下载提示
     */
    function closeDownLoad() {
        $(".banner-ads").hide();
    }

    var showJc =false;
    var spcback = false;
    document.addEventListener('touchmove',function(e){
        event.preventDefault();
    }, { passive: false });

    document.getElementsByClassName('demonstration')[0].style.bottom = -window.screen.availHeight+'px'
    var homeEle = document.getElementsByClassName('home1')[0]
    var startPoint = null;
    homeEle.addEventListener("touchstart",function(e){
        var e = e||window.event;
        startPoint = e.touches[0];
    })
    homeEle.addEventListener("touchend",function(e){
        if(!showJc)
            return
        var e=e||window.event;
        //e.changedTouches能找到离开手机的手指，返回的是一个数组
        var endPoint = e.changedTouches[0];
        //计算终点与起点的差值
        var x = endPoint.clientX - startPoint.clientX;
        var y = endPoint.clientY - startPoint.clientY;
        //设置滑动距离的参考值
        var d = 150;
        if(Math.abs(y)>d){
            if(y<0){
                spcback=true;
                document.getElementsByClassName('demonstration')[0].style.bottom=0+'px'
                document.getElementsByClassName('top-name')[0].innerHTML ="iOS APP教学"
                document.getElementsByClassName('home-header')[0].style.backgroundColor ="#9f9fa1";
            }
        }
    })

    var demonstrationEle = document.getElementsByClassName('demonstration')[0]
    var startPoint1 = null;
    demonstrationEle.addEventListener("touchstart",function(e){
        var e = e||window.event;
        startPoint1 = e.touches[0];
    })
    demonstrationEle.addEventListener("touchend",function(e){
        var e=e||window.event;
        //e.changedTouches能找到离开手机的手指，返回的是一个数组
        var endPoint = e.changedTouches[0];
        //计算终点与起点的差值
        var x = endPoint.clientX - startPoint1.clientX;
        var y = endPoint.clientY - startPoint1.clientY;
        //设置滑动距离的参考值
        var d = 150;
        if(Math.abs(y)>d){
            if(y>0){
                document.getElementsByClassName('demonstration')[0].style.bottom = -window.screen.availHeight+'px'
                document.getElementsByClassName('top-name')[0].innerHTML ="iOS APP安装";
                document.getElementsByClassName('home-header')[0].style.backgroundColor ="rgba(150, 149, 149, 0.205)";

            }
        }
    })

    function back(){
        if(spcback){
            spcback=false;
            document.getElementsByClassName('demonstration')[0].style.bottom = -window.screen.availHeight+'px'
            document.getElementsByClassName('top-name')[0].innerHTML ="iOS APP安装";
            document.getElementsByClassName('home-header')[0].style.backgroundColor ="rgba(150, 149, 149, 0.205)";
        }else{
            showJc=false;
            document.getElementsByClassName('icon-fanhui')[0].style.display ="none";
            document.getElementsByClassName('showindex')[0].style.display ="block";
            document.getElementsByClassName('showdown')[0].style.display ="none"
            document.getElementsByClassName('top-name')[0].innerHTML ="iOS APP下载"
            document.getElementsByClassName('home1')[0].style.backgroundImage = "url("+resRoot+"/themes/images/index_bg.jpg)";
        }
    }

    function downIOS(){
        showJc=true;
        document.getElementsByClassName('icon-fanhui')[0].style.display ="block";
        document.getElementsByClassName('showindex')[0].style.display ="none";
        document.getElementsByClassName('showdown')[0].style.display ="block"
        document.getElementsByClassName('top-name')[0].innerHTML ="iOS APP安装"
        document.getElementsByClassName('home1')[0].style.backgroundImage = "url("+resRoot+"/themes/images/step01_bg.jpg)";
    }

    function downANZ(){
        document.getElementsByClassName('spcname')[0].innerHTML ="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在桌面上查看安装"
        console.log(document.getElementById('loading'))
        document.getElementById('loading').style.display ="block"
    }

    var lb = document.querySelector(".lb");
    var lb_img = document.querySelector(".lb .lb_img");
    var img = document.querySelectorAll(".lb .lb_img img")
    var lis = document.querySelectorAll(".lb ul li");
    var leftWidth = window.screen.availWidth;
    lb_img.style.left=-window.screen.availWidth+'px';
    lb_img.style.width=window.screen.availWidth*6+'px';
    for(var i=0;i<img.length;i++){
        img[i].style.width=window.screen.availWidth+'px'
    }


    var i = 2;
    // 初始化手指坐标点
    var startPoint = 0;
    var startEle = 0;
    //手指按下
    lb.addEventListener("touchstart", function (e) {
        startPoint = e.changedTouches[0].pageX;
        startEle = lb_img.offsetLeft;
        // clearInterval(Time)
    });

    //手指滑动
    lb.addEventListener("touchmove", function (e) {
        var currPoint = e.changedTouches[0].pageX;
        var disX = currPoint - startPoint;
        var left = startEle + disX;
        lb_img.style.left = left + "px";
    });
    //当手指抬起的时候，
    lb.addEventListener("touchend", function (e) {
        var currPoint = e.changedTouches[0].pageX;
        var disX = - (currPoint - startPoint);
        var left = startEle + disX;
        if (disX > 150) {
            i++;
            for (var q = 0; q < lis.length; q++) {
                lis[q].className = '';
            }
            if (i == 6) {
                i = 2;
            }
            lis[i - 2].className = "active";
            lb_img.style.left = -leftWidth * (Math.round(disX / leftWidth) + i + 1) + 'px';
        } else {
            lb_img.style.left = -leftWidth * (i - 1) + "px";
        }
        if (disX < -150) {
            i--;
            for (var q = 0; q < lis.length; q++) {
                lis[q].className = '';
            }
            if (i == 1) {
                i = 5;
            }
            lis[i - 2].className = "active";
            lb_img.style.left = -leftWidth * (Math.round(-disX / leftWidth) + i - 2) + 'px';

        } else {
            lb_img.style.left = -leftWidth * (i - 1) + "px";
        }
        // Time = setInterval(autoplay, 2000);
    })
    //设置定时器
    // Time = setInterval(autoplay, 2000);
    function autoplay() {
        i++;
        for (var q = 0; q < lis.length; q++) {
            lis[q].className = '';
        }
        if (i == 7) {
            i = 2;
        }
        lis[i - 2].className = "active";
        for (var a = 0; a < leftWidth; a++) {
            setTimeout(function () {
                var left = lb_img.style.left ? lb_img.style.left : "-"+leftWidth+"px";
                left = parseInt(left) - 1;
                if (left < -1920) {
                    left = -321;
                }
                lb_img.style.left = left + "px";
            }, a);
        }
    }
</script>

