<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css" />
    <title>${views.themes_auto['数字货币支付']}</title>
</head>

<%@ include file="/themes/common/deposit/Digiccy.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>