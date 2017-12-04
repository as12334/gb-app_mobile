<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <title>
        <c:if test="${payAccount.bankCode eq 'wechatpay'}">
            ${views.deposit_auto['转账到微信']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'alipay'}">
            ${views.deposit_auto['转账到支付宝']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'qqwallet'}">
            ${views.deposit_auto['转账到QQ钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'jdwallet'}">
            ${views.deposit_auto['转账到京东钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'bdwallet'}">
            ${views.deposit_auto['转账到百度钱包']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'onecodepay'}">
            ${views.deposit_auto['转账到一码付']}
        </c:if>
        <c:if test="${payAccount.bankCode eq 'other'}">
            ${payAccount.customBankName}
        </c:if>
    </title>
</head>

<%@ include file="/themes/common/deposit/deposit.electronic.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>