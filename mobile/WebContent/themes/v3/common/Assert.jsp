<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<div data-rel='{"target":"userAssert","opType":"function"}' id="login-info" class="mui-hidden login-info">
    <div class="user_name"></div>
    <div class="money"></div>
    <div class="ex">
        <table>
            <tbody>
            <tr>
                <td>${views.themes_auto['总资产']}</td>
                <td><span class="bar-asset"></span></td>
            </tr>
            </tbody>
        </table>
        <hr>
        <table>
            <tbody>
            <tr>
                <td>${views.themes_auto['钱包']}</td>
                <td class="bar-wallet"></td>
            </tr>
            </tbody>
        </table>
        <hr>
        <div class="mui-scroll-wrapper mui-assets">
            <div class="mui-scroll">
                <table id="api-balance">
                    <tbody>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="ct">
            <p>
                <a id="refresh" data-rel='{"target":"refreshApi","opType":"function"}' class="go btn-refresh mui-hidden">${views.include_auto['刷新额度']}</a>
                <a id="recovery" data-rel='{"target":"recovery","opType":"function"}' class="go btn-refresh mui-hidden">${views.include_auto['一键回收']}</a>
            </p>
            <p>
                <a data-rel='{"opType":"href","target":"${root}/wallet/deposit/index.html"}' class="go btn-deposit">${views.include_auto['存款']}</a>
            </p>
        </div>
    </div>
</div>