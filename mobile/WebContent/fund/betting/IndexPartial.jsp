<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<p style="padding:10px"><span class="mui-pull-right">${views.fund_auto['彩池奖金']}：<span class="text-green">${siteCurrencySign}${soulFn:formatCurrency(statisticalData.winning)}</span></span>
    ${views.fund_auto['有效投注额']}：<span class="text-green">${siteCurrencySign}${soulFn:formatCurrency(statisticalData.effective)}</span>
</p>
<div class="mine-table">
    <table id="content-list">
        <tr class="head">
            <td>${views.fund_auto['游戏名称']}<br><span class="text-blue">${views.fund_auto['总共笔数'].replace('{0}', command.paging.totalCount)}</span></td>
            <td>${views.fund_auto['投注时间']}</td>
            <td>${views.fund_auto['投注额']}<br><span class="text-blue">${siteCurrencySign}${soulFn:formatCurrency(statisticalData.single)}</span></td>
            <td>${views.fund_auto['派彩']}<br><span class="text-blue">${siteCurrencySign}${soulFn:formatCurrency(statisticalData.profit)}</span></td>
            <td>${views.fund_auto['状态']}</td>
        </tr>
        <%@ include file="IndexPartialList.jsp"%>
    </table>
</div>