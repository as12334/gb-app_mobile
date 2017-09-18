<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>
<head>
    <title>${views.fund_auto['资金记录']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>
    <script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css"/>
    <%--popover滚动需在页面自定义样式--%>
    <style>
        #transactionType {position: fixed;top: 16px;right: 6px;}
        #transactionType .mui-popover-arrow {left: auto;right: 6px;}
        .mui-popover {height: 300px;}
    </style>
</head>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <a class="mui-action-backs mui-icon mui-icon-left-nav mui-pull-left"></a>
            <h1 class="mui-title">${views.fund_auto['资金记录']}</h1>
            <%@ include file="/themes/default/include/include.asset.jsp" %>
        </header>
        <div class="mui-content mui-scroll-wrapper" id="refreshContainer" ${os eq 'android'?'style="padding-top:0"':''}>
            <div class="mui-scroll">

                <%@ include file="/themes/common/fund/record/record.index.jsp" %>
                <div class="mui-row">
                        <div class="mui-input-group mine-form m-t-sm">
                        <div class="mine-table lg">
                            <table id="content">
                                <%--<%@ include file="./IndexPartial.jsp" %>--%>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
<div id="selectDate" class="mui-popover scroll-popover" style="height: 220px">
    <style>
        /*跨webview需要手动指定位置*/
        .scroll-popover {
            position: fixed;
            top: 16px;
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
                <li class="mui-table-view-cell"><a href="#" value="today">${views.fund_auto['今天']}</a></li>
                <li class="mui-table-view-cell"><a href="#" value="yesterday">${views.fund_auto['昨天']}</a></li>
                <li class="mui-table-view-cell"><a href="#" value="thisWeek">${views.fund_auto['本周']}</a></li>
                <li class="mui-table-view-cell"><a href="#" value="lastWeek">${views.fund_auto['上周']}</a></li>
                <li class="mui-table-view-cell"><a href="#" value="7days">${views.fund_auto['最近7天']}</a></li>
            </ul>
        </div>
    </div>
</div>
<div id="transactionType" class="mui-popover">
    <div class="mui-scroll-wrapper fund-type-scroll" >
        <div class="mui-scroll">
            <ul class="mui-table-view">
                <li class="mui-table-view-cell"><a href="#" value="">${views.fund_auto['所有']}</a></li>
                <c:forEach var="s" items="${command.dictCommonTransactionType}">
                    <li class="mui-table-view-cell">
                        <a href="#" value="${s.key}">${dicts.common.transaction_type[s.key]}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <script language="JavaScript">
            mui.init({
                swipeBack: true //启用右滑关闭功能
            });
            mui('.fund-type-scroll').scroll();
            mui('body').on('shown', '.mui-popover', function(e) {
                //console.log('shown', e.detail.id);//detail为当前popover元素
            });
            mui('body').on('hidden', '.mui-popover', function(e) {
                //console.log('hidden', e.detail.id);//detail为当前popover元素
            });
        </script>
    </div>
</div>
</body>

<script>
    curl(['common/MobileBasePage', 'site/common/Assets','site/fund/record/Index',],
        function(Page, Assets,FundRecord) {
            page = new Page();
            asset = new Assets();
            fundRecord = new FundRecord();
        });
</script>

</html>
