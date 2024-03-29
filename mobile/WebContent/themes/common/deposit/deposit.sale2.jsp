<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>
<div id="depositSalePop">
<c:choose>
    <c:when test="${unCheckSuccess}">
        <c:choose>
            <c:when test="${pop}">
                <div class="masker" id="master" style="display: block;"></div>
                <div class="gb-withdraw-box pro-window" style="display:block">
                    <div  class="cont">
                        <h3>${views.deposit_auto['消息']}</h3>
                        <div class="cont-text">
                            <p>${views.deposit_auto['金额']}：<span class="org">${soulFn:formatCurrency(rechargeAmount)}</span></p>
                            <p>${views.deposit_auto['手续费']}：${msg}</p>
                        </div>

                        <c:if test="${fn:length(sales)>0}">
                            <div class="text-pro applysale">
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
                                                            <input id="activitySubmitId"  name="activityId" type="radio" value="${i.id}"/>
                                                        </div>
                                                    </li>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </ul>
                            </div>
                        </c:if>

                        <div class="pro-btn">
                            <c:choose>
                                <c:when test="${submitType eq 'company'}">
                                    <a class="next-btn">${views.deposit_auto['已存款']}</a>
                                </c:when>
                                <c:otherwise>
                                    <a class="next-btn">${views.deposit_auto['立即存款']}</a>
                                </c:otherwise>
                            </c:choose>
                            <a class="agin-btn">${views.deposit_auto['重新填写金额']}</a>
                        </div>
                        <div class="close"></div>
                    </div>
                </div>
            </c:when>
        </c:choose>
    </c:when>
</c:choose>
<span style="display: none" id="pop" pop="${pop}"></span>
<span style="display: none" id="unCheckSuccess" unCheckSuccess="${unCheckSuccess}"></span>
<span style="display: none" id="tips" tips="${tips}"></span>
<span style="display: none" id="failureCount" failureCount="${failureCount}"></span>
<span style="display: none" id="accountNotUsing" accountNotUsing="${accountNotUsing}"></span>
</div>