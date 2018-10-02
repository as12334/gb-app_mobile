<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<c:if test="${skip!=0}">
    <div data-rel='{"target":"userAssert","opType":"function"}'class="login-info">
</c:if>
<c:if test="${skip==0}">
    <div class="login-info">
</c:if>
<div class="user_name"></div>
<div class="money"></div>
</div>
<div class="user_money_info" id="_login_info_data">
    <div class="money-shadow"></div>
    <c:if test="${skip!=0}">
        <div class="ex">
            <div class="top_part">
                <div class="total_asset">
                    <span class="asset_txt">${views.themes_auto['总资产']}</span>
                    <span class="asset_num bar-asset"></span>
                </div>
                <div class="wallet">
                    <span class="asset_txt">${views.themes_auto['钱包']}</span>
                    <span class="asset_num bar-wallet"></span>
                </div>
            </div>
            <!--中间滚动部分-->
            <ul class="central_scroll" id="api-balance">
            </ul>
            <!--底部按钮-->
            <div class="btn_group">
                <a id="refresh" data-rel='{"target":"refreshApi","opType":"function"}' class="one_key_recycle mui-hidden">${views.include_auto['刷新额度']}</a>
                <a id="recovery" data-rel='{"target":"recovery","opType":"function"}' class="one_key_recycle mui-hidden">${views.include_auto['一键回收']}</a>
                <a data-rel='{"opType":"href","target":"${root}/wallet/v3/deposit/index.html?skip=0"}' class="saving_money">${views.include_auto['存款']}</a>
            </div>
        </div>
    </c:if>
</div>