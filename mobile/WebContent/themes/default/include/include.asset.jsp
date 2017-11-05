<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="mui-hide-bar"></div>
<div class="mui-pull-right">
    <div class="menu mui-hide _userAsset" id="mui-asset">
        <a>
            <p id="bar-username"></p>
            <p>
                <%--<img src="${resRoot}/images/ico-wallet.png" height="10px" style="float: left;">--%>
                <span class="bar-asset mui-pull-right" style="padding-right: 0; color: #ffffff"></span>
            </p>
        </a>
        <div class="ex">
            <table>
                <tr>
                    <td>${views.include_auto['总资产']}</td>
                    <td><span class="bar-asset" style="padding-right: 0; color: #ffffff"></span></td>
                </tr>
            </table>
            <hr>
            <table>
                <tr>
                    <td>${views.include_auto['钱包']}</td>
                    <td class="bar-wallet"></td>
                </tr>
            </table>
            <hr>
            <div class="mui-scroll-wrapper mui-assets">
                <div class="mui-scroll">
                    <table id="api-balance">
                    </table>
                </div>
            </div>
            <div class="ct">
                <c:if test="${!isAutoPay}">
                    <p><a class="go btn-refresh">${views.include_auto['刷新额度']}</a></p>
                </c:if>
                <c:if test="${isAutoPay}">
                    <p><a class="go btn-recovery">${views.include_auto['一键回收']}</a></p>
                </c:if>
                <%--<c:if test="${os ne 'app_ios'}">--%>
                <p><a class="go btn-deposit" data-url="/wallet/deposit/index.html">${views.include_auto['存款']}</a></p>
                <%--</c:if>--%>
            </div>
        </div>
    </div>
    <div class="user mui-hide _rightUnLogin">
        <a class="btn mui-btn mui-btn-outlined btn-login">${views.include_auto['登录']}</a>
        <a class="btn mui-btn mui-btn-outlined btn-register" data-href="/signUp/index.html">${views.include_auto['注册']}</a>
        <a class="btn mui-btn mui-btn-outlined btn-try" >${views.include_auto['免费试玩']}</a>
    </div>
</div>

<%--

<script>
    curl(['${resRoot}/js/freetry','site/game/ApiLogin'],
        function (Page,ApiLogin) {
            page = new Page();
            page.apiLogin = new ApiLogin();
        }
    );
</script>--%>
