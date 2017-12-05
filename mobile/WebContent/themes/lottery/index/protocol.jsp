<%--@elvariable id="field" type="java.util.List<so.wwb.gamebox.model.master.setting.po.FieldSort>"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>

<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.index_auto['注册条款']}</title>
    <%@ include file="/include/include.js.jsp" %>
</head>
<body class="gb-theme mine-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${views.index_auto['注册条款']}</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0!important"':''}>
                <div class="mui-scroll" style="padding: 10px; background: #ffffff">
                    ${not empty terms.value ? terms.value : terms.defaultValue}
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
    </div>
</body>
<script>
    mui.init({});
    mui('.mui-scroll-wrapper').scroll();
</script>
</html>
<%@ include file="/include/include.footer.jsp" %>