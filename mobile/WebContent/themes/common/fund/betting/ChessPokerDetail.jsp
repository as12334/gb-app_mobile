<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.VPlayerGameOrderVo"--%>
<%--@elvariable id="resultVo" type="so.wwb.gamebox.model.api.vo.ApiLiveResultVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<%--真人投注详细--%>
<c:set var="p" value="${command.result}"/>
<c:set var="resultVo" value="${p.resultVo}"/>
<table class="table table-bordered">
    <c:if test="${! empty  resultVo.tableInfo && resultVo.tableInfo !='null'}">
    <tr>
        <td class="bg-gray al-right" width="33%">
            ${views.common_report['场次']}：
        </td>
        <td class="al-left">
            ${resultVo.tableInfo eq 'null'?'':resultVo.tableInfo}
        </td>
    </tr></c:if>
    <tr>
        <td class="bg-gray al-right" width="33%">
            ${views.common_report['玩家下注']}：
        </td>
        <td class="al-left">
            <gb:chessPokerSelection apiId="${p.apiId}" selectionVoSet="${resultVo.selectionVoSet}" betType="${resultVo.betType}"/>
        </td>
    </tr>
    <tr>
        <td class="bg-gray al-right" width="33%">
            ${views.common_report['游戏详细']}：
        </td>
        <td class="al-left">
            <%@include file="ChessPokerResult.jsp"%>
        </td>
    </tr>
</table>