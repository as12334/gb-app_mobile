<%--@elvariable id="command" type="so.wwb.gamebox.model.company.operator.vo.SystemAnnouncementListVo"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<c:if test="${fn:length(command.result) le 0}">
    <div class="chart-table info-notice">
</c:if>
    <table  border="0" cellpadding="0" cellspacing="0" class="table">
        <tbody>
        <c:choose>
            <c:when test="${fn:length(command.result)>0}">
                <div class="gamenotice">
                    <div class="gamenotice-box">
                        <dl>
                            <c:forEach items="${command.result}" var="s" varStatus="dex">
                                <c:forEach items="${apiMap}" var="apis">
                                    <c:if test="${apis.value.apiId==s.apiId}">
                                        <i class="date clock"></i>
                                        <span class="date">${soulFn:formatDateTz(s.publishTime, DateFormat.DAY_SECOND,timeZone)}</span>
                                        <dd class="clearfix ${(dex.index % 2)==0 ? '':'gameback'}" >
                                            <div class="item">
                                                <h2 class="orange">${gbFn:getSiteApiName((s.apiId).toString())}<c:if test="${s.gameId!=null}">——${gbFn:getGameName((s.gameId).toString())}</c:if></h2>
                                                <p>
                                                    <a href="/operation/pAnnouncementMessage/gameNoticeDetail.html?searchId=${command.getSearchId(s.id)}"
                                                       nav-target="mainFrame">${s.shortContentText80}</a>
                                                </p>
                                            </div>
                                        </dd>
                                        <p class="dottline"></p>
                                    </c:if>
                                </c:forEach>
                            </c:forEach>
                        </dl>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <tr>
                    <td class="no-content_wrap" style="border-bottom: 0px;">
                        <div>
                            <i class="fa fa-exclamation-circle"></i> ${views.operation_auto['暂无内容!']}
                        </div>
                    </td>
                </tr>
            </c:otherwise>
        </c:choose>
        </tbody>
    </table>

<c:if test="${fn:length(command.result) le 0}">
</div>
</c:if>
<%--<soul:pagination/>--%>
