<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<form:form action="${root}/operation/pAnnouncementMessage/gameNotice.html" method="post">
        <div class="notice">
            <div class="notice-left"><em class="path"></em></div>
            <div class="path-right"><a class="cursor">${views.sysResource['消息公告']}</a><span class="arrow">></span>${views.operation_auto['游戏公告']}</div>
        </div>

        <div class="tabmenu">
            <ul class="tab">
                <li><a href="/operation/pAnnouncementMessage/gameNotice.html" class="current gameAnn" nav-target="mainFrame">${views.operation_auto['游戏公告']}</a></li>
                <li><a href="/operation/pAnnouncementMessage/systemNoticeHistory.html" class="systemAnn" nav-target="mainFrame">${views.operation_auto['系统公告']}</a></li>
                <li><a href="/operation/pAnnouncementMessage/messageList.html" class="siteMessage" nav-target="mainFrame">${views.operation_auto['站点消息']}</a></li>
            </ul>
        </div>
        <div class="rgeechar">
            <div class="history btnalign">
                 <gb:dateRange format="${DateFormat.DAY}" style="width:155px" inputStyle="width:135px" useRange="true" useToday="true"
                               startName="search.startTime" endName="search.endTime" maxDate="${maxDate}"
                               startDate="${command.search.startTime}" endDate="${command.search.endTime}"></gb:dateRange>

                <div style="display: inline-block;">
                    <span>
                        <select name="search.apiId" data-placeholder="${views.operation_auto['请选择']}">
                            <option value="">${views.operation_auto['所有游戏']}</option>
                            <c:forEach items="${apiMap}" var="s">
                                <option value="${s.value.apiId}">${s.value.name}</option>
                            </c:forEach>
                        </select>
                    </span>

                    <soul:button text="" target="query" opType="function" cssClass="btn btn-blue btn-big"
                                 tag="button">
                        <i class="fa fa-search"></i>
                        <span class="hd">&nbsp;${views.operation_antu['搜索']}</span>
                    </soul:button>

                </div>
            </div>
        </div>
        <!--表格内容-->

        <div class="search-list-container">
            <%@include file="GameNoticePartial.jsp" %>
        </div>
</form:form>

<soul:import res="site/operation/message/announcementMessage"/>