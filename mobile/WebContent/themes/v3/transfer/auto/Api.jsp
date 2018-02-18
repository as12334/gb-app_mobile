<%--@elvariable id="apis" type="java.util.List<java.util.Map<java.lang.String,java.lang.Object>>"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/themes/v3/include/include.inc.jsp" %>
<div class="mui-row">
    <div class="mui-input-group mine-form mine-form-nobg">
        <c:forEach var="api" items="${apis}" varStatus="vs">
            <c:if test="${api.status!='disable'}">
                <div class="mui-input-row ${vs.last ? 'final' : ''} _api_${api.id}">
                    <label><span class="_apiName">${api.apiName}</span>:</label>
                    <div class="ct">
                        <p class="mui-text-right text-gray">
                            <a class="mui-btn mui-btn-link gb-input-money text-gray">
                                <c:set var="status" value=""/>
                                <c:if test="${api.status=='disable'}">
                                    <c:set var="status" value="${views.transfer_auto['暂停转账']}"/>
                                </c:if>
                                <c:if test="${api.status=='maintain'}">
                                    <c:set var="status" value="${views.transfer_auto['维护中']}"/>
                                </c:if>
                                <span class="_apiMoney_${api.id}">${api.balance}</span>${status}
                            </a>
                            <a class="mui-btn mui-btn-link recovery" apiId="${api.id}" data-rel='{"target":"recovery","dataApiId":"${api.id}" ,"opType":"function"}' style="margin-right: 30px;">${views.transfer_auto['回收']}</a>
                        </p>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </div>
</div>
