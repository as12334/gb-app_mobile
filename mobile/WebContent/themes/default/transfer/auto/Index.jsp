<%--手机免转页面--%>
<%--@elvariable id="player" type="so.wwb.gamebox.model.master.player.po.VUserPlayer"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.transfer_auto['转账']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
</head>
<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                <h1 class="mui-title">${views.transfer_auto['资金管理转账']}</h1>
                <%@ include file="/themes/default/include/include.asset.jsp" %>
            </header>
        </c:if>
        <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <%--api余额--%>
                <%@include file="Api.jsp" %>
                <div class="mui-row">
                    <div class="gb-form-foot p-t-0 p-b-0">
                        <button class="mui-btn mui-btn-primary m-t-sm submit recovery">${views.transfer_auto['一键回收']}</button>
                    </div>
                </div>
                <div class="mui-row">
                    <div class="gb-form-foot p-t-0">
                        <button class="mui-btn mui-btn-primary m-t-sm submit reload">${views.transfer_auto['一键刷新']}</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<script>
    curl(['site/transfer/auto/Index', 'site/common/Assets'], function (Index, Assets) {
        page = new Index();
        page.asset = new Assets();
    });
</script>
</html>
