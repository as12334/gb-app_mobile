<%--@elvariable id="vSystemAnnouncementListVo" type="so.wwb.gamebox.model.company.operator.vo.SystemAnnouncementListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <title></title>
  <%@ include file="/include/include.head.jsp" %>
</head>
<body>
<form>
  <div class="modal-body">
      <pre style="white-space: pre-wrap;word-wrap: break-word;border: 0px;text-indent: -70px;">
          ${vSystemAnnouncementListVo.result.get(0).shortContentText100}
      </pre>
  </div>
</form>
</body>
</html>
