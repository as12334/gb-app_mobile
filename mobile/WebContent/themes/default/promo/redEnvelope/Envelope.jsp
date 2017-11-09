<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/include/include.inc.jsp" %>

<div id="hongbao_detail" class="hongbao_detail" style="display: none;">

    <input type="hidden" id="lottery_type">
    <input type="hidden" id="win_id">
    <input type="hidden" id="record_id">
    <input type="hidden" id="activity_message_id" value="">
    <input type="hidden" id="applyId" value="">
    <input type="hidden" name="gb.token" value="">

    <div class="hongbao_inner">
        <div class="icon-close" onclick="closePage()"></div>
        <div class="hongbao"><!--未能拆时加disabled类名-->
            <div id="lotteryPages" style="margin-top: 159px;">
                <div class="icon-open" onclick="lottery()"></div>
                <div class="hongbao-time-txt">下次拆红包开始时间为</div>
                <div class="hongbao-time">2017-11-11  11:11:11</div>
                <div style="text-align: center;font-size: 18px;color: #fff;" id="tip-msgs">
                    你还有<span style="font-size: 18px;padding: 0 5px;color: gold" id="ramain-count">0</span>次抽奖机会
                </div>
                <a href="javascript:" class="btn-rule" id="btn-rule" onclick="openRule()"></a>
                <!--红包规则元素-->
                <div class="hongbao-rule">
                    <div class="txt">
                        <div class="nice-wrapper">
                            <c:if test="${not empty floatList}">
                                <c:forEach items="${floatList}" var="item">
                                    <c:if test="${item.type=='moneyActivity'}">
                                        ${item.description}
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </div>
                    </div>
                    <a href="javascript:" class="icon-close-rule" onclick="closeRule()"></a>
                </div>
            </div>
            <!--中奖时的提示-->
            <div class="win-hongbao tips">
                <div class="ttxt-1">恭喜您</div>
                <div class="ttxt-2">获得20元</div>
            </div>
            <!--未中奖时的提示-->
            <div class="lose-hongbao tips">
                <div class="ttxt-1">很遗憾</div>
                <div class="ttxt-2">还差一点就中奖了呦！</div>
            </div>
        </div>
        <div class="hongbao_extra"></div>
        <!--拆开红包时的彩带和光环-->
        <div class="caidai"></div>
        <div class="hongbao-light"></div>
        <!--关闭红包继续抽奖按钮-->
        <a href="javascript:" id="btn-ok" class="btn-ok" onclick="onceAgain()"></a>
    </div>
</div>
