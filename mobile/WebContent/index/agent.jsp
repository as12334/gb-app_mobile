<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row">
    <div class="mui-row">
        <div class="gb-panel gray">
            <div class="gb-headtabs span3">
                <div id="segmentedControl" class="mui-segmented-control">
                    <c:forEach var="a" items="${agents}" varStatus="vs">
                        <a href="#agent${vs.count}" id="agent-title-${vs.count}" class="mui-control-item gray ${vs.index == 0 ? 'mui-active' : ''}">${a.title}</a>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <div class="mui-row">
        <c:forEach var="a" items="${agents}" varStatus="vs">
            <div id="agent${vs.count}" class="mui-control-content agent-text ${vs.index == 0 ? 'mui-active' : ''}">
                <c:set var="content" value="${empty a.content ? a.contentDefault : a.content}" />
                ${content.replace("${agentRegisterUrl}", "")}
            </div>
        </c:forEach>
    </div>
</div>
<script src="${resRoot}/js/common/Agent.js?v=${rcVersion}"></script>