<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../../include/include.inc.jsp" %>
<%--VR真人投注详细--%>
<c:set var="p" value="${command.result}"/>
<c:set var="resultVo" value="${p.resultVo}"/>
<c:forEach items="${resultArray}" var="array">
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['彩种']}:</span>
                ${array['channelName']}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['期号']}:</span>
                ${array['issueNumber']}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['赔率']}:</span>
                ${array['odds']}
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['下注内容']}:</span>
                ${array['betTypeName']}&nbsp;&nbsp;&nbsp;[<span class="co-blue">${array['number']}</span>]
        </p>
    </div>
    <div class="panel">
        <p>
            <span class="text">${views.themes_auto['开奖号码']}:</span>
                ${array['winningNumber']}
        </p>
    </div>
</c:forEach>

