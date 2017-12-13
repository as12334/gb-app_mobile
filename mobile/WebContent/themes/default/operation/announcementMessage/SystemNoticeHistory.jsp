<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<form:form action="${root}/operation/pAnnouncementMessage/systemNoticeHistory.html" method="post">
        <div class="notice">
            <div class="notice-left"><em class="path"></em></div>
            <div class="path-right"><a class="cursor">${views.sysResource['消息公告']}</a><span class="arrow">></span>${views.operation_auto['系统公告']}</div>
            <a class="returnMain" style="display: none" href="/operation/pAnnouncementMessage/messageList.html" nav-target="mainFrame"></a>
        </div>

        <!--选项卡-->
        <div class="tabmenu">
            <ul class="tab">
                <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
                <li><a href="javascript:void(0)" class="current systemAnn">${views.operation_auto['系统公告']}</a></li>
                <li><a href="/operation/pAnnouncementMessage/messageList.html" class="siteMessage" nav-target="mainFrame">${views.operation_auto['站点消息']}</a></li>
            </ul>
        </div>

        <div class="rgeechar">
            <div class="history btnalign">
                <gb:dateRange format="${DateFormat.DAY_SECOND}" style="width:155px" inputStyle="width:135px" useRange="true" useToday="true" callback="query"
                              startName="search.startTime" endName="search.endTime" maxDate="${maxDate}"
                              startDate="${command.search.startTime}" endDate="${command.search.endTime}"></gb:dateRange>
            </div>
        </div>
        <!--表格内容-->
        <div class="search-list-container">
            <%@include file="SystemNoticeHistoryPartial.jsp"%>
        </div>
</form:form>

<soul:import res="site/operation/message/SystemNoticeHistory"/>
