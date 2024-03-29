<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="include.inc.jsp"%>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>

<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery-eventlock/jquery-eventlock-1.0.0.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery.validate/jquery.validate.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/jquery/jquery.validate.extend.mobile.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/AppNative.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/Common.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/websocket/Comet.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/websocket/PopUp.js?v=${rcVersion}"></script>
<script>
    var curTheme = '${curTheme}';
    var root = '${root}';
    var resComRoot = '${resComRoot}';
    var resRoot = '${resRoot}';
    var imgRoot = '${imgRoot}';
    var random = '${random}';
    var language = '${language}';
    var mdRoot='${mdRoot}';
    var wsRoot = '${wsRoot}';
    var rcVersion='${rcVersion}';
    var utcOffSet = <%=SessionManagerCommon.getTimeZone().getRawOffset()/60/1000%>;
    var dateFormat={daySecond:'<%= CommonContext.getDateFormat().getDAY_SECOND() %>',day:'<%= CommonContext.getDateFormat().getDAY() %>',dayminute:'<%=CommonContext.getDateFormat().getDAY_MINUTE()%>'};
    var siteCurrency = '${siteCurrency}';
    var siteCurrencySign = '${siteCurrencySign}';
    $(function(){
        var Comet = new MSiteComet();
        Comet.init();
    });
</script>




