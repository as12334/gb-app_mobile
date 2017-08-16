<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/include/include.inc.jsp" %>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:if test="${os ne 'android'}">
        <header class="mui-bar mui-bar-nav">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.passport_auto['冻结安全密码']}</h1>
        </header>
        </c:if>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-row">
                <div class="warning-box">
                    <span class="title">${views.passport_auto['密码错误已达上限']}</span>
                    <span class="title">${views.passport_auto['被冻结3小时']}</span>
                    <span>${views.passport_auto['冻结时间']}：${lockTime}</span>
                    <a data-url="${customer}">${views.passport_auto['联系客服']}</a>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<script>
    if(os == 'app_ios'){
        $('.mui-action-back').on('tap', function () {
            goBack();
        });
    }
    mui('body').on('tap', 'a[data-url]', function () {
        var url = $(this).data('url');
        if(os == 'app_ios'){
            gotoIndex(3);
        }else{
            mui.openWindow({
                url: url,
                id: url,
                extras: {},
                createNew: false,
                show: {autoShow: true},
                waiting: {
                    autoShow: true,
                    title: '正在加载...'
                }
            });
        }

    });
</script>
</body>