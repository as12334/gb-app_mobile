<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<nav class="mui-bar mui-bar-buttom mui-bar-tab mui-hide _footerMenu">
    <a class="mui-tab-item mui-action ${channel == 'index' ? 'mui-active' : ''}" data-href="/mainIndex.html" >
        <span class="mui-icon bar-buttom-icon iconfont icon-home"></span>
        <span class="mui-tab-label">${views.include_auto['首页']}</span>
    </a>
    <a class="mui-tab-item" data-href="/lottery/mainIndex.html">
        <span class="mui-icon bar-buttom-icon iconfont icon-goucaidating"></span>
        <span class="mui-tab-label">${views.include_auto['大厅']}</span>
    </a>
    <a class="mui-tab-item" data-url="/wallet/deposit/index.html" id="footer_deposit">
        <span class="mui-icon bar-buttom-icon iconfont icon-chongzhitikuan"></span>
        <span class="mui-tab-label">${views.include_auto['存款']}</span>
    </a>
    <c:set var="isDemo" value="<%=SessionManagerCommon.getAttribute(SessionManagerCommon.SESSION_IS_LOTTERY_DEMO) %>" />
    <a class="mui-tab-item" data-url="${isDemo ? '' : '/wallet/withdraw/index.html'}" id="footer_transfer">
        <span class="mui-icon bar-buttom-icon iconfont icon-touzhujilu"></span>
        <span class="mui-tab-label">${views.mine_auto['取款']}</span>
    </a>
    <a class="mui-tab-item ${channel == 'mine' ? 'mui-active' : ''}" data-url="/mine/index.html" id="footer_mine">
        <span class="mui-icon bar-buttom-icon iconfont icon-huiyuanzhongxin"></span>
        <span class="mui-tab-label">${views.include_auto['我的']}</span>
    </a>
</nav>