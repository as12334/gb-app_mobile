<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<div>
    <div class="pro_ite">${views.deposit_auto['金额']}：<span>${rechargeAmount}</span></div>
    <div class="pro_ite">${fee<=0?views.deposit_auto['手续费']:'返手续费'}：<span>${empty counterFee?"免手续费":counterFee}</span></div>
    <c:if test="${!isOpenActivityHall}">
        <div class="pro_ite">${views.deposit_auto['优惠']}：</div>
        <div class="pro_lis">
            <div class="mui-scroll-wrapper">
                <div class="mui-scroll">
                    <div class="pro_list_item">
                        <label for="pro_item">
                            <div class="text">${views.deposit_auto['不参与优惠']}</div>
                            <input name="activityId" type="radio" value="" checked="checked"/></label>
                    </div>
                    <c:forEach items="${sales}" varStatus="vs" var="i">
                        <c:if test="${i.preferential}">
                            <div class="pro_list_item">
                                <label for="pro_item">
                                    <div class="text">${i.activityName}</div>
                                    <input name="activityId" type="radio" value="${i.id}"/></label>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>
        </div>
    </c:if>
    <div class="btn_wrap"><a href="javascript:" data-rel='{"opType":"function","target":"baseDeposit.submitDeposit"}' class="mui-btn mui-btn-block btn_dep">马上存款</a></div>
    <span style="display: none" id="failureCount" failureCount="${failureCount}"></span>
</div>