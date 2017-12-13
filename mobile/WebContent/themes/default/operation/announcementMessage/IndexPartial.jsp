<%--@elvariable id="command" type="org.soul.model.msg.notice.vo.VNoticeReceivedTextVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<div class="chart-table info-notice">
    <a style="display: none" class="returnMain" href="/operation/pAnnouncementMessage/messageList.html?paging.pageNumber=${command.paging.pageNumber}" nav-target="mainFrame"></a>
    <table border="0" cellpadding="0" cellspacing="0" class="table dataTable">
        <thead>
            <tr>
                <td colspan="4" class="none-p">
                    <div class="pagination-total">
                        <div class="alldelete">
                            <input type="checkbox" value="" name=""/>${views.common_report['全选']}
                            <span class="function-menu-show show">
                            <soul:button target="deleteMessage" text="${views.operation_auto['删除']}" opType="function" cssClass="btn btn-outline btn-filter"/>
                            <soul:button target="getSelectSystemMessageIds" text="${views.operation_auto['标记已读']}" opType="function" cssClass="btn btn-outline btn-filter"/>
                            </span>
                        </div>
                    </div>
                </td>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${fn:length(command.result) gt 0}">
                    <c:forEach items="${command.result}" var="s">
                        <tr class="${s.receiveStatus=='12'?"":"on"}">
                            <td width="12px"><input type="checkbox" value="${s.id}" class="i-checks"></td>
                            <td>
                                <a href="/operation/pAnnouncementMessage/announcementDetail.html?searchId=${command.getSearchId(s.id)}"
                                   nav-target="mainFrame" title="${s.title}"> ${fn:substring(s.title,0,50)}<c:if test="${fn:length(s.title)>50}">...</c:if></a>
                            </td>
                            <td><span class="datemessage"><i
                                    class="clock"></i>${soulFn:formatDateTz(s.receiveTime, DateFormat.DAY_SECOND,timeZone)}</span>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td class="no-content_wrap">
                            <div>
                                <i class="fa fa-exclamation-circle"></i> ${views.operation_auto['暂无内容!']}
                            </div>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>

            </tbody>
    </table>
</div>

<soul:pagination/>


