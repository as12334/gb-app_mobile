<%--@elvariable id="siteApi" type="java.util.Map<java.lang.String,so.wwb.gamebox.model.company.site.po.SiteApi>"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
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
                <td class="bar-wallet">￥0</td>
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
            <p><a class="go btn-refresh">刷新额度</a></p>
            <p><a class="go btn-deposit">存款</a></p>
        </div>
    </div>
</div>
