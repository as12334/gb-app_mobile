<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<html>

<head>
    <title>${views.mine_auto['消息']}</title>
    <%@ include file="/themes/default/include/include.head.jsp" %>
    <%@ include file="/include/include.js.jsp" %>

    <script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
    <script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css"/>
    <script src="${resRoot}/js/common/pullRefresh.js?v=${rcVersion}"></script>

</head>

<body class="gb-theme mine-page">
<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-draggable">
    <!-- 主页面容器 -->
    <div class="mui-inner-wrap">
        <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
            <%@ include file="/include/include.toolbar.jsp" %>
            <h1 class="mui-title">${views.mine_auto['消息']}</h1>
            <a value="${unReadType}" id="unReadType" hidden></a>
        </header>
        <div class="mui-content bg-white" ${os eq 'android'?'style="padding-top:0!important"':''}>
            <div class="mui-row">
                <div class="gb-panel b-t-0" style="background-color: #f2f2f2;z-index: 1111;position:absolute;width: 100%">
                    <div class="gb-headtabs span3">
                        <div id="segmentedControl1" class="mui-segmented-control">
                            <a data-href="noticeGame" class="mui-control-item ${empty unReadType?'mui-active':''}" id="gameNotice" >${views.mine_auto['游戏公告']}<i></i></a>
                            <a data-href="noticeSys" class="mui-control-item ${unReadType eq 'noticeSys' ? 'mui-active':''}" id="sysNotice" >${views.mine_auto['系统公告']}<i></i></a>
                            <a data-href="noticeSite" class="mui-control-item ${unReadType eq 'sendMessage' ?'mui-active':''}" id="siteNotice">${views.mine_auto['站点消息']}<i></i>
                                ${sysMessageUnReadCount!=0||advisoryUnReadCount!=0?'<span class="unread-count-icon"></span>':''}</a>
                        </div>
                    </div>
                </div>
            </div>
            <input type="hidden" id="hidden_input">
            <div id="noticeGame" class="mui-control-content ${empty unReadType?'mui-active':''}" name="notice" >
                <div class="mui-scroll-wrapper" style="position: static;height:91%;margin-top: 52px;" id="noticeGameScroll" >
                    <div class="mui-scroll" >
                        <div class="mui-row">
                            <div class="mui-input-group mine-form mui-notice">
                                <div class="gb-datafilter" >
                                    <div class="mui-pull-right" style="width: 24%;">
                                        <span class="input-drop">
                                            <a href="#popover">
                                                <div class="game-type" style="float: left;">
                                                <span id="displayGameType">${views.mine_auto['游戏类型']}</span>
                                                    </div>
                                                    <i class="mui-icon mui-icon-arrowdown" style="font-size: 23px"></i>
                                            </a>
                                        </span>
                                    </div>
                                    <a href="#selectDate" class="btn mui-btn mui-btn-primary mui-pull-right" style="width: 15%;margin-right:1px;">${views.mine_auto['快选']}</a>
                                    <span class="input-date" style="width: 28%;"><a href=""></a>
                                        <input type="datetime" class="date" value="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}"
                                               id="gameBeginTime" minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
                                    </span>
                                    ~
                                    <span class="input-date" style="width: 28%;"><a href=""></a>
                                        <input type="datetime" class="date"
                                               value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="gameEndTime"
                                               endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}">
                                    </span>
                                    <div class="clearfix"></div>
                                </div>
                            </div>
                        </div>

                        <div class="mui-row">
                            <div class="notice-list">
                                <ul id="noticeGamePartial">
                                    <%@include file="./NoticeGamePartial.jsp" %>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div id="noticeSys" class="mui-control-content ${unReadType eq 'noticeSys' ?'mui-active':''}" name="notice" >
                <div class="mui-scroll-wrapper" style="position: static;height:91%;margin-top: 52px;" id="noticeSysScroll">
                    <div class="mui-scroll">
                        <div class="mui-row">
                            <div class="mui-input-group mine-form mui-notice">
                                <div class="gb-datafilter">
                                    <span class="input-date"><a href=""></a>
                                        <input type="datetime" class="date" value="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}"
                                               id="sysBeginTime" minDate="${soulFn:formatDateTz(minDate, DateFormat.DAY,timeZone)}">
                                    </span>
                                    ~
                                    <span class="input-date"><a href=""></a>
                                        <input type="datetime" class="date" value="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}" id="sysEndTime"
                                               endTime="${soulFn:formatDateTz(maxDate, DateFormat.DAY,timeZone)}">
                                    </span>
                                    &nbsp;&nbsp;
                                    <a href="#selectDate" class="btn mui-btn mui-btn-primary" style="width: 15%;">${views.mine_auto['快选']}</a>
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
                </div>
            </div>
            <div id="noticeSite" class="mui-control-content ${empty unReadType?'':'mui-active'}" name="notice">
                <%@include file="./NoticeSite.jsp"%>
            </div>

        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</div>
</body>
<div id="popover" class="mui-popover" style="height: 50%;">
    <div class="mui-scroll-wrapper popover-scroll" >
        <div class="mui-scroll">
    <ul class="mui-table-view" >
        <li class="mui-table-view-cell">
            <a href="#"  value="">${views.mine_auto['所有游戏']}</a>
        </li>
        <c:forEach items="${apiMap}" var="s">
            <li class="mui-table-view-cell">
                <a href="#" value="${s.value.apiId}">${gbFn:getApiName(s.value.apiId)}</a>
            </li>
        </c:forEach>
    </ul>
            </div>
        </div>
</div>
<div id="advisoryType" class="mui-popover" style="width: 98px;">
    <ul class="mui-table-view" >
        <c:forEach items="${advisoryType}" var="t">
            <li class="mui-table-view-cell"><a href="#" value="${t.key}">${views.mine_auto[t.value['remark']]}</a></li>
        </c:forEach>
    </ul>
</div>
<div id="selectDate" class="mui-popover scroll-popover" style="height: 300px">
    <%@include file="/include/include.popover.jsp"%>
</div>
<script language="JavaScript">
    mui.init({
        swipeBack: true //启用右滑关闭功能
    });
    mui('.popover-scroll').scroll();
    mui('body').on('shown', '.mui-popover', function(e) {
        //console.log('shown', e.detail.id);//detail为当前popover元素
    });
    mui('body').on('hidden', '.mui-popover', function(e) {
        //console.log('hidden', e.detail.id);//detail为当前popover元素
    });
</script>
<%--<script src="${resRoot}/js/my/message/Notice.js?v=${rcVersion}"></script>--%>
<soul:import res="site/my/message/Notice"/>
</html>
<%@ include file="/include/include.footer.jsp" %>