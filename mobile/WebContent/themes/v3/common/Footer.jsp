<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<footer class="mui-bar mui-bar-tab mui-hidden footerMenu">
    <a data-rel='{"target":"${root}/wallet/deposit/index.html","opType":"href"}' class="mui-tab-item">
        <span class="mui-icon icon-deposit"></span>
        <span class="mui-tab-label">${views.themes_auto['存款']}</span>
    </a>
    <a data-rel='{"target":"${root}/discounts/index.html?skip=1","opType":"href"}' class="mui-tab-item ${skip == 1?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">${views.themes_auto['优惠']}</span>
    </a>
    <a data-rel='{"target":"${root}/mainIndex.html","opType":"href" }' class="mui-tab-item ${empty skip || skip == 2?'mui-active':''}">
        <span class="mui-icon icon-home"></span>
        <span class="mui-tab-label">${views.themes_auto['首页']}</span>
    </a>
    <a data-rel='{"target":"loadCustomer","opType","function" }' class="mui-tab-item">
        <span class="mui-icon icon-service"></span>
        <span class="mui-tab-label">${views.themes_auto['客服']}</span>
    </a>
    <a data-rel='{"target":"${root}/mine/index.html?channel=mine&skip=4","opType":"href"}' class="mui-tab-item ${channel == 'mine'?'mui-active':''}">
        <span class="mui-icon icon-mine"></span>
        <span class="mui-tab-label">${views.themes_auto['我的']}</span>
    </a>
</footer>