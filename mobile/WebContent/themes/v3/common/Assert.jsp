<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-hide-bar" style="display: none;"></div>
<input id="isAutoPay" value="${isAutoPay}" hidden>
<soul:button target="" text="" opType="function" id="login-info" cssClass="mui-hide" tag="button">

</soul:button>
<div class="login-info" id="login-info" style="display: none">
    <div class="user_name"></div>
    <div class="money"></div>
    <div class="ex">
        <table>
            <tbody>
            <tr>
                <td>总资产</td>
                <td><span class="bar-asset" style="padding-right: 0; color: #ffffff"></span></td>
            </tr>
            </tbody>
        </table>
        <hr>
        <table>
            <tbody>
            <tr>
                <td>钱包</td>
                <td class="bar-wallet"></td>
            </tr>
            </tbody>
        </table>
        <hr>
        <div class="mui-scroll-wrapper mui-assets">
            <div class="mui-scroll">
                <table id="api-balance">
                    <tbody>
                        <%--异步求出需展示的api--%>
                        <%--<tr data="${i.value.apiId}">
                            <td>${gbFn:getApiName(i.value.apiId.toString())}</td>
                            <td class="_money" name="money_${i.value.apiId}"></td>
                        </tr>--%>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="ct">
            <p>
                <soul:button target="refreshApi" text="${views.include_auto['刷新额度']}" opType="function" cssClass="go btn-refresh"/>
            </p>
            <p>
                <soul:button target="${root}/wallet/deposit/index.html" text="${views.include_auto['存款']}" opType="href" cssClass="go btn-deposit"/>
            </p>
        </div>
    </div>
</div>
