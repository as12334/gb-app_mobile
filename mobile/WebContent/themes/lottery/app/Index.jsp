<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.app_auto['下载最新客户端']}</title>
    <link rel="stylesheet" href="${resRoot}/themes/default/ios/foundation.css?v=${rcVersion}"/>
    <link rel="stylesheet" href="${resRoot}/themes/default/ios/layout.css?v=${rcVersion}"/>
    <script src="${resRoot}/js/plugin/modernizr.js?v=${rcVersion}"></script>
    <script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/plugin/foundation.min.js?v=${rcVersion}"></script>
    <style type="text/css">
        body {background: #fff;}
        .gb-qr {width: 100%; text-align: center}
        .qrcode-img img {width: 60%;}
        .btn-download {margin: 10px;}
        .ioss a {white-space: nowrap;width: 90px;padding: 10px 0;}
    </style>
</head>

<%@ include file="/themes/common/app/app.index.jsp" %>

</html>