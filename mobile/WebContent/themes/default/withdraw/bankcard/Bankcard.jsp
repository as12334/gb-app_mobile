<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty userBankCard ? views.withdraw_auto['添加'] : views.withdraw_auto['我的']}${views.withdraw_auto['银行卡']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <%@ include file="/include/include.js.jsp" %>
</head>

<%@ include file="/themes/common/withdraw/bankcard/bankcard.bankcard.jsp" %>

</html>
