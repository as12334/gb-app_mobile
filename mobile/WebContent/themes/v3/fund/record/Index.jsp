<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../include/include.inc.jsp" %>
<html>
<head>
    <meta charset="utf-8">
    <title>${views.fund_auto['资金记录']}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <%@ include file="/themes/v3/include/include.head.jsp" %>
    <link rel="bookmark" href="../../mobile-v3/favicon.ico">
    <link rel="shortcut icon" href="../../mobile-v3/favicon.ico">
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css" />
    <%--popover滚动需在页面自定义样式--%>
    <style>
        #transactionType {position: fixed;top: 16px;right: 6px;}
        #transactionType .mui-popover-arrow {left: auto;right: 6px;}
        .mui-popover {height: 270px;}
    </style>
</head>
<body class="mine-cash-record-body">
    <!-- 主页面标题 -->
    <header class="mui-bar mui-bar-nav mine-cash-record-header">
        <a class="mui-icon mui-icon-back mui-pull-left" data-rel='{"target":"goToLastPage","opType":"function"}'></a>
        <h1 class="mui-title">${views.fund_auto['资金记录']}</h1>
    </header>
    <div class="mui-content mine-cash-record-content">
        <!-- 主界面具体展示内容 -->
        <div class="mui-row">
            <div class="mui-input-group mine-form data-filter">
                <div class="gb-datafilter">
                    ${views.fund_auto['创建日期']}:
                    <span class="input-date" ><a href=""></a>
		                <input type="datetime" class="date"
                               value="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}"
                               id="beginTime" data-rel='{"target":"clickBeginTime","opType":"function"}' minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
		            </span>
                    ~
                    <span class="input-date"><a href=""></a>
		                <input type="datetime" class="date"
                               value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="endTime"
                               data-rel='{"target":"clickEndTime","opType":"function"}'
                               endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                               minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
		            </span>
                    <a href="#selectDiv" class="btn mui-btn mui-btn-primary btn-kx" style="width: 15%;">${views.fund_auto['快选']}</a>
                    <div class="clearfix"></div>
                </div>
            </div>
        </div>

        <div id="selectDiv" class="mui-popover scroll-popover" style="height: 180px" data-rel='{"target":"selectChoose","opType":"function"}'>
            <style>
                /*跨webview需要手动指定位置*/
                .scroll-popover {
                    position: fixed;
                    top: 92px;
                    right: 6px;
                }

                .scroll-popover .mui-popover-arrow {
                    left: auto;
                    right: 6px;
                }
            </style>

            <div class="mui-scroll-wrapper popover-scroll">
                <div class="mui-scroll">
                    <ul class="mui-table-view">
                        <li class="mui-table-view-cell"><a href="#" value="today" data-rel='{"target":"chooseValue","opType":"function"}'>${views.fund_auto['今天']}</a></li>
                        <li class="mui-table-view-cell"><a href="#" value="yesterday" data-rel='{"target":"chooseValue","opType":"function"}'>${views.fund_auto['昨天']}</a></li>
                        <li class="mui-table-view-cell"><a href="#" value="thisWeek" data-rel='{"target":"chooseValue","opType":"function"}'>${views.fund_auto['本周']}</a></li>
                        <%--<li class="mui-table-view-cell"><a href="#" value="lastWeek">${views.fund_auto['上周']}</a></li>--%>
                        <li class="mui-table-view-cell"><a href="#" value="7days" data-rel='{"target":"chooseValue","opType":"function"}'>${views.fund_auto['最近7天']}</a></li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="mui-row">
            <div class="mui-input-group mine-form">
                <div class="gb-datafilter cash-type-row">
                    <div class="mui-pull-right" style="width: 49%;">
                        <a href="javascript:" class="btn mui-btn mui-btn-primary query" data-rel='{"target":"searchObj","opType":"function"}' style="width: 100%;">${views.fund_auto['搜索']}</a>
                    </div>
                    <span class="input-drop" style="width: 49%;">
                        <a href="#transactionType">
                            <span id="displayType" value="${command.search.transactionType}">
                            <c:set var="type" value="${views.fund_auto['类型']}"></c:set>
                                ${not empty command.search.transactionType?dicts.common.transaction_type[command.search.transactionType]:type}</span>
                            <i class="mui-icon mui-icon-arrowdown"></i>
                        </a>
                    </span>
                </div>
            </div>
        </div>

        <div id="transactionType" class="mui-popover">
            <div class="mui-scroll-wrapper mui-assets" >
                <div class="mui-scroll">
                    <ul class="mui-table-view">
                        <li class="mui-table-view-cell"><a href="#" value="" data-rel='{"target":"chooseType","opType":"function"}'>${views.fund_auto['所有']}</a></li>
                        <c:forEach var="s" items="${command.dictCommonTransactionType}">
                            <li class="mui-table-view-cell">
                                <a href="#" value="${s.key}" data-rel='{"target":"chooseType","opType":"function"}'>${dicts.common.transaction_type[s.key]}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>

            </div>
        </div>


        <div class="mui-row">
            <div class="mui-input-group mine-form">
                <p class="qkclz">
		            <span class="mui-pull-right">
		                ${views.fund_auto['转账处理中']}：
		                <span class="text-green transferSum">${currency}${soulFn:formatCurrency(transferSum)}</span>
		            </span>
                    ${views.fund_auto['取款处理中']}：
                    <span class="text-green withdrawSum">${currency}${soulFn:formatCurrency(withdrawSum)}</span>
                </p>
            </div>
        </div>

        <div class="mine-table lg">
            <div class="mui-scroll-wrapper">
                <div class="mui-scroll" id="tBody">

                </div>
            </div>
        </div>


        <footer>
            <div class="flex">
                <span>合计：</span>
                <div class="c">
                    <div style="width: 50%;">充值总额：${sumPlayerMap.recharge}${soulFn:formatCurrency(sumPlayerMap.recharge)}</div>
                    <div style="width:50%">提现总额：${sumPlayerMap.withdraw}${soulFn:formatCurrency(sumPlayerMap.withdraw)}</div>
                    <div style="width: 50%;">优惠总额：${sumPlayerMap.favorable}${soulFn:formatCurrency(sumPlayerMap.favorable)}</div>
                    <div style="width:50%">返水总额：${sumPlayerMap.rakeback}${soulFn:formatCurrency(sumPlayerMap.rakeback)}</div>
                </div>
            </div>
        </footer>



    </div>


</body>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
<script type="text/javascript" src="${resRoot}/js/fund/record/Index.js"></script>

</html>
<%@ include file="/include/include.footer.jsp" %>
