<%--手机免转页面--%>
<%--@elvariable id="player" type="so.wwb.gamebox.model.master.player.po.VUserPlayer"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.mine_auto['额度转换']}</title>
    <%@ include file="../../include/include.head.jsp" %>
</head>
<c:choose>
    <c:when test="${isDemo}">
        <body class="gb-theme mine-page no-backdrop" >
        <header class="mui-bar mui-bar-nav">
            <a data-rel='{"target":"goToLastPage","opType":"function"}' class="mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.mine_auto['额度转换']}</h1>
        </header>
        <center>
            <img src="${resRoot}/themes/images/no_limit.png" width="90%" style="margin-top: 150px;" />
        </center>
        </body>
    </c:when>
    <c:otherwise>
        <body class="exchange">
        <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
            <!-- 主页面容器 -->
            <div class="mui-inner-wrap">
                <header class="mui-bar mui-bar-nav">
                    <a data-rel='{"target":"goToLastPage","opType":"function"}' class="mui-icon mui-icon-left-nav mui-pull-left"></a>
                    <h1 class="mui-title">${views.mine_auto['额度转换']}</h1>
                </header>

                <div class="mui-content mui-scroll-wrapper" id="refreshContainer">
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
<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/transfer/auto/Index.js?v=${rcVersion}"></script>
<%@ include file="/include/include.footer.jsp" %>
