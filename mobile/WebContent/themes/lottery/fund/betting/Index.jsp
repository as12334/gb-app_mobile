<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${views.fund_auto['投注记录']}</title>
    <%@ include file="/themes/lottery/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css" />
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css" />
</head>

<body class="gb-theme mine-page">
    <div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
        <!-- 主页面容器 -->
        <div class="mui-inner-wrap">
            <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}" >
                <%@ include file="/include/include.toolbar.jsp" %>
                <h1 class="mui-title">${views.fund_auto['投注记录']}</h1>
                <a class="mui-icon mui-icon-home mui-pull-right" data-href="/mainIndex.html"></a>
            </header>
            <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0!important"':''} >
                <div class="mui-scroll">
                    <div class="mui-row">
                        <div class="mui-input-group mine-form">
                            <div class="gb-datafilter">
                                <div class="mui-pull-right" style="width: 13%;">
                                    <a href="javascript:" class="btn mui-btn mui-btn-primary query">${views.fund_auto['搜索']}</a>
                                </div>
                                ${views.fund_auto['投注日期']}：
                                <span class="input-date"><a href=""></a>
                                    <input type="datetime" class="date" value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="beginTime" beginTime="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
                                </span>
                                ~
                                <span class="input-date"><a href=""></a>
                                    <input type="datetime" class="date" value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="endTime" endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}">
                                </span>
                                <div class="clearfix"></div>
                            </div>
                        </div>
                    </div>
                    <div class="mui-row">
                        <div class="mui-input-group mine-form m-t-sm" id="content">
                            <p style="padding:10px"><span class="mui-pull-right">${views.fund_auto['彩池奖金']}：<span class="text-green" id="statisticalDataWinning">${siteCurrencySign}${soulFn:formatCurrency(0)}</span></span>
                                ${views.fund_auto['有效投注额']}：<span class="text-green" id="statisticalDataEffective">${siteCurrencySign}${soulFn:formatCurrency(0)}</span>
                            </p>
                            <div class="mine-table">
                                <table>
                                    <tr class="head">
                                        <td>${views.fund_auto['游戏名称']}<br><span class="text-blue" id="totalCount"></span></td>
                                        <td>${views.fund_auto['投注时间']}</td>
                                        <td>${views.fund_auto['投注额']}<br><span class="text-blue" id="statisticalDataSingle">${siteCurrencySign}${soulFn:formatCurrency(0)}</span></td>
                                        <td>${views.fund_auto['派彩']}<br><span class="text-blue" id="statisticalDataProfit">${siteCurrencySign}${soulFn:formatCurrency(0)}</span></td>
                                        <td>${views.fund_auto['状态']}</td>
                                    </tr>
                                    <tbody id="content-list">
                                    <%@ include file="IndexPartialList.jsp"%></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="mui-off-canvas-backdrop"></div>
        </div>
    </div>
</body>
</html>
<%--<script src="${resRoot}/js/fund/betting/Index.js?v=${rcVersion}"></script>--%>
<soul:import res="site/fund/betting/Index"/>
<%@ include file="/include/include.footer.jsp" %>