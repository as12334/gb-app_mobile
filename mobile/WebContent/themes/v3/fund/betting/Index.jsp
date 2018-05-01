<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <title>${views.fund_auto['投注记录']}</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css" />
</head>

<body class="mine-bet-record-body">
<!-- 主页面标题 -->
<header class="mui-bar mui-bar-nav mine-bet-record-header">
    <a class="mui-icon  mui-icon-back mui-pull-left mui-action-back"></a>
    <h1 class="mui-title">${views.fund_auto['投注记录']}</h1>
</header>
<div class="mui-content mine-bet-record-content mui-scroll-wrapper" id="refreshContainer">
    <div class="mui-scroll">
        <!-- 主界面具体展示内容 -->
        <div class="mui-row">
            <div class="mui-input-group mine-form data-filter">
                <div class="gb-datafilter">
                    ${views.fund_auto['投注日期']}:
                    <span class="input-date" ><a href=""></a>
		                <input type="datetime" class="date" data-rel='{"target":"loadBeginTime","opType":"function"}'
                               value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                               id="beginTime" minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
		            </span>
                    ~
                    <span class="input-date"><a href=""></a>
		                <input type="datetime" class="date" data-rel='{"target":"loadEndTime","opType":"function"}'
                               value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="endTime"
                               endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                               minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
		            </span>
                    <a data-rel='{"target":"loadData","opType":"function"}' class="btn mui-btn mui-btn-primary btn-ss" style="width: 15%;">${views.fund_auto['搜索']}</a>
                    <div class="clearfix"></div>
                </div>
            </div>
        </div>
        <div class="mine-table">
            <table>
                <tbody><tr class="head">
                    <td>${views.fund_auto['游戏名称']}<br><span class="text-blue" id="totalCount"></span></td>
                    <td>${views.fund_auto['投注时间']}</td>
                    <td>${views.fund_auto['投注额']}<br><span class="text-blue" id="statisticalDataSingle"><%--${siteCurrencySign}${soulFn:formatCurrency(0)}--%></span></td>
                    <td>${views.fund_auto['派彩']}<br><span class="text-blue" id="statisticalDataProfit"><%--${siteCurrencySign}${soulFn:formatCurrency(0)}--%></span></td>
                    <td>${views.fund_auto['状态']}</td>
                </tr>
                </tbody>
            <tbody id="content-list">
            <%@ include file="./IndexPartialList.jsp"%></tbody>
            </tbody>
            </table>
        </div>
    </div>
</div>  <!--mui-content 闭合标签-->
<footer>
    <div class="flex">
        <span>${views.fund_auto['合计']}：</span>
        <div class="c">
            <div style="width: 50%;" id="statisticalTotalAmount">${views.fund_auto['投注总额']}：${siteCurrencySign}${soulFn:formatCurrency(0)}</div>
            <div style="width:50%" id="statisticalProfit">${views.fund_auto['派彩']}：${siteCurrencySign}${soulFn:formatCurrency(0)}</div>
            <div style="width: 50%;" id="statisticalDataEffective">${views.fund_auto['有效投注额']}：${siteCurrencySign}${soulFn:formatCurrency(0)}</div>
            <div style="width:50%" id="statisticalTotalPage">${views.fund_auto['投注笔数']}：0${views.fund_auto['笔']}</div>
        </div>
    </div>
</footer>
</body>
<%@include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.min.js"></script>
<script src="${resComRoot}/js/timeControls/moment.js"></script>
<script type="text/javascript" src="${resRoot}/js/fund/betting/Index.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>