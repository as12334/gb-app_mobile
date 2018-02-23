<%--@elvariable id="messageVo" type="so.wwb.gamebox.model.master.operation.vo.MobileActivityMessageVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${siteName}</title>
    <%@ include file="../include/include.head.jsp" %>
</head>

<body>
<!-- 侧滑导航根容器 -->
<div class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav">
                <a class="mui-icon mui-icon-left-nav mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
                <h1 class="mui-title">注册条款</h1>
            </header>
            <div class="mui-content mui-scroll-wrapper">
                <div class="mui-scroll">
                    <div class="register-rules-wrap" data-list="rules">
                        ${terms.value}
                    </div>
                </div>
            </div>
        <!-- off-canvas backdrop -->
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<%@ include file="../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/registe/RegisteRules.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
