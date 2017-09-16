<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="content-title home-title">
    <h4>${views.themes_auto['热门彩种']}</h4>
    <a data-skip="/lottery/mainIndex.html" data-target="2" class="mui-pull-right home-more-btn moreLottery">
        ${views.themes_auto['更多']}<span class="mui-icon mui-icon-more-filled"></span>
    </a>
</div>
<ul class="mui-table-view mui-grid-view diy-grid-9" id="hotLottery">

</ul>

<script type="text/html" id="template_hotLotteryTemplate">
    {{each list as value index}}
        <li class="diy-table-view-cell mui-col-xs-3">
            <a data-bet="/lottery/{{value.type}}/{{value.code}}/index.html" data-status="normal">
                <span class="lottery-ico lottery-ico-{{value.code}}"></span>
                <span class="lottery-title">{{value.name}}</span>
            </a>
        </li>
    {{/each}}
    <li class="diy-table-view-cell mui-col-xs-3">
        <a data-skip="/lottery/mainIndex.html">
            <span class="lottery-ico lottery-ico-more"></span>
            <span class="lottery-title">更多</span>
        </a>
    </li>
</script>
