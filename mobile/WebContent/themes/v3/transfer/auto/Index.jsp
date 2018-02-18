<%--手机免转页面--%>
<%--@elvariable id="player" type="so.wwb.gamebox.model.master.player.po.VUserPlayer"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/themes/v3/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.mine_auto['额度转换']}</title>
    <%@ include file="/themes/v3/include/include.head.jsp" %>
</head>
<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
        <c:if test="${os ne 'android'}">
            <header class="mui-bar mui-bar-nav">
                <c:if test="${os ne 'app_ios'}">
                    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
                </c:if>
                <h1 class="mui-title">${views.mine_auto['额度转换']}</h1>
            </header>
        </c:if>
        <center>
            <img src="${resRoot}/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
        </center>
        </body>
    </c:when>
    <c:otherwise>

        <body class="gb-theme mine-page">
        <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
            <!-- 主页面容器 -->
            <div class="mui-inner-wrap">
                <c:if test="${os ne 'android'}">
                    <header class="mui-bar mui-bar-nav">
                        <c:if test="${os ne 'app_ios'}">
                            <%@ include file="/include/include.toolbar.jsp" %>
                        </c:if>
                        <h1 class="mui-title">${views.transfer_auto['资金管理转账']}</h1>
                        <%@ include file="/themes/v3/common/Assert.jsp" %>
                    </header>
                </c:if>
                <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0!important"':''}>
                    <div class="mui-scroll">
                            <%--api余额--%>
                        <%@include file="Api.jsp" %>
                        <div class="mui-row">
                            <div class="gb-form-foot p-t-0 p-b-0">
                                <button class="mui-btn mui-btn-primary m-t-sm submit recovery" data-rel='{"target":"recovery","opType":"function"} '>${views.transfer_auto['一键回收']}</button>
                            </div>
                        </div>
                        <div class="mui-row">
                            <div class="gb-form-foot p-t-0">
                                <button class="mui-btn mui-btn-primary m-t-sm submit reload" data-rel='{"target":"reload","opType":"function"}'>${views.transfer_auto['一键刷新']}</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mui-off-canvas-backdrop"></div>
            </div>
        </div>
        </body>


        </html>

    </c:otherwise>
</c:choose>

<%@ include file="/include/include.footer.jsp" %>
<%@ include file="/themes/v3/include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/transfer/auto/Index.js"></script>
<%--<%@ include file="../include/include.js.jsp" %>--%>