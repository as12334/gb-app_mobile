<%@ include file="include.base.js.common.jsp" %>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
    var isAutoPay = '${isAutoPay}';
    var isDemo = '${isDemo}';
    var isMobileUpgrade = '${isMobileUpgrade}';
</script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/common.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/plugin/layer.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/ProgressDialog.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/main.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/include/store.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/curl/curl.js?v=${rcVersion}" data-curl-run=""></script>
<%--<script type="text/javascript">--%>
    <%--curl(['site/index/Comet'], function (Comet) {--%>
        <%--comet = new Comet();--%>
     <%--});--%>
<%--</script>--%>