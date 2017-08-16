<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<%--@elvariable id="bank" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<%--@elvariable id="btc" type="so.wwb.gamebox.model.master.player.po.UserBankcard"--%>
<!DOCTYPE html>
<html>

<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.withdraw_auto['取款']}</title>
    <script src="${resRoot}/js/plugin/inputNumber.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/plugin/map.js?v=${rcVersion}"></script>
    <%@ include file="/include/include.js.jsp" %>
</head>

<%@ include file="/themes/common/withdraw/withdraw.index.jsp" %>

</html>
