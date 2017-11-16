<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<footer class="mui-bar mui-bar-tab">
    <soul:button target="${root}/wallet/deposit/index.html" text="" opType="href" cssClass="mui-tab-item">
        <span class="mui-icon icon-deposit"></span>
        <span class="mui-tab-label">存款</span>
    </soul:button>
    <soul:button target="${root}/discounts/index.html" text="" opType="href" cssClass="mui-tab-item ${skip == 1?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">优惠</span>
    </soul:button>
    <soul:button target="${root}/mainIndex.html" text="" opType="href" cssClass="mui-tab-item ${empty skip || skip == 2?'mui-active':''}">
        <span class="mui-icon icon-home"></span>
        <span class="mui-tab-label">首页</span>
    </soul:button>
    <soul:button target="loadCustomer" text="" opType="function" cssClass="mui-tab-item">
        <span class="mui-icon icon-service"></span>
        <span class="mui-tab-label">客服</span>
    </soul:button>
    <soul:button target="${root}/mine/index.html?channel=mine" text="" opType="href" cssClass="mui-tab-item ${channel == 'mine'?'mui-active':''}">
        <span class="mui-icon icon-mine"></span>
        <span class="mui-tab-label">我的</span>
    </soul:button>
</footer>