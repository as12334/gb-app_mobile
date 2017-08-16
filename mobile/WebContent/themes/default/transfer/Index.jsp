<%--@elvariable id="player" type="so.wwb.gamebox.model.master.player.po.VUserPlayer"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.transfer_auto['转账']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <c:if test="${os ne 'app_ios'}">
                    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                </c:if>
                <h1 class="mui-title">${views.transfer_auto['资金管理转账']}</h1>
                <%@ include file="/themes/default/include/include.asset.jsp" %>
            </header>
        </c:if>
        <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <%--转账--%>
                <div id="transfer">
                    <%@include file="./include/transfer.jsp" %>
                </div>
                <%--api余额--%>

                <div class="mui-row" style="height: 30px;">
                    <button id="refreshAllApiBalance" class="mui-btn mui-pull-right"
                            style="margin-right: 10px;font-size: 12px; padding: 3px 10px;line-height: 1.4;">${views.transfer_auto['刷新余额']}
                    </button>
                </div>
                <div id="apiBalance">
                    <%@include file="./include/api.jsp" %>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<%@ include file="/include/include.base.js.common.jsp" %>
<script type="text/javascript"
        src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
</script>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery.validate/jquery.validate.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/jquery/jquery.validate.extend.mobile.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/global.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/plugin/layer.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/transfer/Index.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Assets.2.js?v=${rcVersion}"></script>
<script>
    $(function () {
        // 刷新页面后获取容器高度，解决IOS设备刷新时出现空白页问题
        $('.mui-inner-wrap').height();
    });
</script>
</html>
