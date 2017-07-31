<%--@elvariable id="command" type="so.wwb.gamebox.model.master.player.vo.VPlayerGameOrderVo"--%>
<%--@elvariable id="resultVo" type="so.wwb.gamebox.model.api.vo.ApiLiveResultVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<%--真人投注详细--%>
<c:set var="p" value="${command.result}"/>
<c:set var="resultVo" value="${p.resultVo}"/>
<c:if test="${! empty resultVo.tableInfo && resultVo.tableInfo !='null'}">
<div class="panel">
    <p>
        <span class="text">${views.fund_auto['场次']}： </span>
        ${resultVo.tableInfo}
    </p>
</div></c:if>
<div class="panel">
    <p>
        <span class="text">${views.fund_auto['玩家下注']}： </span>
        <gb:liveDealerSelection apiId="${p.apiId}" selectionVoSet="${resultVo.selectionVoSet}" betType="${resultVo.betType}"/>
    </p>
</div>
<div class="panel">
    <p>
        <span class="text">${views.fund_auto['游戏详细']}： </span>
        <%@include file="/report/betting/LiveDealerResult.jsp"%>
    </p>
</div>
