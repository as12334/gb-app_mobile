<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../include/include.inc.jsp" %>
<div id="depositSalePop">
    <c:choose>
        <c:when test="${unCheckSuccess && pop}">
            <!--弹窗-->
            <c:if test="${depositChannel eq 'online' || depositChannel eq 'scan' || depositChannel eq 'reverseSacn'}">
                <div class="masker" id="masker" style="display:block;"></div> <!--遮罩-->
            </c:if>
            <div class="gb-withdraw-box pro-window" style="display:block">
                <div  class="cont">
                    <h3>${views.deposit_auto['消息']}</h3>
                    <div class="cont-text">
                        <p>${views.deposit_auto['存款金额']}：<span class="org">${soulFn:formatCurrency(rechargeAmount)}</span></p>
                        <%--<p>${views.deposit_auto['手续费']}：<span class="org">${msg}</span> / 免手续费 / 返还<span class="org">300元</span></p>--%>
                        <p>${views.deposit_auto['手续费']}：<span class="org">${msg}</span></p>
                    </div>
                    <div class="text-pro">
                        <p>${views.deposit_auto['优惠']}：</p>
                        <ul>
                            <div class="mui-scroll-wrapper">
                                <div class="mui-scroll">
                                    <li>
                                        <div class="text-warp">
                                            <span>${views.deposit_auto['不参与优惠']}</span>
                                            <input name="activityId" type="radio" value="" checked="checked"/>
                                        </div>
                                    </li>
                                    <c:forEach items="${sales}" varStatus="vs" var="i">
                                        <c:if test="${i.preferential}">
                                            <li>
                                                <div class="text-warp">
                                                    <span>${i.activityName}</span>
                                                    <input name="activityId" type="radio" value="${i.id}"/>
                                                </div>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                        </ul>
                    </div>
                    <div class="pro-btn">
                        <c:choose>
                            <c:when test="${submitType eq 'company'}">
                                <a class="next-btn" data-rel='{"opType":"function","target":"submitDeposit"}'>${views.deposit_auto['已存款']}</a>
                            </c:when>
                            <c:otherwise>
                                <a class="next-btn" data-rel='{"opType":"function","target":"submitDeposit"}'>${views.deposit_auto['立即存款']}</a>
                            </c:otherwise>
                        </c:choose>
                        <a class="agin-btn" data-rel='{"opType":"function","target":"goToDepositPage"}'>${views.deposit_auto['重新填写金额']}</a>
                    </div>
                    <a class="close" data-rel='{"opType":"function","target":"closeProWindow"}'></a>
                </div>
            </div>
        </c:when>
    </c:choose>
    <span style="display: none" id="pop" pop="${pop}"></span>
    <span style="display: none" id="unCheckSuccess" unCheckSuccess="${unCheckSuccess}"></span>
    <span style="display: none" id="tips" tips="${tips}"></span>
    <span style="display: none" id="failureCount" failureCount="${failureCount}"></span>
    <span style="display: none" id="accountNotUsing" accountNotUsing="${accountNotUsing}"></span>
</div>
