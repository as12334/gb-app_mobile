<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.UserPlayerTransferVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/include/include.head.jsp" %>
    <title>${views.verify_auto['设置新账号']}</title>
</head>

<body class="gb-theme mine-page login-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.verify_auto['设置新账号']}</h1>
            <a class="mui-icon mui-icon-home mui-pull-right" data-href="/"></a>
        </header>
        <div class="mui-content mui-scroll-wrapper" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">
                <form id="subForm">
                    <gb:token />
                    <div class="mui-row">
                        <div class="mui-input-group v-tip">
                            ${views.verify_auto['您的账号与其他玩家账号冲突']}
                        </div>
                    </div>
                    <div class="mui-row">
                        <div class="mui-input-group mine-form m-t-sm">
                            <div class="mui-input-row _pass">
                                <div class="form-row">
                                    <div class="cont">
                                        <input name="result.playerAccount" class="ico1" type="text" placeholder="${views.verify_auto['请输入新账号']}" autocomplete="off" maxlength="15" />
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mui-input-row">
                        <div class="gb-form-foot" style="height: 65px;">
                            <input type="hidden" name="search.id" value="${command.search.id}" />
                            <input type="hidden" name="passLevel" value="${command.passLevel}" />
                            <button class="mui-btn mui-btn-primary submit _sub" type="button">${views.verify_auto['提交']}</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>

</html>

<%@ include file="/include/include.js.jsp" %>
<script>
    curl(['site/verify/SetNewUsername'], function(Page) {
        page = new Page();
    });
</script>