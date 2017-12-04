<%--@elvariable id="payAccount" type="so.wwb.gamebox.model.master.content.po.PayAccount"--%>
<%--@elvariable id="playerRechargeVo" type="so.wwb.gamebox.model.master.fund.vo.PlayerRechargeVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <title>${views.deposit_auto['存款']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.base.js.common.jsp" %>
    <script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
    <script>
        var language = '${language.replace('_','-')}';
        var isLogin = '${isLogin}';
    </script>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
</head>

<%@ include file="/themes/common/deposit/deposit.result.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>