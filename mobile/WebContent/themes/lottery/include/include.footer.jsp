<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<nav class="mui-bar mui-bar-buttom mui-bar-tab mui-hide _footerMenu">
    <a class="mui-tab-item mui-action ${channel == 'index' ? 'mui-active' : ''}" data-href="/mainIndex.html" >
        <span class="mui-icon bar-buttom-icon iconfont icon-shouye"></span>
        <span class="mui-tab-label">${views.include_auto['首页']}</span>
    </a>
    <a class="mui-tab-item" data-url="/lottery/lotteryResultHistory/index.html">
        <span class="mui-icon bar-buttom-icon iconfont icon-kaijiang"></span>
        <span class="mui-tab-label">${views.include_auto['开奖结果']}</span>
    </a>
    <a class="mui-tab-item" data-url="/lottery/mainIndex.html">
        <span class="mui-icon bar-buttom-icon iconfont icon-dating"></span>
        <span class="mui-tab-label">${views.include_auto['购彩大厅']}</span>
    </a>
    <a class="mui-tab-item" data-href="/lottery/bet/betOrders.html">
        <span class="mui-icon bar-buttom-icon iconfont icon-touzhu"></span>
        <span class="mui-tab-label">${views.include_auto['投注记录']}</span>
    </a>
    <a class="mui-tab-item ${channel == 'mine' ? 'mui-active' : ''}" data-url="/mine/index.html" id="footer_mine">
        <span class="mui-icon bar-buttom-icon iconfont icon-wode"></span>
        <span class="mui-tab-label">${views.include_auto['会员中心']}</span>
    </a>
</nav>