<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dawoo</title>
    <%@ include file="../../include/include.head.jsp" %>
    <link rel="stylesheet" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" href="${resRoot}/themes/mui.dtpicker.css"/>
</head>

<body class="mine-notice">
<header class="mui-bar mui-bar-nav">
    <a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
    <h1 class="mui-title">消息</h1>
    <a value="${unReadType}" id="unReadType" hidden></a>
</header>
<div class="mui-content mui-scroll-wrapper" id="refreshContainer">
    <div class="mui-scroll">

        <div class="mui-row">
            <div class="gb-panel" style="background-color: #f2f2f2;">
                <div class="gb-headtabs span3">
                    <div id="segmentedControl" class="mui-segmented-control">
                        <a data-rel='{"target":"segmentedControl1","opType":"function"}'
                           data-href="noticeGame" id="gameNotice"
                           class="mui-control-item ${empty unReadType?'mui-active':''}">${views.mine_auto['游戏公告']}<i></i></a>
                        <a data-rel='{"target":"segmentedControl1","opType":"function"}'
                           data-href="noticeSys" id="sysNotice"
                           class="mui-control-item ${unReadType eq 'noticeSys' ? 'mui-active':''}">${views.mine_auto['系统公告']}<i></i></a>
                        <a data-rel='{"target":"segmentedControl1","opType":"function"}'
                           data-href="noticeSite" id="siteNotice"
                           class="mui-control-item ${unReadType eq 'sendMessage' ?'mui-active':''}">${views.mine_auto['站点消息']}<i></i></a>
                    </div>
                </div>
            </div>
        </div>
        <div id="noticeGame" class="mui-control-content ${empty unReadType?'mui-active':''}" name="notice">
            <div class="mui-row">
                <div class="mui-input-group mine-form">
                    <div class="gb-datafilter">
                        <div class="mui-pull-right" style="width: 30%; margin-bottom: 0;">
                                    <span class="input-drop">
                                        <a href="#popover">
                                            <span id="displayGameType">${views.mine_auto['游戏类型']}</span>
                                            <i class="mui-icon mui-icon-arrowdown"></i>
                                        </a>
                                    </span>
                        </div>
                        <a href="#selectDate"
                           class="btn mui-btn btn-sec mui-pull-right">${views.mine_auto['快选']}</a>
                        <span class="input-date"><a href=""></a>
                                    <input type="datetime" class="date"
                                           data-rel='{"target":"setBeginTime","opType":"function"}'
                                           value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                                           name="beginTime"
                                           minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
                                </span>
                        ~
                        <span class="input-date"><a href=""></a>
                                    <input type="datetime" class="date"
                                           data-rel='{"target":"setEndTime","opType":"function"}'
                                           value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                                           name="endTime"
                                           endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}">
                                </span>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
            <%--消息展示--%>
            <div class="mui-row">
                <div class="notice-list">
                    <ul id="noticeGamePartial">
                        <%@include file="NoticeGamePartial.jsp" %>
                    </ul>
                </div>
            </div>
        </div>
        <div id="noticeSys" class="mui-control-content ${unReadType eq 'noticeSys' ?'mui-active':''}" name="notice">
            <div class="mui-row">
                <div class="mui-input-group mine-form">
                    <div class="gb-datafilter">
                    <span class="input-date"><a href=""></a>
                        <input type="datetime" data-rel='{"target":"setBeginTime","opType":"function"}'
                               class="date" minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}"
                               name="beginTime" value="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
                    </span>
                        ~
                        <span class="input-date"><a href=""></a>
                        <input type="datetime" class="date" data-rel='{"target":"setEndTime","opType":"function"}'
                               endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}"
                               name="endTime" value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}">
                    </span>
                        &nbsp;&nbsp;
                        <a href="#selectDate" class="btn mui-btn mui-btn-primary">${views.mine_auto['快选']}</a>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
            <div class="mui-row">
                <div class="notice-list">
                    <ul id="noticeSysPartial">
                    </ul>
                </div>
            </div>
        </div>
        <div id="noticeSite" class="mui-control-content ${empty unReadType?'':'mui-active'}" name="notice">
            <%@include file="./NoticeSite.jsp" %>
        </div>
    </div>
</div>
</div>

<div id="popover" class="mui-popover" style="height: 50%;">
    <div class="mui-scroll-wrapper popover-scroll">
        <div class="mui-scroll">
            <ul class="mui-table-view">
                <li class="mui-table-view-cell">
                    <a href="#" data-rel='{"target":"selectGameType","opType":"function"}'
                       value="">${views.mine_auto['所有游戏']}</a>
                </li>
                <c:forEach items="${apiMap}" var="s">
                    <li class="mui-table-view-cell">
                        <a href="#" data-rel='{"target":"selectGameType","opType":"function"}'
                           value="${s.value.apiId}">${gbFn:getApiName(s.value.apiId)}</a>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
</div>
</body>
<%--类型--%>
<div id="advisoryType" class="mui-popover" style="width: 98px;">
    <ul class="mui-table-view">
        <c:forEach items="${advisoryType}" var="t">
            <li class="mui-table-view-cell"><a data-rel='{"target":"advisoryType","opType":"function"}'
                                               value="${t.key}">${views.mine_auto[t.value['remark']]}</a></li>
        </c:forEach>
    </ul>
</div>
<%--快选弹出菜单--%>
<div id="selectDate" class="mui-popover scroll-popover" style="height: 300px">
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
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="today">${views.include_auto['今天']}</a></li>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="yesterday">${views.include_auto['昨天']}</a></li>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="thisWeek">${views.include_auto['本周']}</a></li>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="lastWeek">${views.include_auto['上周']}</a></li>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="thisMonth">${views.include_auto['本月']}</a></li>
                <%--<li class="mui-table-view-cell"><a href="#" value="lastMonth">${views.include_auto['上月']}</a></li>--%>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="7days">${views.include_auto['最近7天']}</a></li>
                <li class="mui-table-view-cell"><a data-rel='{"target":"dateFastSelection","opType":"function"}'
                                                   href="#" value="30days">${views.include_auto['最近30天']}</a></li>
            </ul>
        </div>
    </div>
</div>
<%@ include file="../../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.picker.min.js"></script>
<script src="${resComRoot}/js/timeControls/moment.js"></script>
<script type="text/javascript" src="${resRoot}/js/message/Notice.js"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>
