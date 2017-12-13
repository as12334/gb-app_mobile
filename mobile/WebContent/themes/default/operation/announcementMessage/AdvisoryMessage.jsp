<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.VPlayerAdvisoryVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

    <form method="post" action="${root}/operation/pAnnouncementMessage/advisoryMessage.html">
            <div class="notice">
                <div class="notice-left"><em class="path"></em></div>
                <div class="path-right"><a class="cursor">${views.sysResource['消息公告']}</a><span class="arrow">></span>${views.operation_auto['站点消息']}</div>
            </div>

            <!--选项卡-->
            <div class="tabmenu">
                <ul class="tab">
                    <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
                    <li><a href="/operation/pAnnouncementMessage/systemNoticeHistory.html" class="systemAnn" nav-target="mainFrame">${views.operation_auto['系统公告']}</a></li>
                    <li><a href="javascript:void(0)" class="current siteMessage" >${views.operation_auto['站点消息']}</a></li>
                </ul>
            </div>
            <div class="tabmenu tabmenu-2">
                <ul class="tab-2">
                    <a class="returnMain" style="display: none" href="/operation/pAnnouncementMessage/advisoryMessage.html" nav-target="mainFrame"></a>
                    <li>
                        <a href="/operation/pAnnouncementMessage/messageList.html" class="current" nav-target="mainFrame">${views.operation_auto['系统消息']}
                            <c:if test="${length gt 0}">
                                <i class="ci-count">${length}</i>
                            </c:if>
                        </a>
                    </li>
                    <li>
                        <a href="/operation/pAnnouncementMessage/advisoryMessage.html" class="current active" nav-target="mainFrame">${views.operation_auto['我的消息']}
                            <c:if test="${advisoryUnReadCount gt 0}">
                                <i class="ci-count menutwo">${advisoryUnReadCount}</i>
                            </c:if>
                        </a>
                    </li>
                    <li><a href="/operation/pAnnouncementMessage/beforeSendMessage.html" class="current" nav-target="mainFrame">${views.operation_auto['发送消息']}</a></li>
                </ul>
            </div>

                <!--表格内容-->
                <div class="search-list-container">
                    <%@include file="AdvisoryMessagePartial.jsp"%>
                </div>
    </form>
<soul:import res="site/operation/message/announcementMessage"/>


