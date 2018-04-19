<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../../include/include.inc.jsp" %>
<c:choose>
    <c:when test="${fn:length(command.result)>0}">
            <table>
                <tbody>
                <c:forEach items="${command.result}" var="s">
                    <tr data-rel='{"target":"${root}/fund/record/details.html?searchId=${command.getSearchId(s.id)}","opType":"href"}'>
                        <td><span class="text-gray">${soulFn:formatDateTz(s.createTime, DateFormat.DAY,timeZone)}</span></td>
                        <td>
                            <c:set value="${s._describe}" var="_describe"/>
                            <c:choose>
                                <c:when test="${s.transactionMoney!=0}">
                                    <span class="text-green2">${soulFn:formatCurrency(s.transactionMoney)}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-green2">${soulFn:formatCurrency(0)}</span>
                                </c:otherwise>
                            </c:choose>
                            <c:if test="${_describe['bitAmount']>0}">
                                <c:if test="${s.transactionMoney!=0}">
                                    <br/>
                                </c:if>
                                <c:set var="digiccySymbol" value="${dicts.common.currency_symbol[_describe['bankCode']]}"/>
                                <span class="text-green2">${empty digiccySymbol?'Ƀ':digiccySymbol}<fmt:formatNumber value="${_describe['bitAmount']}" pattern="#.########"/></span>
                            </c:if>
                        </td>
                        <td>${dicts.common.transaction_type[s.transactionType]}</td>
                        <td>
                            <c:choose>
                                <c:when test="${s.status=='success'}"><span class="text-green2">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='failure'}"><span class="text-red">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='pending_pay'}"><span class="text-orange">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='pending'}"><span class="text-blue">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='exchange'}"><span class="text-blue">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='over_time'}"><span class="text-red">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='process'}"><span class="text-blue">${dicts.common.status[s.status]}</span></c:when>
                                <c:when test="${s.status=='reject'}"><span class="text-red">${dicts.common.status[s.status]}</span></c:when>
                                <c:otherwise><span class="gray">${views.fund_auto['其他']}</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
    </c:when>
    <c:otherwise>
        <table>
            <tbody>
                <tr >
                    <td style="height: 40px"><p>${views.fund_auto['暂无内容']}</p></td>
                </tr>
            </tbody>
        </table>

    </c:otherwise>
</c:choose>
<input type="hidden" id="deposit" name="deposit" value="${soulFn:formatCurrency(sumPlayerMap.recharge)}">
<input type="hidden" id="withdraw" name="withdraw" value="${soulFn:formatCurrency(sumPlayerMap.withdraw)}">
<input type="hidden" id="promo" name="promo" value="${soulFn:formatCurrency(sumPlayerMap.favorable)}">
<input type="hidden" id="backwater" name="backwater" value="${soulFn:formatCurrency(sumPlayerMap.rakeback)}">
<tr hidden>
    <td><input type="hidden" value="${command.paging.lastPageNumber}" id="lastPageNumber"></td>
</tr>
