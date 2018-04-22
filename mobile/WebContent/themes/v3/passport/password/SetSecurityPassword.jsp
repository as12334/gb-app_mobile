<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.passport_auto['设置安全密码']}</title>
    <%@ include file="../../include/include.head.jsp" %>
</head>

<body class="change-loginpassword">
<input type="hidden" name="hasName" value="${hasName}"/>
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">${views.passport_auto['设置安全密码']}</h1>
</header>
<div class="mui-content mui-scroll-wrapper">
    <form id="updateSecurityPwd">
        <div class="mui-scroll">
            <div class="mui-row">
                <div class="mui-input-group mine-form">
                    <%--<div class="mui-input-row"><label for="">真实姓名</label>
                        <div class="ct">
                            <input type="text" placeholder="请验证真实姓名">
                        </div>
                    </div>
                    <div class="mui-input-row"><label for="">当前密码</label>
                        <div class="ct">
                            <input type="text" placeholder="请输入当前密码">
                        </div>
                    </div>--%>
                    <div class="mui-input-row"><label for="">密码</label>
                        <div class="ct">
                            <input type="password" placeholder="请输入安全码" maxlength="6" name="pwd1">
                        </div>
                    </div>
                    <div class="mui-input-row"><label for="">确认密码</label>
                        <div class="ct">
                            <input type="password" placeholder="请再次输入安全码" maxlength="6" name="pwd2">
                        </div>
                    </div>
                </div>
            </div>
            <%--<div class="mui-row" style="display: none">
                <div class="warning-info"
                     style="text-align: center;color:red; font-size:12px;line-height:18px;margin-top: 5px;">
                <span><i
                        style="background:url(${root}/images/ico-notice.png) no-repeat center center;display: inline-block;width: 15px;height: 15px;background-size: cover;margin-right: 5px;vertical-align: middle;"></i>您输入有误，还可以输入3次
                </span>
            </div>
        </div>--%>
        <div class="mui-row">
            <div class="gb-form-foot">
                <a data-rel={"opType":"function","target":"submitSafeCode"} class="mui-btn mui-btn-primary submit">确认</a>
            </div>
        </div>
    </form>
</div>
</div>
</body>

<%@ include file="../../include/include.js.jsp" %>
<script type="text/javascript" src="${resRoot}/js/game/inputNumber.js"></script>
<script type="text/javascript" src="${resRoot}/js/password/SafePasswordCommon.js"></script>
<script type="text/javascript" src="${resRoot}/js/password/SetSecurityPassword.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
