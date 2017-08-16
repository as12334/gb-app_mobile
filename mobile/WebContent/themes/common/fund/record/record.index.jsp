<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="mui-row">
    <div class="mui-input-group mine-form">
        <div class="gb-datafilter">
            ${views.fund_auto['创建日期']}:
            <span class="input-date" ><a href=""></a>
                <input type="datetime" class="date"
                       value="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}"
                       id="beginTime" minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
            </span>
            ~
            <span class="input-date"><a href=""></a>
                <input type="datetime" class="date"
                       value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="endTime"
                       endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                       minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
            </span>
            <a href="#selectDate" class="btn mui-btn mui-btn-primary" style="width: 15%;">${views.fund_auto['快选']}</a>
            <div class="clearfix"></div>
        </div>

    </div>
</div>
<div class="mui-row">
    <div class="mui-input-group mine-form">
        <div class="gb-datafilter">
            <div class="mui-pull-right" style="width: 45%;">
                <a href="javascript:" class="btn mui-btn mui-btn-primary query" style="width: 100%;">${views.fund_auto['搜索']}</a>
            </div>
            <span class="input-drop" style="width: 45%;">
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
<div class="mui-row">
    <div class="mui-input-group mine-form">
        <p style="padding:10px">
            <span class="mui-pull-right">
                ${views.fund_auto['转账处理中']}：
                <span class="text-green transferSum">${currency}${soulFn:formatCurrency(transferSum)}</span>
            </span>
            ${views.fund_auto['取款处理中']}：
            <span class="text-green withdrawSum">${currency}${soulFn:formatCurrency(withdrawSum)}</span>
        </p>
    </div>
</div>
