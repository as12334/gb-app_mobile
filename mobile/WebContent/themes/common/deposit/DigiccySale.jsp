<%@ page import="so.wwb.gamebox.model.master.fund.enums.RechargeStatusEnum" %>
<%--@elvariable id="playerRecharge" type="so.wwb.gamebox.model.master.fund.po.PlayerRecharge"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<c:set var="success" value="<%=RechargeStatusEnum.ONLINE_SUCCESS.getCode()%>"/>
<div class="gb-withdraw-box pro-window">
    <div class="cont">
        <h3>${views.themes_auto['消息']}</h3>
        <div class="cont-text">
            <c:if test="${playerRecharge.rechargeStatus eq success}">
                <p>${views.themes_auto['兑换金额']}：<span class="org">${currencySign}&nbsp;${soulFn:formatCurrency(playerRecharge.rechargeAmount)}</span></p>
            </c:if>
            <c:if test="${playerRecharge.rechargeStatus != success}">
                <p>${views.themes_auto['兑换处理中']}：<span class="org">${playerRecharge.payerBank}&nbsp;<fmt:formatNumber value="${playerRecharge.bitAmount}" pattern="#.########"/></span></p>
            </c:if>
        </div>
        <div class="text-pro">
            <div class="cont-text">
                <p>${views.themes_auto['优惠']}：</p>
                <select name="activityId" class="selectwidth pro-selec">
                    <option value="">${views.themes_auto['不参与优惠']}</option>
                    <c:forEach items="${sales}" var="i">
                        <option value="${i.id}">${i.classifyKeyName}&nbsp;&nbsp;${i.activityName}</option>
                    </c:forEach>
                </select>
                <input name="search.transactionNo" value="${playerRecharge.transactionNo}"/>
            </div>
        </div>
        <div class="pro-btn">
            <a class="next-btn" name="confirmSale" sale-url="${root}/wallet/deposit/digiccy/saveSale.html">${views.themes_auto['确认']}</a>
            <a class="agin-btn" name="cancelSale">${views.themes_auto['取消']}</a>
        </div>
        <div class="close">
        </div>
    </div>
</div>
<div class="mui-popup-backdrop mui-active"></div>