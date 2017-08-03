<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <title>${views.verify_auto['验证成功']}</title>
</head>

<body class="gb-theme mine-page login-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.verify_auto['验证成功']}</h1>
            <a class="mui-icon mui-icon-home mui-pull-right" data-href="/"></a>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <form id="vForm">
                    <div class="withdraw-out">
                        <div class="withdraw-not" style="padding-bottom: 10px;">
                            <h1><i class="tipbig success"></i></h1>
                            <div class="tiptext">
                                <p>${views.verify_auto['新登录密码设置成功']}</p>
                                <c:if test="${flag == 1}">
                                    <p>${views.verify_auto['您当前的登录账号为']}：<b class="co-tomato">${command.search.playerAccount}</b></p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    <%--<div class="v-s-tip">
                        <div>${views.verify_auto['下次您可以直接']}<c:if test="${flag == 1}">${views.verify_auto['用这个账号和刚才设置的密码']}</c:if>${views.verify_auto['在这两个地方登录']}：</div>
                        <div class="co-tomato">1、页面顶部的登录框；</div>
                        <div class="co-tomato">2、系统弹出的登录弹窗登录。</div>
                    </div>--%>
                    <div class="mui-input-row">
                        <div class="gb-form-foot" style="height: 65px;">
                            <input type="hidden" name="search.id" value="${command.search.id}" />

                            <button class="mui-btn mui-btn-primary submit _know" type="button">${views.verify_auto['知道了']}</button>
                        </div>
                    </div>
                </form>
                <form style="display: none" id="loginForm">
                    <input type="text" name="username" value="${command.search.playerAccount}" />
                    <input type="password" name="password" />
                    <input type="text" name="captcha" />
                </form>
            </div>
        </div>
    </div>
</div>
</body>

</html>

<%@ include file="/include/include.js.jsp" %>
<script>
    curl(['site/verify/ImportSuccess'], function(Page) {
        page = new Page();
    });
</script>
