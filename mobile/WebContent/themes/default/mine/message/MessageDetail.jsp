<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${messages.common['SystemAnnouncement.'.concat(messageType)]}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
</head>

<%@ include file="/themes/common/mine/message/message.messagedetail.jsp" %>

</html>
<%@ include file="/include/include.footer.jsp" %>
