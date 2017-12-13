<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.VPlayerAdvisoryVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<!--咨询-->

<div class="chart-table">
    <table border="0" cellpadding="0" cellspacing="0" class="table table-striped table-hover dataTable">
        <thead>
        <tr>
            <td colspan="7" class="none-p">
                <div class="pagination-total">
                    <div class="alldelete">
                        <input type="checkbox" class="i-checks"/>${views.common_report['全选']}
                        <span class="function-menu-show show">
                        <soul:button target="deleteAdvisoryMessage" text="${views.operation_auto['删除']}" opType="function" cssClass="btn btn-outline btn-filter"/>
                        <soul:button target="getSelectAdvisoryMessageIds" text="${views.operation_auto['标记已读']}" opType="function" cssClass="btn btn-outline btn-filter"/>
                        </span>
                    </div>
                </div>
            </td>
        </tr>
        <tr>
            <th width="6%"><input name="" type="checkbox" value=""></th>
            <th width="11%" align="left" style="padding-left: 0">${views.operation_auto['类型']}</th>
            <th width="16%">${views.operation_auto['提问标题']}</th>
            <th width="20%">${views.operation_auto['提交/追问时间']}</th>
            <th width="21%">${views.operation_auto['回复标题']}</th>
            <th width="19%">${views.operation_auto['最后回复时间']}</th>
            <th width="7%">${views.operation_auto['操作']}</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${command.result}" var="s">
            <c:if test="${s.questionType=='1'}">
                <tr class="${s.isRead==false?'on':''}">
                    <td><input type="checkbox" value="${s.id}" class="i-checks"></td>
                    <td align="left" style="padding-left: 0">${dicts.player.advisory_type[s.advisoryType]}</td>
                    <td title='<c:out value="${s.advisoryTitle}" escapeXml="true" />'>
                        ${s.continueQuizCount>0?views.operation_auto['【追问】']:''}
                            <c:out value="${fn:substring(s.advisoryTitle, 0, 30)}" escapeXml="true" />
                        <c:if test="${fn:length(s.advisoryTitle)>30}">...</c:if>
                    </td>
                    <td>${soulFn:formatDateTz(s.advisoryTime, DateFormat.DAY_SECOND,timeZone)}</td>
                    <td title='<c:out value="${s.advisoryTitle}" escapeXml="true" />'>
                        <c:out value="${fn:substring(s.advisoryTitle, 0, 30)}" escapeXml="true" />
                        <c:if test="${fn:length(s.replyTitle)>30}">...</c:if>
                    </td>
                    <td>${soulFn:formatDateTz(s.replyTime, DateFormat.DAY_SECOND,timeZone)}</td>
                    <td>
                        <a style="color:#2772ee;" href="/operation/pAnnouncementMessage/playerAdvisoryDetail.html?id=${s.id}&continueQuizId=${s.continueQuizId}"
                           nav-target="mainFrame">${views.operation_auto['详细']}</a>
                    </td>
                </tr>
            </c:if>
        </c:forEach>
        </tbody>
    </table>
</div>
<soul:pagination/>