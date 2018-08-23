<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp"%>
    <style>
        #weixin-tip{display:none; position: fixed; left:0; top:0; background: rgba(0,0,0,0.8); filter:alpha(opacity=80); width: 100%; height:100%; z-index: 100;}
        #weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%; position: relative;}
        #weixin-tip .close {
            color: #fff;
            padding: 5px;
            font: bold 20px/20px simsun;
            text-shadow: 0 1px 0 #ddd;
            position: absolute;
            top: 0;
            left: 5%;
        }
        #weixin-tip img{max-width: 100%; height: auto;}
    </style>
</head>
<body onload="initPage()">
<input type="hidden" value="${isWeixin}" id="is_weixin"/>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap">
    <!-- 菜单容器 -->
    <aside class="mui-off-canvas-left">
    </aside>
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <!-- 主页面标题 -->
        <header class="mui-bar mui-bar-nav mui-bar-blue">
            <a class="mui-icon mui-icon-arrowleft mui-pull-left mui-action-back"></a>
            <h1 class="mui-title">APP下载</h1>
        </header>
        <div class="mui-content mui-scroll-wrapper mui-content-without-footer-address download-content">
            <div class="mui-scroll" id="mui_scroll">
                <!-- 主界面具体展示内容 -->
                <img src="${ftlRoot}/ftl/commonPage/images/app_icon/app_icon_${siteId}.png" class="app-logo" />
                <div class="tit">${siteName}</div>
                <div class="des">下载APP 再也无需输入网址</div>
                <div class="btn-wrap">
                    <a data-rel='{"target":"${root}/downLoad/downLoadIOS.html","opType":"href"}' class="btn-download ios">点击下载iOS版</a>
                    <a data-rel='{"target":"download","opType":"function","url":"${androidUrl}"}' class="btn-download android">${views.themes_auto['点击下载安卓版']}</a>
                </div>
            </div> <!--mui-scroll 闭合标签-->
        </div>  <!--mui-content 闭合标签-->
    </div>
</div>
<div id="weixin-tip" style="display: none;"><p><img src="${resRoot}/images/live_weixin.png" alt="微信打开"/></p></div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/download/DownLoad.js?v=${rcVersion}"></script>
</html>

<%@ include file="/include/include.footer.jsp" %>