<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="content-title home-title">
    <h4>${views.themes_auto['热门彩种']}</h4>
    <a data-skip="/lottery/mainIndex.html" data-target="2" class="mui-pull-right home-more-btn moreLottery">
        ${views.themes_auto['更多']}<span class="mui-icon mui-icon-more-filled"></span>
    </a>
</div>
<ul class="mui-table-view mui-grid-view diy-grid-9">
    <li class="diy-table-view-cell mui-col-xs-3">
        <a class="item" data-bet="/lottery/ssc/cqssc/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-cqssc"></span>
            <span class="lottery-title">${views.themes_auto['重庆时时彩']}</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3">
        <a class="item" data-bet="/lottery/lhc/hklhc/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-hklhc"></span>
            <span class="lottery-title">${views.themes_auto['香港六合彩']}</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3">
        <a class="item" data-bet="/lottery/pk10/bjpk10/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-bjpk10"></span>
            <span class="lottery-title">北京PK拾</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3">
        <a class="item" data-bet="/lottery/k3/hbk3/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-hbk3"></span>
            <span class="lottery-title">湖北快3</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3 none">
        <a class="item" data-bet="/lottery/ssc/tjssc/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-tjssc"></span>
            <span class="lottery-title">${views.themes_auto['天津时时彩']}</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3 none">
        <a class="item" data-bet="/lottery/ssc/xjssc/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-xjssc"></span>
            <span class="lottery-title">${views.themes_auto['新疆时时彩']}</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3 none">
        <a class="item" data-bet="/lottery/k3/jsk3/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-jsk3"></span>
            <span class="lottery-title">江苏快3</span>
        </a>
    </li>
    <li class="diy-table-view-cell mui-col-xs-3 none">
        <a class="item" data-bet="/lottery/k3/gxk3/index.html" data-status="normal">
            <span class="lottery-ico lottery-ico-gxk3"></span>
            <span class="lottery-title">广西快3</span>
        </a>
    </li>
</ul>
