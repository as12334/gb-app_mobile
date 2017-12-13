<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="wrap">
    <form>
    <div class="notice">
        <div class="notice-left"><em class="path"></em></div>
        <div class="path-right"><a class="cursor">${views.sysResource['消息公告']}</a><span class="arrow">></span>${views.operation_auto['站点消息']}</div>
    </div>
    <div class="return">
        <a href="/operation/pAnnouncementMessage/messageList.html" class="btn btn-gray btn-big" nav-target="mainFrame">${views.operation_auto['返回上一级']}</a>
    </div>
    <!--选项卡-->
    <div class="tabmenu">
        <ul class="tab">
            <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
            <li><a href="/operation/pAnnouncementMessage/systemNoticeHistory.html" class="systemAnn" nav-target="mainFrame">${views.operation_auto['系统公告']}</a></li>
            <li><a href="javascript:void(0)" class="current siteMessage">${views.operation_auto['站点消息']}</a></li>
        </ul>
    </div>
    <div class="tabmenu tabmenu-2">
        <ul class="tab-2">
            <li><a href="/operation/pAnnouncementMessage/messageList.html" class="current active" nav-target="mainFrame">${views.operation_auto['系统消息']}
                <c:if test="${length gt 0}">
                    <i class="ci-count">${length}</i>
                </c:if>
            </a>
            </li>
            <li><a href="/operation/pAnnouncementMessage/advisoryMessage.html" class="current" nav-target="mainFrame">${views.operation_auto['我的消息']}
                <c:if test="${advisoryUnReadCount gt 0}">
                    <i class="ci-count menutwo">${advisoryUnReadCount}</i>
                </c:if>
            </a></li>
            <li><a href="/operation/pAnnouncementMessage/beforeSendMessage.html" class="current no-badge" nav-target="mainFrame">${views.operation_auto['发送消息']}</a></li>
        </ul>
    </div>

    <div class="salemore">
        <h2><span class="name">${command.result.title}</span><br/>
            <div class="datetime"><i class="clock"></i>${soulFn:formatDateTz(command.result.receiveTime, DateFormat.DAY_SECOND,timeZone)}</div></h2>
        <div class="salemore-con">
            <pre style="white-space: pre-wrap;word-wrap: break-word;border: 0px;">${command.result.content}</pre>
        </div>
    </div>
    </form>
</div>
<soul:import res="site/operation/message/MessageDetail"/>

