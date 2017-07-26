<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<nav class="mui-bar mui-bar-tab mui-hide _footerMenu">
    <a class="mui-tab-item ${channel == 'index' ? 'mui-active' : ''}" data-url="/mainIndex.html" id="footer_index">
        <span class="mui-icon icon1"></span>
        <span class="mui-tab-label">${views.include_auto['首页']}</span>
    </a>
    <a class="mui-tab-item" data-url="/wallet/deposit/index.html" id="footer_deposit">
        <span class="mui-icon icon2"></span>
        <span class="mui-tab-label">${views.include_auto['存款']}</span>
    </a>
    <a class="mui-tab-item" data-url="${footerUrl}" id="footer_transfer">
        <span class="mui-icon icon3"></span>
        <span class="mui-tab-label">${views.mine_auto['取款']}</span>
    </a>
    <a class="mui-tab-item customer" id="footer_customer">
        <span class="mui-icon icon4"></span>
        <span class="mui-tab-label">${views.include_auto['客服']}</span>
    </a>
    <a class="mui-tab-item ${channel == 'mine' ? 'mui-active' : ''}" data-url="/mine/index.html" id="footer_mine">
        <span class="mui-icon icon5"></span>
        <span class="mui-tab-label">${views.include_auto['我的']}</span>
    </a>
</nav>