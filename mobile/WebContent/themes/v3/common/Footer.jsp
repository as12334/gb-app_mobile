<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<c:set var="uri" value="<%=request.getRequestURI()%>"/>
<footer class="mui-bar mui-bar-tab">
    <soul:button target="${root}/wallet/deposit/index.html" text="" opType="href" cssClass="mui-tab-item ${fn:contains(uri, '/wallet/deposit')?'mui-active':''}">
        <span class="mui-icon icon-deposit"></span>
        <span class="mui-tab-label">存款</span>
    </soul:button>
    <soul:button target="" text="" opType="href" cssClass="mui-tab-item ${fn:contains(uri, '/wallet/deposit')?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">优惠</span>
    </soul:button>
    <soul:button target="" text="" opType="href" cssClass="mui-tab-item ${uri eq '' || uri eq '/' ||fn:contains(uri, 'mainIndex')?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">首页</span>
    </soul:button>
    <soul:button target="" text="" opType="href" cssClass="mui-tab-item ${fn:contains(uri, '/wallet/deposit')?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">客服</span>
    </soul:button>
    <soul:button target="" text="" opType="href" cssClass="mui-tab-item ${fn:contains(uri, '/wallet/deposit')?'mui-active':''}">
        <span class="mui-icon icon-promo"></span>
        <span class="mui-tab-label">我的</span>
    </soul:button>
</footer>