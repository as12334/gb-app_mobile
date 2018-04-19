<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!--红包html开始  通过改变hongbao-wrap的class来改变红包样式，一共三种（hb_type_1,hb_type_2,hb_type_3）-->
<c:if test="${not empty floatList}">
    <div class="ads-slider hongbao-slide-wrap hongbao-wrap" id="hongbao">
        <span class="mui-icon mui-icon-close icon-close" data-rel='{"target":"closeAds","opType":"function"}'></span>
        <div class="mui-slider hongbao-slider">
            <div class="mui-slider-group">
                <c:forEach var="item" items="${floatList}">
                    <c:if test="${item.type=='moneyActivity'}">
                        <c:choose>
                            <c:when test="${item.cttFloatPic.singleMode}">
                                <div class="mui-slider-item hb_type_<c:choose>
                                        <c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-first')}">1</c:when>
                                        <c:when test="${fn:contains(item.floatItem.normalEffect, 'panel-second')}">2</c:when>
                                        <c:otherwise>3</c:otherwise>
                                     </c:choose>">
                                    <div class="img"></div>
                                    <div class="extra" data-rel='{"target":"floatList","opType":"function","objectId":"${item.activityId}"}'></div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="mui-slider-item" data-rel='{"target":"floatList","opType":"function","objectId":"${item.activityId}"}'>
                                    <img data-src="${soulFn:getImagePath(domain,item.floatItem.normalEffect)}" src="${soulFn:getThumbPath(domain,item.floatItem.normalEffect,0,0)}">
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>
            </div>
            <div class="mui-slider-indicator">
                <c:forEach var="item" items="${floatList}" varStatus="status">
                    <div class="mui-indicator ${status.index == 0?'mui-active':''}"></div>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>
<div id="hongbao_detail" class="hongbao_detail" style="display: none;">
    <input type="hidden" id="lottery_type"/>
    <input type="hidden" id="win_id"/>
    <input type="hidden" id="record_id"/>
    <input type="hidden" id="activity_message_id" value=""/>
    <input type="hidden" id="applyId" value=""/>
    <input type="hidden" name="gb.token" value=""/>

    <div class="hongbao_inner">
        <a class="icon-close" data-rel='{"target":"closePage","opType":"function"}'></a>
        <div class="hongbao"><!--未能拆时加disabled类名-->
            <div id="lotteryPages">
                <div class="icon-open" data-rel='{"target":"lottery","opType":"function"}'></div>
                <div class="hongbao-time-txt">${views.themes_auto['下次拆红包开始时间为']}</div>
                <div class="hongbao-time">2017-11-11  11:11:11</div>
                <%--<soul:button opType="function" text="" target="openRule" cssClass="btn-rule"/>--%>
                <div class="tip-msgs" id="tip-msgs">
                    ${views.themes_auto['你还有']}<span style="font-size: 18px;padding: 0 5px;color: gold" id="ramain-count">0</span>${views.themes_auto['次抽奖机会']}
                </div>
                <!--红包规则元素-->
                <div class="hongbao-rule">
                    <div class="txt">
                        <div class="nice-wrapper">
                             <c:if test="${not empty floatList}">
                                 <c:forEach items="${floatList}" var="item">
                                     <c:if test="${item.type=='moneyActivity'}">
                                         <c:out value="${item.description}"></c:out>
                                     </c:if>
                                 </c:forEach>
                             </c:if>
                        </div>
                    </div>
                    <a class="icon-close-rule" data-rel='{"target":"closeRule","opType":"function"}'></a>
                </div>
            </div>
            <!--中奖时的提示-->
            <div class="win-hongbao tips">
                <div class="ttxt-1">${views.themes_auto['恭喜您']}</div>
                <div class="ttxt-2">获得0元</div>
            </div>
            <!--未中奖时的提示-->
            <div class="lose-hongbao tips">
                <div class="ttxt-1">${views.themes_auto['很遗憾']}</div>
                <div class="ttxt-2">还差一点就中奖了呦！</div>
            </div>
        </div>
        <div class="hongbao_extra"></div>
        <!--拆开红包时的彩带和光环-->
        <div class="caidai"></div>
        <div class="hongbao-light"></div>
        <!--关闭红包继续抽奖按钮-->
        <a class="btn-ok" data-rel='{"target":"onceAgain","opType":"function"}'></a>
    </div>
</div>
