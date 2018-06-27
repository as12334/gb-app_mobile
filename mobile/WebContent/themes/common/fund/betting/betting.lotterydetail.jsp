<%--@elvariable id="resultVo" type="so.wwb.gamebox.model.api.vo.ApiLotteryResultVo"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<%--彩票投注详细--%>
<c:set var="p" value="${command.result}"/>
<c:set var="resultVo" value="${p.resultVo}"/>
<c:if test="${!empty resultVo}">
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['彩种']}</span>
            ${dicts.lottery.lottery[resultVo.code]}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['期号']}</span>
            ${resultVo.expect}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['赔率']}</span>
            ${resultVo.odd}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['下注内容']}</span>
            ${dicts.lottery.lottery_betting[resultVo.betCode]}-${resultVo.betNum}
        </p>
    </div>
</c:if>
<c:if test="${empty resultVo && !empty resultArray}">
    <c:forEach items="${resultArray}" var="array">
        <div class="panel">
            <p>
                <span class="text">${views.themes_auto['彩种']}</span>
                ${dicts.lottery.lottery[array['code']]}
            </p>
        </div>
        <div class="panel">
            <p>
                <span class="text">${views.themes_auto['期号']}</span>
                ${array['expect']}
            </p>
        </div>
        <div class="panel">
            <p>
                <span class="text">${views.themes_auto['赔率']}</span>
                ${array['odd']}
            </p>
        </div>
        <div class="panel">
            <p>
                <span class="text">${views.themes_auto['下注内容']}</span>
                ${dicts.lottery.lottery_betting[array['bet_code']]}-${array['bet_num']}
            </p>
        </div>
    </c:forEach>
</c:if>

